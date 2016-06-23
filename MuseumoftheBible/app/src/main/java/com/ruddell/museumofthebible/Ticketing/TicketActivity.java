package com.ruddell.museumofthebible.Ticketing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.PrefUtils;
import com.ruddell.museumofthebible.utils.Utils;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class TicketActivity extends AppCompatActivity {

    private static final String TAG = "TicketActivity";
    private static final boolean DEBUG_LOG = true;
    private ViewPager mViewPager = null;
    private TicketingPageAdapter mAdapter = null;
    private final static int CARD_IO_REQUEST_CODE = 0x000001;
    private TicketingFragment mTicketingFragment;
    private PaymentFragment mPaymentFragment;
    private TicketFragment mTicketFragment;
    private Toolbar mToolbar;

    private static final int mViewPagerIndex_UserData = 0;
    private static final int mViewPagerIndex_Billing = 1;
    private static final int mViewPagerIndex_Ticket = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTicketingFragment = TicketingFragment.newInstance();
        mPaymentFragment = PaymentFragment.newInstance();
        mTicketFragment = TicketFragment.newInstance();
        mTicketFragment.setListener(new TicketFragment.TicketFragmentListener() {
            @Override
            public void onTicketMarkedUsed() {
                mViewPager.setCurrentItem(0);
            }
        });
        mAdapter = new TicketingPageAdapter(getFragmentManager(), mTicketingFragment, mPaymentFragment, mTicketFragment);
        mViewPager.setAdapter(mAdapter);


        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        if (PrefUtils.isTicketAvailable(this)) {
            mViewPager.setCurrentItem(mViewPagerIndex_Ticket, false);
            mTicketFragment.showTicket(TicketingBackendHelper.getTicketImageUri(this));
        }

        TicketingBackendHelper backendHelper = new TicketingBackendHelper(new TicketingBackendHelper.ApiListener() {
            @Override
            public void apiResultRetrieved(Uri result) {

            }

            @Override
            public void apiDoubleRetrieved(double value) {
                mPaymentFragment.updateCostPerTicket(value);
            }
        });
        backendHelper.getCostPerTicket(this);

        Utils.closeKeyboard(this);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void continuePressed(View v) {
        mPaymentFragment.updateQuantity(mTicketingFragment.getTicketInfo().numberTickets);
        mViewPager.setCurrentItem(mViewPagerIndex_Billing, true);
    }

    public void backPressed(View v) {
        mViewPager.setCurrentItem(mViewPagerIndex_UserData, true);
    }

    public void buyClicked(View v) {
        final TicketingFragment.TicketInfo ticketInfo = mTicketingFragment.getTicketInfo();
        TicketingBackendHelper backendHelper = new TicketingBackendHelper(new TicketingBackendHelper.ApiListener() {
            @Override
            public void apiResultRetrieved(Uri imageUri) {
                if (DEBUG_LOG) Log.d(TAG, "api result retrieved:" + imageUri.getPath());
                mTicketFragment.showTicket(imageUri);
                mViewPager.setCurrentItem(mViewPagerIndex_Ticket, true);
                PrefUtils.setTicketAvailable(TicketActivity.this, true);
            }

            @Override
            public void apiDoubleRetrieved(double value) {

            }
        });

        //purchase ticket -- do not send actual credit card number as this is a demo app!
        int numberOfTickets = mTicketingFragment.getTicketInfo().numberTickets;
        if (DEBUG_LOG) Log.d(TAG,"buying " + numberOfTickets + " tickets at a cost of $" + numberOfTickets * PaymentFragment.COST_PER_TICKET);
        PurchaseObject purchaseObject = new PurchaseObject();
        purchaseObject.cardNumber = "4242424242424242";     //send fake credit card number
        purchaseObject.firstName = ticketInfo.firstName;
        purchaseObject.lastName = ticketInfo.lastName;
        purchaseObject.email = ticketInfo.email;
        purchaseObject.phone = ticketInfo.phone;
        purchaseObject.numberOfTickets = ticketInfo.numberTickets;




        backendHelper.makePurchaseOnServer(this, purchaseObject);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CARD_IO_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                mPaymentFragment.updateWithCard(scanResult);
            }
            else {
                //scan was cancelled
            }
        }
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // CARD_IO_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, CARD_IO_REQUEST_CODE);
    }
}
