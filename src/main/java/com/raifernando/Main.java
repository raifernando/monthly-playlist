package com.raifernando;

import com.raifernando.spotify.Credentials;
import com.raifernando.spotify.Track;
import com.raifernando.util.PropertiesFile;

public class Main {
    public static void main(String[] args) throws Exception {

        PropertiesFile.setFileName("config.properties");
//
        Credentials.loadKeys();

        Track.getTrack("6rdkCkjk6D12xRpdMXy0I2");
//
//        OAuth.getAccessCode();
//
//        SpotifyUser user = SpotifyUser.getCurrentUser();
//        System.out.println(user.getId());

//        System.out.println(OAuth.spotifyLoginUrl());

    }
}