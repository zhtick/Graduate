package com.zn.face;

/********************************************
 @author zn
 time:2017.03.16
 ע��ҳ�棬������ư�������û��������룬��Ƭ�����ע�ᰴť����Ҫ��ͨsql lite�������ݴ洢��ͬʱ����Ƭ�ļ��洢���洢����
 ��Ҫ��һ��:opencvѵ����ͼƬ��Ҫ�ߴ�һ����������Ƭ�ĺú�Ҫ���й�һ�������ٴ洢                                                                             add��2017.03.29
 *********************************************/



import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zn.dao.Info;
import com.zn.dao.InfoDao;
import com.zn.dao.InfoDaoImpl;
import com.zn.unlock.R;
import com.zn.utils.CameraUtil;

public class RegisterActivity extends Activity{
	private CascadeClassifier mJavaDetector;
	private CameraUtil util=new CameraUtil();
	private EditText username,password;
	private ImageView upload;
	private Button register;
	private static String photoPath = "/sdcard/FaceDetect/Face/";// ��Ƭδ������洢��·��
	private static String photoName;// �浽�ֻ��ϵ�����
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));// �ڶ�����������ʱ�ļ����ں��潫�ᱻ�޸�
	private static final int PHOTO_CAPTURE = 0x11;
	private InfoDao dao;//�ӿ�
	private int id;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				mJavaDetector = new CascadeClassifier(
						"/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		File file=new File(photoPath);
		if(!file.exists()){
			file.mkdirs();
		}
		dao=new InfoDaoImpl(this);
		//dao.DeleteAll();
		username=(EditText)findViewById(R.id.username);
		password=(EditText)findViewById(R.id.password);
		upload=(ImageView)findViewById(R.id.upload);
		register=(Button)findViewById(R.id.register);
		System.out.println("1111");
		long count=dao.qryTableLine();
		id=(int) (count+1);
		System.out.println(count);
		
		photoName=photoPath+(count+1)+".jpg";//photoName��id����
		upload.setOnClickListener(new TakePhotoListener());
		register.setOnClickListener(new RegisterListenner());
	}
	@Override
	protected void onResume() {
		super.onResume();
	
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				mLoaderCallback);
	}
	//�����������
	public  class TakePhotoListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			// "/sdcard/AnBo/";
			File file = new File(photoPath);
			if (!file.exists()) { // ���ͼƬ��ŵ��ļ����Ƿ����
				file.mkdir(); // �����ڵĻ� �����ļ���
			}
			File photo = null;
			photo = new File(photoName);
			imageUri = Uri.fromFile(photo);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // �����ͽ��ļ��Ĵ洢��ʽ��uriָ������CameraӦ����
			startActivityForResult(intent, PHOTO_CAPTURE);
		}	
	}
	//ע�ᰴť�ļ�����
	public class RegisterListenner implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String name=username.getText().toString();
			String passwd=password.getText().toString();
			System.out.println(passwd);
			if(name==null || passwd==null ||"".equals(name) || "".equals(passwd)){
				Toast.makeText(RegisterActivity.this, "��������������Ϣ",Toast.LENGTH_LONG).show();
			}
			else{
			String path=photoName;
			Info info=new Info();
			info.setPassword(passwd);
			info.setUsername(name);
			info.setPhotopath(path);
			info.setId(id);
			dao.insertInfo(info);
			Intent intent=new Intent();
			intent.setClass(RegisterActivity.this,AppMainActivity.class);
			startActivity(intent);
			}
		}
		
	}
	//������Ƭ֮��Ļ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		if (resultCode == Activity.RESULT_CANCELED) {
			Intent it = new Intent(getApplicationContext(),
					RegisterActivity.class);
			startActivity(it);
			finish();
			return;
		}
		switch (requestCode) {
		case PHOTO_CAPTURE:
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
				Log.i("�ڴ濨����", "���������ڴ濨");
			} else {

				BitmapFactory.Options op = new BitmapFactory.Options();
				// ����ͼƬ�Ĵ�С
				Bitmap bitMap = null;
					bitMap = BitmapFactory.decodeFile(photoName);
				int width = bitMap.getWidth();
				int height = bitMap.getHeight();
				
				// �������ű���
				float scaleWidth = (float) 0.5;
				float scaleHeight =(float) 0.5;
				// ȡ����Ҫ���ŵ�matrix����
				Matrix matrix = new Matrix();
				matrix.setRotate(270);
				matrix.postScale(scaleWidth, scaleHeight);
				// �õ��µ�ͼƬ
				bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
						matrix, true);
				// canvas.drawBitmap(bitMap, 0, 0, paint)
				// ��ֹ�ڴ����
				op.inSampleSize = 15; // �������Խ��,ͼƬ��СԽС.
				Bitmap pic = null;		
					pic = BitmapFactory.decodeFile(photoName, op);	
				
				//upload.setImageBitmap(bitMap);
				util.writePhoto(bitMap, bitMap.getWidth(), bitMap.getHeight(), photoName);
				new DetectThread().start();
			}
			break;
		default:
			return;
		}
	}
	public class DetectThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			DetectFace();
		}
		
	}
	public void DetectFace() {
		Mat image = Highgui.imread(photoName);
		Size siz=image.size();
		MatOfRect faceDetections = new MatOfRect();
		mJavaDetector.detectMultiScale(image, faceDetections,1.2D, 2, 2, new Size(150.0D, 150.0D), new Size());


		int k = 0;

		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			Mat sub = image.submat(rect);
			Mat mat = new Mat();
			Size size = new Size(100, 100);
			Imgproc.resize(sub, mat, size);
			Highgui.imwrite(photoName, mat);
			k++;
		}

		if (k == 0) {
			util.writePhoto(BitmapFactory.decodeFile(photoName), 100, 100, photoName);
		}
	}
	

}
