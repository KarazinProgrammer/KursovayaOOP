package com.example.root.navigationdrawertest.Core;

import android.os.Handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class UserSenderTimerTask extends TimerTask{
    private User user;
    private ClientUDP client;
    private Handler handler;

    public UserSenderTimerTask(User user, Handler handler, ClientUDP client){
        this.user = user;
        this.client = client;
        this.handler = handler;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void run() {
        try {
            if(user != null) {
                if (Translator.users.isUniqueNick(user.getNickName())) {
                    client.send(user.toByte(), InetAddress.getByName("255.255.255.255"));
                } else {
                    //вызывается InitialActivity через MainActivity
                    handler.sendEmptyMessage(1);
                    user = null;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}