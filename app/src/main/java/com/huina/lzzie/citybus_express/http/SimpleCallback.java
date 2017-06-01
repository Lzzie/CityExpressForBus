package com.huina.lzzie.citybus_express.http;

import android.content.Context;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者: hanlin on 2017/2/18.
 * 邮箱: hanlin_bj@163.com
 * 版本: v1.0
 */

public abstract class SimpleCallback extends BaseCallBack {

    protected Context mContext;

    public SimpleCallback(Context context){

        mContext = context;

    }

    @Override
    public void onBeforeRequest(Request request) {

    }

    @Override
    public void onFailure(Request request, Exception e) {

    }

    @Override
    public void onResponse(Response response) {

    }

}
