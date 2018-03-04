package com.infurza.infurzaapplication.introduction;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.infurza.infurzaapplication.R;
import com.infurza.infurzaapplication.adapters.ViewPagerAdapter;

public class ViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    private ViewPager viewPager;
    private Button skipButton, nextButton;
    private ViewPagerAdapter mAdapter;
    public String flag = "";

    private int[] mImageResources = {
            R.drawable.viewpagerone,
            R.drawable.viewpagertwo,
            R.drawable.viewpagerthree,
            R.drawable.viewpagerfour

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        viewPager = (ViewPager) findViewById(R.id.viewPagerContainer);
        skipButton = (Button) findViewById(R.id.viewPagerSkip);
        nextButton = (Button) findViewById(R.id.viewPagerNext);

        flag = "MainViewPager";

        mAdapter = new ViewPagerAdapter(ViewPagerActivity.this, mImageResources,flag);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(0, true);

        skipButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);



    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.viewPagerNext:
                if(viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 1 || viewPager.getCurrentItem() == 2){
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else{
                    Intent intent = new Intent(ViewPagerActivity.this, LoginActivity.class);
                    startActivity(intent);

                }

                break;

            case R.id.viewPagerSkip:
                Intent intent = new Intent(ViewPagerActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
