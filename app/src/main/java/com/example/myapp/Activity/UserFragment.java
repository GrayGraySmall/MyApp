package com.example.myapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Model.DetailBean;
import com.example.myapp.Model.SimpleCarBean;
import com.example.myapp.R;
import com.example.myapp.URL.JsonRequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/4/9.
 */
public class UserFragment extends Fragment implements View.OnClickListener{
    private View view;
    private LinearLayout userInfoLy;
    private LinearLayout carControlLy;
    private LinearLayout rulesLy;
    private LinearLayout settingLy;
    private LinearLayout exitingLy;
    private RequestQueue mQueue;
    private List<DetailBean> list;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tab_user,container,false);
        InitView();

        context = getActivity().getApplicationContext();
        userInfoLy.setOnClickListener(this);
        carControlLy.setOnClickListener(this);
        rulesLy.setOnClickListener(this);
        settingLy.setOnClickListener(this);
        exitingLy.setOnClickListener(this);
        list = new ArrayList<DetailBean>();

        mQueue = Volley.newRequestQueue(getContext());
        requestCarInfo();
        return view;
    }


    public void InitView(){
        userInfoLy = (LinearLayout) view.findViewById(R.id.id_ly_user_info);
        carControlLy = (LinearLayout) view.findViewById(R.id.id_ly_carControl);
        rulesLy = (LinearLayout) view.findViewById(R.id.id_ly_rules);
        settingLy = (LinearLayout) view.findViewById(R.id.id_ly_setting);
        exitingLy = (LinearLayout) view.findViewById(R.id.id_ly_quit);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_ly_user_info:
                break;
            case R.id.id_ly_carControl:


                //InitList();
                Log.e("GETINFO","size:"+list.size()+"");
                for (int i = 0; i <list.size();i++){
                    DetailBean de = list.get(i);
                    Log.e("GETINFO","brand: "+ de.getBrandName());
                    Log.e("GETINFO", "model: " + de.getModel());
                    Log.e("GETINFO", "license: " + de.getLicense());
                }
                Intent intent  = new Intent(context,CarinfoActivity.class);

                intent.putExtra("carinfoList", (Serializable) list);

                startActivity(intent);
                break;

            case R.id.id_ly_rules:

                break;
            case R.id.id_ly_setting:
                break;
            case R.id.id_ly_quit:
                break;
        }
    }



    private void requestCarInfo(){
        String url = "http://192.168.191.1:8080/CarANet/servlet/findDetailServlet";
        JsonObjectRequest getCarInfo = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String str = new String(jsonObject.toString().getBytes("iso-8859-1"),"utf-8");
                    Log.e("GETINFO",str);
                    JSONArray arr =  jsonObject.getJSONArray("DetailBean");
                    for (int i = 0;i < arr.length();i++){

                        JSONObject temp = arr.getJSONObject(i);

                        DetailBean detailBean = new DetailBean();

                        String tel = temp.getString("tel");
                        String username = temp.getString("username");
                        String Lincense = temp.getString("license");
                        String tranState = temp.getString("tranState");
                        String lightState = temp.getString("lightState");
                        int Curdis = temp.getInt("curdis");
                        double CurGas = temp.getDouble("curGas");
                        String engineNum = temp.getString("engineNum");
                        String engineState = temp.getString("engineState");
                        String Model = temp.getString("model");
                        String Level = temp.getString("level");
                        int GasVolume = temp.getInt("gasVolume");
                        String BrandName = temp.getString("brandName");
                        String Brandsign = temp.getString("brandsign");
                        String name = temp.getString("name");

                        detailBean.setTel(tel);
                        detailBean.setUsername(username);
                        detailBean.setLicense(Lincense);
                        detailBean.setTranState(tranState);
                        detailBean.setLightState(lightState);
                        detailBean.setCurGas(CurGas);
                        detailBean.setCurdis(Curdis);
                        detailBean.setEngineNum(engineNum);
                        detailBean.setEngineState(engineState);
                        detailBean.setModel(Model);
                        detailBean.setLevel(Level);
                        detailBean.setGasVolume(GasVolume);
                        detailBean.setBrandName(BrandName);
                        detailBean.setBrandsign(Brandsign);
                        detailBean.setName(name);
                        list.add(detailBean);
                    }

                    //Log.e("GETINFO",list.size()+"");
                    /*for (int i = 0; i <list.size();i++){
                        DetailBean de = list.get(i);
                        Log.e("GETINFO","brand: "+ de.getBrandName());
                        Log.e("GETINFO", "model: " + de.getModel());
                        Log.e("GETINFO", "license: " + de.getLicense());
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("CARINFO",volleyError.toString());

            }
        });
        mQueue.add(getCarInfo);
    }


    public void InitList(){
        DetailBean detailBean = new DetailBean();
        detailBean.setBrandName("BMW");
        detailBean.setModel("320");
        detailBean.setLicense("浙C.88888");
        list.add(detailBean);

        DetailBean detailBean1 = new DetailBean();
        detailBean1.setBrandName("Benz");
        detailBean1.setModel("G55");
        detailBean1.setLicense("浙C.88888");
        list.add(detailBean1);

        DetailBean detailBean2 = new DetailBean();
        detailBean2.setBrandName("Ford");
        detailBean2.setModel("Mustang");
        detailBean2.setLicense("浙C.88888");
        list.add(detailBean2);

        DetailBean detailBean3 = new DetailBean();
        detailBean3.setBrandName("Audi");
        detailBean3.setModel("R8");
        detailBean3.setLicense("浙C.88888");
        list.add(detailBean3);

        DetailBean detailBean4 = new DetailBean();
        detailBean4.setBrandName("BMW");
        detailBean4.setModel("320");
        detailBean4.setLicense("浙C.88888");
        list.add(detailBean4);

       /* //测试输出结果
        Log.e("GETINFO", list.size() + "");
        for (int i = 0; i <list.size();i++) {
            DetailBean de = list.get(i);
            Log.e("GETINFO", "brand: " + de.getBrandName());
            Log.e("GETINFO", "model: " + de.getModel());
            Log.e("GETINFO", "license: " + de.getLicense());
        }*/


    }
}
