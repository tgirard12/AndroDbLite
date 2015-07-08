package com.androdblite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tgirard on 26/06/15
 */
public class DbHelper extends SQLiteOpenHelper {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(AndroDbLite.TAG);

    protected static DbHelper instance;
    public Context context;

    public static final String ASSET_FOLDER = "androDbLite/";
    public static final String ASSET_CREATE_ = "dbcreate.sql";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            logger.debug("onCreateDb()");

            final String scriptFile = ASSET_FOLDER + ASSET_CREATE_;
            execSQL(db, scriptFile);

        } catch (Exception ex) {
            logger.error("onCreateDb() > error {}", ex.getMessage(), ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();

            logger.debug("onUpgrade() > From {} to {}", oldVersion, newVersion);
            for (int v = oldVersion + 1; v <= newVersion; v++) {
                final String scriptFile = ASSET_FOLDER + v + ".sql";
                execSQL(db, scriptFile);
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            logger.error("onUpgrade() > error {}", ex.getMessage(), ex);
        } finally {
            db.endTransaction();
        }
    }

    private void execSQL(SQLiteDatabase db, String scriptFile) throws IOException {
        final InputStream is = context.getAssets().open(scriptFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sql = new StringBuilder(1000);
        while ((line = reader.readLine()) != null) {

            sql.append(line);

            // End Of Script => Execute script
            if (line.contains(";")) {
                final String sqlToString = sql.toString();
                logger.debug("execSQL() >", sqlToString);
                db.execSQL(sqlToString);
            }
        }
    }
}
