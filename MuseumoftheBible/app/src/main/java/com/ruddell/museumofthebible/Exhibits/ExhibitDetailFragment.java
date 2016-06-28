package com.ruddell.museumofthebible.Exhibits;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruddell.museumofthebible.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExhibitDetailFragment extends Fragment {
    private int mPageNumber = 0;

    public ExhibitDetailFragment() {
        // Required empty public constructor
    }

    public static ExhibitDetailFragment newInstance(int pageNumber) {
        ExhibitDetailFragment fragment = new ExhibitDetailFragment();
        fragment.mPageNumber = pageNumber;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibit_detail, container, false);


        return view;
    }

}
