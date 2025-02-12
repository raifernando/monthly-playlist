package com.raifernando.lastfm;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link LastfmAlbum} class stores the track's album name
 * retrieved from the JSON data returned by the Last.fm API response.
 */
public class LastfmAlbum {
    @SerializedName("#text") // Inside the "album" field
    private String name;

    public String getName() {
        return name;
    }
}
