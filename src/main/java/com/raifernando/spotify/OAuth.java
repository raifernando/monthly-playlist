package com.raifernando.spotify;

import com.google.gson.JsonObject;
import com.raifernando.localserver.AuthCodeReceiver;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.QueryGenerator;

import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class OAuth {
    public static String authCode;
    public static CountDownLatch latch = new CountDownLatch(1);
    public static LocalDateTime authCodeTime;

    public static String accessToken;
    public static LocalDateTime accessTokenTime;

    public static void getAccessCode() throws Exception {
        // TODO: create custom exceptions for different errors while requesting
        // TODO: refresh token

//        try {
//            String storedTime = PropertiesFile.getFromFile("USER_ACCESS_CODE_TIME");
//            if (storedTime != null) {
//                accessTokenTime = LocalDateTime.parse(storedTime);
//                LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
//                if (!anHourAgo.isAfter(accessTokenTime)) {
//                    accessToken = PropertiesFile.getFromFile("AUTHORIZATION_CODE");
//                    return;
//                }
//                System.out.println("User's access token expired. Authentication required.");
//            }
//            else {
//                authCodeTime = null;
//                accessTokenTime = LocalDateTime.now();
//            }
//        } catch (DateTimeParseException _) {}

        OAuth.requestAuthorizationCode();

        Map<String, String> body = Map.of(
                "grant_type", "authorization_code",
                "code", authCode,
                "redirect_uri", "http://localhost:8080/callback"
        );
        String bodyQuery = QueryGenerator.generateQueryString(body);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyQuery))
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((Credentials.client_id + ":" + Credentials.client_secret).getBytes()))
                .build();

        JsonObject json = Request.requestPost(httpRequest, JsonObject.class);

        try {
            accessToken = json.get("access_token").getAsString();
            saveUserAccessCode();
        } catch (NullPointerException e) {
            System.out.println("Error getting users' access code");
        }
    }

    public static void requestAuthorizationCode() throws Exception {
//        try {
//            String storedTime = PropertiesFile.getFromFile("AUTHORIZATION_CODE_TIME");
//            if (storedTime != null && authCodeTime != null) {
//                authCodeTime = LocalDateTime.parse(storedTime);
//                LocalDateTime anHourAgo = LocalDateTime.now().minusHours(1);
//                if (!anHourAgo.isAfter(authCodeTime)) {
//                    authCode = PropertiesFile.getFromFile("AUTHORIZATION_CODE");
//                    return;
//                }
//                System.out.println("Authorization code expired.");
//            }
//            else {
//                authCodeTime = LocalDateTime.now();
//            }
//        } catch (DateTimeParseException _) {}

        AuthCodeReceiver.startServer();

        System.out.println(spotifyLoginUrl());

        latch.await();

        AuthCodeReceiver.stopServer();

        saveAuthorizationCode();
    }

    private static void saveUserAccessCode() throws IOException {
        PropertiesFile.storeInFile("USER_ACCESS_CODE", accessToken);
        PropertiesFile.storeInFile("USER_ACCESS_CODE_TIME", accessTokenTime.toString());
    }

    private static void saveAuthorizationCode() throws IOException {
        PropertiesFile.storeInFile("AUTHORIZATION_CODE", authCode);
        PropertiesFile.storeInFile("AUTHORIZATION_CODE_TIME", authCodeTime.toString());
    }

    private static String spotifyLoginUrl() {
        String state = "87hasfoJHhsajfHJG2";

        String scope = "playlist-modify-public playlist-modify-private user-read-private user-read-email";

        Map<String, String> query = Map.of(
                "redirect_uri", "http://localhost:8080/callback",
                "state", state,
                "response_type", "code",
                "scope", scope,
                "client_id", Credentials.client_id
        );

        return "https://accounts.spotify.com/authorize?" + QueryGenerator.generateQueryString(query);
    }
}
