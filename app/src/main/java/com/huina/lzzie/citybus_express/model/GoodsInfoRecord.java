package com.huina.lzzie.citybus_express.model;

/**
 * Created by Lzzie on 2017/3/9.
 */

public class GoodsInfoRecord {

    private String record_id;
    private String form_address;
    private String to_address;
    private String creat_time;
    private String order_status;

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getForm_address() {
        return form_address;
    }

    public void setForm_address(String form_address) {
        this.form_address = form_address;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
