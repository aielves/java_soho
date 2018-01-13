package com.soho.mybatis.crud.aconst;

/**
 * 排序定值枚举
 * 
 * @author shadow
 * 
 */
public enum OPT {

	INSERT("insert"), FIND("findByCnd"), COUNT("countByCnd"), UPDATE("update"), DELETE("deleteByCnd"), FINDBYPAGE("findByPage"), UPDATEBYCND("updateByCnd");

	private OPT(String value) {
		this.value = value;
	}

	private String value;

	public String toString() {
		return value;
	}

}
