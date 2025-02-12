package com.raifernando.spotify;

import com.google.gson.JsonObject;
import com.raifernando.localserver.AuthCodeReceiver;
import com.raifernando.util.Credentials;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.QueryGenerator;
import com.raifernando.util.Request;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *     The {@link OAuth} class handles the process of the
 *     <a href="https://developer.spotify.com/documentation/web-api/tutorials/code-flow">
 *         Spotify OAuth Authorization Code Flow
 *     </a>, which is used to manage Spotify resources on behalf of the user
 *     and stores the API keys for future requests.
 * </p>
 * <p>
 *     The process begins with a local server managed by {@link AuthCodeReceiver},
 *     where the user is prompted to authenticate using their Spotify account.
 *     This grants the application permission to access the resources specified in
 *     the {@code scope} in {@link #spotifyLoginUrl()}.
 * </p>
 * <p>
 *     Once the permission is granted, the local server receives the authorization code.
 *     This code is then used to request an access token, which is required to make requests
 *     in the Spotify API to manage the resources mentioned in the {@code scope}.
 * </p>
 * <p>
 *     The access token is valid for one hour, after which it must be refreshed.
 *     This class handles this automatically by storing the expiration date in
 *     the {@link PropertiesFile}.
 * </p>
 * <p>
 *     This class provides the following fields:
 *     <ul>
 *         <li>{@link #authCode}: the authorization code received from Spotify after user authentication.</li>
 *         <li>{@link #latch}: a {@link CountDownLatch} used to wait for the authorization code to be received.</li>
 *         <li>{@link #accessToken}: the access token used to make authenticated requests to the Spotify API.</li>
 *     </ul>
 * </p>
 */
public class OAuth {
    public static String authCode;

    public static CountDownLatch latch = new CountDownLatch(1);

    public static String accessToken;
    private static String refreshToken;

    // Single instance for getting and storing credentials.
    private static final PropertiesFile propertiesFile = new PropertiesFile();

    /**
     * <p>Retrieves the access token from the {@link PropertiesFile}.</p>
     * <p>
     *     If no token is stored, request a new one using the authorization code
     *     received from the user's authentication.
     * </p>
     * <p>If the token is expired, a new access token is requested using the refresh token.</p>
     */
    public static void getAccessCode() {
        accessToken = propertiesFile.get("USER_ACCESS_CODE");

        if (Credentials.isTokenExpired("USER_ACCESS_CODE_TIME", 60))
            refreshAccessToken();

        if (accessToken == null)
            requestAccessToken();
    }

    /**
     * Requests a new access token for the current user, using the authorization code.
     * The response also includes a refresh token, which is used in {@link #refreshAccessToken()}.
     */
    private static void requestAccessToken() {
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

        JsonObject response = Request.post(
                "https://accounts.spotify.com/api/token",
                body,
                headers,
                JsonObject.class
        );

        if (response == null || !response.has("access_token") || !response.has("refresh_token")) {
            System.out.println("Error getting the user's access token");
            return;
        }

        accessToken = response.get("access_token").getAsString();
        refreshToken = response.get("refresh_token").getAsString();
        saveUserAccessCode();
    }

    /**
     * Using the {@link AuthCodeReceiver}, creates a local server used to retrieve
     * the authorization code from the user's authentication process.
     * <br>
     * A latch is used to halt the application until the code is received.
     */
    private static void requestAuthorizationCode()  {
        try {
            AuthCodeReceiver.startServer();
            System.out.println("Authenticate in: " + spotifyLoginUrl());

            // Wait until the authorization code is retrieved
            latch.await();
        } catch (InterruptedException | ServerException e) {
            throw new RuntimeException(e);
        }

        AuthCodeReceiver.stopServer();
        saveAuthorizationCode();
    }

    /**
     * Sends a request to refresh the expired access token.
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

        JsonObject response = Request.post(
                "https://accounts.spotify.com/api/token",
                body,
                headers,
                JsonObject.class
        );

        if (response == null || !response.has("access_token")) {
            System.out.println("Error refreshing access token");
            return;
        }

        accessToken = response.get("access_token").getAsString();
        saveUserAccessCode();
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
     * Generates the URL for the user's authentication.
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
