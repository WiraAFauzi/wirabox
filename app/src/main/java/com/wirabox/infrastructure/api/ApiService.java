package com.wirabox.infrastructure.api;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiService {

    private final OkHttpClient client;

    public ApiService() {
        this.client = new OkHttpClient();
    }

    public ApiResponse sendGet(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return executeRequest(request);
    }

    public ApiResponse sendPost(String url, String jsonBody) throws IOException {

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return executeRequest(request);
    }

    public ApiResponse sendPut(String url, String jsonBody) throws IOException {

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        return executeRequest(request);
    }

    public ApiResponse sendDelete(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        return executeRequest(request);
    }

    private ApiResponse executeRequest(Request request) throws IOException {

        long start = System.currentTimeMillis();

        try (Response response = client.newCall(request).execute()) {

            long end = System.currentTimeMillis();
            long responseTime = end - start;

            Map<String, String> headers = new HashMap<>();
            response.headers().forEach(h ->
                    headers.put(h.getFirst(), h.getSecond())
            );

            String body = "";
            if (response.body() != null) {
                body = response.body().string();
            }

            return new ApiResponse(
                    response.code(),
                    responseTime,
                    headers,
                    body
            );
        }
    }
}