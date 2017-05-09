/********************************************
 @author zn
 time:2017.03.16
 信息实体类
 *********************************************/
package com.zn.dao;

public class Info {
	
	private int id;
	private String username;
	private String password;
	private String photopath;
	public Info(){
		
	}
	public Info(String Id,String Password,String Photopath){
		id=Integer.parseInt(Id);
		password=Password;
		photopath=Photopath;
	}
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhotopath() {
		return photopath;
	}
	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}
	
}
