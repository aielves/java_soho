package com.dangdang.config.service.zkdao;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthDao extends BaseDao
  implements IAuthDao
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthDao.class);

  public boolean checkAuth(String nodeName, String password)
  {
    LOGGER.debug("Check auth: [{}]", nodeName);
    String hash = sha1Digest(password);
    boolean isPass = false;
    try
    {
      Stat stat = (Stat)getClient().checkExists().forPath(nodeName);
      if (stat != null) {
        byte[] data = (byte[])getClient().getData().forPath(nodeName);
        isPass = hash.equals(new String(data));
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return isPass;
  }

  public boolean auth(String nodeName, String password)
  {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Try auth node {} with password ***", nodeName);
    }

    boolean suc = false;
    byte[] sha1Digest = sha1Digest(password).getBytes();
    try
    {
      Stat stat = (Stat)getClient().checkExists().forPath(nodeName);
      if (stat == null) {
        LOGGER.info("Node not exists, create it.");
        getClient().create().creatingParentsIfNeeded().forPath(nodeName, sha1Digest);
        suc = true;
      } else {
        LOGGER.info("Node exists.");
        byte[] data = (byte[])getClient().getData().forPath(nodeName);

        if ((data == null) || ((data.length == 1) && (data[0] != 0))) {
          getClient().setData().forPath(nodeName, sha1Digest);
          suc = true;
          LOGGER.info("Auth done.");
        } else {
          LOGGER.info("Node has been authed, cannot do duplicated authentication.");
        }
      }
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return suc;
  }

  private String sha1Digest(String text) {
    return Hashing.sha1().hashBytes(text.getBytes()).toString();
  }
}