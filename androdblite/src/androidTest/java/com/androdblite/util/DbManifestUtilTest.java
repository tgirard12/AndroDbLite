package com.androdblite.util;

import android.test.AndroidTestCase;

import com.androdblite.AndroDbLite;

import org.slf4j.LoggerFactory;

/**
 * Created by tgirard on 30/06/15
 */
public class DbManifestUtilTest extends AndroidTestCase {

    public void testEmptyDbVersion_return_1() {
        assertEquals("Default DB version", 1, DbManifestUtil.getDatabaseVersion(mContext));
    }

    public void testEmptyDbName_return_AndroDbLite() {
        assertEquals("Default DB name", "androDbLite.sqlite", DbManifestUtil.getDatabaseName(mContext));
    }
}
