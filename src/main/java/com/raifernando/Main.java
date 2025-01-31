package com.raifernando;

import com.raifernando.lastfm.LastfmCredentials;
import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.lastfm.LastfmUser;
import com.raifernando.spotify.*;
import com.raifernando.util.DateRange;
import com.raifernando.util.PropertiesFile;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        // Setup api credentials
        PropertiesFile.setFileName("config.properties");

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

        Credentials.loadKeys();
        OAuth.getAccessCode();
        LastfmCredentials.loadKeys();

        ArrayList<LastfmTrack> tracks = user.getRecentTracks(dateRange);
        tracks = user.getUserTopTracks(tracks, 0, 30, 0);

        SpotifyUser spotifyUser = SpotifyUser.getCurrentUser();
        SpotifyPlaylist playlist = new SpotifyPlaylist("4gpClYRjmw4BSM3dRG2m1S");

        playlist.addTracks(playlist, tracks);
    }
}