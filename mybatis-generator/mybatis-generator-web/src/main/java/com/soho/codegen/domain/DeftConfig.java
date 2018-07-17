package com.soho.codegen.domain;

/**
 * Created by Administrator on 2017/5/25.
 */
public class DeftConfig {

    private String fileDirPath;
    private String configXmlPath;
    private String domain;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFileDirPath() {
        return fileDirPath;
    }

    public void setFileDirPath(String fileDirPath) {
        this.fileDirPath = fileDirPath;
    }

    public String getConfigXmlPath() {
        return configXmlPath;
    }

    public void setConfigXmlPath(String configXmlPath) {
        this.configXmlPath = configXmlPath;
    }
}
