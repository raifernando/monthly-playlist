package com.raifernando.lastfm;

public class LastfmTrack {
    private String mbid;
    private String name;
    private LastfmArtist artist;
    private int playcount;

    public String getName() {
        return name;
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    public int getPlaycount() {
        return playcount;
    }

    public int compareMbidTo(LastfmTrack b) {
        return mbid.compareTo(b.mbid);
    }

    public int compareNameTo(LastfmTrack b) {
        return name.compareTo(b.name);
    }

    public String getMbid() {
        return mbid;
    }

    public void increasePlaycount(int x) {
        playcount += x;
    }

    @Override
    public String toString() {
        return "LastfmTrack{" +
                ", name='" + name + '\'' +
                ", artist=" + artist.getName() +
                '}';
    }
}
