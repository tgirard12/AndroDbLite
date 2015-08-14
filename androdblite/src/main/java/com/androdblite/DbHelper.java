package com.androdblite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tgirard on 26/06/15
 */
public class DbHelper extends SQLiteOpenHelper {

//    static Logger logger = LoggerFactory.getLogger(AndroDbLite.TAG);

    protected static DbHelper instance;
    public Context context;

    public static final String ASSET_FOLDER = "AndroDbLite/";
    public static final String ASSET_CREATE_ = "dbcreate.sql";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            if (AndroDbLite.iActive())
                Log.i(AndroDbLite.TAG, "onCreate()");

            final String scriptFile = ASSET_FOLDER + ASSET_CREATE_;
            execSQL(db, scriptFile);

            if (AndroDbLite.iActive())
                Log.i(AndroDbLite.TAG, "onCreate() > OK");

        } catch (Exception ex) {
            Log.e(AndroDbLite.TAG, "onCreate() > " + ex.getMessage(), ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();
            if (AndroDbLite.iActive())
                Log.i(AndroDbLite.TAG, "onUpgrade()");
            if (AndroDbLite.dActive())
                Log.d(AndroDbLite.TAG, "onUpgrade() > from v:" + oldVersion + " to:" + newVersion);

            for (int v = oldVersion + 1; v <= newVersion; v++) {
                final String scriptFile = ASSET_FOLDER + v + ".sql";
                execSQL(db, scriptFile);
            }
            db.setTransactionSuccessful();

            if (AndroDbLite.iActive())
                Log.i(AndroDbLite.TAG, "onUpgrade() > OK");

        } catch (Exception ex) {
            Log.e(AndroDbLite.TAG, "onCreate() > " + ex.getMessage(), ex);
        } finally {
            db.endTransaction();
        }
    }

    private void execSQL(SQLiteDatabase db, String scriptFile) throws IOException {

        if (AndroDbLite.dActive())
            Log.d(AndroDbLite.TAG, "execSQL() > file:" + scriptFile);

        final InputStream is = context.getAssets().open(scriptFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sql = new StringBuilder(1000);
        while ((line = reader.readLine()) != null) {

            sql.append(line);

            // End Of Script => Execute script
            if (line.contains(";")) {
                final String sqlToString = sql.toString();

                if (AndroDbLite.dActive())
                    Log.d(AndroDbLite.TAG, "execSQL() >\n" + sqlToString);
                db.execSQL(sqlToString);
                sql = new StringBuilder(1000);
            }
        }
        if (AndroDbLite.vActive())
            Log.v(AndroDbLite.TAG, "execSQL() > OK");
    }
}
