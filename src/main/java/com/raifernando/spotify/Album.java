package com.raifernando.spotify;

import com.google.gson.Gson;
import com.raifernando.util.Credentials;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Album {
    private String id;
    private String album_type;
    private int total_tracks;
    private String name;
    private String release_date;
    private int popularity;
    public ArrayList<Artist> artists;

    public static Album getAlbum(String albumId) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/albums/" + albumId))
                .header("Authorization", "Bearer " + Credentials.spotifyAccessToken)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode == 401) {
            System.out.println("Token expired. Requesting new one.");
            Credentials.getAccessToken();
            return getAlbum(albumId);
        }

        Gson gson = new Gson();
        return gson.fromJson(response.body(), Album.class);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", album_type='" + album_type + '\'' +
                ", total_tracks=" + total_tracks +
                ", name='" + name + '\'' +
                ", release_date='" + release_date + '\'' +
                ", popularity=" + popularity +
                ", artists=" + artists +
                '}';
    }
}
