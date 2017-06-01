package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/** 
* 支付成功引导页面
* @author lzzie
* created at 2017/3/18 16:44
*/
public class PaysuccessActivity extends BaseActivity {

    @BindView(R.id.showRecord)
    Button showRecord;
    @BindView(R.id.showMain)
    Button showMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paysuccess);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.showRecord,R.id.showMain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showRecord:
                showRecord();
                break;
            case R.id.showMain:
                showMain();
                break;
        }
    }

    public void showRecord(){
        Intent intent = new Intent(PaysuccessActivity.this, RecordActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMain(){
        Intent intent = new Intent(PaysuccessActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
