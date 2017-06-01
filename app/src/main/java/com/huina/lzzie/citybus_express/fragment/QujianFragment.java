package com.huina.lzzie.citybus_express.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import com.hankkin.library.RefreshSwipeMenuListView;
import com.hankkin.library.SwipeMenu;
import com.hankkin.library.SwipeMenuCreator;
import com.hankkin.library.SwipeMenuItem;
import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.adapter.RecordAdapter;
import com.huina.lzzie.citybus_express.fn.AddorderActivity;
import com.huina.lzzie.citybus_express.fn.GetorderActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.GoodsInfoRecord;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.UserOrderInfo;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
* RecordActivity 寄件记录
* @author lzzie
* created at 2017/2/24 13:37
*/

public class QujianFragment extends Fragment implements RefreshSwipeMenuListView.OnRefreshListener {
	private static final String TAG ="QujianFragment" ;
	private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

	private List<UserOrderInfo> ls;

	private Context context;
 	private RefreshSwipeMenuListView rsmLv;
	private List<GoodsInfoRecord> data;
	private RecordAdapter adapter;
	private int po;
	private int uid;
	private int type;
	String toKen;
	String orderCode;
	int orderId;
	int orderStatus;  //订单状态 1:未支付,2取件寄件中,3配送中,4已完成,5已取消

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_qujian, container, false);
		rsmLv = (RefreshSwipeMenuListView) view.findViewById(R.id.swipe);

		data = new ArrayList<>();
		getUserQuanjianInfo();
		adapter = new RecordAdapter(getActivity(),data);
		rsmLv.setAdapter(adapter);
		rsmLv.setListViewMode(RefreshSwipeMenuListView.HEADER);
		rsmLv.setOnRefreshListener(this);
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// 创建删除选项
				SwipeMenuItem argeeItem = new SwipeMenuItem(context.getApplicationContext());
				argeeItem.setBackground(new ColorDrawable(getResources().getColor(R.color.font)));
				argeeItem.setWidth(dp2px(80, context.getApplicationContext()));
				argeeItem.setTitle("删除");
				argeeItem.setTitleSize(16);
				argeeItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(argeeItem); // 添加选项
			}
		};
		rsmLv.setMenuCreator(creator);
		rsmLv.setOnMenuItemClickListener(new RefreshSwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0: //第一个选项
						orderId = ls.get(position).getId();
						orderStatus = ls.get(position).getOrderStatus();
						if (orderStatus==2){
							ToastUtils.show(getActivity(),"当前已支付，正在寄件中，无法删除");
						}else {
							del(position,rsmLv.getChildAt(position+1-rsmLv.getFirstVisiblePosition()));
							DeleteOrderInfoForOrderCode();
						}
						break;
				}
			}
		});
		return view;
	}

	public void DeleteOrderInfoForOrderCode(){
		ArrayMap<String,String> params = new ArrayMap<>();
		params.put("id",""+orderId);
		httpHelper.post(Constants.API.DELORDER, params, new SpotsCallBack(getActivity()) {
			@Override
			public void onSuccess(Response response, String result) {
				Result result1 = JsonHelper.parseObject(result,Result.class);
				Log.d(TAG,result1.getCode().toString());
				Log.d(TAG,result1.getMsg().toString());
				if (result1.getCode()==0){
					ToastUtils.show(context,"删除成功");
				}else {
					ToastUtils.show(context,"删除失败");
				}
			}
			@Override
			public void onError(Response response, int code, Exception e) {
				ToastUtils.show(context,"请检查网络后重试");
			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		toKen = PreferencesUtils.getString(getActivity(),"token");
	}

	public void getUserQuanjianInfo(){
		if (toKen!=""){
			uid = PreferencesUtils.getInt(getActivity(),"userId");
			type = 0; //0:上门,1 自寄
		}
		ArrayMap<String,String> params = new ArrayMap<>();
		params.put("uid",""+uid);
		params.put("type",""+type);
		httpHelper.post(Constants.API.GETORDERLISTBYUID, params, new SpotsCallBack(getActivity()) {
			@Override
			public void onSuccess(Response response, String result) {
				Result result1 = JsonHelper.parseObject(result,Result.class);
				List<UserOrderInfo> qujian = JsonHelper.parseArray(result1.getData(),UserOrderInfo.class);
				ls = qujian;
				initData();
				Log.d(TAG,result1.getMsg());
				Log.d(TAG,result1.getData());
				if (result1.getCode()==0){
					if (ls.size()>0){
						getItem();
					}else {
						ToastUtils.show(getActivity(),"您还没有上门取件订单");
					}
				}else {
					ToastUtils.show(context,"查询失败");
				}
			}
			@Override
			public void onError(Response response, int code, Exception e) {
				ToastUtils.show(context,"查询失败");
			}
		});

	}

	private void initData(){
		for (int i=0;i<ls.size();i++){
			GoodsInfoRecord goodsInfoRecord = new GoodsInfoRecord();
			goodsInfoRecord.setRecord_id(ls.get(i).getOrderCode());
			goodsInfoRecord.setForm_address(ls.get(i).getFromAddress());
			goodsInfoRecord.setTo_address(ls.get(i).getToAddress());
			goodsInfoRecord.setCreat_time(ls.get(i).getCreateTime());
			if (ls.get(i).getOrderStatus()==1){
				goodsInfoRecord.setOrder_status("未支付");
			}else if (ls.get(i).getOrderStatus()==2){
				goodsInfoRecord.setOrder_status("未寄件");
			}else {
				goodsInfoRecord.setOrder_status("其他状态");
			}
			data.add(goodsInfoRecord);
		}
	}

	/**
	 * 删除item 动画
	 * @param index
	 * @param v
	 */
	private void del(final int index, View v){
		final Animation animation = (Animation) AnimationUtils.loadAnimation(v.getContext(), R.anim.list_anim);
		animation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				data.remove(index);
				po = index;
				Log.d(TAG,""+po);
				adapter.notifyDataSetChanged();
				animation.cancel();
			}
		});
		v.startAnimation(animation);
	}

	public void getItem(){
		rsmLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				//获得选中项的对象
				orderCode = ls.get(arg2-1).getOrderCode();
				orderStatus = ls.get(arg2-1).getOrderStatus();
//				订单状态 1:未支付,2取件寄件中,3配送中,4已完成,5已取消
				if (orderStatus==2){
					getOrderInfo(orderCode);
				}else if (orderStatus==1){
					payOrder(1);
				}
			}
		});
	}

	public void payOrder(int sign){
		Intent intent = new Intent(context, AddorderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra("sign",sign);
		intent.putExtra("orderCodeFromRecord",orderCode);
		startActivity(intent);
	}

	public void getOrderInfo(String orderCode){
		Intent intent = new Intent(context, GetorderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra("orderCode",orderCode);
		startActivity(intent);
	}

	@Override
	public void onRefresh() {
		rsmLv.postDelayed(new Runnable() {
			@Override
			public void run() {
				rsmLv.complete();
				Toast.makeText(context,"刷新完成",Toast.LENGTH_SHORT).show();
			}
		},2000);
	}
	@Override
	public void onLoadMore() {
		rsmLv.postDelayed(new Runnable() {
			@Override
			public void run() {
				rsmLv.complete();
				Toast.makeText(context,"刷新完成",Toast.LENGTH_SHORT).show();
			}
		},2000);
	}
	public  int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}



}
