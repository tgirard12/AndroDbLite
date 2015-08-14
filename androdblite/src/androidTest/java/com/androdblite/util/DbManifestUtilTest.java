package com.androdblite.util;

import android.test.AndroidTestCase;

/**
 * Created by tgirard on 30/06/15
 */
public class DbManifestUtilTest extends AndroidTestCase {

    public void testEmptyDbVersion_return_1() {
        assertEquals("Default DB version", 1, DbManifestUtil.getDatabaseVersion(mContext));
    }

    public void testEmptyDbName_return_AndroDbLite() {
        assertEquals("Default DB name", "AndroDbLite.sqlite", DbManifestUtil.getDatabaseName(mContext));
    }
}
