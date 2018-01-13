package com.soho.zookeeper.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取ZooKeeper中心配置信息
 * 
 * @author shadow
 * 
 */
public class ZKFactory {

	private final static Logger log = LoggerFactory.getLogger(ZKFactory.class);

	/**
	 * 创建链接客户端
	 * 
	 * @param zkConfig
	 * @return CuratorFramework
	 */
	private static CuratorFramework createZkClient(ZKConfig zkConfig) {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkConfig.getSleepTimeMs(), zkConfig.getMaxRetries());
		byte auths[] = null;
		if (zkConfig.getAclAuth() != null && !"".equals(zkConfig.getAclAuth())) {
			auths = zkConfig.getAclAuth().trim().getBytes();
		} else {
			auths = "".getBytes();
		}
		return CuratorFrameworkFactory.builder().connectString(zkConfig.getConnectionUrl()).retryPolicy(retryPolicy)
				.connectionTimeoutMs(zkConfig.getConnectionTimeoutMs()).authorization("digest", auths)
				.sessionTimeoutMs(zkConfig.getSessionTimeoutMs()).build();
	}

	/**
	 * 读取配置中心信息到MAP集合
	 * 
	 * @param zkConfig
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> loadConfigMessage(ZKConfig zkConfig) {
		Map<String, Object> zkConfigMap = new HashMap<String, Object>();
		if (zkConfig.getEnable() != null && !"".equals(zkConfig.getEnable())) {
			boolean isOpen = Boolean.valueOf(zkConfig.getEnable().trim());
			if (!isOpen) {
				return zkConfigMap;
			}
		}
		CuratorFramework client = createZkClient(zkConfig);
		client.start();
		String versionedRootNode = ZKPaths.makePath(zkConfig.getRootNode().trim(), zkConfig.getVersion().trim());
		try {
			String[] groups = zkConfig.getGroupNodes().split(",");
			if (groups != null) {
				for (String group : groups) {
					String versionedGroupNode = ZKPaths.makePath(versionedRootNode, group.trim());
					List<String> nodeKeys = client.getChildren().forPath(versionedGroupNode);
					for (String nodeKey : nodeKeys) {
						byte[] be = client.getData().forPath(ZKPaths.makePath(versionedGroupNode, nodeKey.trim()));
						if (be != null) {
							zkConfigMap.put(nodeKey.trim(), new String(be, "utf-8").trim());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return zkConfigMap;
	}

}
