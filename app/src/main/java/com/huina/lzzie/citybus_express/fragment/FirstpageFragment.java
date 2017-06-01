package com.huina.lzzie.citybus_express.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.fn.LocationActivity;
import com.huina.lzzie.citybus_express.fn.LoginActivity;
import com.huina.lzzie.citybus_express.fn.QujianActivity;
import com.huina.lzzie.citybus_express.fn.ScheduleActivity;
import com.huina.lzzie.citybus_express.fn.SearchorderActivity;
import com.huina.lzzie.citybus_express.fn.ZijiActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
* QujianActivity
* @author lzzie
* created at 2017/2/24 13:37
*/

public class FirstpageFragment extends Fragment {
	private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

	private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
	private Context context;
	private AlertDialog myDialog = null;
	String toKen;
	@BindView(R.id.searchEt)
	EditText searchEt;
	@BindView(R.id.searchEt_button)
	ImageView searchEtButton;
	@BindView(R.id.qujian)
	LinearLayout qujian;
	@BindView(R.id.ziji)
	LinearLayout ziji;
	@BindView(R.id.chajian)
	LinearLayout chajian;
	@BindView(R.id.lianxi)
	LinearLayout lianxi;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_01, container, false);
		ButterKnife.bind(this, view);
		toKen = PreferencesUtils.getString(getActivity(),"token");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
	}
	@Override
	public void onResume() {
		super.onResume();
		toKen = PreferencesUtils.getString(context, "token");
	}
	@OnClick({R.id.qujian,R.id.searchEt_button,R.id.ziji,R.id.chajian,R.id.lianxi})
	public void onClick(View view){
		switch (view.getId()) {
			case R.id.qujian:
				showQujian();
				break;
			case R.id.searchEt_button:
				getOrderInfo();
				break;
			case R.id.ziji:
				showZiji();
				break;
			case R.id.chajian:
				showSchedule();
				break;
			case R.id.lianxi:
				LxAlertDialog();
				break;
		}
	}

	public void getOrderInfo(){
		final String orderCode = searchEt.getText().toString();
		ArrayMap<String,String> params = new ArrayMap<>();
		params.put("orderCode",orderCode);
		httpHelper.post(Constants.API.GETORDERINFO, params, new SpotsCallBack(context) {
			@Override
			public void onSuccess(Response response, String result) {
				Result result1 = JsonHelper.parseObject(result,Result.class);
				Log.d(TAG,result1.getCode().toString());
				Log.d(TAG,result1.getMsg().toString());
				if (result1.getCode()==0){
					showMySearch(orderCode);
				}else {
					showMySearch(orderCode);
				}
			}
			@Override
			public void onError(Response response, int code, Exception e) {
				ToastUtils.show(context,"查询失败，请检查网络");
			}
		});
	}

	public void showMySearch(String orderCode){
		Intent intent = new Intent(context, SearchorderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra("orderCode",orderCode);
		startActivity(intent);
	}

	public void showQujian(){
		if (toKen=="") {
			Intent intent = new Intent(context, LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(context, QujianActivity.class);
			startActivity(intent);
		}
	}
	public void showZiji(){
		if (toKen==""){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(context, ZijiActivity.class);
			startActivity(intent);
		}
	}

	public void showSchedule(){
		if (toKen==""){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(context, ScheduleActivity.class);
			startActivity(intent);
		}

	}

	public void showLocation(){
		Intent intent = new Intent(context, LocationActivity.class);
		startActivity(intent);
	}

	private void LxAlertDialog() {
		myDialog = new AlertDialog.Builder(getActivity()).create();
		myDialog.show();
		myDialog.getWindow().setLayout(760, 860);
		myDialog.getWindow().setContentView(R.layout.alert_lianxi_layout);
		myDialog.getWindow()
				.findViewById(R.id.alert_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getCall();
						myDialog.dismiss();
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

	public void getCall(){
			// 检查是否获得了权限（Android6.0运行时权限）
			if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
			// 没有获得授权，申请授权
				if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CALL_PHONE)) {
//			// 返回值：
//			//如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//			//如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//			//如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
//			// 弹窗需要解释为何需要该权限，再次请求授权
					Toast.makeText(getActivity(), "请授权！", Toast.LENGTH_LONG).show();
//			// 帮跳转到该应用的设置界面，让用户手动授权
//					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//					Uri uri = Uri.fromParts("package", getPackageName(), null);
//					intent.setData(uri);
//					startActivity(intent);
				}else{
			// 不需要解释为何需要该权限，直接请求授权
					ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
				}
			}else {
			// 已经获得授权，可以打电话
						CallPhone();
			}
	}
	private void CallPhone() {
		Intent intent = new Intent();
		String customerServicePhone = "123456";
		intent.setAction(Intent.ACTION_CALL);
		  //url:统一资源定位符
		   //uri:统一资源标示符（更广）
		intent.setData(Uri.parse("tel:"+customerServicePhone));
		startActivity(intent);
	}



}
