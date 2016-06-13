package com.airesplayer.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "AIRESPLAYER_DB.db";

    public static final String TABLE_IMAGE = "TABLE_IMAGE";

    public static final String KEY_IMAGE_URL= "URL";
    public static final String KEY_IMAGE_ID= "ID";
    public static final String KEY_IMAGE_SPOTIFY_ID= "TITLE";
    public static final String KEY_IMAGE_HEIGHT = "HEIGHT";
    public static final String KEY_IMAGE_WIDTH = "WIDTH";

    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE "+ TABLE_IMAGE+"("+
                                                                KEY_IMAGE_URL+" VARCHAR(500),  "+
                                                                KEY_IMAGE_ID+" INTEGER,  "+
                                                                KEY_IMAGE_SPOTIFY_ID+" VARCHAR(100),  "+
                                                                KEY_IMAGE_HEIGHT+" INTEGER,"+
                                                                KEY_IMAGE_WIDTH+" INTEGER)";


    public DatabaseHelper(Context context) {

	  super(context, DATABASE_NAME, null, DATABASE_VERSION);
    
  }
  
  
  @Override
  public void onCreate(SQLiteDatabase db) {

      db.execSQL(CREATE_TABLE_IMAGE);


  }
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  	
  }  
  
  public SQLiteDatabase getDatabase() {
    return this.getReadableDatabase();
  }



}