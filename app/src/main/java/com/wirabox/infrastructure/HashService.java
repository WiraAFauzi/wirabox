package com.wirabox.infrastructure;

import java.security.MessageDigest;

public class HashService {

    public static String hash(String input, String algorithm) {

        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error generating hash");
        }
    }
}