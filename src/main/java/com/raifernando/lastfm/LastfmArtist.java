package com.raifernando.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmArtist {
    private String mbid;

    @SerializedName("#text")
    private String name;

    public String getName() {
        return name;
    }
}
