package com.huina.lzzie.citybus_express.fn;

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
* 重置密码
* @author lzzie
* created at 2017/3/20 9:14
*/
public class ResetpasswordActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    String phone;

    @BindView(R.id.resetPas)
    EditText restePas;
    @BindView(R.id.resetPas2)
    EditText resetPas2;
    @BindView(R.id.resetConfirmButton)
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        ButterKnife.bind(this);
        phone = getIntent().getStringExtra("phone");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restePas.getText().toString().equals(resetPas2.getText().toString())){
                    resetPassword();
                }else {
                    ToastUtils.show(ResetpasswordActivity.this,"您两次输入的密码不同，请重新输入");
                }
            }
        });
    }

    public void resetPassword(){
        String password = restePas.getText().toString();
        ArrayMap<String,String> parms = new ArrayMap<>();
        parms.put("mobile",phone);
        parms.put("password",password);
        httpHelper.post(Constants.API.RESETPASSWORD, parms, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    ToastUtils.show(ResetpasswordActivity.this,"重置成功,请您重新登陆");
                    resetSuccess();
                }else {
                    ToastUtils.show(ResetpasswordActivity.this,"已失效，请重新获取验证码");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(ResetpasswordActivity.this,"请检查网络与设置");
            }
        });
    }

    public void resetSuccess(){
        finish();
    }

}
