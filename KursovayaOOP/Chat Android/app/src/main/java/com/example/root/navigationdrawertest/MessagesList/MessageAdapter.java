package com.example.root.navigationdrawertest.MessagesList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.navigationdrawertest.Core.Message;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.R;

import java.util.ArrayList;


/**
 * Created by User on 13.11.2016.
 */
public class MessageAdapter extends BaseAdapter {
    private User iAm;
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Message> messageHistory;

    public MessageAdapter(Context ctx, ArrayList<Message> messageHistory, User iAm){
        this.ctx = ctx;
        lInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageHistory = messageHistory;
        this.iAm = iAm;
    }

    @Override
    public int getCount() {
        return messageHistory.size();
    }

    @Override
    public Object getItem(int i) {
        return messageHistory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {

        Message m = messageHistory.get(index);

        //if(view == null){
            if (m.getUser().equals(iAm)) {
                view = lInflater.inflate(R.layout.item_my_message, viewGroup, false);
            } else { // If not mine use another message layout
                view = lInflater.inflate(R.layout.item_message, viewGroup, false);
            }
        //}


        int imgId = ctx.getResources().getIdentifier(
                "img"+m.getUser().getImageId(), "drawable", ctx.getPackageName());

        ((ImageView)(view.findViewById(R.id.userImage))).setImageResource(imgId);
        ((TextView)(view.findViewById(R.id.message_text))).setText(m.getText());
        ((TextView)(view.findViewById(R.id.sender_name))).setText(m.getUser().getNickName());
        ((TextView)(view.findViewById(R.id.message_time))).setText(m.getFormatTime());


        return view;
    }

    public void setMessageHistory(ArrayList<Message> messageHistory) {
        this.messageHistory = messageHistory;
    }
}
