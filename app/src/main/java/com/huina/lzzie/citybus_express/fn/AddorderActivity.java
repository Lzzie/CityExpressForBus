package com.huina.lzzie.citybus_express.fn;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Order;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.Search;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;
/**
* 新增订单
* @author lzzie
* created at 2017/3/17 14:11
*/
public class AddorderActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="AddorderActivity" ;
    private AlertDialog myDialog = null;

    @BindView(R.id.off_order)
    TextView offOrder;
    @BindView(R.id.go_payment)
    TextView goPayment;
    @BindView(R.id.jijianren_name)
    TextView jijianrenname;
    @BindView(R.id.jijianren_phone)
    TextView jijianphone;
    @BindView(R.id.jijianren_address)
    TextView jijianrenaddress;
    @BindView(R.id.shoujianren_name)
    TextView shoujianrenname;
    @BindView(R.id.shoujianren_phone)
    TextView shoujianrenphone;
    @BindView(R.id.shoujianren_address)
    TextView shoujianrenaddress;

    @BindView(R.id.order_sum)
    TextView orderSum;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.order_code)
    TextView ordercode;

    String orderCode;
    String orderCodeFromRecord;
    int isUrgent;
    int type;
    int orderid; //订单ID，修改订单状态，取消订单
    int sign;//判断是否从Record寄件记录跳转过来
    String wishtime;
    String goodsName; //物品类别即名字 默认为 " "
    double goodsWeight; //物品重量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addorder);
        ButterKnife.bind(this);

//        String goodsname = getIntent().getStringExtra("goodsType");
//        if (goodsname!=null){
//            goodsName = goodsname;
//        }else {
//            goodsName = " "+getIntent().getStringExtra("goodsType");
//        }
        goodsWeight = getIntent().getDoubleExtra("goodsWeight",1);
        goodsName = " "+getIntent().getStringExtra("goodsType");
        isUrgent = getIntent().getIntExtra("isUrgent",0);
        type = getIntent().getIntExtra("type",0);
        sign = getIntent().getIntExtra("sign",0);
        orderCodeFromRecord= getIntent().getStringExtra("orderCodeFromRecord");
        wishtime = getIntent().getStringExtra("wishTime");
        if (sign==0){
            getOrderinfo();
        }else {
           payOrderFormRecord();
        }

    }

    @OnClick({R.id.off_order,R.id.go_payment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.off_order:
                creatAlertDialog();
                break;
            case R.id.go_payment:
                payOrder();
                break;
        }
    }

    public void getOrderinfo(){

        String jjname = PreferencesUtils.getString(this,"jijianName");
        String jjmobile = PreferencesUtils.getString(this,"jijianMobile");
        String jjadress = PreferencesUtils.getString(this,"jijianAddress");
        String jjid = PreferencesUtils.getString(this,"jijianId");

        String sjname = PreferencesUtils.getString(this,"shoujianName");
        String sjmobile = PreferencesUtils.getString(this,"shoujianMobile");
        String sjadress = PreferencesUtils.getString(this,"shoujianAddress");
        String sjid = PreferencesUtils.getString(this,"shoujianId");

        jijianrenname.setText(jjname);
        jijianphone.setText(jjmobile);
        jijianrenaddress.setText(jjadress);
        shoujianrenname.setText(sjname);
        shoujianrenphone.setText(sjmobile);
        shoujianrenaddress.setText(sjadress);

        int uid = PreferencesUtils.getInt(this,"userId");
        int isUrgent1 = isUrgent;
        if (isUrgent1 ==1){
            orderSum.setText("15元");
        }
//        int type = 0;  // 0=上门取件 1=自寄
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("uid",""+uid);  //用户uid
        params.put("from_address",""+jjid);  // 寄件人ID
        params.put("to_address",""+sjid);    // 收件人ID
        params.put("name",goodsName);//物品名字
        params.put("weight"," "+goodsWeight);//物品重量
        params.put("count","1");//物品数量
        params.put("isUrgent",""+isUrgent1);//是否加急
        params.put("type",""+type);
        params.put("appointment_time",wishtime); //上门服务时间
        httpHelper.post(Constants.API.ADDORDER, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    Order order = JsonHelper.parseObject(result1.getData(),Order.class);
                    MyApplication.order=order;
                    String time = order.getCreateTime();
                    String orderId = order.getOrderCode();
                    orderid = order.getId();
                    createTime.setText(time);
                    ordercode.setText(orderId);
                    orderCode = orderId;
                }else {
                    ToastUtils.show(AddorderActivity.this,"添加失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(AddorderActivity.this,"添加失败,请检查网络后重试");
            }
        });
    }

    private void creatAlertDialog() {
        myDialog = new AlertDialog.Builder(AddorderActivity.this).create();
        myDialog.show();
        myDialog.getWindow().setLayout(860, 760);
        myDialog.getWindow().setContentView(R.layout.alert_cancel_layout);
        myDialog.getWindow()
                .findViewById(R.id.alert_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder();//取消订单
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
    public void cancelOrder(){
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("id",orderid+"");
        httpHelper.post(Constants.API.CANCLEORDER, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                if (result1.getCode()==0){
                    ToastUtils.show(AddorderActivity.this,"取消订单成功");
                }else {
                    ToastUtils.show(AddorderActivity.this,result1.getMsg().toString());
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                    ToastUtils.show(AddorderActivity.this,"请检查网络与设置");
            }
        });
    }

    public void payOrder(){
            Intent intent = new Intent(AddorderActivity.this, PayorderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("orderid",orderid);
            startActivity(intent);
            finish();
    }

    //获得进行支付页面的数据   数据存在Search类中
    public void payOrderFormRecord(){
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("orderCode",orderCodeFromRecord);
        httpHelper.post(Constants.API.GETORDERINFO, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    Search search = JsonHelper.parseObject(result1.getData(),Search.class);
                    jijianrenname.setText(search.getFromName());
                    jijianrenaddress.setText(search.getFromCity()+"  "+search.getFromAddress());
                    jijianphone.setText(search.getFromMobile());
                    shoujianrenname.setText(search.getToName());
                    shoujianrenaddress.setText(search.getToCity()+"  "+search.getToAddress());
                    shoujianrenphone.setText(search.getToMobile());
                    ordercode.setText(orderCodeFromRecord);
                    createTime.setText(search.getCreateTime());
                    orderid= search.getId();
                    if (search.getIsUrgent()==1){
                        orderSum.setText("15元");
                    }
                }else {
                    ToastUtils.show(AddorderActivity.this,"3333333333333333");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(AddorderActivity.this,"33333333333333333333");
            }
        });


    }

}
