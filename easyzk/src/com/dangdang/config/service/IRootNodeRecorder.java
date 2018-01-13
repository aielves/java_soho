package com.dangdang.config.service;

import java.util.List;

public abstract interface IRootNodeRecorder
{
  public abstract void saveNode(String paramString);

  public abstract List<String> listNode();
}