package com.ruddell.museumofthebible.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/21/16.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static final boolean DEBUG_LOG = true;

    public static final String DIRECTORY_MOTB = "motb";

    public static void closeKeyboard(Activity context) {
        View currentFocus = context.getCurrentFocus();
        if (currentFocus!=null){
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void runOnUiThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getApplicationContext().getMainLooper());
        mainHandler.post(runnable);
    }

    public static boolean doesCachedFileExist(String fileName, Context context){
        boolean ret = false;
        try{

            File file = context.getFileStreamPath(fileName);
            if(file.exists()){
                ret = true;
            }
        }catch(Exception e){
            ret = false;
        }

        if(ret){
            return true;
        }else{
            return false;
        }

    }

    //get image (drawable) from cache...
    public static Drawable getDrawableFromCache(String fileName, Context context){
        Drawable d = null;
        try{
            FileInputStream fin = context.openFileInput(fileName);
            if(fin != null){
                d = Drawable.createFromStream(fin, null);
                fin.close();
            }else{
                //could not find file
            }
        }catch(Exception e){
            //exception
            e.printStackTrace();
        }
        return d;

    }

    //save image to cache
    public static void saveImageToCache(Bitmap theImage, String saveAsFileName, Context context){
        try{
            if(saveAsFileName.length() > 3 && theImage != null){
                FileOutputStream fos = context.openFileOutput(saveAsFileName, Context.MODE_PRIVATE);
                theImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }






    public static File getExternalDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), DIRECTORY_MOTB);

        if (!directory.exists())
            directory.mkdirs();

        return directory;
    }

    public static String saveToDevice(byte[] data, String filename) {
        File directory = getExternalDirectory();
        File outputFile = new File(directory, filename);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(data);
        } catch (Exception e) {
            Log.e(TAG,Log.getStackTraceString(e));
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e1) {
                Log.e(TAG,Log.getStackTraceString(e1));
            }
        }
        return outputFile.getAbsolutePath();
    }

    public static boolean fileExists(String fileName) {
        File directory = getExternalDirectory();
        File file = new File(directory, fileName);
        return file.exists();
    }

    public static byte[] retrieveFromDevice(String fileName) {
        File directory = getExternalDirectory();
        File file = new File(directory, fileName);

        if (!file.exists()) return null;

        return retrieveFromDevice(file);
    }

    public static byte[] retrieveFromDevice(File fileToRetrieve) {
        byte[] data = new byte[0];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileToRetrieve);
            data = getBytesFromInputStream(inputStream);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                Log.e(TAG,Log.getStackTraceString(e));
            }
        }
        return data;
    }

    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[inputStream.available()];
        int byteOffset = 0;
        int bytesRead = 0;

        // bytesRead will be -1 if the end of the stream has been reached
        while ((bytesRead = inputStream.read(buffer, byteOffset, buffer.length - byteOffset)) > -1
                && byteOffset < buffer.length) {
            byteOffset += bytesRead;
        }

        if (byteOffset < buffer.length) {
            throw new IOException("Failure to read entire file"); }
        inputStream.close();

        return buffer;
    }
}
