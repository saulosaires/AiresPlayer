package com.airesplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.airesplayer.model.ItemMedia;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Aires on 29/09/2015.
 */
public class AiresPlayerApp extends Application {

    List<ItemMedia> listMusic;
    List<ItemMedia> listAlbum;
    List<ItemMedia> listArtist;

    private static AiresPlayerApp mInstance;
    private RequestQueue mRequestQueue;

    PlayerService s;


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder binder) {

            PlayerService.MyBinder b = (PlayerService.MyBinder) binder;
            s = b.getService();


            s.doInit(0,Media.MUSIC.getTypeMedia(),false);


        }

        public void onServiceDisconnected(ComponentName className) {

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

        Realm.init(this);
    }


    public void bindService() {

        Intent intent= new Intent(this, PlayerService.class);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        startService(intent);

    }

    public void cancelNotification(){
        if(s!=null)s.cancelNotification();
    }

    public void reproduceNext(int position){

        if(s!=null)
        s.reproduceNext(position);
    }


    public void doInit(Integer index, String type, boolean autoPlay){

        if(s!=null)
        s.doInit(index, type, autoPlay);
    }

    public String  getName(){

        if(s==null) return "";

       return  s.getName();
    }

    public void  play(int index){

        if(s!=null)
         s.play(index);
    }

    public int  getCurrentIndex(){

        if(s==null) return 0;

        return s.getCurrentIndex();
    }

    public List<ItemMedia> getPlayList(){

        if(s==null) return new ArrayList<>();

        return s.getPlayList();
    }

    public void doRewind() {

        if(s!=null)
           s.doRewind();

    }

    public void doForward(){

        if(s!=null)
           s.doForward();
    }

    public void doContinue(){

        if(s!=null)
           s.doContinue();
    }

    public void seekTo(int position){

        if(s!=null)
         s.seekTo(position);
    }

    public boolean isPlaying(){

        if(s==null) return false;

        return s.isPlaying();
    }

    public int getDuration(){

        if(s==null) return 0;

        return s.getDuration();
    }

    public int getCurrentPosition(){

        if(s==null) return 0;

        return s.getCurrentPosition();
    }

    public List<ItemMedia> getListMusic() {
        return listMusic;
    }

    public void setListMusic(List<ItemMedia> listMusic) {
        this.listMusic = listMusic;
    }

    public List<ItemMedia> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(List<ItemMedia> listAlbum) {
        this.listAlbum = listAlbum;
    }

    public List<ItemMedia> getListArtist() {
        return listArtist;
    }

    public void setListArtist(List<ItemMedia> listArtist) {
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
