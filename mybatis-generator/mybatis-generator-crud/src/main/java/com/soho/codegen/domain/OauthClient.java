package com.soho.codegen.domain;

import com.soho.mybatis.crud.domain.IDEntity;

@SuppressWarnings("serial")
public class OauthClient implements IDEntity<Long> {
    private Long id;

    private String client_id;

    private String client_secret;

    private String company;

    private Long userId;

    private String username;

    private String mobile;

    private String addr;

    private Long teamId;

    private String domain_uri;

    private Long ctime;

    private Long utime;

    private Integer state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id == null ? null : client_id.trim();
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret == null ? null : client_secret.trim();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getDomain_uri() {
        return domain_uri;
    }

    public void setDomain_uri(String domain_uri) {
        this.domain_uri = domain_uri == null ? null : domain_uri.trim();
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}