package com.nnnowdeal.rewardsunlimited;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ProductDetailsViewPagerAdapter extends PagerAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private String[] imageUrls;

    public ProductDetailsViewPagerAdapter(Context context,String[] imageUrls)
    {
        this.context=context;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageUrls=imageUrls;
    }
    @Override
    public int getCount()
    {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.product_details_view_pager_row_layout,null);
        ImageView imageView=view.findViewById(R.id.productImageView);
        Picasso.with(context).load(imageUrls[position]).into(imageView);
        ViewPager viewPager=(ViewPager) container;
        viewPager.addView(view,0);
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
