package com.raifernando.lastfm;

import java.util.ArrayList;

public class LastfmTrack {
    private String name;
    private LastfmArtist artist;
    private LastfmAlbum album;
    private transient int playcount;

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

    public LastfmAlbum getAlbum() {
        return album;
    }

    /**
     * Print every track information (name, artist and playcount) for each track in the array.
     * @param tracks array of tracks
     */
    public static void printTracksInformation(ArrayList<LastfmTrack> tracks) {
        if (tracks == null)
            return;

        System.out.printf("%d tracks selected:\n", tracks.size());
        for (LastfmTrack track : tracks) {
            System.out.printf(">>> %s - %s | %d scrobbles\n",
                    track.getName(), track.getArtist().getName(), track.getPlaycount()
            );
        }
    }
}
