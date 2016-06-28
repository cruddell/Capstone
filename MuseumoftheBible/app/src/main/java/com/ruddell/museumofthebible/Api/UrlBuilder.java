package com.ruddell.museumofthebible.Api;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/9/16.
 */
public class UrlBuilder {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "UrlBuilder";

    private static final String BASE_URL_DEV = "http://churchphoneapps.com/udacity/";


    enum HttpMethods {
        GET,
        POST,
        PUT,
        DELETE
    }

    public static String exhibitUrl() {
        String url = BASE_URL_DEV + "exhibits/";
        return url;
    }


}
