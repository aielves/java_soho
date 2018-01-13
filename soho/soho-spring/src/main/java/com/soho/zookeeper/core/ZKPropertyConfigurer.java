package com.soho.zookeeper.core;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.soho.zookeeper.security.Decipher;

/**
 * Spring加载配置文件,以及ZooKeeper中心配置信息
 * 
 * @author shadow
 * 
 */
class ZKPropertyConfigurer extends PropertyPlaceholderConfigurer {

	private final static Logger log = LoggerFactory.getLogger(ZKPropertyConfigurer.class);

	private final static String CONNECTION_URL = "zk.connectionUrl";
	private final static String ACL_AUTH = "zk.aclAuth";
	private final static String VERSION = "zk.version";
	private final static String ROOT_NODE = "zk.rootNode";
	private final static String GROUP_NODES = "zk.groupNodes";
	private final static String ENABLE = "zk.enable";
	private final static String TIME_OUT = "zk.timeout";

	private Decipher decipher;
	private String[] decipherKeys;
	private Resource[] locations;
	private boolean ignoreResourceNotFound = false;
	private String fileEncoding;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
		try {
			createZkConfig(props);
			dechipher(props);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		super.processProperties(beanFactoryToProcess, props);
	}

	/** 重置加密数据为明文 */
	private void dechipher(Properties props) throws Exception {
		if (decipherKeys != null) {
			if (decipher == null) {
				throw new Exception("not found dechipher instance...");
			}
			for (String decipherKey : decipherKeys) {
				String value = props.getProperty(decipherKey);
				if (value != null) {
					props.put(decipherKey, decipher.decode(value));
				}
			}
		}
	}

	/** 加载配置文件 */
	private void createZkConfig(Properties props) {
		String enable = props.getProperty(ENABLE);
		String connectionUrl = props.getProperty(CONNECTION_URL);
		String aclAuth = props.getProperty(ACL_AUTH);
		String connectionTimeoutMs = props.getProperty(TIME_OUT);
		String version = props.getProperty(VERSION);
		String rootNode = props.getProperty(ROOT_NODE);
		String groupNodes = props.getProperty(GROUP_NODES);
		ZKConfig zkConfig = new ZKConfig();
		zkConfig.setEnable(enable);
		zkConfig.setConnectionUrl(connectionUrl);
		zkConfig.setAclAuth(aclAuth);
		zkConfig.setConnectionTimeoutMs(connectionTimeoutMs != null ? Integer.parseInt(connectionTimeoutMs) : 100000);
		zkConfig.setVersion(version);
		zkConfig.setRootNode(rootNode);
		zkConfig.setGroupNodes(groupNodes);
		Map<String, Object> zkConfigMap = ZKFactory.loadConfigMessage(zkConfig);
		props.putAll(zkConfigMap);
	}

	protected void loadProperties(Properties props) throws IOException {
		if (locations != null) {
			for (Resource location : this.locations) {
				if (!location.exists()) {
					continue;
				}
				if (logger.isInfoEnabled()) {
					logger.info("Loading properties file from " + location);
				}
				try {
					PropertiesLoaderUtils.fillProperties(props, new EncodedResource(location, this.fileEncoding));
				} catch (IOException ex) {
					if (this.ignoreResourceNotFound) {
						if (logger.isWarnEnabled()) {
							logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
						}
					} else {
						throw ex;
					}
				}
			}
		}
	}

	public Resource[] getLocations() {
		return locations;
	}

	public void setLocations(Resource[] locations) {
		this.locations = locations;
	}

	public boolean isIgnoreResourceNotFound() {
		return ignoreResourceNotFound;
	}

	public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
		this.ignoreResourceNotFound = ignoreResourceNotFound;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public Decipher getDecipher() {
		return decipher;
	}

	public void setDecipher(Decipher decipher) {
		this.decipher = decipher;
	}

	public String[] getDecipherKeys() {
		return decipherKeys;
	}

	public void setDecipherKeys(String[] decipherKeys) {
		this.decipherKeys = decipherKeys;
	}

}
