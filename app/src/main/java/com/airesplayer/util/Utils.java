package com.airesplayer.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.airesplayer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by saulo on 15/04/2016.
 */
public class Utils {

    public static boolean isInt(String str){

        try{
            Integer.parseInt(str);

            return true;
        }catch (Exception e){
            return false;
        }


    }


    public static boolean isEmpty(String str){

        if(str==null || "".equals(str))return true;

          return false;


    }

    public static boolean isScreenLarge(Context context){

        if((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return true;
        }else {
            return false;
        }
    }

    public static PopupMenu getPopUpMenu(Context context,View v){

        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_card, popup.getMenu());

        return popup;
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

    public static boolean emptyList(List l){

        if(l==null || l.size()==0) return true;

        return false;
    }
}
