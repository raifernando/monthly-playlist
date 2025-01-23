package com.raifernando.lastfm;

public class Track {
    private String mbid;
    private String name;
    private Artist artist;
    private int playcount;

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    public int getPlaycount() {
        return playcount;
    }

    public int compareMbidTo(Track b) {
        return mbid.compareTo(b.mbid);
    }

    public int compareNameTo(Track b) {
        return name.compareTo(b.name);
    }

    public String getMbid() {
        return mbid;
    }

    public void increasePlaycount(int x) {
        playcount += x;
    }
}
