package com.wirabox.presentation.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

public class SmoothScrollUtil {

    public static void smoothScrollTo(ScrollPane scrollPane, double targetValue) {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(scrollPane.vvalueProperty(), targetValue))
        );

        timeline.play();
    }
}