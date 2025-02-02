package com.raifernando.lastfm;

import com.google.gson.annotations.SerializedName;

public class LastfmAlbum {
    @SerializedName("#text")
    private String name;

    public String getName() {
        return name;
    }
}
