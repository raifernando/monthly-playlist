package com.raifernando.spotify;

import com.google.gson.JsonObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Properties;

public class Credentials {
    public static String client_id;
    public static String client_secret;
    public static String access_token;
    private static LocalDateTime accessTokenTime;

    public static void loadKeys() throws IOException, InterruptedException {
        Properties properties = new Properties();
        FileInputStream file = new FileInputStream("config.properties");
        properties.load(file);

        client_id = properties.getProperty("CLIENT_ID");
        client_secret = properties.getProperty("CLIENT_SECRET");
        access_token = properties.getProperty("ACCESS_TOKEN");
        try {
            accessTokenTime = LocalDateTime.parse(properties.getProperty("ACCESS_TOKEN_TIME"));
            LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
            if (anHourAgo.isAfter(accessTokenTime)) {
                System.out.println("Access token expired. Generating new one.");
                accessToken();
            }
        } catch (DateTimeParseException e) {
            accessToken();
        }
    }

    private static void saveAccessToken(String token) throws IOException {
        Properties properties = new Properties();
        FileInputStream file = new FileInputStream("config.properties");
        properties.load(file);

        properties.setProperty("ACCESS_TOKEN", token);
        access_token = properties.getProperty("ACCESS_TOKEN");

        accessTokenTime = LocalDateTime.now();
        properties.setProperty("ACCESS_TOKEN_TIME", accessTokenTime.toString());

        OutputStream output = new FileOutputStream("config.properties");
        properties.store(output, null);
    }

    public static void accessToken() throws IOException, InterruptedException {
        System.out.println("Requested new access_token.");

        String url = "https://accounts.spotify.com/api/token";
        String headerName = "Content-Type";
        String headerValue = "application/x-www-form-urlencoded";
        String httpBody = "grant_type=client_credentials&client_id=" + client_id + "&client_secret=" + client_secret;

        JsonObject jsonObject = Request.requestPost(url, headerName, headerValue, httpBody, JsonObject.class);
        saveAccessToken(jsonObject.get("access_token").getAsString());
    }

    @Override
    public String toString() {
        return "client_id:" + client_id + "\nclient_secret:" + client_secret + "\naccess_token:" + access_token;
    }
}
