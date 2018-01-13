package com.soho.codegen.controller;

import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.CodeGenService;
import com.soho.ex.BizErrorEx;
import com.soho.web.domain.Ret;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/codegen")
public class CodeGenController {

    @Autowired
    private CodeGenService codeGenService;

    @RequestMapping(value = "/generate")
    @ResponseBody
    public Object generate(DbMessage dbMessage) throws BizErrorEx {
        return codeGenService.generate(dbMessage);
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

}

