package com.soho.codegen.domain;

import com.soho.mybatis.crud.domain.IDEntity;

@SuppressWarnings("serial")
public class DbMessage implements IDEntity<Long> {
    private Long id;

    private Long userId;

    private String moduleName;

    private String packageName;

    private Integer dbType;

    private String jdbcDriver;

    private String jdbcUrl;

    private String username;

    private String password;

    private String tables;

    private Long ctime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName == null ? null : moduleName.trim();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName == null ? null : packageName.trim();
    }

    public Integer getDbType() {
        return dbType;
    }

    public void setDbType(Integer dbType) {
        this.dbType = dbType;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver == null ? null : jdbcDriver.trim();
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl == null ? null : jdbcUrl.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables == null ? null : tables.trim();
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }
}