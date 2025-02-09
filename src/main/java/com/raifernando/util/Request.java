package com.raifernando.util;

import com.google.gson.Gson;

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
     * @return a new instance of {@code tClass} with the deserialized data, or null if the request fails
     */
    private static <T> T sendRequest(HttpRequest httpRequest, Class<T> tClass) {
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                System.out.println("Request failed with status code: " + response.statusCode());
                return null;
            }

            return gson.fromJson(response.body(), tClass);
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    /**
     * Sends a GET request.
     * @param url the url to send the request
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return returns a new instance of {@code tClass} class with the received data
     */
    public static <T> T requestGet(String url, String [] headers, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .GET()
                .build();

        return sendRequest(httpRequest, tClass);
    }

    /**
     * Sends a GET request with no header.
     * @param url the url to send the request
     * @param tClass the class type used to deserialize the JSON response
     * @return returns a new instance of {@code tClass} class with the received data
     */
    public static <T> T requestGetNoHeader(String url, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return sendRequest(httpRequest, tClass);
    }

    /**
     * Sends a POST request.
     * @param url the url to send the request
     * @param body the body data in the {@link String} type
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return a new instance of {@code tClass} class with the received data
     */
    public static <T> T requestPost(String url, String body, String [] headers, Class<T> tClass) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url))
                .headers(headers)
                .build();

        return sendRequest(httpRequest, tClass);
    }

    /**
     * Sends a POST request.
     * @param url the url to send the request
     * @param body the body data in the {@link Map} of strings type
     * @param headers an array of Strings as name value pairs representing headers
     * @param tClass the class type used to deserialize the JSON response
     * @return a new instance of {@code tClass} class with the received data
     */
    public static <T> T requestPost(String url, Map<String, String> body, String [] headers, Class<T> tClass) {
        String bodyQuery = QueryGenerator.generateQueryString(body);

        return requestPost(url, bodyQuery, headers, tClass);
    }
}
