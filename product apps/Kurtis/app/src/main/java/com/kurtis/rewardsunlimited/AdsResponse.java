package com.kurtis.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdsResponse
{
    //list of ads in JSON response
    @SerializedName("CkAds")
    ArrayList<Ad> adsArrayList;
}