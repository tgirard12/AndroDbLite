package com.androdblite;

import android.test.AndroidTestCase;

import com.androdblite.domain.EntityTest;
import com.androdblite.repository.EntityRepositoryImpl;

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
        boolean delete = mContext.getDatabasePath(DB_NAME).delete();
        repository = new EntityRepositoryImpl(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsert_Entity() {
        final EntityTest e = createEntityFromBaseValue(10);

        assertEquals(1, repository.insert(e));

        final EntityTest en = repository.findById(EntityTest.class, 1);
        assertEquals(1, en.intValue);
        assertEquals(2, en.integerNullableValue.intValue());
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
        e.dateNullable = new Date();
        e.string = String.valueOf(++base);
        e.stringNoInsert = String.valueOf(++base);
        e.stringNoSelect = String.valueOf(++base);
        e.stringTransient = String.valueOf(++base);
        return e;
    }


}