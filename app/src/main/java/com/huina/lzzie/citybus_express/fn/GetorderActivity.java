package com.huina.lzzie.citybus_express.fn;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.Search;
import com.huina.lzzie.citybus_express.util.Ecoad;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
* BarCode
 * 确定支付成功后，生成条形码
* @author lzzie
* created at 2017/3/17 14:11
*/
public class GetorderActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="GetorderActivity" ;
    private AlertDialog myDialog = null;

    @BindView(R.id.jijianren_name)
    TextView jijianrenname;
    @BindView(R.id.jijianren_address)
    TextView jijianrenaddress;
    @BindView(R.id.shoujianren_name)
    TextView shoujianrenname;
    @BindView(R.id.shoujianren_address)
    TextView shoujianrenaddress;
    @BindView(R.id.jijianren_phone)
    TextView jijirenphone;
    @BindView(R.id.shoujianren_phone)
    TextView shoujianrenphone;
    @BindView(R.id.order_sum)
    TextView orderSum;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.order_code)
    TextView ordercode;
    @BindView(R.id.barCode)
    ImageView barCode;
    @BindView(R.id.barCodeTv)
    TextView barCodeTv;
    @BindView(R.id.orderStatus)
    TextView orderStatus;
    @BindView(R.id.barCodeLayout)
    LinearLayout barCodeLayout;
    String orderCode;
    int isUrgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getorder);
        ButterKnife.bind(this);
        orderCode = getIntent().getStringExtra("orderCode");
        getOrderinfo();

    }

    public void getOrderinfo(){
        ArrayMap<String,String> params = new ArrayMap<>();
        params.put("orderCode",orderCode);
        httpHelper.post(Constants.API.GETORDERINFO, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    Search search = JsonHelper.parseObject(result1.getData(),Search.class);
                    MyApplication.search = search;
                    jijianrenname.setText(search.getFromName());
                    jijirenphone.setText(search.getFromMobile());
                    jijianrenaddress.setText(search.getFromCity()+"  "+search.getFromAddress());
                    shoujianrenname.setText(search.getToName());
                    shoujianrenphone.setText(search.getToMobile());
                    shoujianrenaddress.setText(search.getToCity()+"  "+search.getToAddress());
                    ordercode.setText(search.getOrderCode());
                    createTime.setText(search.getCreateTime());
                    isUrgent = search.getIsUrgent();
                    if (isUrgent==1){
                        orderSum.setText("15元");
                    }
                    if (search.getOrderStatus()==1){
                        orderStatus.setText("未支付");
                        barCodeLayout.setVisibility(View.GONE);
                    }else {
                        CreateBarCode();
                    }
                }else {
                    ToastUtils.show(GetorderActivity.this,"获取失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(GetorderActivity.this,"获取失败");
            }
        });

    }

    //根据单号生成条形码
    public void CreateBarCode(){
            String contentString = orderCode;

//        if (!TextUtils.isEmpty(contentString)) {
//            barCode.setImageBitmap(EncodingUtils.creatBarcode(getApplicationContext(), contentString, 1260, 700, true));
//        } else {
//            Toast.makeText(GetorderActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
//        }

            Ecoad ecc=new Ecoad(barCode.getWidth(), barCode.getHeight());
            try {
                Bitmap bitm=ecc.bitmap1(contentString);
                barCode.setImageBitmap(bitm);
                barCodeTv.setText(contentString);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
