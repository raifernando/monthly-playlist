package com.raifernando.spotify;

import com.google.gson.Gson;
import com.raifernando.util.Credentials;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpotifyArtist {
    private String id;
    private String name;
    private int popularity;

    public void setArtistId(String id) {
        this.id = id;
    }

    public String getArtistId() {
        return id;
    }

    public static SpotifyArtist getArtist(String id) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/artists/" + id))
                .header("Authorization", "Bearer " + Credentials.spotifyAccessToken)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        System.out.println(response.statusCode());

        if (statusCode == 200) {
            System.out.println(response.body());
            Gson gson = new Gson();
            SpotifyArtist artist = gson.fromJson(response.body(), SpotifyArtist.class);
            System.out.println(artist.toString());
            return artist;
        }
        else if (statusCode == 401) {
            System.out.println("Token expired. Requesting new one.");
            Credentials.getAccessToken();
            return getArtist(id);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
