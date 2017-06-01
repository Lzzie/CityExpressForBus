package com.huina.lzzie.citybus_express.fn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.fragment.QujianFragment;
import com.huina.lzzie.citybus_express.fragment.ZijiFragment;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 寄件记录
 * @author lzzie
* created at 2017/3/16 10:54
*/

public class RecordActivity extends BaseActivity implements View.OnClickListener {
    String toKen;
    private QujianFragment qujianFragment;
    private ZijiFragment zijiFragment;

    private Map<Integer, Fragment> map = new ArrayMap<>();
    private Fragment conventFragment;

    @BindView(R.id.qujian_page)
    RadioButton qujianPage;
    @BindView(R.id.ziji_page)
    RadioButton zijiPage;
    @BindView(R.id.qujian_iv)
    ImageView quanjianIv;
    @BindView(R.id.ziji_iv)
    ImageView zijiIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        toKen = PreferencesUtils.getString(this,"token");
        if (toKen!=""){
            initListener();
            if (savedInstanceState == null) {
                setDefaultFragment();
            }
        }else {
            ToastUtils.show(RecordActivity.this,"请您先登录");
            Intent intent = new Intent(RecordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction(); //transaction
        qujianFragment = new QujianFragment();
        map.put(0, qujianFragment);
        transaction.replace(R.id.record_content, qujianFragment);
        transaction.commit();
    }
    private void initListener() {
        qujianPage.setOnClickListener(this);
        zijiPage.setOnClickListener(this);
    }

    @OnClick({R.id.qujian_page,R.id.ziji_page})
    public void onClick(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (view.getId()) {
            case R.id.qujian_page:
                initFragments(0);
                changeIv2();
                break;
            case R.id.ziji_page:
                initFragments(1);
                changeIv();
                break;
        }
        transaction.commit();
    }

    public void changeIv(){
        quanjianIv.setVisibility(View.INVISIBLE);
        zijiIv.setVisibility(View.VISIBLE);
    }
    public void changeIv2(){
        quanjianIv.setVisibility(View.VISIBLE);
        zijiIv.setVisibility(View.INVISIBLE);
    }

    private void initFragments(int i) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (i) {
            case 0:
                if (qujianFragment == null) {
                    qujianFragment = new QujianFragment();
                    map.put(0, qujianFragment);
                    transaction.add(R.id.record_content, qujianFragment);
                } else if (conventFragment == qujianFragment) {

                } else {
                    for (Map.Entry<Integer, Fragment> entry : map.entrySet()) {
                        transaction.hide(entry.getValue());  //hide 隐藏<--->代替 replace
                    }
                    transaction.show(qujianFragment);
                }
                conventFragment = qujianFragment;
                break;
            case 1:
                if (zijiFragment == null) {
                    zijiFragment = new ZijiFragment();
                    map.put(1, zijiFragment);
                    transaction.add(R.id.record_content, zijiFragment);
                } else if (conventFragment == zijiFragment) {
                } else {
                    for (Map.Entry<Integer, Fragment> entry : map.entrySet()) {
                        transaction.hide(entry.getValue());
                    }
                    transaction.show(zijiFragment);
                }
                conventFragment = zijiFragment;
                break;
        }
        transaction.commit();
    }

}
