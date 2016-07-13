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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import android.provider.MediaStore;
import android.widget.RemoteViews;

import com.airesplayer.fragment.ItemListTwoLines;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY           = "action.PLAY";
    public static final String ACTION_PAUSE          = "action.PAUSE";
    public static final String ACTION_INIT           = "action.CONTINUE";
    public static final String ACTION_COMPLETE       = "action.COMPLETE";
    public static final String ACTION_PREPARED       = "action.PREPARED";

    public static final String ACTION_SELECTED       = "action.SELECTED";

    public static final String PANEL_STATE_COLLAPSED = "action.COLLAPSED";
    public static final String PANEL_STATE_EXPANDED  = "action.EXPANDED";


    public static int currentIndex;
    public static String currentType;

    List<ItemListTwoLines> list;

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
        return Service.START_NOT_STICKY;
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

    public void init(Integer index, String type){


        if(index!=null && index>=0 && type!=null){

            int id =getIdByType( index,  type);

            initPlayList(id,  type);

            //path=getPath(id,  type);

            if(Media.MUSIC.getTypeMedia().equals(type))
                currentIndex=index;
            else
                currentIndex=0;



            currentType=type;



        }else{

            currentIndex=0;
            currentType=Media.MUSIC.getTypeMedia();

            AiresPlayerApp app = (AiresPlayerApp) getApplication();
            list = app.getListMusic();
        }

        if(list!=null && list.size()>0) {
            sendMessenge(ACTION_INIT, list.get(currentIndex).getId() + "");
            showNotification(list.get(currentIndex).getId());
            //sendMessenge(ACTION_CHANGE_CENTRAL, list.get(currentIndex).getId() + "");
        }
    }

    public void doContinue(){

        if(mediaPlayer!=null  && mediaPlayer.isPlaying()){
             pause();

        }else{

            if(mediaPlayer==null){
                initPlayer();
            }

            mediaPlayer.start();

            sendMessenge(ACTION_PLAY,null);

        }

    }

    public void play(int index, String type) {

            init(index,type);

            int id = list.get(currentIndex).getId();

            play(getPath(id,  type));

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
            sendMessenge(ACTION_INIT,list.get(currentIndex).getId()+"");
            sendMessenge(ACTION_SELECTED,null);
          //  sendMessenge(ACTION_CHANGE_CENTRAL,list.get(currentIndex).getId()+"");

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public int getIndex(int i,int size){

        return (size+i) % size;
    }

    public void doForward(){

        if(list==null || list.size()==0)return;

        int size = list.size()-1;
        currentIndex++;

        currentIndex = getIndex(currentIndex,size);

        int id = list.get(currentIndex).getId();

        play(getPath(id,  currentType));


    }

    public void doRewind(){

        if(list==null || list.size()==0)return;

        int size = list.size()-1;
        currentIndex--;

        currentIndex = getIndex(currentIndex,size);


        int id = list.get(currentIndex).getId();

        play(getPath(id,  currentType));

    }

    public void pause() {

        if (mediaPlayer!=null){
            mediaPlayer.pause();
        }

        sendMessenge(ACTION_PAUSE,null);

    }

    private void initPlayer(){

        AiresPlayerApp app = (AiresPlayerApp) getApplication();

        if( app.getListMusic()!=null && app.getListMusic().size()>0){

            play(0,Media.MUSIC.getTypeMedia());

            sendMessenge(ACTION_INIT,app.getListMusic().get(0).getId()+"");
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
    public void onPrepared(MediaPlayer mp) {
        sendMessenge(ACTION_PREPARED,mediaPlayer.getDuration()+"");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendMessenge(ACTION_COMPLETE,null);
    }

    private int getIdByType(int index, String type){

        AiresPlayerApp app = (AiresPlayerApp) getApplication();

        if(Media.MUSIC.getTypeMedia().equals(type)){

            return app.getListMusic().get(index).getId();

        }else  if(Media.ALBUM.getTypeMedia().equals(type)){

            return app.getListAlbum().get(index).getId();

        }else  if(Media.ARTIST.getTypeMedia().equals(type)){

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

    private String getPath(int id,String type){


        if(Media.MUSIC.getTypeMedia().equals(type)){

            return AudioUtils.getMedia(this,id).getPath();

        }else  {

           return  AudioUtils.getMedia(this,list.get(0).getId()).getPath();

        }


    }

    public List<ItemListTwoLines> getPlayList(){

        return list;
    }

    public void reproduceNext(ItemListTwoLines item){

        if(( currentIndex+1)<=list.size()){

            if(list.get(currentIndex+1).getId()!=item.getId()){
                list.add(currentIndex+1,item);
            }
        }

    }
    public void addToQueue(ItemListTwoLines item){

         list.add(item);
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

    private void showNotification(int id) {

        com.airesplayer.model.Media media = AudioUtils.getMedia(this, id);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,   PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.status_bar_expanded);

        if(Utils.uriExist(this,media.getAlbumArt())) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(media.getAlbumArt()));
                views.setImageViewBitmap(R.id.status_bar_album_art,bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            views.setImageViewResource(R.id.status_bar_album_art,R.drawable.ic_music_note_white_48dp);
            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.color.accent_dark);

        }

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
            views.setImageViewResource(R.id.status_bar_play,R.drawable.ic_pause_white_36dp);
        }else{
            views.setImageViewResource(R.id.status_bar_play,R.drawable.ic_play_arrow_white_36dp);
        }

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);
        views.setOnClickPendingIntent(R.id.status_bar_next, pendingNextIntent);
        views.setOnClickPendingIntent(R.id.status_bar_prev, pendingPrevIntent);
        views.setOnClickPendingIntent(R.id.close, pendingCloseIntent);

        views.setTextViewText(R.id.status_bar_track_name, media.getTitle());
        views.setTextViewText(R.id.status_bar_artist_name, media.getArtist());


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
        mNotifyManager.cancel(1);

    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action.equals(ACTION_PLAY_NOTI)){
                doContinue();
                showNotification(list.get(currentIndex).getId());
            }else if(action.equals(ACTION_NEXT_NOTI)){
                doForward();
                showNotification(list.get(currentIndex).getId());
            }else if(action.equals(ACTION_PREV_NOTI)){
                doRewind();
                showNotification(list.get(currentIndex).getId());
            }else if(action.equals(ACTION_CLOSE_NOTI)){
                cancelNotification();
            }

        }

    }
}
