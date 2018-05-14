package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.myapp.Model.GasStation;
import com.example.myapp.Model.OrderBean;
import com.example.myapp.Model.Order_DetailBean;
import com.example.myapp.Model.Result;
import com.example.myapp.R;
import com.example.myapp.URL.JsonRequestWithAuth;
import com.example.myapp.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button button_back_crete;//返回按钮
    Spinner spinne_gas_station;//获取加油站位置的下拉框s
    Spinner spinne_gas_kind;//获取油量价格的下拉框
    EditText editnumber; //输入油的数量，升
    Button createotder;//创建订单按钮

    List<String> gasStation;//加油站位置
    List<String> gasKind;//油品

    ArrayAdapter<String> adapterGasStation;//加油站spinner适配器
    ArrayAdapter<String> adapterGasKind;//油品spinner适配器

    String selectGasKind = new String(); //选中的油品
    String selectGasStation = new String();
    ; //选中的加油站
    String inputnumber = new String();
    ; //输入油量
    double price; //价格

    OrderBean order;//订单

    private RequestQueue mQueue = null;//
    private final int CONNECT_SUCCESS = 1;//常量 成功
    private final int ERROR = 0;//常量 失败
    private ArrayList<GasStation> stations;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取上一个activity传来的StationList
        stations  = (ArrayList<GasStation>) getIntent().getSerializableExtra("station");
        Log.e("Create",stations.size()+" ");

        mQueue = Volley.newRequestQueue(this);//初始化队列
        setContentView(R.layout.activity_create_order);
        //实例化附近加油站
        gasStation = new ArrayList<>();
        //实例化油品
        gasKind = new ArrayList<>();

        //返回按钮
        button_back_crete = (Button) findViewById(R.id.order_button_back);
        //油品spinner
        spinne_gas_kind = (Spinner) findViewById(R.id.gas_kind_spinner);
        //加油站spinner
        spinne_gas_station = (Spinner) findViewById(R.id.gas_station_spinner);
        //数量输入框
        editnumber = (EditText) findViewById(R.id.edittext_number_gas);
        //创建订单按钮
        createotder = (Button) findViewById(R.id.create_order_button);

        //模拟初始化数据
        initList();
        adapterGasStation = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gasStation);
        adapterGasKind = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gasKind);
        //设置样式
        adapterGasStation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterGasKind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinne_gas_station.setAdapter(adapterGasStation);
        spinne_gas_kind.setAdapter(adapterGasKind);
        //添加事件监听
        spinne_gas_kind.setOnItemSelectedListener(this);
        spinne_gas_station.setOnItemSelectedListener(this);
        button_back_crete.setOnClickListener(this);
        editnumber.setOnClickListener(this);
        createotder.setOnClickListener(this);
    }


    public void initList() {
        gasStation.add("万达附近加油站");
        gasStation.add("河海大学附近加油站");
        gasKind.add("93号汽油");
        gasKind.add("95号无铅汽油");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击返回
            case R.id.order_button_back:
                Intent intent = new Intent();
                break;
            //点击输入框
            case R.id.edittext_number_gas:
                editnumber.setText("");
                break;
            //点击创建订单
            case R.id.create_order_button:
                createOrder();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.e("id", id + "");
        switch (parent.getId()) {
            case R.id.gas_kind_spinner:
                selectGasKind = adapterGasKind.getItem(position);
                Log.e("你选择了---->", selectGasKind);
                break;
            case R.id.gas_station_spinner:
                Log.e("你选择了---->", selectGasStation);
                selectGasStation = adapterGasStation.getItem(position);
                break;
            default:

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e("你选择了---->", selectGasStation + "6666666");
    }

    //生成订单
    public void createOrder() {
        inputnumber = editnumber.getText().toString();
        if (checkOut()) {
            //获取当前时间
            Date data = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //实例化订单对象
            order = new OrderBean(selectGasKind, Integer.parseInt(inputnumber), price, (sdf.format(data)).toString(), "G23225", "未支付", selectGasStation);
            ToastUtils.showShort(CreateOrderActivity.this, "创建成功！！");
            toCreate();
        } else {
            ToastUtils.showShort(CreateOrderActivity.this, "请将订单填写完整...");
        }
    }

    //检查订单是否满足要求
    public boolean checkOut() {
        if (selectGasStation.equals("") || selectGasKind.equals("") || inputnumber.equals("")) {
            return false;
        }
        return true;
    }

    //将生成的订单信息上传至服务器
    public void toCreate() {
        String url = "http://192.168.191.1:8080/CarANet/servlet/CreateOrderServlet";
        Map<String, String> info = new HashMap<>();
        info.put("GasQuality", order.getGasQuality());
        info.put("GasQuantity", order.getGasQuantity() + "");
        info.put("GasPrice", order.getGasPrice() + "");
        info.put("time", order.getTime() + "");
        info.put("License", order.getLicense());
        info.put("PayState", order.getPayState());
        info.put("PayStation", order.getPayStation());

        //实例化详细订单
        final Order_DetailBean detailorder = new Order_DetailBean("杨辉", order.getLicense(), order.getTime().toString(), order.getGasQuality(), order.getGasQuantity(), order.getGasPrice(), order.getPayState(), order.getPayStation());
        JsonRequestWithAuth<Result> requset = new JsonRequestWithAuth<Result>(url, Result.class, new Response.Listener<Result>() {
            @Override
            public void onResponse(Result result) {
                int res = result.getResult();
                Log.e("REGISTER", res + "");
                if (res == CONNECT_SUCCESS) {
                    ToastUtils.showShort(CreateOrderActivity.this, "订单添加成功");
                    Intent intent = new Intent(CreateOrderActivity.this, OrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", detailorder);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                if (res == ERROR) {
                    ToastUtils.showShort(CreateOrderActivity.this, "订单添加失败");
                }
            }
        }, info, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(requset);
    }


}
