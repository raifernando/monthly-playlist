package com.raifernando.spotify;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Artist {
    private String id;
    private String name;
    private int popularity;

    public void setArtistId(String id) {
        this.id = id;
    }

    public String getArtistId() {
        return id;
    }

    public static Artist getArtist(String id) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/artists/" + id))
                .header("Authorization", "Bearer " + Credentials.access_token)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        System.out.println(response.statusCode());

        if (statusCode == 200) {
            System.out.println(response.body());
            Gson gson = new Gson();
            Artist artist = gson.fromJson(response.body(), Artist.class);
            System.out.println(artist.toString());
            return artist;
        }
        else if (statusCode == 401) {
            System.out.println("Token expired. Requesting new one.");
            Credentials.accessToken();
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
