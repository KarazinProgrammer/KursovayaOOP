package com.example.root.navigationdrawertest.OnlineList;

import android.os.Handler;

import java.util.TimerTask;

/**
 * Created by root on 07.12.16.
 */

public class UserAdapterTimerTask extends TimerTask{
    private Handler handler;

    public UserAdapterTimerTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(1);
    }
}
