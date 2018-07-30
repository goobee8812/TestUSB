package com.skylander.jud.testusb;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.skylander.jud.testusb.banner.BannerModel;
import com.skylander.jud.testusb.banner.BannerViewAdapter;
import com.skylander.jud.testusb.banner.NoPreloadViewPager;
import com.skylander.jud.testusb.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    private final String TAG = "MainActivity";
    public static NoPreloadViewPager mViewPager;
    private ImageView backImg;
//    private ArrayList<BannerModel> as;
    private Intent intent;

    private Timer timer;
    private TimerTask timerTask;
    private static ArrayList<BannerModel> playList;
    private BannerViewAdapter mAdapter;
    public static int autoCurrIndex = 0;//设置当前 第几个图片 被选中
    private long period = 5000;//轮播图展示时长,默认5秒

    public static final int UPDATE_VIEWPAGER = 0; //更新节目轮播界面
    public static final int FINISH_VIDEO = 1; //播完视频节目
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //定时轮播图片，需要在主线程里面修改 UI
                case UPDATE_VIEWPAGER:
                    if (msg.arg1 != 0) {
                        mViewPager.setCurrentItem(msg.arg1,true);
                    } else {
                        mViewPager.setCurrentItem(msg.arg1);
                    }
                    break;
                case FINISH_VIDEO:
                    changePlay();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏系统状态栏
        final Window currentWindow = this.getWindow();
        currentWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏虚拟导航键
        currentWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        currentWindow.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                currentWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
        //设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //注册成为订阅者
        EventBus.getDefault().register(this);

        mViewPager = findViewById(R.id.id_view_pager);
        backImg = findViewById(R.id.id_image_view);
        changPlayView(0);
        intent = getIntent();
        playList = new ArrayList<>();
        if (playList.size() != 0){
            playList.clear();
            Logger.d("TestPlay","清空！");
        }
        playList = (ArrayList<BannerModel>) intent.getSerializableExtra("ff");
        if (playList != null){
            changPlayView(1);
            Logger.d("TestPlay","--:" + playList.size());
            for (int i=0;i<playList.size();i++){
                Logger.d("TestPlay",playList.get(i).getUri() + " -- " + playList.get(i).getType());
            }
        }
        initView();
    }

    /**
     * 改变是否播放
     * @param i
     */
    private void changPlayView(int i){
        switch (i){
            case 0: //关闭播放
                if (mViewPager != null){
                    mViewPager.setVisibility(View.GONE);
                }
                if (backImg != null){
                    backImg.setVisibility(View.VISIBLE);
                }
                break;
            case 1: //打开播放
                if (mViewPager != null){
                    mViewPager.setVisibility(View.VISIBLE);
                }
                if (backImg != null){
                    backImg.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }
    private void autoBanner() {
//        int index = playList.size()-1;
        autoCurrIndex = 0;
        final int index = 0;
        mViewPager.setOffscreenPageLimit(index);
        mAdapter = new BannerViewAdapter(this, playList);
        mViewPager.setAdapter(mAdapter);
        //获取第一个播放的时间
        if (playList.get(index).getType().equals("0")){
            //播放图片
            period = 15000;
            createTimerTask();//创建定时器
            timer = new Timer();
            timer.schedule(timerTask, period, period);
        }else if (playList.get(index).getType().equals("1")){
            //播放视频
            Logger.d("TestPlay","播放视频！");
        }

        mViewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Logger.d("Test", "onPageSelected: " + position);
                autoCurrIndex = position;
                //动态设定轮播图每一页的停留时间
                //获取第一个播放的时间
                if (playList.get(position).getType().equals("0")){
                    //播放图片
                    period = 15000;
                    if (timer != null) {//每次改变都需要重新创建定时器
                        timer.cancel();
                        timer = null;
                        timer = new Timer();
                    }else {
                        timer = new Timer();
                    }
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                        createTimerTask();
                    }else {
                        createTimerTask();
                    }
                    timer.schedule(timerTask, period, period);
                }else if (playList.get(position).getType().equals("1")){
                    //播放视频
                    if (timerTask != null) {
                        timerTask.cancel();
                        timerTask = null;
                    }
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void createTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Logger.d("TestPlay","createTimerTask changePlay");
                changePlay();
            }
        };
    }
//    private void initData(){
//        String base_url = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/";
//        BannerModel bannerModel = new BannerModel(base_url + "5acqh6tjcumx.mp4","1");
//        BannerModel bannerModel2 = new BannerModel(base_url + "8t7kmeu9gy50.jpg","0");
//        BannerModel bannerModel3 = new BannerModel(base_url + "szrp31ah7dzi.mp4","1");
//        BannerModel bannerModel4 = new BannerModel(base_url + "cat.jpg","0");
//        if (playList.size() != 0){
//            playList.clear();
//        }
//        playList.add(bannerModel);
//        playList.add(bannerModel2);
//        playList.add(bannerModel3);
//        playList.add(bannerModel4);
//        if (playList != null){
//            for (int i=0;i<playList.size();i++){
//                Logger.d("TestPlay",playList.get(i).getUri() + " -- " + playList.get(i).getType());
//            }
//        }
//    }
    /**
     * 播放下一个
     */
    public void changePlay() {
        Message message = new Message();
        message.what = UPDATE_VIEWPAGER;
        //播放完一遍
        if (autoCurrIndex == playList.size() - 1) {
            autoCurrIndex = -1;
        }
        message.arg1 = autoCurrIndex + 1;
        Log.d("Test_autoCurrIndex",autoCurrIndex + 1 + "playList.size()"  + playList.size());
        mHandler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //解除注册
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        if (playList != null && playList.size() > 0) {
//            videoView = (VideoView) findViewById(R.id.videoView1);
//            videoView.setMediaController(new MediaController(this));
//            videoView.setOnCompletionListener(this);
//            videoView.setOnErrorListener(this);
//            videoView.setVideoURI(Uri.parse(as.get(0).getUri()));
//            videoView.start();
            autoBanner();
        }
    }
//    @Override
//    public void onCompletion(MediaPlayer arg0) {
//        //循环播放处理
//        Log.d(TAG, "onCompletion");
//        i++;
//        if(i<as.size()){
//            videoView.setVideoURI(Uri.parse((as.get(i).getUri())));
//            videoView.start();
//        }else if(i == as.size()){
//            i = 0;
//            videoView.setVideoURI(Uri.parse((as.get(i).getUri())));
//            videoView.start();
//        }
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mediaPlayer, int arg1, int arg2) {
//        Log.d(TAG, "onError");
//        mediaPlayer.pause();
//        mediaPlayer.stop();
//        finish();
//        return false;
//    }
//
//    @Override
//    protected void onStop() {
//        Log.d(TAG, "onStop");
//        if (videoView != null){
//            videoView.pause();
//        }
//        super.onStop();
//    }

    //订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case 0://切换播放
                changePlay();
                break;
            case 1://拔掉U盘了，清空
                Toast.makeText(ExampleApplication.getContext(),"U盘已拔出...", Toast.LENGTH_SHORT).show();
                if (playList != null && playList.size()>0){
                    playList.clear();
                }
                //关掉timer
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                //清空播放
                changPlayView(0);
                break;
        }
    }
}
