package com.example.myapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Model.GasStation;
import com.example.myapp.Model.Order_DetailBean;
import com.example.myapp.R;
import com.example.myapp.adapter.OrderSimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2016/4/9.
 */
public class OrderFragment extends Fragment {
    PopupMenu popup = null;
    ImageButton add_more_order = null;
    Button more_button;
    ListView listview;
    private RequestQueue mQueue;
    private double mLatitude;
    private double mLongitude;
    private ArrayList<GasStation> stations;
    private static final String AppKey = "57ec4e2da6f7dfeed2ee5b06a3e8ed1b";

    //ArrayList<Order_DetailBean> detailorderlist = new ArrayList<Order_DetailBean>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_order, container, false);
        mQueue = Volley.newRequestQueue(getActivity());
        getFromSP();

        Log.e("第一个执行的：------>","不开心");
        //requestOrderInfo();
        add_more_order = (ImageButton)rootView.findViewById(R.id.id_btn_scan);
        listview = (ListView) rootView.findViewById(R.id.order_lists);
        more_button = (Button) rootView.findViewById(R.id.order_more_button);

        fill_order();
        //添加订单的监听
        add_more_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onPopupButtion(v);

                getGasStationInfo();

//                Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
//                startActivity(intent);
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("wo??", id + position + "");
            }
        });
        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOrderInfo();
            }
        });


        Log.e("Main", mLatitude + " " + mLongitude);



        return rootView;
    }

    /**
     * 从sp中获取当前的位置
     */
    public void getFromSP()
    {
        SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mLatitude = Double.parseDouble(pref.getString("latitude",""));
        mLongitude = Double.parseDouble(pref.getString("longitude", ""));

    }



    //向listview内部填充内容
    public void fill_order(){
        ArrayList<Order_DetailBean> detailorderlist = this.requestOrderInfo();//从服务器获取数据
        Log.e("GETINFO","数组是否为空: "+ detailorderlist.size()+"-----");//查看获取的数组是否为空


        //listview初始化
        OrderSimpleAdapter adapter = new OrderSimpleAdapter(OrderFragment.this.getActivity(), R.layout.order_list, detailorderlist);
        listview.setAdapter(adapter);
        //Log.e("GETINFO","LincenseNum: "+ detailorderlist.get(0).getLicenseNum());
    }

    private ArrayList<Order_DetailBean> requestOrderInfo(){
        //detailorderlist.clear();
        final ArrayList<Order_DetailBean> detailorderlist = new ArrayList<Order_DetailBean>();
        String url = "http://192.168.191.1:8080/CarANet/servlet/OrderDetaiPayStateServlet";
        JsonObjectRequest getOrderInfo = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String str = new String(jsonObject.toString().getBytes("iso-8859-1"),"utf-8");
                    Log.e("GETINFO",str);
                    JSONArray arr =  jsonObject.getJSONArray("Order_DetailBean");
                    //JSONArray arr = (JSONArray) str;
                    for (int i = 0;i < arr.length();i++){
                        JSONObject temp = arr.getJSONObject(i);
                        String strs = new String(temp.toString().getBytes("iso-8859-1"), "utf-8");
                        //Log.e("Jsonarr", strs);
                        Order_DetailBean orderdetail;
                        int gasPrice = temp.getInt("gasPrice");
                        String gasQuanlity = new String(temp.getString("gasQuality").toString().getBytes("iso-8859-1"),"utf-8");
                        String name  =new String(temp.getString("name").toString().getBytes("iso-8859-1"), "utf-8");
                        String licenseNum = new String(temp.getString("licenseNum").toString().getBytes("iso-8859-1"), "utf-8");
                        String time = new String(temp.getString("time").toString().getBytes("iso-8859-1"), "utf-8");
                        int gasQuantity = temp.getInt("gasQuantity");
                        String payState = new String(temp.getString("payState").toString().getBytes("iso-8859-1"), "utf-8");
                        String gasStation = new String(temp.getString("gasStation").toString().getBytes("iso-8859-1"), "utf-8");
                        orderdetail = new Order_DetailBean(name,licenseNum,time,gasQuanlity,gasQuantity,gasPrice, payState, gasStation);
                        detailorderlist.add(orderdetail);
                    }

                    Log.e("GETINFO",detailorderlist.size()+"");
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
        mQueue.add(getOrderInfo);
        return detailorderlist;
    }

    //定义下拉菜单（查看更多和扫一扫）
    public void onPopupButtion(View button){
        Log.e("你没有点击按钮：" , "？？？？？");
        popup = new PopupMenu(this.getActivity(), button);
        popup.getMenuInflater().inflate(R.menu.order_more_scan, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("你点击了按钮：" , "？？？？？？？？？");
                switch (item.getItemId()){
                    case R.id.button2:
                        Log.e("你点击了扫一扫：" , "？？？？？？？？？");
                        break;
                    case R.id.button3:
                        Log.e("你点击了查看更多：" , "？？？？？？？？");
                        break;
                    default:
                        Log.e("？？" , "？？？？？");
                }
                return true;
            }
        });
        popup.show();
    }






    /**
     * 请求加油站数据
     */
    public void getGasStationInfo() {
        stations = new ArrayList<>();
        String url = "http://apis.juhe.cn/oil/local";//请求接口地址
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                try {
                    JSONObject obj = new JSONObject(res);
                    Log.e("Gas", res);
                    if (obj.getInt("error_code") == 0) {
                        JSONObject object = new JSONObject(obj.get("result").toString());
                        JSONArray arr = object.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject temp = arr.getJSONObject(i);
                            String name = temp.getString("name");
                            String address = temp.getString("address");
                            double longtitude = temp.getDouble("lon");
                            double latitude = temp.getDouble("lat");
                            int distance = temp.getInt("distance");
                            String t = temp.getString("gastprice");
                            Log.e("order",t);

                            double price1 = 0;
                            double price2 = 0;
                            double price3 = 0;
                            JSONObject price_object = new JSONObject(t);
                            if (t.contains("92")) {
                                price1 = price_object.getDouble("92#");
                            }
                            if (t.contains("95")) {
                                price2 = price_object.getDouble("95#");
                            }
                            if (t.contains("0#")) {
                                price3 = price_object.getDouble("0#车柴");
                            }
                            Map<String, Double> map = new HashMap<String,Double>();
                            if (price1 != 0) {
                                map.put("92#", price1);
                            }
                            if (price2 != 0) {
                                map.put("95#", price2);
                            }
                            if (price3 != 0) {
                                map.put("0#车柴", price3);
                            }
                            Log.e("order",map.toString());
                            GasStation gasStation = new GasStation(name, address, latitude, longtitude, distance,map);
                            stations.add(gasStation);
                            Log.e("Main", stations.toString());
                            //跳转至创建订单界面
                            Intent intent  = new Intent(getContext(),CreateOrderActivity.class);
                            intent.putExtra("station", (Serializable) stations);
                            startActivity(intent);

                        }
                    } else {
                        Log.e("Gas", obj.get("error_code") + ":" + obj.get("reason"));
                    }

                    Log.e("Gas", stations.size() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Gas", volleyError.toString());
                Log.e("Gas", "ErrorResponse");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("lon", mLongitude + "");
                map.put("lat", mLatitude + "");
                map.put("r", "3000");  //设定距离为3000
                map.put("page", "1");
                map.put("format", "1");
                map.put("key", AppKey);
                return map;
            }
        };
        mQueue.add(stringRequest);
    }


}
