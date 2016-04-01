package com.simple.lightnote.utils;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

/**
 * 整理的ImageUtils工具类
 * @author homelink
 *
 */

/*
 * 1、Drawable 就是一个可画的对象， 
 * 其可能是一张位图（BitmapDrawable） 
 * 也可能是一个图形（ShapeDrawable）
 * 还有可能是一个图层（LayerDrawable）
 * 2、Canvas画布，绘图的目的区域，用于绘图
 * 3、Bitmap位图，用于图的处理 
 * 4、Matrix矩阵 
 * 5、Paint画笔 
 * 6、Path路径
 */
public class ImageUtils {

	/**
	 * 按图片的宽高压缩
	 */

	public Bitmap imgCompByQuality(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		//图片的宽高
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 480f;
		// 缩放比。be=1表示不缩放
		int be = 1;
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (w / ww+0.5f);
		} else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (h/ hh+0.5f);
		}
		
		if (be <= 0)
			be = 1;
		if(be>1&&be%2!=0){
			be=be+1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	/**
	 *  图片Size压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap imgCompBySize(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 图片压缩
	 * 
	 * @param photo
	 * @param newHeight
	 * @param context
	 * @return
	 */
	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight,
			Context context) {
		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}
	/**
	 * 将view转为bitmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap view2Bitmap(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else{
			canvas.drawColor(Color.WHITE);
		}
		view.draw(canvas);
		return returnedBitmap;
	}

	/**
	 * 将view转为bitmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap view2Bitmap2(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bm = view.getDrawingCache();
		return bm;
	}


	/**
	 * Bitmap保存成文件
	 * 
	 * @param bmp
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */

	public static boolean bitmap2File(Bitmap bmp, String absoluteName) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(absoluteName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bmp.compress(format, quality, stream);
	}

	/**
	 * Bitmap 转成 Byte
	 * 
	 * @param bm
	 * @return
	 */

	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * Bitmap转换成Drawable
	 */
	public static Drawable bitmap2Drawable(Bitmap xxx, Context mContext) {
		Bitmap bm = xxx; // xxx根据你的情况获取
		BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), bm);
		return bd;
	}

	/**
	 * Bitmap转换成字符串
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmaptoString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	/**
	 * Bitmap转换成InputStream
	 * 
	 * @param bm
	 * @param quality
	 * @return
	 */
	public  static InputStream bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/**
	 * String转换成Bitmap
	 * 
	 * @param string
	 * @return
	 */
	public static Bitmap string2Bitmap(String string) {
		Bitmap bitmap = null;
		byte[] bitmapArray;
		bitmapArray = Base64.decode(string, Base64.DEFAULT);
		bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
				bitmapArray.length);
		return bitmap;
	}

	/**
	 * byte[]转为文件
	 * 
	 * @param b
	 * @return
	 */

	public static File bytes2File(byte[] b) {
		BufferedOutputStream stream = null;
		File file = null;
		File bitmapFile = new File("/tempFile/xxx.jpg");
		// 创建文件夹
		bitmapFile.getParentFile().mkdirs();
		FileOutputStream fstream;
		try {
			fstream = new FileOutputStream(bitmapFile);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * byte[] → Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public static  Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * byte[]转换成Drawable
	 * 
	 * @param b
	 * @return
	 */
	public static  Drawable bytes2Drawable(byte[] b,Context mContext) {
		Bitmap bitmap = bytes2Bimap(b);
		return bitmap2Drawable(bitmap,mContext);
	}

	/**
	 * byte[]转换成InputStream
	 * 
	 * @param b
	 * @return
	 */
	public static  InputStream byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	/**
	 * InputStream转换成byte[]
	 * 
	 * @param is
	 * @return
	 */
	public  static byte[] inputStream2Bytes(InputStream is) {
		String str = "";
		byte[] readByte = new byte[1024];
		try {
			while ((is.read(readByte, 0, 1024)) != -1) {
				str += new String(readByte).trim();
			}
			return str.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * InputStream转换成Bitmap
	 * 
	 * @param is
	 * @return
	 */
	public static  Bitmap inputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * InputStream转换成Drawable
	 * 
	 * @param is
	 * @return
	 */
	public  static Drawable inputStream2Drawable(InputStream is,Context mContext) {
		Bitmap bitmap = inputStream2Bitmap(is);
		return bitmap2Drawable(bitmap,mContext);
	}

	/**
	 * Drawable转换成InputStream
	 * 
	 * @param d
	 * @return
	 */
	public static  InputStream drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2InputStream(bitmap,100);
	}

	/**
	 * Drawable转换成byte[]
	 * 
	 * @param d
	 * @return
	 */
	public  static byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2Bytes(bitmap);
	}

	/**
	 * Drawable转换成Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public  static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Drawable缩放
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h,Context mContext) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		// drawable转换成bitmap
		Bitmap oldbmp = drawable2Bitmap(drawable);
		// 创建操作图片用的Matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放比例
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		// 设置缩放比例
		matrix.postScale(sx, sy);
		// 建立新的bitmap，其内容是对原bitmap的缩放后的图
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(mContext.getResources(),newbmp);
	}

	/**
	 * 从资源中获取Bitmap
	 */
	public static  Bitmap getBitmapFromRes(int id,Context mContext) {
		Resources res = mContext.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, id);
		return bmp;
	}

	/**
	 * 获得带倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 屏幕截屏方法
	 * 
	 * @param v
	 */
	public static  void printscreen_share(View view, Context mContext) {
		/*View view1 = getWindow().getDecorView();
		Display display = mContext.getWindowManager().getDefaultDisplay();
		view1.layout(0, 0, display.getWidth(), display.getHeight());*/
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		bitmap2File(bitmap,"xx.jpg");
	}

	/**
	 * 下载图片
	 * 
	 * @param params
	 * @return
	 */
	public static Bitmap loadImage(String  imgUrl) {
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;

	}
	
	/*
	图片的Matrix变换


	1. Matrix缩放
	2. Matrix旋转
	3. Matrix位移
	4. Matrix倾斜

 
Matrix
　　对于一个图片变换的处理，需要Matrix类的支持，它位于"android.graphics.Matrix"包下，是Android提供的一个矩阵工具类，它本身不能对图像或View进行变换，但它可与其他API结合来控制图形、View的变换，如Canvas。
　　Matrix提供了一些方法来控制图片变换：

	* setTranslate(float dx,float dy)：控制Matrix进行位移。
	* setSkew(float kx,float ky)：控制Matrix进行倾斜，kx、ky为X、Y方向上的比例。
	* setSkew(float kx,float ky,float px,float py)：控制Matrix以px、py为轴心进行倾斜，kx、ky为X、Y方向上的倾斜比例。
	* setRotate(float degrees)：控制Matrix进行depress角度的旋转，轴心为（0,0）。
	* setRotate(float degrees,float px,float py)：控制Matrix进行depress角度的旋转，轴心为(px,py)。
	* setScale(float sx,float sy)：设置Matrix进行缩放，sx、sy为X、Y方向上的缩放比例。
	* setScale(float sx,float sy,float px,float py)：设置Matrix以(px,py)为轴心进行缩放，sx、sy为X、Y方向上的缩放比例。

　　之前有提过，图片在内存中存放的就是一个一个的像素点，而对于图片的变换主要是处理图片的每个像素点，对每个像素点进行相应的变换，即可完成对图像的变换。上面已经列举了Matrix进行变换的常用方法，下面以几个Demo来讲解一下如何通过Matrix进行变换。 
 */
    /** 2      * 缩放图片
     */     
	protected void bitmapScale(float x, float y, Bitmap baseBitmap, Paint paint) {
		// 因为要将图片放大，所以要根据放大的尺寸重新创建Bitmap 6
		Bitmap afterBitmap = Bitmap.createBitmap(
				(int) (baseBitmap.getWidth() * x),
				(int) (baseBitmap.getHeight() * y), baseBitmap.getConfig());
		Canvas canvas = new Canvas(afterBitmap);
		// 初始化Matrix对象11
		Matrix matrix = new Matrix();
		// 根据传入的参数设置缩放比例13
		matrix.setScale(x, y);
		// 根据缩放比例，把图片draw到Canvas上15
		canvas.drawBitmap(baseBitmap, matrix, paint);
		ImageView iv_after = null;
		iv_after.setImageBitmap(afterBitmap);
	}

	/**
	 * 2 * 图片旋转
	 */
	protected void bitmapRotate(float degrees, Bitmap baseBitmap, Paint paint) {
		// 创建一个和原图一样大小的图片 6
		Bitmap afterBitmap = Bitmap.createBitmap(baseBitmap.getWidth(),
				baseBitmap.getHeight(), baseBitmap.getConfig());
		Canvas canvas = new Canvas(afterBitmap);
		Matrix matrix = new Matrix();
		// 根据原图的中心位置旋转11
		matrix.setRotate(degrees, baseBitmap.getWidth() / 2,
				baseBitmap.getHeight() / 2);
		canvas.drawBitmap(baseBitmap, matrix, paint);
		ImageView iv_after = null;
		iv_after.setImageBitmap(afterBitmap);
	}

	/**
	 * 2 * 图片移动
	 */
	protected void bitmapTranslate(float dx, float dy, Bitmap baseBitmap,
			Paint paint) {
		// 需要根据移动的距离来创建图片的拷贝图大小 6
		Bitmap afterBitmap = Bitmap.createBitmap(
				(int) (baseBitmap.getWidth() + dx),
				(int) (baseBitmap.getHeight() + dy), baseBitmap.getConfig());
		Canvas canvas = new Canvas(afterBitmap);
		Matrix matrix = new Matrix();
		// 设置移动的距离12
		matrix.setTranslate(dx, dy);
		canvas.drawBitmap(baseBitmap, matrix, paint);
		ImageView iv_after = null;
		iv_after.setImageBitmap(afterBitmap);
	}

	/**
	 * 2 * 倾斜图片
	 */
	protected void bitmapSkew(float dx, float dy, Bitmap baseBitmap, Paint paint) {
		// 根据图片的倾斜比例，计算变换后图片的大小， 6
		Bitmap afterBitmap = Bitmap.createBitmap(baseBitmap.getWidth()
				+ (int) (baseBitmap.getWidth() * dx), baseBitmap.getHeight()
				+ (int) (baseBitmap.getHeight() * dy), baseBitmap.getConfig());
		Canvas canvas = new Canvas(afterBitmap);
		Matrix matrix = new Matrix();
		// 设置图片倾斜的比例12
		matrix.setSkew(dx, dy);
		canvas.drawBitmap(baseBitmap, matrix, paint);
		ImageView iv_after = null;
		iv_after.setImageBitmap(afterBitmap);
	}

	/**
	 * 将彩色图转换为灰度图
	 * 
	 * @param img
	 *            位图
	 * @return 返回转换好的位图
	 */
	public Bitmap convertGreyImg(Bitmap img) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		img.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}
	/**
	 * 图标加灰色过滤
	 * @param mContext
	 * @param drawable
	 */
	public static void bitmap2Gray(Context mContext,int drawable) {
		Drawable mDrawable = mContext.getResources().getDrawable(drawable);
		mDrawable.mutate();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		mDrawable.setColorFilter(cf);
	}


	/**
	 * 生成水印图片
	 */
	public Bitmap createWatermarkBitmap(Bitmap src, Bitmap watermark) {
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return newb;
	}
	
	/**
	 * 将一个图片切割成多个图片 有种场景，我们想将一个图片切割成多个图片
	 * 一个是ImagePiece类，此类保存了一个Bitmap对象和一个标识图片的顺序索引的int变量。
	 * 一个是ImageSplitter类，有一个静态方法split，传入的参数是要切割的Bitmap对象，和横向和竖向的切割片数
	 */
	public static List<ImagePiece> splitImage(Bitmap bitmap, int xPiece, int yPiece) {
		List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / 3;
		int pieceHeight = height / 3;
		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				ImagePiece piece = new ImagePiece();
				piece.index = j + i * xPiece;
				int xValue = j * pieceWidth;
				int yValue = i * pieceHeight;
				piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces.add(piece);
			}
		}

		return pieces;
	}

}

class ImagePiece {
	public int index = 0;
	public Bitmap bitmap = null;
}
