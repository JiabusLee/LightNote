package com.simple.lightnote.utils;

import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*********************************************
 * author: Blankj on 2016/8/2 21:22
 * blog:   http://blankj.com
 * e-mail: blankj@qq.com
 *********************************************/
public class EncryptUtils {

    private EncryptUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }


    /**
     * MD5加密
     *
     * @param data 明文
     * @return 密文
     */
    public static String getMD5(String data) {
        return TextUtils.isEmpty(data) ? "" : getMD5(data.getBytes());
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文
     */
    public static String getMD5(byte[] data) {
        if (data.length > 0) {
            return bytes2Hex(encryptMD5(data));
        }
        return "";
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] encryptMD5(byte[] data) {
        if (data.length > 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(data);
                return md.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    public static String getMD5File(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            FileInputStream in = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                in = new FileInputStream(filePath);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    md.update(buffer, 0, len);
                }
                return bytes2Hex(md.digest());
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return "";
    }


/**
 * SHA加密
 *
 * @param data 明文
 * @return 密文
 */
public static String getSHA(String data) {
    return TextUtils.isEmpty(data) ? "" : getSHA(data.getBytes());
}

/**
 * SHA加密
 *
 * @param data 明文字节数组
 * @return 密文
 */
public static String getSHA(byte[] data) {
    if (data.length > 0) {
        return bytes2Hex(encryptSHA(data));
    }
    return "";
}

/**
 * SHA加密
 * @param data 明文字节数组
 * @return 密文字节数组
 */
public static byte[] encryptSHA(byte[] data) {
    if (data.length > 0) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    return null;
}


    /**
     * 一个byte转为2个hex字符
     */
    public static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }
        return new String(res);
    }
}
