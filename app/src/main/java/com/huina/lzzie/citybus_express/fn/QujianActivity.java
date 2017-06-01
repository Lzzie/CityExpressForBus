package com.huina.lzzie.citybus_express.fn;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;
import com.kyleduo.switchbutton.SwitchButton;

import org.feezu.liuli.timeselector.TimeSelector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** 
* 上门取件
* @author lzzie
* created at 2017/3/18 16:44
*/

public class QujianActivity extends BaseActivity {
    private AlertDialog myDialog = null;
    private TimeSelector timeSelector;

    @BindView(R.id.jjr_name)
    TextView jjrname;
    @BindView(R.id.jjr_phone)
    TextView jjrphone;
    @BindView(R.id.jjr_dz)
    TextView jjrdz;
    @BindView(R.id.sjr_name)
    TextView sjrname;
    @BindView(R.id.sjr_phone)
    TextView sjrphone;
    @BindView(R.id.sjr_dz)
    TextView sjrdz;
    @BindView(R.id.jj_info)
    LinearLayout jjinfo;
    @BindView(R.id.sj_info)
    LinearLayout sjinfo;
    @BindView(R.id.chooseBox)
    Button chooseBox;
    @BindView(R.id.wish_time)
    TextView wishTime;
    @BindView(R.id.goods_type)
    EditText goodsType;
    @BindView(R.id.goods_weight)
    EditText goodsWeight;
    @BindView(R.id.serviceAgreement)
    TextView serviceAg;
    @BindView(R.id.informService)
    TextView informService;
    private static final String TAG ="QujianActivity" ;
    boolean choose;
    boolean ischoose;
    boolean confirmWishTime = false;
    int isUrgent ;
    String wishtime;
    int goodstype;
    String goodsName;
    double goodsweight;
    String toKen;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toKen = PreferencesUtils.getString(this,"token");

        setContentView(R.layout.activity_qujian);
        ButterKnife.bind(this);
        goodsWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        SwitchButton mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    isUrgent = 1;
                else
                    isUrgent = 0;
            }
        });

        checkBox = (CheckBox) findViewById(R.id.xieyi);
        checkBox.setChecked(true);
        Drawable drawable = this.getResources().getDrawable(R.drawable.checkbox);
        drawable.setBounds(0,0,40,40);
        checkBox.setCompoundDrawables(drawable,null,null,null);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }else{

                }
            }
        });

        if (toKen != ""){
            String jjname = PreferencesUtils.getString(this,"jijianName");
            String jjmobile = PreferencesUtils.getString(this,"jijianMobile");
            String jjadress = PreferencesUtils.getString(this,"jijianAddress");

            String sjname = PreferencesUtils.getString(this,"shoujianName");
            String sjmobile = PreferencesUtils.getString(this,"shoujianMobile");
            String sjadress = PreferencesUtils.getString(this,"shoujianAddress");

            jjrname.setText(jjname);
            jjrphone.setText(jjmobile);
            jjrdz.setText(jjadress);
            sjrname.setText(sjname);
            sjrphone.setText(sjmobile);
            sjrdz.setText(sjadress);

            if(jjrname.equals("") || jjrphone.equals("") || jjrdz.equals("") || sjrname.equals("") || sjrphone.equals("") || sjrdz.equals("")){
                ToastUtils.show(QujianActivity.this,"请检查您填写的寄收件地址是否遗漏信息");
            }else {
                ischoose = true;
            }
        }else {
            ToastUtils.show(QujianActivity.this,"请登陆");
            Intent intent = new Intent(QujianActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();
        }

        timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
//                Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
                wishTime.setText(time+":00");
            }
        }, "2017-01-01 07:00", "2025-01-01 17:00","8:00","19:00");


    }

    @OnClick({R.id.jj_info,R.id.sj_info,R.id.chooseBox,R.id.serviceAgreement,R.id.informService,R.id.wish_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jj_info:
                showMyaddress(1);
                break;
            case R.id.sj_info:
                showMyaddress(2);
                break;
            case R.id.chooseBox:
                getWishTime();
                if ( confirmWishTime!=true){
                    ToastUtils.show(QujianActivity.this,"请检查上门服务时间");
                }else if (checkBox.isChecked() == true && ischoose == true){
                    showAddorder(isUrgent);
                } else {
                    ToastUtils.show(QujianActivity.this,"请阅读并勾选服务协议与告知");
                }
                break;
            case R.id.serviceAgreement:
                serviceAgreement();
                break;
            case R.id.informService:
                informService();
                break;
            case R.id.wish_time:
                opentime();
                break;
        }
    }

    public void opentime(){
        timeSelector.show();
    }

    public void getWishTime(){
        if (wishTime.getText().toString().length()==0){

        }else {
            wishtime = wishTime.getText().toString();
            confirmWishTime=true;
        }
    }

    public void showMyaddress(int type){
        Intent intent = new Intent(QujianActivity.this, MyaddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("type",type);
        startActivity(intent);
        finish();
    }

    public void showAddorder(int isUrgent){
        goodsName = goodsType.getText().toString();
        if (goodsWeight.length()>0){
            goodsweight = Double.parseDouble(goodsWeight.getText().toString());
        }
        Intent intent = new Intent(QujianActivity.this, AddorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("isUrgent",isUrgent);
        intent.putExtra("type","0");// 0=上门取件 1=自寄
        if (confirmWishTime=true){
            intent.putExtra("wishTime",wishtime);
        }
        intent.putExtra("goodsType",goodsName);
        intent.putExtra("goodsWeight",goodsweight);
        startActivity(intent);
        finish();
    }

    private void serviceAgreement() {
        myDialog = new AlertDialog.Builder(QujianActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(760, 980);
        myDialog.getWindow().setContentView(R.layout.alert_serviceagreement_layout);
    }
    private void informService() {
        myDialog = new AlertDialog.Builder(QujianActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(760, 980);
        myDialog.getWindow().setContentView(R.layout.alert_serviceagreement_layout);
    }


}

