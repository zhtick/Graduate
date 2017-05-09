package com.zn.face;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.dao.InfoDao;
import com.zn.dao.InfoDaoImpl;
import com.zn.unlock.R;

public class SuccessActivity extends Activity{
	private TextView text;
	private ImageView img;
	private InfoDao dao;
	private String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suc);
		text=(TextView)findViewById(R.id.suc_name);
		img=(ImageView)findViewById(R.id.img);
		Intent intent=getIntent();
		String name=intent.getStringExtra("name");
		text.setText(name);
		dao=new InfoDaoImpl(this);
		int id=dao.qryId(name);
		path="/sdcard/FaceDetect/Face/"+id+".jpg";
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(path, options);
		img.setImageBitmap(bm);
		
	}
	
}
