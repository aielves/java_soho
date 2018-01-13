/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.config;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author shadow
 */
public class JavaParamConfiguration extends TypedPropertyHolder {

    private String baseProject; // 文件存放目录
    private String basePackage; // 文件分类目录
    private String baseDAO; // baseDAO类路径
    private String baseService; // baseService类路径
    private String buildType; // 1.dao层 2.service层 3.control层,4.domain层
    private String mapperExt; // dao文件后缀配置
    private String cndClass; // 条件对象路径
    private String baseModel; // baseModel路径
    private String baseFXType; // 基础模型泛型类型

    private String domainPath = ".domain";
    private String servicePath = ".service";
    private String serviceImpPath = ".service.imp";
    private String daoPath = ".dao";
    private String daoImpPath = ".dao.imp";
    private String daoMapperPath = ".dao.mapper";


    public JavaParamConfiguration() {
        super();
    }

    public String getBaseDAO() {
        return baseDAO;
    }

    public void setBaseDAO(String baseDAO) {
        this.baseDAO = baseDAO;
    }

    public String getBaseService() {
        return baseService;
    }

    public void setBaseService(String baseService) {
        this.baseService = baseService;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getMapperExt() {
        return mapperExt;
    }

    public void setMapperExt(String mapperExt) {
        this.mapperExt = mapperExt;
    }

    public String getCndClass() {
        return cndClass;
    }

    public void setCndClass(String cndClass) {
        this.cndClass = cndClass;
    }

    public String getBaseModel() {
        return baseModel;
    }

    public void setBaseModel(String baseModel) {
        this.baseModel = baseModel;
    }

    public String getBaseFXType() {
        return baseFXType;
    }

    public void setBaseFXType(String baseFXType) {
        this.baseFXType = baseFXType;
    }

    public String getBaseProject() {
        return baseProject;
    }

    public void setBaseProject(String baseProject) {
        this.baseProject = baseProject;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getDomainPath() {
        return domainPath;
    }

    public void setDomainPath(String domainPath) {
        this.domainPath = domainPath;
    }

    public String getServicePath() {
        return servicePath;
    }

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public String getServiceImpPath() {
        return serviceImpPath;
    }

    public void setServiceImpPath(String serviceImpPath) {
        this.serviceImpPath = serviceImpPath;
    }

    public String getDaoPath() {
        return daoPath;
    }

    public void setDaoPath(String daoPath) {
        this.daoPath = daoPath;
    }

    public String getDaoImpPath() {
        return daoImpPath;
    }

    public void setDaoImpPath(String daoImpPath) {
        this.daoImpPath = daoImpPath;
    }

    public String getDaoMapperPath() {
        return daoMapperPath;
    }

    public void setDaoMapperPath(String daoMapperPath) {
        this.daoMapperPath = daoMapperPath;
    }

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaParam"); //$NON-NLS-1$

        if (baseDAO != null) {
            answer.addAttribute(new Attribute("baseDAO", baseDAO)); //$NON-NLS-1$
        }

        if (baseService != null) {
            answer.addAttribute(new Attribute("baseService", baseService)); //$NON-NLS-1$
        }

        if (buildType != null) {
            answer.addAttribute(new Attribute("buildType", buildType)); //$NON-NLS-1$
        }

        if (mapperExt != null) {
            answer.addAttribute(new Attribute("mapperExt", mapperExt)); //$NON-NLS-1$
        }

        if (cndClass != null) {
            answer.addAttribute(new Attribute("cndClass", cndClass)); //$NON-NLS-1$
        }

        if (baseModel != null) {
            answer.addAttribute(new Attribute("baseModel", baseModel)); //$NON-NLS-1$
        }

        if (baseFXType != null) {
            answer.addAttribute(new Attribute("baseFXType", baseFXType)); //$NON-NLS-1$
        }

        if (baseProject != null) {
            answer.addAttribute(new Attribute("baseProject", baseProject)); //$NON-NLS-1$
        }

        if (basePackage != null) {
            answer.addAttribute(new Attribute("basePackage", basePackage)); //$NON-NLS-1$
        }

        addPropertyXmlElements(answer);

        return answer;
    }

    public void validate(List<String> errors, String contextId) {

        if (!stringHasValue(baseDAO)) {
            errors.add(getString("ValidationError.12", //$NON-NLS-1$
                    "JavaParamConfiguration", contextId)); //$NON-NLS-1$
        }

        if (!stringHasValue(baseService)) {
            errors.add(getString("ValidationError.13", //$NON-NLS-1$
                    "JavaParamConfiguration", contextId)); //$NON-NLS-1$
        }

        if (!stringHasValue(buildType)) {
            errors.add(getString("ValidationError.14", //$NON-NLS-1$
                    "JavaParamConfiguration", contextId)); //$NON-NLS-1$
        }

    }
}