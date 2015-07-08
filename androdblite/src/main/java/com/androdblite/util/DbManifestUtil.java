package com.androdblite.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.androdblite.AndroDbLite;

import org.slf4j.LoggerFactory;

/**
 * Created by tgirard on 30/06/15
 */
public class DbManifestUtil {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(AndroDbLite.TAG);

    public final static String METADATA_DB_NAME = "DB_NAME";
    public final static String METADATA_DB_VERSION = "DB_VERSION";
    // TODO public final static String METADATA_LOG = "QUERY_LOG";
    // TODO public final static String METADATA_LOG_LEVEL = "QUERY_LOG";

    public final static String DATABASE_DEFAULT_NAME = "androDbLite.sqlite";

    public static int getDatabaseVersion(Context context) {

        logger.debug("myDebug Test");

        final ApplicationInfo ai = getApplicationInfo(context);
        if (ai == null || ai.metaData == null)
            return 1;

        final int version = ai.metaData.getInt(METADATA_DB_VERSION);
        if (version == 0)
            return 1;

        return version;
    }

    public static String getDatabaseName(Context context) {
        final ApplicationInfo ai = getApplicationInfo(context);
        if (ai == null || ai.metaData == null)
            return DATABASE_DEFAULT_NAME;

        final String dbName = ai.metaData.getString(METADATA_DB_NAME);
        if (dbName == null)
            return DATABASE_DEFAULT_NAME;

        return dbName;
    }

    private static ApplicationInfo getApplicationInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(AndroDbLite.TAG, "Unable to retreive METADATA info");
            return null;
        }
    }
}
