package com.raifernando.spotify;

import com.google.gson.JsonObject;
import com.raifernando.localserver.AuthCodeReceiver;
import com.raifernando.util.Credentials;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.QueryGenerator;
import com.raifernando.util.Request;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class OAuth {
    public static String authCode;
    public static CountDownLatch latch = new CountDownLatch(1);

    public static String accessToken;
    private static String refreshToken;

    // Single instance for getting and storing credentials.
    private static final PropertiesFile propertiesFile = new PropertiesFile();

    /**
     * Get the access code for the authenticated user. The code is stored in this class field.
     * @throws Exception
     */
    public static void getAccessCode() throws Exception {
        accessToken = propertiesFile.get("USER_ACCESS_CODE");

        if (Credentials.isTokenExpired("USER_ACCESS_CODE_TIME", 60))
            refreshAccessToken();

        if (accessToken == null)
            requestAccessToken();
    }

    /**
     * Request a new access token for the current user, using the authorization code.
     * @throws Exception
     */
    private static void requestAccessToken() throws Exception {
        requestAuthorizationCode();

        Map<String, String> body = Map.of(
                "grant_type", "authorization_code",
                "code", authCode,
                "redirect_uri", "http://localhost:8080/callback"
        );

        String [] headers = {
                "content-type", "application/x-www-form-urlencoded",
                "Authorization", "Basic " + Base64.getEncoder().encodeToString((Credentials.spotifyClientId + ":" + Credentials.spotifyClientSecret).getBytes())
        };

        JsonObject json = Request.requestPost(
                "https://accounts.spotify.com/api/token",
                body,
                headers,
                JsonObject.class
        );

        try {
            accessToken = json.get("access_token").getAsString();
            refreshToken = json.get("refresh_token").getAsString();
            saveUserAccessCode();
        } catch (NullPointerException e) {
            System.out.println("Error getting the user's access token");
        }
    }

    /**
     * Create a local server to authenticate the user and get the authorization code.
     * @throws Exception
     */
    private static void requestAuthorizationCode() throws Exception {
        AuthCodeReceiver.startServer();

        System.out.println(spotifyLoginUrl());

        latch.await();

        AuthCodeReceiver.stopServer();

        saveAuthorizationCode();
    }

    /**
     * Send a request to refresh the stored access token.
     */
    private static void refreshAccessToken() {
        System.out.println("Refreshing access token.");
        refreshToken = propertiesFile.get("REFRESH_TOKEN");

        Map<String, String> body = Map.of(
                "grant_type", "refresh_token",
                "refresh_token", refreshToken
        );

        String [] headers = {
                "content-type", "application/x-www-form-urlencoded",
                "Authorization", "Basic " + Base64.getEncoder().encodeToString((Credentials.spotifyClientId + ":" + Credentials.spotifyClientSecret).getBytes())
        };

        JsonObject json = Request.requestPost(
                "https://accounts.spotify.com/api/token",
                body,
                headers,
                JsonObject.class
        );

        try {
            accessToken = json.get("access_token").getAsString();
            saveUserAccessCode();
        } catch (NullPointerException e) {
            System.out.println("Error refreshing access token");
        }
    }

    private static void saveUserAccessCode() {
        propertiesFile.store(Map.of(
                "USER_ACCESS_CODE", accessToken,
                "USER_ACCESS_CODE_TIME", LocalDateTime.now().toString(),
                "REFRESH_TOKEN", refreshToken
        ));
    }

    private static void saveAuthorizationCode() {
        propertiesFile.store(Map.of(
                "AUTHORIZATION_CODE", authCode,
                "AUTHORIZATION_CODE_TIME", LocalDateTime.now().toString()
        ));
    }

    /**
     * @return - the URL for authentication
     */
    private static String spotifyLoginUrl() {
        String state = "87hasfoJHhsajfHJG2";

        String scope = "playlist-modify-public playlist-modify-private user-read-private user-read-email";

        Map<String, String> query = Map.of(
                "redirect_uri", "http://localhost:8080/callback",
                "state", state,
                "response_type", "code",
                "scope", scope,
                "client_id", Credentials.spotifyClientId
        );

        return "https://accounts.spotify.com/authorize?" + QueryGenerator.generateQueryString(query);
    }
}
