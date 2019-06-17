package com.tshirts.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BannersResponse
{
    //List of banners coming from server
    @SerializedName("Banners")
    ArrayList<Banner> banners;
}
