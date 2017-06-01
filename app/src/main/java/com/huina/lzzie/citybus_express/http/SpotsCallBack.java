package com.huina.lzzie.citybus_express.http;

import android.content.Context;

import dmax.dialog.SpotsDialog;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者: hanlin on 2017/2/18.
 * 邮箱: hanlin_bj@163.com
 * 版本: v1.0
 */

public abstract class SpotsCallBack extends SimpleCallback {

    private SpotsDialog mDialog;

    public SpotsCallBack(Context context){
        super(context);

        initSpotsDialog();
    }


    private  void initSpotsDialog(){

        mDialog = new SpotsDialog(mContext,"拼命加载中...");
    }


    public  void showDialog(){
        mDialog.show();
    }

    public  void dismissDialog(){
        mDialog.dismiss();
    }


    public void setLoadMessage(int resId){
        mDialog.setMessage(mContext.getString(resId));
    }


    @Override
    public void onBeforeRequest(Request request) {

        showDialog();
    }

    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }

}
