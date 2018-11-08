package com.thdtek.acs.terminal.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.util.AppUtil;

import java.util.List;

/**
 * Time:2018/10/11
 * User:lizhen
 * Description:
 */

public class MainViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mList;

    public MainViewPagerAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    public void notifyData(List<String> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_view_pager, container, false);
        ImageView imageView = layout.findViewById(R.id.imageView);
        int index = 0;
        if (mList.size() != 0) {
            index = position % mList.size();
        }

        String msg = mList.get(index);
        if (msg.equals("1")) {
            Glide.with(mContext).load(R.mipmap.ic_main_three_one).into(imageView);
        } else if (msg.contains("ic_ad_default")) {
            Glide.with(mContext).load(MyApplication.getContext().getResources().getIdentifier(mList.get(index), "mipmap", AppUtil.getPackageName())).placeholder(R.mipmap.ic_main_three_one).into(imageView);
        } else {
            Glide.with(mContext).load(mList.get(index)).placeholder(R.mipmap.ic_main_three_one).into(imageView);
        }

        ViewGroup parent = (ViewGroup) imageView.getParent();
        if (parent != null) {
            parent.removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
