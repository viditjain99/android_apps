package com.flipkartdeals.rewardsunlimited;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class AdsViewHolder extends RecyclerView.ViewHolder
{
    //container for placing the ad
    CardView adCardView;
    public AdsViewHolder(@NonNull View itemView)
    {
        super(itemView);
        adCardView=itemView.findViewById(R.id.adCardView);
    }
}
