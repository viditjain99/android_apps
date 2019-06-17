package com.shoes.rewardsunlimited;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SeeAllRetailersActivity extends AppCompatActivity {

    ListView allRetailersListView;
    ArrayList<Retailer> retailersArrayList;
    AllRetailersListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_brands);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+"Retailers"+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allRetailersListView=findViewById(R.id.allCategoriesListView);

        retailersArrayList=(ArrayList<Retailer>) getIntent().getSerializableExtra("RETAILERS");

        adapter=new AllRetailersListViewAdapter(this,retailersArrayList);

        allRetailersListView.setAdapter(adapter);

        allRetailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(SeeAllRetailersActivity.this,SelectGenderActivity.class);
                intent.putExtra("RETAILER_NAME",retailersArrayList.get(position).name);
                intent.putExtra("RETAILER_NAME_IN_TITLE",retailersArrayList.get(position).nameInTitle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
}
