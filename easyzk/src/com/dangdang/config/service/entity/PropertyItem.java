package com.dangdang.config.service.entity;

import java.io.Serializable;

public class PropertyItem
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String name;
  private String value;

  public PropertyItem(String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}