package com.shoes.rewardsunlimited;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private static Retrofit retrofit;  //Retrofit is a REST client library
    private static ProductsService productsService;   //object to use retrofit for getting products
    private static CarouselService carouselService;
    private static LinkService linkService;
    private static AdsService adsService;
    private static RetailersService retailersService;
    static Retrofit getInstance()  //function to get retrofit instance
    {
        if(retrofit==null)
        {
            Retrofit.Builder builder=new Retrofit.Builder().baseUrl("http://35.200.143.159/").addConverterFactory(GsonConverterFactory.create());
            retrofit=builder.build();
        }
        return retrofit;
    }

    static ProductsService getProductsService()
    {
        if(productsService==null)
        {
            productsService=getInstance().create(ProductsService.class);
        }
        return productsService;
    }

    static CarouselService getCarouselService()
    {
        if(carouselService==null)
        {
            carouselService=getInstance().create(CarouselService.class);
        }
        return carouselService;
    }

    static LinkService getLinkService()
    {
        if(linkService==null)
        {
            linkService=getInstance().create(LinkService.class);
        }
        return linkService;
    }

    static AdsService getAdsService()
    {
        if(adsService==null)
        {
            adsService=getInstance().create(AdsService.class);
        }
        return adsService;
    }

    static RetailersService getRetailersService()
    {
        if(retailersService==null)
        {
            retailersService=getInstance().create(RetailersService.class);
        }
        return retailersService;
    }
}
