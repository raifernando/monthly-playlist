package com.raifernando;

import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.lastfm.LastfmUser;
import com.raifernando.spotify.*;
import com.raifernando.util.Credentials;
import com.raifernando.util.DateRange;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 3)
            throw new IllegalArgumentException("Invalid argument. Valid arguments: [LastfmUser] MMM YYYY");

        // Set LastfmUser and date for request
        LastfmUser user = new LastfmUser(args);
        DateRange dateRange = new DateRange(args);

        // Load api keys
        Credentials.loadKeys();

        // Get tracks
        ArrayList<LastfmTrack> tracks = user.getRecentTracks(dateRange);
        tracks = user.getUserTopTracks(tracks, 10, 7);

        SpotifyUser spotifyUser = SpotifyUser.getCurrentUser();
        SpotifyPlaylist playlist = new SpotifyPlaylist(spotifyUser, dateRange.getDateAsString());


        playlist.addMultipleTracks(tracks);
    }
}