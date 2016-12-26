package com.example.root.navigationdrawertest.Dialog;

/**
 * Created by root on 18.12.16.
 */

public class Dialog {
    private String dialogName;
    private String lastMessage;
    private boolean online;
    private int img_id = android.R.mipmap.sym_def_app_icon;
    private int dialogId;

    public Dialog() {
    }

    public Dialog(String dialogName, String lastMessage, boolean online, int dialogId) {
        this.dialogName = dialogName;
        this.lastMessage = lastMessage;
        this.online = online;
        this.dialogId = dialogId;
    }

    public int getDialogId() {
        return dialogId;
    }

    public void setDialogId(int dialogId) {
        this.dialogId = dialogId;
    }

    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }
}
