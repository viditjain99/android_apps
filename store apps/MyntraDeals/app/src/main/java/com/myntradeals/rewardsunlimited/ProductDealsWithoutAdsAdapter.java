package com.myntradeals.rewardsunlimited;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProductDealsWithoutAdsAdapter extends RecyclerView.Adapter<ProductDealsViewHolder>
{
    private ArrayList<Product> productDealsArrayList;
    private Context context;
    private ProductDealsClickListener listener;

    public ProductDealsWithoutAdsAdapter(ArrayList<Product> productDealsArrayList,Context context,ProductDealsClickListener listener)
    {
        this.productDealsArrayList=productDealsArrayList;
        this.context=context;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ProductDealsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowLayout=inflater.inflate(R.layout.product_deals_row_layout,viewGroup,false);
        return new ProductDealsViewHolder(rowLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductDealsViewHolder productDealsViewHolder, int i)
    {
        Product productDeal=(Product) productDealsArrayList.get(i);
        if(productDeal.isInStock.equals("1"))
        {
            String[] imageUrls=productDeal.images.split(Pattern.quote(" || "));
            DisplayMetrics metrics=context.getResources().getDisplayMetrics();
            int width=metrics.widthPixels;
            productDealsViewHolder.relativeLayout.getLayoutParams().width=((width/2));
            Picasso.with(context).load(imageUrls[0]).error(R.drawable.placeholder).into(productDealsViewHolder.productDealImageView);
            productDealsViewHolder.productDealNameTextView.setText(productDeal.productName);
            productDealsViewHolder.productDealMrpTextView.setText("₹"+productDeal.mrp);
            productDealsViewHolder.productDealMrpTextView.setPaintFlags(productDealsViewHolder.productDealMrpTextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            productDealsViewHolder.productDealOfferPriceTextView.setText("₹"+productDeal.discountedPrice);

            productDealsViewHolder.buyNowButton.setBackgroundColor(Color.parseColor("#fb002b"));
            productDealsViewHolder.shareButton.setBackgroundColor(Color.parseColor("#010101"));
            productDealsViewHolder.buyNowButton.setTextColor(Color.parseColor("#ffffff"));
            productDealsViewHolder.shareButton.setTextColor(Color.parseColor("#ffffff"));
            productDealsViewHolder.buyNowButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onBuyNowClick(view,productDealsViewHolder.getAdapterPosition());
                }
            });
            productDealsViewHolder.shareButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onShareClick(productDealsViewHolder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return productDealsArrayList.size();
    }
}
