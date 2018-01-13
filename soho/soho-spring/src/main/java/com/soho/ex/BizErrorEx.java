package com.soho.ex;

/**
 * @author shadow
 */
@SuppressWarnings("serial")
public class BizErrorEx extends Exception {

    private String errorCode;
    private String msg;

    public BizErrorEx() {
        super();
    }

    public BizErrorEx(Throwable e) {
        super(e);
    }

    public BizErrorEx(String msg) {
        super(msg);
        setMsg(msg);
    }

    public BizErrorEx(String msg, Throwable e) {
        super(msg, e);
        setMsg(msg);
    }

    public BizErrorEx(String errorCode, String msg) {
        super(msg);
        setMsg(msg);
        setErrorCode(errorCode);
    }

    public BizErrorEx(String msg, Throwable e, String errorCode) {
        super(msg, e);
        setErrorCode(errorCode);
        setMsg(msg);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
