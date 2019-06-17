package com.flipkartdeals.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BannersResponse
{
    //List of bannners coming from server
    @SerializedName("Banners")
    ArrayList<Banner> banners;
}
