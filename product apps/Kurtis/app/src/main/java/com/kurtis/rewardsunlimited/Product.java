package com.kurtis.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


class Product implements Serializable
{
    String brand;
    String discountPercentage;
    @SerializedName("discounted_price")
    String discountedPrice;
    @SerializedName("image_URL1")
    String imageUrl1;
    @SerializedName("image_URL2")
    String imageUrl2;
    @SerializedName("image_URL3")
    String imageUrl3;
    @SerializedName("image_URL4")
    String imageUrl4;
    String images="";
    @SerializedName("original_price")
    String mrp;
    @SerializedName("product_highlights_app")
    String productDetails;
    @SerializedName("product_name")
    String productName;
    @SerializedName("product_url")
    String productUrl;
    @SerializedName("seller_category")
    String retailerCategory;
    @SerializedName("seller")
    String retailerName;
    @SerializedName("product_id")
    String id;
    @SerializedName("availability")
    String isInStock;
    @SerializedName("popularity")
    int sortOrder;
}
