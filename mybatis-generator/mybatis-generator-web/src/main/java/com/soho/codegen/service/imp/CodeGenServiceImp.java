package com.soho.codegen.service.imp;

import com.soho.codegen.chuanglan.SendSmsUtils;
import com.soho.codegen.dao.DbMessageDAO;
import com.soho.codegen.dao.OauthUserDAO;
import com.soho.codegen.dao.ZipMessageDAO;
import com.soho.codegen.domain.*;
import com.soho.codegen.service.CodeGenService;
import com.soho.codegen.shiro.aconst.BizErrorCode;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.exception.MybatisDAOEx;
import com.soho.mybatis.sqlcode.aconst.SortBy;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.shiro.utils.SessionUtils;
import com.soho.utils.JAQUtils;
import com.soho.utils.ZipUtils;
import com.soho.web.domain.Ret;
import com.soho.zookeeper.security.imp.AESDcipher;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.mybatis.generator.api.ShellRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shadow on 2017/5/24.
 */
@Service
public class CodeGenServiceImp implements CodeGenService {

    @Autowired
    private DeftConfig deftConfig;

    @Autowired
    private ZipMessageDAO zipMessageDAO;
    @Autowired
    private DbMessageDAO dbMessageDAO;
    @Autowired
    private OauthUserDAO oauthUserDAO;

    @Transactional
    @Override
    public Map<String, Object> generate(DbMessage dbMessage) throws BizErrorEx {
        String moduleName = dbMessage.getModuleName();
        String packageName = dbMessage.getPackageName();
        Integer dbType = dbMessage.getDbType();
        String jdbcUrl = dbMessage.getJdbcUrl();
        String username = dbMessage.getUsername();
        String password = dbMessage.getPassword();
        String tables = dbMessage.getTables();
        if (moduleName == null) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "至少选择一个模块");
        }
        if (packageName == null) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入类包路径");
        }
        if (dbType == null) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请选择数据库类型");
        }
        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库地址");
        }
        if (StringUtils.isEmpty(username)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库帐号");
        }
        if (StringUtils.isEmpty(password)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库密码");
        }
        if (StringUtils.isEmpty(tables)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "至少选择一个数据表");
        }
        // 驱动程序名
        String jdbcDriver = null;
        if (dbType.intValue() == 1) {
            jdbcDriver = "com.mysql.jdbc.Driver";
        }
        dbMessage.setJdbcDriver(jdbcDriver);
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("dbmessage", dbMessage);
        OauthUser user = session.getAttribute("session_user") == null ? null : (OauthUser) session.getAttribute("session_user");
        if (user == null) {
            user = new OauthUser();
            user.setId(0l);
        }
        String zipName = System.currentTimeMillis() + "";
        String newPath = user.getId() + File.separator + zipName;
        session.setAttribute("targetProject", deftConfig.getFileDirPath() + File.separator + newPath);
        String[] arg = {"-configfile", deftConfig.getConfigXmlPath()};
        ShellRunner.main(arg);
        String path = deftConfig.getFileDirPath() + File.separator + newPath;
        ZipUtils.zip(path, path + ".zip");
        String zipFilePath = path + ".zip";
        File zipFile = new File(zipFilePath);
        if (zipFile.exists()) {
            ZipMessage zipMessage = new ZipMessage();
            zipMessage.setFileName(zipName + ".zip");
            zipMessage.setCtime(System.currentTimeMillis());
            zipMessage.setFilePath(zipFilePath);
            zipMessage.setUserId(user.getId());
            zipMessage.setFileSize(zipFile.length());
            zipMessage.setDburl(dbMessage.getJdbcUrl());
            zipMessage.setTables(dbMessage.getTables());
            try {
                zipMessageDAO.insert(zipMessage);
                dbMessage.setUserId(user.getId());
                dbMessage.setCtime(System.currentTimeMillis());
                dbMessageDAO.insert(dbMessage);
                Map<String, Object> map = new HashMap<>();
                map.put("result", "执行成功");
                return map;
            } catch (MybatisDAOEx ex) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "保存生成文件失败,请重新尝试");
            }
        } else {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "生成文件失败,请重新尝试");
        }
    }

    @Override
    public Map<String, List<String>> getDBTables(DbMessage dbMessage) throws BizErrorEx {
        Integer dbType = dbMessage.getDbType();
        String jdbcUrl = dbMessage.getJdbcUrl();
        String username = dbMessage.getUsername();
        String password = dbMessage.getPassword();
        if (dbType == null) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请选择数据库类型");
        }
        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库地址");
        }
        if (StringUtils.isEmpty(username)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库帐号");
        }
        if (StringUtils.isEmpty(password)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入数据库密码");
        }
        // 驱动程序名
        String jdbcDriver = null;
        if (dbType.intValue() == 1) {
            jdbcDriver = "com.mysql.jdbc.Driver";
        }
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "没有找到数据库驱动");
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl.trim(), username.trim(), password);
        } catch (SQLException e) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "连接数据库失败");
        }
        try {
            List<String> tables = new ArrayList<>();
            ResultSet tablesSet = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            while (tablesSet.next()) {
                tables.add(tablesSet.getString("TABLE_NAME"));
            }
            Map<String, List<String>> map = new HashMap<>();
            map.put("tables", tables);
            return map;
        } catch (SQLException e) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "获取表结构失败");
        }
    }

    @Override
    public Map<String, Object> getZipFiles() throws BizErrorEx {
        Session session = SecurityUtils.getSubject().getSession();
        OauthUser user = session.getAttribute("session_user") == null ? null : (OauthUser) session.getAttribute("session_user");
        if (user == null) {
            user = new OauthUser();
            user.setId(0l);
        }
        try {
            List<ZipMessage> zipMessages = zipMessageDAO.findByCnd(new SQLCnd().eq("userId", user.getId()).orderby("ctime", SortBy.D).limit(1, 50));
            if (zipMessages != null && !zipMessages.isEmpty()) {
                List<Map<String, Object>> list = new ArrayList<>(zipMessages.size());
                for (ZipMessage message : zipMessages) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", message.getId());
                    map.put("dburl", message.getDburl());
                    map.put("tables", message.getTables());
                    map.put("fileName", message.getFileName());
                    map.put("fileSize", message.getFileSize() + "(Byte)");
                    map.put("ctime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getCtime())));
                    list.add(map);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("list", list);
                return map;
            }
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(ex.getErrorCode(), ex.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> delFile(String cbx_ids) throws BizErrorEx {
        if (cbx_ids == null || "".equals(cbx_ids)) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请选择删除的记录");
        }
        String[] ids = cbx_ids.split(",");
        if (ids == null || ids.length <= 0) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请选择删除的记录");
        }
        List<Long> longIds = new ArrayList<>(ids.length);
        for (String s : ids) {
            try {
                longIds.add(Long.parseLong(s));
            } catch (Exception e) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "非法ID参数");
            }
        }
        Session session = SecurityUtils.getSubject().getSession();
        OauthUser user = session.getAttribute("session_user") == null ? null : (OauthUser) session.getAttribute("session_user");
        if (user == null) {
            user = new OauthUser();
            user.setId(0l);
        }
        try {
            List<ZipMessage> list = zipMessageDAO.findByCnd(new SQLCnd().eq("userId", user.getId()).in("id", longIds.toArray()));
            if (list != null && !list.isEmpty()) {
                for (ZipMessage zipMessage : list) {
                    File file = new File(zipMessage.getFilePath());
                    if (file.exists()) {
                        file.delete();
                    }
                    zipMessageDAO.delete(zipMessage.getId());
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("result", "删除成功");
            return map;
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(ex.getErrorCode(), ex.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> signup(OauthUser user, String smscode) throws BizErrorEx {
        try {
            if (!JAQUtils.toStateByValid()) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请先进行安全认证");
            }
            String username = user.getUsername();
            String password = user.getPassword();
            String nickname = user.getNickname();
            String company = user.getCompany();
            if (!SendSmsUtils.isMobile(username)) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入合法的手机号码");
            } else {
                int count = oauthUserDAO.countByCnd(new SQLCnd().eq("username", username));
                if (count > 0) {
                    throw new BizErrorEx(Ret.UNKNOWN_STATUS, "您输入的手机号码已注册");
                }
            }
            if (StringUtils.isEmpty(smscode)) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入您的短信验证码");
            }
            if (StringUtils.isEmpty(password) || password.length() < 6 || password.length() > 15) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入您的登录密码(6-15位)");
            }
            if (StringUtils.isEmpty(nickname)) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入您的昵称");
            }
            if (StringUtils.isEmpty(company)) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入您的所在的公司名称");
            }
            DxSms dx = SendSmsUtils.validSmsCode("86", username, smscode);
            OauthUser save = new OauthUser();
            save.setUsername(username);
            save.setPassword(AESDcipher.encrypt(password));
            save.setNickname(nickname);
            save.setCompany(company);
            save.setCtime(System.currentTimeMillis());
            save.setUid(UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
            oauthUserDAO.insert(save);
            SendSmsUtils.delSmsCode(dx);
            JAQUtils.toStateByRemove();
            Map<String, Object> map = new HashMap<>();
            map.put("result", "注册成功");
            return map;
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(ex.getErrorCode(), ex.getMessage());
        }
    }

    @Override
    public Map<String, Object> sendsms(String mobile) throws BizErrorEx {
        if (!JAQUtils.toStateByValid()) {
            throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请先进行安全认证");
        }
        SendSmsUtils.toSend(1, "86", mobile);
        Session session = SessionUtils.getSession();
        Object object = session.getAttribute("sms_count");
        int sms_count = 1;
        if (object != null) {
            sms_count = Integer.parseInt(object.toString()) + 1;
        }
        if (sms_count > 3) {
            SessionUtils.getSubject().logout();
            throw new BizErrorEx(BizErrorCode.BIZ_SMS_ERROR, "由于您请求过于频繁,请重新进行认证");
        }
        session.setAttribute("sms_count", sms_count);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "验证码已下发");
        return map;
    }

    @Override
    public ZipMessage downFile(String fileId) throws BizErrorEx {
        Session session = SecurityUtils.getSubject().getSession();
        OauthUser user = session.getAttribute("session_user") == null ? null : (OauthUser) session.getAttribute("session_user");
        if (user == null) {
            user = new OauthUser();
            user.setId(0l);
        }
        try {
            ZipMessage zipMessage = zipMessageDAO.findOneByCnd(new SQLCnd().eq("userId", user.getId()).eq("id", fileId));
            if (zipMessage == null) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "没有找到下载文件");
            }
            return zipMessage;
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(ex.getErrorCode(), ex.getMessage());
        }
    }

    @Override
    public Map<String, Object> uploadFile(Integer uploadtype, MultipartFile file) throws BizErrorEx {
        try {
            if (uploadtype == null) {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "请选择文件类型");
            }
            if (file == null || StringUtils.isEmpty(file.getOriginalFilename())) {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "上传文件不能为空");
            }
            long filesize = file.getSize();
            if (filesize > (5000 * 1024)) {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "上传文件不能超过5000K");
            }
            Session session = SecurityUtils.getSubject().getSession();
            OauthUser user = session.getAttribute("session_user") == null ? null : (OauthUser) session.getAttribute("session_user");
            if (user == null) {
                user = new OauthUser();
                user.setId(0l);
            }
            String filetime = user.getId() + "" + System.currentTimeMillis();
            String orgname = file.getOriginalFilename();
            String fileName = filetime + orgname.substring(orgname.lastIndexOf("."));
            String fileDir = deftConfig.getFileDirPath() + File.separator + user.getId();
            String newPath = fileDir + File.separator + fileName;
            if (uploadtype == 1) {
                if (orgname.endsWith(".zip")) {
                    File file1 = new File(fileDir);
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    File savefile = new File(newPath);
                    file.transferTo(savefile);  // 保存上传ZIP文件
                    String unzipDir = fileDir + File.separator + filetime; // 设置解压目录
                    ZipUtils.unzip(newPath, unzipDir); // 解压原始ZIP文件
                    savefile.delete(); // 删除原始ZIP文件
                    // 遍历原始文件并执行压缩方法
                    File unzipDirFile = new File(unzipDir);
                    if (unzipDirFile.isDirectory()) {
                        File[] files = unzipDirFile.listFiles();
                        if (files != null && files.length > 0) {
                            for (File it : files) {
                                String filename = it.getName();
                                if (it.isFile() && (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
                                    Thumbnails.of(it.getPath()).scale(1.0f).toFile(it.getPath());
                                } else {
                                    it.delete();
                                }
                            }
                        }
                    }
                    newPath = unzipDir + ".zip";
                    ZipUtils.zip(unzipDir, newPath);
                } else {
                    throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "请上传ZIP格式的文件");
                }
            } else if (uploadtype == 2) {
                if (orgname.endsWith(".jpg") || orgname.endsWith(".jpeg")) {
                    File file1 = new File(fileDir);
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    file.transferTo(new File(newPath));
                    Thumbnails.of(newPath).scale(1.0f).toFile(newPath);
                } else {
                    throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "请上传JPG或JPEG格式的文件");
                }
            } else {
                throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "文件类型异常");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("path", newPath);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BizErrorEx) {
                throw new BizErrorEx(((BizErrorEx) e).getErrorCode(), e.getMessage());
            }
        }
        throw new BizErrorEx(BizErrorCode.BIZ_ERROR, "处理失败,请重新尝试");
    }

}
