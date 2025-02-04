package com.raifernando;

import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.lastfm.LastfmUser;
import com.raifernando.spotify.*;
import com.raifernando.util.Credentials;
import com.raifernando.util.DateRange;
import com.raifernando.util.PropertiesFile;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Date for request not provided.");
            System.exit(0);
        }

        // Setup api credentials
        PropertiesFile.setFileName("config.properties");
        try {
            Credentials.loadKeys();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        LastfmUser user = new LastfmUser();
        try {
            user.setUser(args);
        } catch (InvalidParameterException e) {
            System.out.println("Invalid lastfm username");
            System.exit(0);
        }

        // Date for lastfm history
        DateRange dateRange = new DateRange();
        try {
            dateRange.setDate(args);
        } catch (ParseException e) {
            System.out.printf("Invalid month [%s].", e.getMessage());
            System.exit(0);
        }

        OAuth.getAccessCode();

        ArrayList<LastfmTrack> tracks = user.getRecentTracks(dateRange);
        tracks = user.getUserTopTracks(tracks, 0, 3, 0);

        SpotifyUser spotifyUser = SpotifyUser.getCurrentUser();
        SpotifyPlaylist playlist = SpotifyPlaylist.createPlaylist(spotifyUser, dateRange.getDateAsString());

        playlist.addMultipleTracks(playlist, tracks);
    }
}