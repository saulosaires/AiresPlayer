package com.airesplayer.spotifyApi;


import android.util.Log;

import com.airesplayer.AiresPlayerApp;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;



public class SpotifyService {

     public static final String url="https://api.spotify.com/";

     public static final String searchArtist="v1/search";


     public static void searchArtist(String  name,final CallBack callBack){

         if(callBack==null) {
             throw new IllegalArgumentException("callBack cant be null");
         }
         if(name==null) {
             throw new IllegalArgumentException("name cant be null");
         }

         StringBuilder mUrl = new StringBuilder(url);

         try {
             mUrl.append(searchArtist).append("?q=").append( URLEncoder.encode(name, "UTF-8")).append("&type=artist");
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }

         Log.v(SpotifyService.class.getName(), mUrl.toString());

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

    public static void searchAlbum(String  name,final CallBack callBack){

        if(callBack==null) {
            throw new IllegalArgumentException("callBack cant be null");
        }
        if(name==null) {
            throw new IllegalArgumentException("name cant be null");
        }

        StringBuilder mUrl = new StringBuilder(url);

        try {
            mUrl.append(searchArtist).append("?q=").append(URLEncoder.encode(name, "UTF-8")).append("&type=album");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.v(SpotifyService.class.getName(), mUrl.toString());

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
