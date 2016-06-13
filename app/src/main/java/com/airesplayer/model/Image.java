package com.airesplayer.model;

import android.content.ContentValues;

import com.airesplayer.dao.DatabaseHelper;

/**
 * Created by Aires on 30/09/2015.
 */
public class Image {

    Integer height;
    Integer width;
    String url;
    Integer id;
    String spotifyId;

    public Image(Integer height, Integer width, String url, Integer id, String spotifyId) {
        this.height = height;
        this.width = width;
        this.url = url;
        this.id = id;
        this.spotifyId = spotifyId;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public ContentValues toContentValues(){

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.KEY_IMAGE_URL,getUrl());
        values.put(DatabaseHelper.KEY_IMAGE_ID,getId());
        values.put(DatabaseHelper.KEY_IMAGE_SPOTIFY_ID,getSpotifyId());
        values.put(DatabaseHelper.KEY_IMAGE_HEIGHT, getHeight());
        values.put(DatabaseHelper.KEY_IMAGE_WIDTH, getWidth());

        return values;


    }

}
