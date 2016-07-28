package com.ruddell.museumofthebible.Api;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/9/16.
 */
public class UrlBuilder {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "UrlBuilder";

    private static final String BASE_URL_DEV = "http://54.186.81.106/";


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
        String url = BASE_URL_DEV + "audio/" + filename;
        return url;
    }

    public static String imageUrl(String filename) {
        String url = BASE_URL_DEV + "images/" + filename;
        return url;
    }

    public static String registerGcmTokenUrl() {
        return BASE_URL_DEV + "gcm_registration.php";
    }
}
