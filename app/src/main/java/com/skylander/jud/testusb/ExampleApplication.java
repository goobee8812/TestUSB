package com.skylander.jud.testusb;

import android.app.Application;
import android.content.Context;


/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
    private static final String TAG = "JIGUANG-Example";
    private static Context context;
    @Override
    public void onCreate() {    	     
         super.onCreate();
        context = getApplicationContext();

    }
    public static Context getContext(){
        return context;
    }
}
