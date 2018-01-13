package com.dangdang.config.service;

import com.dangdang.config.service.zkdao.IAuthDao;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService
  implements IAuthService, Serializable
{
  private static final long serialVersionUID = 1L;

  @Autowired
  private IAuthDao authDao;

  public boolean checkAuth(String nodeName, String password)
  {
    return this.authDao.checkAuth(nodeName, password);
  }

  public boolean auth(String nodeName, String password)
  {
    return this.authDao.auth(nodeName, password);
  }
}