/********************************************
 @author zn
 time:2017.03.16
 ע�ᣬ�����¼�ӿ�
 *********************************************/
package com.zn.dao;

public interface InfoDao {
	public void insertInfo(Info info);//ע��
	public String login(String username,String password);//��¼
	public long qryTableLine( );//��������
	public  String qryName(String id);//����id������
	public void DeleteAll();//���ڹ���ɾ��������Ϣ
	public int qryId(String name);//�������ֲ�Id
}
