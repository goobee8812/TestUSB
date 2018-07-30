package com.skylander.jud.testusb.banner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.skylander.jud.testusb.ExampleApplication;
import com.skylander.jud.testusb.Logger;
import com.skylander.jud.testusb.MainActivity;
import com.skylander.jud.testusb.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class BannerViewAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<BannerModel> listBean;
//    public static ArrayList<VideoView> videoViewList = new ArrayList<>();
    public static ArrayList<MyVideoView> videoViewList = new ArrayList<>();

    public BannerViewAdapter(Activity context, ArrayList<BannerModel> list) {
        this.context = context.getApplicationContext();
        if (list == null || list.size() == 0) {
            this.listBean = new ArrayList<>();
        } else {
            this.listBean = list;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (!videoViewList.isEmpty())
        {
            videoViewList.clear();
        }
        if (listBean.get(position).getType().equals("0")) {//图片
            final ImageView imageView = new ImageView(context);
            String path = listBean.get(position).getUri();
            Glide.with(context).load(path)
                    .skipMemoryCache(true)
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }else{//视频
            final MyVideoView videoView = new MyVideoView(context);
//            final VideoView videoView = new VideoView(context);

            String path = listBean.get(position).getUri();
            Logger.d("TestPlay", "instantiateItem: " + path);
            videoView.setVideoURI(Uri.parse(path));
            videoView.setOnErrorListener(videoErrorListener);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Logger.d("TestPlay","播放完成！");
                    EventBus.getDefault().post(new MessageEvent(0));
                }
            });
            //开始播放
            videoView.start();
            videoViewList.add(videoView);
            //需要使用一个 layout 装载 videoview
            LinearLayout linearLayout = new LinearLayout(ExampleApplication.getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setMinimumHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            linearLayout.addView(videoView);
            linearLayout.setBackgroundColor(Color.BLACK);
            container.addView(linearLayout);
            return linearLayout;
        }
    }
    //防出现无法播放此视频窗口
    public MediaPlayer.OnErrorListener videoErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(ExampleApplication.getContext(),"视频播放错误", Toast.LENGTH_SHORT).show();
            return true;
        }
    };


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }



    /**
     * 获取指定文件大小
     * @param =
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        else{
            file.createNewFile();
            Log.e("获取文件大小","文件不存在!");
        }
        return size;
    }

}
