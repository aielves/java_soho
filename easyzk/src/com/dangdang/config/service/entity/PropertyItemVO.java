package com.dangdang.config.service.entity;

import java.io.Serializable;

public class PropertyItemVO
  implements Serializable, Comparable<PropertyItemVO>
{
  private static final long serialVersionUID = 1L;
  private String oriName;
  private String name;
  private String value;
  private String comment;

  public PropertyItemVO()
  {
  }

  public PropertyItemVO(PropertyItem propertyItem)
  {
    this.name = propertyItem.getName();
    this.oriName = propertyItem.getName();
    this.value = propertyItem.getValue();
  }

  public PropertyItemVO(String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getOriName() {
    return this.oriName;
  }

  public void setOriName(String oriName) {
    this.oriName = oriName;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String toString()
  {
    return "PropertyItemVO [oriName=" + this.oriName + ", name=" + this.name + ", value=" + this.value + ", comment=" + this.comment + "]";
  }

  public int compareTo(PropertyItemVO o)
  {
    return this.name.compareTo(o.getName());
  }
}