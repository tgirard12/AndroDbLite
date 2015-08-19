package com.androdblite;

import android.content.Context;
import android.util.Log;

/**
 * Created by tgirard on 26/06/15
 */
public class AndroDbLite {

    public static final String TAG = "AndroDbLite.";

    public static int logLevel = Log.INFO;

    public static void initSimple(Context context, String dbName, String DbVersion) {
        DbOpenHelper.instance = new DbOpenHelper(context, "TOTO", null, -1);
    }

    public static boolean vActive() {
        return logLevel >= Log.VERBOSE;
    }

    public static boolean dActive() {
        return logLevel >= Log.DEBUG;
    }

    public static boolean iActive() {
        return logLevel >= Log.INFO;
    }
    public static boolean eActive() {
        return logLevel >= Log.ERROR;
    }
}
