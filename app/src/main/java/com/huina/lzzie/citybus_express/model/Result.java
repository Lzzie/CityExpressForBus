package com.huina.lzzie.citybus_express.model;

import java.io.Serializable;

/**
 * Created by Lzzie on 2017/3/2.
 */

public class Result implements Serializable {

    private Integer code;
    private String msg;
    private String data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
