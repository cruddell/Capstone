package com.ruddell.ticketingbackend;

/**
 * Created by chris on 2/21/16.
 */
public class PurchaseObject {
    String mFirstName = "";
    String mLastName = "";
    String mEmail = "";
    String mPhone = "";
    String mCardNumber = "";
    String mExpiration = "";
    String mCvc = "";
    int mNumberOfTickets = 0;

    public PurchaseObject() {

    }

    public PurchaseObject setFirstName(String firstName) {
        this.mFirstName = firstName;
        return this;
    }

    public PurchaseObject setLastName(String lastName) {
        this.mLastName = lastName;
        return this;
    }

    public PurchaseObject setEmail(String email) {
        this.mEmail = email;
        return this;
    }

    public PurchaseObject setPhone(String phone) {
        this.mPhone = phone;
        return this;
    }

    public PurchaseObject setCardNumber(String cardNumber) {
        this.mCardNumber = cardNumber;
        return this;
    }

    public PurchaseObject setExpiration(String expiration) {
        this.mExpiration = expiration;
        return this;
    }

    public PurchaseObject setCvc(String cvc) {
        this.mCvc = cvc;
        return this;
    }

    public PurchaseObject setNumberTickets(int numberTickets) {
        this.mNumberOfTickets = numberTickets;
        return this;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getExpiration() {
        return mExpiration;
    }

    public String getCvc() {
        return mCvc;
    }

    public int getNumberOfTickets() {
        return mNumberOfTickets;
    }
}
