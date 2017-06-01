package com.huina.lzzie.citybus_express;

import android.app.Application;

import com.huina.lzzie.citybus_express.model.Address;
import com.huina.lzzie.citybus_express.model.Order;
import com.huina.lzzie.citybus_express.model.Search;
import com.huina.lzzie.citybus_express.model.User;


public class MyApplication extends Application {

    public static User user;
    public static Address address;
    public static Order order;
    public static Search search;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
