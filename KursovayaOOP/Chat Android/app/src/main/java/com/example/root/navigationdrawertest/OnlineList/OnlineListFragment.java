package com.example.root.navigationdrawertest.OnlineList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.root.navigationdrawertest.Core.OnlineCleanerTimerTask;
import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.Core.UserList;
import com.example.root.navigationdrawertest.MainActivity;
import com.example.root.navigationdrawertest.MessagesList.MessagesListActivity;
import com.example.root.navigationdrawertest.R;
import com.example.root.navigationdrawertest.data.ChatDbHelper;

import java.util.Timer;

/**
 * Created by root on 06.12.16.
 */

public class OnlineListFragment extends Fragment {
    private UserList users;
    private UserArrayAdapter userArrayAdapter;
    private Handler handler;
    private TextView wifiTextView;
    //private ListView onlineList;
    private ChatDbHelper dbHelper;
    private Timer onlineListFragmentTimer;


    public static OnlineListFragment newInstance(UserList users, ChatDbHelper dbHelper) {
        OnlineListFragment fragment = new OnlineListFragment();
        fragment.users = users;
        fragment.dbHelper = dbHelper;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_online_list, container, false);
        final ListView onlineList = (ListView)rootView.findViewById(R.id.onlineList);
        wifiTextView = (TextView)rootView.findViewById(R.id.wifiTextView);

//        WifiReceiver.setWifiTextView(wifiTextView);

//        if(!MainActivity.isOnline(getContext())){
//            wifiTextView.setText("No wifi connection!");
//        }

        userArrayAdapter = new UserArrayAdapter(getContext(), users);
        userArrayAdapter.setNotifyOnChange(true);
        onlineList.setAdapter(userArrayAdapter);
        userArrayAdapter.notifyDataSetChanged();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                userArrayAdapter.notifyDataSetChanged();
                Log.e("TAG", "refresh list");
                onlineList.requestLayout();
            }
        };

        Translator.setHandler(handler);

        onlineListFragmentTimer = new Timer();
        UserAdapterTimerTask userAdapterTimerTask = new UserAdapterTimerTask(handler);
        OnlineCleanerTimerTask onlineCleanerTimer = new OnlineCleanerTimerTask(
                Translator.users.getUsers(), handler);
        onlineListFragmentTimer.schedule(userAdapterTimerTask, 500, 500);
        onlineListFragmentTimer.schedule(onlineCleanerTimer, 1000, 1000);

        onlineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userArrayAdapter.getItem(i);

                int userId = dbHelper.getUserId(user);

                if (userId == -1) {
                    dbHelper.addUser(user);
                    userId = dbHelper.getUserId(user);
                    dbHelper.addDialog(userId);
                }

                int dialogId = dbHelper.getDialogId(userId);
                Intent intent = new Intent(getContext(), MessagesListActivity.class);
                intent.putExtra("dialogId", dialogId);

                startActivity(intent);
            }
        });

        return rootView;
    }

    public UserList getUsers() {
        return users;
    }

    public void setUsers(UserList users) {
        this.users = users;
    }

    public UserArrayAdapter getUserArrayAdapter() {
        return userArrayAdapter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onlineListFragmentTimer.cancel();
        onlineListFragmentTimer.purge();
    }
}
