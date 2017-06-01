package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.adapter.AddressAdapter;
import com.huina.lzzie.citybus_express.adapter.SjaddressAdapter;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Address;
import com.huina.lzzie.citybus_express.model.GoodsInfoAddress;
import com.huina.lzzie.citybus_express.model.GoodsInfoSjaddress;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
* 通过“我的” 我的地址
* @author lzzie
* created at 2017/3/18 16:43
*/
public class OnlymyaddressActivity extends BaseActivity implements View.OnClickListener {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="OnlymyaddressActivity" ;
    private List<Address> ls;
    private List<Address> ls2;
    private View view1, view2;//页卡视图

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView jjLayout;
    private TextView sjLayout;
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

    ListView goodsLv;
    ListView sjgoodsLv;
    private List<GoodsInfoAddress> list;
    private AddressAdapter adapter;
    private List<GoodsInfoSjaddress> sjlist;
    private SjaddressAdapter sjadapter;
    String toKen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toKen = PreferencesUtils.getString(this,"token");
        if (toKen==""){
            ToastUtils.show(OnlymyaddressActivity.this,"请您先登录");
            Intent intent = new Intent(OnlymyaddressActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_myaddress);

            getJjAddress(); //加载数据
            getSjAddress();

            initialisation();
//            mrLayout();  //下拉刷新控件
        }

    }

    public void initialisation(){
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        view1 = inflater.inflate(R.layout.jj_player, null);
        view2 = inflater.inflate(R.layout.sj_player, null);
        goodsLv = (ListView)view1.findViewById(R.id.address_goods_lv);
        sjgoodsLv = (ListView)view2.findViewById(R.id.sj_address_lv);
        jjLayout = (TextView)findViewById(R.id.jjLayout);
        sjLayout = (TextView)findViewById(R.id.sjLayout);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        jjLayout.setOnClickListener(this);
        sjLayout.setOnClickListener(this);
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

//    public void mrLayout(){
//        MaterialRefreshLayout materialRefreshLayout = (MaterialRefreshLayout)view1.findViewById(R.id.refresh);  //下拉刷新控件
//        MaterialRefreshLayout materialRefreshLayout2 = (MaterialRefreshLayout)view2.findViewById(R.id.refresh2);  //下拉刷新控件
//        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
//            @Override
//            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
//                //下拉刷新...
//                refresh();
//            }
//
//            @Override
//            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//                //上拉刷新...
//                refresh();
//            }
//        });
//        // 结束下拉刷新...
//        materialRefreshLayout.finishRefresh();
//        // 结束上拉刷新...
//        materialRefreshLayout.finishRefreshLoadMore();
//
//        materialRefreshLayout2.setMaterialRefreshListener(new MaterialRefreshListener() {
//            @Override
//            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
//                //下拉刷新...
//                refresh();
//            }
//
//            @Override
//            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//                //上拉刷新...
//                refresh();
//            }
//        });
//        // 结束下拉刷新...
//        materialRefreshLayout2.finishRefresh();
//        // 结束上拉刷新...
//        materialRefreshLayout2.finishRefreshLoadMore();
//    }

//获取寄件地址 ----------------------------------------start
    public void getJjAddress(){
        if (toKen != ""){
            int uid = PreferencesUtils.getInt(this,"userId");
            int jjtype = 1;
            ArrayMap<String,String> params = new ArrayMap<>();
            params.put("uid",""+uid);
            params.put("type",""+jjtype);
            httpHelper.post(Constants.API.FINDALLCONSIGNEEBYUID, params, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result result1 = JsonHelper.parseObject(result,Result.class);
                    if (result1.getCode()==0){
                        List<Address> send  = JsonHelper.parseArray(result1.getData(),Address.class);
                        ls = send;
                        successInitData();
                        getItem();//1代表寄件
                    }else {
                        ToastUtils.show(OnlymyaddressActivity.this,"查询失败");
                    }
                }
                @Override
                public void onError(Response response, int code, Exception e) {
                    ToastUtils.show(OnlymyaddressActivity.this,"查询失败");
                }
            });
        }
        else {
            ToastUtils.show(OnlymyaddressActivity.this,"请您先登录");
        }
    }
    private void successInitData(){
        list = new ArrayList<>();
        adapter = new AddressAdapter(this,list);
        goodsLv.setAdapter(adapter);
        for (int i=0;i<ls.size();i++){
            GoodsInfoAddress goodsInfoAddress = new GoodsInfoAddress();
            goodsInfoAddress.setAdd_jj_name(ls.get(i).getName());
            goodsInfoAddress.setAdd_jj_phone(ls.get(i).getMobile());
            goodsInfoAddress.setAdd_jj_dz(ls.get(i).getCity()+"  "+ls.get(i).getAddress());

            list.add(goodsInfoAddress);
        }
        adapter.notifyDataSetChanged();
    }
    public void getItem(){
        goodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //获得选中项的对象
                String id = ""+ls.get(arg2).getId();
                int type = 1;
                Intent intent=new Intent();
                intent.putExtra("sendId",id);//寄件人id
                intent.putExtra("sendType",type);
                intent.setClass(OnlymyaddressActivity.this, ChangeaddressActivity.class);
                startActivity(intent);
            }
        });
    }

//获取寄件地址 ------------end

//获取收件地址 ----------------------------------------start
    public void getSjAddress(){
        if (toKen != ""){
            int uid = PreferencesUtils.getInt(this,"userId");
            int sjtype = 0;
            ArrayMap<String,String> params2 = new ArrayMap<>();
            params2.put("uid",""+uid);
            params2.put("type",""+sjtype);
            httpHelper.post(Constants.API.FINDALLCONSIGNEEBYUID, params2, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result result2 = JsonHelper.parseObject(result,Result.class);
                    if (result2.getCode()==0){
                        List<Address> send2  = JsonHelper.parseArray(result2.getData(),Address.class);
                        ls2 = send2;
                        successInitData2();
                        getItem2();
                    }else {
                        ToastUtils.show(OnlymyaddressActivity.this,"查询失败");
                    }
                }
                @Override
                public void onError(Response response, int code, Exception e) {
                    ToastUtils.show(OnlymyaddressActivity.this,"查询失败");
                }
            });
        }
        else {
            ToastUtils.show(OnlymyaddressActivity.this,"请您先登录");
        }
    }
    private void successInitData2(){
        sjlist = new ArrayList<>();
        sjadapter = new SjaddressAdapter(this,sjlist);
        sjgoodsLv.setAdapter(sjadapter);
        for (int i=0;i<ls2.size();i++){
            GoodsInfoSjaddress goodsInfoSjaddress = new GoodsInfoSjaddress();
            goodsInfoSjaddress.setAdd_sj_name(ls2.get(i).getName());
            goodsInfoSjaddress.setAdd_sj_phone(ls2.get(i).getMobile());
            goodsInfoSjaddress.setAdd_sj_dz(ls2.get(i).getCity()+"  "+ls2.get(i).getAddress());

            sjlist.add(goodsInfoSjaddress);
        }
        sjadapter.notifyDataSetChanged();
    }
    public void getItem2(){
        sjgoodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //获得选中项的对象
                String id = ""+ls2.get(arg2).getId();
                int type = 0;
                Intent intent=new Intent();
                intent.putExtra("sendId",id);//收件人件人id
                intent.putExtra("sendType",type);
                intent.setClass(OnlymyaddressActivity.this, ChangeaddressActivity.class);
                startActivity(intent);
            }
        });
    }
//获取收件地址 ------------end
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.jjLayout:
                //点击"寄件地址“时切换到第一页
                viewPager.setCurrentItem(0);
                break;
            case R.id.sjLayout:
                //点击“收件地址”时切换的第二页
                viewPager.setCurrentItem(1);
                break;
        }
    }

    public void showTjjjdz(View view){
        Intent intent = new Intent(OnlymyaddressActivity.this,TjjjdzActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("mainForm",1); //mainForm 为1 则证明为从“我的地址”跳转来
        startActivity(intent);
    }
    public void showTjsjdz(View view){
        Intent intent = new Intent(OnlymyaddressActivity.this,TjsjdzActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("mainForm",1); //mainForm 为1 则证明为从“我的地址”跳转来
        startActivity(intent);
    }

    public void refresh() {
        onCreate(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }
}
