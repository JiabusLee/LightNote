package com.simple.lightnote.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",
			Locale.getDefault());

	public static String getDateByTimestamp(long timestamp) {
		return sdf.format(new Date(timestamp));
	}
	public static String getDateByTimestamp(long timestamp,String template) {
		sdf.applyPattern(template);
		return sdf.format(new Date(timestamp));
	}
	

}
