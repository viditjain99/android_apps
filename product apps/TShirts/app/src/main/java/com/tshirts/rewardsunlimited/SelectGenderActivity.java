package com.tshirts.rewardsunlimited;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class SelectGenderActivity extends AppCompatActivity
{

    ImageButton menButton,womenButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        menButton=findViewById(R.id.maleButton);
        womenButton=findViewById(R.id.femaleButton);

        menButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(SelectGenderActivity.this,ProductDealsActivity.class);
                intent.putExtra("GENDER","Men");
                intent.putExtra("RETAILER_NAME",getIntent().getStringExtra("RETAILER_NAME"));
                intent.putExtra("RETAILER_NAME_IN_TITLE",getIntent().getStringExtra("RETAILER_NAME_IN_TITLE"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        womenButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(SelectGenderActivity.this,ProductDealsActivity.class);
                intent.putExtra("GENDER","Women");
                intent.putExtra("RETAILER_NAME",getIntent().getStringExtra("RETAILER_NAME"));
                intent.putExtra("RETAILER_NAME_IN_TITLE",getIntent().getStringExtra("RETAILER_NAME_IN_TITLE"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if(item.getItemId()==android.R.id.home)
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
