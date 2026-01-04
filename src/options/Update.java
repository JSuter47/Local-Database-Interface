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

public class Update {

    /**
     * Edit / delete option: choose entity, pick ID, then edit fields or delete.
     *
     * @param input the input stream
     */
    public static void editDelete(Scanner input) {
        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        // 1. Choose an entity type (table), same pattern as Search/NewEntity
        EntityDefinition def = promptEntityDefinition(input);
        if (def == null) {
            // user canceled
            return;
        }

        String tableName = def.getTableName();
        String[] attributes = def.getAttributes();

        if (attributes == null || attributes.length == 0) {
            System.out.println("No attributes defined for " + tableName + ".");
            return;
        }

        // 2. Load records from DB with rowid so we can identify rows
        List<Long> rowIds = new ArrayList<>();

        String selectSql = "SELECT rowid, * FROM " + tableName + ";";

        try (PreparedStatement ps = conn.prepareStatement(selectSql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount(); // includes rowid as first column

            int idx = 0;
            while (rs.next()) {
                long rowId = rs.getLong(1); // rowid
                rowIds.add(rowId);

                StringBuilder sb = new StringBuilder();
                // Columns 2..columnCount are the actual table columns
                for (int i = 2; i <= columnCount; i++) {
                    String colName = rsmd.getColumnName(i);
                    String val = rs.getString(i);
                    sb.append(colName).append("=").append(val);
                    if (i < columnCount) {
                        sb.append(", ");
                    }
                }

                System.out.println("[" + idx + "] " + sb.toString());
                idx++;
            }
        } catch (SQLException e) {
            System.out.println("Error loading records for " + tableName + ": " + e.getMessage());
            return;
        }

        if (rowIds.isEmpty()) {
            System.out.println("No records for " + tableName + ".");
            return;
        }

        // 3. Prompt user to pick a record index
        Integer recIndex = promptRecordIndex(input, rowIds.size());
        if (recIndex == null) {
            // user canceled
            return;
        }
        long targetRowId = rowIds.get(recIndex);

        // 4. Choose action: edit or delete
        System.out.println("Select an action:");
        System.out.println("  (1): Edit");
        System.out.println("  (2): Delete");
        System.out.print("> ");
        String action = input.nextLine().trim();

        switch (action) {
            case "1":
                editRecord(input, conn, tableName, attributes, targetRowId);
                break;
            case "2":
                deleteRecord(conn, tableName, targetRowId);
                break;
            default:
                System.out.println("Invalid action.");
        }
    }

    /**
     * Prompt the user to choose an entity (table) using the same menu as other
     * options. Returns null if the user cancels.
     */
    private static EntityDefinition promptEntityDefinition(Scanner input) {
        while (true) {
            Utilities.printEntityMenu();
            String type = input.nextLine().trim();

            if (type.equals("0")) {
                System.out.println("Canceled.");
                return null;
            }

            try {
                int idx = Integer.parseInt(type);
                EntityDefinition def = EntityDefinition.fromIndex(idx);
                if (def != null) {
                    return def;
                } else {
                    System.out.println("Invalid selection. Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection. Please try again.\n");
            }
        }
    }

    /**
     * Prompt the user for a record index in [0, size-1]. Returns null if user
     * cancels.
     */
    private static Integer promptRecordIndex(Scanner input, int size) {
        while (true) {
            System.out.print("Enter record ID (0 to " + (size - 1)
                    + "), or 'c' to cancel: ");
            String line = input.nextLine().trim().toLowerCase();

            if (line.equals("c") || line.equals("cancel")) {
                System.out.println("Canceled.");
                return null;
            }

            try {
                int idx = Integer.parseInt(line);
                if (idx >= 0 && idx < size) {
                    return idx;
                } else {
                    System.out.println("ID out of range.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or 'c'.");
            }
        }
    }

    /**
     * Handle editing a single field in the selected record.
     */
    private static void editRecord(Scanner input, Connection conn,
                                   String tableName, String[] attributes,
                                   long rowId) {
        // Show available fields (columns)
        System.out.println("Available fields:");
        for (String attr : attributes) {
            System.out.println("  " + attr);
        }

        System.out.print("Enter field name to edit (exact): ");
        String field = input.nextLine().trim();

        // Validate that the field is one of the known attributes (whitelist)
        boolean fieldValid = false;
        for (String attr : attributes) {
            if (attr.equals(field)) {
                fieldValid = true;
                break;
            }
        }

        if (!fieldValid) {
            System.out.println("Field not found.");
            return;
        }

        System.out.print("New value for '" + field + "': ");
        String newVal = input.nextLine().trim();

        String updateSql = "UPDATE " + tableName + " SET " + field + " = ? WHERE rowid = ?;";

        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, newVal);
            ps.setLong(2, rowId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Record updated.");
            } else {
                System.out.println("No rows updated.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    /**
     * Handle deleting the selected record.
     */
    private static void deleteRecord(Connection conn, String tableName, long rowId) {
        String deleteSql = "DELETE FROM " + tableName + " WHERE rowid = ?;";

        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setLong(1, rowId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Record deleted.");
            } else {
                System.out.println("No rows deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }
}