package com.wirabox.infrastructure.api;

import java.util.Map;

public class ApiResponse {

    private final int statusCode;
    private final long responseTime;
    private final Map<String, String> headers;
    private final String body;

    public ApiResponse(int statusCode, long responseTime, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}