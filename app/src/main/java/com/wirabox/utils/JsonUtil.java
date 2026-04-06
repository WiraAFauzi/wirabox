package com.wirabox.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String prettyPrint(String json) {

        try {

            JsonElement element = JsonParser.parseString(json);
            return gson.toJson(element);

        } catch (Exception e) {

            return json; // If not valid JSON, just return raw text
        }
    }
}