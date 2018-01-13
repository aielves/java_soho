package com.dangdang.config.service;

import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.zkdao.INodeDao;
import com.dangdang.config.service.zkdao.IPropertyDao;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeService
  implements INodeService, Serializable
{
  private static final long serialVersionUID = 1L;

  @Autowired
  private INodeDao nodeDao;

  @Autowired
  private IPropertyDao propertyDao;

  public List<PropertyItem> findProperties(String node)
  {
    return this.nodeDao.findProperties(node);
  }

  public List<String> listChildren(String node)
  {
    List children = this.nodeDao.listChildren(node);
    if (children != null) {
      Collections.sort(children);
    }
    return children;
  }

  public boolean createProperty(String nodeName, String value)
  {
    return this.propertyDao.createProperty(nodeName, value);
  }

  public boolean updateProperty(String nodeName, String value)
  {
    return this.propertyDao.updateProperty(nodeName, value);
  }

  public void deleteProperty(String nodeName)
  {
    this.propertyDao.deleteProperty(nodeName);
  }
}