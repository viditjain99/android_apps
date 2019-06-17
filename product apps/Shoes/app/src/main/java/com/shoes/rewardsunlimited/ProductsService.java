package com.shoes.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ProductsService
{
    @FormUrlEncoded
    @POST("products")
    Call<ProductsResponse> getProducts(@Field("Content-type") String contentType, @Field("ProductCategory") String productCategory, @Field("MODE") String mode);
}
