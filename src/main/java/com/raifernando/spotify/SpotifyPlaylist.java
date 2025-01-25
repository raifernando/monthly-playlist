package com.raifernando.spotify;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

public class SpotifyPlaylist {
    private String id;
    private String name;
    private SpotifyUser owner;

    public static SpotifyPlaylist createPlaylist(SpotifyUser user) throws IOException, InterruptedException {
        if (OAuth.accessToken == null || user.getId() == null) {
            System.out.println("User not authenticated. Failed to create playlist.");
            return null;
        }

        String url = "https://api.spotify.com/v1/users/" + user.getId() + "/playlists";
        String jsonBody = "{\"name\":\"Playlist name\"}";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(url))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + OAuth.accessToken)
                .build();

        return Request.requestPost(httpRequest, SpotifyPlaylist.class);
    }

    public void addTrack(Track track) throws IOException, InterruptedException {
        System.out.println("Adding " + track.getName() + " - " + track.getArtistName());

        String url = "https://api.spotify.com/v1/playlists/" + id + "/tracks";
        String jsonBody = "{\"uris\": [\"" + track.getUri() + "\"]}";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(url))
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + OAuth.accessToken)
                .build();

        var response = Request.requestPost(httpRequest);
        if (response.statusCode() != 201) {
            System.out.println("------ Error adding track [" + track.getName() + "]");
            System.out.println(response.body());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
