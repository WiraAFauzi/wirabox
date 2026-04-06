package com.wirabox.presentation.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarView extends VBox {

    private final List<Label> navItems = new ArrayList<>();
    private final Consumer<String> onNavSelected;

    public SidebarView(Consumer<String> onNavSelected) {

        this.onNavSelected = onNavSelected;

        setPrefWidth(220);
        setSpacing(15);
        setPadding(new Insets(25, 15, 25, 15));
        getStyleClass().add("sidebar");

        // =========================
        // 🔥 LOGO SECTION
        // =========================
        Image logoImage = new Image(
                getClass().getResourceAsStream("/wirabox-logo.png")
        );

        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(30);
        logoView.setPreserveRatio(true);

        Label logoText = new Label("WiraBox");
        logoText.getStyleClass().add("sidebar-logo");

        HBox logoBox = new HBox(10, logoView, logoText);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        // =========================
        // NAVIGATION
        // =========================
        VBox navContainer = new VBox(10);
        navContainer.setAlignment(Pos.TOP_LEFT);

        navContainer.getChildren().addAll(
                createNavItem("API Tester", FontAwesome.PAPER_PLANE),
                createNavItem("JSON Formatter", FontAwesome.CODE),
                createNavItem("Base64 Tool", FontAwesome.LOCK),
                createNavItem("Hash Generator", FontAwesome.KEY),
                createNavItem("Log Viewer", FontAwesome.FILE_TEXT),
                createNavItem("Database Viewer", FontAwesome.DATABASE)
        );

        getChildren().addAll(logoBox, navContainer);
    }

    private Label createNavItem(String text, FontAwesome iconLiteral) {

        FontIcon icon = new FontIcon(iconLiteral);
        icon.setIconSize(14);
        icon.getStyleClass().add("nav-icon");

        Label label = new Label(text, icon);
        label.setGraphicTextGap(10);
        label.setPrefWidth(190);
        label.getStyleClass().add("nav-item");

        navItems.add(label);

        // 🔥 Click event
        label.setOnMouseClicked(e -> {
            setActive(label);
            onNavSelected.accept(text);
        });

        // 🎨 Hover animation
        label.setOnMouseEntered(e -> {
            label.setScaleX(1.03);
            label.setScaleY(1.03);
        });

        label.setOnMouseExited(e -> {
            label.setScaleX(1.0);
            label.setScaleY(1.0);
        });

        return label;
    }

    private void setActive(Label active) {
        for (Label item : navItems) {
            item.getStyleClass().remove("nav-item-active");
        }
        active.getStyleClass().add("nav-item-active");
    }

    public void selectDefault() {
        if (!navItems.isEmpty()) {
            Label first = navItems.get(0);
            setActive(first);
            onNavSelected.accept(first.getText());
        }
    }
}