package com.example.root.navigationdrawertest.MessagesList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.example.root.navigationdrawertest.Core.ClientUDP;
import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.Dialog.DialogListFragment;
import com.example.root.navigationdrawertest.MainActivity;
import com.example.root.navigationdrawertest.R;
import com.example.root.navigationdrawertest.data.ChatDbHelper;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MessagesListActivity extends AppCompatActivity {

    private ListView messagesList;
    private EditText messageEdit;
    private ImageButton buttonSend;
    private MessageAdapter messageAdapter;
    private User iAm, user;
    private ChatDbHelper dbHelper;
    private Timer timerRefresh;
    private Handler handler;
    private ClientUDP clientUDP;
    private int dialogId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        try {
            clientUDP = new ClientUDP();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        messagesList = (ListView) findViewById(R.id.messagesList);
        messageEdit = (EditText) findViewById(R.id.messageEdit);
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);
        dbHelper = new ChatDbHelper(this);

        SharedPreferences sPref = getSharedPreferences("Info", MODE_PRIVATE);
        iAm = new User(null, sPref.getString("nick", "default"), sPref.getInt("img", 1));
        try {
            iAm.macToByte(MainActivity.getMacAddress(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        dialogId = intent.getIntExtra("dialogId", 0);

        if(dialogId!=0) {
            user = dbHelper.getUser(dialogId);
            setTitle(user.getNickName());
        }else{
            user = new User(new byte[]{0}, "", 0);
            setTitle("Conversation");
        }

        messageAdapter = new MessageAdapter(this, dbHelper.getMessages(dialogId), iAm);

        messagesList.setAdapter(messageAdapter);

        messagesList.setStackFromBottom(true);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                refreshMessageList(dialogId);
                messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
            }
        };

        Translator.setMessageHandler(handler);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = messageEdit.getText().toString().replaceAll("\\|", "").trim();
                if(text.equals("")){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return;
                }

                final User result = Translator.users.findByStrMac(Arrays.toString(user.getMac()));

                if(dialogId == 0){
                    if(Translator.users.getUsers().size()==0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        Toast toast = Toast.makeText(
                                MessagesListActivity.this,
                                "Nobody not online!",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                com.example.root.navigationdrawertest.Core.Message message =
                                        new com.example.root.navigationdrawertest.Core.Message(
                                                null, true, text);
                                dbHelper.addMessage(message, dialogId, 0);
                                try {
                                    clientUDP.send(message.toString().getBytes(),
                                            InetAddress.getByName("255.255.255.255"));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(1);

                            }
                        }).start();
                    }
                }else if(result==null){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Toast toast = Toast.makeText(
                            MessagesListActivity.this,
                            user.getNickName()+" not online!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            com.example.root.navigationdrawertest.Core.Message message =
                                    new com.example.root.navigationdrawertest.Core.Message(
                                            null, false, text);
                            dbHelper.addMessage(message, dialogId, 0);
                            try {
                                clientUDP.send(message.toString().getBytes(),
                                        InetAddress.getByAddress(result.getIpAddress()));
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
//                            handler.sendEmptyMessage(1);

                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(1);
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                messageEdit.setText("");
                messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            }
        });

        timerRefresh = new Timer();
        timerRefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 5000, 5000);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for(int i = 0; i< DialogListFragment.unreadedDialogs.size(); i++){
            if(DialogListFragment.unreadedDialogs.get(i)==dialogId){
                DialogListFragment.unreadedDialogs.remove(i);
                Log.e("Removed", "!");
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return false;
    }

    private void refreshMessageList(int dialogId){
        messageAdapter.setMessageHistory(dbHelper.getMessages(dialogId));
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        dbHelper.close();
        timerRefresh.cancel();
        timerRefresh.purge();
        clientUDP.close();
    }
}
