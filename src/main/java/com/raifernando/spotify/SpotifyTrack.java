package com.raifernando.spotify;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.raifernando.util.Credentials;
import com.raifernando.util.QueryGenerator;
import com.raifernando.util.Request;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SpotifyTrack {
    private String id;
    private String name;
    private String uri;

    /**
     * Searches for one track using its name, artist, and album.
     * If no result is found with those parameters,
     * a new search is made only with its track and artist name.
     * @param trackName the name of the track to search for
     * @param artistName the name of the artist
     * @param albumName the name of the album
     * @return a {@link SpotifyTrack} or {@code null} if the track wasn't found
     */
    @Nullable
    public static SpotifyTrack searchForTrack(String trackName, String artistName, String albumName) {
        // Searches with all three parameters
        SpotifyTrack track = searchForTrack(getTrackSearchUrl(trackName, artistName, albumName));

        // No result found
        if (track == null) {
            // Searches with track and artist name only
            track = searchForTrack(getTrackSearchUrl(trackName, artistName, null));

            // If no result found, prints an error message and returns null
            if (track == null)
                System.out.printf("Track %s not found.\n", trackName);
        }

        return track;
    }

    /**
     * Searches for one track using the API URL.
     * @param url the URL to send the request
     * @return a {@link SpotifyTrack} or {@code null} if the track search fails
     */
    @Nullable
    private static SpotifyTrack searchForTrack(String url) {
        Gson gson = new Gson();

        try {
            JsonObject response = Request.get(
                    url,
                    new String[] {"Authorization", "Bearer " + Credentials.spotifyAccessToken},
                    JsonObject.class
            );

            if (response == null || !response.has("tracks"))
                return null;

            JsonObject jsonObject = response.getAsJsonObject("tracks");
            JsonArray jsonArray = jsonObject.getAsJsonArray("items");

            if (jsonArray == null || jsonArray.isEmpty())
                return null;

            // Get the first track from the response
            return gson.fromJson(jsonArray.get(0).getAsJsonObject(), SpotifyTrack.class);
        } catch (Exception _) {
            // Error searching for the track.
            return null;
        }
    }

    /**
     * Generates the URL for requesting one track.
     * The search parameters are its name, artist, and with the {@code albumName} if provided.
     * @param trackName the name of the track to search for
     * @param artistName the name of the artist
     * @param albumName the name of the album
     * @return the URL of the API request for the track
     */
    private static String getTrackSearchUrl(String trackName, String artistName, @Nullable String albumName) {
        String url = "https://api.spotify.com/v1/search?q=";

        Map<String, String> map = Map.of(
                "type", "track",
                "limit", "1"
        );

        String trackInfo = trackName + " artist:"+ artistName;
        if (albumName != null)
            trackInfo += " album:" + albumName;

        return url + URLEncoder.encode(trackInfo, StandardCharsets.UTF_8) +
                "&" + QueryGenerator.generateQueryString(map);
    }

    public String getUri() {
        return uri;
    }
}
