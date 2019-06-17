package com.sareedeals.rewardsunlimited;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AllRetailersListViewAdapter extends BaseAdapter
{
    private ArrayList<Retailer> retailersArrayList;  //list of all brands
    private LayoutInflater inflater;   //object used to inflate layout

    public AllRetailersListViewAdapter(Context context, ArrayList<Retailer> retailersArrayList)
    {
        this.retailersArrayList=retailersArrayList;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return retailersArrayList.size();
    }  //number of categories

    @Override
    public Object getItem(int position)
    {
        return retailersArrayList.get(position);
    }  //category at index position

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View output=convertView;
        if(output==null)  //if layout to be built is null it is built inside this loop
        {
            output=inflater.inflate(R.layout.all_categories_row_layout,parent,false);  //inflating the layout
            TextView categoryNameTextView=output.findViewById(R.id.categoryLabelTextView);
            AllRetailersViewHolder viewHolder=new AllRetailersViewHolder();
            categoryNameTextView.setText(retailersArrayList.get(position).nameInTitle);         //setting category name in textview
            viewHolder.brandNameTextView=categoryNameTextView;
            output.setTag(viewHolder);              //attaching a tag to output so that it can be identified easily the next time it has to be displayed
        }
        AllRetailersViewHolder viewHolder=(AllRetailersViewHolder) output.getTag();     //getting the tag if row has already been inflated
        viewHolder.brandNameTextView.setText(retailersArrayList.get(position).nameInTitle);    //setting the brand name
        return output;
    }
}
