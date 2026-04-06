package com.wirabox.presentation.components;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingSpinner extends StackPane {

    public LoadingSpinner() {
        ProgressIndicator indicator = new ProgressIndicator();
        getChildren().add(indicator);
    }
}