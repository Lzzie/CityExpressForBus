package com.huina.lzzie.citybus_express.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.model.GoodsInfoAddress;

import java.util.List;


/**
 * Created by Lzzie on 2017/3/9.
 */

public class AddressAdapter  extends BaseAdapter {

    private Context myContextr;
    private List<GoodsInfoAddress> data;
    public AddressAdapter(Context context, List<GoodsInfoAddress> list) {
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
        GoodsInfoAddress goodsInfoAddress = data.get(position);
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(myContextr).inflate(R.layout.address_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.addjjname= (TextView) view.findViewById(R.id.add_jjname);
            viewHolder.addjjphone= (TextView) view.findViewById(R.id.add_jjphone);
            viewHolder.addjjdz= (TextView) view.findViewById(R.id.add_jjdz);

            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.addjjname.setText(goodsInfoAddress.getAdd_jj_name()+"");
        viewHolder.addjjphone.setText(goodsInfoAddress.getAdd_jj_phone()+"");
        viewHolder.addjjdz.setText(goodsInfoAddress.getAdd_jj_dz()+"");

        return view;
    }

    class ViewHolder {
        TextView addjjname;
        TextView addjjphone;
        TextView addjjdz;

    }


}
