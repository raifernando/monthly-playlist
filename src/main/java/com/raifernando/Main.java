package com.raifernando;

import com.raifernando.lastfm.LastfmCredentials;
import com.raifernando.lastfm.LastfmTrack;
import com.raifernando.lastfm.LastfmUser;
import com.raifernando.spotify.*;
import com.raifernando.util.PropertiesFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        // Setup api credentials
        PropertiesFile.setFileName("config.properties");
        Credentials.loadKeys();
        OAuth.getAccessCode();

        LastfmCredentials.loadKeys();
        LastfmUser user = new LastfmUser("rrrarrsr");

        // Date for lastfm history
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 27);

        long startUnix = startDate.atStartOfDay(zoneId).toEpochSecond();
        long endUnix = endDate.atStartOfDay(zoneId).toEpochSecond();

        ArrayList<LastfmTrack> tracks = user.getRecentTracks(String.valueOf(startUnix), String.valueOf(endUnix));
        tracks = user.getUserTopTracks(tracks, 0, 30, 0);


        SpotifyUser spotifyUser = SpotifyUser.getCurrentUser();
        SpotifyPlaylist playlist = SpotifyPlaylist.createPlaylist(spotifyUser);
//        SpotifyPlaylist playlist = new SpotifyPlaylist("4gpClYRjmw4BSM3dRG2m1S");
//        playlist.setId("4gpClYRjmw4BSM3dRG2m1S");

        playlist.addTracks(playlist, tracks);
    }
}