package com.wirabox.presentation.tools;

import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class DatabaseTab extends VBox {

    private final TableView<List<Object>> tableView = new TableView<>();

    public DatabaseTab() {
        getChildren().add(tableView);
    }

    public TableView<List<Object>> getTableView() {
        return tableView;
    }
}