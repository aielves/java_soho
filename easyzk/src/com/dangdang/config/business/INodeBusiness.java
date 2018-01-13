package com.dangdang.config.business;

import com.dangdang.config.service.entity.PropertyItemVO;
import java.util.List;

public abstract interface INodeBusiness
{
  public abstract List<PropertyItemVO> findPropertyItems(String paramString1, String paramString2, String paramString3);
}