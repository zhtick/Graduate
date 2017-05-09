/********************************************
 @author zn
 time:2017.03.16
 注册，密码登录具体实现
 *********************************************/
package com.zn.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.zn.utils.DbUtil;

public class InfoDaoImpl  implements InfoDao{
	private DbUtil dbHelper;  
    public InfoDaoImpl(Context context){  
        dbHelper=new DbUtil(context);  
    }  
	@Override
	public void insertInfo(Info info) {
		// TODO Auto-generated method stub
		 SQLiteDatabase sdb=dbHelper.getReadableDatabase();  
	        String sql="insert into user(id,username,password,photopath) values(?,?,?,?)";  
	        Object obj[]={info.getId(),info.getUsername(),info.getPassword(),info.getPhotopath()};  
	        sdb.execSQL(sql, obj);   
	}

	@Override
	public String login(String username, String password) {
		// TODO Auto-generated method stub
		 SQLiteDatabase sdb=dbHelper.getReadableDatabase();  
		 String sql="select * from user where username=? and password=?";  
	        Cursor cursor=sdb.rawQuery(sql, new String[]{username,password}); //游标        
	        if(cursor.moveToFirst()==true){  
	            cursor.close();  
	            return "True";  
	        }  
	        return "false";  
		
	}
	@Override
	public long qryTableLine() {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb=dbHelper.getReadableDatabase(); 
		String sql="select count(*) from user";
		SQLiteStatement statement =sdb.compileStatement(sql);
		long count = statement.simpleQueryForLong();
	    return count;
		
	}
	@Override
	public String qryName(String id) {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb=dbHelper.getReadableDatabase(); 
		String sql="select username from user where id=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{id});
		String name="null";
		while (cursor.moveToNext()) { 
		name=cursor.getString(cursor.getColumnIndex("username"));
		}
		return name;
	}
	@Override
	public void DeleteAll() {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb=dbHelper.getReadableDatabase(); 
		
		String sql="delete  from user";
		
		sdb.execSQL(sql);
		
	}
	@Override
	public int qryId(String name) {
		// TODO Auto-generated method stub
		SQLiteDatabase sdb=dbHelper.getReadableDatabase(); 
		String sql="select id from user where username=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{name});
		int num=0;
		while (cursor.moveToNext()) { 
		num=cursor.getInt(cursor.getColumnIndex("id"));
		}
		return num;
	}


}
