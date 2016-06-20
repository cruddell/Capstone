package com.ruddell.museumofthebible.Ticketing;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ruddell.museumofthebible.R;

import io.card.payment.CreditCard;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    private EditText mCardField;
    private EditText mExpiryField;
    private EditText mCVCField;
    private TextView mChargeLabel;
    private int mQuantity = 0;
    public static double COST_PER_TICKET = 15.00;   //default setting for cost per ticket; get updated data from api

    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PaymentFragment.
     */
    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public void updateCostPerTicket(double newCost) {
        COST_PER_TICKET = newCost;
        updateQuantity(mQuantity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        mCardField = (EditText) view.findViewById(R.id.cardNumber);
        mExpiryField = (EditText) view.findViewById(R.id.expiry);
        mCVCField = (EditText) view.findViewById(R.id.cvc);
        mChargeLabel = (TextView) view.findViewById(R.id.chargeLabel);

        updateQuantity(mQuantity);

        return view;
    }

    public void updateQuantity(int quantity) {
        mQuantity = quantity;
        if (mChargeLabel!=null) mChargeLabel.setText("" + quantity + " x $" + COST_PER_TICKET + "/" + getResources().getString(R.string.ticket) + ": $" + quantity * COST_PER_TICKET);
    }

    public void updateWithCard(CreditCard creditCard) {
        mCardField.setText(creditCard.getRedactedCardNumber());
        mExpiryField.setText(creditCard.expiryMonth + creditCard.expiryYear);
        mCVCField.setText(creditCard.cvv);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
