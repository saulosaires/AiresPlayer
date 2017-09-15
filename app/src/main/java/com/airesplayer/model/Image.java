package com.airesplayer.model;

import io.realm.RealmObject;



public class Image extends RealmObject {

    private int mediaId;
    private String imageUrl;

    public Image() {}

    public Image(int mediaId, String imageUrl) {
        this.mediaId = mediaId;
        this.imageUrl = imageUrl;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
