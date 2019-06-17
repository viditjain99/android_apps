package com.amazondeals.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdsResponse
{
    //list of ads in JSON response
    @SerializedName("CkAds")
    ArrayList<Ad> adsArrayList;
}
