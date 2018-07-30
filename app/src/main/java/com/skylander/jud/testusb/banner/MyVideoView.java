package com.skylander.jud.testusb.banner;

import android.content.Context;
import android.util.AttributeSet;


import com.sprylab.android.widget.TextureVideoView;


/**
 * Created by Huangzh on 2018/2/22.
 */

public class MyVideoView extends TextureVideoView {
    public MyVideoView(Context context) {
        super(context);
    }
    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
//        super.setOnErrorListener(l);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int viewWidth = getDefaultSize(0, widthMeasureSpec);
        int viewHeight = getDefaultSize(0, heightMeasureSpec);
        //父类方法，执行完之后，如果View尺寸大于视频源尺寸，则视频按照视频源尺寸显示，四周都有黑边；如果View尺寸小于视频源尺寸，则视频保留宽高比放大到与View尺寸相匹配，至多会有两条黑边
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int videoWidth = getMeasuredWidth();
        int videoHeight = getMeasuredHeight();

        //如果View的宽高比与视频源的宽高比接近，则不管这两者尺寸的大小关系如何，都将视频填满View显示
        float viewAspect = viewWidth / (float) viewHeight;
        float videoAspect = videoWidth / (float) videoHeight;
        if (viewAspect / videoAspect > 0.9f && viewAspect / videoAspect < 1.1f) {
            setMeasuredDimension(viewWidth, viewHeight);
            //如果View的宽高比与视频源的宽高比相差较大，若View尺寸小于视频源尺寸，则父类的onMeasure()方法是正确的处理方式；若视频源尺寸小于View尺寸，则需要将视频保留宽高比放大到与View尺寸相匹配
        } else if (videoWidth < viewWidth && videoHeight < viewHeight) {
            if ((float)videoWidth / videoHeight < (float)viewWidth / viewHeight) {
                //两侧留黑边
                setMeasuredDimension((int)(viewHeight * ((float)videoWidth/videoHeight)), viewHeight);
            } else {
                //上下留黑边
                setMeasuredDimension(viewWidth, (int)(viewWidth * ((float)videoHeight/videoWidth)));
            }
        }
    }
}
