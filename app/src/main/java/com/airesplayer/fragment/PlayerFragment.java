package com.airesplayer.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airesplayer.AiresPlayerApp;
import com.airesplayer.MainActivity;
import com.airesplayer.PlayerService;
import com.airesplayer.R;

import com.airesplayer.dao.ImageDAO;
import com.airesplayer.model.Image;
import com.airesplayer.model.Media;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.File;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by saulo on 22/04/2016.
 */
public class PlayerFragment  extends Fragment implements  View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private String centralCover;

    private TextView title,subtitle;
    private ImageView albumArtUp, playQueue;
    private FloatingActionButton playPauseDown;
    private TextView played,left;
    private SeekBar seek;

    QueueFragment queueFragment;

    RelativeLayout central;

    AiresPlayerApp app;

    Handler mHandler = new Handler();
    Runnable updatePlay = new Runnable() {

        @Override
        public void run() {

            played.setText(Utils.humanReadableTime(app.getService().getCurrentPosition()));
            left.setText(Utils.humanReadableTime(app.getService().getDuration()- app.getService().getCurrentPosition()));

            seek.setProgress(app.getService().getCurrentPosition());

            if(app.getService().isPlaying())
                mHandler.postDelayed(this,300);

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);
        return inflater.inflate(R.layout.player, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

    }


    public void initReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_PLAY);
        intentFilter.addAction(PlayerService.ACTION_PAUSE);
        intentFilter.addAction(PlayerService.ACTION_INIT);
        intentFilter.addAction(PlayerService.ACTION_COMPLETE);
        intentFilter.addAction(PlayerService.ACTION_PREPARED);
        intentFilter.addAction(PlayerService.PANEL_STATE_EXPANDED);
        intentFilter.addAction(PlayerService.PANEL_STATE_COLLAPSED);
        intentFilter.addAction(PlayerService.ACTION_SELECTED);
        getActivity().registerReceiver(new ServiceReceiver(), intentFilter);

    }

    public void initCentralImg(Integer id,String img,boolean animation) {

        if (getSlideState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            playQueue.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        } else {
            playQueue.setImageResource(R.drawable.ic_album_black_36dp);
        }

        if(img!=null && Utils.uriExist(getContext(),img))
             centralCover = img;
        else
            centralCover = findLocalImg(id);

        final ImageView iv = new ImageView(getContext());

        iv.setScaleType(ImageView.ScaleType.CENTER);
        iv.setBackgroundResource(R.drawable.ic_music_note_white_48dp);
        central.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.accent_light));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (centralCover != null) {

            iv.setScaleType(ImageView.ScaleType.FIT_XY);

            layoutParams = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);


            Picasso.with(getActivity())
                    .load(centralCover)
                    .placeholder(R.drawable.ic_music_note_white_48dp)
                    .into(iv);

        }else{
            layoutParams.height=150;
            layoutParams.width=150;

        }

        iv.setLayoutParams(layoutParams);

        if (!animation) {
            central.addView(iv);
        } else {

            iv.post(new Runnable() {
                @Override
                public void run() {

                    if(queueFragment!=null) {

                        int cx=(int)(central.getX() + central.getWidth()  / 2);
                        int cy=(int)(central.getY() + central.getHeight() / 2);

                            Animator unreveal = queueFragment.prepareUnrevealAnimator(cx, cy);
                            unreveal.start();
                            unreveal.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    central.removeAllViews();
                                    central.addView(iv);
                                }
                            });


                    }else{
                        central.removeAllViews();
                        central.addView(iv);
                    }

                }
            });

         }

    }

    public void initCentralList(List<ItemListTwoLines> list){

        if(list==null) return;

        playQueue.setImageResource(R.drawable.ic_queue_music_black_36dp);


        central.removeAllViews();

        int cx=(int)(central.getX() + central.getWidth()  / 2);
        int cy=(int)(central.getY() + central.getHeight() / 2);

        queueFragment=QueueFragment.newInstance(list,cx,cy);

        getFragmentManager().beginTransaction().add(R.id.central, queueFragment).commit();


    }

    public void init(){

        app = (AiresPlayerApp) getActivity().getApplication();

        ImageButton rewind,forwad;

        albumArtUp = (ImageView) getView().findViewById(R.id.album_art_up);
        title = (TextView) getView().findViewById(R.id.title);
        subtitle = (TextView) getView().findViewById(R.id.subtitle);
        playQueue = (ImageView) getView().findViewById(R.id.play_pause_up);

        central = (RelativeLayout) getView().findViewById(R.id.central);

        rewind    =(ImageButton)getView().findViewById(R.id.rewind);
        playPauseDown =(FloatingActionButton)getView().findViewById(R.id.play_pause_down);
        forwad    =(ImageButton)getView().findViewById(R.id.forward);
        played =(TextView)getView().findViewById(R.id.played);
        left   =(TextView)getView().findViewById(R.id.left);
        seek = (SeekBar) getView().findViewById(R.id.seek);



        rewind.setOnClickListener(this);
        playQueue.setOnClickListener(this);
        playPauseDown.setOnClickListener(this);
        forwad.setOnClickListener(this);

        seek.setOnSeekBarChangeListener(this);

        initReceiver();

        initCentralImg(null,centralCover,false);




    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.rewind:          doRewind();   break;
            case R.id.play_pause_up:   doPlayPauseUp();break;
            case R.id.play_pause_down: doPlayPause();break;
            case R.id.forward:         doForward();  break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


    public void seekTo(int progress){

        app.getService().seekTo(progress);
    }

    public void doPlayPauseUp(){

        if(getSlideState()== SlidingUpPanelLayout.PanelState.EXPANDED) {

            changeCentral();


        }else{
            app.getService().doContinue();
        }


    }

    public void doPlayPause(){

      app.getService().doContinue();

    }

    public void doForward(){
        app.getService().doForward();
    }

    public void doRewind(){
        app.getService().doRewind();
    }

    private void initCard(Media media){

        if(media==null)return;

        title.setText(media.getTitle());
        subtitle.setText(media.getArtist());

        if(media.getArtAlbum()!=null && !"".equals(media.getArtAlbum())){
            Utils.loadImage(getActivity(),media.getAlbumArt(),albumArtUp);

        }

        initCentralImg(media.getArtistId(),media.getArtAlbum(),false);
    }

    public void stopUpdate(){

        mHandler.removeCallbacks(updatePlay);

    }

    public void startUpdate(){
        mHandler.post(updatePlay);
    }

    public SlidingUpPanelLayout.PanelState getSlideState(){

        return ((MainActivity)getActivity()).getSlideState();
    }

    private void  changeCentral(){

        String status = (String) central.getTag();

        if(status==null || "COVER".equals(status)){
            central.setTag("LIST");
            initCentralList(app.getService().getPlayList());

        }else{
            central.setTag("COVER");
            initCentralImg(null,centralCover,true);

        }


    }

    private void initPlayQueue(){

        if(getSlideState()== SlidingUpPanelLayout.PanelState.EXPANDED) {

            String status = (String) central.getTag();

            if(status==null || "COVER".equals(status)){
                playQueue.setImageResource(R.drawable.ic_album_black_36dp);

            }else{
                playQueue.setImageResource(R.drawable.ic_queue_music_black_36dp);

            }

        }else {

            if(app.getService().isPlaying()){
                playQueue.setImageResource(R.drawable.ic_pause_black_36dp);
            }else{
                playQueue.setImageResource(R.drawable.ic_play_arrow_black_36dp);
            }


        }

    }

    private String findLocalImg(Integer id){

        if(id==null)return null;

        List<Image> list = new ImageDAO(getContext()).read(id);

        if (list != null && list.size() > 0)
            return  list.get(0).getUrl();
        else
           return null;

    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action.equals(PlayerService.ACTION_PLAY)){

                initPlayQueue();
                playPauseDown.setImageResource(R.drawable.ic_pause_white_36dp);

                startUpdate();

            }else if(action.equals(PlayerService.ACTION_PAUSE)){

                initPlayQueue();
                playPauseDown.setImageResource(R.drawable.ic_play_arrow_white_36dp);

                stopUpdate();

            }else if(action.equals(PlayerService.ACTION_INIT)){

                String id= intent.getStringExtra("DATA");

                Media media = AudioUtils.getMedia(getContext(), Integer.parseInt(id));

                initCard(media);


            }else if(action.equals(PlayerService.ACTION_COMPLETE)){

               stopUpdate();

            }else if(action.equals(PlayerService.ACTION_PREPARED)){

                String duration= intent.getStringExtra("DATA");

                seek.setProgress(0);
                seek.setMax(Integer.parseInt(duration));

            }else if(action.equals(PlayerService.PANEL_STATE_COLLAPSED) ||
                     action.equals(PlayerService.PANEL_STATE_EXPANDED)){

                initPlayQueue();

            }else if (action.equals(PlayerService.ACTION_SELECTED)) {

                if(queueFragment!=null)
                   queueFragment.update();

            }


        }

    }


}
