package com.raifernando.lastfm;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link LastfmArtist} class stores the track's artist name
 * retrieved from the JSON data returned by the Last.fm API response.
 */
public class LastfmArtist {
    @SerializedName("#text") // Inside the "artist" field
    private String name;

    public String getName() {
        return name;
    }
}
