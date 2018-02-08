package com.soho.zookeeper.security.imp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.soho.zookeeper.security.Decipher;

/**
 * AES算法解密数据实现
 * 
 * @author shadow
 * 
 */
public class AESDcipher implements Decipher {

	private final static String PRIV_KEY = "#4Z.FC02_CFB9$2/";

	public String decode(String data) {
		return decrypt(data);
	}

	public String encode(String data) {
		return encrypt(data);
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 * @return String
	 */
	public static String decrypt(String data) {
		try {
			byte[] raw = PRIV_KEY.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = hex2byte(data);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 数据加密
	 * 
	 * @param data
	 * @return String
	 */
	public static String encrypt(String data) {
		try {
			byte[] raw = PRIV_KEY.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(data.trim().getBytes());
			return byte2hex(encrypted).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 16进制转byte
	 * 
	 * @param strhex
	 * @return byte[]
	 */
	private static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
		}
		return b;
	}

	/**
	 * byte转16进制
	 * 
	 * @param b
	 *            字节数组
	 * @return String
	 */
	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = java.lang.Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
	
	public static void main(String[] args) {
		String s = new AESDcipher().encode("q88886666");
		System.out.println("加密后:"+s);
		System.out.println("解密后:"+new AESDcipher().decode(s));
	}

}
