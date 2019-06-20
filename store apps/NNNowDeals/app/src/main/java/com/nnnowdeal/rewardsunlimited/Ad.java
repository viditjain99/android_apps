package com.nnnowdeal.rewardsunlimited;

import com.google.gson.annotations.SerializedName;

public class Ad
{
    //Fields in JSON response coming from server for CK ads
    int status;
    @SerializedName("image_url")  //renaming field for convenience
    String imageUrl;
    @SerializedName("redirect_url")
    String redirectUrl;
    String position;
    String store;
}
