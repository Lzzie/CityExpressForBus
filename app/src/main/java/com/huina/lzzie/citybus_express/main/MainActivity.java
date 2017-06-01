package com.huina.lzzie.citybus_express.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.fragment.FirstpageFragment;
import com.huina.lzzie.citybus_express.fragment.SecondFragment;
import com.huina.lzzie.citybus_express.model.User;
import com.huina.lzzie.citybus_express.util.SystemStatusManager;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
*
* @author lzzie
* created at 2017/2/24 13:36
*/

public class MainActivity extends Activity implements View.OnClickListener {
    private FirstpageFragment firstpageFragment;
    private SecondFragment secondFragment;
    User user = MyApplication.user;

    @BindView(R.id.first_page)
    RadioButton firstPage;
    @BindView(R.id.second_page)
    RadioButton secondPage;

    private Map<Integer, Fragment> map = new ArrayMap<>();  //HashMap,现改为ArrayMap,节约35%内存.此处只需要处理Integer,当处理数据量大时改用HashMap
    private Fragment conventFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);//@BindView
        new SystemStatusManager(this).setTranslucentStatus(R.color.white);//设置状态栏透明，参数为你要设置的颜色
        initListener();
        if (savedInstanceState == null) {
            setDefaultFragment();
        }
        //登陆或者注册成功后，带参跳转
        Intent intent = getIntent();
        int page = intent.getIntExtra("page",0);
        initFragments(page);
        if (page == 1) {
            if (user != null){
                secondPage.setChecked(true);
            }
        }

    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction(); //transaction
        firstpageFragment = new FirstpageFragment();
        map.put(0, firstpageFragment);
        transaction.replace(R.id.content, firstpageFragment);
        transaction.commit();
    }

    private void initListener() {
        firstPage.setOnClickListener(this);
        secondPage.setOnClickListener(this);
    }

    @OnClick({R.id.first_page,R.id.second_page})
    public void onClick(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (view.getId()) {
            case R.id.first_page:
                initFragments(0);
                break;
            case R.id.second_page:
                initFragments(1);
                break;
        }
        transaction.commit();
    }
    private void initFragments(int i) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (i) {
            case 0:
                if (firstpageFragment == null) {
                    firstpageFragment = new FirstpageFragment();
                    map.put(0, firstpageFragment);
                    transaction.add(R.id.content, firstpageFragment);
                } else if (conventFragment == firstpageFragment) {

                } else {
                    for (Map.Entry<Integer, Fragment> entry : map.entrySet()) {
                        transaction.hide(entry.getValue());  //hide 隐藏<--->代替 replace
                    }
                    transaction.show(firstpageFragment);
                }
                conventFragment = firstpageFragment;
                break;
            case 1:
                if (secondFragment == null) {
                    secondFragment = new SecondFragment();
                    map.put(1, secondFragment);
                    transaction.add(R.id.content, secondFragment);
                } else if (conventFragment == secondFragment) {
                } else {
                    for (Map.Entry<Integer, Fragment> entry : map.entrySet()) {
                        transaction.hide(entry.getValue());
                    }
                    transaction.show(secondFragment);
                }
                conventFragment = secondFragment;
                break;
        }
        transaction.commit();
    }

//    //退出样式 ========================== Start
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 3000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
