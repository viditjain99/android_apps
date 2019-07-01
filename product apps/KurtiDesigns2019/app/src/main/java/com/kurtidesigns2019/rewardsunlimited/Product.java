package com.kurtidesigns2019.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

public class Product
{
    @SerializedName("image_URL1")
    String imageUrl1;
    @SerializedName("image_URL2")
    String imageUrl2;
    @SerializedName("image_URL3")
    String imageUrl3;
    @SerializedName("image_URL4")
    String imageUrl4;
    String images="";
    @SerializedName("product_url")
    String productUrl;
    @SerializedName("availability")
    String isInStock;
    @SerializedName("popularity")
    int sortOrder;
}
