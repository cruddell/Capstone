package com.ruddell.museumofthebible.Ticketing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * Created by chris on 1/20/16.
 */
public class TicketingPageAdapter extends FragmentPagerAdapter {
    private TicketingFragment mTicketingFragment;
    private PaymentFragment mPaymentFragment;
    private TicketFragment mTicketFragment;

    public TicketingPageAdapter(FragmentManager fm, TicketingFragment ticketingFragment, PaymentFragment paymentFragment, TicketFragment ticketFragment) {
        super(fm);
        mTicketingFragment = ticketingFragment;
        mPaymentFragment = paymentFragment;
        mTicketFragment = ticketFragment;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mTicketingFragment;
            case 1:
                return mPaymentFragment;
            case 2:
                return mTicketFragment;
            default:
                return mTicketingFragment;
        }


    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }
}
