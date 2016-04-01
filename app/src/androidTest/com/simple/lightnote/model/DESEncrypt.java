package com.simple.lightnote.model;
/**
 * @FileName: DESEncrypt.java
 * @Package: com.ziroom.cleaning.common.encrypt
 * @author sence
 * @created 9/8/2015 4:25 PM
 * <p/>
 * Copyright 2015 ziroom
 */


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * <p>DES加密解密类实现</p>
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author sence
 * @since 1.0
 * @version 1.0
 */
public class DESEncrypt{

/*	static {
		System.load("jniinterface");
	}
	
	public static native byte[] getkeyBytesKey();
	public static native byte[] getkeyBytesIv();
	*/
    /**
     * 密钥
     */
    private static final String keyString="vpRZ1kmU";
    /**
     * 虚拟密钥
     */
    private static final String ivString="EbpU4WtY";

    /**
	 * 
	* @Title: desCrypto 
	* @Description: des加密
	* @param @param encryptString
	* @param @param encryptKey
	* @param @param ivString
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String encrypt(String content){  
		try {
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());  
			DESKeySpec dks = new DESKeySpec(keyString.getBytes());  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
			SecretKey key = keyFactory.generateSecret(dks);  
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
			cipher.init(Cipher.ENCRYPT_MODE, key, iv); 
			byte[] result=cipher.doFinal(content.getBytes("utf-8"));
			return DESPlus.byteArr2HexStr(result); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	* @Title: desCrypto 
	* @Description:DES解密
	* @param @param encryptString
	* @param @param encryptKey
	* @param @param ivString
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String decrypt(String content){  
		try {
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());  
			DESKeySpec dks = new DESKeySpec(keyString.getBytes());  
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
			SecretKey key = keyFactory.generateSecret(dks);  
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
			cipher.init(Cipher.DECRYPT_MODE, key, iv); 
			byte[] result=cipher.doFinal(DESPlus.hexStr2ByteArr(content));
			return new String (result,"utf-8"); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
