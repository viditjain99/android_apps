package com.sareedeals.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LinkService
{
    //GET request for getting link with affiliate id
    @GET("http://dealsunlimited.in/api/linkgen.php")
    Call<LinkResponse> getLink(@Query("url") String url, @Query("subtag") String subtag);
}
