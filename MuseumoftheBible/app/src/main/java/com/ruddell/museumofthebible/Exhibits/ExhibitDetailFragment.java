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


    public ExhibitDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exhibit_detail, container, false);
    }

}
