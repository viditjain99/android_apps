package com.nnnowdeal.rewardsunlimited;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CarouselService
{
    //GET request function for getting banners
    @GET("banners")
    Call<BannersResponse> getBanners();
}
