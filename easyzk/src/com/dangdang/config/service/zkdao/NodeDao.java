package com.dangdang.config.service.zkdao;

import com.dangdang.config.service.entity.PropertyItem;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeDao extends BaseDao
  implements INodeDao
{
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeDao.class);

  public List<PropertyItem> findProperties(String node)
  {
    LOGGER.debug("Find properties in node: [{}]", node);
    List properties = Lists.newArrayList();
    try {
      Stat stat = (Stat)getClient().checkExists().forPath(node);
      if (stat != null) {
        GetChildrenBuilder childrenBuilder = getClient().getChildren();
        List<String> children = (List<String>)childrenBuilder.forPath(node);
        GetDataBuilder dataBuilder = getClient().getData();
        if (children != null)
          for (String child : children) {
            String propPath = ZKPaths.makePath(node, child);
            PropertyItem item = new PropertyItem(child, new String((byte[])dataBuilder.forPath(propPath), Charsets.UTF_8));
            properties.add(item);
          }
      }
    }
    catch (Exception e)
    {
      GetDataBuilder dataBuilder;
      throw Throwables.propagate(e);
    }
    return properties;
  }

  public List<String> listChildren(String node)
  {
    LOGGER.debug("Find children of node: [{}]", node);
    List children = null;
    try {
      Stat stat = (Stat)getClient().checkExists().forPath(node);
      if (stat != null) {
        GetChildrenBuilder childrenBuilder = getClient().getChildren();
        children = (List)childrenBuilder.forPath(node);
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return children;
  }
}