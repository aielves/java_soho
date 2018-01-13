package com.dangdang.config.service;

import com.dangdang.config.service.entity.PropertyItem;
import java.util.List;

public abstract interface INodeService
{
  public abstract List<PropertyItem> findProperties(String paramString);

  public abstract List<String> listChildren(String paramString);

  public abstract boolean createProperty(String paramString1, String paramString2);

  public abstract boolean updateProperty(String paramString1, String paramString2);

  public abstract void deleteProperty(String paramString);
}