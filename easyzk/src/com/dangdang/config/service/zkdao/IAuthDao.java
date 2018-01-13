package com.dangdang.config.service.zkdao;

public abstract interface IAuthDao
{
  public abstract boolean checkAuth(String paramString1, String paramString2);

  public abstract boolean auth(String paramString1, String paramString2);
}