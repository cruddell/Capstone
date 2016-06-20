package com.ruddell.museumofthebible.Ticketing;

import com.google.gson.Gson;

/**
 * Created by chris on 2/21/16.
 */
public class PurchaseObject {
    String firstName = "";
    String lastName = "";
    String email = "";
    String phone = "";
    String cardNumber = "";
    String expiration = "";
    String cvc = "";
    int numberOfTickets = 0;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
