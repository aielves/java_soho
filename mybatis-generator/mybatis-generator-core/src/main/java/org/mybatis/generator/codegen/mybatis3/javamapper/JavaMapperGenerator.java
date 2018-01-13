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
package org.mybatis.generator.codegen.mybatis3.javamapper;

import com.soho.codegen.domain.DbMessage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.*;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class JavaMapperGenerator extends AbstractJavaClientGenerator {

    /**
     *
     */
    public JavaMapperGenerator() {
        super(true);
    }

    public JavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
        super(requiresMatchedXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String oldDaoName = introspectedTable.getMyBatis3JavaMapperType();

        String str1 = oldDaoName.substring(0, oldDaoName.lastIndexOf("."));
        String str2 = oldDaoName.replaceAll(str1, "");
        String currentDaoName = str1 + context.getJavaParamConfiguration().getDaoPath() + str2;

        try {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                str1 = dbMessage.getPackageName();
                str2 = oldDaoName.substring(oldDaoName.lastIndexOf("."));
                currentDaoName = str1 + context.getJavaParamConfiguration().getDaoPath() + str2;
            }
        } catch (Exception e) {
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(currentDaoName);

        String oldModelName = introspectedTable.getBaseRecordType();
        str1 = oldModelName.substring(0, oldModelName.lastIndexOf("."));
        str2 = oldModelName.replaceAll(str1, "");
        String currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;

        try {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                str1 = dbMessage.getPackageName();
                str2 = oldModelName.substring(oldModelName.lastIndexOf("."));
                currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;
            }
        } catch (Exception e) {
        }

        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        String superDAOClass = context.getJavaParamConfiguration().getBaseDAO();
        interfaze.addImportedType(new FullyQualifiedJavaType(superDAOClass));
        FullyQualifiedJavaType superIface = new FullyQualifiedJavaType(superDAOClass.substring(superDAOClass.lastIndexOf(".") + 1));
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(currentModelName.substring(currentModelName.lastIndexOf(".") + 1));
        superIface.addTypeArgument(recordType);
        superIface.addTypeArgument(new FullyQualifiedJavaType(context.getJavaParamConfiguration().getBaseFXType()));
        interfaze.addSuperInterface(superIface);
        interfaze.addImportedType(new FullyQualifiedJavaType(currentModelName));

//        addCountByExampleMethod(interfaze);
//        addDeleteByExampleMethod(interfaze);
//        addDeleteByPrimaryKeyMethod(interfaze);
//        addInsertMethod(interfaze);
//        addInsertSelectiveMethod(interfaze);
//        addSelectByExampleWithBLOBsMethod(interfaze);
//        addSelectByExampleWithoutBLOBsMethod(interfaze);
//        addSelectByPrimaryKeyMethod(interfaze);
//        addUpdateByExampleSelectiveMethod(interfaze);
//        addUpdateByExampleWithBLOBsMethod(interfaze);
//        addUpdateByExampleWithoutBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeySelectiveMethod(interfaze);
//        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
//        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            String seletor = context.getJavaParamConfiguration().getBuildType();
            try {
                Session session = SecurityUtils.getSubject().getSession();
                if (session != null) {
                    DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                    seletor = dbMessage.getModuleName();
                }
            } catch (Exception e) {
            }
            if (seletor != null && !"".equals(seletor)) {
                if (seletor.indexOf("1") != -1) {
                    answer.add(interfaze); // dao接口
                    answer.add(getDaoImpClass(currentDaoName, currentModelName)); // dao实现类
                }
                if (seletor.indexOf("2") != -1) {
                    answer.add(getServiceInterface(currentDaoName, currentModelName)); // serice接口
                    answer.add(getServiceImpClass(currentDaoName, currentModelName));  // service实现类
                }
                if (seletor.indexOf("3") != -1) {
                    answer.add(getControllerClass(currentDaoName, currentModelName));  // web实现类
                }
            }
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    public TopLevelClass getDaoImpClass(String currentDaoName, String currentModelName) {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String str1 = currentDaoName.substring(0, currentDaoName.lastIndexOf("."));
        String str2 = currentDaoName.replaceAll(str1, "");
        String superDAOClass = str1 + ".imp" + str2 + "Imp";

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                superDAOClass);
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        topLevelClass.addAnnotation("@Repository");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        String mybatisDaoImp = context.getJavaParamConfiguration().getBaseDAO() + "Imp";

        str1 = mybatisDaoImp.substring(0, mybatisDaoImp.lastIndexOf("."));
        str2 = mybatisDaoImp.replaceAll(str1, "");
        String currentMybatisDaoImp = str1 + ".imp" + str2;

        FullyQualifiedJavaType superIface = new FullyQualifiedJavaType(superDAOClass.substring(superDAOClass.lastIndexOf(".") + 1));
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(currentModelName.substring(currentModelName.lastIndexOf(".") + 1));
        superIface.addTypeArgument(recordType);

        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentMybatisDaoImp));
        FullyQualifiedJavaType mybatisSuperClass = new FullyQualifiedJavaType(currentMybatisDaoImp);
        mybatisSuperClass.addTypeArgument(recordType);
        topLevelClass.setSuperClass(mybatisSuperClass);

        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentModelName));
        FullyQualifiedJavaType superRecordIface = new FullyQualifiedJavaType(currentDaoName.substring(currentDaoName.lastIndexOf(".") + 1));
        topLevelClass.addSuperInterface(superRecordIface);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentDaoName));
        return topLevelClass;
    }

    public Interface getServiceInterface(String currentDaoName, String currentModelName) {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();


        String oldModelName = introspectedTable.getBaseRecordType();
        String str1 = oldModelName.substring(0, oldModelName.lastIndexOf("."));
        String str2 = oldModelName.replaceAll(str1, "");
        currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;

        try {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                str1 = dbMessage.getPackageName();
                str2 = oldModelName.substring(oldModelName.lastIndexOf("."));
                currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;
            }
        } catch (Exception e) {
        }

        String currentServiceName = str1 + context.getJavaParamConfiguration().getServicePath() + str2 + "Service";
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(currentServiceName);

        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        String superServiceClass = context.getJavaParamConfiguration().getBaseService();
        interfaze.addImportedType(new FullyQualifiedJavaType(superServiceClass));
        FullyQualifiedJavaType superIface = new FullyQualifiedJavaType(superServiceClass.substring(superServiceClass.lastIndexOf(".") + 1));
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(currentModelName.substring(currentModelName.lastIndexOf(".") + 1));
        superIface.addTypeArgument(recordType);
        superIface.addTypeArgument(new FullyQualifiedJavaType(context.getJavaParamConfiguration().getBaseFXType()));
        interfaze.addSuperInterface(superIface);
        interfaze.addImportedType(new FullyQualifiedJavaType(currentModelName));
        return interfaze;
    }

    public TopLevelClass getServiceImpClass(String currentDaoName, String currentModelName) {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String oldModelName = introspectedTable.getBaseRecordType();
        String str1 = oldModelName.substring(0, oldModelName.lastIndexOf("."));
        String str2 = oldModelName.replaceAll(str1, "");
        currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;

        try {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                str1 = dbMessage.getPackageName();
                str2 = oldModelName.substring(oldModelName.lastIndexOf("."));
                currentModelName = str1 + context.getJavaParamConfiguration().getDomainPath() + str2;
            }
        } catch (Exception e) {
        }

        String currentServiceName = str1 + context.getJavaParamConfiguration().getServiceImpPath() + str2 + "ServiceImp";
        String baseCurrentServiceName = str1 + context.getJavaParamConfiguration().getServicePath() + str2 + "Service";
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(currentServiceName);

        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        topLevelClass.addAnnotation("@Service");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));

        commentGenerator.addJavaFileComment(topLevelClass);

        Field field = new Field();
        field.setType(new FullyQualifiedJavaType(str2.substring(1) + "DAO"));
        field.setName(str2.substring(1).toLowerCase() + "DAO");
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation("@Autowired");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentDaoName));
        topLevelClass.addField(field);

        Method method = new Method();
        method.setName("getMybatisDAO");
        method.setVisibility(JavaVisibility.PUBLIC);
        String retDAO = context.getJavaParamConfiguration().getBaseDAO();
        FullyQualifiedJavaType retDAOType = new FullyQualifiedJavaType(retDAO.substring(retDAO.lastIndexOf(".") + 1));
        retDAOType.addTypeArgument(new FullyQualifiedJavaType(str2.substring(1)));
        retDAOType.addTypeArgument(new FullyQualifiedJavaType(context.getJavaParamConfiguration().getBaseFXType()));
        method.setReturnType(retDAOType);
        method.setDefault(true);
        method.addBodyLine("return " + str2.substring(1).toLowerCase() + "DAO" + ";");
        topLevelClass.addImportedType(new FullyQualifiedJavaType(retDAO));
        topLevelClass.addMethod(method);


        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        String baseServiceImp = context.getJavaParamConfiguration().getBaseService() + "Imp";

        str1 = baseServiceImp.substring(0, baseServiceImp.lastIndexOf("."));
        str2 = baseServiceImp.replaceAll(str1, "");
        String currentBaseServiceImp = str1 + ".imp" + str2;

        FullyQualifiedJavaType superIface = new FullyQualifiedJavaType(currentBaseServiceImp.substring(currentBaseServiceImp.lastIndexOf(".") + 1));
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(currentModelName.substring(currentModelName.lastIndexOf(".") + 1));
        superIface.addTypeArgument(recordType);

        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentBaseServiceImp));
        FullyQualifiedJavaType serviceSuperClass = new FullyQualifiedJavaType(currentBaseServiceImp);
        serviceSuperClass.addTypeArgument(recordType);
        serviceSuperClass.addTypeArgument(new FullyQualifiedJavaType(context.getJavaParamConfiguration().getBaseFXType()));
        topLevelClass.setSuperClass(serviceSuperClass);

        topLevelClass.addImportedType(new FullyQualifiedJavaType(currentModelName));

        FullyQualifiedJavaType superRecordIface = new FullyQualifiedJavaType(baseCurrentServiceName.substring(baseCurrentServiceName.lastIndexOf(".") + 1));
        topLevelClass.addSuperInterface(superRecordIface);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(baseCurrentServiceName));
        return topLevelClass;
    }

    public TopLevelClass getControllerClass(String currentDaoName, String currentModelName) {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        String oldModelName = introspectedTable.getBaseRecordType();
        String str1 = oldModelName.substring(0, oldModelName.lastIndexOf("."));
        String str2 = oldModelName.replaceAll(str1, "");

        String path = str1 + ".controller" + str2 + "Controller";

        try {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                DbMessage dbMessage = (DbMessage) session.getAttribute("dbmessage");
                str1 = dbMessage.getPackageName();
                str2 = oldModelName.substring(oldModelName.lastIndexOf("."));
                path = str1 + ".controller" + str2 + "Controller";
            }
        } catch (Exception e) {
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(path);

        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        commentGenerator.addJavaFileComment(topLevelClass);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Controller"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping"));

        topLevelClass.addAnnotation("@Controller");
        topLevelClass.addAnnotation("@RequestMapping(\"/" + str2.substring(1).toLowerCase() + "\")");

        return topLevelClass;
    }


    protected void addCountByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void initializeAndExecuteGenerator(
            AbstractJavaMapperMethodGenerator methodGenerator,
            Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

    public List<CompilationUnit> getExtraCompilationUnits() {
        return null;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGenerator();
    }
}
