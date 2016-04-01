package com.simple.lightnote.util;

/**
 * 支持的标签 Created by homelink on 2016/3/16.
 */
public interface TagCons {
	/**
	 * 格式化8种数据类型:1。换行;2。无序列表;3。有序列表;4。标题;5。强调；6。引用；7.横线；8。链接；9.代码;
	 */

	String newLineFlag = "^lightnote^";
	String disorder = "^-(\\s){1,4}(?<!\\s)";
	String order = "^-(\\d).\\s{1,4}(?<!\\s)";
	String title = "^#{1,6}.*";
	String strong = "((?<!\\*)\\*\\*(?!\\*))|((?<!_)__(?!_))";
	String ref = "^>.*";
	String line = "(^\\*\\*\\*$)|(^---$)|(^- - -$)|(^\\* \\* \\*$)";
	String href = ".*\\!?\\[(.*?)\\]\\((.*?)\\).*";

	String newLine = "<br/>";
	String space="&quot;";




	
	
}
