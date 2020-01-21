package com.krishna.workingwithmultipletable.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

import android.provider.MediaStore;

public class RealPathUtil {

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
    	 String result;
 	    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
 	    if (cursor == null) { // Source is Dropbox or other similar local file path
 	        result = uri.getPath();
 	    } else { 
 	        cursor.moveToFirst(); 
 	        int idx = cursor.getColumnIndex("_data"); 
 	        result = cursor.getString(idx);
 	        cursor.close();
 	    }
 	    return result;
    }
    
    
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
          String[] proj = { MediaStore.Images.Media.DATA };
          String result = null;
           
          CursorLoader cursorLoader = new CursorLoader(
                  context, 
            contentUri, proj, null, null, null);        
          Cursor cursor = cursorLoader.loadInBackground();
          
          if(cursor != null){
           int column_index = 
             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           cursor.moveToFirst();
           result = cursor.getString(column_index);
          }
          return result;  
    }
    
    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
               String[] proj = { MediaStore.Images.Media.DATA };
               Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
               int column_index
          = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
               cursor.moveToFirst();
               return cursor.getString(column_index);
    }
}