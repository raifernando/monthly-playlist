package com.raifernando.spotify;

import com.google.gson.JsonObject;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.Request;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class Credentials {
    public static String client_id;
    public static String client_secret;
    public static String access_token;
    private static LocalDateTime accessTokenTime;

    public static void loadKeys() throws IOException, InterruptedException {
        client_id = PropertiesFile.getFromFile("CLIENT_ID");
        client_secret = PropertiesFile.getFromFile("CLIENT_SECRET");
        access_token = PropertiesFile.getFromFile("ACCESS_TOKEN");

        try {
            String storedTime = PropertiesFile.getFromFile("ACCESS_TOKEN_TIME");
            if (storedTime != null) {
                accessTokenTime = LocalDateTime.parse(storedTime);
                LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
                if (anHourAgo.isAfter(accessTokenTime)) {
                    System.out.println("Access token expired. Generating new one.");
                    accessToken();
                }
            }
            else {
                accessTokenTime = LocalDateTime.now();
            }
        } catch (DateTimeParseException e) {
            accessToken();
        }
    }

    private static void saveAccessToken(String token) throws IOException {
        PropertiesFile.storeInFile("ACCESS_TOKEN", token);
        PropertiesFile.storeInFile("ACCESS_TOKEN_TIME", accessTokenTime.toString());
    }

    public static void accessToken() throws IOException, InterruptedException {
        System.out.println("Requested new access token.");

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "client_id", client_id,
                "client_secret", client_secret
        );

        JsonObject jsonObject = Request.requestPost(
                "https://accounts.spotify.com/api/token",
                body,
                new String[] {"Content-Type", "application/x-www-form-urlencoded"},
                JsonObject.class);

        access_token = jsonObject.get("access_token").getAsString();
        accessTokenTime = LocalDateTime.now();
        saveAccessToken(access_token);
    }

    @Override
    public String toString() {
        return "client_id:" + client_id + "\nclient_secret:" + client_secret + "\naccess_token:" + access_token;
    }
}
