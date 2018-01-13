package com.soho.zookeeper.security;

/**
 * 数据解密器接口
 * 
 * @author shadow
 * 
 */
public interface Decipher {

	/**
	 * 解密数据
	 * 
	 * @param data
	 * @return String
	 */
	public String decode(String data);

	/**
	 * 加密数据
	 * 
	 * @param data
	 * @return String
	 */
	public String encode(String data);

}
