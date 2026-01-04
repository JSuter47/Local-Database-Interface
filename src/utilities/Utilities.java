package utilities;

public final class Utilities {

    // Private constructor to prevent instantiation
    private Utilities() {
    }

    /**
     * Displays the entity type selection menu.
     */
    public static void printEntityMenu() {
        System.out.println("Select an entity type:");
        for (EntityDefinition e : EntityDefinition.values()) {
            System.out.println("  " + e.getIndex() + " - " + e.getMenuLabel());
        }
        System.out.println("  0 - Back");
    }

    /**
     * Primary client-facing main menu view.
     */
    public static void printMainMenu() {
        System.out.println("Welcome to Drone Delivery DBMS!");
        System.out.println("Select an option:");
        System.out.println("  (1): Manage Rentals");
        System.out.println("  (2): Create a new entity.");
        System.out.println("  (3): Edit or delete an entity.");
        System.out.println("  (4): Search for an entity.");
        System.out.println("  (5): List all records by entity.");
        System.out.println("  (6): Generate Reports.");
        System.out.println("  (0): Exit");
        System.out.print("> ");
    }

    /**
     * Returns the entity (table) name for a given entity type index.
     *
     * @param type the entity type index as a string
     * @return the table name, or null if invalid
     */
    public static String getEntityNameFromIndex(String type) {
        try {
            int idx = Integer.parseInt(type);
            EntityDefinition def = EntityDefinition.fromIndex(idx);
            return (def != null) ? def.getTableName() : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the attribute list for a given entity type index.
     *
     * @param type the entity type index as a string
     * @return an array of attribute names, or an empty array if invalid
     */
    public static String[] getAttributesForType(String type) {
        try {
            int idx = Integer.parseInt(type);
            EntityDefinition def = EntityDefinition.fromIndex(idx);
            return (def != null) ? def.getAttributes() : new String[0];
        } catch (NumberFormatException e) {
            return new String[0];
        }
    }

    /**
     * Displays the rental transaction menu options.
     */
    public static void printRentalsMenu() {
        System.out.println("Select an option:");
        System.out.println("  (1): Create a new rental transaction.");
        System.out.println("  (2): Register an equipment return.");
        System.out.println("  (3): Schedule a delivery.");
        System.out.println("  (4): Schedule a pickup.");
        System.out.println("  (0): Back to main menu.");
        System.out.print("> ");
    }
}