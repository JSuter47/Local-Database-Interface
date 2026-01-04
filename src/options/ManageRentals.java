package options;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import sql.SQL;
import utilities.EntityDefinition;
import utilities.Utilities;

public class ManageRentals {

    /**
     * Rentals sub-menu.
     *
     * @param input the input stream
     */
    public static void rentalsMenu(Scanner input) {
        String selection = "";
        while (!selection.equals("0")) {
            Utilities.printRentalsMenu();
            selection = input.nextLine().trim();
            switch (selection) {
                case "1": { // create rental transaction
                    final Integer rentalIndex = EntityDefinition.RENTAL_TRANSACTION.getIndex();
                    NewEntity.createNew(rentalIndex, input);
                    System.out.println("Rental transaction created.");
                    break;
                }
                case "2": { // register a return (update return_date)
                    recordReturnSimple(input);
                    break;
                }
                case "3": { // schedule a delivery
                    scheduleDeliverySimple(input);
                    break;
                }
                case "4": { // schedule a pickup
                    schedulePickUpSimple(input);
                    break;
                }
                case "0":
                    // back to main menu
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    /*
     *  Helper methods
     */

    /**
     * Check whether a given value exists in table.column.
     */
    private static boolean existsById(String tableName, String columnName, String value) {
        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return false;
        }

        String sql = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ? LIMIT 1;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Prompt until the user provides an ID that actually exists in table.column.
     */
    private static String promptExistingId(Scanner input,
                                           String prompt,
                                           String tableName,
                                           String columnName) {
        while (true) {
            System.out.print(prompt + " (or 'q' to cancel): ");
            String id = input.nextLine().trim();

            if (id.equalsIgnoreCase("q")) {
                System.out.println("Canceled.");
                return null;
            }
            if (id.isEmpty()) {
                System.out.println("Please enter a value.");
                continue;
            }
            if (!existsById(tableName, columnName, id)) {
                System.out.println("No matching record found. Please enter an existing ID.");
                continue;
            }
            return id;
        }
    }

    /**
     * Prompt for a date in YYYY-MM-DD format.
     */
    private static String promptDate(Scanner input, String label) {
        String date = "";
        while (date.isEmpty()) {
            System.out.print(label + " (YYYY-MM-DD) (or 'q' to cancel): ");
            date = input.nextLine().trim();
            if (date.equalsIgnoreCase("q")) {
                System.out.println("Canceled.");
                return null;
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Please use YYYY-MM-DD format.");
                date = "";
            }
        }
        return date;
    }

    /*
     *  Option 2: Record a return
     */

    /**
     * Record a simple equipment return by updating Rental_Transaction.return_date.
     *
     * @param input the input stream
     */
    private static void recordReturnSimple(Scanner input) {
        // Get table info for Rental_Transaction
        EntityDefinition rentalDef = EntityDefinition.RENTAL_TRANSACTION;
        String rentalTable = rentalDef.getTableName();   // "Rental_Transaction"

        // Prompt for an existing rental_id
        String rentalId = promptExistingId(
                input,
                "Enter rental ID to record a return for",
                rentalTable,
                "rental_id"
        );
        
        if (rentalId == null) {
            return; // user canceled
        }

        // Prompt for return date
        String returnDate = promptDate(input, "Enter return date");
        if (returnDate == null) {
            return; // user canceled
        }

        // Update Rental_Transaction.return_date
        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String sql = "UPDATE " + rentalTable + " SET return_date = ? WHERE rental_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setString(2, rentalId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Return recorded for rental " + rentalId + ".");
            } else {
                System.out.println("No rows updated. Please check the rental ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error recording return: " + e.getMessage());
        }
    }

    /*
     *  Option 3: Schedule a delivery
     */

    /**
     * Schedule a drone for equipment delivery.
     *
     * Inserts into Drone_Performs_Delivery:
     *   (rental_id, drone_id, miles_flown)
     *
     * @param input the input stream
     */
    public static void scheduleDeliverySimple(Scanner input) {
        // Table info for related entities
        EntityDefinition rentalDef = EntityDefinition.RENTAL_TRANSACTION;
        EntityDefinition droneDef  = EntityDefinition.DRONE;
        EntityDefinition dpdDef    = EntityDefinition.DRONE_PERFORMS_DELIVERY;

        String rentalTable = rentalDef.getTableName();   // "Rental_Transaction"
        String droneTable  = droneDef.getTableName();    // "Drone"
        String dpdTable    = dpdDef.getTableName();      // "Drone_Performs_Delivery"

        /*
         * Get an existing rental_id
         */
        String rentalId = promptExistingId(
                input,
                "Enter rental ID to schedule a delivery for",
                rentalTable,
                "rental_id"
        );
        if (rentalId == null) {
            return; // canceled
        }

        /*
         * Get an existing drone_id
         */
        String droneId = promptExistingId(
                input,
                "Enter drone ID to assign to this delivery",
                droneTable,
                "drone_id"
        );
        if (droneId == null) {
            return; // canceled
        }

        /*
         * Enter miles flown.
         */
        String milesFlown = "";
        while (milesFlown.isEmpty()) {
            System.out.print("Enter miles flown (DECIMAL 10,2) (or 'q' to cancel): ");
            milesFlown = input.nextLine().trim();

            if (milesFlown.equalsIgnoreCase("q")) {
                System.out.println("Canceled.");
                return;
            }

            // validate DECIMAL(10,2) format
            if (!milesFlown.matches("^\\d{1,10}(\\.\\d{1,2})?$")) {
                System.out.println("Please enter a valid decimal number (e.g., 12.34).");
                milesFlown = "";
            }
        }

        /*
         * Insert into Drone_Performs_Delivery.
         * Attributes from EntityDefinition: { "rental_id", "drone_id", "miles_flown" }
         */
        String[] columns = dpdDef.getAttributes();
        String[] values  = { rentalId, droneId, milesFlown };

        try {
            SQL.insertRow(dpdTable, columns, values);
            System.out.println("Drone " + droneId + " assigned.");
            System.out.println("Delivery scheduled for rental " + rentalId + ".");
        } catch (SQLException e) {
            System.out.println("Error scheduling delivery: " + e.getMessage());
        }
    }

    /*
     *  Option 4: Schedule a pickup
     */

    /**
     * Schedule a drone for equipment return pickup.
     *
     * Inserts into Drone_Perfroms_Return:
     *   (rental_id, drone_id)
     *
     * @param input the input stream
     */
    public static void schedulePickUpSimple(Scanner input) {
        // Table info
        EntityDefinition rentalDef = EntityDefinition.RENTAL_TRANSACTION;
        EntityDefinition droneDef  = EntityDefinition.DRONE;
        EntityDefinition dprDef    = EntityDefinition.DRONE_PERFORMS_RETURN;

        String rentalTable = rentalDef.getTableName();   // "Rental_Transaction"
        String droneTable  = droneDef.getTableName();    // "Drone"
        String dprTable    = dprDef.getTableName();      // "Drone_Perfroms_Return"

        /*
         * Get an existing rental_id
         */
        String rentalId = promptExistingId(
                input,
                "Enter rental ID to schedule a pickup for",
                rentalTable,
                "rental_id"
        );
        if (rentalId == null) {
            return; // canceled
        }

        /*
         * Get an existing drone_id
         */
        String droneId = promptExistingId(
                input,
                "Enter drone ID to assign to this pickup",
                droneTable,
                "drone_id"
        );
        if (droneId == null) {
            return; // canceled
        }

        /*
         * Insert into Drone_Perfroms_Return.
         * Attributes from EntityDefinition: { "rental_id", "drone_id" }
         */
        String[] columns = dprDef.getAttributes();
        String[] values  = { rentalId, droneId };

        try {
            SQL.insertRow(dprTable, columns, values);
            System.out.println("Drone " + droneId + " assigned.");
            System.out.println("Pickup scheduled for rental " + rentalId + ".");
        } catch (SQLException e) {
            System.out.println("Error scheduling pickup: " + e.getMessage());
        }
    }
}
