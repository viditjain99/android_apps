package com.nnnowdeal.rewardsunlimited;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDealDetailsActivity extends AppCompatActivity {

    ViewPager photosViewPager;
    TextView productNameTextView,productDescriptionTextView,productBrandTextView,productMrpTextView,productOfferPriceTextView,discountPercentTextView;
    Button buyNowButton,shareButton;
    String productName,productDescriptionString,productBrand,productMrp,productOfferPrice,productUrl,photosString,discount;
    ProductDetailsViewPagerAdapter adapter;
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;
    ImageView[] dots;
    ConstraintLayout constraintLayout;
    LinearLayout dotsPanel;
    int dotsCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_product_deal_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        getSupportActionBar().setTitle("");
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        photosViewPager=findViewById(R.id.photosViewPager);
        productNameTextView=findViewById(R.id.productDealNameTextView);
        productDescriptionTextView=findViewById(R.id.productDescriptionTextView);
        productBrandTextView=findViewById(R.id.brandTextView);
        productMrpTextView=findViewById(R.id.mrpTextView);
        productOfferPriceTextView=findViewById(R.id.offerPriceTextView);
        buyNowButton=findViewById(R.id.buyNowButton);
        shareButton=findViewById(R.id.shareButton);
        constraintLayout=findViewById(R.id.constraintLayout);
        dotsPanel=findViewById(R.id.sliderDots);
        discountPercentTextView=findViewById(R.id.discountPercentTextView);

        productName=intent.getStringExtra("PRODUCT_NAME");
        productDescriptionString=intent.getStringExtra("PRODUCT_DESC");
        productBrand=intent.getStringExtra("PRODUCT_BRAND");
        productMrp=intent.getStringExtra("PRODUCT_MRP");
        productOfferPrice=intent.getStringExtra("PRODUCT_OFFER_PRICE");
        productUrl=intent.getStringExtra("PRODUCT_URL");
        photosString=intent.getStringExtra("PRODUCT_PHOTOS");
        discount=intent.getStringExtra("DISCOUNT");

        String[] imageUrls=photosString.split(Pattern.quote(" || "));
        dotsCount=imageUrls.length;
        dots=new ImageView[dotsCount];
        for(int i=0;i<dotsCount;i++)
        {
            dots[i]=new ImageView(ProductDealDetailsActivity.this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.non_active_dot));   //setting image of non-active dot to all dots
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  //setting layout parameters to dots panel
            params.setMargins(8, 0, 8, 0);   //setting margins to dots panel
            dotsPanel.addView(dots[i], params);    //adding dots to dots panel
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));  //setting active dot image to first dot

        adapter=new ProductDetailsViewPagerAdapter(this,imageUrls);

        photosViewPager.setAdapter(adapter);

        photosViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
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
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        productNameTextView.setText(productName);
        String[] productDescription=productDescriptionString.split(Pattern.quote(" LFLF "));
        productDescriptionString="";
        for(int i=0;i<productDescription.length;i++)
        {
            if(i<productDescription.length-1)
            {
                productDescriptionString=productDescriptionString+"• "+productDescription[i]+"\n"+"\n";
            }
            else
            {
                productDescriptionString=productDescriptionString+"• "+productDescription[i];
            }
        }
        productDescriptionTextView.setText(productDescriptionString);
        productBrandTextView.setText(productBrand);
        productMrpTextView.setText(productMrp);
        productMrpTextView.setPaintFlags(productMrpTextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        productOfferPriceTextView.setText("₹"+productOfferPrice);
        buyNowButton.setBackgroundColor(Color.parseColor("#0394E5"));
        shareButton.setBackgroundColor(Color.parseColor("#010101"));
        buyNowButton.setTextColor(Color.parseColor("#ffffff"));
        shareButton.setTextColor(Color.parseColor("#ffffff"));
        discountPercentTextView.setText(discount+"% off");

        buyNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(ProductDealDetailsActivity.this,"Please wait...",Toast.LENGTH_SHORT).show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(productUrl, getString(R.string.subtag));
                call.enqueue(new Callback<LinkResponse>()
                {
                    @Override
                    public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response)
                    {
                        String url=response.body().link;
                        if(url!=null)
                        {
                            intentBuilder=new CustomTabsIntent.Builder();
                            customTabsIntent=intentBuilder.build();
                            customTabsIntent.intent.setPackage("com.android.chrome");
                            intentBuilder.setStartAnimations(ProductDealDetailsActivity.this,R.anim.slide_up_in,R.anim.slide_down_out);
                            intentBuilder.setToolbarColor(ContextCompat.getColor(ProductDealDetailsActivity.this, R.color.black));
                            intentBuilder.setShowTitle(true);
                            intentBuilder.addDefaultShareMenuItem();
                            customTabsIntent.launchUrl(ProductDealDetailsActivity.this, Uri.parse(url));
                        }
                        else
                        {
                            Toast.makeText(ProductDealDetailsActivity.this,"Invalid link",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        Toast.makeText(ProductDealDetailsActivity.this,getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(ProductDealDetailsActivity.this,"Please wait...",Toast.LENGTH_SHORT).show();
                Call<LinkResponse> call=ApiClient.getLinkService().getLink(productUrl, getString(R.string.subtag));
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
                        }
                        else
                        {
                            Toast.makeText(ProductDealDetailsActivity.this,"Invalid link",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponse> call, Throwable t)
                    {
                        Toast.makeText(ProductDealDetailsActivity.this,getString(R.string.error_string),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.homeButton)
        {
            Intent intent=new Intent(ProductDealDetailsActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(item.getItemId()==android.R.id.home)
        {
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
