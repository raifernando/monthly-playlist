package com.raifernando.spotify;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Request {
    public static <T> T requestGet(String url, Class<T> tClass) throws IOException, InterruptedException {
        return requestGet(url, Credentials.access_token, tClass);
    }

    public static <T> T requestGet(String url, String accessToken, Class<T> tClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode == 401) {
            System.out.println("Access Token invalid. Requesting new one.");
            System.out.println(response.body());
            Credentials.accessToken();
            return requestGet(url, tClass);
        }

        System.out.println(response.body());
        Gson gson = new Gson();
        return gson.fromJson(response.body(), tClass);
    }

    public static <T> T requestPost(String url, String headerName, String headerValue, String httpBody, Class<T> tClass)
            throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(headerName, headerValue)
                .POST(HttpRequest.BodyPublishers.ofString(httpBody))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), tClass);
    }

    public static <T> T requestPost(HttpRequest httpRequest, Class<T> tClass) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("response:" + response.body());

        Gson gson = new Gson();
        return gson.fromJson(response.body(), tClass);
    }
}
