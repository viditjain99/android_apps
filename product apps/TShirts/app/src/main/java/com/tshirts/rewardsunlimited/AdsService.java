package com.tshirts.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AdsService
{
    //GET request function for getting CK ads
    @GET("ckads")
    Call<AdsResponse> getAds();
}
