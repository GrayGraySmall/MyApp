package com.example.myapp.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.example.myapp.Music_Domain.Mp3Info;
import com.example.myapp.Music_Service.AppConstant;
import com.example.myapp.Music_Utils.MediaUtil;
import com.example.myapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by asus on 2016/4/9.
 */
public class MusicFragment extends Fragment {
    private double mLatitude;
    private double mLongitude;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private MyLocationListenerMain mLocationListener = null;
    private MapView mapView = null;

    //音乐播放器有关变量
    private ListView mMusiclist; // 音乐列表
    private List<Mp3Info> mp3Infos = null;
    private SimpleAdapter mAdapter; // 简单适配器
    private Button previousBtn; // 上一首
    private Button repeatBtn; // 重复（单曲循环、全部循环）
    private Button playBtn; // 播放（播放、暂停）
    private Button shuffleBtn; // 随机播放
    private Button nextBtn; // 下一首
    private TextView musicTitle;// 歌曲标题
    private TextView musicDuration; // 歌曲时间
    private ProgressBar bar; //音乐进度条

    private int repeatState; // 循环标识
    private final int isCurrentRepeat = 1; // 单曲循环
    private final int isAllRepeat = 2; // 全部循环
    private final int isNoneRepeat = 3; // 无重复播放
    private boolean isPlaying = true; // 正在播放
    private boolean isPause; // 暂停
    private boolean isNoneShuffle = true; // 顺序播放
    private boolean isShuffle = false; // 随机播放

    private int listPosition = 0; // 标识列表位置
    private HomeReceiver homeReceiver; // 自定义的广播接收器
    // 一系列动作
    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION"; // 更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION"; // 控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT"; // 当前音乐改变动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION"; // 音乐时长改变动作
    public static final String REPEAT_ACTION = "com.wwj.action.REPEAT_ACTION"; // 音乐重复改变动作
    public static final String SHUFFLE_ACTION = "com.wwj.action.SHUFFLE_ACTION"; // 音乐随机播放动作

    private int currentTime; // 当前时间
    private int duration; // 时长
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_music, container, false);

        previousBtn = (Button) view.findViewById(R.id.previous_music);
        repeatBtn = (Button) view.findViewById(R.id.repeat_music);
        playBtn = (Button) view.findViewById(R.id.play_music);
        shuffleBtn = (Button) view.findViewById(R.id.shuffle_music);
        nextBtn = (Button) view.findViewById(R.id.next_music);
        musicTitle = (TextView) view.findViewById(R.id.music_title);
        musicDuration = (TextView) view.findViewById(R.id.music_duration);
        bar = (ProgressBar) view.findViewById(R.id.progressBar);

        mapView = (MapView) view.findViewById(R.id.id_main_mapView);
        mBaiduMap = mapView.getMap();

        mMusiclist = (ListView) view.findViewById(R.id.music_list);
        mMusiclist.setOnItemClickListener(new MusicListItemClickListener());
        mp3Infos = MediaUtil.getMp3Infos(this.getActivity().getApplicationContext()); // 获取歌曲对象集合
        setListAdpter(MediaUtil.getMusicMaps(mp3Infos)); //显示歌曲列表
        setViewOnclickListener(); // 为一些控件设置监听器
        repeatState = isNoneRepeat; // 初始状态为无重复播放状态

        homeReceiver = new HomeReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        // 注册BroadcastReceiver
        this.getActivity().getApplicationContext().registerReceiver(homeReceiver, filter);

        play();

        initLocation();
        return view;
    }

    /**
     * 给每一个按钮设置监听器
     */
    private void setViewOnclickListener() {
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        previousBtn.setOnClickListener(viewOnClickListener);
        repeatBtn.setOnClickListener(viewOnClickListener);
        playBtn.setOnClickListener(viewOnClickListener);
        shuffleBtn.setOnClickListener(viewOnClickListener);
        nextBtn.setOnClickListener(viewOnClickListener);
    }

    /**
     * 控件的监听器
     */
    private class ViewOnClickListener implements View.OnClickListener {
        Intent intent = new Intent();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.previous_music: // 上一首
                    playBtn.setBackgroundResource(R.drawable.play_selector);
                    isPlaying = true;
                    isPause = false;
                    previous();
                    break;
                case R.id.repeat_music: // 重复播放
                    if (repeatState == isNoneRepeat) {
                        repeat_one();
                        shuffleBtn.setClickable(false);
                        repeatState = isCurrentRepeat;
                    } else if (repeatState == isCurrentRepeat) {
                        repeat_all();
                        shuffleBtn.setClickable(false);
                        repeatState = isAllRepeat;
                    } else if (repeatState == isAllRepeat) {
                        repeat_none();
                        shuffleBtn.setClickable(true);
                        repeatState = isNoneRepeat;
                    }
                    switch (repeatState) {
                        case isCurrentRepeat: // 单曲循环
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_current_selector);
                            Toast.makeText(getActivity().getApplicationContext(),
                                    R.string.repeat_current,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case isAllRepeat: // 全部循环
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_all_selector);
                            Toast.makeText(getActivity().getApplicationContext(),
                                    R.string.repeat_all,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case isNoneRepeat: // 无重复
                            repeatBtn
                                    .setBackgroundResource(R.drawable.repeat_none_selector);
                            Toast.makeText(getActivity().getApplicationContext(),
                                    R.string.repeat_none,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;
                case R.id.play_music: // 播放音乐
                    if (isPlaying) {
                        playBtn.setBackgroundResource(R.drawable.play_selector);
                        intent.setAction("com.wwj.media.MUSIC_SERVICE");
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        intent.setPackage(getActivity().getApplicationContext()
                                .getPackageName());
                        getActivity().getApplicationContext().startService(intent);
                        isPlaying = false;
                        isPause = true;

                    } else if (isPause) {
                        playBtn.setBackgroundResource(R.drawable.pause_selector);
                        intent.setAction("com.wwj.media.MUSIC_SERVICE");
                        intent.putExtra("MSG",
                                AppConstant.PlayerMsg.CONTINUE_MSG);
                        intent.setPackage(getActivity().getApplicationContext()
                                .getPackageName());
                        getActivity().getApplicationContext().startService(intent);
                        isPause = false;
                        isPlaying = true;
                    }
                    break;
                case R.id.shuffle_music: // 随机播放
                    if (isNoneShuffle) {
                        shuffleBtn
                                .setBackgroundResource(R.drawable.shuffle_selector);
                        Toast.makeText(getActivity().getApplicationContext(), R.string.shuffle,
                                Toast.LENGTH_SHORT).show();
                        isNoneShuffle = false;
                        isShuffle = true;
                        shuffleMusic();
                        repeatBtn.setClickable(false);
                    } else if (isShuffle) {
                        shuffleBtn
                                .setBackgroundResource(R.drawable.shuffle_none_selector);
                        Toast.makeText(getActivity().getApplicationContext(), R.string.shuffle_none,
                                Toast.LENGTH_SHORT).show();
                        isShuffle = false;
                        isNoneShuffle = true;
                        repeatBtn.setClickable(true);
                    }
                    break;
                case R.id.next_music: // 下一首
                    playBtn.setBackgroundResource(R.drawable.pause_selector);
                    isPlaying = true;
                    isPause = false;
                    next();
                    break;
            }
        }
    }

    /**
     * 列表点击监听器
     *
     * @author wwj
     */
    private class MusicListItemClickListener implements AdapterView.OnItemClickListener {
        /**
         * 点击列表播放音乐
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            listPosition = position; // 获取列表点击的位置
            playMusic(listPosition); // 播放音乐
        }

    }

    /**
     * 填充列表
     */
    public void setListAdpter(List<HashMap<String, String>> mp3list) {
        mAdapter = new SimpleAdapter(getActivity().getApplicationContext(), mp3list,
                R.layout.music_list_item_layout, new String[]{"title",
                "Artist", "duration"}, new int[]{R.id.music_title,
                R.id.music_Artist, R.id.music_duration});
        mMusiclist.setAdapter(mAdapter);

    }

    /**
     * 下一首歌曲
     */
    public void next() {
        listPosition = listPosition + 1;
        if (listPosition <= mp3Infos.size() - 1) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.NEXT_MSG);
            intent.setPackage(getActivity().getApplicationContext().getPackageName());
            getActivity().getApplicationContext().startService(intent);
        } else {
            listPosition = mp3Infos.size() - 1;
            Toast.makeText(getActivity().getApplicationContext(), "没有下一首了", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 上一首歌曲
     */
    public void previous() {
        listPosition = listPosition - 1;
        if (listPosition >= 0) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle());
            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PRIVIOUS_MSG);
            intent.setPackage(this.getActivity().getApplicationContext().getPackageName());
            this.getActivity().getApplicationContext().startService(intent);
        } else {
            listPosition = 0;
            Toast.makeText(this.getActivity().getApplicationContext(), "没有上一首了", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 播放音乐
     */
    public void play() {
        playBtn.setBackgroundResource(R.drawable.pause_selector);
        Mp3Info mp3Info = mp3Infos.get(listPosition);
        musicTitle.setText(mp3Info.getTitle());
        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("listPosition", 0);
        intent.putExtra("url", mp3Info.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
        intent.setPackage(this.getActivity().getApplicationContext().getPackageName());
        this.getActivity().getApplicationContext().startService(intent);
    }

    /**
     * 单曲循环
     */
    public void repeat_one() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 1);
        this.getActivity().getApplicationContext().sendBroadcast(intent);
    }

    /**
     * 全部循环
     */
    public void repeat_all() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 2);
        this.getActivity().getApplicationContext().sendBroadcast(intent);
    }

    /**
     * 顺序播放
     */
    public void repeat_none() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 3);
        this.getActivity().getApplicationContext().sendBroadcast(intent);
    }

    /**
     * 随机播放
     */
    public void shuffleMusic() {
        Intent intent = new Intent(CTL_ACTION);
        intent.putExtra("control", 4);
        this.getActivity().getApplicationContext().sendBroadcast(intent);
    }

    /**
     * 此方法通过传递列表点击位置来获取mp3Info对象
     *
     * @param listPosition
     */
    public void playMusic(int listPosition) {
        if (mp3Infos != null) {
            Mp3Info mp3Info = mp3Infos.get(listPosition);
            musicTitle.setText(mp3Info.getTitle()); // 这里显示标题
            playBtn.setBackgroundResource(R.drawable.pause_selector);
            isPlaying = true;
            isPause = false;
//            Bitmap bitmap = MediaUtil.getArtwork(this, mp3Info.getId(),
//                    mp3Info.getAlbumId(), true, true);// 获取专辑位图对象，为小图
//            musicAlbum.setImageBitmap(bitmap); // 这里显示专辑图片
            Intent intent = new Intent(); // 定义Intent对象
            // 添加一系列要传递的数据
            intent.putExtra("title", mp3Info.getTitle());
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("artist", mp3Info.getArtist());
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("repeatState", repeatState);
            intent.putExtra("shuffleState", isShuffle);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);

            intent.setPackage(this.getActivity().getApplicationContext().getPackageName());
            this.getActivity().getApplicationContext().startService(intent);

        }
    }

    // 自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT)) {
                // currentTime代表当前播放的时间
                currentTime = intent.getIntExtra("currentTime", -1);
                duration = intent.getIntExtra("duration", -1);
                musicDuration.setText(MediaUtil.formatTime(currentTime));

                int percentage_duration = 100 * currentTime / duration;
                bar.setProgress(percentage_duration);

            } else if (action.equals(MUSIC_DURATION)) {
                duration = intent.getIntExtra("duration", -1);
            } else if (action.equals(UPDATE_ACTION)) {
                // 获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                if (listPosition >= 0) {
                    musicTitle.setText(mp3Infos.get(listPosition).getTitle());
                }
            } else if (action.equals(REPEAT_ACTION)) {
                repeatState = intent.getIntExtra("repeatState", -1);
                switch (repeatState) {
                    case isCurrentRepeat: // 单曲循环
                        repeatBtn
                                .setBackgroundResource(R.drawable.repeat_current_selector);
                        shuffleBtn.setClickable(false);
                        break;
                    case isAllRepeat: // 全部循环
                        repeatBtn
                                .setBackgroundResource(R.drawable.repeat_all_selector);
                        shuffleBtn.setClickable(false);
                        break;
                    case isNoneRepeat: // 无重复
                        repeatBtn
                                .setBackgroundResource(R.drawable.repeat_none_selector);
                        shuffleBtn.setClickable(true);
                        break;
                }
            } else if (action.equals(SHUFFLE_ACTION)) {
                isShuffle = intent.getBooleanExtra("shuffleState", false);
                if (isShuffle) {
                    isNoneShuffle = false;
                    shuffleBtn
                            .setBackgroundResource(R.drawable.shuffle_selector);
                    repeatBtn.setClickable(false);
                } else {
                    isNoneShuffle = true;
                    shuffleBtn
                            .setBackgroundResource(R.drawable.shuffle_none_selector);
                    repeatBtn.setClickable(true);
                }
            }
        }

    }

   /* public void InitView() {
        //获取map相关控件
        mapView = (MapView) view.findViewById(R.id.id_main_mapView);
        mBaiduMap = mapView.getMap();
    }
*/

    /**
     * 初始化LoctionClient
     */
    public void initLocation() {
        mLocationClient = new LocationClient(MainActivity.context);
        mLocationListener = new MyLocationListenerMain();
        mLocationClient.registerLocationListener(mLocationListener);
        //对其进行配置
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//设置模式
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000 * 60 * 60);//每间隔1小时进行一次请求
        mLocationClient.setLocOption(option);
    }


    public class MyLocationListenerMain implements BDLocationListener {

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
            writeToSP(mLongitude, mLatitude);

            Log.e("Music", mLatitude + " " + mLongitude);
        }
    }

    /**
     * 生命周期管理
     */
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

    public void writeToSP(double mLongitude, double mLatitude) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",
                Context.MODE_PRIVATE).edit();
        editor.putString("longitude", mLongitude + "");
        editor.putString("latitude", mLatitude + "");
        editor.commit();
    }

}
