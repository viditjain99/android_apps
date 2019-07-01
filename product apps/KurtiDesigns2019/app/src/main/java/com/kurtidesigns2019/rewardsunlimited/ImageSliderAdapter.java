package com.kurtidesigns2019.rewardsunlimited;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderViewHolder>
{
    private Context context;
    private ArrayList<Product> products;
    private LayoutInflater inflater;

    public ImageSliderAdapter(Context context,ArrayList<Product> products)
    {
        this.context=context;
        this.products=products;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageSliderLayout=inflater.inflate(R.layout.image_slider_layout,viewGroup,false);
        return new ImageSliderViewHolder(imageSliderLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder imageSliderViewHolder, int i)
    {
        Picasso.with(context).load(products.get(i).images).placeholder(R.drawable.placeholder).into(imageSliderViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
