package com.tshirts.rewardsunlimited;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ProductDealsViewHolder extends RecyclerView.ViewHolder
{
    ImageView productDealImageView;
    TextView productDealMrpTextView,productDealOfferPriceTextView,productDealDescriptionTextView;
    StringLimiterTextView productDealNameTextView;
    Button buyNowButton,shareButton;
    RelativeLayout relativeLayout;
    ProductDealsViewHolder(@NonNull View itemView)
    {
        super(itemView);
        productDealImageView=itemView.findViewById(R.id.productDealImageView);
        productDealNameTextView=itemView.findViewById(R.id.productDealNameTextView);
        productDealMrpTextView=itemView.findViewById(R.id.productDealMrpTextView);
        productDealOfferPriceTextView=itemView.findViewById(R.id.productDealOfferPriceTextView);
        productDealDescriptionTextView=itemView.findViewById(R.id.productDealDescriptionTextView);
        buyNowButton=itemView.findViewById(R.id.buyNowButton);
        shareButton=itemView.findViewById(R.id.shareButton);
        relativeLayout=itemView.findViewById(R.id.relativeLayout);
    }
}
