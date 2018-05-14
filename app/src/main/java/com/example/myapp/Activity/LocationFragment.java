package com.example.myapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.myapp.Model.DetailBean;
import com.example.myapp.Model.GasStation;
import com.example.myapp.Model.SuggestInfoBean;
import com.example.myapp.R;
import com.example.myapp.adapter.SuggestAdapter;
import com.example.myapp.utils.DrivingRouteOverlay;
import com.example.myapp.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2016/4/9.
 */
public class LocationFragment extends Fragment implements View.OnClickListener{
    private View view;
    public static double mLatitude;
    public static double mLongitude;
    private double endLatitude;
    private double endLongitude;
    private LocationClient mLocationClient = null;
    private MyLocationListener mLocationListener = null;
    private boolean isFirst = true;
    private BaiduMap mBaiduMap = null;
    private ImageButton btn_locat = null;
    private MapView mapView = null;
    private String startPlace;  //出发点
    private String endPlace;    //终点
    private ImageButton btn_gas = null;
    private ImageButton btn_exchange = null;
    private ImageButton btn_query = null;
    private EditText text_startPlace = null;
    private EditText text_endPlace = null;
    private RoutePlanSearch routePlanSearch;// 路径规划搜索接口
    private ArrayList<SuggestInfoBean> list = null;
    private static final String AppKey = "57ec4e2da6f7dfeed2ee5b06a3e8ed1b";
    private ArrayList<GasStation> stations;
    //覆盖物相关
    private BitmapDescriptor mMarker;//设置覆盖物的图标
    private RelativeLayout mMarkerLy = null;
    private RequestQueue mQueue = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.tab_location,container,false);
        mQueue = Volley.newRequestQueue(getContext());
        InitView();
        initLocation();
        InitMarker();

        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
        btn_locat.setOnClickListener(this);
        btn_exchange.setOnClickListener(this);
        btn_query.setOnClickListener(this);
        btn_gas.setOnClickListener(this);

        //覆盖物点击的监听事件

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                final GasStation station = (GasStation) extraInfo.getSerializable("station");

                mMarkerLy = (RelativeLayout) view.findViewById(R.id.id_marker_ly);
                TextView name = (TextView) mMarkerLy.findViewById(R.id.id_marker_name);
                TextView address = (TextView) mMarkerLy.findViewById(R.id.id_marker_address);
                TextView distance = (TextView) mMarkerLy.findViewById(R.id.id_marker_distance);
                mMarkerLy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //gasLatLng= new LatLng(station.getLatitude(),station.getLongitude());
                        //Log.e("Gas",station.getLatitude() + " " + station.getLongitude());
                        endLatitude = station.getLatitude();
                        endLongitude = station.getLongitude();
                        startPlace = null;
                        endPlace = null;
                        drivingSearch(startPlace,endPlace);
                    }
                });


                if (station.getName().length() > 16) {
                    String nameText = station.getName().substring(0, 15);
                    nameText += "...";
                    name.setText(nameText);
                } else {
                    name.setText(station.getName());
                }

                if (station.getAddress().length() > 20) {
                    String addressText = station.getAddress().substring(0, 19);
                    addressText += "...";
                    address.setText(addressText);
                } else {
                    address.setText(station.getAddress());
                }

                int temp = station.getDistance();
                int qian = temp / 1000;
                int bai = (temp - qian * 1000) / 100;
                String distanceText = qian + "." + bai;
                distance.setText(distanceText);
                mMarkerLy.setVisibility(View.VISIBLE);
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE); //按下时，商家详情消失
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        return view;
    }

    /**
     * 初始化Marker
     */
    public void InitMarker(){
        mMarkerLy = (RelativeLayout) view.findViewById(R.id.id_marker_ly);
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.pin);
    }

    /**
     * 添加覆盖物
     */
    public void addOverlays(ArrayList<GasStation> stations){
        Log.e("Gas", stations.size() + "hdasjhsadjhj");
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options = null;
        for (GasStation station:stations){
            latLng = new LatLng(station.getLatitude(),station.getLongitude());
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle a = new Bundle();//bundle用于在activity之间传递数据
            a.putSerializable("station",station);
            marker.setExtraInfo(a);
            Log.e("Gas",station.getLatitude()+" ");
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }



    public void InitView(){
        btn_locat = (ImageButton) view.findViewById(R.id.id_btn_myLocation);
        btn_exchange = (ImageButton) view.findViewById(R.id.id_btn_exchange);
        btn_query = (ImageButton) view.findViewById(R.id.id_btn_query);
        btn_gas = (ImageButton) view.findViewById(R.id.id_btn_gasStation);
        text_startPlace = (EditText) view.findViewById(R.id.id_text_startPlace);
        text_endPlace = (EditText) view.findViewById(R.id.id_text_endPlace);

        //这里还没有考虑完整
        text_endPlace.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    startPlace = text_startPlace.getText().toString();
                    endPlace = text_endPlace.getText().toString();

                    if (list == null) {
                        list = new ArrayList<SuggestInfoBean>();//弹出窗口的list
                    }
                    drivingSearch(startPlace,endPlace);
                }
                return false;
            }
        });
        mapView = (MapView) view.findViewById(R.id.id_map_mapView);
        mBaiduMap = mapView.getMap();
        //设置进入地图时候的比例
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

    }

    /**
     * 初始化LoctionClient
     */
    public void initLocation(){
        mLocationClient = new LocationClient(MainActivity.context);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        //对其进行配置
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//设置模式
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);//每间隔1秒进行一次请求
        mLocationClient.setLocOption(option);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }
    @Override
    public void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }



    public void setToMyLocation(){
        LatLng latLng = new LatLng(mLatitude,mLongitude);//实例化，设置经纬度
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_myLocation:
                setToMyLocation();
                break;
            case R.id.id_btn_exchange:
                exchange();
                break;
            case R.id.id_btn_query:
                startPlace = text_startPlace.getText().toString();
                endPlace = text_endPlace.getText().toString();
                list = new ArrayList<SuggestInfoBean>();//弹出窗口的list
                drivingSearch(startPlace,endPlace);
                break;
            case R.id.id_btn_gasStation:
                Log.e("Gas", "按了Gas");
                getGasStationInfo();
                break;
            default:
                break;
        }
    }


    private void exchange() {

        startPlace = text_startPlace.getText().toString();
        endPlace = text_endPlace.getText().toString();

        String temp = startPlace;
        startPlace = endPlace;
        endPlace = temp;
        text_startPlace.setText(startPlace);
        text_endPlace.setText(endPlace);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        //receive成功后的回调
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .latitude(bdLocation.getLatitude())//
                    .longitude(bdLocation.getLongitude())//
                    .build();
            mBaiduMap.setMyLocationData(data);

            //获取最新一次定位的经纬度
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            writeToSP(mLongitude,mLatitude);
            // MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,)
            //第一次定位时候，设置进入当前位置
            if (isFirst){
                LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());//实例化，设置经纬度
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirst = false;
                //Toast.makeText(context,bdLocation.getAddrStr(),Toast.LENGTH_SHORT).show();//显示当前地址
            }
        }
    }

    /**
     * 路线规划结果的回调
     */
    public OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }


        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            mBaiduMap.clear();
            if (drivingRouteResult == null
                    || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR){
                ToastUtils.showShort(getContext(),"未找到查询结果");
            }

            //出现多个搜索结果时候
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR ){
                SuggestAddrInfo suggestAddrInfo = drivingRouteResult.getSuggestAddrInfo();
                List<PoiInfo> poiInfos = suggestAddrInfo.getSuggestEndNode();

                for (PoiInfo info:poiInfos){
                    SuggestInfoBean suggestInfoBean = new SuggestInfoBean();
                    suggestInfoBean.setName(info.name);
                    suggestInfoBean.setAddress(info.address);
                    list.add(suggestInfoBean);
                }

                suggestionDialog(list);

            }


            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR){
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
                drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0));
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            }
        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };



    private void drivingSearch(String mStartPlace,String mEndPlace) {

        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_AVOID_JAM);
        if (startPlace == null || startPlace.equals("")) {
            drivingRoutePlanOption.from(PlanNode.withLocation(new LatLng(mLatitude, mLongitude)));
        } else {
            drivingRoutePlanOption.from(PlanNode.withCityNameAndPlaceName("常州", mStartPlace));// 设置起点
        }

        if (startPlace == null || startPlace.equals("")) {
            drivingRoutePlanOption.to(PlanNode.withLocation(new LatLng(endLatitude, endLongitude)));
        } else {
            drivingRoutePlanOption.to(PlanNode.withCityNameAndPlaceName("常州", mEndPlace));// 设置起点
        }

        routePlanSearch.drivingSearch(drivingRoutePlanOption);// 发起驾车路线规划
    }



    /**
     * 当有多个搜索结果的时候弹出listview
     * @param list 结果的List
     */
    private void suggestionDialog(final ArrayList<SuggestInfoBean> list){
        AlertDialog.Builder builder;
        final AlertDialog alertDialog;

        Context mapContext = getContext();

        LayoutInflater inflater = (LayoutInflater) mapContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.list, null);

        ListView myListView = (ListView) layout.findViewById(R.id.id_list_suggest);

        SuggestAdapter adapter = new SuggestAdapter(mapContext, list);
        myListView.setAdapter(adapter);
        builder = new AlertDialog.Builder(mapContext);
        builder.setView(layout);
        alertDialog = builder.create();
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SuggestInfoBean info = list.get(position);
                String name = info.getName();
                text_endPlace.setText(endPlace);
                endPlace = name;
                drivingSearch(startPlace,endPlace);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
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
                    Log.e("Gas",res);
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
                            GasStation gasStation = new GasStation(name, address, latitude, longtitude, distance);
                            Log.e("Gas", gasStation.toString());
                            stations.add(gasStation);
                            Log.e("Gas", stations.toString());
                        }
                        addOverlays(stations);
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
                Log.e("Gas",volleyError.toString());
                Log.e("Gas","ErrorResponse");
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

    public void writeToSP(double mLongitude,double mLatitude){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString("longitude",mLongitude+"");
        editor.putString("latitude",mLatitude+"");
        editor.commit();
    }


}
