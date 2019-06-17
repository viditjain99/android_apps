package com.kurtis.rewardsunlimited;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Retailer> retailersArrayList;  //list of categories for main page grid

    public GridAdapter(Context context,ArrayList<Retailer> retailersArrayList)
    {
        this.context=context;
        this.retailersArrayList=retailersArrayList;
    }

    @Override
    public int getCount()   //number of categories
    {
        return retailersArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.grid_layout,parent,false);  //inflating the layout
            TextView textView=convertView.findViewById(R.id.categoryNameTextView);
            ImageView imageView=convertView.findViewById(R.id.categoryIconImageView);
            textView.setText(retailersArrayList.get(position).nameInTitle);    //setting category name
            Glide.with(context).load(retailersArrayList.get(position).url).into(imageView);  //loading icon using the Picasso library
        }
        return convertView;
    }
}
