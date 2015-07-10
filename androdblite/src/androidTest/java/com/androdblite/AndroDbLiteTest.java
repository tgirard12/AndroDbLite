package com.androdblite;

import android.test.AndroidTestCase;

import com.androdblite.domain.EntityTest;
import com.androdblite.repository.EntityRepositoryImpl;

import java.io.File;
import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AndroDbLiteTest extends AndroidTestCase {


    public static final String DB_NAME = "myDbName.sqlite";
    private EntityRepositoryImpl repository;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final File[] files = mContext.getDatabasePath(DB_NAME).getParentFile().listFiles();
        if (files != null)
            for (File f : files) {
                boolean delete = f.delete();
            }
        repository = new EntityRepositoryImpl(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsert_Entity() {
        int base = 10;
        final EntityTest e = createEntityFromBaseValue(base);

        assertEquals(1, repository.insert(e));

        final EntityTest en = repository.findById(EntityTest.class, 1);
        assertEntityTest(base, en);
    }

    private void assertEntityTest(int base, EntityTest en) {
        assertEquals(++base, en.intValue);
        assertEquals(++base, en.integerNullableValue.intValue());
        assertEquals(++base, en.longValue);
        assertEquals(++base, en.longNullableValue.longValue());
        assertEquals((float) ++base, en.floatValue);
        assertEquals((float) ++base, en.floatNullableValue);
        assertEquals((double) ++base, en.doubleValue);
        assertEquals((double) ++base, en.doubleNullableValue);
        assertEquals(true, en.booleanValue);
        assertEquals(true, en.booleanNullableValue.booleanValue());
        assertEquals(new Date(2015, 5, 20), en.date);
        assertEquals(null, en.dateNullable);
        assertEquals("19", en.string);
        assertEquals(null, en.stringNoInsert);
        assertEquals(null, en.stringNoSelect);
        assertEquals(null, en.stringTransient);
    }

    private EntityTest createEntityFromBaseValue(int base) {
        final EntityTest e = new EntityTest();
        e.intValue = ++base;
        e.integerNullableValue = ++base;
        e.longValue = ++base;
        e.longNullableValue = (long) ++base;
        e.floatValue = ++base;
        e.floatNullableValue = (float) ++base;
        e.doubleValue = ++base;
        e.doubleNullableValue = (double) ++base;
        e.booleanValue = true;
        e.booleanNullableValue = true;
        e.date = new Date(2015, 5, 20);
        e.dateNullable = null;
        e.string = String.valueOf(++base);
        e.stringNoInsert = String.valueOf(++base);
        e.stringNoSelect = String.valueOf(++base);
        e.stringTransient = String.valueOf(++base);
        return e;
    }


}