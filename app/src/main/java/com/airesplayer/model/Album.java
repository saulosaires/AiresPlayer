package com.airesplayer.model;

/**
 * Created by Aires on 23/09/2015.
 */
public class Album  implements ItemMedia {

    private int albumId;
    private String album;
    private String albumArt;
    private String artistName;

    public Album() {}

    public Album(int albumId,String album,String albumArt,String title) {

        this.albumId=albumId;
        this.album=album;
        this.albumArt=albumArt;
        this.artistName=title;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
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

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public int getId() {
        return getAlbumId();
    }

    @Override
    public String getArtAlbum() {
        return getAlbumArt();
    }

    @Override
    public String getTitle() {
        return getAlbum();
    }

    @Override
    public String getSubTitle() {
        return getArtistName();
    }
}
