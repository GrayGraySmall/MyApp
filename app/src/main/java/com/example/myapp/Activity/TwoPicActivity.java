package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapp.Model.Order_DetailBean;
import com.example.myapp.R;
import com.google.gson.Gson;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TwoPicActivity extends Activity {

    private Button twopicback;
    private ImageView img;
    private Order_DetailBean orderdetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_pic);
        Intent intent = getIntent();
        orderdetail = (Order_DetailBean) intent.getSerializableExtra("detailorder");
        String orderstr = new Gson().toJson(orderdetail);

        Log.e("Json+str", orderstr);
        img = (ImageView) findViewById(R.id.pic_scan_order);
        makeView(orderstr);
    }

    public void makeView(String jsonstr){
        if(jsonstr.equals("")){
            Toast.makeText(TwoPicActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
        }else{
            Bitmap bitmap = EncodingUtils.createQRCode(jsonstr, 500, 500, null);
            img.setImageBitmap(bitmap);
        }
    }
}
