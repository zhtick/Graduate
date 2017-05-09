package com.zn.face;

/********************************************
 @author zn
 time:2017.03.16
 注册页面，初步设计包括三项，用户名，密码，照片，点击注册按钮，需要联通sql lite进行数据存储，同时，照片文件存储到存储卡中
 重要的一步:opencv训练的图片需要尺寸一样，所以照片拍好后，要进行归一化处理，再存储                                                                             add：2017.03.29
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
	private static String photoPath = "/sdcard/FaceDetect/Face/";// 照片未经处理存储的路径
	private static String photoName;// 存到手机上的名称
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));// 第二个参数是临时文件，在后面将会被修改
	private static final int PHOTO_CAPTURE = 0x11;
	private InfoDao dao;//接口
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
		
		photoName=photoPath+(count+1)+".jpg";//photoName以id命名
		upload.setOnClickListener(new TakePhotoListener());
		register.setOnClickListener(new RegisterListenner());
	}
	@Override
	protected void onResume() {
		super.onResume();
	
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				mLoaderCallback);
	}
	//点击进行拍照
	public  class TakePhotoListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			// "/sdcard/AnBo/";
			File file = new File(photoPath);
			if (!file.exists()) { // 检查图片存放的文件夹是否存在
				file.mkdir(); // 不存在的话 创建文件夹
			}
			File photo = null;
			photo = new File(photoName);
			imageUri = Uri.fromFile(photo);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
			startActivityForResult(intent, PHOTO_CAPTURE);
		}	
	}
	//注册按钮的监听器
	public class RegisterListenner implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String name=username.getText().toString();
			String passwd=password.getText().toString();
			System.out.println(passwd);
			if(name==null || passwd==null ||"".equals(name) || "".equals(passwd)){
				Toast.makeText(RegisterActivity.this, "请输入完整的信息",Toast.LENGTH_LONG).show();
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
	//拍完照片之后的回显
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
				Log.i("内存卡错误", "请检查您的内存卡");
			} else {

				BitmapFactory.Options op = new BitmapFactory.Options();
				// 设置图片的大小
				Bitmap bitMap = null;
					bitMap = BitmapFactory.decodeFile(photoName);
				int width = bitMap.getWidth();
				int height = bitMap.getHeight();
				
				// 计算缩放比例
				float scaleWidth = (float) 0.5;
				float scaleHeight =(float) 0.5;
				// 取得想要缩放的matrix参数
				Matrix matrix = new Matrix();
				matrix.setRotate(270);
				matrix.postScale(scaleWidth, scaleHeight);
				// 得到新的图片
				bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
						matrix, true);
				// canvas.drawBitmap(bitMap, 0, 0, paint)
				// 防止内存溢出
				op.inSampleSize = 15; // 这个数字越大,图片大小越小.
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
