package com.airesplayer.lastFmApi;


import android.util.Log;

import com.airesplayer.AiresPlayerApp;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class LastFmService {

    public static final String apiKey="e3ebe97f722ddf42fa2df40d0fcf4148";
    public static final String sharedSecret="301ef833b502f33a4893ed7bafe22ee2";

     public static final String url="http://ws.audioscrobbler.com/2.0/";

     private static final String searchArtist="artist.getinfo";
     private static final String searchAlbum="album.getinfo";

     public static void searchArtist(String  name,final CallBack callBack){

         if(callBack==null) {
             throw new IllegalArgumentException("callBack cant be null");
         }
         if(name==null) {
             throw new IllegalArgumentException("name cant be null");
         }

         StringBuilder mUrl = new StringBuilder(url);

         try {
             mUrl.append("?method=").append(searchArtist).append("&artist=")
                                    .append( URLEncoder.encode(name, "UTF-8"))
                                    .append("&api_key=")
                                    .append(apiKey)
                                    .append("&format=json");

         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

         Log.v(LastFmService.class.getName(), mUrl.toString());

         JsonObjectRequest jsonObjectRequest =
                 new JsonObjectRequest(Request.Method.GET, mUrl.toString(), new Response.Listener<JSONObject>()
                 {
                     @Override
                     public void onResponse(JSONObject response)
                     {
                         callBack.onResponse(response);
                     }
                 },
                 new Response.ErrorListener()
                 {
                     @Override
                     public void onErrorResponse(VolleyError error)
                     {
                         callBack.onErrorResponse(error);
                     }
                 });

         AiresPlayerApp.getInstance().addRequest(jsonObjectRequest, searchArtist);
     }

    public static void searchAlbum(String  artist,String  album,final CallBack callBack){

        if(callBack==null) {
            throw new IllegalArgumentException("callBack cant be null");
        }
        if(artist==null) {
            throw new IllegalArgumentException("artist cant be null");
        }
        if(album==null) {
            throw new IllegalArgumentException("album cant be null");
        }

        StringBuilder mUrl = new StringBuilder(url);

        try {
            mUrl.append("?method=").append(searchAlbum)
                    .append("&artist=").append( URLEncoder.encode(artist, "UTF-8"))
                    .append("&album=").append( URLEncoder.encode(album, "UTF-8"))
                    .append("&api_key=").append(apiKey)
                    .append("&format=json");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.v(LastFmService.class.getName(), mUrl.toString());

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, mUrl.toString(), new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        callBack.onResponse(response);
                    }
                },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                callBack.onErrorResponse(error);
                            }
                        });

        AiresPlayerApp.getInstance().addRequest(jsonObjectRequest, searchArtist);
    }

    public static abstract class CallBack {

        public abstract void onResponse(JSONObject response);
        public abstract void onErrorResponse(VolleyError error);
    }

}
