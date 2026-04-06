package com.wirabox.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonService {

    private final ObjectMapper mapper;

    public JsonService() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Format JSON into pretty string
     */
    public String format(String json) throws Exception {
        Object obj = mapper.readValue(json, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * Validate JSON (returns true if valid)
     */
    public boolean isValid(String json) {
        try {
            mapper.readValue(json, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}