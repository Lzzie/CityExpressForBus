package com.huina.lzzie.citybus_express.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.model.GoodsInfoSjaddress;

import java.util.List;


/**
 * Created by Lzzie on 2017/3/9.
 */

public class SjaddressAdapter extends BaseAdapter {

    private Context myContextr;
    private List<GoodsInfoSjaddress> data;
    public SjaddressAdapter(Context context, List<GoodsInfoSjaddress> list) {
        myContextr = context;
        data=list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        GoodsInfoSjaddress goodsInfoSjaddress = data.get(position);
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(myContextr).inflate(R.layout.sjaddress_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.addsjname = (TextView)view.findViewById(R.id.add_sjname);
            viewHolder.addsjphone = (TextView)view.findViewById(R.id.add_sjphone);
            viewHolder.addsjdz = (TextView)view.findViewById(R.id.add_sjdz);

            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.addsjname.setText(goodsInfoSjaddress.getAdd_sj_name()+"");
        viewHolder.addsjphone.setText(goodsInfoSjaddress.getAdd_sj_phone()+"");
        viewHolder.addsjdz.setText(goodsInfoSjaddress.getAdd_sj_dz()+"");

        return view;
    }

    class ViewHolder {
        TextView addsjname;
        TextView addsjphone;
        TextView addsjdz;

    }


}
