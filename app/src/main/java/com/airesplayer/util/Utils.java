package com.airesplayer.util;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.airesplayer.R;

/**
 * Created by saulo on 15/04/2016.
 */
public class Utils {

    public static boolean uriExist(Context context,String uri){

        ImageView iv = new ImageView(context);

        iv.setImageURI(Uri.parse(uri));
        if(iv.getDrawable() == null){
          return false;
        }else{
           return true;
        }

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
}
