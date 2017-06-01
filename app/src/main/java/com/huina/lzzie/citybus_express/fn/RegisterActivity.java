package com.huina.lzzie.citybus_express.fn;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import butterknife.OnClick;
import okhttp3.Response;

/** 
* 注册
* @author lzzie
* created at 2017/3/18 16:39
*/
public class RegisterActivity extends BaseActivity {

    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    @BindView(R.id.registerName)
    EditText registerName;
    @BindView(R.id.registerPas)
    EditText registerPas;
    @BindView(R.id.registerPas2)
    EditText registerPas2;
    @BindView(R.id.registerCode)
    EditText registercode;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.registeryzm)
    Button yzm;

    private static final String TAG = "RegisterActivity";
    private Context context;
    String toKen;
    int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        ButterKnife.bind(this);

    }

    @OnClick({R.id.register,R.id.registeryzm})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.register:
                if (registerPas.getText().toString().equals(registerPas2.getText().toString())){
                    Register();
                }else {
                    ToastUtils.show(RegisterActivity.this,"您两次输入的密码不同，请重新输入");
                }
                break;
            case R.id.registeryzm:
                Yzm();
                break;
        }
    }

    public void Yzm() {
        final String username = registerName.getText().toString().trim();
        if (TextUtils.isEmpty(username) ) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("mobile",username);
        httpHelper.post(Constants.API.GETVERIFYCODE, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                if (result1.getCode()==0){
                    registercode.setText(result1.getData().toString());
                }else {
                    ToastUtils.show(RegisterActivity.this,result1.getMsg());
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
            }
        });
    }

    public void Register(){
        final String username = registerName.getText().toString().trim();
        final String password = registerPas.getText().toString().trim();
        final String code = registercode.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
//Start
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("mobile",username);
        params.put("password",password);
        params.put("code",code);
        httpHelper.post(Constants.API.REGISTER, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result r = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,r.getData().toString());
                Log.d(TAG,r.getCode()+"");
                Log.d(TAG,r.getMsg());
                if (r.getCode() == 0) {
                    ToastUtils.show(context, "注册成功,请登录");
//                    User user = JsonHelper.parseObject(r.getData(),User.class);
//                    MyApplication.user = user;
//                    toKen= user.getToken();
//                    uid = user.getId();
//                    PreferencesUtils.putString(context,"token",toKen);
//                    PreferencesUtils.putInt(context,"userId",uid);
                    Showsuccess();
                } else {
                    ToastUtils.show(RegisterActivity.this,r.getMsg());
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(RegisterActivity.this,"请检查网络与设置");
            }
        });
//End
    }
    public void Showsuccess(){
        finish();
    }

}
