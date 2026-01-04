package options;

import java.util.Scanner;

import sql.SQL;
import utilities.Utilities;

public class List {

    /**
     * Handles the "List all" main menu option.
     */
    public static void listAll(Scanner input) {
        boolean done = false;

        while (!done) {
            // Reuses the same entity menu as NewEntity
            Utilities.printEntityMenu();
            String type = input.nextLine().trim();

            if (type.equals("0")) {
                System.out.println("Canceled.");
                break;
            }

            final String entityName = Utilities.getEntityNameFromIndex(type);

            if (entityName == null) {
                System.out.println("Invalid selection. Please try again.\n");
                continue;
            }

            // Simple static SELECT * using the SQL helper
            String sql = "SELECT * FROM " + entityName + ";";
            System.out.println("Listing all rows from " + entityName + ":");
            SQL.sqlQuery(sql);   // uses the Statement-based version

            // List once and return to main menu.
            done = true;
        }
    }
}
