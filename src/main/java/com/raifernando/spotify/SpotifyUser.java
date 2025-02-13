package com.raifernando.spotify;

import com.raifernando.util.Request;

/**
 * The {@link SpotifyUser} stores the ID information about a Spotify user.
 */
public class SpotifyUser {
    private String id;

    /**
     * <p>
     *     Requests the information about the current authenticated user.
     * </p>
     * <p>
     *     The process of authentication in {@link OAuth} must be completed
     *     before this method is called.
     * </p>
     * @return the current authenticated user as a {@link SpotifyUser}
     * @throws NullPointerException if the request fails
     */
    public static SpotifyUser getCurrentUser() {
        System.out.println("Requesting current user's information.");

        SpotifyUser response = Request.get(
                "https://api.spotify.com/v1/me",
                new String[] {"Authorization", "Bearer " + OAuth.accessToken},
                SpotifyUser.class);

        if (response == null)
            throw new NullPointerException("Failed to retrieve the current user information.");

        return response;
    }

    public String getId() {
        return id;
    }
}
