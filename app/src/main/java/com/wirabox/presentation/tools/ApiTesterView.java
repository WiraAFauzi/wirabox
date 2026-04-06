package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.api.ApiResponse;
import com.wirabox.infrastructure.api.ApiService;
import com.wirabox.presentation.components.LoadingSpinner;
import com.wirabox.utils.JsonUtil;
import com.wirabox.utils.JsonTreeUtil;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class ApiTesterView extends BorderPane {

    private final TextField urlField;
    private final ComboBox<String> methodBox;
    private final Button sendButton;

    private final TextArea prettyArea;
    private final TextArea rawArea;
    private final TreeView<String> jsonTree;

    private final Label statusLabel;
    private final Label timeLabel;

    private final LoadingSpinner spinner;

    private final ApiService apiService;

    public ApiTesterView() {

        apiService = new ApiService();

        // ✅ FIXED HERE
        urlField = new TextField();
        urlField.setPromptText("Enter API URL...");
        // ❌ REMOVED: setText("https://api.github.com");

        methodBox = new ComboBox<>();
        methodBox.getItems().addAll("GET", "POST", "PUT", "DELETE");
        methodBox.setValue("GET");

        sendButton = new Button("Send");

        prettyArea = new TextArea();
        prettyArea.setEditable(false);

        rawArea = new TextArea();
        rawArea.setEditable(false);

        jsonTree = new TreeView<>();

        spinner = new LoadingSpinner();
        spinner.setVisible(false);

        statusLabel = new Label("Status: -");
        statusLabel.getStyleClass().add("api-info-label");

        timeLabel = new Label("Time: -");
        timeLabel.getStyleClass().add("api-info-label");

        buildLayout();
        registerActions();
    }

    private void buildLayout() {

        HBox topBar = new HBox(10, methodBox, urlField, sendButton);
        topBar.setPadding(new Insets(10));
        HBox.setHgrow(urlField, Priority.ALWAYS);

        TabPane tabs = new TabPane();

        Tab prettyTab = new Tab("Pretty", prettyArea);
        prettyTab.setClosable(false);

        Tab rawTab = new Tab("Raw", rawArea);
        rawTab.setClosable(false);

        Tab treeTab = new Tab("Tree", jsonTree);
        treeTab.setClosable(false);

        tabs.getTabs().addAll(prettyTab, rawTab, treeTab);

        StackPane responseContainer = new StackPane();
        responseContainer.getChildren().addAll(tabs, spinner);

        HBox infoBar = new HBox(20, statusLabel, timeLabel);
        infoBar.setPadding(new Insets(10));

        setTop(topBar);
        setCenter(responseContainer);
        setBottom(infoBar);
    }

    private void registerActions() {

        sendButton.setOnAction(e -> sendRequest());

        urlField.textProperty().addListener((obs, oldText, newText) -> clearUrlError());
    }

    private void showUrlError() {
        if (!urlField.getStyleClass().contains("url-error")) {
            urlField.getStyleClass().add("url-error");
        }
    }

    private void clearUrlError() {
        urlField.getStyleClass().remove("url-error");
    }

    private void sendRequest() {

        String url = urlField.getText();
        String method = methodBox.getValue();

        // URL validation
        try {
            new java.net.URI(url).toURL();
            clearUrlError();
        } catch (Exception e) {

            showUrlError();

            prettyArea.setText("Invalid URL");
            rawArea.setText("Invalid URL");
            jsonTree.setRoot(null);

            return;
        }

        spinner.setVisible(true);

        Task<ApiResponse> task = new Task<>() {

            @Override
            protected ApiResponse call() throws Exception {

                switch (method) {

                    case "GET":
                        return apiService.sendGet(url);

                    case "POST":
                        return apiService.sendPost(url, "{}");

                    case "PUT":
                        return apiService.sendPut(url, "{}");

                    case "DELETE":
                        return apiService.sendDelete(url);
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {

            spinner.setVisible(false);

            ApiResponse response = task.getValue();

            if (response != null) {

                statusLabel.setText("Status: " + response.getStatusCode());
                timeLabel.setText("Time: " + response.getResponseTime() + " ms");

                String rawBody = response.getBody();
                String prettyBody = JsonUtil.prettyPrint(rawBody);

                rawArea.setText(rawBody);
                prettyArea.setText(prettyBody);

                jsonTree.setRoot(JsonTreeUtil.buildTree(rawBody));
            }
        });

        task.setOnFailed(event -> {

            spinner.setVisible(false);

            String error = "Error:\n" + task.getException().getMessage();

            rawArea.setText(error);
            prettyArea.setText(error);
            jsonTree.setRoot(null);
        });

        new Thread(task).start();
    }
}