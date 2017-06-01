package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
/** 
* 选择支付方式
* @author lzzie
* created at 2017/3/18 16:41
* 现在Pay点击之后默认支付成功
 */

public class PayorderActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="PayorderActivity" ;

    @BindView(R.id.pay)
    Button pay;
    boolean wechat;
    boolean alipay;
    int orderStatus=2;
    int orderid;
    boolean payStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payorder);
        ButterKnife.bind(this);

        payCheck();  //CheckBox 支付方式选择

        orderid = getIntent().getIntExtra("orderid",1);
    }

    public void payCheck(){
        final CheckBox checkPay = (CheckBox) findViewById(R.id.checkPay);
        final CheckBox checkPay2 = (CheckBox) findViewById(R.id.checkPay2);

        Drawable drawable = this.getResources().getDrawable(R.drawable.checkpay);
        drawable.setBounds(0, 0, 65, 65);
        Drawable drawable2 = this.getResources().getDrawable(R.drawable.checkpay);
        drawable2.setBounds(0, 0, 65, 65);
        checkPay.setCompoundDrawables(drawable, null, null, null);
        checkPay2.setCompoundDrawables(drawable2, null, null, null);
        checkPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkPay.setChecked(true);
                    checkPay2.setChecked(false);
                    wechat=true;
                } else {
                    checkPay.setChecked(false);
                    checkPay2.setChecked(true);
                }
            }
        });
        checkPay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkPay2.setChecked(true);
                    checkPay.setChecked(false);
                    alipay=true;
                } else {
                    checkPay2.setChecked(false);
                    checkPay.setChecked(true);
                }
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wechat==true || alipay==true){
                    updateOrderStatus();
                    if (payStatus=true){
                        Intent intent = new Intent(PayorderActivity.this, PaysuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    ToastUtils.show(PayorderActivity.this,"请选择支付方式");
                }
            }
        });

    }

    public void updateOrderStatus(){
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("id",""+orderid);
        params.put("order_status",""+orderStatus);
        httpHelper.post(Constants.API.UPDATEORDERSTATUS, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    ToastUtils.show(PayorderActivity.this,"支付成功");
                    payStatus=true;
                }else {
                    ToastUtils.show(PayorderActivity.this,"支付失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(PayorderActivity.this,"支付失败");
            }
        });

    }

}
