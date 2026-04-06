package com.wirabox.presentation;

import com.wirabox.presentation.layout.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        MainLayout layout = new MainLayout();

        Scene scene = new Scene(layout, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/theme.css").toExternalForm()
        );

        // =========================
        // WINDOW SETTINGS
        // =========================
        stage.setTitle("WiraBox");

        // ✅ ADD APP ICON (LOGO)
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/wirabox-logo.png"))
        );

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}