package com.raifernando.spotify;

import com.raifernando.util.Request;

public class SpotifyUser {
    private String id = null;

    public static SpotifyUser getCurrentUser() {
        System.out.println("Requesting current user's information.");

        SpotifyUser response = Request.get(
                "https://api.spotify.com/v1/me",
                new String[] {"Authorization", "Bearer " + OAuth.accessToken},
                SpotifyUser.class);

        if (response == null)
            System.out.println("Failed to get current user.");

        return response;
    }

    public String getId() {
        return id;
    }
}
