package com.androdblite;

import android.test.AndroidTestCase;

import com.androdblite.repository.DbRepository;
import com.androdblite.repository.DbRepositoryImpl;

import java.io.File;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AndroDbLiteTest extends AndroidTestCase {


    public static final String DB_NAME = "myDbName.sqlite";
    private DbRepository repository;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final File[] files = mContext.getDatabasePath(DB_NAME).getParentFile().listFiles();
        if (files != null)
            for (File f : files) {
                boolean delete = f.delete();
            }
        repository = new DbRepositoryImpl(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}