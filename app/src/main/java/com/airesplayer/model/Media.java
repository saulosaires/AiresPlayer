package com.airesplayer.model;

import com.airesplayer.fragment.ItemListTwoLines;

/**
 * Created by Aires on 23/09/2015.
 */
public class Media implements ItemListTwoLines {

    private String mediaTitle;
    private String path;
    private int  id;
    private String displayName;
    private int size;
    private String mimeType;
    private String dateAdded;
    private int duration;
    private int artistId;
    private String composer;
    private int albumId;
    private String albumArt;
    private String track;
    private int year;
    private String artist;
    private String album;
    private boolean isMusic;

    public Media(){}

    public Media(String title,
                 String path,
                 int id,
                 String displayName,
                 int size,
                 String mimeType,
                 String dateAdded,
                 int duration,
                 int artistId,
                 String composer,
                 int albumId,
                 String albumArt,
                 String track,
                 int year,
                 String artist,
                 String album,
                 boolean isMusic) {


        this.mediaTitle = title;
        this.path = path;
        this.id = id;

        this.displayName = displayName;
        this.size = size;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
        this.duration = duration;
        this.artistId = artistId;
        this.composer = composer;
        this.albumId = albumId;
        this.albumArt=albumArt;
        this.track = track;
        this.year = year;
        this.artist = artist;
        this.album = album;
        this.isMusic = isMusic;
    }

    public boolean isMusic() {
        return isMusic;
    }

    public void setIsMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public void setMusic(boolean music) {
        isMusic = music;
    }

    @Override
    public String getArtAlbum() {
        return getAlbumArt();
    }

    @Override
    public String getTitle() {
        return getMediaTitle();
    }

    @Override
    public String getSubTitle() {
        return getArtist();
    }
}