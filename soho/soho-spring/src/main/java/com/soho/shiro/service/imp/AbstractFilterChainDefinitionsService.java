package com.soho.shiro.service.imp;

import com.soho.shiro.service.FilterChainDefinitionsService;
import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 安全框架角色资源配置服务类
 *
 * @author shadow
 */
public abstract class AbstractFilterChainDefinitionsService implements FilterChainDefinitionsService {

    private String definitions = "";

    @Autowired(required = false)
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    public void initDefinitions() {
        shiroFilterFactoryBean.setFilterChainDefinitionMap(fetchDefinitions());
    }

    public void refurbishDefinitions() {
        synchronized (shiroFilterFactoryBean) {
            AbstractShiroFilter shiroFilter = null;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 获取过滤管理器
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                    .getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
            // 清空初始权限配置
            manager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            // 重新构建生成
            shiroFilterFactoryBean.setFilterChainDefinitions(definitions);
            Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                manager.createChain(url, chainDefinition);
            }
            // 加载自定义权限配置
            Map<String, String> otherDefinitions = fetchOtherDefinitions();
            if (otherDefinitions != null && !otherDefinitions.isEmpty()) {
                for (Map.Entry<String, String> entry : chains.entrySet()) {
                    String url = entry.getKey();
                    String chainDefinition = entry.getValue().trim().replace(" ", "");
                    manager.createChain(url, chainDefinition);
                }
            }
        }
    }

    /**
     * 读取配置资源
     */
    private Ini.Section fetchDefinitions() {
        Ini ini = new Ini();
        ini.load(definitions); // 加载资源文件节点串
        Ini.Section section = ini.getSection("urls"); // 使用默认节点
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME); // 如不存在默认节点切割,则使用空字符转换
        }
        Map<String, String> otherDefinitions = fetchOtherDefinitions();
        if (otherDefinitions != null && !otherDefinitions.isEmpty()) {
            section.putAll(otherDefinitions);
        }
        return section;
    }

    public abstract Map<String, String> fetchOtherDefinitions();

    public String getDefinitions() {
        return definitions;
    }

    public void setDefinitions(String definitions) {
        this.definitions = definitions;
    }

}