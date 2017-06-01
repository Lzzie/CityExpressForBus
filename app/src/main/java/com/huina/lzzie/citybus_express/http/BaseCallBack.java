package com.huina.lzzie.citybus_express.http;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者: hanlin on 2017/2/18.
 * 邮箱: hanlin_bj@163.com
 * 版本: v1.0
 *
 * 回调接口
 */

public abstract class BaseCallBack {



    public  abstract void onBeforeRequest(Request request);


    public abstract  void onFailure(Request request, Exception e) ;


    /**
     *请求成功时调用此方法
     * @param response
     */
    public abstract  void onResponse(Response response);

    /**
     *
     * 状态码大于200，小于300 时调用此方法
     * @param response
     * @param result
     * @throws IOException
     */
    public abstract void onSuccess(Response response,String result) ;

    /**
     * 状态码400，404，403，500等时调用此方法
     * @param response
     * @param code
     * @param e
     */
    public abstract void onError(Response response, int code,Exception e) ;

}
