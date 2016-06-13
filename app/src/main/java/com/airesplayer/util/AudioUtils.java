package com.airesplayer.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.airesplayer.dao.ImageDAO;
import com.airesplayer.fragment.ItemListTwoLines;
import com.airesplayer.model.Album;
import com.airesplayer.model.Artist;
import com.airesplayer.model.Image;
import com.airesplayer.model.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by Aires on 21/06/2015.
 */
public class AudioUtils {

    public static List<ItemListTwoLines> getAll(Context context){


        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC+"=1",
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        return parseMedia(context,cursor);

    }

    public static Media getMedia(Context context, int id){


        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media._ID+"="+id,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        return (Media) parseMedia(context,cursor).get(0);

    }

    public static List<ItemListTwoLines> getTracksFromArtist(Context context,int artistId){


        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC+"=1 and "+MediaStore.Audio.Media.ARTIST_ID+"="+artistId,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        return parseMedia(context,cursor);

    }

    public static List<ItemListTwoLines> getTracks(Context context, int albumID){


        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC+"=1 and "+MediaStore.Audio.Media.ALBUM_ID+"="+albumID,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");



        return parseMedia(context,cursor);

    }

    public static List<ItemListTwoLines> getArtist(Context context){

        String[] projection = new String[] {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS

                };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        List<ItemListTwoLines> list = new ArrayList<ItemListTwoLines>();

        while (cursor.moveToNext()) {

            int id        = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
            int numAlbum  = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
            int numTracks = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Artists.NUMBER_OF_TRACKS));

            list.add(new Artist(context,id,artist,numAlbum,numTracks));


        }

        cursor.close();

        return list;

    }

    public static List<ItemListTwoLines> getAlbuns(Context context){

        String[] projection = new String[] { MediaStore.Audio.Albums._ID,
                                             MediaStore.Audio.Albums.ALBUM,
                                             MediaStore.Audio.Albums.ARTIST,
                                             MediaStore.Audio.Albums.ALBUM_ART,
                                             MediaStore.Audio.Albums.NUMBER_OF_SONGS
                                          };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";
        Cursor cursor = context.getContentResolver().query(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    sortOrder
        );

        List<ItemListTwoLines> listAlbum = new ArrayList<ItemListTwoLines>();

        while (cursor.moveToNext()) {

            String title       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            int albumId        = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
            String album       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            String albumArt    = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            listAlbum.add(new Album(albumId, album,albumArt,title));

        }


        cursor.close();

        return listAlbum;

    }

    public static String getAlbumArt(Context context,int albumId){


        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,albumId);

        return uri.toString();


/*

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[] {String.valueOf(albumId)},
                null);

        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            return path;
        }
        cursor.close();


        return null;

        */

    }

    private  static List<ItemListTwoLines> parseMedia(Context context, Cursor cursor){

        List<ItemListTwoLines> listMedia = new ArrayList<>();

        while (cursor.moveToNext()) {


            String title       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String path        = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            int  id            = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            int size           = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            String mimeType    = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
            String dateAdded   = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
            int duration       = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int artistId       = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
            String composer    = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
            int albumId        = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArt    = getAlbumArt(context,albumId);
            String track       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
            int year           = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
            String artist      = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album       = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            boolean isMusic    = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC))==1?true:false;

            listMedia.add(new Media(title,
                                    path,
                                    id,
                                    displayName,
                                    size,
                                    mimeType,
                                    dateAdded,
                                    duration,
                                    artistId,
                                    composer,
                                    albumId,
                                    albumArt,
                                    track,
                                    year,
                                    artist,
                                    album,
                                    isMusic)
            );


        }

        cursor.close();

        return listMedia;

    }

    public static int updateMedia(Context context,int id,String name,String album,String artist,String track,String year){

        ContentValues values = new ContentValues();
        values.put( MediaStore.Audio.Media.TITLE,name);
        values.put( MediaStore.Audio.Media.ALBUM,album);
        values.put( MediaStore.Audio.Media.ARTIST,artist);
        values.put( MediaStore.Audio.Media.TRACK,track);
        values.put( MediaStore.Audio.Media.YEAR,year);

        int num=context.getContentResolver().update(
                                             MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                             values,
                                             MediaStore.Audio.Media._ID+"=?",
                                             new String[] {String.valueOf(id)}
                                           );

        return num;

    }

    public static  int deleteMedia(Context context,int id){

        int num=context.getContentResolver().delete(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Audio.Media._ID+"=?",
                new String[] {String.valueOf(id)}
        );

        return num;



    }



}