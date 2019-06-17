package com.kurtis.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetailersService
{
    @FormUrlEncoded
    @POST("categories")
    Call<RetailersResponse> getRetailers(@Field("Content-type") String contentType, @Field("retailer") String retailer);
}
