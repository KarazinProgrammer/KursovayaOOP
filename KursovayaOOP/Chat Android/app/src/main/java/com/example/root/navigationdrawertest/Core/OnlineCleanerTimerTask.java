package com.example.root.navigationdrawertest.Core;

import android.os.Handler;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by root on 08.12.16.
 */

public class OnlineCleanerTimerTask extends TimerTask {
    private ArrayList<User> users;
    private Handler onlineListHandler;

    public OnlineCleanerTimerTask(ArrayList<User> users, Handler handler) {
        this.users = users;
        this.onlineListHandler = handler;
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        for(int i = 0; i<users.size(); i++){
            if((currentTime - users.get(i).getTime()) >= 3000){
                users.remove(i);
//                onlineListHandler.sendEmptyMessage(0);
            }
        }
    }
}
