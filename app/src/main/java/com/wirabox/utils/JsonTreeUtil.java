package com.wirabox.utils;

import com.google.gson.*;

import javafx.scene.control.TreeItem;

public class JsonTreeUtil {

    public static TreeItem<String> buildTree(String json) {

        try {

            JsonElement rootElement = JsonParser.parseString(json);

            TreeItem<String> rootItem = new TreeItem<>("JSON");
            buildNode(rootItem, rootElement);

            rootItem.setExpanded(true);
            return rootItem;

        } catch (Exception e) {

            TreeItem<String> error = new TreeItem<>("Invalid JSON");
            return error;
        }
    }

    private static void buildNode(TreeItem<String> parent, JsonElement element) {

        if (element.isJsonObject()) {

            JsonObject obj = element.getAsJsonObject();

            obj.entrySet().forEach(entry -> {

                TreeItem<String> child = new TreeItem<>(entry.getKey());
                parent.getChildren().add(child);

                buildNode(child, entry.getValue());
            });

        }

        else if (element.isJsonArray()) {

            JsonArray array = element.getAsJsonArray();

            for (int i = 0; i < array.size(); i++) {

                TreeItem<String> child = new TreeItem<>("[" + i + "]");
                parent.getChildren().add(child);

                buildNode(child, array.get(i));
            }

        }

        else if (element.isJsonPrimitive()) {

            parent.setValue(parent.getValue() + " : " + element.getAsJsonPrimitive().toString());

        }

        else if (element.isJsonNull()) {

            parent.setValue(parent.getValue() + " : null");

        }
    }
}