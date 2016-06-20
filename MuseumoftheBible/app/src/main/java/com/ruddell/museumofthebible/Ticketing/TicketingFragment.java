package com.ruddell.museumofthebible.Ticketing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ruddell.museumofthebible.R;



public class TicketingFragment extends Fragment {

    private EditText mNameField;
    private EditText mPhoneField;
    private EditText mEmailField;
    private EditText mNumberTicketsField;

    public TicketingFragment() {
        // Required empty public constructor
    }


    public static TicketingFragment newInstance() {
        TicketingFragment fragment = new TicketingFragment();
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
        View view = inflater.inflate(R.layout.fragment_ticketing, container, false);
        mNameField = (EditText)view.findViewById(R.id.nameField);
        mPhoneField = (EditText)view.findViewById(R.id.phoneField);
        mEmailField = (EditText)view.findViewById(R.id.emailField);
        mNumberTicketsField = (EditText)view.findViewById(R.id.numberTicketsField);
        return view;
    }

    public TicketInfo getTicketInfo() {
        TicketInfo ticketInfo = new TicketInfo();
        String name = mNameField.getText().toString();
        int space_separator = name.indexOf(" ");
        if (space_separator>0) {
            ticketInfo.firstName = name.substring(0, space_separator);
            ticketInfo.lastName = name.substring(space_separator+1);
        }
        else ticketInfo.firstName = name;
        ticketInfo.email = mEmailField.getText().toString();
        ticketInfo.phone = mPhoneField.getText().toString();

        ticketInfo.numberTickets = is_number(mNumberTicketsField.getText().toString()) ? Integer.parseInt(mNumberTicketsField.getText().toString()) : 0;

        return ticketInfo;
    }

    private boolean is_number(String number) {
        boolean isNumber = false;
        try {
            int numberVal = Integer.parseInt(number);
            isNumber = true;
        }
        catch (Exception e) {

        }
        return isNumber;
    }


    public class TicketInfo {
        String userName;
        String firstName;
        String lastName;
        String phone;
        String email;
        int numberTickets;
    }
}
