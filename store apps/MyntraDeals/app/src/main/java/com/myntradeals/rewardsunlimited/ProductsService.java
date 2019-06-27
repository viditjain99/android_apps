package com.myntradeals.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ProductsService
{
    @FormUrlEncoded
    @POST("productsnew")
    Call<ProductsResponse> getProducts(@Field("Content-type") String contentType, @Field("Retailer") String retailer, @Field("ProductCategory") String productCategory);
}
