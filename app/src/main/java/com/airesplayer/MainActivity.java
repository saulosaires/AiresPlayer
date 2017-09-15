package com.airesplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;


import com.airesplayer.fragment.AlbumFragment;
import com.airesplayer.fragment.ArtistFragment;
import com.airesplayer.fragment.EmptyFragment;
import com.airesplayer.fragment.MusicFragment;
import com.airesplayer.fragment.PlayerFragment;
import com.airesplayer.util.AudioUtils;
import com.airesplayer.util.Utils;



public class MainActivity extends AppCompatActivity {


    private final int REQUEST_CODE_ASK_PERMISSIONS=11;
    private ViewPager mViewPager;

    AiresPlayerApp app;

    ServiceReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        app = (AiresPlayerApp) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        receiver = new ServiceReceiver();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);

            }
        }else{
            init();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(receiver);
            app.cancelNotification();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init(){

        app.setListMusic(AudioUtils.getAll(this));
        app.setListAlbum(AudioUtils.getAlbuns(this));
        app.setListArtist(AudioUtils.getArtist(this));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.SERVICE_STARTED);

        registerReceiver(receiver, intentFilter);

        app.bindService();


    }

    private void initUI(){

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    init();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(PlayerService.SERVICE_STARTED)) {

                initUI();
            }


        }


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return getMusicFragment();
                case 1:
                    return getAlbumFragment();
                case 2:
                    return getArtistFragment();

            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.music);
                case 1:
                    return getResources().getString(R.string.album);
                case 2:
                    return getResources().getString(R.string.artist);
            }
            return null;
        }


        private Fragment getMusicFragment(){

            if(Utils.emptyList(app.getListMusic())){
                return EmptyFragment.newInstance();
            }else{
                return MusicFragment.newInstance(app.getListMusic());
            }
        }

        private Fragment getAlbumFragment(){

            if(Utils.emptyList(app.getListAlbum())){
                return EmptyFragment.newInstance();
            }else{
                return AlbumFragment.newInstance(app.getListAlbum());
            }
        }

        private Fragment getArtistFragment(){

            if(Utils.emptyList(app.getListArtist())){
                return EmptyFragment.newInstance();
            }else{
                return  ArtistFragment.newInstance(app.getListArtist());
            }
        }




    }
}
