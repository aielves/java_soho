package com.soho.codegen.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by shadow on 2018/5/24.
 */
public class GGKUtils {

    public static IAcsClient iAcsClient = null;
    public static String appId = "";
    public static String appKey = "";

    static {
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", appId, appKey);
            iAcsClient = new DefaultAcsClient(profile);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "afs", "afs.aliyuncs.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
