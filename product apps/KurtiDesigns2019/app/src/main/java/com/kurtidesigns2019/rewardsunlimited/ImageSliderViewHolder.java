package com.kurtidesigns2019.rewardsunlimited;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jsibbold.zoomage.ZoomageView;

public class ImageSliderViewHolder extends RecyclerView.ViewHolder
{
    ZoomageView imageView;
    public ImageSliderViewHolder(@NonNull View itemView)
    {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageView);
    }
}
