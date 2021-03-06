package com.soho.codegen.domain;

import com.soho.mybatis.crud.domain.IDEntity;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table dx_sms
 *
 * @mbg.generated do_not_delete_during_merge
 */
@SuppressWarnings("serial")
public class DxSms implements IDEntity<Long> {
    private Long id;

    private Long userId;

    /**
     * Database Column Remarks:
     *   积分点
     */
    private String areacode;

    private String mobile;

    private String code;

    private String content;

    private Long sendtime;

    private Long validtime;

    private String reqstate;

    private String callstate;

    /**
     * Database Column Remarks:
     *   1.正常 2.已使用
     */
    private Integer usestate;

    /**
     * Database Column Remarks:
     *   1.登录验证短信 2.注册验证短信 3.通知短信
     */
    private Integer dxtype;

    private Long ctime;

    private Long utime;

    /**
     * Database Column Remarks:
     *   1.正常 2.删除
     */
    private Integer state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode == null ? null : areacode.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Long getSendtime() {
        return sendtime;
    }

    public void setSendtime(Long sendtime) {
        this.sendtime = sendtime;
    }

    public Long getValidtime() {
        return validtime;
    }

    public void setValidtime(Long validtime) {
        this.validtime = validtime;
    }

    public String getReqstate() {
        return reqstate;
    }

    public void setReqstate(String reqstate) {
        this.reqstate = reqstate == null ? null : reqstate.trim();
    }

    public String getCallstate() {
        return callstate;
    }

    public void setCallstate(String callstate) {
        this.callstate = callstate == null ? null : callstate.trim();
    }

    public Integer getUsestate() {
        return usestate;
    }

    public void setUsestate(Integer usestate) {
        this.usestate = usestate;
    }

    public Integer getDxtype() {
        return dxtype;
    }

    public void setDxtype(Integer dxtype) {
        this.dxtype = dxtype;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}