package com.example.root.navigationdrawertest.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.root.navigationdrawertest.Core.Message;
import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.Core.UserList;
import com.example.root.navigationdrawertest.Dialog.Dialog;
import com.example.root.navigationdrawertest.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by root on 17.12.16.
 */

public class ChatDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chatDb.db";
    private Context ctx;
    private static final int DATABASE_VERSION = 1;

    public ChatDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_USERS_TABLE =
                "CREATE TABLE " + DataContract.UsersEntry.TABLE_NAME + " ("
                + DataContract.UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataContract.UsersEntry.COLUMN_NICK + " TEXT NOT NULL, "
                + DataContract.UsersEntry.COLUMN_MAC + " TEXT NOT NULL, "
                + DataContract.UsersEntry.COLUMN_IMG_ID + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_USERS_TABLE);

        String SQL_CREATE_MESSAGES_TABLE =
                "CREATE TABLE " + DataContract.MessagesEntry.TABLE_NAME + " ("
                + DataContract.MessagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataContract.MessagesEntry.COLUMN_ID_DLG + " INTEGER NOT NULL, "
                + DataContract.MessagesEntry.COLUMN_ID_USER + " INTEGER NOT NULL, "
                + DataContract.MessagesEntry.COLUMN_TEXT + " TEXT NOT NULL, "
                + DataContract.MessagesEntry.COLUMN_BROADCAST + " INTEGER NOT NULL, "
                + DataContract.MessagesEntry.COLUMN_TIME + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);

        String SQL_CREATE_DIALOGS_TABLE =
                "CREATE TABLE " + DataContract.DialogsEntry.TABLE_NAME + " ("
                + DataContract.DialogsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataContract.DialogsEntry.COLUMN_ID_USER + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_DIALOGS_TABLE);

        ContentValues values = new ContentValues();
        //adding broadcast conversation
        values.put(DataContract.DialogsEntry._ID, 0);
        values.put(DataContract.DialogsEntry.COLUMN_ID_USER, 0);

        db.insert(DataContract.DialogsEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public void addUser(final User user){
        //new Thread(new Runnable() {
          //  @Override
         //   public void run() {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DataContract.UsersEntry.COLUMN_IMG_ID, user.getImageId());
                values.put(DataContract.UsersEntry.COLUMN_MAC, Arrays.toString(user.getMac()));
                values.put(DataContract.UsersEntry.COLUMN_NICK, user.getNickName());

                db.insert(DataContract.UsersEntry.TABLE_NAME, null, values);
           // }
       // }).start();
    }

    public void addDialog(final int userId){
        //new Thread(new Runnable() {
            //@Override
           // public void run() {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DataContract.DialogsEntry.COLUMN_ID_USER, userId);

                db.insert(DataContract.DialogsEntry.TABLE_NAME, null, values);
           // }
       // }).start();
    }

    public void addMessage(final Message message, final int dialogId, final int userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DataContract.MessagesEntry.COLUMN_ID_DLG, dialogId);
                values.put(DataContract.MessagesEntry.COLUMN_ID_USER, userId);
                values.put(DataContract.MessagesEntry.COLUMN_TEXT, message.getText());
                values.put(DataContract.MessagesEntry.COLUMN_TIME, message.getFormatTime());
                int broadcast = 0;
                if (message.isBroadcast()){
                    broadcast = 1;
                }
                values.put(DataContract.MessagesEntry.COLUMN_BROADCAST, broadcast);

                db.insert(DataContract.MessagesEntry.TABLE_NAME, null, values);
            }
        }).start();
    }

    public int getDialogId(int userId){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DataContract.DialogsEntry._ID};
        String selection = DataContract.DialogsEntry.COLUMN_ID_USER + "=?";
        String[] selectionArgs = {Integer.toString(userId)};

        Cursor cursor = db.query(DataContract.DialogsEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();
        if(count<=0){
            cursor.close();
            return -1;
        }else{
            cursor.moveToNext();

            int result = cursor.getInt(cursor.getColumnIndex(DataContract.DialogsEntry._ID));
            cursor.close();
            return result;
        }
    }

    public Message getMessage(int messageId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor messageCursor = db.query(DataContract.MessagesEntry.TABLE_NAME, null,
                DataContract.MessagesEntry._ID+"=?",
                new String[]{Integer.toString(messageId)},
                null, null, null);

        int dialogIdColumnIndex = messageCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_ID_DLG);
        int userIdColumnIndex = messageCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_ID_USER);
        int textColumnIndex = messageCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_TEXT);
        int timeColumnIndex = messageCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_TIME);

        messageCursor.moveToNext();
        int userId = messageCursor.getInt(userIdColumnIndex);
        String text = messageCursor.getString(textColumnIndex);
        String time = messageCursor.getString(timeColumnIndex);
        int dialogId = messageCursor.getInt(dialogIdColumnIndex);
        long timeMillis = 0;

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("HH:mm");
        try {
            Date docDate= format.parse(time);
            timeMillis = docDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = null;
        if(userId==0){
            SharedPreferences sPref = ctx.getSharedPreferences("Info", ctx.MODE_PRIVATE);
            String nick = sPref.getString("nick", "default");
            int imageId = sPref.getInt("img", 1);
            String macStr = null;
            try {
                macStr = MainActivity.getMacAddress(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = new User(null, nick, imageId);
            user.macToByte(macStr);
        }else {
            Cursor userCursor = db.query(DataContract.UsersEntry.TABLE_NAME,
                    null, DataContract.UsersEntry._ID + "=?",
                    new String[]{Integer.toString(userId)}, null, null, null);

            userCursor.moveToNext();

            int imgIdColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_IMG_ID);
            int nickColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_NICK);
            int macColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_MAC);

            int imgId = userCursor.getInt(imgIdColumnIndex);
            String nick = userCursor.getString(nickColumnIndex);
            String strMac = userCursor.getString(macColumnIndex);

            userCursor.close();

            strMac = strMac.substring(1, strMac.length() - 1);

            String[] strArr = strMac.split(",");

            byte[] resultByte = new byte[strArr.length];

            for (int i = 0; i < strArr.length; i++) {
                resultByte[i] = Byte.parseByte(strArr[i].trim());
            }

            user = new User(resultByte, nick, imgId);
        }
        boolean broadcast = dialogId == 0;
        Message message = new Message(user, broadcast, text, timeMillis);


        messageCursor.close();
        return message;
    }

    public int getUserId(final User user){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {DataContract.UsersEntry._ID};
        String selection = DataContract.UsersEntry.COLUMN_MAC + "=?";
        String[] selectionArgs = {Arrays.toString(user.getMac())};

        Cursor cursor = db.query(DataContract.UsersEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();
        if(count<=0){
            cursor.close();
            return -1;
        }else{
            cursor.moveToNext();
            int result = cursor.getInt(cursor.getColumnIndex(DataContract.DialogsEntry._ID));
            cursor.close();
            return result;
        }
    }


    public ArrayList<Dialog> getDialogs(){
        ArrayList<Dialog> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor dialogsCursor = db.query(DataContract.DialogsEntry.TABLE_NAME,
                null, null, null, null, null, null);

        int userIdColumn_index = dialogsCursor.getColumnIndex(DataContract.DialogsEntry.COLUMN_ID_USER);
        int dialogIdColumn_index = dialogsCursor.getColumnIndex(DataContract.DialogsEntry._ID);

        while(dialogsCursor.moveToNext()){
            Dialog dialog = new Dialog();

            int user_id = dialogsCursor.getInt(userIdColumn_index);
            int dialog_id = dialogsCursor.getInt(dialogIdColumn_index);

            dialog.setDialogId(dialog_id);

            if(user_id==0){
                dialog.setDialogName("Conversation");
                dialog.setImg_id(0); //HERE MUST SPECIAL IMG FOR BROADCAST
                dialog.setOnline(false);//IN FUTURE MAY BE FIXED
            }else{
                String selection = DataContract.UsersEntry._ID + "=?";
                String[] selectionArgs = {Integer.toString(user_id)};

                Cursor cursor = db.query(DataContract.UsersEntry.TABLE_NAME,
                        null, selection, selectionArgs, null, null, null);

                if(cursor.moveToNext()) {
                    int nickColumnIndex = cursor.getColumnIndex(DataContract.UsersEntry.COLUMN_NICK);
                    int imgColumnIndex = cursor.getColumnIndex(DataContract.UsersEntry.COLUMN_IMG_ID);
                    int macColumnIndex = cursor.getColumnIndex(DataContract.UsersEntry.COLUMN_MAC);
                    String mac = cursor.getString(macColumnIndex);
                    String nick = cursor.getString(nickColumnIndex);
                    int img = cursor.getInt(imgColumnIndex);
                    dialog.setOnline(Translator.users.findByStrMac(mac) != null);
                    dialog.setDialogName(nick);
                    dialog.setImg_id(img);
                }

                cursor.close();
            }

            String selection = DataContract.MessagesEntry.COLUMN_ID_DLG+"=?";
            String[] selectionArgs = new String[]{Integer.toString(dialog_id)};
            Cursor cursor = db.query(DataContract.MessagesEntry.TABLE_NAME,
                    null, selection, selectionArgs, null, null, DataContract.MessagesEntry._ID+" DESC");

            int messageTextColumnIndex = cursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_TEXT);
            if(cursor.moveToNext()) {
                String lastMessage = cursor.getString(messageTextColumnIndex);
                dialog.setLastMessage(lastMessage);

                result.add(dialog);
            }else if(user_id == 0){
                result.add(dialog);
            }

            cursor.close();
        }

        dialogsCursor.close();
        return result;
    }

    public void refreshUsers(UserList users) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i = 0; i< users.getUsers().size(); i++){
            User user = users.getUsers().get(i);
            values.put(DataContract.UsersEntry.COLUMN_IMG_ID, user.getImageId());
            values.put(DataContract.UsersEntry.COLUMN_NICK, user.getNickName());
            String strMac = Arrays.toString(user.getMac());
            values.put(DataContract.UsersEntry.COLUMN_MAC, strMac);
            db.update(DataContract.UsersEntry.TABLE_NAME,
                    values, DataContract.UsersEntry.COLUMN_MAC+"= ?", new String[]{strMac});

            values.clear();
        }
    }

    public int deleteMessage(int dialogId){
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(DataContract.MessagesEntry.TABLE_NAME,
                DataContract.MessagesEntry.COLUMN_ID_DLG+"=?",
                new String[]{Integer.toString(dialogId)});
    }

    public ArrayList<Message> getMessages(int dialogId){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> result = new ArrayList<>();
        Cursor messagesCursor = db.query(DataContract.MessagesEntry.TABLE_NAME, null,
                DataContract.MessagesEntry.COLUMN_ID_DLG+"=?",
                new String[]{Integer.toString(dialogId)},
                null, null, null);

        int userIdColumnIndex = messagesCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_ID_USER);
        int textColumnIndex = messagesCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_TEXT);
        int timeColumnIndex = messagesCursor.getColumnIndex(DataContract.MessagesEntry.COLUMN_TIME);

        while(messagesCursor.moveToNext()) {
            int userId = messagesCursor.getInt(userIdColumnIndex);
            String text = messagesCursor.getString(textColumnIndex);
            String time = messagesCursor.getString(timeColumnIndex);
            long timeMillis = 0;

            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("HH:mm");
            try {
                Date docDate= format.parse(time);
                timeMillis = docDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            User user = null;
            if(userId==0){
                SharedPreferences sPref = ctx.getSharedPreferences("Info", ctx.MODE_PRIVATE);
                String nick = sPref.getString("nick", "default");
                int imageId = sPref.getInt("img", 1);
                String macStr = null;
                try {
                    macStr = MainActivity.getMacAddress(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                user = new User(null, nick, imageId);
                user.macToByte(macStr);
            }else {
                Cursor userCursor = db.query(DataContract.UsersEntry.TABLE_NAME,
                        null, DataContract.UsersEntry._ID + "=?",
                        new String[]{Integer.toString(userId)}, null, null, null);

                userCursor.moveToNext();

                int imgIdColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_IMG_ID);
                int nickColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_NICK);
                int macColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_MAC);

                int imgId = userCursor.getInt(imgIdColumnIndex);
                String nick = userCursor.getString(nickColumnIndex);
                String strMac = userCursor.getString(macColumnIndex);

                userCursor.close();

                strMac = strMac.substring(1, strMac.length() - 1);

                String[] strArr = strMac.split(",");

                byte[] resultByte = new byte[strArr.length];

                for (int i = 0; i < strArr.length; i++) {
                    resultByte[i] = Byte.parseByte(strArr[i].trim());
                }

                user = new User(resultByte, nick, imgId);
            }
            boolean broadcast = dialogId == 0;
            Message message = new Message(user, broadcast, text, timeMillis);

            result.add(message);
        }
        messagesCursor.close();
        return result;
    }

    public int regulateMessagesCount(int dialog_id, int messageCount){
        SQLiteDatabase db = getWritableDatabase();
        Cursor messageCursor = db.query(DataContract.MessagesEntry.TABLE_NAME,
                null, DataContract.MessagesEntry.COLUMN_ID_DLG+"=?",
                new String[]{Integer.toString(dialog_id)},
                null, null, DataContract.MessagesEntry._ID+" ASC");

        int count = messageCursor.getCount();
        int deletedCount = 0;

        if (count > messageCount){
            deletedCount = count - messageCount;
            int messageIdColumnIndex = messageCursor.getColumnIndex(DataContract.MessagesEntry._ID);
            for(int i = 0; i < deletedCount; i++){
                messageCursor.moveToNext();
                int messageId = messageCursor.getInt(messageIdColumnIndex);
                db.delete(DataContract.MessagesEntry.TABLE_NAME,
                        DataContract.MessagesEntry._ID+"=?",
                        new String[]{Integer.toString(messageId)});
            }
        }

        messageCursor.close();
        return deletedCount;
    }

    public User getUser(int dialogId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor dialogCursor = db.query(DataContract.DialogsEntry.TABLE_NAME, null,
                DataContract.DialogsEntry._ID+"=?", new String[]{Integer.toString(dialogId)},
                null, null, null);
        int userIdColumnIndex = dialogCursor.getColumnIndex(DataContract.DialogsEntry.COLUMN_ID_USER);
        dialogCursor.moveToNext();
        int userId = dialogCursor.getInt(userIdColumnIndex);
        dialogCursor.close();

        Cursor userCursor = db.query(DataContract.UsersEntry.TABLE_NAME, null,
                DataContract.UsersEntry._ID+"=?", new String[]{Integer.toString(userId)},
                null, null, null);

        userCursor.moveToNext();

        int imgIdColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_IMG_ID);
        int nickColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_NICK);
        int macColumnIndex = userCursor.getColumnIndex(DataContract.UsersEntry.COLUMN_MAC);

        int imgId = userCursor.getInt(imgIdColumnIndex);
        String nick = userCursor.getString(nickColumnIndex);
        String strMac = userCursor.getString(macColumnIndex);

        userCursor.close();

        strMac = strMac.substring(1, strMac.length()-1);

        String[] strArr = strMac.split(",");

        byte[] resultByte = new byte[strArr.length];

        for(int i = 0; i<strArr.length; i++){
            resultByte[i] = Byte.parseByte(strArr[i].trim());
        }

        User user = new User(resultByte, nick, imgId);
        return user;
    }
}
