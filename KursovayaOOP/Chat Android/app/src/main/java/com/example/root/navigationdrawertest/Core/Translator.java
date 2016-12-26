package com.example.root.navigationdrawertest.Core;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;


import com.example.root.navigationdrawertest.Dialog.DialogListFragment;
import com.example.root.navigationdrawertest.R;
import com.example.root.navigationdrawertest.data.ChatDbHelper;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by root on 06.12.16.
 */
public class Translator {
    public volatile static UserList users = new UserList();
    //public static MessageList messagesQueue = new MessageList();
    private User iAm;
    private ChatDbHelper dbHelper;
    private Context ctx;
    private static Handler messageHandler;
    private static Handler handler;
    private SoundPool sp;
    private int soundId;

    public static void setHandler(Handler handler){
        Translator.handler = handler;
    }

    public static void setMessageHandler(Handler handler){
        messageHandler = handler;
    }

    public Translator(User iAm, ChatDbHelper dbHelper, Context ctx) {
        this.iAm = iAm;
        this.dbHelper = dbHelper;
        this.ctx = ctx;

        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(ctx, R.raw.message_sound, 1);
    }

    public synchronized void convert(byte[] data, InetAddress ipAddress) {
        String str = new String(data);
        str = Translator.filterString(str);
        String[] arr = str.split("\\|");

        if(arr.length == 2){//Message instance
            try {
                Message m = Message.valueOf(str);
                User u = users.findByIP(ipAddress);
                if(u==null){
                    return;
                }
                m.setUser(u);
                int userId = dbHelper.getUserId(u);
                int dialogId = 0;
                if(userId<0){//dont exist
                    dbHelper.addUser(u);
                    userId = dbHelper.getUserId(u);
                    dbHelper.addDialog(userId);
                }else{//exist
                    if(!m.isBroadcast()){
                        dialogId = dbHelper.getDialogId(userId);
                    }
                }

                dbHelper.addMessage(m, dialogId, userId);
                //DialogListFragment.unreadedDialogs.add(dialogId);

                sp.play(soundId, 1, 1, 0, 0, 1);

                //обновить сообщения
                if(messageHandler != null) {
                    messageHandler.sendEmptyMessage(0);
                }
                //messagesQueue.add(m);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }else if(arr.length == 3){//User instance
            User u = User.valueOf(str);
            if(!Arrays.equals(u.getMac(), iAm.getMac())) {
                u.setIpAddress(ipAddress.getAddress());
                u.setTime(System.currentTimeMillis());
                users.add(u);
                if(handler != null){
                    //handler.sendEmptyMessage(0);
                }
            }
        }else{//Unknown instance
            System.err.print("Unknown instance!\n");
        }
    }

    private static String filterString(String str){
        String emptySymbol = ""+(char)0;
        return str.replaceAll(emptySymbol, "");
    }
}
