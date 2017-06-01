package com.huina.lzzie.citybus_express.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.model.GoodsInfoRecord;
import com.huina.lzzie.citybus_express.model.UserOrderInfo;

import java.util.List;

/**
 * 寄件记录
* @author lzzie
* created at 2017/3/16 13:40
*/

public class RecordAdapter extends BaseAdapter{
        UserOrderInfo userOrderInfo = new UserOrderInfo();
        private List<UserOrderInfo> ls;
        private Context mycontext;
        private List<GoodsInfoRecord> data;

        public RecordAdapter(Context context, List<GoodsInfoRecord> list) {
            mycontext = context;
            data = list;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holoder;
            if (convertView==null){

                convertView = LayoutInflater.from(mycontext).inflate(R.layout.msg_item,null);
                holoder = new ViewHolder();
                holoder.recordId = (TextView) convertView.findViewById(R.id.record_id);
                holoder.formAddress = (TextView) convertView.findViewById(R.id.form_address);
                holoder.toAddress = (TextView) convertView.findViewById(R.id.to_address);
                holoder.createTime = (TextView) convertView.findViewById(R.id.create_time);
                holoder.orderStatus = (TextView) convertView.findViewById(R.id.order_status);
                convertView.setTag(holoder);

            }
            else {
                holoder = (ViewHolder) convertView.getTag();
            }
            GoodsInfoRecord item = data.get(position);
            holoder.recordId.setText(item.getRecord_id());
            holoder.formAddress.setText(item.getForm_address());
            holoder.toAddress.setText(item.getTo_address());
            holoder.createTime.setText(item.getCreat_time());
            holoder.orderStatus.setText(item.getOrder_status());

//             Log.d("22222222223",""+userOrderInfo.getOrder_status());
//            if (ls.get(position).getOrder_status()==2){
//                holoder.orderStatus.setTextColor(Color.parseColor("#FC3C39"));
//            }

            return convertView;
        }

       public class ViewHolder{
            TextView recordId;
            TextView formAddress;
            TextView toAddress;
            TextView createTime;
            TextView orderStatus;
        }




}
