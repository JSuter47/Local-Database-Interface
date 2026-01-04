import java.util.Scanner;
import options.*;
import sql.SQL;
import utilities.Utilities;

public class DBMSPrimaryInterface {	
	
	private static final String DATABASE = "DroneDeliveryDBS.db";

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Initialize database (handled inside SQL.java)
         */
        SQL.initializeDB(DATABASE);

        /*
         * Read user input until they request to exit.
         */
        Scanner input = new Scanner(System.in);
        String selection = "";
        while (!selection.equals("0")) {
            Utilities.printMainMenu();
            selection = input.nextLine().trim();
            switch (selection) {
                case "1":
                    ManageRentals.rentalsMenu(input);
                    break;
                case "2":
                    Integer defaultValue = 0;
                    NewEntity.createNew(defaultValue, input);
                    break;
                case "3":
                    Update.editDelete(input);
                    break;
                case "4":
                    Search.search(input);
                    break;
                case "5":
                    List.listAll(input);
                    break;
                case "6":
                    Reports.generateReports(input);
                    break;
                case "0":
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid input.");
            }
        }

        /*
         * Close input stream and DB connection.
         */
        input.close();
        SQL.closeConnection();
    }
}