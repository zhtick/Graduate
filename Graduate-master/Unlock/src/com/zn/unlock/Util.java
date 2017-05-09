package com.zn.unlock;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Matrix;

	public class Util {
	//判断文件是否存在
	public static boolean isFileExist(String path1,String path2){
		File file1=new File(path1);
		File file2=new File(path2);
		if(file1.exists()&&file2.exists()){
			return true;
		}
		return false;
	}
	public static boolean isFileExist(String path){
		File file1=new File(path);
		
		if(file1.exists()){
			return true;
		}
		return false;
	}
	public static void saveBitmap(Bitmap paramBitmap, String paramString1, String paramString2)
			    throws IOException
			  {
			    float f1 = paramBitmap.getWidth();
			    float f2 = paramBitmap.getHeight();
			    Matrix localMatrix = new Matrix();
			    localMatrix.postScale(200.0F / f1, 200.0F / f2);
			    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, (int)f1, (int)f2, localMatrix, true);
			    File localFile = new File(paramString2, paramString1);
			    if (localFile.exists())
			      localFile.delete();
			    FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
			    if (!localBitmap.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream))
			      return;
			    localFileOutputStream.flush();
			    localFileOutputStream.close();
			  }
	public static void createFile(String path){
			    File file=new File(path);
			    file.mkdirs();
	}
	public static void writePhoto(Bitmap bmp, int width, int height, String path) {
		System.out.println("this");
		File file = new File(path);
		System.out.println("that");
		try {
			Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			System.out.println("222");
			if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
				bos.flush();
				bos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String readTxt(String filePath) {
		return filePath;
	
		  }
	 public static String readTxtFile(File fileName)throws Exception{  
		  String result="";  
		  FileReader fileReader=null;  
		  BufferedReader bufferedReader=null;  
		  try{  
		   fileReader=new FileReader(fileName);  
		   bufferedReader=new BufferedReader(fileReader);  
		   try{  
		    String read=null;  
		    while((read=bufferedReader.readLine())!=null){  
		     result=read;  
		    }  
		   }catch(Exception e){  
		    e.printStackTrace();  
		   }  
		  }catch(Exception e){  
		   e.printStackTrace();  
		  }finally{  
		   if(bufferedReader!=null){  
		    bufferedReader.close();  
		   }  
		   if(fileReader!=null){  
		    fileReader.close();  
		   }  
		  }  
		  System.out.println("读取出来的文件内容是："+"\r\n"+result);  
		  return result;  
		 }  	  
	//递归删除文件夹
	    public static void deleteFile(File file) {
	     if (file.exists()) {//判断文件是否存在
	      if (file.isFile()) {//判断是否是文件
	       file.delete();//删除文件 
	      } else if (file.isDirectory()) {//否则如果它是一个目录
	       File[] files = file.listFiles();//声明目录下所有的文件 files[];
	       for (int i = 0;i < files.length;i ++) {//遍历目录下所有的文件
	        deleteFile(files[i]);//把每个文件用这个方法进行迭代
	       }
	       file.delete();//删除文件夹
	      }
	     } else {
	      System.out.println("所删除的文件不存在");
	     }
	    }
}
