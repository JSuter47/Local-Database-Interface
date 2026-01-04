package options;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sql.SQL;
import utilities.EntityDefinition;
import utilities.Utilities;

public class Search {

    /**
     * Search option: by entity (or all) and substring across any attribute value.
     *
     * @param input the input stream
     */
    public static void search(Scanner input) {

        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String yn = "";
        boolean validInput = false;

        /*
         * Prompt the user to search by entity or across all types.
         */
        while (!validInput) {
            System.out.print("Search a specific entity? (y/n): ");
            yn = input.nextLine().trim().toLowerCase();
            if (yn.equals("y") || yn.equals("yes") || yn.equals("n") || yn.equals("no")) {
                validInput = true;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }

        /*
         * Build the set of entities (tables) to search.
         */
        List<EntityDefinition> entitiesToSearch = new ArrayList<>();

        if (yn.startsWith("y")) {
            // Let the user pick a single entity via the same menu used by NewEntity
            boolean picked = false;
            while (!picked) {
                Utilities.printEntityMenu();
                String type = input.nextLine().trim();

                if (type.equals("0")) {
                    System.out.println("Canceled.");
                    return;
                }

                try {
                    int idx = Integer.parseInt(type);
                    EntityDefinition def = EntityDefinition.fromIndex(idx);
                    if (def != null) {
                        entitiesToSearch.add(def);
                        picked = true;
                    } else {
                        System.out.println("Invalid selection. Please try again.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid selection. Please try again.\n");
                }
            }
        } else {
            // Search across all known entities
            for (EntityDefinition def : EntityDefinition.values()) {
                entitiesToSearch.add(def);
            }
            if (entitiesToSearch.isEmpty()) {
                System.out.println("No entities defined.");
                return;
            }
        }

        /*
         * Get the user's search query (substring).
         */
        System.out.print("Enter substring to search for: ");
        String q = input.nextLine().trim();
        if (q.isEmpty()) {
            System.out.println("Empty query.");
            return;
        }

        String likePattern = "%" + q + "%";

        /*
         * Search for matches and output where they came from.
         */
        int totalMatches = 0;

        for (EntityDefinition def : entitiesToSearch) {
            String tableName = def.getTableName();
            String[] attributes = def.getAttributes();

            if (attributes == null || attributes.length == 0) {
                continue;
            }

            // Build: SELECT * FROM tableName WHERE col1 LIKE ? OR col2 LIKE ? OR ...
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM ").append(tableName).append(" WHERE ");

            for (int i = 0; i < attributes.length; i++) {
                sb.append(attributes[i]).append(" LIKE ?");
                if (i < attributes.length - 1) {
                    sb.append(" OR ");
                }
            }
            sb.append(";");

            String sql = sb.toString();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Bind the same pattern for every column
                for (int i = 0; i < attributes.length; i++) {
                    ps.setString(i + 1, likePattern);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    boolean hasRowsForThisEntity = false;

                    while (rs.next()) {
                        if (!hasRowsForThisEntity) {
                            // First time we see a row for this entity
                            if (totalMatches == 0) {
                                System.out.println("Matches:");
                            }
                            System.out.println("[" + tableName + "]");

                            // Print header
                            for (int i = 1; i <= columnCount; i++) {
                                String colName = rsmd.getColumnName(i);
                                System.out.print(colName);
                                if (i < columnCount) {
                                    System.out.print(",  ");
                                }
                            }
                            System.out.print("\n");

                            hasRowsForThisEntity = true;
                        }

                        // Print row
                        for (int i = 1; i <= columnCount; i++) {
                            String columnValue = rs.getString(i);
                            System.out.print(columnValue);
                            if (i < columnCount) {
                                System.out.print(",  ");
                            }
                        }
                        System.out.print("\n");

                        totalMatches++;
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error searching " + tableName + ": " + e.getMessage());
            }
        }

        if (totalMatches == 0) {
            System.out.println("No matches.");
        }
    }
}