package com.wangzhenzhe.artdaily.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ArtDailyDBHelper extends SQLiteOpenHelper {

	private final static String dbName = "artdaily.db";
	private final static int db_version = 1;

	public ArtDailyDBHelper(Context context)
	{
		super(context, dbName, null, db_version);
	}
	
	public ArtDailyDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public ArtDailyDBHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("CREATE TABLE IF NOT EXISTS ARTIST" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS ARTIMAGE" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, artist_id INTEGER, info TEXT, image BLOB)");
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
