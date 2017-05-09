package com.zn.face;

/********************************************
 @author zn
 
主要工作：
1.完成环境配置，主要是opencv 2.4.10和javacv,javacv相当于是对opencv进一步的封装，
     主要是在人脸识别的时候使用              **************************************** 2017.03.16
2.理清主要思路1.注册时将人脸存储到sd卡相应路径下
		  2.surface将人脸绘制到屏幕上，camera自动拍照，存储到sd卡
		  3.将上一步得到的图片进行处理             *******************************2017.03.20
		  

 *********************************************/


import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;
import com.zn.dao.Info;
import com.zn.dao.InfoDao;
import com.zn.dao.InfoDaoImpl;
import com.zn.unlock.R;
import com.zn.utils.CameraUtil;

public class FaceLoginActivity extends Activity implements SurfaceHolder.Callback,
				Camera.PictureCallback{
	private CascadeClassifier mJavaDetector;
	private SurfaceView mSurfaceView;
	private SurfaceHolder holder;
	private Camera camera;
	private int angle=0;//保证surface正向
	private ProgressDialog proDialog;  //正在识别
	private CameraUtil util=new CameraUtil();
	private String FACE = "/sdcard/FaceDetect/Detect/face.jpg";
	private String FACEDONE = "/sdcard/FaceDetect/Detect/faceDone.jpg";
	private int index;
	private double value;
	private Info info;
	private List<Info> faceList=new ArrayList<Info>();
	private boolean flag = true;
	private int t = 2;
	private int cameraCount;
	private int[] iconArr = new int[] { R.drawable.icon1, R.drawable.icon2,
			R.drawable.icon3 };
	private Toast toast;
	private String name;
	InfoDao dao;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				System.out.println("qqqqqqqqqqqqqqqqqqq");{
				
				mJavaDetector = new CascadeClassifier("/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml"
					//mJavaDetector = new CascadeClassifier("/sdcard/FaceDetect/cascade.xml"
					
						);
				System.out.println("wwwwwwwwwwwwwwwwww");	
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	
	private Handler updateUI = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				proDialog.dismiss();
				Intent intent = new Intent();		
					index = Identification();
					info = faceList.get(index);
					int id=info.getId();
					name=dao.qryName(id+"");
					value = CmpPic(info.getPhotopath()) * 100;
				if (value < 70 ) {
					intent.setClass(FaceLoginActivity.this, FailActivity.class);
					startActivity(intent);
				} else {
						intent.setClass(FaceLoginActivity.this, SuccessActivity.class);
						intent.putExtra("name", name);
						startActivity(intent);
				}
				break;
			}
		}
	};

	private Timer dataTimer = new Timer();
	private Handler dataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
			case 1:
				if (flag && t == -1) {
					// camera.autoFocus(null);
					camera.takePicture(null, null, FaceLoginActivity.this);
					flag = false;
				}
				if (flag) {
					timeDown();
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facelogin);
		File file=new File("/sdcard/FaceDetect/Detect");
		if(!file.exists()){
			file.mkdirs();
		}
		mSurfaceView=(SurfaceView)findViewById(R.id.camera);
		dao=new InfoDaoImpl(this);
		proDialog=new ProgressDialog(this);
		holder=mSurfaceView.getHolder();
		holder.addCallback(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LoadFaceData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!OpenCVLoader.initDebug()) {
            Log.d("TAG", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        } else {
            Log.d("TAG", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		try {
			camera = Camera.open(camera());
			camera.setPreviewDisplay(holder);
			dataTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;
					dataHandler.sendMessage(message);
				}

			}, 0, 2000);
		} catch (Exception e) {
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Parameters parameters=camera.getParameters();
		camera.setParameters(parameters);
		int flag = getWindowManager().getDefaultDisplay().getRotation();
		
		angle = 450 - flag * 90;
		if (angle >= 360) {
			angle = angle - 360;
		}
		camera.setDisplayOrientation(angle);
		camera.startPreview();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		String path= Environment.getExternalStorageDirectory()
				+ "/FaceDetect/Detect/face.jpg";//拍照后照片的存储路径，还要经过灰化等处理
		Bitmap bitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
		Matrix matrix = new Matrix();
		//设置旋转角度
		matrix.setRotate(270);
		//生成新的bitmap对象
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		util.writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(), path);//
		System.out.println("***************");
		proDialog.setMessage("正在识别..."); 
		proDialog.show();
		try{
		Thread loginThread = new Thread(new LoginThread());
		loginThread.start();
	} catch (Exception e) {
	}
	camera.startPreview();
		
	}
	class LoginThread implements Runnable {

		@Override
		public void run() {
			System.out.println("jinlaile&&&&&&&&&&&&&&&");
			DetectFace();
			System.out.println("chulaile&&&&&&&&&&&&&&&");
			Message message = new Message();
			message.what = 1;
			updateUI.sendMessage(message);
		}
	}
	public void DetectFace() {
		Mat image = Highgui.imread(FACE);
		//cvtColor(image, image, Imgproc.COLOR_BGRA2GRAY);
		Size siz=image.size();
		MatOfRect faceDetections = new MatOfRect();
		//mJavaDetector.detectMultiScale(image, faceDetections);
		mJavaDetector.detectMultiScale(image, faceDetections,1.2D, 2, 2, new Size(150.0D, 150.0D), new Size());


		int k = 0;
		

		for (Rect rect : faceDetections.toArray()) {
			System.out.println("#########0000000000000");
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			Mat sub = image.submat(rect);
			Mat mat = new Mat();
			Size size = new Size(100, 100);
			Imgproc.resize(sub, mat, size);
			Highgui.imwrite(FACEDONE, mat);
			k++;
		}

		if (k == 0) {
			util.writePhoto(BitmapFactory.decodeFile(FACE), 100, 100, FACEDONE);
		}
	}
	public int Identification() {
		FaceRecognizer fr = createFisherFaceRecognizer();
		MatVector mv = new MatVector(faceList.size());
		CvMat cvMat = CvMat.create(faceList.size(), 1, CV_32SC1);
		for (int i = 0; i < faceList.size(); i++) {
			IplImage img = cvLoadImage(faceList.get(i).getPhotopath(),
					CV_LOAD_IMAGE_GRAYSCALE);
			mv.put(i, img);
			cvMat.put(i, 0, i);
		}
		fr.train(mv, cvMat);
		IplImage testImage = cvLoadImage(
				Environment.getExternalStorageDirectory()
						+ "/FaceDetect/Detect/faceDone.jpg", CV_LOAD_IMAGE_GRAYSCALE);
		return fr.predict(testImage);

	}
	public double CmpPic(String path) {
		int l_bins = 20;
		int hist_size[] = { l_bins };

		float v_ranges[] = { 0, 100 };
		float ranges[][] = { v_ranges };

		IplImage Image1 = cvLoadImage(Environment.getExternalStorageDirectory()
				+ "/FaceDetect/Detect/faceDone.jpg", CV_LOAD_IMAGE_GRAYSCALE);
		IplImage Image2 = cvLoadImage(path, CV_LOAD_IMAGE_GRAYSCALE);

		IplImage imageArr1[] = { Image1 };
		IplImage imageArr2[] = { Image2 };

		CvHistogram Histogram1 = CvHistogram.create(1, hist_size,
				CV_HIST_ARRAY, ranges, 1);
		CvHistogram Histogram2 = CvHistogram.create(1, hist_size,
				CV_HIST_ARRAY, ranges, 1);

		cvCalcHist(imageArr1, Histogram1, 0, null);
		cvCalcHist(imageArr2, Histogram2, 0, null);

		cvNormalizeHist(Histogram1, 100.0);
		cvNormalizeHist(Histogram2, 100.0);

		return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);
		//
	}
	
	public void timeDown() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom,
				(ViewGroup) findViewById(R.id.llToast));
		ImageView image = (ImageView) layout.findViewById(R.id.tvImageToast);
		image.setImageResource(iconArr[t--]);
		toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void LoadFaceData() {
		File[] files = new File("/sdcard/FaceDetect/Face/").listFiles();//注册时人脸照片存放路径
		File f;
		String id;
		String photopath;
		faceList.clear();
		for (int i = 0; i < files.length; i++) {
			f = files[i];
			if (!f.canRead()) {
				return;
			}
			if (f.isFile()) {
				id = f.getName();
				System.out.println(id);
				id=id.substring(0,id.lastIndexOf("."));//获取文件名，不要后缀，得到id
				System.out.println(id);
				//根据id查询得到姓名
				InfoDao dao=new InfoDaoImpl(this);
				name=dao.qryName(id);
				System.out.println(name+"*****************************");
				faceList.add(new Info(id, name, Environment
						.getExternalStorageDirectory()
						+ "/FaceDetect/Face/"
						+ f.getName()));//将得到的id,name,path放入list中，在Identification中使用，对比后，返回相似度最高的
			}
		}
	}
	public int camera() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					return camIdx;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
		return 1;
	}
}
