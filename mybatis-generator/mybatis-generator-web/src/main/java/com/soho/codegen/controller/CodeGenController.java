package com.soho.codegen.controller;

import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.CodeGenService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/codegen")
public class CodeGenController {

    @Autowired
    private CodeGenService codeGenService;

    @ResponseBody
    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public Object generate(DbMessage dbMessage) throws BizErrorEx {
        validUserSession();
        return codeGenService.generate(dbMessage);
    }

    /*@ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Object uploadFile(Integer uploadtype, @RequestParam("file") MultipartFile file) throws BizErrorEx {
        validUserSession();
        return codeGenService.uploadFile(uploadtype, file);
    }*/

    @ResponseBody
    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    public Object generate() throws BizErrorEx {
        validUserSession();
        OauthUser user = (OauthUser) SecurityUtils.getSubject().getSession().getAttribute("session_user");
        user.setPassword(null);
        return user;
    }

    @ResponseBody
    @RequestMapping(value = "/getDBTables", method = RequestMethod.POST)
    public Object getDBTables(DbMessage dbMessage) throws BizErrorEx {
        validUserSession();
        return codeGenService.getDBTables(dbMessage);
    }

    @ResponseBody
    @RequestMapping(value = "/getZipFile", method = RequestMethod.POST)
    public Object getZipFile() throws BizErrorEx {
        validUserSession();
        return codeGenService.getZipFiles();
    }

    @ResponseBody
    @RequestMapping(value = "/delFile", method = RequestMethod.POST)
    public Object delFile(String cbx_ids) throws BizErrorEx {
        validUserSession();
        return codeGenService.delFile(cbx_ids);
    }

    @ResponseBody
    @RequestMapping(value = "/downFile", method = RequestMethod.POST)
    public Object downFile(HttpServletResponse response, String fileId) throws BizErrorEx {
        validUserSession();
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
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/downUploadFile", method = RequestMethod.POST)
    public Object downUploadFile(HttpServletResponse response, String filePath) throws BizErrorEx {
        validUserSession();
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
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "下载失败");
    }

    private void validUserSession() throws BizErrorEx {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "尚未登录");
        }
        Object object = SecurityUtils.getSubject().getSession().getAttribute("session_user");
        if (object == null) {
            throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "用户数据异常,请重新登录");
        }
    }

}

