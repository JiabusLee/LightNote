package com.simple.lightnote.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SH1Utils {

	public static String toSHA1(byte[] convertme) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest(convertme);
		return new String(digest);
	}
}
