package com.raifernando.spotify;

import java.io.IOException;
import java.util.ArrayList;

public class Track {
    private String id;
    private Album album;
    private ArrayList<Artist> artists;
    private String name;
    private int popularity;
    private int track_number;

    public static Track getTrack(String trackId) throws IOException, InterruptedException {
        String requestUrl = "https://api.spotify.com/v1/tracks/" + trackId;

        Track track = Request.requestGet(requestUrl, Track.class);
        System.out.println(track.toString());
        return track;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", album=" + album +
                ", artists=" + artists +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                ", track_number=" + track_number +
                '}';
    }
}
