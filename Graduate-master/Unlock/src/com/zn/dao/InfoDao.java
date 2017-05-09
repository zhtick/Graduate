/********************************************
 @author zn
 time:2017.03.16
 注册，密码登录接口
 *********************************************/
package com.zn.dao;

public interface InfoDao {
	public void insertInfo(Info info);//注册
	public String login(String username,String password);//登录
	public long qryTableLine( );//返回行数
	public  String qryName(String id);//根据id查名字
	public void DeleteAll();//便于管理，删除所有信息
	public int qryId(String name);//根据名字查Id
}
