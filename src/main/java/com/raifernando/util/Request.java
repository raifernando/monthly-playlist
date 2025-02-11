package com.raifernando.util;

import com.google.gson.Gson;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Request {
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Sends a request and deserialize the response in a new instance of {@code tClass}.
     * @param httpRequest the HTTP Request already built
     * @param tClass the class for the deserialization
     * @return a new instance of {@code tClass} with the deserialized data, or {@code null} if the request fails
     */
    @Nullable
    private static <T> T send(HttpRequest httpRequest, Class<T> tClass) {
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.out.println("------ Invalid API response.");
                return null;
            }

            return gson.fromJson(response.body(), tClass);
        } catch (InterruptedException | IOException e) {
            System.out.println("------ API send request failed.");
            return null;
        }
    }

    /**
     * Sends a GET request.
     * @param url the URL to send the request
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return returns a new instance of {@code tClass} class with the received data, or {@code null} if the request fails
     */
    @Nullable
    public static <T> T get(String url, String [] headers, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .GET()
                .build();

        return send(httpRequest, tClass);
    }

    /**
     * Sends a GET request with no header.
     * @param url the URL to send the request
     * @param tClass the class type used to deserialize the JSON response
     * @return returns a new instance of {@code tClass} class with the received data, or {@code null} if the request fails
     */
    @Nullable
    public static <T> T getNoHeader(String url, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return send(httpRequest, tClass);
    }

    /**
     * Sends a POST request.
     * @param url the URL to send the request
     * @param body the body data in the {@link String} type
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return a new instance of {@code tClass} class with the received data, or {@code null} if the request fails
     */
    @Nullable
    public static <T> T post(String url, String body, String [] headers, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url))
                .headers(headers)
                .build();

        return send(httpRequest, tClass);
    }

    /**
     * Sends a POST request.
     * @param url the URL to send the request
     * @param body the body data in the {@link Map} of strings type
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return a new instance of {@code tClass} class with the received data, or {@code null} if the request fails
     */
    @Nullable
    public static <T> T post(String url, Map<String, String> body, String [] headers, Class<T> tClass) {
        String bodyQuery = QueryGenerator.generateQueryString(body);

        return post(url, bodyQuery, headers, tClass);
    }
}
