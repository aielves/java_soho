package com.soho.shiro.utils;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    /**
     * 判断是否异步请求
     */
    public static boolean isAjax(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader(
                "X-Requested-With").toString()));
    }

    /**
     * 判断是否API请求
     */
    public static boolean isAPI(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return (queryString != null && queryString.indexOf("access_token") != -1);
    }

    /**
     * 获取发送的异常信息
     */
    public static void sendTextMsg(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取发送的异常信息
     */
    public static void sendJsonMsg(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取客户IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }

        }

        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    // 发送Post请求,返回JSON字符串,请求失败返回空字符串
    public static String sendPost(String url, Map<String, String> map) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPatch = new HttpPost(url);
        // httpPatch.setHeader("Content-type", "application/json");
        httpPatch.setHeader("Charset", HTTP.UTF_8);
        // httpPatch.setHeader("Accept", "application/json");
        httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            if (map != null && !map.isEmpty()) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPatch.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = httpClient.execute(httpPatch);
            String result = EntityUtils.toString(response.getEntity());
            if (!StringUtils.isEmpty(result)) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
