package com.airesplayer.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.airesplayer.AiresPlayerApp;

import com.airesplayer.MainActivity;
import com.airesplayer.PlayerService;
import com.airesplayer.R;
import com.airesplayer.model.ItemMedia;
import com.airesplayer.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class PlayerFragment  extends Fragment implements  View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private FloatingActionButton playPause,queue;
    private TextView played,left,songName;
    private SeekBar seek;

    ServiceReceiver receiver;

    AiresPlayerApp app;

    Handler mHandler = new Handler();
    Runnable updatePlay = new Runnable() {

        @Override
        public void run() {

            played.setText(Utils.humanReadableTime(app.getCurrentPosition()));
            left.setText(Utils.humanReadableTime(app.getDuration()- app.getCurrentPosition()));

            int pp=app.getCurrentPosition();
            seek.setProgress(pp);

            if(app.isPlaying())
                mHandler.postDelayed(this,300);

        }
    };


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

    @Override
    public void onStart() {
        super.onStart();

        updateCard();
        updateSeekBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(receiver);
    }

    public void init(){

        app = (AiresPlayerApp) getActivity().getApplication();

        ImageButton rewind,forwad;



        rewind        =(ImageButton)getView().findViewById(R.id.rewind);
        playPause     =(FloatingActionButton)getView().findViewById(R.id.play_pause_down);
        queue         =(FloatingActionButton)getView().findViewById(R.id.queue);
        forwad        =(ImageButton)getView().findViewById(R.id.forward);
        played        =(TextView)getView().findViewById(R.id.played);
        left          =(TextView)getView().findViewById(R.id.left);
        songName      =(TextView)getView().findViewById(R.id.songName);
        seek          =(SeekBar) getView().findViewById(R.id.seek);

        initReceiver();

        playPause.setOnClickListener(this);
        queue.setOnClickListener(this);
        rewind.setOnClickListener(this);
        forwad.setOnClickListener(this);

        seek.setOnSeekBarChangeListener(this);


    }

    public void initReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_PLAY);
        intentFilter.addAction(PlayerService.ACTION_COMPLETE);

        receiver = new ServiceReceiver();

        getActivity().registerReceiver(receiver, intentFilter);

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.rewind:          doRewind();   break;
            case R.id.play_pause_down: doPlayPause();break;
            case R.id.queue:           showPlayList();break;
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

    public void  showPlayList(){

        ArrayList<String> displayValues=new ArrayList<>();

        List<ItemMedia> list = app.getPlayList();

        if(Utils.emptyList(list))return;

        for(ItemMedia item :list){
            displayValues.add(item.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,displayValues);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.play_list);
        builder.setSingleChoiceItems(adapter, app.getCurrentIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

             app.play(which);

             dialog.dismiss();
            }
        });


        builder.show();

    }

    public void seekTo(int progress){

        app.seekTo(progress);
    }

    public void doPlayPause(){

      app.doContinue();

      updateCard();

    }

    public void doForward(){

        app.doForward();
        updateCard();

    }

    public void doRewind(){

        app.doRewind();
        updateCard();

    }

    public void stopUpdate(){

        mHandler.removeCallbacks(updatePlay);

    }

    private void updateCard(){

        if(app.isPlaying()){
            startUpdate();
            playPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_play_arrow_white_36dp));

        }  else{
            stopUpdate();
            playPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_white_36dp));
        }

        songName.setText(app.getName());

    }

    private void updateSeekBar(){
        seek.setProgress(0);
        seek.setMax(app.getDuration());
    }

    public void startUpdate(){
        mHandler.post(updatePlay);
    }


        private class ServiceReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if (action.equals(PlayerService.ACTION_PLAY)) {

                    updateCard();
                    updateSeekBar();

                } else if (action.equals(PlayerService.ACTION_COMPLETE)) {


                }


            }


        }


    }
