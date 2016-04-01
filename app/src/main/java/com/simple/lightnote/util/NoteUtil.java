package com.simple.lightnote.util;

import java.util.regex.Pattern;

public class NoteUtil {
	
	public static final String orderList="^(\\d)+\\.(\\s)+.";//匹配有序列表
	public static final String listTitle="^#{1,6}.*";//匹配标题
	public static final String disorderList="^\\*\\s.+";//匹配无序列表
	public static final String link="\\[.*\\]\\(http://\\w+\\.\\w+\\)";//匹配链接
	//匹配分隔线
	public static final String diliver="^(\\*\\s\\*\\s\\*)|(\\*{3,})|(-\\s-\\s-)|(-{3,})$";
	//匹配强调_斜体
	public static final String emphasize_u="(\\*.*\\*)|(_.*_)";
	//匹配强调_粗体
	public static final String emphasize_s="(\\*\\*.*\\*\\*)|(__.*__)";
	
	Pattern p=Pattern.compile(".*((\\r)|(\\n)|(\\r\\n)).*",Pattern.DOTALL);
	
	public static String formatText(String s_notedetail) {
		String[] split = s_notedetail.split("\r\n");
		
		
		return null;
	}
	
}
