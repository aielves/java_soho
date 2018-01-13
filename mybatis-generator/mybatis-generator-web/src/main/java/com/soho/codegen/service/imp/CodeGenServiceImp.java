package com.soho.codegen.service.imp;

import com.soho.codegen.dao.DbMessageDAO;
import com.soho.codegen.dao.OauthUserDAO;
import com.soho.codegen.dao.ZipMessageDAO;
import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.domain.DeftConfig;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.CodeGenService;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.exception.MybatisDAOEx;
import com.soho.mybatis.sqlcode.aconst.SortBy;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import com.soho.utils.ZipUtils;
import com.soho.web.domain.Ret;
import com.soho.zookeeper.security.imp.AESDcipher;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.mybatis.generator.api.ShellRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        String newPath = user.getUsername() + File.separator + zipName;
        session.setAttribute("targetProject", deftConfig.getFileDirPath() + File.separator + newPath);
        String[] arg = {"-configfile", deftConfig.getConfigXmlPath()};
        ShellRunner.main(arg);
        String path = deftConfig.getFileDirPath() + File.separator + newPath;
        ZipUtils.zip(path);
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

    @Transactional
    @Override
    public Map<String, Object> signup(OauthUser user) throws BizErrorEx {
        try {
            String username = user.getUsername();
            if (StringUtils.isEmpty(username) || !username.matches("[a-zA-Z]{1}[a-zA-Z0-9_]{6,15}")) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "由字母数字下划线组成且开头必须是字母，6-15位");
            } else {
                int count = 0;
                count = oauthUserDAO.countByCnd(new SQLCnd().eq("username", username));
                if (count > 0) {
                    throw new BizErrorEx(Ret.UNKNOWN_STATUS, "帐号已存在");
                }
            }
            String passwd = user.getPassword();
            if (StringUtils.isEmpty(passwd) || passwd.length() < 6 || passwd.length() > 15) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "请输入6-15位密码");
            }
            String nickname = user.getNickname();
            if (StringUtils.isEmpty(nickname)) {
                throw new BizErrorEx(Ret.UNKNOWN_STATUS, "昵称不能为空");
            }
            user.setPassword(AESDcipher.encrypt(passwd));
            user.setCtime(System.currentTimeMillis());
            user.setUid(UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
            oauthUserDAO.insert(user);
            Map<String, Object> map = new HashMap<>();
            map.put("result", "注册成功");
            return map;
        } catch (MybatisDAOEx ex) {
            throw new BizErrorEx(ex.getErrorCode(), ex.getMessage());
        }
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

}
