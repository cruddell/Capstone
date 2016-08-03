package com.ruddell.museumofthebible.Exhibits;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruddell.museumofthebible.Exhibits.model.Exhibit;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.Utils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExhibitDetailFragment extends Fragment {
    private static final String TAG = "ExhibitDetailFragment";
    private static final boolean DEBUG_LOG = true;
    private int mPageNumber = 0;
    private Exhibit mExhibit;
    private ExhibitCallback mCallback;
    private TextView mDetailTextView;

    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_EXHIBIT = "exhibit";

    private boolean mIsVisible = false;



    public ExhibitDetailFragment() {
        // Required empty public constructor
    }

    public static ExhibitDetailFragment newInstance(int pageNumber, Exhibit exhibit, ExhibitCallback callback) {
        ExhibitDetailFragment fragment = new ExhibitDetailFragment();
        fragment.mPageNumber = pageNumber;
        fragment.mExhibit = exhibit;
        fragment.mCallback = callback;
        return fragment;
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_PAGE_NUMBER, mPageNumber);
        outState.putParcelable(ARG_EXHIBIT, mExhibit);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibit_detail, container, false);
        
        if (savedInstanceState!=null) {
            mPageNumber = savedInstanceState.getInt(ARG_PAGE_NUMBER);
            mExhibit = savedInstanceState.getParcelable(ARG_EXHIBIT);
            if (getActivity() instanceof ExhibitCallback) mCallback = (ExhibitCallback)getActivity();
        }

        TextView titleView = (TextView)view.findViewById(R.id.titleView);
        titleView.setText(mExhibit.title);


        mDetailTextView = (TextView)view.findViewById(R.id.detailTextView);


        ImageView backgroundImage = (ImageView)view.findViewById(R.id.backgroundImage);
        File directory = Utils.getExternalDirectory();
        File outputFile = new File(directory, mExhibit.imageName);
        Uri imageUri = Uri.fromFile(outputFile);
        String pathName = outputFile.getPath();

        mDetailTextView.setText(mExhibit.description);

        Drawable drawable = Drawable.createFromPath(imageUri.getPath());
        if (drawable!=null) {
            backgroundImage.setImageDrawable(drawable);
        }
        else if (DEBUG_LOG) Log.e(TAG,"unable to set background image:" + pathName);


        return view;
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mCallback!=null) mCallback.onFragmentShown(isVisibleToUser);
    }

    protected interface ExhibitCallback {
        void onFragmentShown(boolean isVisible);
    }

}
