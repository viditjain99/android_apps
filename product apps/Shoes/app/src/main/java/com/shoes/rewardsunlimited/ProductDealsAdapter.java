package com.shoes.rewardsunlimited;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProductDealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private ArrayList<Object> productDealsWithAdsArrayList;
    private Context context;
    private ProductDealsClickListener listener;
    private int AD_ITEM=1,DEAL_ITEM=2,CK_AD_ITEM=3;

    public ProductDealsAdapter(ArrayList<Object> productDealsWithAdsArrayList, Context context, ProductDealsClickListener listener)
    {
        this.productDealsWithAdsArrayList=productDealsWithAdsArrayList;
        this.context=context;
        this.listener=listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        if(i==AD_ITEM || i==CK_AD_ITEM)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View adsLayout=inflater.inflate(R.layout.ad_container,viewGroup,false);
            return new AdsViewHolder(adsLayout);
        }
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowLayout=inflater.inflate(R.layout.product_deals_row_layout,viewGroup,false);
        return new ProductDealsViewHolder(rowLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i)
    {
        if(getItemViewType(i)==AD_ITEM)
        {
            AdsViewHolder adsViewHolder=(AdsViewHolder) viewHolder;
            ViewGroup adCardView=(ViewGroup) adsViewHolder.adCardView;
            AdView adView=(AdView) productDealsWithAdsArrayList.get(i);
            if(adCardView.getChildCount()>0)
            {
                adCardView.removeAllViews();
            }
            if(adView.getParent()!=null)
            {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }
            adCardView.addView(adView);
            adView.loadAd(new AdRequest.Builder().build());
        }
        else if(getItemViewType(i)==DEAL_ITEM)
        {
            Product productDeal=(Product) productDealsWithAdsArrayList.get(i);
            if(!productDeal.isInStock.equals("0"))
            {
                final ProductDealsViewHolder productDealsViewHolder=(ProductDealsViewHolder) viewHolder;
                String[] imageUrls=productDeal.images.split(Pattern.quote(" || "));
                DisplayMetrics metrics=context.getResources().getDisplayMetrics();
                int width=metrics.widthPixels;
                productDealsViewHolder.relativeLayout.getLayoutParams().width=((width/2));
                if(!imageUrls[0].equals(""))
                {
                    Picasso.with(context).load(imageUrls[0]).error(R.drawable.placeholder).into(productDealsViewHolder.productDealImageView);
                }
                else
                {
                    productDealsViewHolder.productDealImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder));
                }
                productDealsViewHolder.productDealNameTextView.setText(productDeal.productName);
                productDealsViewHolder.productDealMrpTextView.setText("₹"+productDeal.mrp);
                productDealsViewHolder.productDealMrpTextView.setPaintFlags(productDealsViewHolder.productDealMrpTextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                productDealsViewHolder.productDealOfferPriceTextView.setText("₹"+productDeal.discountedPrice);

                productDealsViewHolder.buyNowButton.setBackgroundColor(Color.parseColor("#868A65"));
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

                if(ProductDealsActivity.dealsToBeOpened)
                {
                    productDealsViewHolder.itemView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            listener.onDealClick(productDealsViewHolder.getAdapterPosition());
                        }
                    });
                }
            }
        }
        else if(getItemViewType(i)==CK_AD_ITEM)
        {
            AdsViewHolder adsViewHolder=(AdsViewHolder) viewHolder;
            ViewGroup adCardView=(ViewGroup) adsViewHolder.adCardView;
            ImageView adImageView=(ImageView) productDealsWithAdsArrayList.get(i);
            if(adCardView.getChildCount()>0)
            {
                adCardView.removeAllViews();
            }
            if(adImageView.getParent()!=null)
            {
                ((ViewGroup) adImageView.getParent()).removeView(adImageView);
            }
            adCardView.addView(adImageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return productDealsWithAdsArrayList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(productDealsWithAdsArrayList.get(position) instanceof AdView)
        {
            return AD_ITEM;
        }
        else if(productDealsWithAdsArrayList.get(position) instanceof ImageView)
        {
            return CK_AD_ITEM;
        }
        return DEAL_ITEM;
    }
}
