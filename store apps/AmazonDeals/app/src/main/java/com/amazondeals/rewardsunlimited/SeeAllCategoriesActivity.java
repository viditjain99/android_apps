package com.amazondeals.rewardsunlimited;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SeeAllCategoriesActivity extends AppCompatActivity {

    ListView allCategoriesListView;
    ArrayList<Category> categoryArrayList;
    AllCategoriesListViewAdapter adapter;
    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_categories);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">"+"Categories"+ "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allCategoriesListView=findViewById(R.id.allCategoriesListView);

        categoryArrayList=(ArrayList<Category>) getIntent().getSerializableExtra("Categories");

        adapter=new AllCategoriesListViewAdapter(this,categoryArrayList);

        allCategoriesListView.setAdapter(adapter);

        allCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(categoryArrayList.get(position).name.split(" ")[0].equals("Under"))
                {
                    Intent intent=new Intent(SeeAllCategoriesActivity.this,ProductDealsActivity.class);
                    intent.putExtra("SEARCH_KEY",categoryArrayList.get(position).name);
                    mode=0;
                    intent.putExtra("MODE",mode);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(SeeAllCategoriesActivity.this,SubCategoryActivity.class);
                    intent.putExtra("CATEGORY_NAME",categoryArrayList.get(position).name);
                    mode=1;
                    intent.putExtra("MODE",mode);
                    startActivity(intent);
                }
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
