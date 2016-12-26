package com.example.root.navigationdrawertest.InitialActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;


import com.example.root.navigationdrawertest.R;

import java.util.ArrayList;

/**
 * Created by root on 08.12.16.
 */

public class GridViewAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Integer> imageIDs;
    private View.OnClickListener listener;

    public GridViewAdapter(Context ctx) {
        this.ctx = ctx;
        this.lInflater = (LayoutInflater)this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageIDs = new ArrayList<>();
        for(int i = 1; i<=30; i++){
            int id = ctx.getResources().getIdentifier("img"+i , "drawable", ctx.getPackageName());
            imageIDs.add(id);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return imageIDs.size();
    }

    @Override
    public Object getItem(int i) {
        return imageIDs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int imageID = (Integer)getItem(i);

        if(view == null){
            view = lInflater.inflate(R.layout.item_grid_view, viewGroup, false);
        }

        ImageButton ib = (ImageButton)view.findViewById(R.id.gridImage);
        ib.setImageResource(imageID);
        ib.setOnClickListener(listener);
        ib.setTag(i+1);

        return view;
    }
}
