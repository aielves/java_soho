package com.dangdang.config.service.zkdao;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundVersionable;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyDao extends BaseDao
  implements IPropertyDao
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyDao.class);

  public boolean createProperty(String nodeName, String value)
  {
    LOGGER.debug("Create property : [{}] = [{}]", nodeName, value);
    boolean suc = false;
    try {
      Stat stat = (Stat)getClient().checkExists().forPath(nodeName);
      if (stat == null) {
        String opResult = null;
        if (Strings.isNullOrEmpty(value))
          opResult = (String)getClient().create().creatingParentsIfNeeded().forPath(nodeName);
        else {
          opResult = (String)getClient().create().creatingParentsIfNeeded().forPath(nodeName, value.getBytes(Charsets.UTF_8));
        }
        suc = Objects.equal(nodeName, opResult);
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return suc;
  }

  public boolean updateProperty(String nodeName, String value)
  {
    LOGGER.debug("Update property: [{}] = [{}]", nodeName, value);
    boolean suc = false;
    try {
      Stat stat = (Stat)getClient().checkExists().forPath(nodeName);
      if (stat != null) {
        Stat opResult = (Stat)getClient().setData().forPath(nodeName, value.getBytes(Charsets.UTF_8));
        suc = opResult != null;
      } else {
        String opResult = (String)getClient().create().creatingParentsIfNeeded().forPath(nodeName, value.getBytes(Charsets.UTF_8));
        suc = Objects.equal(nodeName, opResult);
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return suc;
  }

  public void deleteProperty(String nodeName)
  {
    LOGGER.debug("Delete property: [{}]", nodeName);
    try {
      Stat stat = (Stat)getClient().checkExists().forPath(nodeName);
      if (stat != null)
        getClient().delete().deletingChildrenIfNeeded().forPath(nodeName);
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}