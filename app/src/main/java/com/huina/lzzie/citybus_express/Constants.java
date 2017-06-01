package com.huina.lzzie.citybus_express;

/**
 * Created by Lzzie on 2017/3/3.
 */

public class Constants {

    public class API {

        public static final String BASE_URL = " http://192.168.0.189:8080/mobile/";

        public static final String GETVERSIONINFO = BASE_URL + "version/getVersionInfo";  //版本信息

        public static final String GETVERIFYCODE = BASE_URL + "user/getRegisteCode"; //注册用-验证码

        public static final String REGISTER = BASE_URL + "user/register";           //注册

        public static final String LOGIN = BASE_URL + "user/login";                 //登陆

        public static final String ADDCONSIGNEE = BASE_URL + "consignee/addConsignee";   //添加收发件人

        public static final String UPDATECONSIGNEE = BASE_URL + "consignee/updateConsignee"; //修改收发件人

        public static final String FINDALLCONSIGNEEBYUID = BASE_URL + "consignee/findAllConsigneeByuid";   //查询指定用户的收发地址

        public static final String ADDORDER = BASE_URL + "order/addOrder";    //添加订单

        public static final String GETORDERINFO = BASE_URL + "order/getOrderInfo-code"; //根据订单号查询订单信息

        public static final String GETORDERLISTBYUID = BASE_URL + "order/getOrderListByUid"; //根据uid，查询用户所有订单

        public static final String GETORDERLISTTODO = BASE_URL + "order/getOrderListTodo"; //根据uid，查询用户待办业务

        public static final String CANCLEORDER = BASE_URL + "order/cancleOrder"; //根据订单id，取消订单 订单并未删除，仅状态改为5

        public static final String DELORDER = BASE_URL + "order/delOrder"; //根据订单id，删除订单

        public static final String UPDATEORDERSTATUS = BASE_URL + "order/updateOrderStatus"; //根据订单id,修改订单状态

        public static final String GETRESETCODE = BASE_URL +"user/getVerifyCode";   //获取非注册验证码

        public static final String VERIFYCODE = BASE_URL +"user/verifyCode";   //手机号与验证码的有效验证

        public static final String RESETPASSWORD = BASE_URL +"user/resetPassword"; //重置密码

    }

}
