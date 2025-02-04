package com.raifernando.util;

import com.google.gson.JsonObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class Credentials {
    public static String spotifyClientId;
    public static String spotifyClientSecret;
    public static String spotifyAccessToken;

    public static String lastfmApiKey;

    public static void loadKeys() throws IOException, InterruptedException, NullPointerException {
        spotifyClientId = PropertiesFile.getFromFile("CLIENT_ID");
        spotifyClientSecret = PropertiesFile.getFromFile("CLIENT_SECRET");
        spotifyAccessToken = PropertiesFile.getFromFile("ACCESS_TOKEN");
        lastfmApiKey = PropertiesFile.getFromFile("API_KEY");

        if (spotifyClientId == null || spotifyClientSecret == null || lastfmApiKey == null)
            throw new NullPointerException("Credentials in the " + PropertiesFile.getFileName() + " file are missing.");

        if (spotifyAccessToken == null || isTokenExpired("ACCESS_TOKEN_TIME", 60))
            getAccessToken();
    }

    private static void saveAccessToken(String token) throws IOException {
        PropertiesFile.storeInFile("ACCESS_TOKEN", token);
        PropertiesFile.storeInFile("ACCESS_TOKEN_TIME", LocalDateTime.now().toString());
    }

    public static void getAccessToken() throws IOException, InterruptedException {
        System.out.println("Requested new access token.");

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "SpotifyClientId", spotifyClientId,
                "spotifyClientSecret", spotifyClientSecret
        );

        JsonObject jsonObject = Request.requestPost(
                "https://accounts.spotify.com/api/token",
                body,
                new String[] {"Content-Type", "application/x-www-form-urlencoded"},
                JsonObject.class);

        spotifyAccessToken = jsonObject.get("spotifyAccessToken").getAsString();
        saveAccessToken(spotifyAccessToken);
    }

    /**
     * Checks if the token has expired based on the stored time.
     * @param keyOfStoredTime - key for retrieving the stored time from the Properties file
     * @param minutes - the number of minutes the token is valid for
     * @return true if the token is expired, false otherwise
     */
    public static boolean isTokenExpired(String keyOfStoredTime, int minutes) {
        try {
            String storedTime = PropertiesFile.getFromFile(keyOfStoredTime);
            if (storedTime != null) {
                LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(minutes);
                return LocalDateTime.parse(storedTime).isBefore(expirationTime);

            }
        } catch (DateTimeParseException e) {
            // If the date is invalid, treat it as expired
        }
        return false;
    }

    @Override
    public String toString() {
        return "SpotifyClientId:" + spotifyClientId + "\nspotifyClientSecret:" + spotifyClientSecret + "\nspotifyAccessToken:" + spotifyAccessToken;
    }
}
