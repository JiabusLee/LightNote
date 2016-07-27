package com.simple.lightnote.util;

public class MyTextUtils {
	public static final String orderList="/^{d}+\\.(\\s*)|(\\s*$)/";
	
	
	public static boolean orderListText(String text){
		return text.matches(orderList);
	}
}
	