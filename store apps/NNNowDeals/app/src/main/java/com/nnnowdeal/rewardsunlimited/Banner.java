package com.nnnowdeal.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

class Banner
{
    //Fields in JSON response coming from server for Banners
    @SerializedName("image_url")
    String imageUrl;
    @SerializedName("link_type")
    String linkType;
    @SerializedName("redirect_url")
    String redirectUrl;
    int status;
    String store;
}
