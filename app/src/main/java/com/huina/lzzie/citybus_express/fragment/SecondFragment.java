package com.huina.lzzie.citybus_express.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.fn.FeedbackActivity;
import com.huina.lzzie.citybus_express.fn.LoginActivity;
import com.huina.lzzie.citybus_express.fn.OnlymyaddressActivity;
import com.huina.lzzie.citybus_express.fn.RecordActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.model.User;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
* QujianActivity
* @author lzzie
* created at 2017/2/24 13:37
*/

public class SecondFragment extends Fragment {
	private static final String TAG = "SecondFragment";
	private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

	private Context context;
	private AlertDialog myDialog = null;
	private Fragment secondFragment;
	String toKen;
	String mobile;
	User user = MyApplication.user;
	@BindView(R.id.loginArea)
	LinearLayout loginArea;
	@BindView(R.id.userPic)
	ImageView userPic;
	@BindView(R.id.showLogin)
	TextView logint;
	@BindView(R.id.exit)
	LinearLayout showexit;
	@BindView(R.id.jjjl)
	LinearLayout showjjjl;
	@BindView(R.id.wddz)
	LinearLayout showwddz;
	@BindView(R.id.jcgx)
	LinearLayout showjcgx;
	@BindView(R.id.yjfk)
	LinearLayout showyjfk;
	@BindView(R.id.zs)
	LinearLayout showzs;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_02, container, false);
		ButterKnife.bind(this, view);
		context = getActivity();
		toKen = PreferencesUtils.getString(context, "token");
		mobile = PreferencesUtils.getString(context, "userMobile");
		if (toKen != "") {
			logint.setText(mobile);
			showexit.setVisibility(View.VISIBLE);
			userPic.setImageDrawable(getResources().getDrawable(R.mipmap.user_pic));
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		toKen = PreferencesUtils.getString(context, "token");
		mobile = PreferencesUtils.getString(context, "userMobile");
		if (toKen != "") {
			logint.setText(mobile);
			showexit.setVisibility(View.VISIBLE);
			userPic.setImageDrawable(getResources().getDrawable(R.mipmap.user_pic));
		}

	}

	@OnClick({R.id.loginArea, R.id.exit, R.id.yjfk, R.id.jjjl, R.id.wddz, R.id.jcgx, R.id.zs})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.loginArea:
				if (toKen != "") {
				} else {
					showLogin();
				}
				break;
			case R.id.exit:
				showExitinfo();
				break;
			case R.id.jjjl:
				showMyrecord();
				break;
			case R.id.wddz:
				showMyadress();
				break;
			case R.id.jcgx:
				getVersion();
				break;
			case R.id.yjfk:
				showFeedback();
				break;
			case R.id.zs:
				showTest();
				break;
		}
	}

	public void showLogin() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivity(intent);
	}

	public void showMyrecord() {
		if (toKen == "") {
			ToastUtils.show(getActivity(), "请您先登录");
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
		Intent intent = new Intent(getActivity(), RecordActivity.class);
		startActivity(intent);
	}

	public void showMyadress() {
		if (toKen == "") {
			ToastUtils.show(getActivity(), "请您先登录");
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
		Intent intent = new Intent(getActivity(), OnlymyaddressActivity.class);
		startActivity(intent);
	}

	public void showFeedback() {
		if (toKen == "") {
			ToastUtils.show(getActivity(), "请您先登录");
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}
		Intent intent = new Intent(getActivity(), FeedbackActivity.class);
		startActivity(intent);
	}

	public void showExitinfo() {
		creatAlertDialog();
	}

	public void showTest() {
	}

	private void creatAlertDialog() {
		myDialog = new AlertDialog.Builder(getActivity()).create();
		myDialog.show();
		myDialog.getWindow().setLayout(760, 560);
		myDialog.getWindow().setContentView(R.layout.alert_dialog_layout);
		myDialog.getWindow()
				.findViewById(R.id.alert_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						toKen = "";
						PreferencesUtils.putString(getActivity(), "token", toKen);
						user = null;
						userPic.setImageDrawable(getResources().getDrawable(R.mipmap.my_headpic));
						logint.setText("立即登陆");
						showexit.setVisibility(View.GONE);
						ToastUtils.show(getActivity(), "当前账号已成功退出");
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

	public void getVersion(){
		String phoneType =  "android";
		ArrayMap<String,String> params = new ArrayMap<>();
		params.put("phoneType",phoneType);
		httpHelper.post(Constants.API.GETVERSIONINFO, params, new SpotsCallBack(getActivity()) {
			@Override
			public void onSuccess(Response response, String result) {
				Result result1 = JsonHelper.parseObject(result,Result.class);
				if (result1.getCode()==0){
					UnFindNewVersion();
				}else {
					ToastUtils.show(getActivity(),"获取版本号失败");
				}

			}

			@Override
			public void onError(Response response, int code, Exception e) {

			}
		});
	}

	private void FindNewVersion() {
//		final CheckBox newVesion = (CheckBox) .findViewById();
//		Drawable drawable = this.getResources().getDrawable(R.drawable.checkbox);
//		drawable.setBounds(0,0,40,40);
//		newVesion.setCompoundDrawables(drawable,null,null,null);
//		newVesion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//				}else{
//
//				}
//			}
//		});
		myDialog = new AlertDialog.Builder(getActivity()).create();
		myDialog.show();
		myDialog.getWindow().setLayout(960, 1260);
		myDialog.getWindow().setContentView(R.layout.alert_findvewsion_layout);
		myDialog.getWindow()
				.findViewById(R.id.alert_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		myDialog.getWindow()
				.findViewById(R.id.alert_btn2)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
	}

	private void UnFindNewVersion() {
		myDialog = new AlertDialog.Builder(getActivity()).create();
		myDialog.show();
		myDialog.getWindow().setLayout(760, 560);
		myDialog.getWindow().setContentView(R.layout.alert_unfindvewsion_layout);
		myDialog.getWindow()
				.findViewById(R.id.alert_btn)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
					}
				});
	}


}
