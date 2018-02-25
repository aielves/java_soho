package com.soho.codegen.service;

import com.soho.codegen.domain.DbMessage;
import com.soho.codegen.domain.OauthUser;
import com.soho.codegen.domain.ZipMessage;
import com.soho.ex.BizErrorEx;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by shadow on 2017/5/24.
 */
public interface CodeGenService {

    public Map<String, Object> generate(DbMessage dbMessage) throws BizErrorEx;

    public Map<String, List<String>> getDBTables(DbMessage dbMessage) throws BizErrorEx;

    public Map<String, Object> getZipFiles() throws BizErrorEx;

    public Map<String, Object> delFile(String cbx_ids) throws BizErrorEx;

    public Map<String, Object> signup(OauthUser user) throws BizErrorEx;

    public ZipMessage downFile(String fileId) throws BizErrorEx;

    public Map<String, Object> uploadFile(Integer uploadtype, MultipartFile file) throws BizErrorEx;

}
