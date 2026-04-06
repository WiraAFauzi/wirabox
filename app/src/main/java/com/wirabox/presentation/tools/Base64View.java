package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.Base64Service;
import com.wirabox.presentation.utils.AnimationUtil;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class Base64View extends VBox {

    private final TextArea inputArea = new TextArea();
    private final TextArea outputArea = new TextArea();
    private final Label statusLabel = new Label();

    public Base64View() {
        setupUI();
        setupActions();
    }

    private void setupUI() {

        setSpacing(12);
        setPadding(new Insets(15));

        inputArea.setPromptText("Enter text or Base64 here...");
        inputArea.setWrapText(true);

        outputArea.setPromptText("Result will appear here...");
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        Button encodeBtn = new Button("Encode");
        Button decodeBtn = new Button("Decode");
        Button swapBtn = new Button("Swap");
        Button clearBtn = new Button("Clear");
        Button copyBtn = new Button("Copy");

        encodeBtn.getStyleClass().add("primary-button");

        HBox buttonRow = new HBox(10,
                encodeBtn, decodeBtn, swapBtn, clearBtn, copyBtn
        );

        getChildren().addAll(
                new Label("Input"),
                inputArea,
                buttonRow,
                new Label("Output"),
                outputArea,
                statusLabel
        );

        // Actions
        encodeBtn.setOnAction(e -> encode());
        decodeBtn.setOnAction(e -> decode());

        swapBtn.setOnAction(e -> {
            String temp = inputArea.getText();
            inputArea.setText(outputArea.getText());
            outputArea.setText(temp);
            statusLabel.setText("🔁 Swapped");
        });

        clearBtn.setOnAction(e -> {
            inputArea.clear();
            outputArea.clear();
            statusLabel.setText("🧹 Cleared");
        });

        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(outputArea.getText());
            clipboard.setContent(content);
            statusLabel.setText("📋 Copied to clipboard");
        });
    }

    private void setupActions() {

        // 🔥 Ctrl + Enter shortcut
        inputArea.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.ENTER) {
                encode();
            }
        });
    }

    private void encode() {
        try {
            String result = Base64Service.encode(inputArea.getText());
            outputArea.setText(result);

            statusLabel.setText("✅ Encoded successfully");
            AnimationUtil.playSuccessGlow(outputArea);

        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }

    private void decode() {
        try {
            String result = Base64Service.decode(inputArea.getText());
            outputArea.setText(result);

            statusLabel.setText("✅ Decoded successfully");
            AnimationUtil.playSuccessGlow(outputArea);

        } catch (Exception e) {
            statusLabel.setText("❌ " + e.getMessage());
        }
    }
}