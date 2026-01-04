package utilities;

public enum EntityDefinition {
    // index, menu label, table name, attributes (column names in DB)
    DRONE(
        1,
        "Drone",
        "Drone",
        new String[] { "manufacturer", "drone_id", "year", "load_capacity",
            "warranty_expir", "wrhs_address", "order_num" }
    ),
    EQUIPMENT(
        2,
        "Equipment",
        "Equipment",
        new String[] { "type", "year", "order_num", "wrhs_address", "equipment_id",
            "status", "weight", "warranty_expiration" }
    ),
    RENTAL_TRANSACTION(
        3,
        "Rental Transaction",
        "Rental_Transaction",
        new String[] { "checkout_date", "rental_id", "equipment_id", "member_id",
            "due_date", "return_date", "rental_fee" }
    ),
    COMMUNITY_MEMBER(
        4,
        "Community Member",
        "Community_Member",
        new String[] { "member_id", "wrhs_address", "f_name", "m_name", "l_name",
            "mbr_address", "phone", "email", "start_date", "mbr_status",
            "wrhs_distance" }
    ),
    DRONE_PERFORMS_DELIVERY(
        5,
        "Drone Performs Delivery",
        "Drone_Performs_Delivery",
        new String[] { "rental_id", "drone_id", "miles_flown" }
    ),
    DRONE_PERFORMS_RETURN(
        6,
        "Drone Performs Return",
        "Drone_Performs_Return",
        new String[] { "rental_id", "drone_id" }
    ),
    DRONE_UNDERGO_MAINT(
        7,
        "Drone Undergoes Maintenance",
        "Drone_Undergo_Maint",
        new String[] { "maint_id", "drone_id" }
    ),
    EMPLOYEE(
        8,
        "Employee",
        "Employee",
        new String[] { "f_name", "m_name", "l_name", "ssn", "wrhs_address", "role",
            "phone_no", "email", "hire_date", "pay_rate", "hrs_worked" }
    ),
    EQUIPMENT_UNDERGO_MAINT(
        9,
        "Equipment Undergoes Maintenance",
        "Equip_Undergo_Maint",
        new String[] { "maint_id", "equipment_id" }
    ),
    MAINTENANCE_RECORD(
        10,
        "Maintenance Record",
        "Maintenance_Record",
        new String[] { "maint_id", "employee_ssn", "maint_date", "issue", "action",
            "next_due_date", "cost", "maint_hrs" }
    ),
    PURCHASE_ORDER(
        11,
        "Purchase Order",
        "Purchase_Order",
        new String[] { "quantity", "order_num", "wrhs_address", "value",
            "est_arrive_date","actual_arrive_date"  }
    ),
    RATING_AND_REVIEW(
        12,
        "Rating and Review",
        "Rating_and_Review",
        new String[] { "rating", "member_id", "equipment_id", "comments", "date" }
    ),
    WAREHOUSE(
        13,
        "Warehouse",
        "Warehouse",
        new String[] { "phone", "wrhs_address", "manager_ssn", "equipment_cap",
            "drone_cap" }
    ),
    WAREHOUSE_HOUSES_DRONE(
        14,
        "Warehouse Houses Drone",
        "Warehouse_Houses_Drone",
        new String[] { "start_date", "wrhs_address", "drone_id" }
    );

    private final int index;
    private final String menuLabel;
    private final String tableName;
    private final String[] attributes;

    EntityDefinition(int index, String menuLabel, String tableName, String[] attributes) {
        this.index = index;
        this.menuLabel = menuLabel;
        this.tableName = tableName;
        this.attributes = attributes;
    }

    public int getIndex() {
        return index;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public static EntityDefinition fromIndex(int idx) {
        for (EntityDefinition e : values()) {
            if (e.index == idx) {
                return e;
            }
        }
        return null;
    }
}

