package com.example.root.navigationdrawertest.OnlineList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.Core.UserList;
import com.example.root.navigationdrawertest.R;

/**
 * Created by root on 25.12.16.
 */

public class UserArrayAdapter extends ArrayAdapter<User> {
    private LayoutInflater lInflater;

    public UserArrayAdapter(Context ctx, UserList users){
        super(ctx, R.layout.item_user, users.getUsers());
        this.lInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = null;

        if(convertView == null){
            convertView = lInflater.inflate(R.layout.item_user, parent, false);
        }

        if(position < Translator.users.getUsers().size()) {
            user = getItem(position);
        }else{
            return convertView;
        }

        ((TextView)convertView.findViewById(R.id.userNick)).setText(user.getNickName());

        ImageView userImage = (ImageView)convertView.findViewById(R.id.userImage);

        if(user.getImageId() != 0){
            int id = getContext().getResources().getIdentifier("img"+user.getImageId(),
                    "drawable", getContext().getPackageName());
            userImage.setImageResource(id);
        }else{
            userImage.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}
