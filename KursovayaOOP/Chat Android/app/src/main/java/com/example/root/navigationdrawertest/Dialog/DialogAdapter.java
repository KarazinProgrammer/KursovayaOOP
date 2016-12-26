package com.example.root.navigationdrawertest.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.R;

import java.util.ArrayList;

/**
 * Created by root on 18.12.16.
 */

public class DialogAdapter extends BaseAdapter {
    private ArrayList<Dialog> dialogs;
    private Context context;
    private LayoutInflater lInflater;

    public DialogAdapter(Context context, ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
        this.context = context;
        this.lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    public int getCount() {
        return dialogs.size();
    }

    @Override
    public Object getItem(int i) {
        return dialogs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view = lInflater.inflate(R.layout.dialog_item, viewGroup, false);
        }

        Dialog dialog = (Dialog)getItem(i);

        if(DialogListFragment.unreadedDialogs.contains(dialog.getDialogId())){
            view.findViewById(R.id.parentDialog).setBackgroundColor(
                    context.getResources().getColor(android.R.color.darker_gray));
        }

        TextView nameDialog = (TextView)view.findViewById(R.id.nameDialog);
        TextView lastMessageDialog = (TextView)view.findViewById(R.id.lastMsgDialog);
        ImageView onlineMark = (ImageView)view.findViewById(R.id.onlineMark);
        ImageView imageDialog = (ImageView)view.findViewById(R.id.imgDialog);

        int imgId = context.getResources().getIdentifier(
                "img"+dialog.getImg_id() , "drawable", context.getPackageName());
        imageDialog.setImageResource(imgId);
        nameDialog.setText(dialog.getDialogName());
        lastMessageDialog.setText(dialog.getLastMessage());

        if(dialog.isOnline()){
            onlineMark.setVisibility(View.VISIBLE);
        }else{
            onlineMark.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
