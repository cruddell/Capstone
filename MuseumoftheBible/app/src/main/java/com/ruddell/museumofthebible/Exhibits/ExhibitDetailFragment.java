package com.ruddell.museumofthebible.Exhibits;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibit_detail, container, false);
        


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
