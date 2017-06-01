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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** 
* 邮柜自寄
* @author lzzie
* created at 2017/3/18 16:46
*/

public class ZijiActivity extends BaseActivity {
    private AlertDialog myDialog = null;

    @BindView(R.id.jjr_name_z)
    TextView jjrname;
    @BindView(R.id.jjr_phone_z)
    TextView jjrphone;
    @BindView(R.id.jjr_dz_z)
    TextView jjrdz;
    @BindView(R.id.sjr_name_z)
    TextView sjrname;
    @BindView(R.id.sjr_phone_z)
    TextView sjrphone;
    @BindView(R.id.sjr_dz_z)
    TextView sjrdz;
    @BindView(R.id.jj_info_z)
    LinearLayout jjinfo;
    @BindView(R.id.sj_info_z)
    LinearLayout sjinfo;
    @BindView(R.id.goods_type)
    EditText goodsType;
    @BindView(R.id.goods_weight)
    EditText goodsWeight;
    @BindView(R.id.chooseBox_z)
    Button chooseBox;
    @BindView(R.id.serviceAgreement)
    TextView serviceAg;
    @BindView(R.id.informService)
    TextView informService;

    boolean choose;
    boolean ischoose;
    int isUrgent ;//是否加急
    int signFromZiji = 1;
    String toKen;
    CheckBox checkBox;
    String goodsName;
    double goodsweight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toKen = PreferencesUtils.getString(this,"token");

        setContentView(R.layout.activity_ziji);
        ButterKnife.bind(this);
        goodsWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        SwitchButton mSwitchButton = (SwitchButton) findViewById(R.id.switchButton_z);
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    isUrgent = 1;
                else
                    isUrgent = 0;
            }
        });

        checkBox = (CheckBox) findViewById(R.id.xieyi_z);
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

        if ( toKen != "") {

            String jjname = PreferencesUtils.getString(this, "jijianName");
            String jjmobile = PreferencesUtils.getString(this, "jijianMobile");
            String jjadress = PreferencesUtils.getString(this, "jijianAddress");

            String sjname = PreferencesUtils.getString(this, "shoujianName");
            String sjmobile = PreferencesUtils.getString(this, "shoujianMobile");
            String sjadress = PreferencesUtils.getString(this, "shoujianAddress");

            jjrname.setText(jjname);
            jjrphone.setText(jjmobile);
            jjrdz.setText(jjadress);
            sjrname.setText(sjname);
            sjrphone.setText(sjmobile);
            sjrdz.setText(sjadress);

            if(jjrname.equals("") || jjrphone.equals("") || jjrdz.equals("") || sjrname.equals("") || sjrphone.equals("") || sjrdz.equals("")){
                ToastUtils.show(ZijiActivity.this,"请检查您填写的寄收件地址是否遗漏信息");
            }else {
                ischoose = true;
            }
        }else {
            ToastUtils.show(ZijiActivity.this,"请登陆");
            Intent intent = new Intent(ZijiActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();
        }

    }

    @OnClick({R.id.jj_info_z,R.id.sj_info_z,R.id.chooseBox_z,R.id.serviceAgreement,R.id.informService})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jj_info_z:
                showMyaddress(1);
                break;
            case R.id.sj_info_z:
                showMyaddress(2);
                break;
            case R.id.chooseBox_z:
                if (checkBox.isChecked() == true & ischoose == true){
                    showAddorder(isUrgent,1);
                }else {
                    ToastUtils.show(ZijiActivity.this,"请阅读并勾选服务协议与告知");
                }
                break;
            case R.id.serviceAgreement:
                serviceAgreement();
                break;
            case R.id.informService:
                informService();
                break;
        }
    }

    public void showMyaddress(int type){
        Intent intent = new Intent(ZijiActivity.this, MyaddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("type",type);
        intent.putExtra("signFromZiji",signFromZiji);
        startActivity(intent);
        finish();
    }

    public void showAddorder(int isUrgent,int type){
        goodsName = goodsType.getText().toString();
        if (goodsWeight.length()>0){
            goodsweight = Double.parseDouble(goodsWeight.getText().toString());
        }
        Intent intent = new Intent(ZijiActivity.this, AddorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("isUrgent",isUrgent);
        intent.putExtra("type",type);
        intent.putExtra("goodsType",goodsName);
        intent.putExtra("goodsWeight",goodsweight);
        startActivity(intent);
        finish();
    }

    private void serviceAgreement() {
        myDialog = new AlertDialog.Builder(ZijiActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(760, 980);
        myDialog.getWindow().setContentView(R.layout.alert_serviceagreement_layout);
    }
    private void informService() {
        myDialog = new AlertDialog.Builder(ZijiActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(760, 980);
        myDialog.getWindow().setContentView(R.layout.alert_serviceagreement_layout);
    }

}
