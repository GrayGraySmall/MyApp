package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myapp.Model.DetailBean;
import com.example.myapp.Model.Result;
import com.example.myapp.R;
import com.example.myapp.URL.JsonRequestWithAuth;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 显示某辆车的详细信息
 * Created by asus on 2016/5/17.
 */
public class DetailCarInfoActivity extends Activity implements View.OnClickListener{
    private TextView text_brand,text_model,text_licence,text_engineNum,text_level,text_distance,text_curgas,text_engineState;
    private TextView text_tranState,text_lightState;
    private ImageButton btn_update;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_car_info);
        InitView();
        DetailBean detailBean = (DetailBean)this.getIntent().getSerializableExtra("detailInfo");
        SetText(detailBean);
        btn_update.setOnClickListener(this);


    }
    public void InitView(){
        text_brand = (TextView) findViewById(R.id.id_detail_brandname);
        text_model = (TextView) findViewById(R.id.id_detail_model);
        text_licence = (TextView) findViewById(R.id.id_detail_licence);
        text_engineNum = (TextView) findViewById(R.id.id_detail_engineNum);
        text_level = (TextView) findViewById(R.id.id_detail_level);
        text_distance = (TextView) findViewById(R.id.id_detail_distance);
        text_curgas = (TextView) findViewById(R.id.id_detail_curGas);
        text_engineState = (TextView) findViewById(R.id.id_detail_engineState);
        text_tranState = (TextView) findViewById(R.id.id_detail_tranState);
        text_lightState = (TextView) findViewById(R.id.id_detail_lightState);
        btn_update = (ImageButton) findViewById(R.id.id_detail_update);

    }
    public void SetText(DetailBean detailBean){
        text_brand.setText(detailBean.getBrandName());
        text_model.setText(detailBean.getModel());
        text_licence.setText(detailBean.getLicenseNum());
        text_engineNum.setText(detailBean.getEngineNum());
        text_level.setText(detailBean.getLevel());
        text_distance.setText(detailBean.getCurdis()+"");


        double curGas = detailBean.getCurGas();
        int gasVolume = detailBean.getGasVolume();
        String percent = curGas / gasVolume + "";
        text_curgas.setText(percent);


        text_engineState.setText(detailBean.getEngineState());
        text_tranState.setText(detailBean.getTranState());
        text_lightState.setText(detailBean.getLightState());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_detail_update:
                scan();
                break;
            default:
                break;
        }
    }

    public void scan(){
        startActivityForResult(new Intent(DetailCarInfoActivity.this,
                CaptureActivity.class),0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String res = bundle.getString("result");
            DetailBean resObj = parseJSONWithJSONObject(res);
            SetText(resObj);
        }
    }



    public DetailBean parseJSONWithJSONObject(String jsonData){
        DetailBean detailBean = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String scan_brand = jsonObject.optString("brandName");
            String scan_model = jsonObject.optString("model");
            String scan_licence = jsonObject.optString("licenseNum");
            String scan_engineNum = jsonObject.optString("engineNum");
            String scan_level = jsonObject.optString("level");
            int scan_distance = jsonObject.optInt("curdis");
            int scan_gasVolume = jsonObject.optInt("gasVolume");
            double scan_curGas = jsonObject.optDouble("curGas");
            String scan_tranState = jsonObject.optString("tranState");
            String scan_engineState = jsonObject.optString("engineState");
            String scan_lightState = jsonObject.optString("lightState");
            String scan_tel = jsonObject.optString("tel");
            String scan_name = jsonObject.optString("name");
            String scan_username = jsonObject.optString("username");
            String scan_brandSign = jsonObject.optString("brandsign");

            detailBean = new DetailBean(scan_name,scan_tel,scan_username,scan_licence,scan_tranState,
                    scan_lightState,scan_curGas,scan_distance,scan_engineNum,scan_engineState,scan_model,scan_level,
                    scan_gasVolume,scan_brand,scan_brandSign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailBean;
    }







    public void UpdateToServer(DetailBean detailBean){
        String  url = "";
        Map<String,String> detailCarInfo = new HashMap<String, String>();
        detailCarInfo.put("name",detailBean.getName());
        detailCarInfo.put("tel",detailBean.getTel());
        detailCarInfo.put("username",detailBean.getUsername());
        detailCarInfo.put("Licence",detailBean.getLicenseNum());
        detailCarInfo.put("tranState",detailBean.getTranState());
        detailCarInfo.put("lightState",detailBean.getLightState());
        detailCarInfo.put("CurGas",detailBean.getCurGas()+"");            //这里是一个坑。DOUBLE
        detailCarInfo.put("Curdis",detailBean.getCurdis()+"");            //INT
        detailCarInfo.put("engineNume",detailBean.getEngineNum());
        detailCarInfo.put("Model",detailBean.getModel());
        detailCarInfo.put("Level",detailBean.getLevel());
        detailCarInfo.put("GasVolume",detailBean.getGasVolume()+"");          //INT
        detailCarInfo.put("BrandName",detailBean.getBrandName());
        detailCarInfo.put("BrandSign",detailBean.getBrandsign());

        //还没有添加到queue

        JsonRequestWithAuth<Result> requset = new JsonRequestWithAuth<Result>(url, Result.class, new Response.Listener<Result>()
        {
            @Override
            public void onResponse(Result result)
            {
                int res = result.getResult();
                Log.e("REGISTER",res+"");

            }
        }, detailCarInfo, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
    }

}

