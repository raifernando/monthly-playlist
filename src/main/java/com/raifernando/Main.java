package com.raifernando;

import com.raifernando.spotify.*;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.QueryGenerator;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        PropertiesFile.setFileName("config.properties");
//
        Credentials.loadKeys();

//        Track.getTrack("6rdkCkjk6D12xRpdMXy0I2");

        OAuth.getAccessCode();
//
//        SpotifyUser user = SpotifyUser.getCurrentUser();
//        SpotifyPlaylist playlist = SpotifyPlaylist.createPlaylist(user);
//
//        System.out.println(playlist.getId());

        Track track = Track.searchForTrack("dealer", "lana del rey");
        System.out.println(track.toString());

        SpotifyPlaylist playlist = new SpotifyPlaylist();
        playlist.setId("4gpClYRjmw4BSM3dRG2m1S");

        playlist.addTrack(track);
        playlist.addTrack(track);
        playlist.addTrack(track);




    }
}