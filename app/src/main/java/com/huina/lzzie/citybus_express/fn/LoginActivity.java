package com.huina.lzzie.citybus_express.fn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.main.MainActivity;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.User;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.SystemStatusManager;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.zip.Deflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;


/**
*
* @author lzzie
* created at 2017/3/1 9:45
*/

public class LoginActivity extends Activity implements View.OnClickListener {
    public static Deflater instance;
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private AlertDialog myDialog = null;

    @BindView(R.id.userName)
    EditText muserName;
    @BindView(R.id.userPas)
    EditText muserPas;
    @BindView(R.id.login)
    Button mlogin;
    @BindView(R.id.showRegister)
    TextView showRegister;
    @BindView(R.id.forgerPas)
    TextView showForget;

    int userId;
    String mobile;
    String toKen;
    Context context;
    private static final String TAG ="LoginActivity" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);// 初始化 注解组件 监听器
        context = this;

        new SystemStatusManager(this).setTranslucentStatus(R.color.white);//设置状态栏透明，参数为你要设置的颜色

    }

    public void goback(View v){
        creatAlertDialog();
    }

    @OnClick({R.id.login,R.id.showRegister,R.id.forgerPas})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.showRegister:
                showRegister();
                break;
            case R.id.forgerPas:
                setShowForget();
                break;
        }
    }

    private void login() {
        final String username = muserName.getText().toString().trim();
        final String password = muserPas.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("mobile",username);
        params.put("password",password);
        httpHelper.post(Constants.API.LOGIN, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    User user = JsonHelper.parseObject(result1.getData(),User.class);
                    MyApplication.user = user;
                    toKen = user.getToken();
                    mobile = user.getMobile();
                    userId = user.getId();
                    PreferencesUtils.putString(context,"token",toKen);
                    PreferencesUtils.putString(context,"userMobile",mobile);
                    PreferencesUtils.putInt(context,"userId",userId);
                    ToastUtils.show(context,"登陆成功");
//                    Log.d(TAG,user.getToken());
                    showSuccess();
                }else {
                    ToastUtils.show(context,"请检查用户名或密码");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(context,"登录失败，请检查网络或联系客服");
            }
        });
    }

    public void showRegister(){
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }

    public void setShowForget(){
        Intent intent = new Intent(context,ForgetpasActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }

    public void showSuccess(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("page",1);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            exit();
            creatAlertDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    private void creatAlertDialog() {
        myDialog = new AlertDialog.Builder(LoginActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(860, 500);
        myDialog.getWindow().setContentView(R.layout.alert_login_layout);
        myDialog.getWindow()
                .findViewById(R.id.alert_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        finish();
                    }
                });
        myDialog.getWindow()
                .findViewById(R.id.alert_btn2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
    }

}
