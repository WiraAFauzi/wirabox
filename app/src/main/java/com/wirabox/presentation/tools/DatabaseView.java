package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.ConnectionService;
import com.wirabox.infrastructure.DatabaseService;
import com.wirabox.infrastructure.QueryHistoryService;
import com.wirabox.infrastructure.QueryResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class DatabaseView extends BorderPane {

    private final DatabaseService databaseService = new DatabaseService();
    private final QueryHistoryService historyService = new QueryHistoryService();
    private final ConnectionService connectionService = new ConnectionService();

    private final Label statusLabel = new Label("Not connected");

    private final ListView<String> tableList = new ListView<>();
    private final ListView<String> connectionList = new ListView<>();

    private final TabPane tabPane = new TabPane();

    private final TextArea queryArea = new TextArea();

    private final Button runQueryBtn = new Button("Run Query");
    private final Button exportBtn = new Button("Export CSV");
    private final Button saveConnBtn = new Button("Save Connection");

    private final ListView<String> historyList = new ListView<>();

    private File selectedFile;

    public DatabaseView() {

        setPadding(new Insets(15));

        getStylesheets().add(
                getClass().getResource("/dark-table.css").toExternalForm()
        );

        // =========================
        // TOP BAR
        // =========================
        Button openBtn = new Button("Select DB File");
        Button connectBtn = new Button("Connect");

        HBox topBar = new HBox(10, openBtn, connectBtn, saveConnBtn);
        VBox topContainer = new VBox(10, topBar, statusLabel);

        // =========================
        // QUERY
        // =========================
        queryArea.setPromptText("Write SQL query here...");
        queryArea.setPrefHeight(100);

        HBox queryBar = new HBox(10, runQueryBtn, exportBtn);

        historyList.setPrefHeight(120);

        VBox querySection = new VBox(10,
                new Label("SQL Editor"),
                queryArea,
                queryBar,
                new Label("Query History"),
                historyList
        );

        VBox fullTop = new VBox(15, topContainer, querySection);

        // =========================
        // LEFT PANEL
        // =========================
        connectionList.setPrefHeight(120);

        VBox leftPane = new VBox(
                new Label("Saved Connections"),
                connectionList,
                new Label("Tables"),
                tableList
        );

        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10));
        tableList.setPrefWidth(200);

        setTop(fullTop);
        setLeft(leftPane);
        setCenter(tabPane);

        refreshConnections();

        // =========================
        // FILE PICKER
        // =========================
        openBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select SQLite Database");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("SQLite DB Files", "*.db")
            );

            File file = fileChooser.showOpenDialog(getScene().getWindow());

            if (file != null) {
                selectedFile = file;
                statusLabel.setText("Selected: " + file.getName());
            }
        });

        connectBtn.setOnAction(e -> connectToDatabase());

        saveConnBtn.setOnAction(e -> {
            if (selectedFile == null) {
                statusLabel.setText("⚠ Select DB first");
                return;
            }

            connectionService.saveConnection(selectedFile.getAbsolutePath());
            refreshConnections();
            statusLabel.setText("✅ Connection saved");
        });

        connectionList.setOnMouseClicked(e -> {
            String path = connectionList.getSelectionModel().getSelectedItem();

            if (path != null) {
                selectedFile = new File(path);
                connectToDatabase();
            }
        });

        tableList.setOnMouseClicked(e -> {
            String table = tableList.getSelectionModel().getSelectedItem();
            if (table != null) loadTableData(table);
        });

        runQueryBtn.setOnAction(e -> runQuery());
        exportBtn.setOnAction(e -> exportToCSV());

        historyList.setOnMouseClicked(e -> {
            String selected = historyList.getSelectionModel().getSelectedItem();
            if (selected != null) queryArea.setText(selected);
        });
    }

    // =========================
    // CONNECT → CREATE TAB
    // =========================
    private void connectToDatabase() {
        if (selectedFile == null) {
            statusLabel.setText("⚠ No DB selected");
            return;
        }

        try {
            databaseService.connect(selectedFile.getAbsolutePath());

            DatabaseTab dbTab = new DatabaseTab(); // ✅ SAFE
            Tab tab = new Tab(selectedFile.getName());
            tab.setContent(dbTab);
            tab.setClosable(true);

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            statusLabel.setText("✅ Connected: " + selectedFile.getName());
            loadTables();

        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    // =========================
    // GET CURRENT TABLE SAFE
    // =========================
    private TableView<List<Object>> getCurrentTable() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();

        if (tab != null && tab.getContent() instanceof DatabaseTab) {
            return ((DatabaseTab) tab.getContent()).getTableView();
        }

        return null;
    }

    // =========================
    // RUN QUERY
    // =========================
    private void runQuery() {
        String sql = queryArea.getText();

        if (sql == null || sql.isBlank()) {
            statusLabel.setText("⚠ Enter query");
            return;
        }

        TableView<List<Object>> tableView = getCurrentTable();

        if (tableView == null) {
            statusLabel.setText("⚠ No active tab");
            return;
        }

        try {
            QueryResult result = databaseService.executeQuery(sql);

            String[] queries = sql.split(";");
            for (String q : queries) {
                if (!q.trim().isEmpty()) {
                    historyService.addQuery(q.trim() + ";");
                }
            }

            historyList.getItems().setAll(historyService.getHistory());

            if (result.isSelect()) {
                renderTable(tableView, result.getColumns(), result.getRows());
                statusLabel.setText("✅ Query executed");
            } else {
                statusLabel.setText("Rows affected: " + result.getAffectedRows());
            }

        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    // =========================
    // RENDER TABLE
    // =========================
    private void renderTable(TableView<List<Object>> tableView,
                             List<String> columns,
                             List<List<Object>> rows) {

        tableView.getColumns().clear();
        tableView.getItems().clear();

        for (int i = 0; i < columns.size(); i++) {
            final int index = i;

            TableColumn<List<Object>, String> col =
                    new TableColumn<>(columns.get(i));

            col.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            data.getValue().get(index) == null
                                    ? ""
                                    : data.getValue().get(index).toString()
                    )
            );

            tableView.getColumns().add(col);
        }

        tableView.getItems().addAll(rows);
    }

    // =========================
    // EXPORT CSV
    // =========================
    private void exportToCSV() {

        TableView<List<Object>> tableView = getCurrentTable();

        if (tableView == null || tableView.getItems().isEmpty()) {
            statusLabel.setText("⚠ No data to export");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = chooser.showSaveDialog(getScene().getWindow());
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(file)) {

            for (TableColumn<?, ?> col : tableView.getColumns()) {
                writer.print(col.getText() + ",");
            }
            writer.println();

            for (List<Object> row : tableView.getItems()) {
                for (Object cell : row) {
                    writer.print((cell != null ? cell : "") + ",");
                }
                writer.println();
            }

            statusLabel.setText("✅ Exported CSV");

        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    private void loadTables() {
        try {
            tableList.getItems().setAll(databaseService.getTables());
        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    private void loadTableData(String table) {
        try {
            QueryResult result = databaseService.executeQuery("SELECT * FROM " + table);
            TableView<List<Object>> tv = getCurrentTable();
            if (tv != null) {
                renderTable(tv, result.getColumns(), result.getRows());
            }
        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    private void refreshConnections() {
        connectionList.getItems().setAll(connectionService.loadConnections());
    }
}