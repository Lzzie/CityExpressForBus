package com.huina.lzzie.citybus_express.model;

/**
 * Created by Lzzie on 2017/3/16.
 */

public class UserOrderInfo {


    /**
     * create_time : 2017-03-15 17:04:29
     * count : 33
     * toPeople : 收件人  17662834697
     * weight : 22
     * type : 0
     * toAddress : 山东省 临沂市 兰山区市政府
     * order_code : 170315170429278
     * is_urgent : 1
     * uid : 5
     * order_status : 1
     * fromPeople : 寄件人 13176993877
     * name : 11
     * fromAddress : 山东省 临沂市 河东区中印软件园223室
     * id : 79
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
