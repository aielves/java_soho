package com.dangdang.config.service.zkdao;

public abstract interface IPropertyDao
{
  public abstract boolean createProperty(String paramString1, String paramString2);

  public abstract boolean updateProperty(String paramString1, String paramString2);

  public abstract void deleteProperty(String paramString);
}