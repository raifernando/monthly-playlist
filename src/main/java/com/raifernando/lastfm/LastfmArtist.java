package com.raifernando.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmArtist {
    @SerializedName("#text")
    private String name;

    public String getName() {
        return name;
    }
}
