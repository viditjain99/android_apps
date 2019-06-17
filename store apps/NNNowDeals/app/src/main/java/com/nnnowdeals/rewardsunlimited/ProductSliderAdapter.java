package com.nnnowdeals.rewardsunlimited;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductSliderAdapter extends PagerAdapter
{
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Banner> productSliderObjects;
    private ProductSliderClickListener listener;

    public ProductSliderAdapter(Context context,ArrayList<Banner> productSliderImages,ProductSliderClickListener listener)
    {
        this.context=context;
        this.productSliderObjects=productSliderImages;
        this.listener=listener;
    }
    @Override
    public int getCount()
    {
        return productSliderObjects.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view==o;
    }

    @Override
    public Object instantiateItem(ViewGroup container,final int position)
    {
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.product_slider_row_layout,null);
        ImageView imageView=view.findViewById(R.id.productSliderObjectImageView);
        Picasso.with(context).load(productSliderObjects.get(position).imageUrl).into(imageView);
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onProductSliderObjectClick(productSliderObjects.get(position));
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object)
    {
        ViewPager viewPager=(ViewPager) container;
        View view=(View) object;
        viewPager.removeView(view);
    }
}
