package com.ruddell.museumofthebible.Exhibits;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ruddell.museumofthebible.R;


public class ImageFragment extends Fragment {
    private ImageView mImageView;

    public ImageFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image, container, false);

        mImageView = (ImageView)view.findViewById(R.id.imageView3);
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.img3500,getActivity().getTheme()));

        return view;
    }


}
