package com.raifernando.spotify;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.util.Request;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;

public class SpotifyPlaylist {
    private final String id;

    /**
     * Creates an instance of {@link SpotifyPlaylist} with an existing playlist ID.
     * @param id the playlist ID
     * @throws NullPointerException if the ID is {@code null}
     */
    public SpotifyPlaylist(String id) throws NullPointerException {
        if (id == null)
            throw new NullPointerException("Playlist id cannot be null.");
        this.id = id;
    }

    /**
     * Creates an instance of {@link SpotifyPlaylist} creating a new Spotify Playlist with custom name.
     * @param user {@link SpotifyUser} where the playlist is created
     * @param name the playlist name
     * @throws BadRequestException if it fails to create the playlist
     * @throws NullPointerException if the ID of the created playlist is {@code null}
     */
    public SpotifyPlaylist(SpotifyUser user, String name) throws BadRequestException, NullPointerException {
        this(createPlaylist(user, name));
    }

    /**
     * Creates an instance of {@link SpotifyPlaylist} creating a new Spotify Playlist with the default name.
     * @param user {@link SpotifyUser} where the playlist is created
     * @throws BadRequestException if it fails to create the playlist
     * @throws NullPointerException if the ID of the created playlist is {@code null}
     */
    public SpotifyPlaylist(SpotifyUser user) throws BadRequestException, NullPointerException {
        this(createPlaylist(user));
    }

    /**
     * Creates a new playlist in the {@code user}'s Spotify account using the API.
     * The playlist has a custom {@code name}.
     * @param user {@link SpotifyUser} in whose account the playlist is created
     * @param name the name of the new playlist
     * @return a {@link String} containing the ID of the created playlist
     * @throws BadRequestException if the API request fails to create the playlist
     */
    private static String createPlaylist(SpotifyUser user, String name) throws BadRequestException{
        if (OAuth.accessToken == null || user.getId() == null) {
            System.out.println("User not authenticated. Failed to create playlist.");
            return null;
        }

        System.out.printf("Creating playlist [%s].\n", name);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("name", name);

        String [] headers = {
                "content-type", "application/json",
                "Authorization", "Bearer " + OAuth.accessToken
        };

        SpotifyPlaylist response = Request.requestPost(
                "https://api.spotify.com/v1/users/" + user.getId() + "/playlists",
                jsonBody.toString(),
                headers,
                SpotifyPlaylist.class
        );

        if (response == null)
            throw new BadRequestException("Error creating playlist.");

        return response.id;
    }

    /**
     * Creates a new playlist in the {@code user}'s Spotify account using the API.
     * The playlist has the default name "monthly-playlist".
     * @param user {@link SpotifyUser} in whose account the playlist is created
     * @return a {@link String} containing the ID of the created playlist
     * @throws BadRequestException if the API request fails to create the playlist
     */
    private static String createPlaylist(SpotifyUser user) throws BadRequestException{
        return createPlaylist(user, "monthly-playlist");
    }

    /**
     * Sends a request to add the list of URIs in this playlist.
     * The list of URIs must be in the JSON format (see {@link #generateUrisJsonBody(List)}).
     * A maximum of 100 tracks can be added at once.
     * @param uris list of uris
     * @return {@code true} if the request was successful, or {@code false} if it failed
     */
    private boolean requestToAddTrack(String uris) {
        if (uris == null)
            return false;

        String [] headers = {
                "content-type", "application/json",
                "Authorization", "Bearer " + OAuth.accessToken
        };

        SpotifyPlaylist response = Request.requestPost(
                "https://api.spotify.com/v1/playlists/" + id + "/tracks",
                uris,
                headers,
                SpotifyPlaylist.class
        );

        return response != null;
    }

    /**
     * Generates a {@link String} containing the list of URIs used to add tracks into the playlist.
     * The {@link String} follows this format: {"uris":[..., ...]}
     * @param trackList a list of {@link SpotifyTrack} used to retrieve the track's URI
     * @return a {@link String} containing the list of URIs
     */
    private static String generateUrisJsonBody(List<SpotifyTrack> trackList) {
        JsonArray uris = new JsonArray();

        for (SpotifyTrack track : trackList) {
            if (track != null)
                uris.add(track.getUri());
        }

        JsonObject urisJsonBody = new JsonObject();
        urisJsonBody.add("uris", uris);

        return urisJsonBody.toString();
    }

    /**
     * Add all tracks from the list of {@link LastfmTrack} into this playlist.
     * @param tracks list of {@link LastfmTrack} used to search for a {@link SpotifyTrack}
     */
    public void addMultipleTracks(ArrayList<LastfmTrack> tracks) {
        LastfmTrack.printTracksInformation(tracks);
        System.out.println("Adding tracks to playlist");

        List<SpotifyTrack> tracklist = new ArrayList<>();
        for (LastfmTrack track : tracks) {
            SpotifyTrack spotifyTrack = SpotifyTrack.searchForTrack(track.getName(), track.getArtist().getName(), track.getAlbum().getName());
            tracklist.add(spotifyTrack);
        }

        // A maximum of 100 items can be added in one request.
        List<List<SpotifyTrack>> groupOfTracks = Lists.partition(tracklist, 100);
        for (List<SpotifyTrack> group : groupOfTracks) {
            if (!requestToAddTrack(generateUrisJsonBody(group)))
                System.out.println("------ Error adding tracks");
        }

        System.out.printf("Tracks added in: %s\n", getURL());
    }

    /**
     * @return the URL for the playlist
     */
    public String getURL() {
        return "https://open.spotify.com/playlist/" + id;
    }
}
