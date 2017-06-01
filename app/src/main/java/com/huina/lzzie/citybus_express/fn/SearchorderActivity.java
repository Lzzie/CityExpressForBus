package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.adapter.SearchAdapter;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.GoodsInfoSearch;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.Schedule;
import com.huina.lzzie.citybus_express.model.Search;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/** 
* 根据订单号 查询订单
* @author lzzie
* created at 2017/3/18 16:45
*/

public class SearchorderActivity extends BaseActivity implements View.OnClickListener{
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="SearchorderActivity" ;

    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.sButton)
    ImageView sButton;
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView receivedLayout;
    private TextView sendLayout;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;

    private List<Search> ls;
    private List<GoodsInfoSearch> receivedList;
    private SearchAdapter receivedAdapter;
    private List<GoodsInfoSearch> senddList;
    private SearchAdapter sendAdapter;
    private List<GoodsInfoSearch> list;
    private SearchAdapter adapter;
    ListView recrivedsLv;
    ListView sendsLv;
    TextView unOrderFinish;
    private List<Schedule> orderFinish;   //orderFinish

    String toKen;
    String searchCode;
    String formAddress;
    String toAddress;
    String cTime;
    int orderStatus;
    int uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchorder);
        ButterKnife.bind(this);
        toKen = PreferencesUtils.getString(this,"token");
        if (toKen!=""){
            getOrderListTodo();
        }
        String orderCode = getIntent().getStringExtra("orderCode");

        if(orderCode!=""){
            search.setText(orderCode);
            searchCode = search.getText().toString();
        }

        initialisation();

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCode = search.getText().toString();
                getOrderInfo();
            }
        });

    }

    public void initialisation(){
        viewPager = (ViewPager)findViewById(R.id.viewPagerSearch);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.received_player, null);
        View view2 = inflater.inflate(R.layout.send_player, null);
        unOrderFinish = (TextView)view1.findViewById(R.id.unOrderFinish);
        recrivedsLv = (ListView)view1.findViewById(R.id.received_search_lv);

        receivedLayout = (TextView)findViewById(R.id.receivedLayout);
        sendLayout = (TextView)findViewById(R.id.sendLayout);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        receivedLayout.setOnClickListener(this);
        sendLayout.setOnClickListener(this);
        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){
            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }
            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);
    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.receivedLayout:
                //点击"寄件地址“时切换到第一页
                viewPager.setCurrentItem(0);
                break;
            case R.id.sendLayout:
                //点击“收件地址”时切换的第二页
                viewPager.setCurrentItem(1);
                break;
        }
    }

    public void getOrderInfo(){
            ArrayMap<String,String> params = new ArrayMap<>();
            params.put("orderCode",searchCode);
            httpHelper.post(Constants.API.GETORDERINFO, params, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result result1 = JsonHelper.parseObject(result,Result.class);
                    Log.d(TAG,result1.getCode().toString());
                    Log.d(TAG,result1.getMsg().toString());
                    if (result1.getCode()==0){
                        Search search = JsonHelper.parseObject(result1.getData(),Search.class);
                        MyApplication.search = search;
                        formAddress = search.getFromAddress();
                        toAddress = search.getToAddress();
                        cTime = search.getCreateTime();
                        orderStatus = search.getOrderStatus();
                        ToastUtils.show(SearchorderActivity.this,"查询成功");
                        getOrderForSearchCode(); //根据订单号目标查询
                        getItem();
                    }else {
                        ToastUtils.show(SearchorderActivity.this,"请您输入十五位运单号并检查");
                    }
                }
                @Override
                public void onError(Response response, int code, Exception e) {
                    ToastUtils.show(SearchorderActivity.this,"查询失败，请检查网络");
                }
            });
    }

    private void getOrderForSearchCode(){
        //根据订单号 目标查询
        receivedList = new ArrayList<>();
        receivedAdapter = new SearchAdapter(this,receivedList);
        recrivedsLv.setAdapter(receivedAdapter);
        for (int i=0;i<1;i++){
            GoodsInfoSearch goodsInfoSearch = new GoodsInfoSearch();
            goodsInfoSearch.setSearch_id(searchCode);
            goodsInfoSearch.setForm_address(formAddress);
            goodsInfoSearch.setTo_address(toAddress);
            goodsInfoSearch.setCreat_time(cTime);

            receivedList.add(goodsInfoSearch);
        }
        receivedAdapter.notifyDataSetChanged();
    }

    public void getItem(){
        recrivedsLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (orderStatus==2){
                    getOrderInfo(searchCode);
                }else if (orderStatus==1){
                    payOrder(1);
                }

            }
        });
    }
    public void payOrder(int sign){
        Intent intent = new Intent(SearchorderActivity.this, AddorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("sign",sign);
        intent.putExtra("orderCodeFromRecord",searchCode);
        startActivity(intent);
    }

    public void getOrderInfo(String orderCode){
        Intent intent = new Intent(SearchorderActivity.this, GetorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("orderCode",orderCode);
        startActivity(intent);
    }

    //    查看未支付   订单状态 1:未支付,2取件寄件中,3配送中,4已完成,5已取消
    private void getOrderListTodo(){
        ArrayMap<String,String> params = new ArrayMap<>();
        uid = PreferencesUtils.getInt(this,"userId");
        params.put("uid",""+uid);
        httpHelper.post(Constants.API.GETORDERLISTTODO, params, new SpotsCallBack(this) {
            @Override
            public void onSuccess(Response response, String result) {
                Result result1 = JsonHelper.parseObject(result,Result.class);
                Log.d(TAG,result1.getCode().toString());
                Log.d(TAG,result1.getMsg().toString());
                if (result1.getCode()==0){
                    List<Schedule> schedule  = JsonHelper.parseArray(result1.getData(),Schedule.class);
                    List<Schedule> list1 = new ArrayList<Schedule>();
                    if (schedule.size()>0){
                        for (int i=0;i<schedule.size();i++){
                            if (schedule.get(i).getOrderStatus()==4){
                                list1.add(schedule.get(i));
                                orderFinish = list1;
                                if (orderFinish.size()>0){
                                    successInitData();
                                }else {
                                    unOrderFinish.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }else {

                    }

                }else {
                    ToastUtils.show(SearchorderActivity.this,"查询失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(SearchorderActivity.this,"查询失败，请检查网络");
            }
        });
    }
    private void successInitData(){
        list = new ArrayList<>();
        adapter = new SearchAdapter(this,list);
        recrivedsLv.setAdapter(adapter);
        for (int i=0;i<orderFinish.size();i++){
            if (orderFinish.get(i).getOrderStatus()==1){
                GoodsInfoSearch goodsInfoSearch  = new GoodsInfoSearch();
                goodsInfoSearch.setSearch_id(orderFinish.get(i).getOrderCode());
                goodsInfoSearch.setForm_address(orderFinish.get(i).getFromAddress());
                goodsInfoSearch.setTo_address(orderFinish.get(i).getToAddress());
                goodsInfoSearch.setCreat_time(orderFinish.get(i).getCreateTime());
                list.add(goodsInfoSearch);
            }else {

            }
        }
        adapter.notifyDataSetChanged();
    }



}
