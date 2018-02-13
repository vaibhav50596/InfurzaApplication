package com.infurza.infurzaapplication.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.infurza.infurzaapplication.R;

/**
 * Created by vaibhav on 1/20/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int[] mResources;
    private String flag;

    public ViewPagerAdapter(Context context, int[] resources, String fg){
        this.mContext = context;
        this.mResources = resources;
        this.flag = fg;
    }
    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (flag.equalsIgnoreCase("MainViewPager")){
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }

        else if(flag.equalsIgnoreCase("SellImagesViewPager")){
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.sell_images_item_viewpager, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.sell_img_pager_item);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }
        return true;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }

}
