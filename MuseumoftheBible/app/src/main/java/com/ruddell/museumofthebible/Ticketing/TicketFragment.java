package com.ruddell.museumofthebible.Ticketing;


import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.PrefUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketFragment extends Fragment {
    private static final String TAG = "TicketFragment";
    private static final boolean DEBUG_LOG = true;
    private static final int OPTION_MENU_ITEM_MARK_USED = 0x000001;
    private ImageView mTicketView;
    private Uri mTicketImageUri = null;
    private TicketFragmentListener mListener;

    public TicketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TicketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TicketFragment newInstance() {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);
        mTicketView = (ImageView)view.findViewById(R.id.ticketView);

        if (mTicketImageUri!=null) showTicket(mTicketImageUri);

        ((TicketActivity)getActivity()).getToolbar().inflateMenu(R.menu.menu_ticket_fragment);
        ((TicketActivity)getActivity()).getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.clear_ticket) {
                    if (DEBUG_LOG) Log.d(TAG, "cleared");
                    PrefUtils.setTicketAvailable(getActivity(), false);
                    if (mListener!=null) mListener.onTicketMarkedUsed();
                }
                else if (DEBUG_LOG) Log.d(TAG,"unknown menu item clicked:" + item.getTitle());

                return false;
            }
        });
        ((TicketActivity)getActivity()).getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public void showTicket(Uri imageUri) {
        mTicketImageUri = imageUri;
        if (mTicketView!=null) mTicketView.setImageDrawable(Drawable.createFromPath(mTicketImageUri.getPath()));
    }

    public void setListener(TicketFragmentListener listener) {
        mListener = listener;
    }


    public interface TicketFragmentListener {
        void onTicketMarkedUsed();
    }
}
