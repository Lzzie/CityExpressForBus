package com.huina.lzzie.citybus_express.model;

/**
 * Created by Lzzie on 2017/3/17.
 */

public class Schedule {

    /**
     * create_time : 2017-03-10 14:00:47
     * count : 1
     * toPeople : 路人 1 13856565656
     * weight : 3
     * type : 1
     * toAddress : 松花江上 111
     * order_code : 170310140047013
     * is_urgent : 0
     * uid : 1
     * order_status : 1  订单状态 1:未支付,2取件寄件中,3配送中,4已完成,5已取消
     * fromPeople : 路人甲 13856565656
     * name : 啊飒飒
     * fromAddress : 松花江上
     * id : 2
     */

    private String createTime;
    private int count;
    private String toPeople;
    private int weight;
    private int type;
    private String toAddress;
    private String orderCode;
    private int isUrgent;
    private int uid;
    private int orderStatus;
    private String fromPeople;
    private String name;
    private String fromAddress;
    private int id;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getToPeople() {
        return toPeople;
    }

    public void setToPeople(String toPeople) {
        this.toPeople = toPeople;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(int isUrgent) {
        this.isUrgent = isUrgent;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFromPeople() {
        return fromPeople;
    }

    public void setFromPeople(String fromPeople) {
        this.fromPeople = fromPeople;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
