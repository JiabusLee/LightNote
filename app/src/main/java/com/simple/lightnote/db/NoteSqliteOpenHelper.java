package com.simple.lightnote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteSqliteOpenHelper extends SQLiteOpenHelper{

	static String t_note="CREATE TABLE note (\n" +
			"\t_id INTEGER PRIMARY KEY autoincrement,\n" +
			"\tnoteTitle VARCHAR (20) NULL,\n" +
			"\tnoteContent VARCHAR (500) NOT NULL,\n" +
			"\tcreateTime VARCHAR (32) NOT NULL,\n" +
			"\tlastModifyTime VARCHAR (32) NOT NULL,\n" +
			"\tnoteMd5 VARCHAR (20) NOT NULL,\n" +
			"\tnoteType VARCHAR (10)\n" +
			"); \n";

	public NoteSqliteOpenHelper(Context context, String name, CursorFactory factory,  
            int version) {
		super(context, name, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(t_note);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	static SQLiteOpenHelper helper=null;
	public static SQLiteOpenHelper getInstance(Context context,String tableName,int version){
		if(helper==null)
			helper=new NoteSqliteOpenHelper(context, tableName, null, version);
		return helper;
	}
}
