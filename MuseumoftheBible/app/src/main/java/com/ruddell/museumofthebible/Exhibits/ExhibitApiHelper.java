package com.ruddell.museumofthebible.Exhibits;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by chris on 2/21/16.
 */
public class ExhibitApiHelper {
    private static final String mBaseUrl = "http://motbedev.xyz/api/";

    private void getFeaturedExhibits(final ExhibitApiListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String apiResponse = staticGetRequest(mBaseUrl + "getFeaturedExhibits");

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(apiResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.jsonArrayRetrieved(jsonArray);

            }
        }).start();
    }


    private static String staticGetRequest(final String theURL) {
        String apiResponse = "";

        java.net.HttpURLConnection urlc = null;
        java.io.OutputStreamWriter out = null;
        java.io.DataOutputStream dataout = null;
        java.io.BufferedReader in = null;
        try {
            java.net.URL url = new java.net.URL(theURL);
            urlc = (java.net.HttpURLConnection) url.openConnection();
            urlc.setRequestMethod("GET");
            urlc.setDoOutput(false);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            //   urlc.setRequestProperty(HEADER_USER_AGENT, HEADER_USER_AGENT_VALUE);
            urlc.setRequestProperty("Content-Type","text/json");
            urlc.setRequestProperty("Accept","text/json");
            in = new java.io.BufferedReader(new java.io.InputStreamReader(urlc.getInputStream()),8096);

            // write html to System.out for debug
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            apiResponse = sb.toString();
            in.close();

            int status = urlc.getResponseCode();

            if(status != 200)
                android.util.Log.e("staticGetRequest", "HttpStatus is bad!:" + status);


            //return response;
        } catch (java.net.ProtocolException e) {
            android.util.Log.e("staticGetRequest","ProtocolException!");
            e.printStackTrace();
            apiResponse = e.toString();
            //return e.toString();
        } catch (java.io.IOException e) {
            android.util.Log.e("staticGetRequest","IOException! (1)");
            e.printStackTrace();
            apiResponse = e.toString();
            //return e.toString();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    //return  response;
                } catch (java.io.IOException e) {
                    android.util.Log.e("staticGetRequest","IOException! (2)");
                    e.printStackTrace();
                    apiResponse = e.toString();


                    //return e.toString();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException e) {
                    //e.printStackTrace();
                    android.util.Log.e("staticGetRequest","IOException! (3)");
                    e.printStackTrace();
                    apiResponse = e.toString();
                }
            }
        }
        return apiResponse;
    }

    public interface ExhibitApiListener {
        void jsonArrayRetrieved(JSONArray results);
    }
}
