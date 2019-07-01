package com.flipkartdeals.rewardsunlimited;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDealsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    String searchKeyString;
    RecyclerView productDealsRecyclerView;
    ProductDealsAdapter adapter,adapterWithFilter;
    ArrayList<Object> productDealsWithAdsArrayList=new ArrayList<>();
    ArrayList<Object> tempProductDealsArrayList=new ArrayList<>();
    ArrayList<Product> masterProductDealsArrayList=new ArrayList<>();
    ArrayList<Product> auxiliarySortingArrayList=new ArrayList<>();
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;
    ProgressBar loadingView,filtersLoadingView;
    ImageView dealNotFoundImageView;
    ConstraintLayout container;
    Snackbar snackbar;
    int retryCount=0,checkBoxesSelected=0;
    TextView noDealsFoundLabelTextView,minPriceTextView,maxPriceTextView,priceFilterLabelTextView,brandsListViewLabelTextView,noDealsFoundTextViewNav;
    LinearLayout noDealsFoundLayout,errorLayout,filterPriceLayout,filterButtonsLayout;
    ArrayList<BrandFilter> brands=new ArrayList<>();
    Menu menu;
    NavigationView navigationView;
    BrandsListViewAdapter brandsListViewAdapter;
    ListView navDrawerBrandsListView;
    HashSet<String> selectedBrandsInFilter=new HashSet<>();
    CrystalRangeSeekbar priceSeekBar;
    float maxPrice;
    Button applyFilterButton,clearFilterButton,retryButton;
    Number selectedMinPrice,selectedMaxPrice;
    DrawerLayout drawer;
    boolean filterApplied=false;
    AlertDialog.Builder builder;
    AlertDialog sortByDialog;
    int sortByDialogOptionSelected=-1,itemsLoaded=0,sortByDialogOptionApplied=-1;
    String[] options={"Price: High to Low","Price: Low to High","Discount: High to Low","Discount: Low to High"};
    boolean moreDataAvailable=true,isLoading=false,noDealFound=false;
    int activeArrayList=0,WITHOUT_FILTER=0,WITH_FILTER=1;
    int productsInMainArrayList=0;
    int ITEMS_PER_AD=4;
    Random random=new Random();
    boolean ckAdsAvailable=true;
    ArrayList<Ad> adArrayList=new ArrayList<>();
    static boolean dealsToBeOpened=true;
    int mode;
    String[] searchKey;
    float price=0f;
    String adUnitId="ca-app-pub-5162888426019346/6925659053";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_deals);
        Window window=this.getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        mode=intent.getIntExtra("MODE",-1);
        searchKeyString=intent.getStringExtra("SEARCH_KEY");
        if(mode==0)
        {
            String[] search=searchKeyString.split(" ");
            price=Float.parseFloat(search[1]);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+searchKeyString+"</font>"));
        }
        else
        {
            searchKey=searchKeyString.split("->");
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+searchKey[searchKey.length-1]+ "</font>"));
        }
        productDealsRecyclerView=findViewById(R.id.productDealsRecyclerView);
        loadingView=findViewById(R.id.loadingView);
        dealNotFoundImageView=findViewById(R.id.dealNotFoundImageView);
        container=findViewById(R.id.constraintLayout);
        noDealsFoundLabelTextView=findViewById(R.id.noDealsFoundLabelTextView);
        noDealsFoundLayout=findViewById(R.id.noDealsFoundLayout);
        errorLayout=findViewById(R.id.errorLayout);
        navDrawerBrandsListView=findViewById(R.id.brandsListView);
        priceSeekBar=findViewById(R.id.priceSeekBar);
        minPriceTextView=findViewById(R.id.minPriceTextView);
        maxPriceTextView=findViewById(R.id.maxPriceTextView);
        applyFilterButton=findViewById(R.id.applyFiltersButton);
        clearFilterButton=findViewById(R.id.clearFilterButton);
        filterPriceLayout=findViewById(R.id.filterPriceLayout);
        priceFilterLabelTextView=findViewById(R.id.priceFilterLabelTextView);
        filtersLoadingView=findViewById(R.id.filtersLoadingView);
        brandsListViewLabelTextView=findViewById(R.id.brandsListViewLabelTextView);
        filterButtonsLayout=findViewById(R.id.filterButtonsLayout);
        noDealsFoundTextViewNav=findViewById(R.id.noDealsFoundTextView);
        retryButton=findViewById(R.id.retryButton);

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        menu=navigationView.getMenu();

        snackbar= Snackbar.make(container,"Please wait...", Snackbar.LENGTH_INDEFINITE);

        productDealsRecyclerView.setVisibility(View.GONE);
        dealNotFoundImageView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        noDealsFoundLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        filterPriceLayout.setVisibility(View.GONE);
        priceSeekBar.setVisibility(View.GONE);
        priceFilterLabelTextView.setVisibility(View.GONE);
        navDrawerBrandsListView.setVisibility(View.GONE);
        brandsListViewLabelTextView.setVisibility(View.GONE);
        filterButtonsLayout.setVisibility(View.GONE);
        noDealsFoundTextViewNav.setVisibility(View.GONE);
        filtersLoadingView.setVisibility(View.VISIBLE);

        MobileAds.initialize(this,"ca-app-pub-5162888426019346~5416124506");
        adapter=new ProductDealsAdapter(productDealsWithAdsArrayList, ProductDealsActivity.this, new ProductDealsClickListener()
        {
            @Override
            public void onBuyNowClick(View view, int position)
            {
                Product product=(Product) productDealsWithAdsArrayList.get(position);
                snackbar.setText("Please wait...");
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(product.productUrl, getString(R.string.subtag));
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        String url=response.body().link;
                        if(url!=null)
                        {
                            snackbar.dismiss();
                            intentBuilder=new CustomTabsIntent.Builder();
                            customTabsIntent=intentBuilder.build();
                            customTabsIntent.intent.setPackage("com.android.chrome");
                            intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                            intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                            intentBuilder.setShowTitle(true);
                            intentBuilder.addDefaultShareMenuItem();
                            customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(url));
                        }
                        else
                        {
                            snackbar.setText("Invalid Link");
                            snackbar.setDuration(1500);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        snackbar.setText(getString(R.string.error_string));
                        snackbar.setDuration(1500);
                        snackbar.show();
                    }
                });
            }

            @Override
            public void onShareClick(int position)
            {
                final Product product=(Product) productDealsWithAdsArrayList.get(position);
                snackbar.setText("Please wait...");
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(product.productUrl, getString(R.string.subtag));
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        String url=response.body().link;
                        if(url!=null)
                        {
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,"Check out this deal at "+url);
                            intent.setType("text/plain");
                            startActivity(intent);
                            snackbar.dismiss();
                        }
                        else
                        {
                            snackbar.setText("Something went wrong. Try again later");
                            snackbar.setDuration(1500);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        snackbar.setText(getString(R.string.error_string));
                        snackbar.setDuration(1500);
                        snackbar.show();
                    }
                });
            }

            @Override
            public void onDealClick(int position)
            {
                Product product=(Product) productDealsWithAdsArrayList.get(position);
                Intent intent=new Intent(ProductDealsActivity.this,ProductDealDetailsActivity.class);
                intent.putExtra("PRODUCT_NAME",product.productName);
                intent.putExtra("PRODUCT_DESC",product.productDetails);
                intent.putExtra("PRODUCT_BRAND",product.brand);
                intent.putExtra("PRODUCT_MRP",product.mrp);
                intent.putExtra("PRODUCT_OFFER_PRICE",product.discountedPrice);
                intent.putExtra("PRODUCT_PHOTOS",product.images);
                intent.putExtra("PRODUCT_URL",product.productUrl);
                intent.putExtra("DISCOUNT",product.discountPercentage);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        productDealsRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener()
        {
            @Override
            public void onLoadMore()
            {
                if(moreDataAvailable && !isLoading)
                {
                    addProductsToList();
                }
            }
        });

        adapterWithFilter=new ProductDealsAdapter(tempProductDealsArrayList, this, new ProductDealsClickListener()
        {
            @Override
            public void onBuyNowClick(View view, int position)
            {
                Product product=(Product) tempProductDealsArrayList.get(position);
                snackbar.setText("Please wait...");
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(product.productUrl, getString(R.string.subtag));
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        String url=response.body().link;
                        if(url!=null)
                        {
                            snackbar.dismiss();
                            intentBuilder=new CustomTabsIntent.Builder();
                            customTabsIntent=intentBuilder.build();
                            customTabsIntent.intent.setPackage("com.android.chrome");
                            intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                            intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                            intentBuilder.setShowTitle(true);
                            intentBuilder.addDefaultShareMenuItem();
                            customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(url));
                        }
                        else
                        {
                            snackbar.setText("Invalid Link");
                            snackbar.setDuration(1500);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        snackbar.setText(getString(R.string.error_string));
                        snackbar.setDuration(1500);
                        snackbar.show();
                    }
                });
            }

            @Override
            public void onShareClick(int position)
            {
                final Product product=(Product) tempProductDealsArrayList.get(position);
                snackbar.setText("Please wait...");
                snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(product.productUrl, getString(R.string.subtag));
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        String url=response.body().link;
                        if(url!=null)
                        {
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,"Check out this deal at "+url);
                            intent.setType("text/plain");
                            startActivity(intent);
                            snackbar.dismiss();
                        }
                        else
                        {
                            snackbar.setText("Something went wrong. Try again later");
                            snackbar.setDuration(1500);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        snackbar.setText(getString(R.string.error_string));
                        snackbar.setDuration(1500);
                        snackbar.show();
                    }
                });
            }

            @Override
            public void onDealClick(int position)
            {
                Product product=(Product) tempProductDealsArrayList.get(position);
                Intent intent=new Intent(ProductDealsActivity.this,ProductDealDetailsActivity.class);
                intent.putExtra("PRODUCT_NAME",product.productName);
                intent.putExtra("PRODUCT_DESC",product.productDetails);
                intent.putExtra("PRODUCT_BRAND",product.brand);
                intent.putExtra("PRODUCT_MRP",product.mrp);
                intent.putExtra("PRODUCT_OFFER_PRICE",product.discountedPrice);
                intent.putExtra("PRODUCT_PHOTOS",product.images);
                intent.putExtra("PRODUCT_URL",product.productUrl);
                intent.putExtra("DISCOUNT",product.discountPercentage);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        productDealsRecyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager=new GridLayoutManager(ProductDealsActivity.this,2, StaggeredGridLayoutManager.VERTICAL,false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize(int i)
            {
                int val=-1;
                if(activeArrayList==WITHOUT_FILTER)
                {
                    if(productDealsWithAdsArrayList.get(i) instanceof AdView || productDealsWithAdsArrayList.get(i) instanceof ImageView)
                    {
                        val=2;
                    }
                    else
                    {
                        val=1;
                    }
                }
                else if(activeArrayList==WITH_FILTER)
                {
                    if(tempProductDealsArrayList.get(i) instanceof AdView || tempProductDealsArrayList.get(i) instanceof ImageView)
                    {
                        val=2;
                    }
                    else
                    {
                        val=1;
                    }
                }
                return val;
            }
        });
        productDealsRecyclerView.setLayoutManager(layoutManager);

        brandsListViewAdapter=new BrandsListViewAdapter(ProductDealsActivity.this,brands,new BrandFilterClickListener()
        {
            @Override
            public void onBrandClick(BrandFilter brandFilter)
            {
                filterApplied=true;
                if(brandFilter.checked)
                {
                    selectedBrandsInFilter.remove(brandFilter.brandName);
                    checkBoxesSelected--;
                }
                else
                {
                    selectedBrandsInFilter.add(brandFilter.brandName);
                    checkBoxesSelected++;
                }
            }
        });
        navDrawerBrandsListView.setAdapter(brandsListViewAdapter);

        priceSeekBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action=event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        priceSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener()
        {
            @Override
            public void valueChanged(Number minValue, Number maxValue)
            {
                minPriceTextView.setText("₹"+minValue);
                maxPriceTextView.setText("₹"+maxValue);
                selectedMaxPrice=maxValue;
                selectedMinPrice=minValue;
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                errorLayout.setVisibility(View.GONE);
                filtersLoadingView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                fetchData(searchKeyString);
            }
        });
        fetchData(searchKeyString);
    }
    Handler handler=new Handler();
    Runnable runnable=new Runnable()
    {
        @Override
        public void run()
        {
            loadingView.setVisibility(View.GONE);
            productDealsRecyclerView.setVisibility(View.VISIBLE);
            filterPriceLayout.setVisibility(View.VISIBLE);
            priceSeekBar.setVisibility(View.VISIBLE);
            priceFilterLabelTextView.setVisibility(View.VISIBLE);
            brandsListViewLabelTextView.setVisibility(View.VISIBLE);
            navDrawerBrandsListView.setVisibility(View.VISIBLE);
            filterButtonsLayout.setVisibility(View.VISIBLE);
            filtersLoadingView.setVisibility(View.GONE);
        }
    };

    Runnable runnable1=new Runnable()
    {
        @Override
        public void run()
        {
            loadingView.setVisibility(View.GONE);
            productDealsRecyclerView.setVisibility(View.GONE);
            dealNotFoundImageView.setVisibility(View.VISIBLE);
            noDealsFoundLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_deals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.sort)
        {
            if(!noDealFound)
            {
                sortByDialog();
            }
            else
            {
                Snackbar.make(container,"No deals to sort", Snackbar.LENGTH_LONG).show();
            }
        }
        else if(id==R.id.homeButton)
        {
            Intent intent=new Intent(ProductDealsActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sortByDialog()
    {
        builder=new AlertDialog.Builder(ProductDealsActivity.this);
        builder.setTitle("Sort by");
        builder.setSingleChoiceItems(options, sortByDialogOptionApplied, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                sortByDialogOptionSelected=which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(sortByDialogOptionSelected!=-1)
                {
                    sortByDialogOptionApplied=sortByDialogOptionSelected;
                    int productsAdded=0;
                    auxiliarySortingArrayList.clear();
                    if(filterApplied)
                    {
                        activeArrayList=WITH_FILTER;
                        for(int i=0;i<tempProductDealsArrayList.size();i++)
                        {
                            if(tempProductDealsArrayList.get(i) instanceof Product)
                            {
                                auxiliarySortingArrayList.add((Product) tempProductDealsArrayList.get(i));
                            }
                        }
                    }
                    else
                    {
                        activeArrayList=WITHOUT_FILTER;
                        auxiliarySortingArrayList.addAll(masterProductDealsArrayList);
                    }

                    switch (options[sortByDialogOptionApplied])
                    {
                        case "Price: High to Low":
                            Collections.sort(auxiliarySortingArrayList, Collections.reverseOrder(new Comparator<Product>()
                            {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if (Float.parseFloat(o1.discountedPrice) > Float.parseFloat(o2.discountedPrice))
                                    {
                                        return 1;
                                    }
                                    else if (Float.parseFloat(o1.discountedPrice) < Float.parseFloat(o2.discountedPrice))
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }));
                            break;

                        case "Price: Low to High":
                            Collections.sort(auxiliarySortingArrayList, new Comparator<Product>()
                            {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if (Float.parseFloat(o1.discountedPrice) > Float.parseFloat(o2.discountedPrice))
                                    {
                                        return 1;
                                    }
                                    else if (Float.parseFloat(o1.discountedPrice) < Float.parseFloat(o2.discountedPrice))
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            break;

                        case "Discount: High to Low":
                            Collections.sort(auxiliarySortingArrayList, Collections.reverseOrder(new Comparator<Product>()
                            {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if (Float.parseFloat(o1.discountPercentage) > Float.parseFloat(o2.discountPercentage))
                                    {
                                        return 1;
                                    }
                                    else if (Float.parseFloat(o1.discountPercentage) < Float.parseFloat(o2.discountPercentage))
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }));
                            break;

                        case "Discount: Low to High":
                            Collections.sort(auxiliarySortingArrayList, new Comparator<Product>()
                            {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if (Float.parseFloat(o1.discountPercentage) > Float.parseFloat(o2.discountPercentage))
                                    {
                                        return 1;
                                    }
                                    else if (Float.parseFloat(o1.discountPercentage) < Float.parseFloat(o2.discountPercentage))
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            break;
                    }

                    if(filterApplied)
                    {
                        tempProductDealsArrayList.clear();
                    }
                    else
                    {
                        productDealsWithAdsArrayList.clear();
                    }

                    for(int i=0;i<auxiliarySortingArrayList.size();i++)
                    {
                        if(productsAdded%ITEMS_PER_AD==0 && productsAdded>0)
                        {
                            int ch=1+random.nextInt(4);
                            if(ch<=2)
                            {
                                AdView adView=new AdView(ProductDealsActivity.this);
                                adView.setAdUnitId(adUnitId);
                                adView.setAdSize(AdSize.LARGE_BANNER);
                                if(filterApplied)
                                {
                                    tempProductDealsArrayList.add(adView);
                                }
                                else
                                {
                                    productDealsWithAdsArrayList.add(adView);
                                }
                            }
                            else
                            {
                                ImageView adImageView=new ImageView(ProductDealsActivity.this);
                                final int pos=random.nextInt(adArrayList.size());
                                Picasso.with(ProductDealsActivity.this).load(adArrayList.get(pos).imageUrl).priority(Picasso.Priority.HIGH).into(adImageView);
                                adImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                adImageView.setAdjustViewBounds(true);
                                adImageView.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        intentBuilder=new CustomTabsIntent.Builder();
                                        customTabsIntent=intentBuilder.build();
                                        customTabsIntent.intent.setPackage("com.android.chrome");
                                        intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                                        intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                                        intentBuilder.setShowTitle(true);
                                        intentBuilder.addDefaultShareMenuItem();
                                        customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(adArrayList.get(pos).redirectUrl));
                                    }
                                });
                                if(filterApplied)
                                {
                                    tempProductDealsArrayList.add(adImageView);
                                }
                                else
                                {
                                    productDealsWithAdsArrayList.add(adImageView);
                                }
                            }
                        }
                        if(filterApplied)
                        {
                            tempProductDealsArrayList.add(auxiliarySortingArrayList.get(i));
                        }
                        else
                        {
                            productDealsWithAdsArrayList.add(auxiliarySortingArrayList.get(i));
                        }
                        productsAdded++;
                    }
                    if(filterApplied)
                    {
                        adapterWithFilter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
                sortByDialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(sortByDialogOptionApplied==-1)
                {
                    sortByDialogOptionSelected=-1;
                    sortByDialogOptionApplied=-1;
                }
                else
                {
                    sortByDialogOptionSelected=sortByDialogOptionApplied;
                }
                sortByDialog.dismiss();
            }
        });

        sortByDialog=builder.create();
        sortByDialog.show();
    }

    public void fetchData(String searchKeyString)
    {
        if(mode==1)
        {
            Call<ProductsResponse> call=ApiClient.getProductsService().getProducts("application/json",getString(R.string.store_name),searchKeyString);
            call.enqueue(new Callback<ProductsResponse>()
            {
                @Override
                public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response)
                {
                    ProductsResponse productsResponse=response.body();
                    if(productsResponse!=null)
                    {
                        ArrayList<Product> products=productsResponse.products;
                        if(products.size()!=0)
                        {
                            parseData(products);
                            for(int i=0;i<products.size();i++)
                            {
                                if(!products.get(i).isInStock.equals("0"))
                                {
                                    masterProductDealsArrayList.add(products.get(i));
                                }
                            }
                            Collections.sort(masterProductDealsArrayList,Collections.reverseOrder(new Comparator<Product>() {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if(o1.sortOrder>o2.sortOrder)
                                    {
                                        return 1;
                                    }
                                    else if(o1.sortOrder<o2.sortOrder)
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }));
                            maxPrice=Float.parseFloat(masterProductDealsArrayList.get(0).discountedPrice);
                            selectedMinPrice=0f;
                            for(int i=0;i<masterProductDealsArrayList.size();i++)
                            {
                                boolean found=false;
                                for(int j=0;j<brands.size();j++)
                                {
                                    if(brands.get(j).brandName.equals(masterProductDealsArrayList.get(i).brand))
                                    {
                                        found=true;
                                        break;
                                    }
                                }
                                if(!found)
                                {
                                    BrandFilter brandFilter=new BrandFilter(false,masterProductDealsArrayList.get(i).brand);
                                    brandFilter.brandNameInTitle=brandFilter.convertToTitle(brandFilter.brandName);
                                    brands.add(brandFilter);
                                }
                                if(Float.parseFloat(masterProductDealsArrayList.get(i).discountedPrice)>maxPrice)
                                {
                                    maxPrice=Float.parseFloat(masterProductDealsArrayList.get(i).discountedPrice);
                                }
                                if(i<4)
                                {
                                    productDealsWithAdsArrayList.add(masterProductDealsArrayList.get(i));
                                    productsInMainArrayList++;
                                }
                            }
                            brandsListViewLabelTextView.setText(brandsListViewLabelTextView.getText()+" ("+brands.size()+")");
                            selectedMaxPrice=maxPrice;
                            maxPriceTextView.setText("₹"+(int) maxPrice);
                            priceSeekBar.setMaxValue((int) maxPrice);
                            if(masterProductDealsArrayList.size()==0)
                            {
                                final Timer timer=new Timer();
                                timer.schedule(new TimerTask()
                                {
                                    @Override
                                    public void run()
                                    {
                                        handler.post(runnable1);
                                        timer.cancel();
                                    }
                                },2000,2000);
                                return;
                            }
                            adapter.notifyDataSetChanged();
                            final Timer timer=new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    handler.post(runnable);
                                    timer.cancel();
                                }
                            },2000,2000);
                        }
                        else
                        {
                            loadingView.setVisibility(View.GONE);
                            productDealsRecyclerView.setVisibility(View.GONE);
                            noDealsFoundLayout.setVisibility(View.VISIBLE);
                            dealNotFoundImageView.setVisibility(View.VISIBLE);
                            filtersLoadingView.setVisibility(View.GONE);
                            noDealsFoundTextViewNav.setVisibility(View.VISIBLE);
                            noDealFound=true;
                        }
                    }
                    else
                    {
                        if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                        {
                            retryCount++;
                            call.clone().enqueue(this);
                        }
                        else
                        {
                            loadingView.setVisibility(View.GONE);
                            productDealsRecyclerView.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductsResponse> call, Throwable t)
                {
                    if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                    {
                        retryCount++;
                        call.clone().enqueue(this);
                    }
                    else
                    {
                        loadingView.setVisibility(View.GONE);
                        productDealsRecyclerView.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        else if(mode==0)
        {
            Call<ProductsResponse> call=ApiClient.getProductsService().getProducts("application/json",getString(R.string.store_name),getString(R.string.store_name));
            call.enqueue(new Callback<ProductsResponse>()
            {
                @Override
                public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response)
                {
                    ProductsResponse productsResponse=response.body();
                    if(productsResponse!=null)
                    {
                        ArrayList<Product> products=productsResponse.products;
                        if(products.size()!=0)
                        {
                            parseData(products);
                            for(int i=0;i<products.size();i++)
                            {
                                if(Float.parseFloat(products.get(i).discountedPrice)<=price && !products.get(i).isInStock.equals("0"))
                                {
                                    masterProductDealsArrayList.add(products.get(i));
                                }
                            }
                            Collections.sort(masterProductDealsArrayList,Collections.reverseOrder(new Comparator<Product>() {
                                @Override
                                public int compare(Product o1, Product o2)
                                {
                                    if(o1.sortOrder>o2.sortOrder)
                                    {
                                        return 1;
                                    }
                                    else if(o1.sortOrder<o2.sortOrder)
                                    {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }));
                            maxPrice=Float.parseFloat(masterProductDealsArrayList.get(0).discountedPrice);
                            selectedMinPrice=0f;
                            for(int i=0;i<masterProductDealsArrayList.size();i++)
                            {
                                boolean found=false;
                                for(int j=0;j<brands.size();j++)
                                {
                                    if(brands.get(j).brandName.equals(masterProductDealsArrayList.get(i).brand))
                                    {
                                        found=true;
                                        break;
                                    }
                                }
                                if(!found)
                                {
                                    BrandFilter brandFilter=new BrandFilter(false,masterProductDealsArrayList.get(i).brand);
                                    brandFilter.brandNameInTitle=brandFilter.convertToTitle(brandFilter.brandName);
                                    brands.add(brandFilter);
                                }
                                if(Float.parseFloat(masterProductDealsArrayList.get(i).discountedPrice)>maxPrice)
                                {
                                    maxPrice=Float.parseFloat(masterProductDealsArrayList.get(i).discountedPrice);
                                }
                                if(i<4)
                                {
                                    productDealsWithAdsArrayList.add(masterProductDealsArrayList.get(i));
                                    productsInMainArrayList++;
                                }
                            }
                            brandsListViewLabelTextView.setText(brandsListViewLabelTextView.getText()+" ("+brands.size()+")");
                            selectedMaxPrice=maxPrice;
                            maxPriceTextView.setText("₹"+(int) maxPrice);
                            priceSeekBar.setMaxValue((int) maxPrice);
                            if(masterProductDealsArrayList.size()==0)
                            {
                                final Timer timer=new Timer();
                                timer.schedule(new TimerTask()
                                {
                                    @Override
                                    public void run()
                                    {
                                        handler.post(runnable1);
                                        timer.cancel();
                                    }
                                },2000,2000);
                                return;
                            }
                            adapter.notifyDataSetChanged();
                            final Timer timer=new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    handler.post(runnable);
                                    timer.cancel();
                                }
                            },2000,2000);
                        }
                        else
                        {
                            loadingView.setVisibility(View.GONE);
                            productDealsRecyclerView.setVisibility(View.GONE);
                            noDealsFoundLayout.setVisibility(View.VISIBLE);
                            dealNotFoundImageView.setVisibility(View.VISIBLE);
                            filtersLoadingView.setVisibility(View.GONE);
                            noDealsFoundTextViewNav.setVisibility(View.VISIBLE);
                            noDealFound=true;
                        }
                    }
                    else
                    {
                        if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                        {
                            retryCount++;
                            call.clone().enqueue(this);
                        }
                        else
                        {
                            loadingView.setVisibility(View.GONE);
                            productDealsRecyclerView.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ProductsResponse> call, Throwable t)
                {
                    if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                    {
                        retryCount++;
                        call.clone().enqueue(this);
                    }
                    else
                    {
                        loadingView.setVisibility(View.GONE);
                        productDealsRecyclerView.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        final Call<AdsResponse> call1=ApiClient.getAdsService().getAds();
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
                        if(ads.get(i).position.equals("page"))
                        {
                            adArrayList.add(ads.get(i));
                        }
                    }
                }
                else
                {
                    if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                    {
                        retryCount++;
                        call1.clone().enqueue(this);
                    }
                    else
                    {
                        ckAdsAvailable=false;
                    }
                }
            }

            @Override
            public void onFailure(Call<AdsResponse> call, Throwable t)
            {
                if(retryCount<=MainActivity.MAXIMUM_RETRY_COUNT)
                {
                    retryCount++;
                    call1.clone().enqueue(this);
                }
                else
                {
                    ckAdsAvailable=false;
                }
            }
        });

        applyFilterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkBoxesSelected==0 && selectedMinPrice.floatValue()==0f && selectedMaxPrice.floatValue()==maxPrice)
                {
                    productDealsRecyclerView.removeAllViews();
                    productDealsRecyclerView.swapAdapter(adapter,true);
                    drawer.closeDrawers();
                    activeArrayList=WITHOUT_FILTER;
                }
                else
                {
                    tempProductDealsArrayList.clear();
                    int productsAdded=0;
                    if(selectedBrandsInFilter.size()==0)
                    {
                        for(int i=0;i<masterProductDealsArrayList.size();i++)
                        {
                            Product product=masterProductDealsArrayList.get(i);
                            if(Float.parseFloat(product.discountedPrice)>=selectedMinPrice.floatValue() && Float.parseFloat(product.discountedPrice)<=selectedMaxPrice.floatValue())
                            {
                                if(productsAdded%ITEMS_PER_AD==0 && productsAdded>0)
                                {
                                    int ch=1+random.nextInt(4);
                                    if(ch<=2)
                                    {
                                        AdView adView=new AdView(ProductDealsActivity.this);
                                        adView.setAdUnitId("ca-app-pub-5162888426019346/6925659053");
                                        adView.setAdSize(AdSize.LARGE_BANNER);
                                        tempProductDealsArrayList.add(adView);
                                    }
                                    else
                                    {
                                        ImageView adImageView=new ImageView(ProductDealsActivity.this);
                                        final int pos=random.nextInt(adArrayList.size());
                                        Picasso.with(ProductDealsActivity.this).load(adArrayList.get(pos).imageUrl).priority(Picasso.Priority.HIGH).into(adImageView);
                                        adImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        adImageView.setAdjustViewBounds(true);
                                        adImageView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                intentBuilder=new CustomTabsIntent.Builder();
                                                customTabsIntent=intentBuilder.build();
                                                customTabsIntent.intent.setPackage("com.android.chrome");
                                                intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                                                intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                                                intentBuilder.setShowTitle(true);
                                                intentBuilder.addDefaultShareMenuItem();
                                                customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(adArrayList.get(pos).redirectUrl));
                                            }
                                        });
                                        tempProductDealsArrayList.add(adImageView);
                                    }
                                }
                                tempProductDealsArrayList.add(product);
                                productsAdded++;
                            }
                        }
                    }
                    else
                    {
                        for(int i=0;i<masterProductDealsArrayList.size();i++)
                        {
                            Product product=masterProductDealsArrayList.get(i);
                            if(selectedBrandsInFilter.contains(product.brand) && Float.parseFloat(product.discountedPrice)>=selectedMinPrice.floatValue() && Float.parseFloat(product.discountedPrice)<=selectedMaxPrice.floatValue())
                            {
                                if(productsAdded%ITEMS_PER_AD==0 && productsAdded>0)
                                {
                                    int ch=1+random.nextInt(4);
                                    if(ch<=2)
                                    {
                                        AdView adView=new AdView(ProductDealsActivity.this);
                                        adView.setAdUnitId("ca-app-pub-5162888426019346/6925659053");
                                        adView.setAdSize(AdSize.LARGE_BANNER);
                                        tempProductDealsArrayList.add(adView);
                                    }
                                    else
                                    {
                                        ImageView adImageView=new ImageView(ProductDealsActivity.this);
                                        final int pos=random.nextInt(adArrayList.size());
                                        Picasso.with(ProductDealsActivity.this).load(adArrayList.get(pos).imageUrl).priority(Picasso.Priority.HIGH).into(adImageView);
                                        adImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        adImageView.setAdjustViewBounds(true);
                                        adImageView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                intentBuilder=new CustomTabsIntent.Builder();
                                                customTabsIntent=intentBuilder.build();
                                                customTabsIntent.intent.setPackage("com.android.chrome");
                                                intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                                                intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                                                intentBuilder.setShowTitle(true);
                                                intentBuilder.addDefaultShareMenuItem();
                                                customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(adArrayList.get(pos).redirectUrl));
                                            }
                                        });
                                        tempProductDealsArrayList.add(adImageView);
                                    }
                                }
                                tempProductDealsArrayList.add(product);
                                productsAdded++;
                            }
                        }
                    }
                    if(tempProductDealsArrayList.size()==0)
                    {
                        productDealsRecyclerView.setVisibility(View.GONE);
                        noDealsFoundLayout.setVisibility(View.VISIBLE);
                        dealNotFoundImageView.setVisibility(View.VISIBLE);
                        noDealFound=true;
                    }
                    else
                    {
                        productDealsRecyclerView.setVisibility(View.VISIBLE);
                        noDealsFoundLayout.setVisibility(View.GONE);
                        dealNotFoundImageView.setVisibility(View.VISIBLE);
                        noDealFound=false;
                    }
                    adapterWithFilter.notifyDataSetChanged();
                    productDealsRecyclerView.swapAdapter(adapterWithFilter,true);
                    activeArrayList=WITH_FILTER;
                    drawer.closeDrawers();
                    if(productsAdded==1)
                    {
                        Toast.makeText(ProductDealsActivity.this,productsAdded+" result",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ProductDealsActivity.this,productsAdded+" results",Toast.LENGTH_SHORT).show();
                    }
                    sortByDialogOptionApplied=-1;
                }
                filterApplied=true;
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                priceSeekBar.setMaxStartValue((int) maxPrice);
                priceSeekBar.apply();
                selectedBrandsInFilter.clear();
                tempProductDealsArrayList.clear();
                brandsListViewAdapter.uncheckAll();
                brandsListViewAdapter.notifyDataSetChanged();
                activeArrayList=WITHOUT_FILTER;
                productDealsRecyclerView.swapAdapter(adapter,true);
                adapter.notifyDataSetChanged();
                sortByDialogOptionApplied=-1;
                filterApplied=false;
                if(noDealFound)
                {
                    productDealsRecyclerView.setVisibility(View.VISIBLE);
                    noDealsFoundLayout.setVisibility(View.GONE);
                    dealNotFoundImageView.setVisibility(View.VISIBLE);
                    noDealFound=false;
                }
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(@NonNull View view, float v)
            {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view)
            {
                if(filterApplied)
                {
                    if(checkBoxesSelected==0 && selectedMinPrice.floatValue()==0f && selectedMaxPrice.floatValue()==maxPrice)
                    {
                        productDealsRecyclerView.swapAdapter(adapter,true);
                        activeArrayList=WITHOUT_FILTER;
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int i)
            {

            }
        });
    }

    private void parseData(ArrayList<Product> products)
    {
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        for(int i=0;i<products.size();i++)
        {
            Product product=products.get(i);
            if(!product.imageUrl1.equals("null"))
            {
                product.images=product.images+product.imageUrl1+" || ";
            }
            if(!product.imageUrl2.equals("null"))
            {
                product.images=product.images+product.imageUrl2+" || ";
            }
            if(!product.imageUrl3.equals("null"))
            {
                product.images=product.images+product.imageUrl3+" || ";
            }
            if(!product.imageUrl4.equals("null"))
            {
                product.images=product.images+product.imageUrl4+" || ";
            }
            float percentage=((Float.parseFloat(product.mrp)-Float.parseFloat(product.discountedPrice))/Float.parseFloat(product.mrp))*100;
            decimalFormat.setRoundingMode(RoundingMode.UP);
            product.discountPercentage=decimalFormat.format(percentage);
        }
    }

    public void addProductsToList()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                int index=0;
                if(productDealsWithAdsArrayList.size()>0)
                {
                    index=productDealsWithAdsArrayList.size();
                }
                isLoading=true;
                moreDataAvailable=true;
                if(index+10<=masterProductDealsArrayList.size()-1)
                {
                    int i=index;
                    while(i<index+10)
                    {
                        if(productsInMainArrayList%ITEMS_PER_AD==0)
                        {
                            int ch=1+random.nextInt(4);
                            Log.d("HELLO",ch+"");
                            if(ch<=2)
                            {
                                AdView adView=new AdView(ProductDealsActivity.this);
                                adView.setAdUnitId("ca-app-pub-5162888426019346/6925659053");
                                adView.setAdSize(AdSize.LARGE_BANNER);
                                productDealsWithAdsArrayList.add(adView);
                            }
                            else
                            {
                                final int pos=random.nextInt(adArrayList.size());
                                ImageView imageView=new ImageView(ProductDealsActivity.this);
                                Picasso.with(ProductDealsActivity.this).load(adArrayList.get(pos).imageUrl).priority(Picasso.Priority.HIGH).into(imageView);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                imageView.setAdjustViewBounds(true);
                                imageView.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        intentBuilder=new CustomTabsIntent.Builder();
                                        customTabsIntent=intentBuilder.build();
                                        customTabsIntent.intent.setPackage("com.android.chrome");
                                        intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                                        intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                                        intentBuilder.setShowTitle(true);
                                        intentBuilder.addDefaultShareMenuItem();
                                        customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(adArrayList.get(pos).redirectUrl));
                                    }
                                });
                                productDealsWithAdsArrayList.add(imageView);
                            }
                        }
                        productDealsWithAdsArrayList.add(masterProductDealsArrayList.get(i));
                        itemsLoaded++;
                        i++;
                        productsInMainArrayList++;
                    }
                }
                else
                {
                    int i=index;
                    while(i<masterProductDealsArrayList.size())
                    {
                        if(productsInMainArrayList%ITEMS_PER_AD==0)
                        {
                            int ch=1+random.nextInt(4);
                            if(ch<=2)
                            {
                                AdView adView=new AdView(ProductDealsActivity.this);
                                adView.setAdUnitId("ca-app-pub-5162888426019346/6925659053");
                                adView.setAdSize(AdSize.LARGE_BANNER);
                                productDealsWithAdsArrayList.add(adView);
                            }
                            else
                            {
                                final int pos=random.nextInt(adArrayList.size());
                                ImageView imageView=new ImageView(ProductDealsActivity.this);
                                Picasso.with(ProductDealsActivity.this).load(adArrayList.get(pos).imageUrl).priority(Picasso.Priority.HIGH).into(imageView);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                imageView.setAdjustViewBounds(true);
                                imageView.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        intentBuilder=new CustomTabsIntent.Builder();
                                        customTabsIntent=intentBuilder.build();
                                        customTabsIntent.intent.setPackage("com.android.chrome");
                                        intentBuilder.setStartAnimations(ProductDealsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                                        intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealsActivity.this, R.color.black));
                                        intentBuilder.setShowTitle(true);
                                        intentBuilder.addDefaultShareMenuItem();
                                        customTabsIntent.launchUrl(ProductDealsActivity.this, Uri.parse(adArrayList.get(pos).redirectUrl));
                                    }
                                });
                                productDealsWithAdsArrayList.add(imageView);
                            }
                        }
                        productDealsWithAdsArrayList.add(masterProductDealsArrayList.get(i));
                        itemsLoaded++;
                        i++;
                        productsInMainArrayList++;
                    }
                    moreDataAvailable=false;
                }
                adapter.notifyDataSetChanged();
                isLoading=false;
            }
        },100);
    }
}
