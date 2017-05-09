package com.zn.unlock;

/********************************************
 @author zn

 主要工作：
 1.完成环境配置，主要是opencv 2.4.10和javacv,javacv相当于是对opencv进一步的封装，
 主要是在人脸识别的时候使用              **************************************** 2017.03.16
 2.理清主要思路1.注册时将人脸存储到sd卡相应路径下
 2.surface将人脸绘制到屏幕上，camera自动拍照，存储到sd卡
 3.将上一步得到的图片进行处理             *******************************2017.03.20


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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FaceEntryActivity extends Activity implements
		SurfaceHolder.Callback, Camera.PictureCallback {
	private CascadeClassifier mJavaDetector;
	private SurfaceView mSurfaceView;
	private SurfaceHolder holder;
	private Camera camera;
	private int angle = 0;// 保证surface正向
	private int count=0;
	private int clickcount=0;
	private String[] Path = { "/sdcard/Unlock/face/face1.jpg",
			"/sdcard/Unlock/face/face2.jpg", "/sdcard/Unlock/face/face3.jpg" };
	private int index;
	private double value;
	private boolean flag = true;
	private int t = 2;
	private int cameraCount;
	private Toast toast;
	private String name;
	private TextView text;
	private Button entry;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				System.out.println("qqqqqqqqqqqqqqqqqqq");
				{

					mJavaDetector = new CascadeClassifier(
							"/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml"
					// mJavaDetector = new
					// CascadeClassifier("/sdcard/FaceDetect/cascade.xml"

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_input);
		mSurfaceView = (SurfaceView) findViewById(R.id.camera);
		text = (TextView) findViewById(R.id.text_input_tip);
		entry = (Button) findViewById(R.id.btn_preview);
		entry.setOnClickListener(new EntryListener());
		text.setText("面对摄像头，录入三张照片");
		holder = mSurfaceView.getHolder();
		holder.addCallback(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!OpenCVLoader.initDebug()) {
			Log.d("TAG",
					"Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
					mLoaderCallback);
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
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Parameters parameters = camera.getParameters();
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
		String path = Environment.getExternalStorageDirectory()
				+ "/Unlock/temp.jpg";// 拍照后照片的存储路径，还要经过灰化等处理
		Bitmap bitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
		Matrix matrix = new Matrix();
		// 设置旋转角度
		matrix.setRotate(270);
		// 生成新的bitmap对象
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		Util.writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(), path);//
		System.out.println("***************");
		try {
			Thread thread = new Thread(new EntryThread());
			thread.start();
			thread.join();
		} catch (Exception e) {
		}
		camera.startPreview();

	}

	class EntryThread implements Runnable {

		@Override
		public void run() {
			System.out.println("jinlaile&&&&&&&&&&&&&&&");
			DetectFace();
			System.out.println("chulaile&&&&&&&&&&&&&&&");
		}
	}
	class EntryListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!Util.isFileExist("/sdcard/Unlock/face"));{
				File file=new File("/sdcard/Unlock/face");
				file.mkdirs();
			}
			clickcount++;
			if(clickcount>3){
				Toast.makeText(FaceEntryActivity.this, "已录入完成",Toast.LENGTH_LONG).show();
				FaceEntryActivity.this.finish();
			}
			Toast.makeText(FaceEntryActivity.this, "已录入"+(count+1)+"张，共3张",Toast.LENGTH_LONG).show();
			
			camera.takePicture(null, null, FaceEntryActivity.this);
			
			
		}
		
	}
	public void DetectFace() {
		Mat image = Highgui.imread(Environment.getExternalStorageDirectory()
				+ "/Unlock/temp.jpg");
		// cvtColor(image, image, Imgproc.COLOR_BGRA2GRAY);
		Size siz = image.size();
		MatOfRect faceDetections = new MatOfRect();
		//mJavaDetector.detectMultiScale(image, faceDetections);
		mJavaDetector.detectMultiScale(image, faceDetections,1.2D, 2, 2, new Size(150.0D, 150.0D), new Size());

		int k = 0;

		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			Mat sub = image.submat(rect);
			Mat mat = new Mat();
			Size size = new Size(100, 100);
			Imgproc.resize(sub, mat, size);
			System.out.println(count);
			Highgui.imwrite(Path[count], mat);
			k++;
		}

		if (k == 0) {
			Util.writePhoto(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
					+ "/Unlock/temp.jpg"), 100, 100, Path[count]);
		}
		count++;
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
