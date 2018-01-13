package com.soho.zookeeper.utils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

public class ZKUtils {

	/**
	 * @param host
	 *            ZK地址
	 * @param nodePath
	 *            根节点,如 /root
	 * @param nodePwd
	 *            根节点登录密码
	 * @param adminAcl
	 *            完全权限
	 * @param guestAcl
	 *            只读权限
	 */
	public static void createNode(String host, String nodePath, String nodePwd, String adminAcl, String guestAcl) {
		try {
			List<ACL> acls = new ArrayList<ACL>(2);
			Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(adminAcl));
			ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1);
			Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest(guestAcl));
			ACL acl2 = new ACL(ZooDefs.Perms.READ, id2);
			acls.add(acl1);
			acls.add(acl2);
			// 通过密文创建zk node节点
			ZooKeeper zk = new ZooKeeper(host, 10000, null);
			if (null == zk.exists(nodePath, false)) {
				zk.create(nodePath, nodePwd.getBytes(), acls, CreateMode.PERSISTENT);
				System.out.println("根节点[" + nodePath + "]新增成功");
			} else {
				System.out.println("根节点[" + nodePath + "]已存在");
			}
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(DigestAuthenticationProvider.generateDigest("gg:gg123"));
	}
	
}
