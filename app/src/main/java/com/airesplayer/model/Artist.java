package com.airesplayer.model;

import android.content.ContentValues;
import android.content.Context;

import com.airesplayer.R;
import com.airesplayer.fragment.ItemListTwoLines;

import java.util.ArrayList;
import java.util.List;


public class Artist   implements ItemListTwoLines {

    private int id;
    private String artist;
    private int numAlbum;
    private int numTracks;
    private String numAlbumLabel;
    private String numTracksLabel;

    public Artist() {
        super();
    }

    public Artist(Context context,int id, String artist, int numAlbum, int numTracks) {
        this.artist = artist;
        this.numAlbum = numAlbum;
        this.numTracks = numTracks;
        this.id=id;
       numAlbumLabel=numAlbumToLabel(context);
       numTracksLabel=numTracksToLabel(context);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getNumAlbum() {
        return numAlbum;
    }

    public void setNumAlbum(int numAlbum) {
        this.numAlbum = numAlbum;
    }

    public int getNumTracks() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }

    private String numAlbumToLabel(Context context){

        if(numAlbum>1){
            return numAlbum+ context.getResources().getString(R.string.albuns);
        }else{
            return numAlbum+ context.getResources().getString(R.string.album);
        }

    }

    private String numTracksToLabel(Context context){

        if(numTracks>1){
            return numTracks+ context.getResources().getString(R.string.musics);
        }else{
            return numTracks+ context.getResources().getString(R.string.music);
        }

    }

    @Override
    public String getArtAlbum() {
        return null;
    }

    @Override
    public String getTitle() {
        return getArtist();
    }

    @Override
    public String getSubTitle() {

        return numAlbumLabel + ", "+ numTracksLabel;
    }
}
