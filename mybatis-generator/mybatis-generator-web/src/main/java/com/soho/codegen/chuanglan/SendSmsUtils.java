package com.soho.codegen.chuanglan;

import com.alibaba.fastjson.JSON;
import com.soho.codegen.domain.DxSms;
import com.soho.codegen.service.DxSmsService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.sqlcode.aconst.SortBy;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.shiro.utils.SpringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shadow on 2017/10/18.
 */
public class SendSmsUtils {

    public static final String charset = "utf-8";
    // 用户平台API账号(非登录账号,示例:N1234567)
    public static String account = "";
    // 用户平台API密码(非登录密码)
    public static String pswd = "";
    //状态报告
    public static String report = "true";
    // 发送短信地址
    public static String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";

    // 1.短信验证码
    public static void toSend(int dxtype, String areacode, String mobile) throws BizErrorEx {
        if (!isMobile(mobile)) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机号码不合法");
        }
        DxSmsService dxSmsService = SpringUtils.getBean(DxSmsService.class);
        DxSms sms = dxSmsService.findOneByCnd(new SQLCnd().eq("mobile", mobile).eq("dxtype", dxtype).eq("usestate", 1).gt("sendtime", (System.currentTimeMillis() - 60000l)).orderby("sendtime", SortBy.D).limit(1, 1));
        if (sms != null) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "一分钟内不能重复两次发送");
        }
        sms = new DxSms();
        sms.setAreacode(areacode);
        sms.setMobile(mobile);
        sms.setDxtype(dxtype);
        sms.setSendtime(System.currentTimeMillis());
        sms.setValidtime(sms.getSendtime() + 600000l);
        String code = buildValidCode();
        if (dxtype == 1) {
            sms.setCode(code);
            sms.setContent("【SOHO科技】您的验证码是: " + code + "，10分钟内有效。");
        } else {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "暂不支持其他类型发送");
        }
        SmsSendRequest smsSingleRequest = new SmsSendRequest(account, pswd, sms.getContent(), sms.getMobile(), report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        sms.setReqstate(requestJson);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        sms.setCallstate(response);
        sms.setCtime(sms.getSendtime());
        if (response != null) {
            dxSmsService.insert(sms);
        } else {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "短信发送失败,请重新尝试");
        }
    }

    private static String buildValidCode() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            buffer.append(new Random().nextInt(9));
        }
        return buffer.toString();
    }

    // 校验短信验证码
    public static DxSms validSmsCode(String areacode, String mobile, String smscode) throws BizErrorEx {
        if (!isMobile(mobile)) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机号码不合法");
        }
        DxSmsService dxSmsService = SpringUtils.getBean(DxSmsService.class);
        DxSms dx = dxSmsService.findOneByCnd(new SQLCnd().eq("usestate", 1).eq("areacode", areacode).eq("mobile", mobile).eq("code", smscode).orderby("ctime", SortBy.D).limit(1, 1));
        if (dx == null) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机验证码错误");
        }
        if (dx.getValidtime() < System.currentTimeMillis()) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机验证码已过期");
        }
        if (!dx.getMobile().equals(mobile)) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机号码不一致");
        }
        if (dx.getState() > 5) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "手机验证码已过期");
        }
        if (StringUtils.isEmpty(dx.getCode()) || !dx.getCode().equals(smscode.trim())) {
            dx.setState(dx.getState() + 1);
            dxSmsService.update(dx);
            return null;
        }
        return dx;
    }

    // 短信设置为已使用
    public static void delSmsCode(DxSms sms) throws BizErrorEx {
        sms.setUtime(System.currentTimeMillis());
        sms.setUsestate(2);
        DxSmsService dxSmsService = SpringUtils.getBean(DxSmsService.class);
        dxSmsService.update(sms);
    }

    public static void toSendByOther(int dxtype, String areacode, String mobile) throws BizErrorEx {
        DxSmsService dxSmsService = SpringUtils.getBean(DxSmsService.class);
        DxSms sms = dxSmsService.findOneByCnd(new SQLCnd().eq("mobile", mobile).eq("dxtype", dxtype).eq("usestate", 1).gt("sendtime", (System.currentTimeMillis() - 60000l)).orderby("sendtime", SortBy.D).limit(1, 1));
        if (sms != null) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "一分钟内不能重复两次发送");
        }
        sms = new DxSms();
        sms.setAreacode(areacode);
        sms.setMobile(mobile);
        sms.setDxtype(dxtype);
        sms.setSendtime(System.currentTimeMillis());
        sms.setValidtime(sms.getSendtime() + 600000l);
        String code = buildValidCode();
        String content = "MCH - Your verification code is: " + code + ", valid within 10 minutes.";
        if (dxtype == 1) {
            sms.setCode(code);
            sms.setContent(content);
        } else {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "暂不支持其他类型发送");
        }
        HttpClient httpClient = new DefaultHttpClient();
        String loginUrl = "https://rest.nexmo.com/sms/json";
        HttpPost httpPatch = new HttpPost(loginUrl);
        // httpPatch.setHeader("Content-type", "application/json");
        httpPatch.setHeader("Charset", HTTP.UTF_8);
        httpPatch.setHeader("Accept", "application/json");
        httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);
        try {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("api_key", "3ad7b6e8"));
            nvps.add(new BasicNameValuePair("api_secret", "201b1c9735d729ba"));
            nvps.add(new BasicNameValuePair("from", "NEXMO"));
            nvps.add(new BasicNameValuePair("to", areacode + mobile));
            nvps.add(new BasicNameValuePair("text", content));
            sms.setReqstate(JSON.toJSONString(nvps));
            httpPatch.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = httpClient.execute(httpPatch);
            String result = EntityUtils.toString(response.getEntity());
            sms.setCallstate(result);
            sms.setCtime(sms.getSendtime());
            if (response != null) {
                dxSmsService.insert(sms);
            } else {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "短信发送失败,请重新尝试");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMobile(String mobile) {
        if (!StringUtils.isEmpty(mobile) && mobile.matches("^1[3|4|5|6|7|8|9][0-9]{9}")) {
            return true;
        }
        return false;
    }

}
