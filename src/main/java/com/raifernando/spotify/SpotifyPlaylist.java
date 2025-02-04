package com.raifernando.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.util.Request;

import java.io.IOException;
import java.util.ArrayList;

public class SpotifyPlaylist {
    private String id;
    private String name;
    private SpotifyUser owner;

    public SpotifyPlaylist(String id) {
        this.id = id;
    }

    public static SpotifyPlaylist createPlaylist(SpotifyUser user, String name) throws IOException, InterruptedException {
        if (OAuth.accessToken == null || user.getId() == null) {
            System.out.println("User not authenticated. Failed to create playlist.");
            return null;
        }

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("name", name);

        String [] headers = {
                "content-type", "application/json",
                "Authorization", "Bearer " + OAuth.accessToken
        };

        return Request.requestPost(
                "https://api.spotify.com/v1/users/" + user.getId() + "/playlists",
                jsonBody.toString(),
                headers,
                SpotifyPlaylist.class
        );
    }

    public static SpotifyPlaylist createPlaylist(SpotifyUser user) throws IOException, InterruptedException {
        return createPlaylist(user, "monthly-playlist");
    }

    public void addTrack(SpotifyTrack track) throws IOException, InterruptedException {
        if (track == null)
            return;

        System.out.println("Adding " + track.getName() + " - " + track.getArtistName());

        JsonObject jsonBody = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(track.getUri());
        jsonBody.add("uris", jsonArray);

        String [] headers = {
                "content-type", "application/json",
                "Authorization", "Bearer " + OAuth.accessToken
        };

        SpotifyPlaylist response = Request.requestPost(
                "https://api.spotify.com/v1/playlists/" + id + "/tracks",
                jsonBody.toString(),
                headers,
                SpotifyPlaylist.class
        );

        if (response == null) {
            System.out.println("------ Error adding track [" + track.getName() + "]");
        }
    }

    public void addMultipleTracks(SpotifyPlaylist playlist, ArrayList<LastfmTrack> tracks) throws IOException, InterruptedException {
        System.out.println("Adding tracks to playlist");

        JsonArray jsonArray = new JsonArray();

        for (LastfmTrack track : tracks) {
            SpotifyTrack spotifyTrack = SpotifyTrack.searchForTrack(track.getName(), track.getArtist().getName(), track.getAlbum().getName());
            jsonArray.add(spotifyTrack.getUri());
        }

        JsonObject jsonBody = new JsonObject();
        jsonBody.add("uris", jsonArray);

        String [] headers = {
                "content-type", "application/json",
                "Authorization", "Bearer " + OAuth.accessToken
        };

        SpotifyPlaylist response = Request.requestPost(
                "https://api.spotify.com/v1/playlists/" + id + "/tracks",
                jsonBody.toString(),
                headers,
                SpotifyPlaylist.class
        );

        if (response == null) {
            System.out.println("------ Error adding tracks");
        }

        System.out.printf("Tracks added in: %s\n", playlist.getURL());
    }

    public void addTracks(SpotifyPlaylist playlist, ArrayList<LastfmTrack> tracks) throws IOException, InterruptedException {
        for (LastfmTrack track : tracks) {
            SpotifyTrack spotifyTrack = SpotifyTrack.searchForTrack(track.getName(), track.getArtist().getName(), track.getAlbum().getName());
            playlist.addTrack(spotifyTrack);
        }

        System.out.printf("Tracks added in: %s\n", playlist.getURL());
    }

    public String getURL() {
        return "https://open.spotify.com/playlist/" + id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
