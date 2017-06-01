package com.huina.lzzie.citybus_express.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.util.SystemStatusManager;

/** 
*
* @author lzzie
* created at 2017/2/25 9:57
*/

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new SystemStatusManager(this).setTranslucentStatus(R.color.white);//设置状态栏透明，参数为你要设置的颜色

	}
	
	public void goback(View v){
		finish();
	}

}

