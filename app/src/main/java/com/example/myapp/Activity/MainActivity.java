package com.example.myapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.example.myapp.Model.DetailBean;
import com.example.myapp.R;
import com.example.myapp.SpeakUtils.IatSettings;
import com.example.myapp.SpeakUtils.JsonParser;
import com.example.myapp.utils.ToastUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private LinearLayout mTabMusicLy;
    private LinearLayout mTabLocationLy;
    private LinearLayout mTabOrderLy;
    private ImageButton mImgBtnMusic;
    private ImageButton mImgBtnLocation;
    private ImageButton mImgBtnOrder;

    private Fragment mTabMusic;
    private Fragment mTabLocation;
    private Fragment mTabOrder;

    private List<DetailBean> list;
    public static Context context = null;
    private RequestQueue mQueue;
    //地图相关



    //侧滑相关控件
    private Toolbar toolbar; //设置拖动
    private FloatingActionButton fab;//我也不知道这个是啥，但是移植过来准没错,zheshi
    private DrawerLayout drawer; //这个是抽屉布局我知道
    private ActionBarDrawerToggle toggle; //这个是啥我也不知道
    private NavigationView navigationView; //这个好像是那个是么视图

    //语音听写相关控件
    // 语音听写对象
    private SpeechRecognizer mIat;
    private SharedPreferences mSharedPreferences;
    private Button startspeakbutton;//开始语音按钮
    private HashMap<String, String> mIatResults = new HashMap<String, String>();//用来存储听写结果
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    private Toast mToast;
    //引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private String username = null;
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        //Init BaiduSDK
        SDKInitializer.initialize(context);
        //初始化Speech
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=574a6ad2");
        mQueue = Volley.newRequestQueue(context);//初始化Volley队列

        InitView();
//        initLocation();
        InitEvent();
        setSelect(0);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_database);
        ab.setDisplayHomeAsUpEnabled(true);

        //初始化推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //设置应用别名（作为用户的唯一标识），改为username（待修改）
        JPushInterface.setAlias(this, "G23225", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.e("TAG", "set alias result is" + s);
            }
        });

    }

    public void InitView(){
        mTabMusicLy = (LinearLayout) findViewById(R.id.id_tab_music);
        mTabLocationLy = (LinearLayout) findViewById(R.id.id_tab_location);
        mTabOrderLy = (LinearLayout) findViewById(R.id.id_tab_order);

        mImgBtnMusic = (ImageButton) findViewById(R.id.id_tab_music_img);
        mImgBtnLocation = (ImageButton) findViewById(R.id.id_tab_location_img);
        mImgBtnOrder = (ImageButton) findViewById(R.id.id_tab_order_img);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //查找id
        toolbar.setLogo(R.drawable.ic_action_database); //设置图标
        setSupportActionBar(toolbar);
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);//抽屉布局
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        startspeakbutton = (Button) findViewById(R.id.nav_speak);

        //获取map相关控件
      //  mapView = (MapView) findViewById(R.id.id_main_mapView);
       // mBaiduMap = mapView.getMap();

    }
    public void InitEvent(){
        mTabMusicLy.setOnClickListener(this);
        mTabLocationLy.setOnClickListener(this);
        mTabOrderLy.setOnClickListener(this);
        //fab.setOnClickListener(this);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        resetImgs();
        switch (v.getId())
        {
            case R.id.id_tab_music:
                setSelect(0);
                break;
            case R.id.id_tab_location:
                setSelect(1);
                break;
            case R.id.id_tab_order:
                setSelect(2);
                break;
            /*case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            default:
                break;
        }

    }

    public void setSelect(int i ){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (i)
        {
            case 0:
                if (mTabMusic == null)
                {
                    mTabMusic = new MusicFragment();
                    transaction.add(R.id.id_content, mTabMusic);
                } else
                {
                    transaction.show(mTabMusic);
                }
                mImgBtnMusic.setImageResource(R.drawable.music_pressed);
                break;
            case 1:
                if (mTabLocation == null)
                {
                    mTabLocation = new LocationFragment();
                    transaction.add(R.id.id_content, mTabLocation);
                } else
                {
                    transaction.show(mTabLocation);
                }
                mImgBtnLocation.setImageResource(R.drawable.location_pressed);
                break;
            case 2:
                if (mTabOrder == null)
                {
                    mTabOrder = new OrderFragment();
                    transaction.add(R.id.id_content, mTabOrder);
                } else
                {
                    transaction.show(mTabOrder);
                }
                mImgBtnOrder.setImageResource(R.drawable.order_pressed);
                break;
            default:
                break;
        }

        transaction.commit();
    }

    public void hideFragment(FragmentTransaction transaction) {
        if (mTabMusic != null){
            transaction.hide(mTabMusic);
        }
        if (mTabLocation != null){
            transaction.hide(mTabLocation);
        }
        if (mTabOrder != null){
            transaction.hide(mTabOrder);
        }
    }

    public void resetImgs(){
        mImgBtnMusic.setImageResource(R.drawable.music_normal);
        mImgBtnLocation.setImageResource(R.drawable.location_normal);
        mImgBtnOrder.setImageResource(R.drawable.order_normal);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧滑菜单的事件监听
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_car) {  //切换车辆
            // Handle the camera action
        } else if (id == R.id.nav_police) {//违章查询
            Intent intent = new Intent(this, WeiZhangActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_speak){//语音
            FlowerCollector.onEvent(this, "button_start");
            //textView.setText("");
            mIatResults.clear();
            //设置参数
            setParam();
            boolean isShowDialog = mSharedPreferences.getBoolean("iat_show", true);
            if (isShowDialog){
                //显示听写对话框
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                ToastUtils.makeShortText("请开始说话", this);
                //showTip("请开始说话......");
            }else{
                // 不显示听写对话框
                ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    ToastUtils.makeShortText("听写失败,错误码：" + ret, this);
                } else {
                    ToastUtils.makeShortText("请开始说话...", this);
                }
            }
        } else if (id == R.id.nav_set) {//设置

        }  else if (id == R.id.nav_quit) {//退出
            ToastUtils.makeShortText("你点击了退出！！！-->", this);
        }else if (id == R.id.nav_driving){//驾驶状态


        }else if (id == R.id.nav_manager_car){//车辆管理
            InitList();
            //将list传到下一个activity中
            //首先得到当前登录的用户的username
            getFromSP();
            //requestCarInfo();
            Intent intent  = new Intent(MainActivity.this,CarinfoActivity.class);
            intent.putExtra("carinfoList", (Serializable) list);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * 语音听写相关---->
     *
     */

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    int ret = 0; // 函数调用返回值

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 从sp中获取当前的uesrname
     */
    public void getFromSP()
    {
        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        username = pref.getString("username","");
    }


    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
 //       mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(MainActivity.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
//        mapView.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(MainActivity.this);
        super.onPause();
//        mapView.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
//        mBaiduMap.setMyLocationEnabled(true);
//        if (!mLocationClient.isStarted())
//            mLocationClient.start();
    }
    @Override
    public void onStop() {
        super.onStop();
        //关闭定位
//        mBaiduMap.setMyLocationEnabled(false);
//        mLocationClient.stop();
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }
        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    private void showTip(final String str) {
        ToastUtils.makeShortText(str, this);
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

       /* textView.setText(resultBuffer.toString());
        textView.setSelection(textView.length());*/

        ToastUtils.makeShortText(resultBuffer.toString(), this);
    }


    /**
     * 向服务器请求车辆信息
     */
    private void requestCarInfo(){
        String url = "http://192.168.191.1:8080/CarANet/servlet/findDetailServlet";
        list = new ArrayList<DetailBean>();
        JsonObjectRequest getCarInfo = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String str = new String(jsonObject.toString().getBytes("GBK"),"utf-8");
                    Log.e("GETINFO",str);
                    JSONArray arr =  jsonObject.getJSONArray("DetailBean");
                    Log.e("GETINFO",arr.length()+"");
                    for (int i = 0; i < arr.length(); i ++){
                        JSONObject temp = arr.getJSONObject(i);
                        String tel = temp.getString("tel");
                        String username = temp.getString("username");
                        String Lincense = temp.getString("licenseNum");
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
                        //实例化detailbean
                        DetailBean detailBean = new DetailBean(name,tel,username,Lincense,tranState,
                                lightState,CurGas,Curdis,engineNum,engineState,Model,Level,
                                GasVolume,BrandName,Brandsign);
                        list.add(detailBean);
                        Log.e("GETINFO",1+"");
                    }

                    //在回调函数中若得到正确的结果跳转至CarinfoActivity中
                    Intent intent  = new Intent(context,CarinfoActivity.class);
                    intent.putExtra("carinfoList", (Serializable) list);
                    startActivity(intent);

                    Log.e("GETINFO",list.size()+"");


                    for (int j = 0; j < list.size(); j ++){
                        DetailBean de = list.get(j);
                        Log.e("GETINFO","brand: "+ de.getBrandName());
                        Log.e("GETINFO", "model: " + de.getModel());
                        Log.e("GETINFO", "license: " + de.getLicenseNum());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("CARINFO",volleyError.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username",username);
                return map;
            }
        };

        mQueue.add(getCarInfo);

    }

    /**
     * 测试的list
     */
    public void InitList(){
        list = new ArrayList<DetailBean>();
        DetailBean detailBean = new DetailBean();
        detailBean.setBrandName("BMW");
        detailBean.setModel("320");
        detailBean.setLicenseNum("浙C.88888");
        list.add(detailBean);

        DetailBean detailBean1 = new DetailBean();
        detailBean1.setBrandName("Benz");
        detailBean1.setModel("G55");
        detailBean1.setLicenseNum("浙C.88888");
        list.add(detailBean1);

        DetailBean detailBean2 = new DetailBean();
        detailBean2.setBrandName("Ford");
        detailBean2.setModel("Mustang");
        detailBean2.setLicenseNum("浙C.88888");
        detailBean2.setEngineNum("KHJDSAKJAHSDL");
        detailBean2.setLevel("双门四座");
        detailBean2.setCurdis(1000);
        detailBean2.setCurGas(30);
        detailBean2.setGasVolume(100);
        detailBean2.setTranState("良好");
        detailBean2.setEngineState("良好");
        detailBean2.setLightState("良好");

        list.add(detailBean2);

        DetailBean detailBean3 = new DetailBean();
        detailBean3.setBrandName("Audi");
        detailBean3.setModel("R8");
        detailBean3.setLicenseNum("浙C.88888");
        list.add(detailBean3);

        DetailBean detailBean4 = new DetailBean();
        detailBean4.setBrandName("BMW");
        detailBean4.setModel("320");
        detailBean4.setLicenseNum("浙C.88888");
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

    /**
     * 初始化LoctionClient
     */
//    public void initLocation(){
//        mLocationClient = new LocationClient(this);
//        mLocationListener = new MyLocationListenerMain();
//        mLocationClient.registerLocationListener(mLocationListener);
//        //对其进行配置
//        LocationClientOption option = new LocationClientOption();
//        option.setCoorType("bd09ll");//设置模式
//        option.setIsNeedAddress(true);
//        option.setOpenGps(true);
//        option.setScanSpan(1000 * 60 * 60);//每间隔1小时进行一次请求
//        mLocationClient.setLocOption(option);
//    }


//    public class MyLocationListenerMain implements BDLocationListener {
//
//        @Override
//        //receive成功后的回调
//        public void onReceiveLocation(BDLocation bdLocation) {
//            MyLocationData data = new MyLocationData.Builder()//
//                    .accuracy(bdLocation.getRadius())//
//                    .latitude(bdLocation.getLatitude())//
//                    .longitude(bdLocation.getLongitude())//
//                    .build();
//            mBaiduMap.setMyLocationData(data);
//
//            //获取最新一次定位的经纬度
//            mLatitude = bdLocation.getLatitude();
//            mLongitude = bdLocation.getLongitude();
//            writeToSP(mLongitude,mLatitude);
//
//        }
//    }

//    public void writeToSP(double mLongitude,double mLatitude){
//        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//        editor.putString("longitude",mLongitude+"");
//        editor.putString("latitude",mLatitude+"");
//        editor.commit();
//    }


}
