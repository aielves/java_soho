package com.soho.codegen.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.soho.codegen.domain.DeftConfig;
import com.soho.shiro.utils.SpringUtils;

/**
 * Created by shadow on 2018/5/24.
 */
public class GGKUtils {

    public static IAcsClient iAcsClient = null;

    static {
        try {
            DeftConfig config = SpringUtils.getBean(DeftConfig.class);
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", config.getAliAppId(), config.getAliAppKey());
            iAcsClient = new DefaultAcsClient(profile);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "afs", "afs.aliyuncs.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
