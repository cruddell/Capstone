package com.ruddell.museumofthebible.Exhibits;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ruddell.museumofthebible.Api.ApiHelper;
import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.Exhibits.model.Exhibit;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.PrefUtils;
import com.ruddell.museumofthebible.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class ExhibitActivity extends BaseActivity implements ViewPager.OnPageChangeListener, MediaPlayerHelper.MediaPlayerHelperListener, SeekBar.OnSeekBarChangeListener, ExhibitDetailFragment.ExhibitCallback {
    private static final String TAG = "ExhibitActivity";
    private static final boolean DEBUG_LOG = true;
    private ExhibitPagerAdapter mAdapter;
    private ViewPager mViewPager;
    protected int mViewPagerPageMargin = 0;

    public MediaPlayerHelper mMediaPlayerHelper;
    private SeekBar mSeekBar;
    private ImageButton mPauseButton;
    private TextView mRemainingTimeLabel;

    int mDuration = 0;
    int mProgress = 0;
    static final int UPDATE_TIMER_INTERVAL = 100;
    //member objects related to time formatter method
    private static StringBuilder mFormatBuilder = new StringBuilder();
    private static Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[5];
    static final String durationShort = "%2$d:%5$02d";
    static final String durationLong = "%1$d:%3$02d:%5$02d";

    /** Handler for updating progress timer */
    private Handler mHandler = new Handler();

    private boolean mDidInit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_LOG) Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_exhibit);

        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mAdapter = new ExhibitPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);

        mSeekBar = (SeekBar)findViewById(R.id.seekBar2);
        mSeekBar.setOnSeekBarChangeListener(this);
        mRemainingTimeLabel = (TextView)findViewById(R.id.timeRemaining);
        mPauseButton = (ImageButton) findViewById(R.id.playButton);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mMediaPlayerHelper.isPlaying()) {
                    stop();
                }
                else play();
            }
        });

        mViewPagerPageMargin = getResources().getDimensionPixelOffset(R.dimen.gallery_image_fragment_viewpager_page_margin);
        mViewPager.setPageMargin(mViewPagerPageMargin);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (DEBUG_LOG) Log.d(TAG, "onPageSelected:" + position);
                Exhibit thisExhibit = mAdapter.mExhibits.get(position);
                setMedia(thisExhibit.audioFile);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.md_blue_900));

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop();
    }

    private void getExhibits() {
        JSONArray jsonArray = PrefUtils.getFeaturedExhibits(this);
        if (jsonArray!=null) {
            try {
                ArrayList<Exhibit> exhibits = new ArrayList<>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("ID");
                    String title = jsonObject.getString("Title");
                    String audioFile = jsonObject.getString("Audio");
                    String imageName = jsonObject.getString("Image");
                    String description = jsonObject.getString("Description");
                    Exhibit exhibit = new Exhibit(id, title, audioFile, imageName,description);
                    exhibits.add(exhibit);
                }
                mAdapter.setExhibits(exhibits);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            //still need to download!
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
                                String description = jsonObject.getString("Description");
                                Exhibit exhibit = new Exhibit(id, title, audioFile, imageName, description);
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

    //fragment callback method
    @Override
    public void onFragmentShown(final boolean isVisible) {

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
            ExhibitDetailFragment fragment = ExhibitDetailFragment.newInstance(position, mExhibits.get(position), ExhibitActivity.this);
            if (position==0 && mDidInit==false) {
                setMedia(mExhibits.get(0).audioFile);
                mDidInit = true;
            }
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



    public void setMedia(String audioFile) {
        File directory = Utils.getExternalDirectory();
        File outputFile = new File(directory, audioFile);
        String mediaPath = outputFile.getPath();

        if (outputFile.exists()) {
            if (mMediaPlayerHelper!=null) {
                if (mMediaPlayerHelper.isReady()) mMediaPlayerHelper.stop();
                mMediaPlayerHelper.updateMedia(mediaPath,0,true);
                updateSeekBarProgress(0);
            }
            else {
                mMediaPlayerHelper = new MediaPlayerHelper(ExhibitActivity.this,mediaPath,true,this);
                updateSeekBarProgress(0);
            }
        }
    }

    public void stop() {
        if (mMediaPlayerHelper!=null) mMediaPlayerHelper.pause();
        mPauseButton.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white_24dp));
    }

    public void play() {
        if (mMediaPlayerHelper!=null) mMediaPlayerHelper.play();
        mPauseButton.setImageDrawable(getDrawable(R.drawable.ic_pause_white_24dp));
    }

    private void updateProgress(int position) {
        mProgress = position;
        updateSeekBarProgress(mProgress);
        mRemainingTimeLabel.setText(makeTimeString((long) (mDuration - mProgress) / 1000));

    }



    private void updateSeekBarProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    // Set startMediaPlayback/pause button image based upon startMediaPlayback state
    private void setPauseButtonImage() {
        if (mMediaPlayerHelper.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    void togglePlayback() {
        if(mMediaPlayerHelper.isPlaying()) {
            mMediaPlayerHelper.pause();
        } else {
            mMediaPlayerHelper.play();
        }
        setPauseButtonImage();
    }

    /*  Use String.format() as little as possible to optimize efficiency.
    *   Reusing an existing Formatter significantly increases speed of makeTimeString().
    */
    private static String makeTimeString(long secs) {
        mFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;          // hours
        timeArgs[1] = secs / 60;            // mins
        timeArgs[2] = (secs / 60) % 60;     // secs
        timeArgs[3] = secs;                 // total secs
        timeArgs[4] = secs % 60;            // secs remainder

        return mFormatter.format(secs < 3600 ? durationShort : durationLong, timeArgs).toString();
    }

    //MediaPlayerHelperListener Methods

    //MediaPlayerHelperListener interface methods
    @Override
    public void onMediaPlayerReady() {
        if(DEBUG_LOG) Log.i(TAG,"onMediaPlayerReady() checkpoint!");
        // player is done preparing media; update our media duration
        mDuration = mMediaPlayerHelper.getDuration();
        mSeekBar.setMax(mDuration);
        mRemainingTimeLabel.setText(makeTimeString(mDuration/1000));

    }

    @Override
    public void onSeekComplete(final int position) {
        updateProgress(position);
        mHandler.postDelayed(mUpdateTimeTask, UPDATE_TIMER_INTERVAL);  // update every 100 milliseconds
    }

    @Override
    public void onPlayComplete() {
        updateProgress(mDuration);
    }

    @Override
    public void onMediaPlayerError(final int what, final int extra) {
        if(DEBUG_LOG) Log.e(TAG,"onMediaPlayerError("+what+","+extra+") detected!");
    }

    @Override
    public void onPlayStart() {
        if(DEBUG_LOG) Log.i(TAG,"onPlayStart() checkpoint!");
        setPauseButtonImage();  // sync our startMediaPlayback/pause button image
        mHandler.postDelayed(mUpdateTimeTask, UPDATE_TIMER_INTERVAL);  // update every 100 milliseconds
    }

    @Override
    public void onPlayEnd() {
        if(DEBUG_LOG) Log.i(TAG,"onPlayEnd() checkpoint!");
        setPauseButtonImage();  // sync our startMediaPlayback/pause button image
    }


    /**
     * background runnable thread used to update progress bar
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // Debug.LOGI(TAG,"mUpdateTimeTask run() checkpoint");
            if(mMediaPlayerHelper != null && mMediaPlayerHelper.isPlaying()) {
                // Obtain MediaPlayer time in milliseconds
                mProgress = mMediaPlayerHelper.getCurrentPosition();
                //if(DEBUG) Debug.LOGD(TAG,"mUpdateTimeTask() mProgress="+mProgress);

                // Display current duration
                mRemainingTimeLabel.setText(makeTimeString((long) (mDuration -mProgress) / 1000));

                // Update progress bar
                updateSeekBarProgress(mProgress);

                // Running this thread after a set number of milliseconds defined by TIMER_INTERVAL
                mHandler.postDelayed(this, UPDATE_TIMER_INTERVAL);

            }
        }
    };

    //OnSeekBarChangeListener interface methods
    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        // Do not update progress bar while it is being modified by user
        // Don't forget to enable updates once seekTo() is complete.
        if(mHandler!=null) mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        int request = seekBar.getProgress();
        if(DEBUG_LOG) Log.d(TAG,"onStopTrackingTouch() newProgress="+request);
        mProgress = request < mDuration ? request : mDuration-1000;
        mMediaPlayerHelper.seekTo(mProgress);
        updateProgress(mProgress);
    }
}
