package com.dangdang.config.service.zkdao;

import com.dangdang.config.service.entity.PropertyItem;
import java.util.List;

public abstract interface INodeDao
{
  public abstract List<PropertyItem> findProperties(String paramString);

  public abstract List<String> listChildren(String paramString);
}