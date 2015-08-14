package com.androdblite.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.androdblite.AndroDbLite;
import com.androdblite.AndroDbLiteError;


/**
 * Created by tgirard on 30/06/15
 */
public class DbManifestUtil {

    public final static String METADATA_DB_NAME = "DB_NAME";
    public final static String METADATA_DB_VERSION = "DB_VERSION";

    public final static String DATABASE_DEFAULT_NAME = "AndroDbLite.sqlite";

    public static int getDatabaseVersion(Context context) {

        final ApplicationInfo ai = getApplicationInfo(context);
        if (ai == null || ai.metaData == null) {
            if (AndroDbLite.dActive()) {
                Log.d(AndroDbLite.TAG, "getDatabaseVersion() > manifest DB_VERSION not found");
                Log.d(AndroDbLite.TAG, "getDatabaseVersion() > DB_VERSION set to: " + 1);
            }
            return 1;
        }

        final int version = ai.metaData.getInt(METADATA_DB_VERSION);
        if (version == 0)
            throw new AndroDbLiteError("DB_VERSION must be a positive int");

        return version;
    }

    public static String getDatabaseName(Context context) {
        final ApplicationInfo ai = getApplicationInfo(context);
        if (ai == null || ai.metaData == null) {
            if (AndroDbLite.dActive()) {
                Log.d(AndroDbLite.TAG, "getDatabaseName() > manifest DB_NAME not found");
                Log.d(AndroDbLite.TAG, "getDatabaseName() > DB_NAME set to: " + DATABASE_DEFAULT_NAME);
            }
            return DATABASE_DEFAULT_NAME;
        }

        final String dbName = ai.metaData.getString(METADATA_DB_NAME);
        if (dbName == null) {
            if (AndroDbLite.dActive()) {
                Log.d(AndroDbLite.TAG, "getDatabaseName() > manifest DB_NAME not found ");
                Log.d(AndroDbLite.TAG, "getDatabaseName() > DB_NAME set to: " + DATABASE_DEFAULT_NAME);
            }
            return DATABASE_DEFAULT_NAME;
        }
        Log.d(AndroDbLite.TAG, "getDatabaseName() > DbName found: " + dbName);
        return dbName;
    }

    private static ApplicationInfo getApplicationInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(AndroDbLite.TAG, "getApplicationInfo() > Unable to retreive METADATA info");
            return null;
        }
    }
}
