package com.zn.unlock;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class FaceUnLockActivity extends Activity implements SurfaceHolder.Callback,
		Camera.PictureCallback{
	private ProgressDialog proDialog;  //正在识别
	private SurfaceView surfaceView;
	private Button btn_preview,btn_digital;
	private ImageView preview;
	private CascadeClassifier mJavaDetector;
	private SurfaceHolder holder;
	private Camera camera;
	private int angle=0;//保证surface正向
	private int cameraCount;
	String FACE= Environment.getExternalStorageDirectory()
			+ "/Unlock/face.jpg";
	private String[] FACE1 = {"/sdcard/Unlock/face/face1.jpg","/sdcard/Unlock/face/face2.jpg","/sdcard/Unlock/face/face3.jpg"};
	private String TEMP = "/sdcard/Unlock/temp.jpg";
	private Bitmap bm = null; 
	private  TextView text;
	private List faceList=new ArrayList();
	private ImageView img;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				{
				mJavaDetector = new CascadeClassifier("/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml"
					//mJavaDetector = new CascadeClassifier("/sdcard/FaceDetect/cascade.xml"
					
						);
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
		setContentView(R.layout.activity_face_lock);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		surfaceView=(SurfaceView)findViewById(R.id.camera);
		btn_preview=(Button)findViewById(R.id.btn_preview);
		text=(TextView)findViewById(R.id.text_input_tip);
		text.setText("请正面摄像头，准备好后点击解锁键");
		btn_preview.setOnClickListener(new PreviewListener());
		img=(ImageView)findViewById(R.id.imgview_unlock_face);
		btn_digital=(Button)findViewById(R.id.btn_digital);
		btn_digital.setOnClickListener(new DigitalListener());
		holder=surfaceView.getHolder();
		holder.addCallback(this);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	public class DigitalListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			intent.setClass(FaceUnLockActivity.this, PwdUnlockActivity.class);
			startActivity(intent);
			FaceUnLockActivity.this.finish();
		}
		
	}
	public class PreviewListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			camera.takePicture(null, null, FaceUnLockActivity.this);
			Thread t1=new findFace();
			t1.start();
			try {
				t1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*for(int i=0;i<3;i++){
				double value=CmpPic(TEMP,FACE1[i]);
				System.out.println(value);
				if(value>=0.6){
					finish();			
			        Toast.makeText(getApplicationContext(), "解锁成功！", 0).show();
			        break;
				}*/
			int i=Identification();
			double value=CmpPic(TEMP,FACE1[i]);
			if(value>=0.6){
				finish();			
		        Toast.makeText(getApplicationContext(), "解锁成功！", 0).show();
		        
			}
			else{
				text.setText("未录入您的信息,请重试或选择密码解锁");
			}
			}
				
		}
		
	
	@Override
	protected void onStart() {
		super.onStart();
	
		
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
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		String path= Environment.getExternalStorageDirectory()
				+ "/Unlock/face.jpg";//拍照后照片的存储路径，还要经过灰化等处理
		File file=new File(Environment.getExternalStorageDirectory()
				+ "/Unlock");
		if(!file.exists()){
			file.mkdir();
		}
		Bitmap bitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
		Matrix matrix = new Matrix();
		//设置旋转角度
		matrix.setRotate(270);
		//生成新的bitmap对象
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		
		Util.writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(), path);//
		camera.startPreview();
	}
	public void DetectFace() {
		Mat image = Highgui.imread(FACE);
		//cvtColor(image, image, Imgproc.COLOR_BGRA2GRAY);
		Size siz=image.size();
		MatOfRect faceDetections = new MatOfRect();
		//mJavaDetector.detectMultiScale(image, faceDetections,1.5,3,0,new Size(),new Size());
		mJavaDetector.detectMultiScale(image, faceDetections,1.2D, 2, 2, new Size(150.0D, 150.0D), new Size());
		int k = 0;
		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			Mat sub = image.submat(rect);
			Mat mat = new Mat();
			Size size = new Size(100, 100);
			Imgproc.resize(sub, mat, size);
			Highgui.imwrite(TEMP, mat);
			k++;
		}

		if (k == 0) {
			Util.writePhoto(BitmapFactory.decodeFile(FACE), 100, 100, TEMP);
		}
		
	}
	public class findFace extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			DetectFace();
		}
		
	}
	public int Identification() {
		FaceRecognizer fr = createFisherFaceRecognizer();
		MatVector mv = new MatVector(3);
		CvMat cvMat = CvMat.create(3, 1, CV_32SC1);
		for (int i = 0; i < 3; i++) {
			IplImage img = cvLoadImage(FACE1[i],
					CV_LOAD_IMAGE_GRAYSCALE);
			mv.put(i, img);
			cvMat.put(i, 0, i);
		}
		fr.train(mv, cvMat);
		IplImage testImage = cvLoadImage(
				"/sdcard/Unlock/temp.jpg", CV_LOAD_IMAGE_GRAYSCALE);
		return fr.predict(testImage);

	}
	public double CmpPic(String path,String path2) {
		int l_bins = 20;
		int hist_size[] = { l_bins };

		float v_ranges[] = { 0, 100 };
		float ranges[][] = { v_ranges };

		IplImage Image1 = cvLoadImage(path2, CV_LOAD_IMAGE_GRAYSCALE);
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
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
			camera = Camera.open(camera());
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
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
