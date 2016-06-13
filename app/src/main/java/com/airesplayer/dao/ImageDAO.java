package com.airesplayer.dao;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.airesplayer.model.Image;

import java.util.ArrayList;
import java.util.List;


public class ImageDAO {

	private DatabaseHelper helper;

  public ImageDAO(Context context) {
	  this.helper = new DatabaseHelper(context);

  }

 public Image fromCursor(Cursor c){

	String url        = c.getString(c.getColumnIndex(DatabaseHelper.KEY_IMAGE_URL));
    String spotifyId  = c.getString(c.getColumnIndex(DatabaseHelper.KEY_IMAGE_SPOTIFY_ID));
	Integer id        = c.getInt(c.getColumnIndex(DatabaseHelper.KEY_IMAGE_ID));
	Integer width     = c.getInt(c.getColumnIndex(DatabaseHelper.KEY_IMAGE_WIDTH));
	Integer height    = c.getInt(c.getColumnIndex(DatabaseHelper.KEY_IMAGE_HEIGHT));

	return new Image(height, width, url, id, spotifyId);

 }

  public boolean create(Image image){

	  SQLiteDatabase db = helper.getDatabase();

	  if(image==null)return false;

      long todo_id = db.insert(DatabaseHelper.TABLE_IMAGE, null, image.toContentValues());
 
	  if(todo_id==-1)
	    	return false;
	  else
	    	return true;
	  
  }



  public List<Image> read(int id){

	    SQLiteDatabase db = helper.getDatabase();

	    String selectQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_IMAGE + " WHERE "
	            + DatabaseHelper.KEY_IMAGE_ID + " =" +id + " order by "+DatabaseHelper.KEY_IMAGE_HEIGHT+" ASC";

	    Cursor c = db.rawQuery(selectQuery, null);

	    if (c != null)
	        c.moveToFirst();

	    if(c.getCount()==0)return null;

	  List<Image> list = new ArrayList<Image>();

	  while (c.moveToNext()) {
		  list.add(fromCursor(c));
	  }

	  	c.close();

	  return list;

  }


}