package com.example.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapp.Model.SimpleCarBean;
import com.example.myapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/5/1.
 */
public class SimpleAdapter extends ArrayAdapter<SimpleCarBean> {

    private int resourceId;

    public SimpleAdapter(Context context, int resource, List<SimpleCarBean> object) {
        super(context,resource,object);
        resourceId = resource;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        SimpleCarBean car = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView brand = (TextView) view.findViewById(R.id.id_text_user_brand);
        TextView model = (TextView) view.findViewById(R.id.id_text_user_model);
        TextView licence = (TextView) view.findViewById(R.id.id_text_user_license);
        brand.setText(car.getBrand());
        model.setText(car.getModel());
        licence.setText(car.getLicence());
        return view;
    }

}
