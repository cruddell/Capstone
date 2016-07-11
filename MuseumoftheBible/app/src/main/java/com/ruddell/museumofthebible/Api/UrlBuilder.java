package com.ruddell.museumofthebible.Api;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/9/16.
 */
public class UrlBuilder {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "UrlBuilder";

    private static final String BASE_URL_DEV = "http://192.254.187.228/udacity/";


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

    public static String audioUrl(String filename) {
        String url = exhibitUrl() + "audio/" + filename;
        return url;
    }

    public static String imageUrl(String filename) {
        String url = exhibitUrl() + "images/" + filename;
        return url;
    }
}
