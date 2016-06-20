package com.ruddell.museumofthebible.Database;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by chris on 1/20/16.
 */
public class BibleDatabaseCopier {

    private static final String TAG = "BibleDatabaseCopier";
    private static final boolean DEBUG_LOG = true;
    public static final String[] DATABASE_VERSIONS = {
            "kjv.db",
            "asv.db"
    };
    public static final String[] TRANSLATION_NAMES = {
            "King James Version",
            "American Standard Version"
    };
    private static final String DATABASE_PATH = "/data/data/com.ruddell.museumofthebible/databases/";

    /**
     * Creates a empty database on the system and rewrites it with your a precompiled database.
     * */
    public static void createDataBases(final Context context) throws IOException {

        final BibleDatabase database = BibleDatabase.getInstance(context);

        boolean shouldCopyDatabases = false;
        for (final String dbName : DATABASE_VERSIONS) {
            boolean dbExist = checkDataBase(dbName);

            if(dbExist){
                //do nothing - database already exist
            }else{
                if (DEBUG_LOG) Log.d(TAG,"create database");
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our precompiled database.
                database.changeDatabase(context, dbName, new BibleDatabase.DatabaseListener() {
                    @Override
                    public void onDatabaseCreated() {
                        if (DEBUG_LOG) Log.d(TAG, "onDatabaseCreated()");

                    }

                    @Override
                    public void onDatabaseReady() {
                        if (DEBUG_LOG) Log.d(TAG,"onDatabaseReady()");
                        copyDataBase(context, dbName);
                    }
                });

            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private static boolean checkDataBase(String databaseName){

        boolean dbSizeTooSmall = false;
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + databaseName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            File dbFile = new File(myPath);
            dbSizeTooSmall = dbFile.length()<100000;    //if database size is small it must be the blank database!
            if (DEBUG_LOG) Log.d(TAG,databaseName + " size = " + dbFile.length() + " path=" + dbFile.getAbsolutePath());
        }
        catch (SQLiteCantOpenDatabaseException e) {
            if (DEBUG_LOG) Log.d(TAG,"db does not exist");
        }
        catch(Exception e){
            if (DEBUG_LOG) Log.d(TAG,"db does not exist");
        }
        finally {
            if(checkDB != null){
                checkDB.close();
            }
        }

        boolean dbExists = (checkDB != null && dbSizeTooSmall==false) ? true : false;
        if (DEBUG_LOG) Log.d(TAG,"checkDataBase(" + databaseName + ") = " + dbExists);
        return dbExists;
    }

    public static void unzip(Context myContext, String zipFileName, File targetDirectory) {
        try {
            AssetFileDescriptor fileDescriptor = null;
            fileDescriptor = myContext.getAssets().openFd(zipFileName);
            FileInputStream inStream = fileDescriptor.createInputStream();

            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(inStream));

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
                zis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {

                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private static void copyDataBase(Context myContext, String dbName){
        try {
            if (DEBUG_LOG) Log.d(TAG, "copyDataBase(" + dbName + ")");
            //Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(dbName);

            // Path to the just created empty db
            String outFileName = DATABASE_PATH + dbName;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

            if (DEBUG_LOG) Log.d(TAG,"finished copying database:" + dbName);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new Error("Error copying database");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Error("Unknown error copying database");
        }


    }
}
