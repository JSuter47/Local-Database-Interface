package options;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import sql.SQL;

public class Reports {

    /**
     * Top-level Reports menu.
     *
     * @param input the input stream
     */
    public static void generateReports(Scanner input) {
        String selection = "";
        while (!selection.equals("0")) {
            printReportsMenu();
            selection = input.nextLine().trim();

            switch (selection) {
                case "1":
                    runReport1(input);
                    break;
                case "2":
                    runReport2(input);
                    break;
                case "3":
                    runReport3(input);
                    break;
                case "4":
                    runReport4(input);
                    break;
                case "5":
                    runReport5(input);
                    break;
                case "6":
                    runReport6(input);
                    break;
                case "0":
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }
    }

    private static void printReportsMenu() {
        System.out.println("Select a report:");
        System.out.println("  (1): Total Rentals by a Member");
        System.out.println("  (2): Most Popular Items");
        System.out.println("  (3): Most Frequently Used Equipment Manufacturer");
        System.out.println("  (4): Most Used Drone");
        System.out.println("  (5): Member with Most Items Rented");
        System.out.println("  (6): Equipment by Type Released Before a Given Year");
        System.out.println("  (0): Back to main menu");
        System.out.print("> ");
    }

    /*
     * Report 1: Total Rentals by a Member
     */
    private static void runReport1(Scanner input) {
        System.out.println("\n=== Report 1: Total Rentals by a Member ===");

        // Ask the user for a member ID
        System.out.print("Enter member ID to search (or 'q' to cancel): ");
        String memberId = input.nextLine().trim();
        if (memberId.equalsIgnoreCase("q")) {
            System.out.println("Canceled.");
            return;
        }

        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String sql =
            "SELECT M.member_id, M.f_name, M.l_name, COUNT(R.rental_id) AS Rentals " +
            "FROM Community_Member AS M " +
            "LEFT JOIN Rental_Transaction AS R ON R.member_id = M.member_id " +
            "WHERE M.member_id = ? " +
            "GROUP BY M.member_id;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, memberId);

            System.out.println("\nResults:");
            SQL.sqlQuery(ps);

        } catch (SQLException e) {
            System.out.println("Error running report: " + e.getMessage());
        }
    }

/*
 * Report 2: Equipment Ranked by Total Times Rented
 */
private static void runReport2(Scanner input) {
    System.out.println("\n=== Report 2: Most Frequently Rented Equipment ===");

    Connection conn = SQL.getConnection();
    if (conn == null) {
        System.out.println("Database not initialized.");
        return;
    }

    String sql =
        "SELECT " +
        "    E.equipment_ID, " +
        "    E.manufacturer, " +
        "    COUNT(RT.equipment_ID) AS total_times_rented " +
        "FROM Equipment AS E " +
        "LEFT JOIN Rental_Transaction AS RT " +
        "    ON E.equipment_ID = RT.equipment_ID " +
        "GROUP BY E.equipment_ID, E.manufacturer " +
        "ORDER BY total_times_rented DESC;";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        System.out.println("\nResults:");
        SQL.sqlQuery(ps);
    } catch (SQLException e) {
        System.out.println("Error running report: " + e.getMessage());
    }
}

    /*
     * Report 3: Most Frequent Equipment Manufacturer
     */
    private static void runReport3(Scanner input) {
        System.out.println("\n=== Report 3: Most Frequent Equipment Manufacturer ===");

        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String sql =
            "SELECT manufacturer " +
            "FROM (" +
            "    SELECT E.manufacturer, COUNT(RT.equipment_id) AS rented_count " +
            "    FROM Equipment AS E " +
            "    JOIN Rental_Transaction AS RT ON E.equipment_id = RT.equipment_id " +
            "    GROUP BY E.manufacturer " +
            "    ORDER BY rented_count DESC" +
            ") " +
            "LIMIT 1;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("\nResults:");
            SQL.sqlQuery(ps);
        } catch (SQLException e) {
            System.out.println("Error running report: " + e.getMessage());
        }
    }

    /*
     * Report 4: Total Miles Flown by Drone
     */
    private static void runReport4(Scanner input) {
        System.out.println("\n=== Report 4: Drone Miles Flown ===");

        Connection conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String sql =
            "SELECT D.drone_ID, D.manufacturer, SUM(DD.miles_flown) AS total_miles_flown " +
            "FROM Drone AS D " +
            "JOIN Drone_Performs_Delivery AS DD ON D.drone_ID = DD.drone_ID " +
            "GROUP BY D.drone_ID, D.manufacturer " +
            "ORDER BY total_miles_flown DESC;";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("\nResults:");
            SQL.sqlQuery(ps);
        } catch (SQLException e) {
            System.out.println("Error running report: " + e.getMessage());
        }
    }

    /*
     * Report 5: Member with Most Items Rented
     */
    private static void runReport5(Scanner input) {
        System.out.println("\n=== Report 5: Member with Most Items Rented ===");

        var conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        String sql =
            "SELECT M.member_id, M.f_name, M.l_name, COUNT(R.rental_id) AS Rentals " +
            "FROM Community_Member AS M " +
            "JOIN Rental_Transaction AS R ON R.member_id = M.member_id " +
            "GROUP BY R.member_id " +
            "ORDER BY COUNT(R.rental_id) DESC " +
            "LIMIT 1;";

        try (var ps = conn.prepareStatement(sql)) {
            System.out.println("\nResults:");
            SQL.sqlQuery(ps);
        } catch (Exception e) {
            System.out.println("Error running report: " + e.getMessage());
        }
    }

    /*
     * Report 6: Equipment by Type Released Before a Given Year
     */
    private static void runReport6(Scanner input) {
        System.out.println("\n=== Report 6: Equipment by Type Released Before a Given Year ===");

        var conn = SQL.getConnection();
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        // Prompt for equipment type
        System.out.print("Enter equipment type (or 'q' to cancel): ");
        String type = input.nextLine().trim();
        if (type.equalsIgnoreCase("q")) {
            System.out.println("Canceled.");
            return;
        }

        // Prompt for year
        Integer year = null;
        while (year == null) {
            System.out.print("Enter cutoff year (e.g., 2020), or 'q' to cancel: ");
            String inputYear = input.nextLine().trim();
            if (inputYear.equalsIgnoreCase("q")) {
                System.out.println("Canceled.");
                return;
            }
            try {
                year = Integer.parseInt(inputYear);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year. Please enter a numeric value.");
            }
        }

        String sql =
            "SELECT equipment_id, type, manufacturer, year " +
            "FROM Equipment " +
            "WHERE type = ? " +
            "AND year < ?;";

        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt(2, year);

            System.out.println("\nResults:");
            SQL.sqlQuery(ps);

        } catch (Exception e) {
            System.out.println("Error running report: " + e.getMessage());
        }
    }
}