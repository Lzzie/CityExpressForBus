package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.os.Bundle;

import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.model.User;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;
/** 
* 意见反馈
* @author lzzie
* created at 2017/3/18 16:43
*/
public class FeedbackActivity extends BaseActivity {
    User user = MyApplication.user;
    String toKen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toKen = PreferencesUtils.getString(this,"token");
        if (toKen==""){
            ToastUtils.show(FeedbackActivity.this,"请您先登录");
            Intent intent = new Intent(FeedbackActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_feedback);
    }
}
