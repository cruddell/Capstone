package com.ruddell.museumofthebible.Bible;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.Database.BibleDatabase;
import com.ruddell.museumofthebible.Database.BibleDatabaseCopier;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.views.CustomViewPager;

public class BibleActivity extends BaseActivity implements BibleBooksFragment.OnListFragmentInteractionListener, BibleChapterFragment.OnListFragmentInteractionListener {
    private static final String TAG = "BibleActivity";
    private static final boolean DEBUG = true;
    private CustomViewPager mViewpager;
    private BiblePagerAdapter mAdapter;
    private BibleChapterFragment mBibleChapterFragment;
    private BibleBooksFragment mBibleBooksFragment;
    private int mSelectedBookId = 0;
    private BibleChapterHelper.BibleChapterItem mChapterItem = null;
    private TextView mTitleView;

    public static final String ARG_BOOK_TO_LOAD = "book_to_load";
    public static final String ARG_BOOK_NAME = "book_name";
    public static final String ARG_CHAPTER_TO_LOAD = "chapter_to_load";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        setActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));

        mViewpager = (CustomViewPager) findViewById(R.id.viewPager);
        mTitleView = (TextView)findViewById(R.id.titleView);

        mAdapter = new BiblePagerAdapter(getFragmentManager());
        mViewpager.setAdapter(mAdapter);

        mTitleView.setText(BibleDatabaseCopier.TRANSLATION_NAMES[0]);

    }

    @Override
    public void onAttachFragment(final Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof BibleBooksFragment) mBibleBooksFragment = (BibleBooksFragment) fragment;
        else if (fragment instanceof BibleChapterFragment) mBibleChapterFragment = (BibleChapterFragment) fragment;
    }

    @Override
    public void onListFragmentInteraction(MyBibleBooksRecyclerViewAdapter.BibleBookItem item) {
        if (DEBUG) Log.d(TAG, "onListFragmentInteraction:" + item.longName);
        mSelectedBookId = item.id;
        mViewpager.setCurrentItem(1);
        mBibleChapterFragment.showChaptersForBook(item);
    }

    @Override
    public void onFragmentViewCreated(final Fragment fragment) {
        if (fragment instanceof BibleChapterFragment) {
            int bookToLoad = getIntent().getIntExtra(ARG_BOOK_TO_LOAD, 0);
            int chapterToLoad = getIntent().getIntExtra(ARG_CHAPTER_TO_LOAD, 0);
            String bookName = getIntent().getStringExtra(ARG_BOOK_NAME);
            if (chapterToLoad!=0 && bookToLoad!=0) {
                MyBibleBooksRecyclerViewAdapter.BibleBookItem item = new MyBibleBooksRecyclerViewAdapter.BibleBookItem(bookToLoad, "",bookName);
                onListFragmentInteraction(item);
                BibleChapterHelper.BibleChapterItem chapterItem = new BibleChapterHelper.BibleChapterItem("" + chapterToLoad);
                onListFragmentInteraction(chapterItem);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(BibleChapterHelper.BibleChapterItem item) {
        if (DEBUG) Log.d(TAG,"onListFragmentInteraction(" + item.chapterName + ")");
        mBibleChapterFragment.loadTextForChapter(item.chapterName);
        mChapterItem = item;
    }

    @Override
    public void onDataUpdated() {
        if (DEBUG) Log.d(TAG,"onDataUpdated");
        mBibleChapterFragment.refreshAdapter();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but we override this if the view pager has advanced forward and instead move it back one page.
     */
    @Override
    public void onBackPressed() {
        int bookToLoad = getIntent().getIntExtra(ARG_BOOK_TO_LOAD, 0);
        int chapterToLoad = getIntent().getIntExtra(ARG_CHAPTER_TO_LOAD, 0);
        if (bookToLoad!=0 && chapterToLoad!=0) finish();

        if (mBibleChapterFragment!=null && mBibleChapterFragment.bibleTextIsShown()) {
            if (DEBUG) Log.d(TAG, "onBackPressed -- bibleTextIsShown - fading back to chapter list...");
            mBibleChapterFragment.fadeContent(false);
        }
        else if (mViewpager.getCurrentItem()>0) {
            if (DEBUG) Log.d(TAG, "onBackPressed -- chapter list is shown ... going back to book list...");
            mViewpager.setCurrentItem(0);
        }
        else {
            if (DEBUG) Log.d(TAG, "onBackPressed -- on first page ... going back to home page...");
            super.onBackPressed();
        }
    }

    private class BiblePagerAdapter extends FragmentPagerAdapter {

        public BiblePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the number of views available.
         * Page 1 = Books of the Bible
         * Page 2 = Chapters for selected book
         */
        @Override
        public int getCount() {
            return 2;
        }


        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            if (mBibleChapterFragment==null) mBibleChapterFragment = BibleChapterFragment.newInstance(2, BibleActivity.this);
            if (mBibleBooksFragment==null) mBibleBooksFragment = BibleBooksFragment.newInstance(1, BibleActivity.this);
            return position==0 ? mBibleBooksFragment : position==1 ? mBibleChapterFragment : null;
        }


    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p/>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p/>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p/>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p/>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DEBUG) Log.d(TAG,"onCreateOptionsMenu");

        int index = 0;
        for (String dbName : BibleDatabaseCopier.TRANSLATION_NAMES) {
            MenuItem item = menu.add(0,index, 0, dbName);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            index++;
        }
        return true;
    }


    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p/>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int index = item.getItemId();
        String dbName = BibleDatabaseCopier.DATABASE_VERSIONS[index];

        mTitleView.setText(BibleDatabaseCopier.TRANSLATION_NAMES[index]);
        Log.d(TAG, "onOptionsItemSelected:" + dbName);
        BibleDatabase.getInstance(this).changeDatabase(this, dbName, null);

        mBibleBooksFragment.refreshData();
        mBibleChapterFragment.refreshAdapter();
        if (mChapterItem!=null) mBibleChapterFragment.loadTextForChapter(mChapterItem.chapterName);
        return super.onOptionsItemSelected(item);
    }
}
