package com.raifernando.spotify;

import java.io.IOException;

public class SpotifyUser {
    private String id = null;

    public static SpotifyUser getCurrentUser() throws IOException, InterruptedException {
        return Request.requestGet("https://api.spotify.com/v1/me", OAuth.accessToken, SpotifyUser.class);
    }

    public String getId() {
        return id;
    }
}
