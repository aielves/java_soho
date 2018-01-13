package com.dangdang.config.service;

public abstract interface IAuthService
{
  public abstract boolean checkAuth(String paramString1, String paramString2);

  public abstract boolean auth(String paramString1, String paramString2);
}