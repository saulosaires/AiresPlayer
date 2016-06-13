package com.airesplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.airesplayer.fragment.ItemListTwoLines;
import com.airesplayer.util.AudioUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by Aires on 29/09/2015.
 */
public class AiresPlayerApp extends Application {

    List<ItemListTwoLines> listMusic;
    List<ItemListTwoLines> listAlbum;
    List<ItemListTwoLines> listArtist;

    private static AiresPlayerApp mInstance;
    private RequestQueue mRequestQueue;

    PlayerService s;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder binder) {

            PlayerService.MyBinder b = (PlayerService.MyBinder) binder;
            s = b.getService();
            s.init(null,null);


        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

    public static AiresPlayerApp getInstance()
    {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public PlayerService getService(){
        return s;
    }


    public void init(){

        setListMusic(AudioUtils.getAll(this));
        setListAlbum(AudioUtils.getAlbuns(this));
        setListArtist(AudioUtils.getArtist(this));


        Intent intent= new Intent(this, PlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }



    public List<ItemListTwoLines> getListMusic() {
        return listMusic;
    }

    public void setListMusic(List<ItemListTwoLines> listMusic) {
        this.listMusic = listMusic;
    }

    public List<ItemListTwoLines> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(List<ItemListTwoLines> listAlbum) {
        this.listAlbum = listAlbum;
    }

    public List<ItemListTwoLines> getListArtist() {
        return listArtist;
    }

    public void setListArtist(List<ItemListTwoLines> listArtist) {
        this.listArtist = listArtist;
    }

    public RequestQueue getVolleyRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    private static void addRequest(Request<?> request)
    {
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        getInstance().getVolleyRequestQueue().add(request);
    }

    public static void addRequest(Request<?> request, String tag)
    {
        request.setTag(tag);
        addRequest(request);
    }

    public static void cancelAllRequests(String tag)
    {
        if (getInstance().getVolleyRequestQueue() != null)
        {
            getInstance().getVolleyRequestQueue().cancelAll(tag);
        }
    }




}
