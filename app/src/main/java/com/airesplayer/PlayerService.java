package com.airesplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.widget.RemoteViews;

import com.airesplayer.model.ItemMedia;
import com.airesplayer.lastFmApi.LastFmJsonUtil;
import com.airesplayer.lastFmApi.LastFmService;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY    = "action.PLAY";
    public static final String ACTION_COMPLETE= "action.COMPLETE";

    public static final String SERVICE_STARTED="SERVICE_STARTED";

    public static int currentIndex;
    public static String currentType;
    public static String name;
    List<ItemMedia> list;

    private MediaPlayer mediaPlayer;

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful


        Utils.sendMessenge(getApplicationContext(), SERVICE_STARTED,  null);

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_PLAY_NOTI);
        intentFilter.addAction(ACTION_NEXT_NOTI);
        intentFilter.addAction(ACTION_PREV_NOTI);
        intentFilter.addAction(ACTION_CLOSE_NOTI);
        registerReceiver(new NotificationReceiver(), intentFilter);


        return mBinder;
    }

    public void doInit(Integer index, String type, boolean autoPlay){

        init(index, type);

        if(autoPlay){
            play();
        }

    }

    private void init(Integer index, String type){

         int id =getIdByType( index,  type);

         if(id>=0){
             initPlayList(id,  type);

             currentIndex=getCurrentIndex(index,  type);

             currentType=type;
         }

    }

    private int getCurrentIndex(int index,String type){

        if(type.equals(Media.MUSIC.getTypeMedia())){
            return index;
        }

        return 0;
    }

    public void doContinue(){

        if(mediaPlayer==null){
            initPlayer();
        }else if( mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }

        showNotification();

    }

    public String getName(){
        return name;
    }

    public void play() {

        if(currentIndex> list.size())return;

        ItemMedia media = list.get(currentIndex);

        int id = media.getId();
        name= media.getTitle();

        showNotification();

        play(getPath(id));

    }

    public void play(int index) {

        currentIndex=index;
        play();

    }

    private void play(String path){

        try {

            if (mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            mediaPlayer=null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();

            mediaPlayer.start();

            sendMessenge(ACTION_PLAY,null);


        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public int getIndex(int i,int size){

        if(size==0)size=1;

        return (size+i) % size;
    }

    public void doForward(){

        if(Utils.emptyList(list))return;

        int size = list.size()-1;
        currentIndex++;

        currentIndex = getIndex(currentIndex,size);

        play();


    }

    public void doRewind(){

        if(Utils.emptyList(list))return;

        int size = list.size()-1;
        currentIndex--;

        currentIndex = getIndex(currentIndex,size);

        play();

    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    public void initPlayer(){

        AiresPlayerApp app = (AiresPlayerApp) getApplication();

        if( app.getListMusic()!=null && app.getListMusic().size()>0){

            init(0, Media.MUSIC.getTypeMedia());
            play();

        }

    }

    public void seekTo(int i) {

        if(mediaPlayer!=null)
            mediaPlayer.seekTo(i);
    }

    public int getDuration() {

        if(mediaPlayer==null)return 0;

        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {

        if(mediaPlayer==null)return false;

        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {

        if(mediaPlayer==null)return 0;

        return mediaPlayer.getCurrentPosition();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {}

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendMessenge(ACTION_COMPLETE,null);
    }

    private int getIdByType(int index, String type){

        AiresPlayerApp app = (AiresPlayerApp) getApplication();

        if(Media.MUSIC.getTypeMedia().equals(type)){

            if(Utils.emptyList(app.getListMusic()) || (index > app.getListMusic().size())){return -1;}

            return app.getListMusic().get(index).getId();

        }else  if(Media.ALBUM.getTypeMedia().equals(type)){

            if(Utils.emptyList(app.getListAlbum()) || (index > app.getListAlbum().size())){return -1;}

            return app.getListAlbum().get(index).getId();

        }else  if(Media.ARTIST.getTypeMedia().equals(type)){

            if(Utils.emptyList(app.getListArtist()) || (index > app.getListArtist().size())){return -1;}

            return app.getListArtist().get(index).getId();

        }

        return -1;

    }

    private void initPlayList(int id,String type){

        AiresPlayerApp app = (AiresPlayerApp) getApplication();

        if(Media.MUSIC.getTypeMedia().equals(type)){

              list = app.getListMusic();

        }else  if(Media.ALBUM.getTypeMedia().equals(type)){

              list= AudioUtils.getTracks(this,id);

        }else  if(Media.ARTIST.getTypeMedia().equals(type)){

              list= AudioUtils.getTracksFromArtist(this,id);

        }


    }

    private String getPath(int id){

            return AudioUtils.getMedia(this,id).getPath();

    }

    public List<ItemMedia> getPlayList(){

        return list;
    }

    public void reproduceNext(int position){

        ItemMedia item = list.get(position);

        if(list.get(currentIndex).getId()==item.getId()){
         return;
        }

        item = list.remove(position);

        if( (currentIndex+1)<list.size() ){

            list.add(currentIndex+1,item);
        }else{

            list.add(item);
        }




    }

    public int getCurrentId(){
        return list.get(currentIndex).getId();
    }

    public void sendMessenge(String action, String data){

        Utils.sendMessenge(this, action,  data);

    }

    private final String ACTION_PLAY_NOTI="ACTION_PLAY_NOTI";
    private final String ACTION_NEXT_NOTI="ACTION_PLAY_NEXT";
    private final String ACTION_PREV_NOTI="ACTION_PLAY_PREV";
    private final String ACTION_CLOSE_NOTI="ACTION_CLOSE_NOTI";

    private void showNotification() {

        if(Utils.emptyList(list) || (currentIndex > list.size()))return;

        int id =list.get(currentIndex).getId();

        final com.airesplayer.model.Media media = AudioUtils.getMedia(this, id);

        if(media==null)return;

        LastFmService.searchArtist(media.getArtist(), new LastFmService.CallBack() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imageUrl = LastFmJsonUtil.parseSearchArtist(response);

                    if(Utils.isEmpty(imageUrl)){
                        showContentNotification( media, null);
                        return;
                    }

                    Picasso.with(getApplicationContext())
                            .load(imageUrl)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                    showContentNotification( media, bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    showContentNotification( media, null);
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {}
                            });

                } catch (JSONException e) {
                    showContentNotification( media, null);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }

    private void showContentNotification(com.airesplayer.model.Media media,Bitmap bitmap){


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,   PendingIntent.FLAG_UPDATE_CURRENT);
        final RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.status_bar_expanded);

        views.setImageViewBitmap(R.id.status_bar_album_art,bitmap );
        views.setTextViewText(R.id.status_bar_track_name, media.getTitle());
        views.setTextViewText(R.id.status_bar_artist_name, media.getArtist());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("ACTION.MAIN_ACTION");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent play = new Intent();
        play.setAction(ACTION_PLAY_NOTI);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, 100, play, 0);

        Intent prev = new Intent();
        prev.setAction(ACTION_PREV_NOTI);
        PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(this, 101, prev, 0);

        Intent next = new Intent();
        next.setAction(ACTION_NEXT_NOTI);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 102, next, 0);

        Intent close = new Intent();
        close.setAction(ACTION_CLOSE_NOTI);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 103, close, 0);

        if(isPlaying()){
            views.setImageViewResource(R.id.status_bar_play,R.drawable.ic_play_arrow_white_36dp);
        }else{
            views.setImageViewResource(R.id.status_bar_play,R.drawable.ic_pause_white_36dp);
        }

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);
        views.setOnClickPendingIntent(R.id.status_bar_next, pendingNextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, pendingPrevIntent);
        views.setOnClickPendingIntent(R.id.close, pendingCloseIntent);




        Notification.Builder mNotifyBuilder = new Notification.Builder(this);
        Notification foregroundNote = mNotifyBuilder.setContentTitle(media.getArtist())
                .setContentText(media.getTitle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_album_black_36dp)
                // .setOngoing(true)
                .build();

        foregroundNote.bigContentView = views;

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyManager.notify(1, foregroundNote);



    }

    public void cancelNotification(){

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.cancelAll();

    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action.equals(ACTION_PLAY_NOTI)){
                doContinue();
                sendMessenge(ACTION_PLAY,null);
                showNotification();
            }else if(action.equals(ACTION_NEXT_NOTI)){
                doForward();
                showNotification();
            }else if(action.equals(ACTION_PREV_NOTI)){
                doRewind();
                showNotification();
            }else if(action.equals(ACTION_CLOSE_NOTI)){
                cancelNotification();
            }

        }

    }
}
