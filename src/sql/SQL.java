package sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQL {

    /**
     * Single shared connection for the entire application.
     */
    private static Connection conn = null;

    // Private constructor to prevent instantiation
    private SQL() {
    }

    /**
     * Connects to the database if it exists, creates it if it does not.
     *
     * @param databaseFileName the database file name
     */
    public static void initializeDB(String databaseFileName) {
        if (conn != null) {
            // Already initialized
            return;
        }

        String url = "jdbc:sqlite:" + databaseFileName;

        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("The connection to the database was successful.");
            } else {
                System.out.println("Null Connection");
            }
        } catch (SQLException e) {
            System.out.println("There was a problem connecting to the database.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the underlying Connection for internal use in this class.
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Close the connection when the program ends.
     */
    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing database connection: " + e.getMessage());
            } finally {
                conn = null;
            }
        }
    }

    /**
     * Generic INSERT for any table using PreparedStatement.
     *
     * @param tableName the table to insert into
     * @param columns   the column names in order
     * @param values    the values for each column, same order, as Strings
     */
    public static void insertRow(String tableName, String[] columns, String[] values)
            throws SQLException {

        if (conn == null) {
            throw new IllegalStateException("Database not initialized. Call SQL.initializeDB(...) first.");
        }

        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values length mismatch");
        }

        // Build: INSERT INTO tableName (col1, col2, ...) VALUES (?, ?, ...)
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");

        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i]);
            if (i < columns.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(") VALUES (");

        for (int i = 0; i < columns.length; i++) {
            sb.append("?");
            if (i < columns.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(");");

        String sql = sb.toString();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // For SQLite, we can safely bind everything as strings.
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);  // 1-based index
            }

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " row(s) inserted into " + tableName + ".");
        }
    }

    /*
     * Query helpers
     */

    /**
     * Prints column names and all rows from a ResultSet.
     */
    private static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // Print header row (column names)
        for (int i = 1; i <= columnCount; i++) {
            String value = rsmd.getColumnName(i);
            System.out.print(value);
            if (i < columnCount) {
                System.out.print(",  ");
            }
        }
        System.out.print("\n");

        // Print each row
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnValue = rs.getString(i);
                System.out.print(columnValue);
                if (i < columnCount) {
                    System.out.print(",  ");
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Queries the database and prints the results.
     *
     * This version uses a simple Statement, so it's for static SQL SELECT
     * statements without user-supplied pieces in the SQL string.
     *
     * @param sql a SQL SELECT statement that returns rows
     */
    public static void sqlQuery(String sql) {
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            printResultSet(rs);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Queries the database and prints the results.
     *
     * This version uses a PreparedStatement for dynamic SQL SELECT statements,
     * where parameters are bound with setXxx(...) before calling this method.
     *
     * NOTE: This method does NOT close the PreparedStatement; the caller should
     * use try-with-resources when creating it.
     *
     * @param ps a PreparedStatement ready to be executed (all parameters set)
     */
    public static void sqlQuery(PreparedStatement ps) {
        if (conn == null) {
            System.out.println("Database not initialized.");
            return;
        }

        try (ResultSet rs = ps.executeQuery()) {
            printResultSet(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}