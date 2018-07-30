package com.skylander.jud.testusb.eventbus;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/1/30.
 */

public class MessageEvent {
    private int type;

    public MessageEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
