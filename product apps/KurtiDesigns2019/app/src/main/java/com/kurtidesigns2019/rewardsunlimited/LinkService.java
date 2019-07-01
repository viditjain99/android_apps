package com.kurtidesigns2019.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LinkService
{
    @GET("http://dealsunlimited.in/api/linkgen.php")
    Call<LinkResponse> getLink(@Query("url") String url, @Query("subtag") String subtag);
}
