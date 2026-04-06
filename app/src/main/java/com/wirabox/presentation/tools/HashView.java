package com.wirabox.presentation.tools;

import com.wirabox.infrastructure.HashService;
import com.wirabox.presentation.utils.AnimationUtil;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class HashView extends VBox {

    private final TextArea inputArea = new TextArea();

    private final TextField md5Field = new TextField();
    private final TextField sha1Field = new TextField();
    private final TextField sha256Field = new TextField();
    private final TextField sha512Field = new TextField();

    private final Label statusLabel = new Label();

    public HashView() {
        setupUI();
        setupLiveUpdate(); // 🔥 NEW
    }

    private void setupUI() {

        setSpacing(12);
        setPadding(new Insets(15));

        inputArea.setPromptText("Type here... hashes update instantly 🔥");
        inputArea.setWrapText(true);

        md5Field.setEditable(false);
        sha1Field.setEditable(false);
        sha256Field.setEditable(false);
        sha512Field.setEditable(false);

        getChildren().addAll(
                new Label("Input"),
                inputArea,
                createHashRow("MD5", md5Field),
                createHashRow("SHA-1", sha1Field),
                createHashRow("SHA-256", sha256Field),
                createHashRow("SHA-512", sha512Field),
                statusLabel
        );
    }

    private HBox createHashRow(String label, TextField field) {

        Label lbl = new Label(label);
        lbl.setMinWidth(80);

        Button copyBtn = new Button("Copy");

        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(field.getText());
            clipboard.setContent(content);

            statusLabel.setText("📋 Copied " + label);
        });

        return new HBox(10, lbl, field, copyBtn);
    }

    // 🔥 LIVE HASH UPDATE
    private void setupLiveUpdate() {

        inputArea.textProperty().addListener((obs, oldText, newText) -> {

            if (newText == null || newText.isEmpty()) {
                clearFields();
                statusLabel.setText("");
                return;
            }

            try {
                md5Field.setText(HashService.hash(newText, "MD5"));
                sha1Field.setText(HashService.hash(newText, "SHA-1"));
                sha256Field.setText(HashService.hash(newText, "SHA-256"));
                sha512Field.setText(HashService.hash(newText, "SHA-512"));

                statusLabel.setText("⚡ Live hashes updated");

                AnimationUtil.playSuccessGlow(this);

            } catch (Exception e) {
                statusLabel.setText("❌ " + e.getMessage());
            }
        });
    }

    private void clearFields() {
        md5Field.clear();
        sha1Field.clear();
        sha256Field.clear();
        sha512Field.clear();
    }
}