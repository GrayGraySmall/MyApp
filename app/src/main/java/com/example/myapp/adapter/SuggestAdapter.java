package com.example.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapp.Model.SuggestInfoBean;
import com.example.myapp.R;

import java.util.ArrayList;

/**
 * Created by asus on 2016/5/14.
 */
public class SuggestAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SuggestInfoBean> list;
    private LayoutInflater inflater;

    public SuggestAdapter(Context context, ArrayList<SuggestInfoBean> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SuggestInfoBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.suggest_item, null);

            holder.name = (TextView) convertView.findViewById(R.id.id_suggest_name);
            holder.address = (TextView) convertView.findViewById(R.id.id_suggest_address);

            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getName());
        holder.address.setText(list.get(position).getAddress());
        return convertView;
    }

    protected class Holder{
        TextView name;
        TextView address;
    }
}
