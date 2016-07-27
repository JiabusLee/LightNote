package com.simple.lightnote;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.simple.lightnote.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class CustomCrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = CustomCrashHandler.class.getSimpleName();
	private static final String AppRoot="/lightnote";
	public static final String CrashDIR = AppRoot+"/crash";
	private Context mContext;
	private static CustomCrashHandler mInstance = new CustomCrashHandler();

	private CustomCrashHandler() {
	}

	/**
	 * 单例模式，保证只有一个CustomCrashHandler实例存在
	 * 
	 * @return
	 */
	public static CustomCrashHandler getInstance() {
		return mInstance;
	}

	/**
	 * 异常发生时，系统回调的函数，我们在这里处理一些操作
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// 将一些信息保存到SDcard中
		saveCrashInfo(mContext, ex);
		// 提示用户程序即将退出
		showToast(mContext, "程序异常,即将退出!!!");
		try {
			Thread.sleep(2000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
		// 完美退出程序方法
//		((ApplicationEx)mContext).exit();
	}

	/**
	 * 为我们的应用程序设置自定义Crash处理
	 */
	public void initCrashHandler(Context context) {
		mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 显示提示信息，需要在线程中显示Toast
	 * 
	 * @param context
	 * @param msg
	 */
	private void showToast(final Context context, final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
	 * 
	 * @param context
	 * @return
	 */
	private HashMap<String, String> obtainSystemInfo(Context context) {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		map.put("MANUFACTURER", Build.MANUFACTURER);//制造商
		map.put("versionName", mPackageInfo.versionName);//版本名称
		map.put("versionCode", "" + mPackageInfo.versionCode);//版本号
		map.put("MODEL", String.valueOf(Build.MODEL));//设备型号
		map.put("SDK_INT", String.valueOf( Build.VERSION.SDK_INT));//系统SDK版本号
		map.put("---------","---------");
		map.put("DEVICE", Build.DEVICE);//设备
		map.put("DISPLAY", Build.DISPLAY);//UI名称
		map.put("PRODUCT", Build.PRODUCT);//型号
		
		return map;
	}

	/**
	 * 获取系统未捕捉的错误信息
	 * 
	 * @param throwable
	 * @return
	 */
	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();

		Log.e(TAG, mStringWriter.toString());
		return mStringWriter.toString();
	}

	/**
	 * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
	 * 
	 * @param context
	 * @param ex
	 * @return crashFilePath
	 */
	private String saveCrashInfo(Context context, Throwable ex) {
		File crashFile = null;
		StringBuffer sb = new StringBuffer();
		HashMap<String,String> systemInfo = obtainSystemInfo(context);
		Set<Entry<String,String>> entrySet = systemInfo.entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}
		sb.append("\n\n");

		String exceptionInfo= sb.append(obtainExceptionInfo(ex)).toString();
		if(!TextUtils.isEmpty(exceptionInfo)){
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File externalStorageDirectory = Environment.getExternalStorageDirectory();
				File crashFilePath =new File(externalStorageDirectory, CrashDIR);
				if(!crashFilePath.exists())crashFilePath.mkdirs();
				try {
					String fileName=DateUtils.getDateByTimestamp(System.currentTimeMillis(),"yyyyMMddHHss") + ".log";
					crashFile=new File(crashFilePath,fileName);
					if(!crashFile.exists()){
						crashFile.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(crashFile,true);
					fos.write(exceptionInfo.getBytes());
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		return crashFile!=null?crashFile.getAbsolutePath():null;

	}

}