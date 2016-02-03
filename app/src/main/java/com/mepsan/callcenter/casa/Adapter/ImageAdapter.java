package com.mepsan.callcenter.casa.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mepsan.callcenter.casa.ArizaClass.GridItem;
import com.mepsan.callcenter.casa.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    // Keep all Images in array
    List<GridItem> resimler=new ArrayList<>();

    // Constructor
    public ImageAdapter(Context c,List<GridItem> resimler){
        mContext = c;
        this.resimler=resimler;

    }

    @Override
    public int getCount() {
        return resimler.size();
    }

    @Override
    public Object getItem(int position) {
        return resimler.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.image_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.imageView2);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        try {
            GridItem item = resimler.get(position);
            holder.imageView.setImageBitmap(item.getImage());

        }catch (Exception e){
            Log.d("asdasddsadsa",e.toString());
        }
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}