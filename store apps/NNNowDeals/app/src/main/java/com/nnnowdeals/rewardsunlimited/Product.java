package com.nnnowdeals.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

public class Product implements Comparable<Product>
{
    @SerializedName("Brand")
    String brand;
    @SerializedName("CategoryCashbackID")
    String categoryCashbackId;
    @SerializedName("CategoryURLProductURL")
    String categoryUrlProductUrl;
    @SerializedName("DiscountPercentage")
    String discountPercentage;
    @SerializedName("DiscountThreshold")
    String discountThreshold;
    @SerializedName("DiscountedPrice")
    String discountedPrice;
    @SerializedName("EKCategory")
    String eKCategory;
    @SerializedName("Images")
    String images;
    @SerializedName("MRP")
    String mrp;
    @SerializedName("ProductDetails")
    String productDetails;
    @SerializedName("ProductName")
    String productName;
    @SerializedName("ProductURL")
    String productUrl;
    @SerializedName("RetailerCategory")
    String retailerCategory;
    @SerializedName("RetailerName")
    String retailerName;
    @SerializedName("Type")
    String type;
    String id;
    @SerializedName("is_in_stock")
    String isInStock;
    String status;

    @Override
    public int compareTo(Product o)
    {
        float result=Float.parseFloat(this.discountedPrice)-Float.parseFloat(o.discountedPrice);
        int r=(int) result;
        return r;
    }
}
