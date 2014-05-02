package com.wangzhenzhe.artdaily.sqlite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wangzhenzhe.library.TouchView.InputStreamWrapper;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ArtDailyDB {

	private ArtDailyDBHelper dbHelper;
	private SQLiteDatabase	db;
	
	public ArtDailyDB(Context context) {
		dbHelper = new ArtDailyDBHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
    /** 
     * add artists 
     * @param Artists 
     */  
    public void addArtist(List<Artist> artists) {  
        db.beginTransaction();  //开始事务  
        try {  
            for (Artist person : artists) {  
                db.execSQL("INSERT INTO ARTIST VALUES(null, ?, ?, ?)", new Object[]{person.name, person.age, person.info});  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务

        }
    }  
    
    public void addArtImage(String path)
    {
    	ContentValues values = new ContentValues();
    	
        Bitmap bmp = null;
        try {
        	File file = new File(path);
        	FileInputStream fis = new FileInputStream(file);
            InputStreamWrapper bis = new InputStreamWrapper(fis, 8192, file.length());
            bmp = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    	
        db.beginTransaction();  //开始事务  
        try {  
        	values.put("name", "测试用");
        	values.put("artist_id", 0);
        	values.put("info", "这是测试用图片");
        	
        	if (null != bmp)
        	{
        		values.put("image", bmpToByteArray(bmp));
        		db.insert("ARTIMAGE", null, values);
        	}
        	
            db.setTransactionSuccessful();  //设置事务成功完成  	
        } finally {  
            db.endTransaction();    //结束事务
        }
        
    }
    
    /** 
     * add art image 
     * @param art image 
     */  
    public void addArtImage(List<ArtImage> images) {  
    	
    	ContentValues values = new ContentValues();
    	
        db.beginTransaction();  //开始事务  
        try {
            for (ArtImage artimage : images) {
            	values.put("name", artimage.name);
            	values.put("artist_id", artimage.artist._id);
            	values.put("info", artimage.info);
            	
            	if (artimage.bmp != null)
            	{
            		values.put("image", bmpToByteArray(artimage.bmp));
            		db.insert("ARTIMAGE", null, values);
            	}
            	  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  	
        } finally {  
            db.endTransaction();    //结束事务
        }
    }
    
    public int getArtImageCount()
    {
    	int count = 0;
    	
    	String[] columns = {"_id", "name"};
    	Cursor c = db.query("ARTIMAGE", columns, null, null, null, null, null);
    	
    	while(c.moveToNext())
 	   {
 		   count++;
 	   }
        c.close();
        return count;
    }
    
    public Bitmap loadArtImage(String sID)
    {
    	Bitmap bmp = null;
    	String[] columns = {"_id", "name", "image"};
    	String[] params = {sID};
    	Cursor c = db.query("ARTIMAGE", columns, "_id=?", params, null, null, null);
    	
        c.moveToLast();  
        if (c.isLast()) {    
               bmp = cursorToBmp(c, c.getColumnIndex("image"));    
        }  
        c.close();  
        return bmp;
    }
    
   public ArrayList<String> loadArtImageIDs()
   {
	   ArrayList<String> items = new ArrayList<String>();
	   
	   String[] columns = {"_id", "name"};
	   Cursor c = db.query("ARTIMAGE", columns, null, null, null, null, null);
	   while(c.moveToNext())
	   {
		   items.add(String.valueOf(c.getInt(c.getColumnIndex("_id"))));
	   }
       c.close();
       return items;
   }
   
   // Bitmap to byte[]  
   public byte[] bmpToByteArray(Bitmap bmp) {  
        // Default size is 32 bytes  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        try {  
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
            bos.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return bos.toByteArray();  
    }
   
public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
   // Cursor to bitmap  
   public Bitmap cursorToBmp(Cursor c, int columnIndex) {  
 
       byte[] data = c.getBlob(columnIndex);  
       try {  
           return BitmapFactory.decodeByteArray(data, 0, data.length);  
       } catch (Exception e) {  
           return null;  
       }  
   }  
   
   public void close()
   {
	   db.close();
   }
}
