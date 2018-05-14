package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapp.Model.Order_DetailBean;
import com.example.myapp.Model.SimpleOrderBean;
import com.example.myapp.R;

public class OrderDetailActivity extends Activity {

    private Button orderBack;
    private Button createTwoPic;
    private Order_DetailBean detailorder;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Intent intent = this.getIntent();
        detailorder = (Order_DetailBean) intent.getSerializableExtra("order");
        Log.e("simpleorder", detailorder.getLicenseNum());
        orderBack = (Button) findViewById(R.id.id_btn_order_back);
        orderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        createTwoPic = (Button) findViewById(R.id.order_detail_code);
        createTwoPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, TwoPicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detailorder", detailorder);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
