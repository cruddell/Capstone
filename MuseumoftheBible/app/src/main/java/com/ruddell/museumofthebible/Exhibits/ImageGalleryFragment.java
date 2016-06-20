package com.ruddell.museumofthebible.Exhibits;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ruddell.museumofthebible.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Uses open source carousel view implementation from https://gist.github.com/thuytrinh/3999404, created by Thuy Trinh
 *      (No license specified)
 */
public class ImageGalleryFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageView> imageList;
    int mImageCount = 10;
    private static final float MIN_SCALE = 1f - 1f / 7f;
    private static final float MAX_SCALE = 1f;
    private static final float PAGE_WIDTH_AS_PERCENT_OF_SCREEN = 1.f;
    private int mScreenWidth = 0;
    private ViewPager mViewPager;
    private static final String IMAGE_VIEW_TAG = "image_view";
    private int mViewPagerPageMargin = 0;
    private int mImageViewMargin = 0;

    public ImageGalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_gallery, container, false);

        mViewPager = (ViewPager)view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new CustomPagerAdapter());
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(2);

        mViewPagerPageMargin = getResources().getDimensionPixelOffset(R.dimen.gallery_image_fragment_viewpager_page_margin);
        mViewPager.setPageMargin(mViewPagerPageMargin);


        android.graphics.Point size = new android.graphics.Point();
        WindowManager w = getActivity().getWindowManager();

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            w.getDefaultDisplay().getSize(size);

            mScreenWidth = size.x;
        }
        else
        {
            Display d = w.getDefaultDisplay();
            mScreenWidth = d.getWidth();
        }

        mImageViewMargin = Math.abs(mViewPagerPageMargin)/2;




        return view;
    }

    @Override
    public void onPageScrollStateChanged(int state) {


    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        for (int i = 0; i < mViewPager.getChildCount(); i++) {
            View imageHolder = mViewPager.getChildAt(i);
            ImageView imageView = (ImageView) imageHolder.findViewById(R.id.imageView4);
            int itemPosition = (Integer) imageHolder.getTag();


            if (itemPosition == position) {
                imageView.setScaleX(MAX_SCALE - positionOffset / 7f);
                imageView.setScaleY(MAX_SCALE - positionOffset / 7f);
            }


            if (itemPosition == (position + 1)) {
                imageView.setScaleX(MIN_SCALE + positionOffset / 7f);
                imageView.setScaleY(MIN_SCALE + positionOffset / 7f);
            }
        }
    }


    @Override
    public void onPageSelected(int position) {


    }

    private class CustomPagerAdapter extends PagerAdapter {


        private boolean mIsDefaultItemSelected = false;


        private int[] mImages = {
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500
        };


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);


            View view = inflater.inflate(R.layout.gallery_image_holder, container,false);
            view.setTag(position);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView4);
            imageView.setImageDrawable(getResources().getDrawable(mImages[position], getActivity().getTheme()));


            if (!mIsDefaultItemSelected) {
                imageView.setScaleX(MAX_SCALE);
                imageView.setScaleY(MAX_SCALE);
                mIsDefaultItemSelected = true;
            } else {
                imageView.setScaleX(MIN_SCALE);
                imageView.setScaleY(MIN_SCALE);
            }

            container.addView(view, 0);

            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public int getCount() {
            return mImages.length;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public float getPageWidth(final int position) {
            return PAGE_WIDTH_AS_PERCENT_OF_SCREEN;
        }


    }





}
