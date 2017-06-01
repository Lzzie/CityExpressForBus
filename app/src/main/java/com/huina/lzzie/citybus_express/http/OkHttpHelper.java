package com.huina.lzzie.citybus_express.http;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者: hanlin on 2017/2/22.
 * 邮箱: hanlin_bj@163.com
 * 版本: v1.0
 */

public class OkHttpHelper {

    private static final String TAG = "OkHttpHelper";

    private  static  OkHttpHelper mInstance;
    private OkHttpClient mHttpClient;

    private Handler mHandler;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    static {
        mInstance = new OkHttpHelper();
    }

    private OkHttpHelper(){
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15,TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS);
        mHttpClient = builder.build();

        mHandler = new Handler(Looper.getMainLooper());

    };

    public static  OkHttpHelper getInstance(){
        return  mInstance;
    }

    /**
     * 带参数GET方法
     * @param url
     * @param params
     * @param callBack
     */
    public void get(String url, ArrayMap<String,String> params,BaseCallBack callBack){
        Request request = buildGetRequest(url,params);
        request(request,callBack);
    }

    /**
     * 无参数GET方法
     * @param url
     * @param callBack
     */
    public void get(String url,BaseCallBack callBack){
        get(url,null,callBack);
    }

    /**
     * post方法
     * @param url
     * @param params
     * @param callBack
     */
    public void post(String url, ArrayMap<String, String> params, BaseCallBack callBack) {
        Request request = buildPostRequest(url,params);
        request(request,callBack);
    }


    /**
     * 异步上传文件
     * @param url
     * @param fileName
     * @param callBack
     */
    public void postAsynFile(String url, String fileName,BaseCallBack callBack) {
        File file = new File(fileName);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        request(request,callBack);
    }

    /**
     * 异步下载文件
     * @param url
     * @param callBack
     */
    public void downAsynFile(String url, BaseCallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        request(request,callBack);
    }

    /*------------------------------   私有逻辑方法   -----------------------------------*/
    private Request buildPostRequest(String url, ArrayMap<String, String> params) {
        return buildRequest(url,HttpMethodType.POST,params);
    }

    private Request buildGetRequest(String url, ArrayMap<String, String> params) {
        return buildRequest(url,HttpMethodType.GET,params);
    }

    private Request buildRequest(String url, HttpMethodType methodType, ArrayMap<String, String> params) {
        Request.Builder builder = new Request.Builder().url(url);

        if (methodType == HttpMethodType.GET) {

            url = buildUrlParams(url,params);
            builder.url(url);

            builder.get();

        } else if (methodType == HttpMethodType.POST) {
            RequestBody body = buildFormData(params);
            builder.post(body);
        }
        return builder.build();

    }

    private RequestBody buildJSONData(ArrayMap<String, String> params) {

        MediaType type = MediaType.parse("application/json");
        String content = getRequestData(params,"UTF-8");
        RequestBody body = RequestBody.create(type,content);
        return  body;

    }

    /**
     * 构建post的form表单参数
     * @param params
     * @return
     */
    private RequestBody buildFormData(ArrayMap<String, String> params) {

        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (ArrayMap.Entry<String, String> param : params.entrySet()) {
                builder.add(param.getKey(),param.getValue()==null?"":param.getValue());
            }
        }

        return builder.build();

    }

    private void request(final Request request,final BaseCallBack callBack) {

        callBack.onBeforeRequest(request);

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callBack, request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                callbackResponse(callBack,response);

                if (response.isSuccessful()) {
                    String resultStr = response.body().string();
                    Log.d(TAG, "返回结果 =" + resultStr);
                    callbackSuccess(callBack,response,resultStr);
                }else {
                    callbackError(callBack,response,null);
                }
            }
        });

    }

    private void callbackError(final BaseCallBack callback, final Response response, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response,response.code(),e);
            }
        });

    }

    private void callbackSuccess(final BaseCallBack callback, final Response response, final String resultStr) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, resultStr);
            }
        });

    }

    private void callbackResponse(final BaseCallBack callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });

    }

    private void callbackFailure(final BaseCallBack callback, final Request request, final IOException e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request,e);
            }
        });

    }


    /**
     * json封装post参数
     * @param params
     * @param encode
     * @return
     */
    private String getRequestData(ArrayMap<String, String> params, String encode) {

        JSONObject jsonObject =new JSONObject();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), URLEncoder.encode(entry.getValue(), encode));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return  jsonObject.toString();

    }

    private String buildUrlParams(String url, ArrayMap<String, String> params) {

        if(params == null)
            params = new ArrayMap<>();
        StringBuffer sb = new StringBuffer();
        for (ArrayMap.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + (entry.getValue()==null?"":entry.getValue().toString()));
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }

        if(url.indexOf("?")>0){
            url = url +"&"+s;
        }else{
            url = url +"?"+s;
        }

        return url;

    }

    enum  HttpMethodType{

        GET,
        POST,

    }


}
