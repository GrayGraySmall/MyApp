package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapp.Activity.OrderDetailActivity;
import com.example.myapp.Model.Order_DetailBean;
import com.example.myapp.R;
import java.util.List;

/**
 * 重写adapter方法
 */
public class OrderSimpleAdapter extends ArrayAdapter<Order_DetailBean> {
    private int resourceId;
    public OrderSimpleAdapter(Context context, int resource, List<Order_DetailBean> object) {
        super(context,resource,object);
        Log.e("adapterSize---------->", object.size()+"");
        resourceId = resource;
    }

    public View getView(int position, final View convertView, ViewGroup parent){
        final Order_DetailBean order = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView licence = (TextView) view.findViewById(R.id.brandId);
        TextView price = (TextView) view.findViewById(R.id.Price);
        TextView PayState = (TextView) view.findViewById(R.id.PayState);
        TextView time = (TextView)view.findViewById(R.id.time);
        Button buninto = (Button)view.findViewById(R.id.firstbtn);
        buninto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent = new Intent(getContext(), OrderDetailActivity.class);
                Bundle bundle = new Bundle();
                //Log.e("order--------->", order.getLicenseNum());
                bundle.putSerializable("order", order);
                itent.putExtras(bundle);
                getContext().startActivity(itent);
            }
        });

        licence.setText(order.getLicenseNum());
        //price.setText(order.getGasQuantity()*order.getGasPrice()+"");
        price.setText("100");
        PayState.setText(order.getPayState());
        time.setText(order.getTime());
        return view;
    }
}
