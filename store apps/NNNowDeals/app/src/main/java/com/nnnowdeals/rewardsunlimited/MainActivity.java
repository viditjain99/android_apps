package com.nnnowdeals.rewardsunlimited;

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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ProductSliderClickListener, NavigationView.OnNavigationItemSelectedListener {
    ArrayList<Banner> productSliderObjects=new ArrayList<>();
    ViewPager productSliderViewPager;   //carousel
    LinearLayout dotsPanel,errorLayout;
    int dotsCount=0;
    ImageView[] dots;
    CustomGridView categoriesGridView;
    ArrayList<Category> categories=new ArrayList<>();      //list of categories for grid
    ArrayList<Category> allCategories=new ArrayList<>();   //list of all categories for see all categories
    int currentSliderObject=0;
    Timer timer;
    final int DELAY_MS=0;
    final int PERIOD_MS=2000;
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;
    GridAdapter gridAdapter;
    TextView categoriesLabelTextView;
    AdView adView;
    ProductSliderAdapter adapter;
    ConstraintLayout constraintLayout;
    ImageView errorImageView;
    boolean carouselLoaded = false, gridLoaded = false;
    int retryCount=0;
    public static int MAXIMUM_RETRY_COUNT=5;
    Button seeAllButton,retryButton;
    FrameLayout frameLayout;
    Snackbar snackbar;
    ProgressBar startUpLoading;
    static ArrayList<String> adIds=new ArrayList<>();
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window=this.getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));    //setting status bar colour
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_action_overflow));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+getString(R.string.store_name)+" Deals"+ "</font>"));   //setting title

        productSliderViewPager=findViewById(R.id.viewPager);
        categoriesGridView=findViewById(R.id.categoriesGridView);
        categoriesLabelTextView=findViewById(R.id.categoriesLabelTextView);
        dotsPanel=findViewById(R.id.sliderDots);
        adView=findViewById(R.id.adView);
        constraintLayout=findViewById(R.id.constraintLayout);
        seeAllButton=findViewById(R.id.seeAllButton);
        errorImageView=findViewById(R.id.errorImageView);
        retryButton=findViewById(R.id.retryButton);
        errorLayout=findViewById(R.id.errorLayout);
        frameLayout=findViewById(R.id.contentContainer);
        startUpLoading=findViewById(R.id.startUpLoading);

        productSliderViewPager.setVisibility(View.GONE);
        dotsPanel.setVisibility(View.GONE);
        categoriesGridView.setVisibility(View.GONE);
        categoriesLabelTextView.setVisibility(View.GONE);
        adView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        startUpLoading.setVisibility(View.VISIBLE);

        snackbar= Snackbar.make(frameLayout,"Please wait...", Snackbar.LENGTH_INDEFINITE);
        adapter=new ProductSliderAdapter(this, productSliderObjects, new ProductSliderClickListener()
        {
            @Override
            public void onProductSliderObjectClick(Banner productSliderObject)      //called when banner is clicked
            {
                if(productSliderObject.linkType.equals("out"))      //opens chrome tab
                {
                    snackbar.show();
                    intentBuilder=new CustomTabsIntent.Builder();
                    customTabsIntent=intentBuilder.build();
                    intentBuilder.setStartAnimations(MainActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                    intentBuilder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                    intentBuilder.setShowTitle(true);
                    intentBuilder.addDefaultShareMenuItem();
                    customTabsIntent.launchUrl(MainActivity.this,Uri.parse(productSliderObject.redirectUrl));
                    snackbar.dismiss();
                }
                else      //redirects to in-app category
                {
                    Intent intent=new Intent(MainActivity.this,ProductDealsActivity.class);
                    intent.putExtra("SEARCH_KEY",productSliderObject.redirectUrl);
                    startActivity(intent);
                }
            }
        });
        productSliderViewPager.setAdapter(adapter);

        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.grid_item_anim);
        GridLayoutAnimationController controller=new GridLayoutAnimationController(animation, .1f, .1f);
        categoriesGridView.setLayoutAnimation(controller);      //animation for grid layout

        categoriesGridView.setExpanded(true);
        gridAdapter=new GridAdapter(this,categories);
        categoriesGridView.setAdapter(gridAdapter);

        final Handler handler=new Handler();
        final Runnable Update=new Runnable()    //runnable for changing carousel banner
        {
            @Override
            public void run()
            {
                if(currentSliderObject==dotsCount)
                {
                    currentSliderObject=0;
                }
                productSliderViewPager.setCurrentItem(currentSliderObject++,true);
            }
        };

        timer=new Timer();
        timer.schedule(new TimerTask()        //timer to schedule and repeat task of carousel update
        {
            @Override
            public void run()
            {
                handler.post(Update);
            }
        },DELAY_MS,PERIOD_MS);

        loadData();      //function to load all data


        productSliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)       //function to change dots panel state according to banner on screen
            {
                for(int i=0;i<dotsCount;i++)
                {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                currentSliderObject=position;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share)        //app share button clicked
        {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"For the best deals on NNNow.com check out this app at https://play.google.com/store/apps/details?id=com.nnnowdeals.rewardsunlimited&hl=en");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public void onProductSliderObjectClick(Banner productSliderObject)
    {

    }

    public void loadData()
    {
        errorLayout.setVisibility(View.GONE);
        Call<BannersResponse> call1=ApiClient.getCarouselService().getBanners();   //call for getting banners
        call1.enqueue(new Callback<BannersResponse>()
        {
            @Override
            public void onResponse(Call<BannersResponse> call, Response<BannersResponse> response)
            {
                BannersResponse bannersResponse=response.body();
                if(bannersResponse!=null)
                {
                    ArrayList<Banner> bannerArrayList=bannersResponse.banners;
                    for(int i=0;i<bannerArrayList.size();i++)
                    {
                        if(bannerArrayList.get(i).store.equals(getString(R.string.store_name).toLowerCase()) && bannerArrayList.get(i).status==1)
                        {
                            productSliderObjects.add(bannerArrayList.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();                 //notifying adapter of addition of banners in list
                    dotsCount=adapter.getCount();
                    dots=new ImageView[dotsCount];
                    for(int i=0;i<dotsCount;i++)
                    {
                        dots[i]=new ImageView(MainActivity.this);
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_active_dot));   //setting image of non-active dot to all dots
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  //setting layout parameters to dots panel
                        params.setMargins(8, 0, 8, 0);   //setting margins to dots panel
                        dotsPanel.addView(dots[i], params);    //adding dots to dots panel
                    }
                    dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));  //setting active dot image to first dot
                    carouselLoaded=true;   //carousel completely loaded
                    retryCount=0;
                }
                else
                {
                    if(retryCount<=MAXIMUM_RETRY_COUNT)     //if request retry count is less than the max, put another request
                    {
                        Log.d("Request Failed","failed");
                        retryCount++;
                        call.clone().enqueue(this);
                    }
                    else       //if max retries are exceeded show error
                    {
                        productSliderViewPager.setVisibility(View.GONE);
                        dotsPanel.setVisibility(View.GONE);
                        categoriesGridView.setVisibility(View.GONE);
                        categoriesLabelTextView.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        startUpLoading.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BannersResponse> call, Throwable t)
            {
                if(retryCount<=MAXIMUM_RETRY_COUNT)        //if request retry count is less than the max, put another request
                {
                    Log.d("Request Failed","failed");
                    retryCount++;
                    call.clone().enqueue(this);
                }
                else             //if max retries are exceeded show error
                {
                    productSliderViewPager.setVisibility(View.GONE);
                    dotsPanel.setVisibility(View.GONE);
                    categoriesGridView.setVisibility(View.GONE);
                    categoriesLabelTextView.setVisibility(View.GONE);
                    adView.setVisibility(View.GONE);
                    startUpLoading.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        MobileAds.initialize(MainActivity.this,"ca-app-pub-5162888426019346~2355367382");       //initialise mobile ads
        AdRequest adRequest=new AdRequest.Builder().build();            //request for google ad
        adView.loadAd(adRequest);             //load google ad in adview
        final Call<CategoriesResponse> call=ApiClient.getCategoryService().getCategories(getString(R.string.store_name).toLowerCase());   //call for getting categories from server
        call.enqueue(new Callback<CategoriesResponse>()
        {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response)
            {
                CategoriesResponse categoriesResponse=response.body();
                if(categoriesResponse!=null)
                {
                    ArrayList<Category> temp=categoriesResponse.products;
                    for(int i=0;i<temp.size();i++)
                    {
                        if(temp.get(i).status==1)
                        {
                            allCategories.add(temp.get(i));
                        }
                    }
                    if(allCategories.size()<=9)     //if there are less than 9 categories then add all
                    {
                        categories.addAll(allCategories);
                    }
                    else
                    {
                        for(int i=0;i<9;i++)
                        {
                            categories.add(allCategories.get(i));
                        }
                    }

                    if(allCategories.size()%3!=0 && allCategories.size()<9)
                    {
                        categoriesGridView.setNumColumns(2);
                        ViewGroup.MarginLayoutParams layoutParams=(ViewGroup.MarginLayoutParams) productSliderViewPager.getLayoutParams();
                        layoutParams.topMargin=40;
                    }
                    gridAdapter.notifyDataSetChanged();
                    productSliderViewPager.setVisibility(View.VISIBLE);
                    dotsPanel.setVisibility(View.VISIBLE);
                    categoriesLabelTextView.setVisibility(View.VISIBLE);
                    categoriesGridView.setVisibility(View.VISIBLE);
                    adView.setVisibility(View.VISIBLE);
                    startUpLoading.setVisibility(View.GONE);
                    retryCount=0;
                }
                else
                {
                    if(retryCount<=MAXIMUM_RETRY_COUNT)       //retry request if max is not reached
                    {
                        retryCount++;
                        call.clone().enqueue(this);
                    }
                    else          //show error if max retries are reached
                    {
                        productSliderViewPager.setVisibility(View.GONE);
                        dotsPanel.setVisibility(View.GONE);
                        categoriesGridView.setVisibility(View.GONE);
                        categoriesLabelTextView.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        startUpLoading.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
                gridLoaded=true;      //grid loaded successfully
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t)
            {
                if(retryCount<=MAXIMUM_RETRY_COUNT)    //retry request if max is not reached
                {
                    retryCount++;
                    call.clone().enqueue(this);
                }
                else        //show error if max retries are reached
                {
                    productSliderViewPager.setVisibility(View.GONE);
                    dotsPanel.setVisibility(View.GONE);
                    categoriesGridView.setVisibility(View.GONE);
                    categoriesLabelTextView.setVisibility(View.GONE);
                    adView.setVisibility(View.GONE);
                    startUpLoading.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        categoriesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()     //setting click listener for all categories in gridview
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Category category=categories.get(position);
                if(category.name.split(" ")[0].equals("Under"))
                {
                    Intent intent=new Intent(MainActivity.this,ProductDealsActivity.class);
                    intent.putExtra("SEARCH_KEY",category.name);
                    mode=0;
                    intent.putExtra("MODE",mode);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,SubCategoryActivity.class);
                    intent.putExtra("CATEGORY_NAME",category.name);
                    mode=1;
                    intent.putExtra("MODE",mode);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        seeAllButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,SeeAllCategoriesActivity.class);
                intent.putExtra("Categories",allCategories);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startUpLoading.setVisibility(View.VISIBLE);
                loadData();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
