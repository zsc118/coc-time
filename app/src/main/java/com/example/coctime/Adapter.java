package com.example.coctime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Adapter extends BaseAdapter {
    Context context;
    ArrayList<Item> list;

    public Adapter(Context context, ArrayList<Item> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.tv = view.findViewById(R.id.tv_it);
            holder.subTv = view.findViewById(R.id.tv_subIt);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();
        Item it = list.get(i);
        holder.tv.setText(it.getText());
        holder.subTv.setText(it.getTimeStr());
        return view;
    }

    static class ViewHolder {
        TextView tv, subTv;
    }
}
