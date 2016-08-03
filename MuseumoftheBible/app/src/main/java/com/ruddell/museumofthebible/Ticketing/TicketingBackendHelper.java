package com.ruddell.museumofthebible.Ticketing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ruddell.museumofthebible.utils.Utils;
import com.ruddell.ticketingbackend.ticketApi.TicketApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.ruddell.ticketingbackend.ticketApi.model.Ticket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chris on 2/20/16.
 */
public class TicketingBackendHelper {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "TicketingBackendHelper";
    private ApiListener mListener;
    private static final String mApiUrl_localhost = "http://10.0.2.2:8080/_ah/api/";
    private static final String mApiUrl_deployed = "https://nanodegree-capstone.appspot.com/_ah/api/";
    private static final String mApiUrl = mApiUrl_deployed;
    private static final String ticketImageFileName = "qr_code.png";

    public TicketingBackendHelper(ApiListener mListener) {
        this.mListener = mListener;
    }

    public void getCostPerTicket(final Activity context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TicketApi myApiService = null;
                TicketApi.Builder builder = new TicketApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl(mApiUrl)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
                try {
                    final double costPerTicket = myApiService.getCostPerTicket().execute().getData();
                    if (mListener!=null) {
                        Utils.runOnUiThread(context, new Runnable() {
                            @Override
                            public void run() {
                                mListener.apiDoubleRetrieved(costPerTicket);
                            }
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Make purchase on the server using the card number and the number of tickets (server will control cost per ticket)
     * @param context -- calling activity
     * @param purchaseObject -- NOTE: DO NOT SEND ACTUAL CARD NUMBER TO SERVER! THIS IS A DEMO APP ONLY!
     */
    public void makePurchaseOnServer(final Activity context, final PurchaseObject purchaseObject) {
        //delete existing ticket if exists
        File file = new File(context.getFilesDir(), ticketImageFileName);
        if (file.exists()) file.delete();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, send the info to the server in the background and retrieve the qr code url
                TicketApi myApiService = null;
                TicketApi.Builder builder = new TicketApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl(mApiUrl)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });

                // end options for devappserver

                myApiService = builder.build();
                String qrcodeUrl = "";
                try {
                    String jsonString = purchaseObject.toString();
                    if (DEBUG_LOG) Log.d(TAG, "sending purchase object to api:");
                    if (DEBUG_LOG) Log.d(TAG, jsonString);
                    com.ruddell.ticketingbackend.ticketApi.model.PurchaseObject apiObject = new com.ruddell.ticketingbackend.ticketApi.model.PurchaseObject()
                            .setFirstName(purchaseObject.firstName)
                            .setLastName(purchaseObject.lastName)
                            .setEmail(purchaseObject.email)
                            .setPhone(purchaseObject.phone)
                            .setCardNumber(purchaseObject.cardNumber)
                            .setExpiration(purchaseObject.expiration)
                            .setCvc(purchaseObject.cvc)
                            ;
                    Ticket ticket = myApiService.purchaseTicket(apiObject).execute();
                    qrcodeUrl = ticket.getTicketUrl();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //second, download qr code image and save to disk
                URL url = null;

                File imageFile = new File(context.getFilesDir(), ticketImageFileName);

                try {
                    url = new URL(qrcodeUrl);
                    InputStream in = new BufferedInputStream(url.openStream());
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile));

                    for ( int i; (i = in.read()) != -1; ) {
                        out.write(i);
                    }
                    in.close();
                    out.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //finally, notify listener
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener!=null) mListener.apiResultRetrieved(getTicketImageUri(context));
                    }
                });


            }
        }).start();


    }

    public static Uri getTicketImageUri(Context context) {
        return Uri.fromFile(new File(context.getFilesDir(), ticketImageFileName));
    }


    public interface ApiListener {
        void apiResultRetrieved(Uri result);
        void apiDoubleRetrieved(double value);
    }



}
