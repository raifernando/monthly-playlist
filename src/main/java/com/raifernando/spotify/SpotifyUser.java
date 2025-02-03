package com.raifernando.spotify;

import com.raifernando.util.Request;

public class SpotifyUser {
    private String id = null;

    public static SpotifyUser getCurrentUser() {
        return Request.requestGet(
                "https://api.spotify.com/v1/me",
                new String[] {"Authorization", "Bearer " + OAuth.accessToken},
                SpotifyUser.class);
    }

    public String getId() {
        return id;
    }
}
