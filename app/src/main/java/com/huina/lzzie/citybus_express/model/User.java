package com.huina.lzzie.citybus_express.model;

/**
 * Created by Lzzie on 2017/3/2.
 */

public class User {

    /**
     * realName :
     * password : 111
     * nickName :
     * sex : 1
     * mobile : 111
     * id : 6
     * token : SmT1DmsntEnFroWU
     */

    private String realName;
    private String password;
    private String nickName;
    private int sex;
    private String mobile;
    private int id;
    private String token;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
