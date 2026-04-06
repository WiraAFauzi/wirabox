package com.wirabox.presentation.layout;

import com.wirabox.presentation.components.SidebarView;
import com.wirabox.presentation.components.TopBarView;
import com.wirabox.presentation.tools.ApiTesterView;
import com.wirabox.presentation.tools.JsonFormatterView;
import com.wirabox.presentation.tools.Base64View;
import com.wirabox.presentation.tools.HashView;
import com.wirabox.presentation.tools.LogViewerView;
import com.wirabox.presentation.tools.DatabaseView; // 🔥 NEW

import com.wirabox.presentation.utils.AnimationUtil;
import com.wirabox.presentation.utils.SmoothScrollUtil;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainLayout extends BorderPane {

    private final TopBarView topBar;
    private final SidebarView sidebar;
    private final StackPane contentArea;

    public MainLayout() {

        topBar = new TopBarView();
        sidebar = new SidebarView(this::onNavSelected);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        contentArea.setPadding(new Insets(30));

        setTop(topBar);
        setLeft(sidebar);
        setCenter(contentArea);

        sidebar.selectDefault();
    }

    private void onNavSelected(String toolName) {

        topBar.setTitle(toolName);

        // ===============================
        // API TESTER
        // ===============================
        if ("API Tester".equals(toolName)) {
            loadWithAnimation(new ApiTesterView());
            return;
        }

        // ===============================
        // JSON FORMATTER
        // ===============================
        if ("JSON Formatter".equals(toolName)) {
            loadWithAnimation(new JsonFormatterView());
            return;
        }

        // ===============================
        // BASE64 TOOL
        // ===============================
        if ("Base64 Tool".equals(toolName)) {
            loadWithAnimation(new Base64View());
            return;
        }

        // ===============================
        // HASH GENERATOR
        // ===============================
        if ("Hash Generator".equals(toolName)) {
            loadWithAnimation(new HashView());
            return;
        }

        // ===============================
        // LOG VIEWER
        // ===============================
        if ("Log Viewer".equals(toolName)) {
            loadWithAnimation(new LogViewerView());
            return;
        }

        // ===============================
        // 🔥 DATABASE VIEWER (PHASE 5)
        // ===============================
        if ("Database Viewer".equals(toolName)) {
            loadWithAnimation(new DatabaseView());
            return;
        }

        // ===============================
        // Placeholder (fallback)
        // ===============================
        VBox contentWrapper = new VBox(15);
        contentWrapper.setPadding(new Insets(10));

        for (int i = 1; i <= 30; i++) {
            Label item = new Label(toolName + " Item " + i);
            item.getStyleClass().add("content-placeholder");
            contentWrapper.getChildren().add(item);
        }

        ScrollPane scrollPane = new ScrollPane(contentWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        loadWithAnimation(scrollPane);
    }

    private void loadWithAnimation(javafx.scene.Node node) {

        node.setOpacity(0);
        node.setTranslateX(40);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);

        FadeTransition fade = new FadeTransition(Duration.millis(250), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(250), node);
        slide.setFromX(40);
        slide.setToX(0);

        ParallelTransition transition = new ParallelTransition(fade, slide);

        transition.setOnFinished(e -> {
            AnimationUtil.playSuccessGlow(node);

            if (node instanceof ScrollPane scrollPane) {
                SmoothScrollUtil.smoothScrollTo(scrollPane, 0.0);
            }
        });

        transition.play();
    }
}