package com.kurtidesigns2019.rewardsunlimited;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private static Retrofit retrofit;
    private static ProductsService productsService;
    private  static LinkService linkService;

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

    static LinkService getLinkService()
    {
        if(linkService==null)
        {
            linkService=getInstance().create(LinkService.class);
        }
        return linkService;
    }
}
