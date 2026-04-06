package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.JsonService;
import com.wirabox.presentation.utils.AnimationUtil;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonFormatterView extends BorderPane {

    private CodeArea inputArea;
    private CodeArea outputArea;

    private Button formatButton;
    private Button minifyButton;
    private Button copyButton;
    private Button downloadButton;

    private Label statusLabel;

    private final JsonService jsonService;

    public JsonFormatterView() {
        this.jsonService = new JsonService();
        initializeUI();
        setupActions();
        setupKeyboardShortcut();
        applyHighlighting(inputArea);
        applyHighlighting(outputArea);
    }

    private void initializeUI() {

        inputArea = new CodeArea();
        inputArea.setParagraphGraphicFactory(LineNumberFactory.get(inputArea));
        inputArea.setWrapText(true);
        inputArea.getStyleClass().add("code-area");

        inputArea.getStylesheets().add(
                getClass().getResource("/theme.css").toExternalForm()
        );
        inputArea.setStyle("-fx-background-color: #1e1e1e;");

        outputArea = new CodeArea();
        outputArea.setEditable(false);
        outputArea.setParagraphGraphicFactory(LineNumberFactory.get(outputArea));
        outputArea.setWrapText(true);
        outputArea.getStyleClass().add("code-area");

        outputArea.getStylesheets().add(
                getClass().getResource("/theme.css").toExternalForm()
        );
        outputArea.setStyle("-fx-background-color: #1e1e1e;");

        formatButton = new Button("Format");
        minifyButton = new Button("Minify");
        copyButton = new Button("Copy");
        downloadButton = new Button("Download");

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        HBox buttonBar = new HBox(10, formatButton, minifyButton, copyButton, downloadButton);

        StackPane inputPane = new StackPane(inputArea);
        StackPane outputPane = new StackPane(outputArea);

        VBox.setVgrow(inputPane, Priority.ALWAYS);
        VBox.setVgrow(outputPane, Priority.ALWAYS);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        layout.getChildren().addAll(
                new Label("Input JSON"),
                inputPane,
                buttonBar,
                statusLabel,
                new Label("Output"),
                outputPane
        );

        setCenter(layout);
    }

    private void setupActions() {

        formatButton.setOnAction(e -> formatJson());
        minifyButton.setOnAction(e -> minifyJson());

        copyButton.setOnAction(e -> {
            outputArea.selectAll();
            outputArea.copy();
            statusLabel.setText("📋 Copied to clipboard");
        });

        downloadButton.setOnAction(e -> downloadJson());
    }

    private void formatJson() {

        String input = inputArea.getText();

        if (input == null || input.trim().isEmpty()) {
            showError("❌ Input is empty");
            return;
        }

        try {
            String formatted = jsonService.format(input);

            outputArea.replaceText(formatted);
            statusLabel.setText("✅ JSON formatted");

            inputArea.setStyle(null);
            AnimationUtil.playSuccessGlow(outputArea);

        } catch (Exception ex) {
            showError("❌ Invalid JSON");
        }
    }

    private void minifyJson() {

        String input = inputArea.getText();

        if (input == null || input.trim().isEmpty()) {
            showError("❌ Input is empty");
            return;
        }

        try {
            String minified = jsonService.format(input)
                    .replaceAll("\\s+", "");

            outputArea.replaceText(minified);
            statusLabel.setText("⚡ JSON minified");

            inputArea.setStyle(null);
            AnimationUtil.playSuccessGlow(outputArea);

        } catch (Exception ex) {
            showError("❌ Invalid JSON");
        }
    }

    private void downloadJson() {

        String content = outputArea.getText();

        if (content == null || content.isEmpty()) {
            showError("❌ Nothing to download");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.setInitialFileName("data.json");

        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                statusLabel.setText("💾 File saved successfully");
            } catch (Exception e) {
                showError("❌ Failed to save file");
            }
        }
    }

    private void setupKeyboardShortcut() {

        inputArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                formatJson();
                event.consume();
            }
        });
    }

    private void applyHighlighting(CodeArea area) {

        area.textProperty().addListener((obs, oldText, newText) -> {
            area.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    // ✅ FINAL CORRECT REGEX
    private static final Pattern JSON_PATTERN = Pattern.compile(
            "\"([^\"]+)\"(?=\\s*:)" +   // keys ONLY
            "|\"([^\"]+)\"" +           // string values
            "|\\b(true|false|null)\\b" +
            "|-?\\d+(\\.\\d+)?"
    );

    private StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = JSON_PATTERN.matcher(text);
        int lastEnd = 0;

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);

            if (matcher.group(1) != null) {
                spansBuilder.add(Collections.singleton("json-key"), matcher.end() - matcher.start());
            } else if (matcher.group(2) != null) {
                spansBuilder.add(Collections.singleton("json-string"), matcher.end() - matcher.start());
            } else if (matcher.group(3) != null) {
                spansBuilder.add(Collections.singleton("json-keyword"), matcher.end() - matcher.start());
            } else {
                spansBuilder.add(Collections.singleton("json-number"), matcher.end() - matcher.start());
            }

            lastEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);

        return spansBuilder.create();
    }

    private void showError(String message) {

        statusLabel.setText(message);
        outputArea.clear();

        inputArea.setStyle("-fx-border-color: red;");
        AnimationUtil.playErrorPulse(inputArea);
    }
}