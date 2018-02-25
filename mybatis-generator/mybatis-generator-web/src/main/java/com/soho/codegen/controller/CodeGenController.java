package com.soho.codegen.controller;

import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.CodeGenService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.shiro.oauth2.aconst.OAuth2Client;
import com.soho.shiro.utils.HttpUtils;
import com.soho.shiro.utils.SessionUtils;
import com.soho.web.domain.Ret;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/codegen")
public class CodeGenController {

    @Autowired
    private CodeGenService codeGenService;

    @Autowired
    private OAuth2Client oAuth2Client;

    @RequestMapping(value = "/logout")
    @ResponseBody
    public Object logout() {
        Map<String, String> map = (Map<String, String>) SessionUtils.getAttribute("tokeninfo");
        String s = HttpUtils.sendPost(oAuth2Client.getLogout_token_uri() + "?access_token=" + map.get("access_token") + "&access_pbk=" + map.get("access_pbk"), null);
        SecurityUtils.getSubject().logout();
        return new ModelAndView("redirect:" + oAuth2Client.getDomain_uri() + "?ts=" + System.currentTimeMillis());
    }

    @RequestMapping(value = "/generate")
    @ResponseBody
    public Object generate(DbMessage dbMessage) throws BizErrorEx {
        return codeGenService.generate(dbMessage);
    }

    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public Object uploadFile(Integer uploadtype, @RequestParam("file") MultipartFile file) throws BizErrorEx {
        return codeGenService.uploadFile(uploadtype, file);
    }

    @RequestMapping(value = "/getUser")
    @ResponseBody
    public Object generate() throws BizErrorEx {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            Session session = SecurityUtils.getSubject().getSession();
            if (session != null) {
                Object obj = session.getAttribute("session_user");
                if (obj != null) {
                    OauthUser user = (OauthUser) obj;
                    user.setPassword(null);
                    return user;
                }
            }
        }
        throw new BizErrorEx(Ret.UNKNOWN_STATUS, "尚未登录");
    }

    @RequestMapping(value = "/getDBTables")
    @ResponseBody
    public Object getDBTables(DbMessage dbMessage) throws BizErrorEx {
        return codeGenService.getDBTables(dbMessage);
    }

    @RequestMapping(value = "/getZipFile")
    @ResponseBody
    public Object getZipFile() throws BizErrorEx {
        return codeGenService.getZipFiles();
    }

    @RequestMapping(value = "/delFile")
    @ResponseBody
    public Object delFile(String cbx_ids) throws BizErrorEx {
        return codeGenService.delFile(cbx_ids);
    }

    @RequestMapping(value = "/signup")
    @ResponseBody
    public Object signup(OauthUser user) throws BizErrorEx {
        return codeGenService.signup(user);
    }

    @RequestMapping(value = "/downFile")
    @ResponseBody
    public Object downFile(HttpServletResponse response, String fileId) throws BizErrorEx {
        ZipMessage zipMessage = codeGenService.downFile(fileId);
        File file = new File(zipMessage.getFilePath());
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + zipMessage.getFileName());// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/downUploadFile")
    @ResponseBody
    public Object downUploadFile(HttpServletResponse response, String filePath) throws BizErrorEx {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" + filePath.substring(filePath.lastIndexOf(File.separator)));// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.OAUTH_LOGIN_ERROR, "下载失败");
    }

}

