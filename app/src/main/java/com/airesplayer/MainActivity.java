package com.airesplayer;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.airesplayer.fragment.AlbumFragment;
import com.airesplayer.fragment.ArtistFragment;
import com.airesplayer.fragment.ItemListTwoLines;
import com.airesplayer.fragment.MusicFragment;
import com.airesplayer.fragment.PlayerFragment;
import com.airesplayer.util.AudioUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener {


    private ViewPager mViewPager;

    AiresPlayerApp app;

    SlidingUpPanelLayout slidingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onResume() {
        super.onResume();


        app = (AiresPlayerApp) getApplication();

        app.init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);

        slidingPanel.addPanelSlideListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if( app.getListMusic()!=null &&  app.getListMusic().size()>0) {
            int id =app.getListMusic().get(0).getId();
            Util.sendMessenge(this, PlayerService.ACTION_INIT, id + "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel,
                                    SlidingUpPanelLayout.PanelState previousState,
                                    SlidingUpPanelLayout.PanelState newState) {

        if(SlidingUpPanelLayout.PanelState.COLLAPSED==newState){
            Util.sendMessenge(this, PlayerService.PANEL_STATE_COLLAPSED,  newState.toString());
        }else if(SlidingUpPanelLayout.PanelState.EXPANDED==newState){
            Util.sendMessenge(this, PlayerService.PANEL_STATE_EXPANDED,   newState.toString());
        }


    }

    public SlidingUpPanelLayout.PanelState getSlideState(){

        return slidingPanel.getPanelState();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return MusicFragment.newInstance(app.getListMusic());
                case 1:
                    return AlbumFragment.newInstance(app.getListAlbum());
                case 2:
                    return ArtistFragment.newInstance(app.getListArtist());

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
    }
}