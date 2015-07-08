package com.androdblite;

import android.content.Context;

import org.slf4j.LoggerFactory;

/**
 * Created by tgirard on 26/06/15
 */
public class AndroDbLite {

    public static final String TAG = "AndroDbLite";
    static org.slf4j.Logger logger = LoggerFactory.getLogger(AndroDbLite.TAG);

    public static void initSimple(Context context, String dbName, String DbVersion) {
        DbHelper.instance = new DbHelper(context, "TOTO", null, -1);
    }
}
