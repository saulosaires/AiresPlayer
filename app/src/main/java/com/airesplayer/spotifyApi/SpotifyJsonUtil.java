package com.airesplayer.spotifyApi;

import com.airesplayer.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Aires on 30/09/2015.
 */
public class SpotifyJsonUtil {

    public static List<Image> parseSearchArtist(JSONObject response) throws JSONException {

        JSONObject artists = response.getJSONObject("artists");

        JSONArray items = artists.getJSONArray("items");

        if (items.length() > 0) {

            JSONObject item= items.getJSONObject(0);
            String id= item.getString("id");
            JSONArray images = item.getJSONArray("images");

            if (images != null) {

                List<Image> listImage = new ArrayList<Image>();

                for (int i = 0; i < images.length(); i++) {

                    JSONObject img = images.getJSONObject(i);

                    int height = img.getInt("height");
                    int width = img.getInt("width");
                    String url = img.getString("url");

                    listImage.add(new Image(height, width, url,null, id));

                }
                return listImage;
            }

        }

        return null;

    }

    public static List<Image> parseSearchAlbum(JSONObject response) throws JSONException {

        if(!response.has("albums"))return null;

        JSONObject albums = response.getJSONObject("albums");

        JSONArray items = albums.getJSONArray("items");

        if (items.length() > 0) {

            JSONObject item= items.getJSONObject(0);

            JSONArray images = item.getJSONArray("images");

            if (images != null) {

                List<Image> listImage = new ArrayList<Image>();

                for (int i = 0; i < images.length(); i++) {

                    JSONObject img = images.getJSONObject(i);

                    int height = img.getInt("height");
                    int width = img.getInt("width");
                    String url = img.getString("url");

                    listImage.add(new Image(height, width, url,null,null));

                }
                return listImage;
            }

        }

        return null;

    }
}
