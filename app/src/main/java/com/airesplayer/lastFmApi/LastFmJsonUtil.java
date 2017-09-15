package com.airesplayer.lastFmApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LastFmJsonUtil {

    public static String parseSearchArtist(JSONObject response) throws JSONException {

        if(!response.has("artist")){
            return null;
        }

        JSONObject artists = response.getJSONObject("artist");

        JSONArray images = artists.getJSONArray("image");


            if (images != null) {

                JSONObject img = images.getJSONObject(images.length()-1);

                return img.getString("#text");
            }



        return null;

    }
    public static String parseSearchAlbum(JSONObject response) throws JSONException {

        if(!response.has("album")){
            return null;
        }

        JSONObject artists = response.getJSONObject("album");

        JSONArray images = artists.getJSONArray("image");


        if (images != null) {

            JSONObject img = images.getJSONObject(images.length()-1);

            return img.getString("#text");
        }



        return null;

    }
}
