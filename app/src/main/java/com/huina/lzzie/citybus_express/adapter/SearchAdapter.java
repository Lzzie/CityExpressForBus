package com.huina.lzzie.citybus_express.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huina.lzzie.citybus_express.R;
import com.huina.lzzie.citybus_express.model.GoodsInfoSearch;

import java.util.List;


/**
 * Created by Lzzie on 2017/3/9.
 */

public class SearchAdapter extends BaseAdapter {

    private Context myContextr;
    private List<GoodsInfoSearch> data;
    public SearchAdapter(Context context, List<GoodsInfoSearch> list) {
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
        GoodsInfoSearch goodsInfoSearch = data.get(position);
        ViewHolder viewHolder;
        if (view==null){
            view = LayoutInflater.from(myContextr).inflate(R.layout.search_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.searchId= (TextView) view.findViewById(R.id.searchId);
            viewHolder.formAddress = (TextView) view.findViewById(R.id.formAddress);
            viewHolder.toAddress = (TextView) view.findViewById(R.id.toAddress);
            viewHolder.createTime = (TextView) view.findViewById(R.id.createTime);

            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.searchId.setText(goodsInfoSearch.getSearch_id()+"");
        viewHolder.formAddress.setText(goodsInfoSearch.getForm_address()+"");
        viewHolder.toAddress.setText(goodsInfoSearch.getTo_address()+"");
        viewHolder.createTime.setText(goodsInfoSearch.getCreat_time()+"");

        return view;
    }

    class ViewHolder {
        TextView searchId;
        TextView formAddress;
        TextView toAddress;
        TextView createTime;
    }


}
