package com.raifernando.util;

import com.google.gson.JsonObject;
import com.raifernando.spotify.OAuth;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * <p>
 *     The {@link Credentials} class manages the API credentials required to make requests
 *     to Spotify and Lastfm.
 *     These credentials are retrieved from the {@link PropertiesFile}.
 * </p>
 * <p>
 *     For this application, the Last.fm requests only require the {@link #lastfmApiKey}, while
 *     Spotify requests require the {@link #spotifyAccessToken} which is obtained
 *     by sending a request to Spotify using the {@link #spotifyClientId} and {@link #spotifyClientSecret}.
 * </p>
 * <p>
 *     These credentials are essential for the correct use of the application.
 *     If any credentials are missing, the program won't work as intended to.
 * </p>
 */
public class Credentials {
    public static String spotifyClientId;
    public static String spotifyClientSecret;
    public static String spotifyAccessToken;

    public static String lastfmApiKey;

    // Single instance for getting and storing credentials.
    private static final PropertiesFile propertiesFile = new PropertiesFile();

    /**
     * Loads credentials from the {@link PropertiesFile} and stores in the class fields.
     * If no user is authenticated, a new authentication process starts with {@link OAuth}.
     * @throws NullPointerException if any of the credentials are null
     */
    public static void loadKeys() throws NullPointerException {
        spotifyClientId = propertiesFile.get("CLIENT_ID");
        spotifyClientSecret = propertiesFile.get("CLIENT_SECRET");
        spotifyAccessToken = propertiesFile.get("ACCESS_TOKEN");
        lastfmApiKey = propertiesFile.get("API_KEY");

        if (spotifyClientId == null || spotifyClientSecret == null || lastfmApiKey == null)
            throw new NullPointerException("Credentials in the " + propertiesFile.getFileName() + " file are missing.");

        if (spotifyAccessToken == null || isTokenExpired("ACCESS_TOKEN_TIME", 60))
            getAccessToken();

        OAuth.getAccessCode();
    }

    /**
     * Updates the fields related to the access token in the {@link PropertiesFile}.
     */
    private static void saveAccessToken() {
        propertiesFile.store(Map.of(
                "ACCESS_TOKEN", spotifyAccessToken,
                "ACCESS_TOKEN_TIME", LocalDateTime.now().toString()
        ));
    }

    /**
     * Requests a new access token.
     */
    public static void getAccessToken() {
        System.out.println("Requested new access token.");

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "client_id", spotifyClientId,
                "client_secret", spotifyClientSecret
        );

        JsonObject response = Request.post(
                "https://accounts.spotify.com/api/token",
                body,
                new String[] {"Content-Type", "application/x-www-form-urlencoded"},
                JsonObject.class);

        if (response == null) {
            System.out.println("Failed to get new access token.");
            return;
        }

        spotifyAccessToken = response.get("access_token").getAsString();
        saveAccessToken();
    }

    /**
     * Checks if the token has expired based on the stored time.
     * @param keyOfStoredTime - key for retrieving the stored time from the Properties file
     * @param minutes - the number of minutes the token is valid for
     * @return true if the token is expired, false otherwise
     */
    public static boolean isTokenExpired(String keyOfStoredTime, int minutes) {
        try {
            String storedTime = propertiesFile.get(keyOfStoredTime);
            if (storedTime != null) {
                LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(minutes);
                return LocalDateTime.parse(storedTime).isBefore(expirationTime);

            }
        } catch (DateTimeParseException e) {
            // If the date is invalid, treat it as expired
        }
        return false;
    }
}
