package com.tshirts.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RetailersResponse
{
    @SerializedName("products")
    ArrayList<Retailer> retailerArrayList;
}
