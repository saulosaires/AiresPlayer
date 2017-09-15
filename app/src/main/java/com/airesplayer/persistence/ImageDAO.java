package com.airesplayer.persistence;

import com.airesplayer.model.Image;

import io.realm.Realm;

/**
 * Created by Saulo Aires on 21/08/2017.
 */

public class ImageDAO {


    public void persist(final Image img){

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Image image = realm.createObject(Image.class);
                image.setMediaId(img.getMediaId());
                image.setImageUrl(img.getImageUrl());


            }
        });

    }

    public Image read(int id){

        Realm realm = Realm.getDefaultInstance();

        return realm.where(Image.class).equalTo("mediaId", id).findFirst();


    }

}
