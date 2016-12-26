package com.example.root.navigationdrawertest.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;


import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.MessagesList.MessagesListActivity;
import com.example.root.navigationdrawertest.R;
import com.example.root.navigationdrawertest.data.ChatDbHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 17.12.16.
 */

public class DialogListFragment extends Fragment {

    private DialogAdapter adapter;
    private Timer timer;
    private Handler handler;
    private ChatDbHelper dbHelper;
    public volatile static ArrayList<Integer> unreadedDialogs = new ArrayList<>();


    public static DialogListFragment newInstance(DialogAdapter adapter, ChatDbHelper dbHelper){
        DialogListFragment obj = new DialogListFragment();
        obj.adapter = adapter;
        obj.dbHelper = dbHelper;
        return obj;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogs_list, container, false);
        ListView list = (ListView)view.findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), MessagesListActivity.class);
                intent.putExtra("dialogId", ((Dialog)adapter.getItem(i)).getDialogId());

                startActivity(intent);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int dialogId = ((Dialog)adapter.getItem(i)).getDialogId();
                showPopupMenu(view, dialogId);
                return true;
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);

            }
        }, 1000, 1000);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        dbHelper.refreshUsers(Translator.users);
                        adapter.setDialogs(dbHelper.getDialogs());
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        return view;
    }

    private void showPopupMenu(View v, final int i) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.dialog_popupmenu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                switch (itemId){
                    case R.id.actionDeleteDialog:
                        dbHelper.deleteMessage(i);
                        return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timer.cancel();
        timer.purge();
    }
}
