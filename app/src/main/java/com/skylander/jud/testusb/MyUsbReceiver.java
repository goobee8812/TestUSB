package com.skylander.jud.testusb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.skylander.jud.testusb.banner.BannerModel;
import com.skylander.jud.testusb.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyUsbReceiver extends BroadcastReceiver {
    private String TAG="MyUsbReceiver";
    ArrayList<BannerModel> as = new ArrayList<>();

    /**
     *  判断是否是视频
     * @param context
     * @param fileName
     * @return
     */
    private  boolean isMovieSuffix(Context context, String fileName) {
        //判断是否是视频文件
        String name = fileName.toLowerCase();
        String[] suffixs = context.getResources().getStringArray(
                R.array.video_type_suffix);
        for (String string : suffixs) {
            if (name.endsWith(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是图片
     * @param context
     * @param fileName
     * @return
     */
    private  boolean isImageSuffix(Context context, String fileName) {
        //判断是否是视频文件
        String name = fileName.toLowerCase();
        String[] suffixs = context.getResources().getStringArray(
                R.array.image_type_suffix);
        for (String string : suffixs) {
            if (name.endsWith(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final Context mContext = context;
        String action = intent.getAction();
        Uri uri = intent.getData();
        final String path = uri.getPath();
        final String featureFilePath = path+"/" + context.getResources().getString(
                R.string.feature_file_name);
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            //挂载成功
            Log.d(TAG,"~~~~~~~~~~~~~~~~~~~media mounted 111111");
            Toast.makeText(context,"USB挂载成功",Toast.LENGTH_LONG).show();
            new Thread() {
                public void run() {
                    File file = new File(featureFilePath);
                    if (file.exists() && file.isDirectory()) {
                        String[] files = file.list();
                        for(int i = 0;i<files.length;i++){
                            String s = files[i];
                            if(isMovieSuffix(mContext,s)){
                                //添加视频
                                BannerModel resources = new BannerModel(featureFilePath+"/" + s,"1");
                                as.add(resources);
                            }else if (isImageSuffix(mContext,s)){
                                //添加图片
                                BannerModel resources = new BannerModel(featureFilePath+"/" + s,"0");
                                as.add(resources);
                            }
                        }
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ff",as);
                        mContext.startActivity(intent);
                    }else{
                        Log.d(TAG, featureFilePath+" is not exist.");
                    }
                }
            }.start();
        }else if (action.equals(Intent.ACTION_MEDIA_REMOVED) | action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
            Log.d(TAG,"~~~~~~~~~~~~~~~~~~~unnnnnnn——mount");
            //拔掉U盘，关闭播放
            EventBus.getDefault().post(new MessageEvent(1));
        }
    }
}