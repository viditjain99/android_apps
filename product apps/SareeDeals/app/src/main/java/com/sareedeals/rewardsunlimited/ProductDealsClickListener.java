package com.sareedeals.rewardsunlimited;

import android.view.View;

public interface ProductDealsClickListener
{
    void onBuyNowClick(View view, int position);
    void onShareClick(int position);
    void onDealClick(int position);
}
