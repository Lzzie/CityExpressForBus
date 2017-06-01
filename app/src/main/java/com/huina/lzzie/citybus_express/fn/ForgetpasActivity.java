package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
* 忘记密码
* @author lzzie
* created at 2017/3/18 16:43
*/
public class ForgetpasActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    @BindView(R.id.forgetPhone)
    EditText forgetPhone;
    @BindView(R.id.forgetYzm)
    EditText forgetYzm;
    @BindView(R.id.getResetYzm)
    Button getResetYzm;
    @BindView(R.id.confirmButton)
    Button confirmButton;

    boolean sign=false;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpas);
        ButterKnife.bind(this);

        getResetYzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forgetPhone.getText().toString()!=null){
                    getResetYzm();
                    sign = true;
                }else {
                    ToastUtils.show(ForgetpasActivity.this,"请您输入手机号");
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sign==true){
                    verifyCode();
                }else {
                    ToastUtils.show(ForgetpasActivity.this,"请您获取验证码");
                }
            }
        });

    }


    public void getResetYzm(){
        phone = forgetPhone.getText().toString();
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("mobile",phone);
        httpHelper.post(Constants.API.GETRESETCODE, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    forgetYzm.setText(result1.getData());
                }else {
                    ToastUtils.show(ForgetpasActivity.this,"请检查手机号");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(ForgetpasActivity.this,"请检查网络与设置");
            }
        });
    }

    public void verifyCode(){
        String phone = forgetPhone.getText().toString();
        String yzm = forgetYzm.getText().toString();
        ArrayMap<String,String> parms = new ArrayMap<>();
        parms.put("mobile",phone);
        parms.put("code",yzm);
        httpHelper.post(Constants.API.VERIFYCODE, parms, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    resetPassword();
                }else {
                    ToastUtils.show(ForgetpasActivity.this,"请重新获取验证码");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(ForgetpasActivity.this,"请检查网络与设置");
            }
        });

    }

    public void resetPassword(){
        Intent intent = new Intent(ForgetpasActivity.this, ResetpasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("phone",phone);
        startActivity(intent);
        finish();
    }


}
