package com.wirabox.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private Connection connection;

    // Connect to database
    public void connect(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        connection = DriverManager.getConnection(url);
    }

    // Disconnect
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Check connection
    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    public Connection getConnection() {
        return connection;
    }

    // Get all table names
    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();

        String query = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            tables.add(rs.getString("name"));
        }

        rs.close();
        stmt.close();

        return tables;
    }

    // Get table data (rows)
    public List<List<Object>> getTableData(String tableName) throws SQLException {
        List<List<Object>> rows = new ArrayList<>();

        String query = "SELECT * FROM " + tableName;

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        int columnCount = rs.getMetaData().getColumnCount();

        while (rs.next()) {
            List<Object> row = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }

            rows.add(row);
        }

        rs.close();
        stmt.close();

        return rows;
    }

    // Get column names
    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();

        String query = "SELECT * FROM " + tableName + " LIMIT 1";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        int columnCount = rs.getMetaData().getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columns.add(rs.getMetaData().getColumnName(i));
        }

        rs.close();
        stmt.close();

        return columns;
    }

    // 🔥 FINAL — Execute SQL query
    public QueryResult executeQuery(String sql) throws SQLException {
        QueryResult result = new QueryResult();

        Statement stmt = connection.createStatement();

        boolean hasResultSet = stmt.execute(sql);

        if (hasResultSet) {
            // SELECT query
            ResultSet rs = stmt.getResultSet();

            List<String> columns = new ArrayList<>();
            List<List<Object>> rows = new ArrayList<>();

            int columnCount = rs.getMetaData().getColumnCount();

            // Columns
            for (int i = 1; i <= columnCount; i++) {
                columns.add(rs.getMetaData().getColumnName(i));
            }

            // Rows
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rows.add(row);
            }

            result.setSelect(true);
            result.setColumns(columns);
            result.setRows(rows);

            rs.close();

        } else {
            // INSERT / UPDATE / DELETE
            int affected = stmt.getUpdateCount();

            result.setSelect(false);
            result.setAffectedRows(affected);
            result.setMessage("Query executed successfully.");
        }

        stmt.close();

        return result;
    }
}