package com.raifernando.util;

import com.google.gson.JsonObject;
import com.raifernando.spotify.OAuth;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class Credentials {
    public static String spotifyClientId;
    public static String spotifyClientSecret;
    public static String spotifyAccessToken;

    public static String lastfmApiKey;

    private static final PropertiesFile propertiesFile = new PropertiesFile();

    public static void loadKeys() throws Exception {
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

    private static void saveAccessToken() {
        propertiesFile.store(Map.of(
                "ACCESS_TOKEN", spotifyAccessToken,
                "ACCESS_TOKEN_TIME", LocalDateTime.now().toString()
        ));
    }

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
