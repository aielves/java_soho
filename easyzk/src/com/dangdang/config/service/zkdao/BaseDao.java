package com.dangdang.config.service.zkdao;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs.Ids;

import sun.misc.BASE64Encoder;

public abstract class BaseDao implements Serializable {
	private static final long serialVersionUID = 1L;
	private String zkAddress;
	private CuratorFramework client;
	private String zkAuth;
	private byte[] auth = { 97, 100, 109, 105, 110, 58, 85, 114, 101, 50, 70, 120, 35, 115, 108, 65, 100, 101, 103 };

	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	public void setZkAuth(String zkAuth) {
		this.zkAuth = zkAuth;
	}

	@PostConstruct
	private void init() {
		if (zkAuth != null) {
			auth = zkAuth.getBytes();
			// System.out.println(Ids.ANYONE_ID_UNSAFE);
		}
		this.client = CuratorFrameworkFactory.builder().connectString(this.zkAddress)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3)).authorization("digest", this.auth)
				.connectionTimeoutMs(2000).sessionTimeoutMs(10000).build();

		this.client.start();
	}

	static public String generateDigest(String idPassword) throws NoSuchAlgorithmException {
		String parts[] = idPassword.split(":", 2);
		// System.out.println(parts[0] + "======" + parts[1]);
		byte digest[] = getSHA1(parts[1]);
		// System.out.println("-----------" + parts[0] + ":" + new BASE64Encoder().encode(digest));
		return parts[0] + ":" + new BASE64Encoder().encode(digest);
	}

	static public byte[] getSHA1(String str) {
		try {
			return MessageDigest.getInstance("SHA1").digest(str.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "".getBytes();
	}

	@PreDestroy
	private void destroy() {
		if (this.client != null)
			this.client.close();
	}

	public CuratorFramework getClient() {
		return this.client;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(generateDigest("cjq:cjq"));
	}

}