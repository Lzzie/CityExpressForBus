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
 * 添加收件地址
*/
public class TjsjdzActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    String toKen;

    @BindView(R.id.tj_sj_name)
    EditText tjsjname;
    @BindView(R.id.tj_sj_phone)
    EditText tjsjphone;
    @BindView(R.id.tj_sj_sf)
    EditText tjsjsf;
    @BindView(R.id.tj_sj_sq)
    EditText tjsjsq;
    @BindView(R.id.tj_sj_xxdz)
    EditText tjsjxxdz;
    @BindView(R.id.addressList)
    TextView addressList;
    int phoneLen;
    boolean name=false;
    String username, usernumber;
    int mainForm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tjsjdz);
        ButterKnife.bind(this);
        mainForm = getIntent().getIntExtra("mainForm",0);//mainForm 为1 则证明为从“我的地址”跳转来
    }

    @OnClick({R.id.tj_sj_button,R.id.addressList})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.tj_sj_button:
                add2();
                break;
            case R.id.addressList:
                getAddressList();
                break;
        }
    }

    public void add2() {
        int uid = PreferencesUtils.getInt(this, "userId");
        final String muid = "" + uid;
        final String sjname = tjsjname.getText().toString().trim();
        final String sjphone = tjsjphone.getText().toString().trim();
        final String sjsf = tjsjsf.getText().toString().trim();
        final String sjsq = tjsjsq.getText().toString().trim();
        final String sjxxdz = tjsjxxdz.getText().toString().trim();
        final String type = "0"; // 0：收件人 1：发件人
        phoneLen = tjsjphone.getText().toString().length();

        if (TextUtils.isEmpty(sjname) || TextUtils.isEmpty(sjphone)) {
//            Toast.makeText(TjjjdzActivity.this, "收件人与联系方式不能为空", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sjxxdz)) {
            ToastUtils.show(TjsjdzActivity.this, "请填写收件人详细地址");
        } else if (TextUtils.isEmpty(sjsf)){
            ToastUtils.show(TjsjdzActivity.this, "收件人省份不能为空");
        }else if (TextUtils.isEmpty(sjsq)){
            ToastUtils.show(TjsjdzActivity.this, "收件人市区不能为空");
        }else {
            name = true;
        }
        if (phoneLen < 11 || phoneLen > 11) {
            ToastUtils.show(TjsjdzActivity.this, "请填写11位手机号，无特殊符号");
        } else if (name == true) {
            ArrayMap<String, String> params = new ArrayMap<>();
            params.put("uid", muid);
            params.put("name", sjname);
            params.put("mobile", sjphone);
            params.put("address",sjxxdz);
            params.put("province",sjsf);
            params.put("city",sjsq);
            params.put("type", type);
            httpHelper.post(Constants.API.ADDCONSIGNEE, params, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result r = JsonHelper.parseObject(result, Result.class);
                    if (r.getCode() == 0) {
                        Address address = JsonHelper.parseObject(r.getData(), Address.class);
                        MyApplication.address = address;
                        addSuccess2();
                    } else {
                        ToastUtils.show(TjsjdzActivity.this, r.getMsg());
                    }
                }
                @Override
                public void onError(Response response, int code, Exception e) {
                }
            });
        }
    }
    public void addSuccess2(){
        ToastUtils.show(TjsjdzActivity.this, "保存收件人地址成功");
        if (mainForm==1){
            Intent intent = new Intent(TjsjdzActivity.this, OnlymyaddressActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("signFormAddFinish",1);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(TjsjdzActivity.this, MyaddressActivity.class);
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
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                tjsjname.setText(username);
                tjsjphone.setText(usernumber);

            }
        }
    }


}
