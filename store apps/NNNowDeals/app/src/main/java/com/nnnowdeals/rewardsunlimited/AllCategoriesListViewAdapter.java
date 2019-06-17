package com.nnnowdeals.rewardsunlimited;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AllCategoriesListViewAdapter extends BaseAdapter
{
    private ArrayList<Category> categoryArrayList;  //list of all categories
    private LayoutInflater inflater;   //object used to inflate layout

    public AllCategoriesListViewAdapter(Context context,ArrayList<Category> categoryArrayList)
    {
        this.categoryArrayList=categoryArrayList;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return categoryArrayList.size();
    }  //number of categories

    @Override
    public Object getItem(int position)
    {
        return categoryArrayList.get(position);
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
            AllCategoriesViewHolder viewHolder=new AllCategoriesViewHolder();
            categoryNameTextView.setText(categoryArrayList.get(position).name);         //setting category name in textview
            viewHolder.categoryNameTextView=categoryNameTextView;
            output.setTag(viewHolder);              //attaching a tag to output so that it can be identified easily the next time it has to be displayed
        }
        AllCategoriesViewHolder viewHolder=(AllCategoriesViewHolder) output.getTag();     //getting the tag if row has already been inflated
        viewHolder.categoryNameTextView.setText(categoryArrayList.get(position).name);    //setting the category name
        return output;
    }
}
