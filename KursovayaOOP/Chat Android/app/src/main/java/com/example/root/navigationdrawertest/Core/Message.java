package com.example.root.navigationdrawertest.Core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 07.12.16.
 */

public class Message {
    public static final String regex = "|";

    private User user;
    private boolean broadcast;
    private String text;
    private long time;

    public Message(User user, boolean broadcast, String text, long time) {
        this.user = user;
        this.broadcast = broadcast;
        this.text = text;
        this.time = time;
    }

    public Message(User user, boolean broadcast, String text) {
        this.user = user;
        this.broadcast = broadcast;
        this.text = text;
        this.time = System.currentTimeMillis();
    }

    public String getFormatTime(){
        Date date = new Date(this.time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(date);
    }

    @Override
    public String toString() {
        return text+Message.regex+broadcast;
    }

    public byte[] toByte(){
        return this.toString().getBytes();
    }

    public static Message valueOf(String string)throws InstantiationException{
        String[] arr = string.split("\\|");
        if(arr.length != 2){
            throw new InstantiationException("Not valid parameter!");
        }

        return new Message(null, Boolean.valueOf(arr[1]), arr[0]);
    }

    public static Message createFromByte(byte[] bytes) throws InstantiationException{
        String str = new String(bytes);
        return Message.valueOf(str);
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
