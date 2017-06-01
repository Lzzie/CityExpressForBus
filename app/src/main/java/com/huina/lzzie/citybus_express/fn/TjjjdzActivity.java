package com.huina.lzzie.citybus_express.fn;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.MyApplication;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Address;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.PreferencesUtils;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
*
* @author lzzie
* created at 2017/3/10 15:25
 * 添加寄件地址
*/
public class TjjjdzActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();

    @BindView(R.id.tj_jj_name)
    EditText tjjjname;
    @BindView(R.id.tj_jj_phone)
    EditText tjjjphone;
    @BindView(R.id.tj_jj_sf)
    EditText tjjjsf;
    @BindView(R.id.tj_jj_sq)
    EditText tjjjsq;
    @BindView(R.id.tj_jj_xxdz)
    EditText tjjjxxdz;
    @BindView(R.id.addressList)
    TextView addressList;
    int phoneLen;
    boolean name = false;
    String username, usernumber;
    int mainForm ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tjjjdz);
        ButterKnife.bind(this);
        mainForm=getIntent().getIntExtra("mainForm",0);//mainForm 为1 则证明为从“我的地址”跳转来
    }

    @OnClick({R.id.tj_jj_button, R.id.addressList})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tj_jj_button:
                add();
                break;
            case R.id.addressList:
                getAddressList();
                break;
        }
    }

    public void add() {
        int uid = PreferencesUtils.getInt(this, "userId");
        final String muid = "" + uid;
        final String jjname = tjjjname.getText().toString().trim();
        final String jjphone = tjjjphone.getText().toString().trim();
        final String jjsf = tjjjsf.getText().toString().trim();
        final String jjsq = tjjjsq.getText().toString().trim();
        final String jjxxdz = tjjjxxdz.getText().toString().trim();
        final String type = "1"; // 0：收件人 1：发件人
        phoneLen = tjjjphone.getText().toString().length();

        if (TextUtils.isEmpty(jjname) || TextUtils.isEmpty(jjphone)) {
//            Toast.makeText(TjjjdzActivity.this, "收件人与联系方式不能为空", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(jjxxdz)) {
            ToastUtils.show(TjjjdzActivity.this, "请填写寄件人详细地址");
        } else if (TextUtils.isEmpty(jjsf)){
            ToastUtils.show(TjjjdzActivity.this, "寄件人省份不能为空");
        }else if (TextUtils.isEmpty(jjsq)){
            ToastUtils.show(TjjjdzActivity.this, "寄件人市区不能为空");
        }else {
            name = true;
        }

        if (phoneLen < 11 || phoneLen > 11) {
            ToastUtils.show(TjjjdzActivity.this, "请填写11位手机号,无特殊符号");
        } else if (name == true) {
            ArrayMap<String, String> params = new ArrayMap<>();
            params.put("uid", muid);
            params.put("name", jjname);
            params.put("mobile", jjphone);
            params.put("address",jjxxdz);
            params.put("province",jjsf);
            params.put("city",jjsq);
            params.put("type", type);
            httpHelper.post(Constants.API.ADDCONSIGNEE, params, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result r = JsonHelper.parseObject(result, Result.class);
                    if (r.getCode() == 0) {
                        Address address = JsonHelper.parseObject(r.getData(), Address.class);
                        MyApplication.address = address;
                        addSuccess();
                    } else {
                        ToastUtils.show(TjjjdzActivity.this, r.getMsg());
                    }
                }

                @Override
                public void onError(Response response, int code, Exception e) {
                }
            });
        } else {
        }

    }

    public void addSuccess() {
        ToastUtils.show(TjjjdzActivity.this, "保存寄件人地址成功");
        if (mainForm==1){
            Intent intent = new Intent(TjjjdzActivity.this, OnlymyaddressActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("signFormAddFinish",1);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(TjjjdzActivity.this, MyaddressActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("signFormAddFinish",1);
            startActivity(intent);
            finish();
        }

    }

    public void getAddressList() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        } else {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();

                tjjjname.setText(username);
                tjjjphone.setText(usernumber.trim());

            }
        }
    }


}