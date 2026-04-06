package com.wirabox.presentation.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopBarView extends HBox {

    private final Label titleLabel;
    private final Label licenseBadge;

    public TopBarView() {

        setPrefHeight(50);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);
        getStyleClass().add("top-bar");

        titleLabel = new Label("Dashboard");
        titleLabel.getStyleClass().add("top-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        licenseBadge = new Label("FREE");
        licenseBadge.getStyleClass().add("license-badge");

        getChildren().addAll(titleLabel, spacer, licenseBadge);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setLicenseStatus(String status) {
        licenseBadge.setText(status);
    }
}