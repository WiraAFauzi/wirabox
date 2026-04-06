package com.wirabox.presentation.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationUtil {

    public static void playSuccessGlow(Node node) {

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#4F8CFF"));
        glow.setRadius(0);
        node.setEffect(glow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 0)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(glow.radiusProperty(), 25)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(glow.radiusProperty(), 0)
                )
        );

        timeline.play();
    }

    public static void playErrorPulse(Node node) {

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 1),
                        new KeyValue(node.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(node.scaleXProperty(), 1.05),
                        new KeyValue(node.scaleYProperty(), 1.05)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(node.scaleXProperty(), 1),
                        new KeyValue(node.scaleYProperty(), 1)
                )
        );

        pulse.setCycleCount(2);
        pulse.play();
    }
}