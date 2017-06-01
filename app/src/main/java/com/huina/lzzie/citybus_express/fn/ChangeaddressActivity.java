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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.Constants;
import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.base.BaseActivity;
import com.huina.lzzie.citybus_express.http.OkHttpHelper;
import com.huina.lzzie.citybus_express.http.SpotsCallBack;
import com.huina.lzzie.citybus_express.model.Result;
import com.huina.lzzie.citybus_express.util.JsonHelper;
import com.huina.lzzie.citybus_express.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class ChangeaddressActivity extends BaseActivity {
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private static final String TAG ="ChangeaddressActivity" ;

    @BindView(R.id.jj_name)
    EditText jjrname;
    @BindView(R.id.jj_phone)
    EditText jjrphone;
    @BindView(R.id.jj_sf)
    EditText jjrsf;
    @BindView(R.id.jj_sq)
    EditText jjrsq;
    @BindView(R.id.jj_xxdz)
    EditText jjrxxdz;
    @BindView(R.id.addressList)
    TextView addressList;
    @BindView(R.id.change_button)
    Button changeButton;
    int phoneLen;
    boolean name = false;
    String username,usernumber;
    String sendId;
    int type; // 0：收件人 1：发件人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeaddress);
        ButterKnife.bind(this);

        sendId = getIntent().getStringExtra("sendId");
        type = getIntent().getIntExtra("sendType",2);

    }

    @OnClick({R.id.change_button, R.id.addressList})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_button:
                change();
                break;
            case R.id.addressList:
                getAddressList();
                break;
        }
    }

    public void change(){
        final String id = sendId;
        final String jjname = jjrname.getText().toString().trim();
        final String jjphone = jjrphone.getText().toString().trim();
        final String jjsf = jjrsf.getText().toString().trim();
        final String jjsq = jjrsq.getText().toString().trim();
        final String jjxxdz = jjrxxdz.getText().toString().trim();
        phoneLen = jjrphone.getText().toString().length();

        if (TextUtils.isEmpty(jjname) || TextUtils.isEmpty(jjphone)) {
        } else if (TextUtils.isEmpty(jjxxdz)) {
            ToastUtils.show(ChangeaddressActivity.this, "请填写寄件人详细地址");
        } else {
            name = true;
        }
        if (phoneLen < 11 || phoneLen > 11) {
            ToastUtils.show(ChangeaddressActivity.this, "请填写11位手机号,无特殊符号");
        } else if (name == true) {
            ArrayMap<String, String> params = new ArrayMap<>();
            params.put("id",""+id);
            params.put("name", jjname);
            params.put("mobile", jjphone);
            params.put("address",jjxxdz);
            params.put("type",""+type);
            httpHelper.post(Constants.API.UPDATECONSIGNEE, params, new SpotsCallBack(this) {
                @Override
                public void onSuccess(Response response, String result) {
                    Result result1 = JsonHelper.parseObject(result, Result.class);
                    Log.d(TAG,result1.getMsg());
                    if (result1.getCode() == 0) {
                        addSuccess();
                    } else {
                        ToastUtils.show(ChangeaddressActivity.this, result1.getMsg());
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
        ToastUtils.show(ChangeaddressActivity.this, "修改地址成功");
        Intent intent = new Intent(ChangeaddressActivity.this, OnlymyaddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
        finish();
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

                jjrname.setText(username);
                jjrphone.setText(usernumber);

            }
        }
    }

}
