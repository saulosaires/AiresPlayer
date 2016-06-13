package com.airesplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.airesplayer.util.AudioUtils;

public class EditTagActivity extends AppCompatActivity {

    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tag_editor);
        setContentView(R.layout.activity_edit_tag);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);


        id=getIntent().getExtras().getInt("id");

        EditText name   = (EditText) findViewById(R.id.name);
        EditText album  = (EditText) findViewById(R.id.album);
        EditText artist = (EditText) findViewById(R.id.artist);
        EditText track  = (EditText) findViewById(R.id.track);
        EditText year   = (EditText) findViewById(R.id.year);

        com.airesplayer.model.Media  media = AudioUtils.getMedia(this, id);

        name.setText(media.getTitle());
        album.setText(media.getAlbum());
        artist.setText(media.getArtist());
        track.setText(media.getTrack()+"");
        year.setText(media.getYear()+"");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {

        String name  =((EditText) findViewById(R.id.name)).getText().toString();
        String album =((EditText) findViewById(R.id.album)).getText().toString();
        String artist=((EditText) findViewById(R.id.artist)).getText().toString();
        String track =((EditText) findViewById(R.id.track)).getText().toString();
        String year  =((EditText) findViewById(R.id.year)).getText().toString();

        AudioUtils.updateMedia(this,id,name,album,artist,track,year);

        startActivity(new Intent(this, MainActivity.class));
    }
}
