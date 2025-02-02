package com.raifernando.lastfm;

public class LastfmTrack {
    private String name;
    private LastfmArtist artist;
    private LastfmAlbum album;
    private transient int playcount;

    private Date date;
    private static class Date {
        private String uts;
    }

    public String getName() {
        return name;
    }

    public LastfmArtist getArtist() {
        return artist;
    }

    public int getPlaycount() {
        return playcount;
    }

    public int compareNameTo(LastfmTrack b) {
        return name.compareTo(b.name);
    }

    public void increasePlaycount(int x) {
        playcount += x;
    }

    public String getDate() {
        return date.uts;
    }

    public LastfmAlbum getAlbum() {
        return album;
    }

    @Override
    public String toString() {
        return "LastfmTrack{" +
                ", name='" + name + '\'' +
                ", artist=" + artist.getName() +
                '}';
    }
}
