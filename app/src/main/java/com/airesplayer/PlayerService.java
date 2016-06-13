package com.airesplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.airesplayer.fragment.ItemListTwoLines;
import com.airesplayer.util.AudioUtils;

import java.io.IOException;
import java.util.List;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY           = "action.PLAY";
    public static final String ACTION_PAUSE          = "action.PAUSE";
    public static final String ACTION_INIT           = "action.CONTINUE";
    public static final String ACTION_COMPLETE       = "action.COMPLETE";
    public static final String ACTION_PREPARED       = "action.PREPARED";
    public static final String ACTION_CHANGE_CENTRAL = "action.CHANGE_CENTRAL";
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
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
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
            sendMessenge(ACTION_CHANGE_CENTRAL, list.get(currentIndex).getId() + "");
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


        } catch (IOException e) {
            //Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);
            e.printStackTrace();
        }

    }

    public void doForward(){

        if(list==null || list.size()==0)return;

        int size = list.size();

        currentIndex=(++currentIndex)%size;

        int id = list.get(currentIndex).getId();

        play(getPath(id,  currentType));


    }

    public void doRewind(){

        if(list==null || list.size()==0)return;

        int size = list.size();

        currentIndex=(--currentIndex)%size;

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
        mediaPlayer.seekTo(i);
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {

        if(mediaPlayer==null)return false;

        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
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

    public int getCurrentIndex(){
        return currentIndex;
    }

    public void sendMessenge(String action, String data){

        Util.sendMessenge(this, action,  data);

    }


}
