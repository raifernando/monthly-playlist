package com.raifernando.spotify;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.raifernando.util.QueryGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Track {
    private String id;
    private Album album;
    private ArrayList<Artist> artists;
    private String name;
    private int popularity;
    private int track_number;
    private String uri;

    public static Track getTrack(String trackId) throws IOException, InterruptedException {
        String requestUrl = "https://api.spotify.com/v1/tracks/" + trackId;

        Track track = Request.requestGet(requestUrl, Track.class);
        System.out.println(track.toString());
        return track;
    }

    public static Track searchForTrack(String trackName, String artistName) throws IOException, InterruptedException {
        String url = "https://api.spotify.com/v1/search?";

        Map<String, String> map = Map.of(
                "q", trackName + " artist:" + artistName,
                "type", "track",
                "limit", "1"
        );
        String query = QueryGenerator.generateQueryString(map);

        Gson gson = new Gson();

        JsonObject jsonObject = Request.requestGet(url + query, JsonObject.class).getAsJsonObject("tracks");
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");

        try {
            return gson.fromJson(jsonArray.get(0).getAsJsonObject(), Track.class);
        } catch (IndexOutOfBoundsException _) {
            System.out.printf("Track %s not found.", trackName);
            return null;
        }
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artists.getFirst().getName();
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", album=" + album +
                ", artists=" + artists +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                ", track_number=" + track_number +
                ", uri=" + uri +
                '}';
    }
}
