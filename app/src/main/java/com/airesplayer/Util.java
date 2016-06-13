package com.airesplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Random;

/**
 * Created by saulo on 17/11/2015.
 */
public class Util {

    public static int randomColor(Context ctx){

        int[] color   = ctx.getResources().getIntArray(R.array.rainbow);

        return color[new Random().nextInt(color.length)];

    }

    public static void loadImage(Context ctx, String uri, ImageView img){

        Picasso.with(ctx)
                .load( (uri))
                .placeholder(R.drawable.ic_music_note_white_24dp)
                .into(img);

    }



    public static String humanReadableTime(long dur) {

        String seconds = String.valueOf((dur % 60000) / 1000);

        String minutes = String.valueOf(dur / 60000);

        if (seconds.length() == 1) {
            return "0" + minutes + ":0" + seconds;
        }else {
            return "0" + minutes + ":" + seconds;
        }

    }


    public static void sendMessenge(Context ctx,String action, String data){

        Intent intent = new Intent();
        intent.setAction(action);

        intent.putExtra("DATA",data);

        ctx.sendBroadcast(intent);
    }

}
