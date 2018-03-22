package com.soho.web.domain;

/**
 * Created by Administrator on 2018/3/22.
 */
public class ProjectInfo {

    private String name = "TEST项目";
    private String code = "deft";
    private String company = "TEST公司";
    private String author = "TEST";
    private String create = "2018/01/01";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }
}
