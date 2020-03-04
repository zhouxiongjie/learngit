package com.shuangling.software.entity;

public class BaseModel {

    /**
     * msg : 请求成功
     * data : null
     * code : 100000
     */

    private String msg;
    private Object data;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
