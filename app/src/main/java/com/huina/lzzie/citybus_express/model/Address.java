package com.huina.lzzie.citybus_express.model;

/**
 * Created by Lzzie on 2017/3/10.
 */

public class Address {


    /**
     * uid : 37
     * address : xiangxidizhi
     * province : sheng
     * city : shi
     * name : li
     * mobile : 15615615666
     * id : 145
     * type : 1
     */

    private int uid;
    private String address;
    private String province;
    private String city;
    private String name;
    private String mobile;
    private int id;
    private int type;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
