package com.wirabox;

import com.wirabox.infrastructure.DatabaseService;
import com.wirabox.infrastructure.QueryResult;

import java.util.List;

public class DbTest {
    public static void main(String[] args) {
        try {
            DatabaseService db = new DatabaseService();

            // Connect
            db.connect("test.db");

            System.out.println("Connected: " + db.isConnected());

            // Create table
            db.getConnection().createStatement().execute(
                "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT);"
            );

            // Reset data
            db.getConnection().createStatement().execute("DELETE FROM users;");

            db.getConnection().createStatement().execute(
                "INSERT INTO users (name) VALUES ('Wira'), ('Alex');"
            );

            // Show tables
            System.out.println("\nTables:");
            for (String table : db.getTables()) {
                System.out.println("- " + table);
            }

            // Show table data
            System.out.println("\nData from 'users':");

            List<String> columns = db.getColumnNames("users");
            List<List<Object>> data = db.getTableData("users");

            System.out.println(columns);

            for (List<Object> row : data) {
                System.out.println(row);
            }

            // 🔥 Execute custom query
            System.out.println("\nExecuting custom query:");

            QueryResult result = db.executeQuery("SELECT * FROM users");

            if (result.isSelect()) {
                System.out.println(result.getColumns());
                for (List<Object> row : result.getRows()) {
                    System.out.println(row);
                }
            } else {
                System.out.println("Affected rows: " + result.getAffectedRows());
            }

            db.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}