package com.simple.lightnote.util;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by homelink on 2016/3/15.
 */
public class HtmlParser {

    private static final String TAG = "HtmlParser";

    /**
     * 取出HTml
     *
     * @return
     */
    public String getHtmlText() {
        return null;
    }

    public static String formatText(String text, String newLineFlag) {
        // 换行
        String s = text.replace(newLineFlag, "\r\n");
        String[] split = s.split("\r\n");
        StringBuilder sb = new StringBuilder();
        for (String str : split) {
            // System.out.println(str);
            sb.append(handleText(str)).append("\r\n");
        }
        return sb.toString();

    }

    private static String handleText(String str) {
        return toHtml(str);
    }

    static boolean disOrderline = false;
    static boolean isOrderLine = false;

    /**
     *
     */
    String previousType = "";


    private static String toHtml(String str) {
        StringBuilder sb = new StringBuilder(str);

        if (str != null && str.length() > 0) {
            // 标题
            if (str.matches(TagCons.title)) {
                int temp = 0;
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '#') {
                        temp++;
                    } else {
                        break;
                    }
                }

                return sb.delete(0, temp).insert(0, "<h" + temp + ">")
                        .append("</h" + temp + ">").toString();

            } else if (str.matches(TagCons.line)) {
                return sb.delete(0, sb.length()).append("<hr/>").toString();
            } else if (str.matches(TagCons.ref)) {

                return sb.delete(0, 1).insert(0, "<blockquote>\n<p>")
                        .append("</p>").append("</blockquote>").toString();

            } else if (str.split("(?<!\\*)\\*\\*(?!\\*)").length > 1) {

                return setStrong(str);
//				return setStrongText(str, "**");
            } else if (str.matches(TagCons.disorder)) {
                // 无序

                if (disOrderline) {

                }
                return sb.append(TagCons.newLine).toString();
                // 判断换行的情况
            } else if (str.matches(TagCons.order)) {

                // 有序
                if (isOrderLine) {

                }
                // 判断换行的情况
                return sb.append(TagCons.newLine).toString();
            } else if (str.matches(TagCons.href)) return parseHref(str, sb);

            else {
                return sb.append(TagCons.newLine).toString();
            }

        } else {
            // 空
            return TagCons.newLine;
        }

    }

    @NonNull
    private static String parseHref(String str, StringBuilder sb) {
        Pattern compile = Pattern.compile(TagCons.href);
        Matcher m = compile.matcher(str);
        StringBuilder sbTemp = new StringBuilder();
        while (m.find()) {
            String group = m.group(0);
            String showText = m.group(1);
            String linkText = m.group(2);
            if (group.startsWith("!")) {
                sbTemp.append("<img src='").append(linkText)
                        .append(" alt='").append(showText)
                        .append("'><br/>");
            } else {
                sbTemp.append("<a href='").append(linkText)
                        .append("'>").append(showText).append("</a>");
            }
            str = str.replace(group, sbTemp.toString());
        }
        return sb.delete(0, sb.length()).append(sbTemp)
                .append(TagCons.newLine).toString();
    }

    public static String setStrong(String text) {
        String[] split = text.split(TagCons.strong);

//		System.out.println(Arrays.toString(split));
//		System.out.println("----------");
        StringBuilder sb = new StringBuilder();
        int length = split.length;
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                // start
                if ((length % 2 != 0 && i + 1 == length)) {
                    sb.append(split[i]).append("**");
                } else {
                    sb.append(split[i]).append("<strong>");

                }

            } else {
                // end
                sb.append(split[i]).append("</strong>");
            }

        }

        return sb.toString();

    }


    public static String setStrongText(String text, String str) {
        StringBuilder sb = new StringBuilder(text);
        int left = -1;
        int right = -1;
        int position = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char cc = str.charAt(0);
            if (c == cc) {
                try {
                    if (i + str.length() <= text.length()) {
                        for (int j = 0; j < str.length(); j++) {

                            char cj = str.charAt(j);
                            char c3 = text.charAt(j + i);
                            if (c3 == cj) {
                                // 全部匹配
                                if (j == str.length() - 1) {
                                    if (left > 0) {
                                        right = i + 2;
                                        i = i + 2;// 从i+2的位置开始循环
                                    } else {
                                        left = i;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

            if (left >= 0 && right > 0) {
                // 一个正常的结构体
                sb.insert(left, "<strong>").insert(right, "</strong>");
                left = -1;
                right = -1;
            }
        }
        left = -1;
        right = -1;

        return sb.toString().replace(str, "");

    }

}
