package com.zn.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbUtil extends SQLiteOpenHelper{
	
	static String name="user.db";  
    static int dbVersion=1;  
    public DbUtil(Context context) {  
        super(context, name, null, dbVersion);  
    }  
    //只在创建的时候用一次   
    @Override
	public void onCreate(SQLiteDatabase db) {  
        String sql="create table user(id integer primary key ,username varchar(20),password varchar(20),photopath varchar(20))";  
        db.execSQL(sql);  
    }  
    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
  
    }  

}
