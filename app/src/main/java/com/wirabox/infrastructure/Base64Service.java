package com.wirabox.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Service {

    // Encode text to Base64
    public static String encode(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        return Base64.getEncoder()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    // Decode Base64 to text
    public static String decode(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 input");
        }
    }
}