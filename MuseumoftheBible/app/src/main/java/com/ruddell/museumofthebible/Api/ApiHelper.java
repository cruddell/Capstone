package com.ruddell.museumofthebible.Api;

import android.content.Context;
import android.util.Log;

import com.ruddell.museumofthebible.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/23/16.
 */

public class ApiHelper {
    private static final String TAG = "ApiHelper";
    private static final boolean DEBUG_LOG = true;

    /**
     * Retrieves a listing of the currently available featured exhibits
     * @param callback -- implementation of the NetworkCallback interface used to retrieve the results
     * @param context -- any valid context - will be used to return results back on the main thread
     */
    public static void getExhibits(final NetworkCallback callback, final Context context) {
        String url = UrlBuilder.exhibitUrl();
        final HttpSendObj sendObj = new HttpSendObj();
        sendObj.url = url;
        sendObj.method = UrlBuilder.HttpMethods.GET;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final HttpResponse response = performApiRequest(sendObj);
                Utils.runOnUiThread(context, new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponseReceived(response);
                    }
                });

            }
        }).start();
    }


    /**
     * Make HTTP request
     * @param httpSendObj - the object to send
     * @return HttpResponse - a response object
     */
    private static HttpResponse performApiRequest(HttpSendObj httpSendObj) {
        if (DEBUG_LOG) Log.d(TAG, "performApiRequest (" + httpSendObj.method.name() + "):" + httpSendObj.url);
        HttpResponse responseObject = new HttpResponse();


        HttpURLConnection urlc;

        java.io.BufferedReader in = null;
        try {
            URL url = new URL(httpSendObj.url);
            urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestMethod(httpSendObj.method.name());

            urlc.setDoOutput(httpSendObj.method == UrlBuilder.HttpMethods.POST || httpSendObj.method == UrlBuilder.HttpMethods.PUT);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);

            urlc.setRequestProperty("Accept", "application/json");

            if (httpSendObj.jsonArray!=null || httpSendObj.jsonObject!=null) {
                if (DEBUG_LOG) Log.d(TAG, "sending as type JSON");
                urlc.setRequestProperty("Content-Type","application/json");
                urlc.setRequestProperty("Content-Length", "" + (httpSendObj.jsonArray!=null ? httpSendObj.jsonArray.toString().length() : httpSendObj.jsonObject.toString().length()));
            }
            else {
                urlc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            }


            String dataAsString = httpSendObj.encodedString;
            if (httpSendObj.jsonObject!=null) dataAsString = httpSendObj.jsonObject.toString();
            else if (httpSendObj.jsonArray!=null) dataAsString = httpSendObj.jsonArray.toString();

            if (DEBUG_LOG && dataAsString.length()>0) Log.d(TAG, "dataAsString:" + dataAsString);

            if (dataAsString.length()>0) {
                DataOutputStream wr = new DataOutputStream(
                        urlc.getOutputStream());
                wr.writeBytes(dataAsString);
                wr.flush();
                wr.close();
            }

            int status = urlc.getResponseCode();
            responseObject.responseCode= status;
            boolean isError = false;
            if(status >= 300) {
                isError = true;
            }

            in = new java.io.BufferedReader(new java.io.InputStreamReader(isError ? urlc.getErrorStream() : urlc.getInputStream()),8096);

            // read server response
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            responseObject.responseBody = sb.toString();

            //return response;
        } catch (java.net.ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        responseObject.responseBody = (responseObject.responseBody!=null && responseObject.responseBody.length()>0) ? responseObject.responseBody : httpSendObj.url.length()<1 ? "Error:missing url" : "Error: (" + responseObject.responseCode + "):" + responseObject.responseBody;

        if (DEBUG_LOG) Log.d(TAG, "response:" + responseObject.responseBody);
        return responseObject;
    }

    static class HttpSendObj {
        public String url;
        public String encodedString = "";
        public JSONObject jsonObject;
        public JSONArray jsonArray;
        public UrlBuilder.HttpMethods method;

        @Override
        public String toString() {
            String retVal = "HttpSendObj{";
            if (encodedString.length()>0) retVal += "encodedString:" + encodedString;
            else if (jsonObject!=null) retVal += "jsonObject:" + jsonObject;
            else if (jsonArray!=null) retVal += "jsonArray:" + jsonArray;
            retVal += "}";
            return retVal;
        }


    }

    public static class HttpResponse {
        public String responseBody;
        public int responseCode;
    }

    public interface NetworkCallback {
        void onResponseReceived(HttpResponse response);
    }
}
