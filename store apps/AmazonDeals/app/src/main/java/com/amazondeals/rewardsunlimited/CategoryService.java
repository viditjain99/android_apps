package com.amazondeals.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CategoryService
{
    //POST request function for getting categories from server
    @FormUrlEncoded
    @POST("categories")
    Call<CategoriesResponse> getCategories(@Field("retailer") String retailer);
}
