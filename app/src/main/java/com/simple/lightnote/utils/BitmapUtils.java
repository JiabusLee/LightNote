package com.simple.lightnote.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;

public class BitmapUtils {
	
	public static int defaultImageWidth=360;
	public static int defaultImageHeight=480;
	
	
	/**
	 * 读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmapById(Context context, int resId) {
		return readBitmapById(context, resId,-1,-1);
	}

	/***
	 * 根据资源文件获取Bitmap
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static Bitmap readBitmapById(Context context, int drawableId,
			int showWidth, int showHight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Config.ARGB_8888;
		opts.inJustDecodeBounds=true;
		InputStream stream = context.getResources().openRawResource(drawableId);
		Options setOptions = setOptions(BitmapFactory.decodeStream(stream, null, opts), showWidth, showHight);
		return BitmapFactory.decodeStream(stream, null, setOptions);
		
	}

	public static Bitmap getBitmap(String filePath, int showWidth, int showHight) {
		Bitmap map = null;
		try {
			if (FileUtils.isFileExist(filePath)) {
				compressImages(filePath, showWidth, showHight);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	
	
	
	


	/**
	 * 获取文件二进制
	 * 
	 * @param path
	 * @return
	 * 
	 */
	public static byte[] getBitmapBytes(String path) {
		return getBitmapBytes(path,defaultImageWidth,defaultImageHeight);
	}
	
	public static byte[] getBitmapBytes(String path,int nWidth,int nHeight) {
		if (path != null) {
			byte b[] = null;
			File f = new File(path);
			if (f.exists()) {
				try {
					Bitmap compressBitmap = compressImages(path,nWidth,nHeight);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					compressBitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos);
					b = baos.toByteArray();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return b;
		}
		return null;
	
	}
	
	
	
	public static Bitmap compressImages(String filePath,int nWidth,int nHeight){
		Options opts=setOptions(getBoundsBitmap(filePath), nWidth, nHeight);
		return BitmapFactory.decodeFile(filePath, opts);  
		
	}
	
	public static Bitmap getBoundsBitmap(String filePath){
		Options opts=new Options();
		opts.inJustDecodeBounds=true;
		return BitmapFactory.decodeFile(filePath, opts);
		
	}
	
	
	
	public static Options setOptions(Bitmap bmp,int nWidth,int nHeight){
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		int w = bmp.getWidth();
		int h = bmp.getHeight();  
		int hh = nWidth;//  
		int ww = nHeight;// 
		int temp=hh>ww?hh:ww;
		int be = 1;  
		if (w > h && w > ww) {  
			be = (int) (w / temp+0.5f);  
		} else if (w < h && h > hh) {  
			be = (int) (h / temp+0.5f);  
		}  
		if (be <= 0)  
			be = 1;  
		if(be>1&&be%2!=0){
			be=be+1;
		}
		opts.inSampleSize = be;//设置采样率  
		opts.inJustDecodeBounds = false;  
		return opts;
	}
	
	
	/**
	 * 直接读取图片进行压缩：
	 * 
	 * @param filename
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap scalePicture(String filename, int maxWidth,
			int maxHeight) {
		Bitmap bitmap = null;
		try {

			// 计算缩放比

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;// 这样就只返回图片参数
			BitmapFactory.decodeFile(filename, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int desWidth = 0;
			int desHeight = 0;
			// 缩放比例
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxWidth;
				desWidth = maxWidth;
				desHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxHeight;
				desHeight = maxHeight;
				desWidth = (int) (srcWidth / ratio);
			}
			// 设置输出宽度、高度
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio) + 1;
			newOpts.inJustDecodeBounds = false;
			newOpts.outWidth = desWidth;
			newOpts.outHeight = desHeight;
			bitmap = BitmapFactory.decodeFile(filename, newOpts);

		} catch (Exception e) {
		}
		return bitmap;
	}

	
	/***
	 * 保存图片至SD卡
	 * 
	 * @param bm
	 * @param filename
	 * @param quantity
	 */
	public static void saveBitmap(Bitmap bm, String filename,String savePath) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
			return;

		File file = new File(savePath + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();

		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * 获取SD卡图片
	 * 
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmap(String filePath) {
		return getBitmap(filePath, 360, 480);
	}

}
