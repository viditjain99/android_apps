package com.kurtidesigns2019.rewardsunlimited;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    Button buyNowButton,shareButton;
    RecyclerView imageRecyclerView;
    ProgressBar progressBar;
    LinearLayout buttonsLinearLayout;
    ArrayList<Product> masterProductsArrayList=new ArrayList<>();
    ImageSliderAdapter adapter;
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;
    ConstraintLayout constraintLayout;
    TextView zoomTextView;
    int currentIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buyNowButton=findViewById(R.id.buyNowButton);
        shareButton=findViewById(R.id.shareButton);
        buttonsLinearLayout=findViewById(R.id.buttonLinearLayout);
        imageRecyclerView=findViewById(R.id.imageRecyclerView);
        progressBar=findViewById(R.id.loading);
        constraintLayout=findViewById(R.id.constraintLayout);
        zoomTextView=findViewById(R.id.zoomTextView);

        buttonsLinearLayout.setVisibility(View.GONE);
        imageRecyclerView.setVisibility(View.GONE);
        zoomTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        adapter=new ImageSliderAdapter(this,masterProductsArrayList);
        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        imageRecyclerView.setLayoutManager(manager);
        imageRecyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper=new PagerSnapHelper();
        snapHelper.attachToRecyclerView(imageRecyclerView);

        imageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                final int offset=imageRecyclerView.computeHorizontalScrollOffset();
                int cellWidth=recyclerView.getChildAt(0).getMeasuredWidth();
                if(offset%cellWidth==0)
                {
                    currentIndex=offset/cellWidth;
                }
            }
        });

        buyNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this,"Please wait...",Toast.LENGTH_SHORT).show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(masterProductsArrayList.get(currentIndex).productUrl,"KURTDES");
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        LinkResponse linkResponse=response.body();
                        if(linkResponse!=null)
                        {
                            String link=linkResponse.link;
                            intentBuilder=new CustomTabsIntent.Builder();
                            customTabsIntent=intentBuilder.build();
                            customTabsIntent.intent.setPackage("com.android.chrome");
                            intentBuilder.setStartAnimations(MainActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                            intentBuilder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                            intentBuilder.setShowTitle(true);
                            intentBuilder.addDefaultShareMenuItem();
                            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(link));
                        }
                        else
                        {
                            call.clone().enqueue(this);
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        Toast.makeText(MainActivity.this,"Network error",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this,"Please wait...",Toast.LENGTH_SHORT).show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(masterProductsArrayList.get(currentIndex).productUrl,"KURTDES");
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        LinkResponse linkResponse=response.body();
                        if(linkResponse!=null)
                        {
                            String link=linkResponse.link;
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,"Check out this deal at "+link);
                            intent.setType("text/plain");
                            startActivity(intent);
                        }
                        else
                        {
                            call.clone().enqueue(this);
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        Toast.makeText(MainActivity.this,"Network error",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Call<ProductsResponse> call=ApiClient.getProductsService().getProducts("application/json",getString(R.string.product),"category");
        call.enqueue(new Callback<ProductsResponse>()
        {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response)
            {
                ProductsResponse productsResponse=response.body();
                if(productsResponse!=null)
                {
                    ArrayList<Product> allProducts=productsResponse.products;
                    for(int i=0;i<allProducts.size();i++)
                    {
                        if(!allProducts.get(i).isInStock.equals("0"))
                        {
                            if(!allProducts.get(i).imageUrl1.equals("null"))
                            {
                                allProducts.get(i).images=allProducts.get(i).imageUrl1;
                            }
                            else if(!allProducts.get(i).imageUrl2.equals("null"))
                            {
                                allProducts.get(i).images=allProducts.get(i).imageUrl2;
                            }
                            else if(!allProducts.get(i).imageUrl3.equals("null"))
                            {
                                allProducts.get(i).images=allProducts.get(i).imageUrl3;
                            }
                            else if(!allProducts.get(i).imageUrl4.equals("null"))
                            {
                                allProducts.get(i).images=allProducts.get(i).imageUrl4;
                            }
                            masterProductsArrayList.add(allProducts.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    buttonsLinearLayout.setVisibility(View.VISIBLE);
                    imageRecyclerView.setVisibility(View.VISIBLE);
                    zoomTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    call.clone().enqueue(this);
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t)
            {
                call.clone().enqueue(this);
            }
        });
    }
}
