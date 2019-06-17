package com.ajiodeals.rewardsunlimited;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class BrandsListViewAdapter extends BaseAdapter
{
    private ArrayList<BrandFilter> brands;  //list of brands
    private LayoutInflater inflater;   //object to inflate layout
    private BrandFilterClickListener listener;  //listener to listen for clicks on checkbox of brand

    BrandsListViewAdapter(Context context, ArrayList<BrandFilter> brands, BrandFilterClickListener listener)
    {
        this.brands=brands;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener=listener;
    }
    @Override
    public int getCount()
    {
        return brands.size();
    }   //number of brands

    @Override
    public Object getItem(int position)
    {
        return brands.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private boolean isChecked(int position)   //returns true if brand at index position is checked
    {
        return brands.get(position).checked;
    }

    public void uncheckAll()   //function to un-select all brands
    {
        for(int i=0;i<brands.size();i++)
        {
            if(isChecked(i))
            {
                brands.get(i).checked=false;
            }
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View output=convertView;
        if(output==null)    //if layout to be built is null it is built inside this loop
        {
            output=inflater.inflate(R.layout.brands_listview_row_layout,parent,false);   //inflating the layout
            BrandsViewHolder viewHolder=new BrandsViewHolder();
            viewHolder.brandNameTextView=output.findViewById(R.id.brandNameTextView);
            viewHolder.checkBox=output.findViewById(R.id.checkBox);
            output.setTag(viewHolder);
        }
        final BrandsViewHolder viewHolder=(BrandsViewHolder) output.getTag();
        viewHolder.brandNameTextView.setText(brands.get(position).brandNameInTitle);   //setting brand name
        viewHolder.checkBox.setChecked(brands.get(position).checked);           //setting checked state of checkbox
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener()        //setting click listener on check box
        {
            @Override
            public void onClick(View v)
            {
                listener.onBrandClick(brands.get(position));           //defines work to be done
                brands.get(position).checked=!brands.get(position).isChecked();    //inverting the checked state of checkbox
            }
        });
        viewHolder.checkBox.setChecked(isChecked(position));     //setting checked state of checkbox depending on current value returned from isChecked(position) function
        return output;
    }
}