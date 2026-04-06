package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.LogService;
import com.wirabox.presentation.utils.AnimationUtil;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class LogViewerView extends VBox {

    private final TextArea logArea = new TextArea();
    private final TextField searchField = new TextField();
    private final Label statusLabel = new Label();

    private List<String> originalLines;

    // 🔥 Debounce (prevents UI freeze)
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    public LogViewerView() {
        setupUI();
        setupActions();
    }

    private void setupUI() {

        setSpacing(12);
        setPadding(new Insets(15));

        Button openBtn = new Button("Open Log File");

        searchField.setPromptText("Search logs... 🔍");

        logArea.setEditable(false);
        logArea.setWrapText(false);

        HBox topBar = new HBox(10, openBtn, searchField);

        getChildren().addAll(
                topBar,
                logArea,
                statusLabel
        );

        openBtn.setOnAction(e -> openFile());
    }

    private void setupActions() {

        searchField.textProperty().addListener((obs, oldVal, keyword) -> {

            if (originalLines == null) return;

            // 🔥 Debounce search (wait before executing)
            debounce.setOnFinished(event -> performSearch(keyword));
            debounce.playFromStart();
        });
    }

    // 🔥 Heavy work moved here (NOT every keystroke)
    private void performSearch(String keyword) {

        List<String> filtered = LogService.filterLines(originalLines, keyword);

        StringBuilder result = new StringBuilder();

        for (String line : filtered) {

            String styledLine = line;

            // 🔥 LOG LEVEL TAGGING
            String lower = line.toLowerCase();

            if (lower.contains("error")) {
                styledLine = "🔴 " + styledLine;
            } else if (lower.contains("warn")) {
                styledLine = "🟡 " + styledLine;
            } else if (lower.contains("info")) {
                styledLine = "🔵 " + styledLine;
            }

            // 🔍 SEARCH HIGHLIGHT
            if (keyword != null && !keyword.isEmpty()) {

                String lowerLine = styledLine.toLowerCase();
                String lowerKeyword = keyword.toLowerCase();

                int index = 0;

                while ((index = lowerLine.indexOf(lowerKeyword, index)) != -1) {

                    String before = styledLine.substring(0, index);
                    String match = styledLine.substring(index, index + keyword.length());
                    String after = styledLine.substring(index + keyword.length());

                    styledLine = before + "[" + match.toUpperCase() + "]" + after;

                    lowerLine = styledLine.toLowerCase();
                    index += keyword.length();
                }
            }

            result.append(styledLine).append("\n");
        }

        logArea.setText(result.toString());

        statusLabel.setText("🔍 Found " + filtered.size() + " matches");

        scrollToTop();
    }

    private void openFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Log File");

        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file == null) return;

        try {
            Path path = file.toPath();

            originalLines = LogService.loadLines(path);

            logArea.setText(String.join("\n", originalLines));

            statusLabel.setText("✅ Loaded: " + file.getName() +
                    " (" + originalLines.size() + " lines)");

            AnimationUtil.playSuccessGlow(logArea);

            scrollToTop();

        } catch (Exception e) {
            statusLabel.setText("❌ Error loading file");
        }
    }

    // ✅ SAFE UI SCROLL
    private void scrollToTop() {
        Platform.runLater(() -> logArea.positionCaret(0));
    }
}