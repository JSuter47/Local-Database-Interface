package options;

import java.sql.SQLException;
import java.util.Scanner;

import sql.SQL;
import utilities.Utilities;

public class NewEntity {

    /**
     * New instance handler for all entity types.
     *
     * @param entityIndex index for a preselected entity type (may be null or 0)
     * @param input       the input stream
     */
    public static void createNew(Integer entityIndex, Scanner input) {
        boolean done = false;
        Integer idx = entityIndex;

        while (!done) {
            String type = "";

            // If we were given a specific entity type index, use it once.
            if (idx != null && idx != 0) {
                type = idx.toString();
            } else {
                Utilities.printEntityMenu(); // <-- uses EntityDefinition enum
                type = input.nextLine().trim();
            }

            if (type.equals("0")) {
                System.out.println("Canceled.");
                break; // user exit
            }

            // Look up table name + attribute list from enum-backed Utilities
            final String entityName = Utilities.getEntityNameFromIndex(type);
            final String[] attributes = Utilities.getAttributesForType(type);

            /*
             * Safety net checks, unlikely to happen unless menu/index is invalid.
             */
            if (entityName == null || attributes.length == 0) {
                System.out.println("Invalid selection. Please try again.\n");
                idx = 0; // force menu to re-display
                continue; // retry loop
            }

            /*
             * Prompt the user for values for each column (attribute).
             * Keep them in an array that lines up with `attributes`.
             */
            String[] values = new String[attributes.length];
            for (int i = 0; i < attributes.length; i++) {
                String attr = attributes[i];
                String value = "";
                while (value.isEmpty()) {
                    System.out.print("Enter " + attr + ": ");
                    value = input.nextLine().trim();
                    if (value.isEmpty()) {
                        System.out.println("Please enter a value.");
                    }
                }
                values[i] = value;
            }

            /*
             * Insert the row into the database using a prepared statement.
             */
            try {
                SQL.insertRow(entityName, attributes, values);
                System.out.println("Added new " + entityName + " record.");
            } catch (SQLException e) {
                System.out.println("Error inserting new " + entityName + ": " + e.getMessage());
            }

            done = true;
        }
    }
}