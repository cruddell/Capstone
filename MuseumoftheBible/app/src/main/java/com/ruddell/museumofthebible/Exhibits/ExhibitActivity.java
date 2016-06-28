package com.ruddell.museumofthebible.Exhibits;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ruddell.museumofthebible.Api.ApiHelper;
import com.ruddell.museumofthebible.Exhibits.model.Exhibit;
import com.ruddell.museumofthebible.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ExhibitActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "ExhibitActivity";
    private static final boolean DEBUG_LOG = true;
    private ExhibitPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private int mViewPagerPageMargin = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_LOG) Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_exhibit);

        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mAdapter = new ExhibitPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPagerPageMargin = getResources().getDimensionPixelOffset(R.dimen.gallery_image_fragment_viewpager_page_margin);
        mViewPager.setPageMargin(mViewPagerPageMargin);

        getWindow().setStatusBarColor(getResources().getColor(R.color.md_red_900));

        getExhibits();

        //show notice to swipe after a short delay
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNotice();
            }
        }, 2000);

    }

    private void getExhibits() {
        ApiHelper.getExhibits(new ApiHelper.NetworkCallback() {
            @Override
            public void onResponseReceived(final ApiHelper.HttpResponse response) {

                if (response.responseCode == HttpURLConnection.HTTP_OK) {
                    ArrayList<Exhibit> exhibits = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response.responseBody);
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("ID");
                            String title = jsonObject.getString("Title");
                            String audioFile = jsonObject.getString("Audio");
                            String imageName = jsonObject.getString("Image");
                            Exhibit exhibit = new Exhibit(id, title, audioFile, imageName);
                            exhibits.add(exhibit);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAdapter.setExhibits(exhibits);

                }
            }
        }, this);
    }

    private void showNotice() {
        Snackbar snackbar = Snackbar.make(mViewPager, "Swipe left to see more exhibits!", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.md_blue_500));
        snackbar.show();
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {

    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    private class ExhibitPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Exhibit> mExhibits = new ArrayList<>();

        public ExhibitPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        private void setExhibits(ArrayList<Exhibit> exhibits) {
            mExhibits = exhibits;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(final int position) {
            ExhibitDetailFragment fragment = ExhibitDetailFragment.newInstance(position);

            return fragment;
        }

        @Override
        public int getCount() {
            return mExhibits.size();
        }

    }

    private class CustomPagerAdapter extends PagerAdapter {
        private static final float MIN_SCALE = 1f - 1f / 7f;
        private static final float MAX_SCALE = 1f;
        private static final float PAGE_WIDTH_AS_PERCENT_OF_SCREEN = 1.f;

        private boolean mIsDefaultItemSelected = false;


        private int[] mImages = {
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500,
                R.drawable.img3500
        };


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);


            View view = inflater.inflate(R.layout.gallery_image_holder, container,false);
            view.setTag(position);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView4);
            imageView.setImageDrawable(getResources().getDrawable(mImages[position], getTheme()));


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
