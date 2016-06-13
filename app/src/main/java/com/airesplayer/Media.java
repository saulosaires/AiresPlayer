package com.airesplayer;

/**
 * Created by saulo on 23/04/2016.
 */
public enum Media {

    MUSIC("MUSIC"),
    ALBUM("ALBUM"),
    ARTIST("ARTIST");


    private final String typeMedia;

    Media(String typeMedia) {
        this.typeMedia = typeMedia;
    }

    public String getTypeMedia() {
        return this.typeMedia;
    }

}
