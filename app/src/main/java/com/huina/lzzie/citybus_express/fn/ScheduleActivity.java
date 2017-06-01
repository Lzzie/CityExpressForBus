package com.huina.lzzie.citybus_express.fn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.adapter.ScheduleAdapter;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.GoodsInfoSchedule;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.Schedule;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
* 待办业务
* @author lzzie
* created at 2017/3/17 12:50
*/
public class ScheduleActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    ListView goodsLv;
    ListView goodsLv2;
    private List<Schedule> unpaid;   //将数据转到unpaid
    private List<Schedule> unsend;
    private List<GoodsInfoSchedule> list;
    private ScheduleAdapter adapter;
    private List<GoodsInfoSchedule> list2;
    private ScheduleAdapter adapter2;

    TextView unpaidTv;
    TextView unsendTv;
    TextView scheduleTv;  //未投递提示
    int uid;
    String toKen;
    String orderCode;
    int orderStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        toKen = PreferencesUtils.getString(this,"token");
        if (toKen!=""){
            Initialise();
            goodsLv = (ListView)view1.findViewById(R.id.unpaid_lv);
            goodsLv2 = (ListView)view2.findViewById(R.id.unsend_lv);
            unpaidTv = (TextView)view1.findViewById(R.id.unpaid_tv);
            unsendTv = (TextView)view2.findViewById(R.id.unsen_tv);
            scheduleTv = (TextView)view2.findViewById(R.id.scheduleTv);

            getOrderListTodo();
            getOrderListTodo2();
        }else {
            ToastUtils.show(ScheduleActivity.this,"请登陆");
            Intent intent = new Intent(ScheduleActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            finish();
        }

    }

    public void  Initialise(){
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(R.layout.unpaid_player, null);
        view2 = mInflater.inflate(R.layout.unsend_player, null);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("未支付");
        mTitleList.add("未寄件");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }
    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

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
                            if (schedule.get(i).getOrderStatus()==1){
                                list1.add(schedule.get(i));
                                unpaid = list1;
                            }
                        }
                        if (unpaid!=null){
                            successInitData();
                            getItem();
                        }else {
                            unpaidTv.setVisibility(View.VISIBLE);
                        }
                    }else {
                        unpaidTv.setVisibility(View.VISIBLE);
                    }

                }else {
                    ToastUtils.show(ScheduleActivity.this,"查询失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(ScheduleActivity.this,"查询失败，请检查网络");
            }
        });
    }
    private void successInitData(){
        list = new ArrayList<>();
        adapter = new ScheduleAdapter(this,list);
        goodsLv.setAdapter(adapter);
        for (int i=0;i<unpaid.size();i++){
            if (unpaid.get(i).getOrderStatus()==1){
                GoodsInfoSchedule goodInfoSchedule  = new GoodsInfoSchedule();
                goodInfoSchedule.setSearch_id(unpaid.get(i).getOrderCode());
                goodInfoSchedule.setForm_address(unpaid.get(i).getFromAddress());
                goodInfoSchedule.setTo_address(unpaid.get(i).getToAddress());
                goodInfoSchedule.setCreat_time(unpaid.get(i).getCreateTime());
                list.add(goodInfoSchedule);
            }else {

            }
        }
        adapter.notifyDataSetChanged();
    }

    public void getItem(){
        goodsLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //获得选中项的对象
                for (int i=0;i<unpaid.size();i++){
                    if (unpaid.get(i).getOrderStatus()==1) {
                        orderCode = unpaid.get(arg2).getOrderCode();
                        orderStatus = unpaid.get(arg2).getOrderStatus();
                        payOrder(1);
                        break;
                    }
                }
            }
        });
    }

//    查件未寄件   订单状态 1:未支付,2取件寄件中,3配送中,4已完成,5已取消
    private void getOrderListTodo2(){
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
                    List<Schedule> list2 = new ArrayList<Schedule>();
                    if (schedule.size()>0){
                        for (int i=0;i<schedule.size();i++){
                            if (schedule.get(i).getOrderStatus()==2){
                                list2.add(schedule.get(i));
                                unsend = list2;
                            }
                        }
                        if (unsend!=null){
                            successInitData2();
                            getItem2();
                            scheduleTv.setVisibility(View.VISIBLE);
                        }else {
                            unsendTv.setVisibility(View.VISIBLE);
                        }
                    }else {
                        unsendTv.setVisibility(View.VISIBLE);
                    }

                }else {
                    ToastUtils.show(ScheduleActivity.this,"查询失败");
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(ScheduleActivity.this,"查询失败，请检查网络");
            }
        });
    }
    private void successInitData2(){
        list2 = new ArrayList<>();
        adapter2 = new ScheduleAdapter(this,list2);
        goodsLv2.setAdapter(adapter2);
        for (int i=0;i<unsend.size();i++){
            if (unsend.get(i).getOrderStatus()==2){
                GoodsInfoSchedule goodInfoSchedule  = new GoodsInfoSchedule();
                goodInfoSchedule.setSearch_id(unsend.get(i).getOrderCode());
                goodInfoSchedule.setForm_address(unsend.get(i).getFromAddress());
                goodInfoSchedule.setTo_address(unsend.get(i).getToAddress());
                goodInfoSchedule.setCreat_time(unsend.get(i).getCreateTime());
                list2.add(goodInfoSchedule);
            }else {

            }
        }
        adapter2.notifyDataSetChanged();
    }
    public void getItem2(){
        goodsLv2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //获得选中项的对象
                for (int i=0;i<unsend.size();i++){
                    if (unsend.get(i).getOrderStatus()==2) {
                        orderCode = unsend.get(arg2).getOrderCode();
                        orderStatus = unsend.get(arg2).getOrderStatus();
                        getOrderInfo(orderCode);
                        break;
                    }
                }

            }
        });
    }

    public void payOrder(int sign){
        Intent intent = new Intent(ScheduleActivity.this, AddorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("sign",sign);
        intent.putExtra("orderCodeFromRecord",orderCode);
        startActivity(intent);
    }

    public void getOrderInfo(String orderCode){
        Intent intent = new Intent(ScheduleActivity.this, GetorderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("orderCode",orderCode);
        startActivity(intent);
    }



}
