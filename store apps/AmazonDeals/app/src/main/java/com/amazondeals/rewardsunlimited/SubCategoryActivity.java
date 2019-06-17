package com.amazondeals.rewardsunlimited;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity
{

    ExpandableListView subCategoryListView;
    ArrayList<String> categoriesArrayList;
    HashMap<String, ArrayList<String>> subCategories;
    ExpandableListViewAdapter adapter;
    String parentCategoryName;
    FrameLayout frameLayout;
    LinearLayout animationView,errorLayout;
    TextView messageTextView;
    ProgressBar loadingView;
    Button retryButton;
    ImageView notFoundImageView;
    ImageView adImage;
    int retryCount=0;
    ArrayList<Ad> menuAds=new ArrayList<>();
    CustomTabsIntent.Builder intentBuilder;
    CustomTabsIntent customTabsIntent;
    boolean adAvailable=false;
    int pos;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        parentCategoryName=intent.getStringExtra("CATEGORY_NAME");
        mode=intent.getIntExtra("MODE",-1);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+parentCategoryName+"</font>"));
        subCategoryListView=findViewById(R.id.subCategoryListView);
        frameLayout=findViewById(R.id.frameLayout);
        animationView=findViewById(R.id.animationView);
        notFoundImageView=findViewById(R.id.notFoundImageView);
        categoriesArrayList=new ArrayList<>();
        loadingView=findViewById(R.id.loadingView);
        messageTextView=findViewById(R.id.messageTextView);
        errorLayout=findViewById(R.id.errorLayout);
        retryButton=findViewById(R.id.retryButton);
        adImage=findViewById(R.id.adImage);

        subCategories=new HashMap<>();
        adapter=new ExpandableListViewAdapter(SubCategoryActivity.this,categoriesArrayList,subCategories);
        subCategoryListView.setAdapter(adapter);
        subCategoryListView.setVisibility(View.GONE);
        animationView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        adImage.setVisibility(View.GONE);
        subCategoryListView.bringToFront();
        loadingView.setVisibility(View.VISIBLE);

        MobileAds.initialize(this,"ca-app-pub-5162888426019346~5423311055");
        loadData();

        subCategoryListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                setListViewHeight(parent,groupPosition);
                return false;
            }
        });
        subCategoryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                String subCategoryName=categoriesArrayList.get(groupPosition);
                String searchKey=parentCategoryName+"->"+subCategoryName+"->"+subCategories.get(subCategoryName).get(childPosition);
                Intent intent1=new Intent(SubCategoryActivity.this,ProductDealsActivity.class);
                intent1.putExtra("SEARCH_KEY",searchKey);
                intent1.putExtra("MODE",mode);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                return true;
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loadData();
            }
        });

        adImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v.getId()==R.id.adImage)
                {
                    if(adAvailable)
                    {
                        intentBuilder=new CustomTabsIntent.Builder();
                        customTabsIntent=intentBuilder.build();
                        customTabsIntent.intent.setPackage("com.android.chrome");
                        intentBuilder.setStartAnimations(SubCategoryActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                        intentBuilder.setToolbarColor(ContextCompat.getColor(SubCategoryActivity.this, R.color.black));
                        intentBuilder.setShowTitle(true);
                        intentBuilder.addDefaultShareMenuItem();
                        customTabsIntent.launchUrl(SubCategoryActivity.this, Uri.parse(menuAds.get(pos).redirectUrl));
                    }
                }
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData()
    {
        errorLayout.setVisibility(View.GONE);
        Call<ProductsResponse> call=ApiClient.getProductsService().getProducts("application/json",getString(R.string.store_name),parentCategoryName);
        call.enqueue(new Callback<ProductsResponse>()
        {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response)
            {
                ProductsResponse productsResponse=response.body();
                if(productsResponse==null)
                {
                    if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                    {
                        Log.d("Request Failed","failed");
                        retryCount++;
                        call.clone().enqueue(this);
                    }
                    else
                    {
                        subCategoryListView.setVisibility(View.GONE);
                        animationView.setVisibility(View.GONE);
                        notFoundImageView.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                        adImage.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    ArrayList<Product> products=productsResponse.products;
                    if(products.size()==0)
                    {
                        animationView.setVisibility(View.VISIBLE);
                        subCategoryListView.setVisibility(View.GONE);
                        loadingView.setVisibility(View.GONE);
                        adImage.setVisibility(View.GONE);
                        messageTextView.setText(messageTextView.getText()+parentCategoryName);
                    }
                    else
                    {
                        HashSet<String> categoriesHashSet=new HashSet<>();
                        for(int i=0;i<products.size();i++)
                        {
                            String subCategoryString=products.get(i).retailerCategory;
                            String[] subCategory=subCategoryString.split("->");
                            categoriesHashSet.add(subCategory[1]);
                        }
                        categoriesArrayList.addAll(categoriesHashSet);
                        Iterator iterator=categoriesHashSet.iterator();
                        while(iterator.hasNext())
                        {
                            String category=(String)iterator.next();
                            for(int i=0;i<products.size();i++)
                            {
                                if(!subCategories.containsKey(category))
                                {
                                    subCategories.put(category,new ArrayList<String>());
                                }
                                else
                                {
                                    Product product=products.get(i);
                                    String[] c=product.retailerCategory.split("->");
                                    if(c.length>=2)
                                    {
                                        String subCategory=c[2];
                                        if(c[1].equals(category))
                                        {
                                            if(!subCategories.get(category).contains(subCategory))
                                            {
                                                subCategories.get(category).add(subCategory);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        subCategoryListView.expandGroup(0);
                        loadingView.setVisibility(View.GONE);
                        animationView.setVisibility(View.GONE);
                        subCategoryListView.setVisibility(View.VISIBLE);
                        adImage.setVisibility(View.VISIBLE);
                        retryCount=0;
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t)
            {
                if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                {
                    Log.d("Request Failed","failed ");
                    retryCount++;
                    call.clone().enqueue(this);
                }
                else
                {
                    subCategoryListView.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);
                    adImage.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        Call<AdsResponse> call1=ApiClient.getAdsService().getAds();
        call1.enqueue(new Callback<AdsResponse>()
        {
            @Override
            public void onResponse(Call<AdsResponse> call, Response<AdsResponse> response)
            {
                AdsResponse adsResponse=response.body();
                if(adsResponse!=null)
                {
                    ArrayList<Ad> ads=adsResponse.adsArrayList;
                    for(int i=0;i<ads.size();i++)
                    {
                        if(ads.get(i).position.equals("menu") && ads.get(i).store.equals(getString(R.string.store_name).toLowerCase()))
                        {
                            menuAds.add(ads.get(i));
                        }
                    }
                    Random random=new Random();
                    pos=random.nextInt(menuAds.size());

                    Picasso.with(SubCategoryActivity.this).load(menuAds.get(pos).imageUrl).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).priority(Picasso.Priority.HIGH).into(adImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            adAvailable=true;
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AdsResponse> call, Throwable t) {

            }
        });
    }

    private void setListViewHeight(ExpandableListView listView, int group)
    {
        int totalHeight=0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            View groupItem = adapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight+=groupItem.getMeasuredHeight();

            if(((listView.isGroupExpanded(i)) && (i != group)) || ((!listView.isGroupExpanded(i)) && (i == group)))
            {
                for (int j = 0; j < adapter.getChildrenCount(i); j++) {
                    View listItem = adapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (adapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
}
