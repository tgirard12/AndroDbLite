package com.androdblite.repository;

import android.test.AndroidTestCase;

import com.androdblite.domain.DbEntityServerTest;
import com.androdblite.domain.DbEntityTest;
import com.androdblite.domain.EntityTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by tgirard on 16/07/15
 */
public class DbRepositoryImplTest extends AndroidTestCase {

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

    public void testCount_EntityTest() {
        insert_2_entityTest();

        assertEquals(2, repository.count(EntityTest.class));
        assertEquals(1, repository.count(EntityTest.class, "myIntValue > ?", new String[]{"200"}));
    }

    public void testCount_DbEntityTest() {
        insert_2_DbEntityTest();

        assertEquals(2, repository.count(DbEntityTest.class));
        assertEquals(1, repository.count(DbEntityTest.class, "intValue > ?", new String[]{"200"}));
    }

    public void testCount_DbEntityServerTest() {
        insert_2_DbEntityServerTest();

        assertEquals(2, repository.count(DbEntityServerTest.class));
        assertEquals(1, repository.count(DbEntityServerTest.class, "intValue > ?", new String[]{"200"}));
    }

    public void testDelete_EntityTest() {
        insert_2_entityTest();

        repository.delete(EntityTest.class, "myIntValue > ?", new String[]{"200"});
        assertEquals(1, repository.count(EntityTest.class));
    }

    public void testDelete_DbEntityTest() {
        insert_2_DbEntityTest();

        repository.delete(DbEntityTest.class, "intValue > ?", new String[]{"200"});
        assertEquals(1, repository.count(DbEntityTest.class));
    }

    public void testDelete_DbEntityServerTest() {
        insert_2_DbEntityServerTest();

        repository.delete(DbEntityServerTest.class, "intValue > ?", new String[]{"200"});
        assertEquals(1, repository.count(DbEntityServerTest.class));
    }

    public void testDeleteById_DbEntityTest() {
        final DbEntityTest e1 = createDbEntityTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityTest e2 = createDbEntityTestFromBaseValue(200);
        repository.insert(e2);

        repository.deleteById(e1);
        assertEquals(1, repository.count(DbEntityTest.class));
        assertEquals(2, repository.findAll(DbEntityTest.class).get(0).getId());
    }

    public void testDeleteById_DbEntityServerTest() {
        final DbEntityServerTest e1 = createDbEntityServerTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityServerTest e2 = createDbEntityServerTestFromBaseValue(200);
        repository.insert(e2);

        repository.deleteById(e1);
        assertEquals(1, repository.count(DbEntityServerTest.class));
        assertEquals(2, repository.findAll(DbEntityServerTest.class).get(0).getId());
    }

    public void testDeleteByIdServer_DbEntityServerTest() {
        final DbEntityServerTest e1 = createDbEntityServerTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityServerTest e2 = createDbEntityServerTestFromBaseValue(200);
        repository.insert(e2);

        repository.deleteByIdServer(e1);
        assertEquals(1, repository.count(DbEntityServerTest.class));
        assertEquals("213", repository.findAll(DbEntityServerTest.class).get(0).getIdServer());
    }

    public void testDeleteListById_DbEntityTest() {
        final DbEntityTest e1 = createDbEntityTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityTest e2 = createDbEntityTestFromBaseValue(200);
        repository.insert(e2);
        final DbEntityTest e3 = createDbEntityTestFromBaseValue(300);
        repository.insert(e3);

        repository.deleteById(Arrays.asList(e1, e2));
        assertEquals(1, repository.count(DbEntityTest.class));
        assertEquals(3, repository.findAll(DbEntityTest.class).get(0).getId());
    }

    public void testDeleteListById_DbEntityServerTest() {
        final DbEntityServerTest e1 = createDbEntityServerTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityServerTest e2 = createDbEntityServerTestFromBaseValue(200);
        repository.insert(e2);
        final DbEntityServerTest e3 = createDbEntityServerTestFromBaseValue(300);
        repository.insert(e3);

        repository.deleteById(Arrays.asList(e1, e2));
        assertEquals(1, repository.count(DbEntityServerTest.class));
        assertEquals(3, repository.findAll(DbEntityServerTest.class).get(0).getId());
    }

    public void testDeleteListByIdServer_DbEntityServerTest() {
        final DbEntityServerTest e1 = createDbEntityServerTestFromBaseValue(100);
        repository.insert(e1);
        final DbEntityServerTest e2 = createDbEntityServerTestFromBaseValue(200);
        repository.insert(e2);
        final DbEntityServerTest e3 = createDbEntityServerTestFromBaseValue(300);
        repository.insert(e3);

        repository.deleteByIdServer(Arrays.asList(e1, e3));
        assertEquals(1, repository.count(DbEntityServerTest.class));
        assertEquals("213", repository.findAll(DbEntityServerTest.class).get(0).getIdServer());
    }

    public void testDeleteListByIdInTx_DbEntityTest() {
        final ArrayList<DbEntityTest> list = new ArrayList<>();

        list.add(createDbEntityTestFromBaseValue(100));
        repository.insert(list.get(0));
        list.add(createDbEntityTestFromBaseValue(200));
        repository.insert(list.get(1));
        list.add(createDbEntityTestFromBaseValue(300));
        repository.insert(list.get(2));

        list.add(null);

        try {
            repository.deleteByIdInTx(list);
        } catch (Exception ignored) {
        }

        assertEquals(3, repository.count(DbEntityTest.class));
        assertEquals(1, repository.findAll(DbEntityTest.class).get(0).getId());
        assertEquals(2, repository.findAll(DbEntityTest.class).get(1).getId());
        assertEquals(3, repository.findAll(DbEntityTest.class).get(2).getId());
    }

    public void testDeleteListByIdInTx_DbEntityServerTest() {
        final ArrayList<DbEntityServerTest> list = new ArrayList<>();

        list.add(createDbEntityServerTestFromBaseValue(100));
        repository.insert(list.get(0));
        list.add(createDbEntityServerTestFromBaseValue(200));
        repository.insert(list.get(1));
        list.add(createDbEntityServerTestFromBaseValue(300));
        repository.insert(list.get(2));

        list.add(null);

        try {
            repository.deleteByIdInTx(list);
        } catch (Exception ignored) {
        }

        assertEquals(3, repository.count(DbEntityServerTest.class));
        assertEquals(1, repository.findAll(DbEntityServerTest.class).get(0).getId());
        assertEquals(2, repository.findAll(DbEntityServerTest.class).get(1).getId());
        assertEquals(3, repository.findAll(DbEntityServerTest.class).get(2).getId());
    }

    public void testDeleteListByIdServerInTx_DbEntityServerTest() {
        final ArrayList<DbEntityServerTest> list = new ArrayList<>();

        list.add(createDbEntityServerTestFromBaseValue(100));
        repository.insert(list.get(0));
        list.add(createDbEntityServerTestFromBaseValue(200));
        repository.insert(list.get(1));
        list.add(createDbEntityServerTestFromBaseValue(300));
        repository.insert(list.get(2));

        list.add(null);

        try {
            repository.deleteByIdServerInTx(list);
        } catch (Exception ignored) {
        }

        assertEquals(3, repository.count(DbEntityServerTest.class));
        assertEquals("113", repository.findAll(DbEntityServerTest.class).get(0).getIdServer());
        assertEquals("213", repository.findAll(DbEntityServerTest.class).get(1).getIdServer());
        assertEquals("313", repository.findAll(DbEntityServerTest.class).get(2).getIdServer());
    }

    public void testFindAll_EntityTest() {
        insert_4_entityTest();

        repository.findAll(EntityTest.class);
        assertEquals(4, repository.count(EntityTest.class));
        assertEntityTest(100, repository.findAll(EntityTest.class).get(0));
        assertEntityTest(200, repository.findAll(EntityTest.class).get(1));
        assertEntityTest(300, repository.findAll(EntityTest.class).get(2));
        assertEntityTest(400, repository.findAll(EntityTest.class).get(3));
    }

    public void testFindAll_DbEntityTest() {
        insert_4_DbEntityTest();

        repository.findAll(DbEntityTest.class);
        assertEquals(4, repository.count(DbEntityTest.class));
        assertDbEntityTest(100, repository.findAll(DbEntityTest.class).get(0));
        assertDbEntityTest(200, repository.findAll(DbEntityTest.class).get(1));
        assertDbEntityTest(300, repository.findAll(DbEntityTest.class).get(2));
        assertDbEntityTest(400, repository.findAll(DbEntityTest.class).get(3));
    }

    public void testFindAll_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        repository.findAll(DbEntityServerTest.class);
        assertEquals(4, repository.count(DbEntityServerTest.class));
        assertDbEntityServerTest(100, repository.findAll(DbEntityServerTest.class).get(0));
        assertDbEntityServerTest(200, repository.findAll(DbEntityServerTest.class).get(1));
        assertDbEntityServerTest(300, repository.findAll(DbEntityServerTest.class).get(2));
        assertDbEntityServerTest(400, repository.findAll(DbEntityServerTest.class).get(3));
    }

    public void testFindById_DbEntityTest() {
        insert_4_DbEntityTest();

        final DbEntityTest dbEntityTest = repository.findById(DbEntityTest.class, 3);
        assertDbEntityTest(300, dbEntityTest);
    }

    public void testFindByIdServer_DbEntityTest() {
        insert_4_DbEntityServerTest();

        final DbEntityServerTest dbEntityServerTest = repository.findByIdServer(DbEntityServerTest.class, "213");
        assertDbEntityServerTest(200, dbEntityServerTest);
    }

    public void testFind_Entity() {
        insert_4_entityTest();

        assertEquals(1, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"101"}).size());
        assertEntityTest(200, repository.find(EntityTest.class, "myIntValue > ?", new String[]{"200"}).get(0));

        assertEquals(2, repository.find(EntityTest.class,
                "myIntValue > ?",
                new String[]{"0"},
                "myIntValue desc",
                "2",
                "myIntValue").size());
    }

    public void testFind_DbEntityTest() {
        insert_4_DbEntityTest();

        assertEquals(1, repository.find(DbEntityTest.class, "intValue = ?", new String[]{"201"}).size());
        assertDbEntityTest(300, repository.find(DbEntityTest.class, "intValue > ?", new String[]{"300"}).get(0));

        assertEquals(2, repository.find(DbEntityTest.class,
                "intValue > ?",
                new String[]{"0"},
                "intValue desc",
                "2",
                "intValue").size());
    }

    public void testFind_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        assertEquals(1, repository.find(DbEntityServerTest.class, "intValue = ?", new String[]{"301"}).size());
        assertDbEntityServerTest(400, repository.find(DbEntityServerTest.class, "intValue > ?", new String[]{"400"}).get(0));

        assertEquals(2, repository.find(DbEntityServerTest.class,
                "intValue > ?",
                new String[]{"0"},
                "intValue desc",
                "2",
                "intValue").size());
    }

    public void testFirst_Entity() {
        insert_4_entityTest();

        assertEntityTest(100, repository.first(EntityTest.class));
        assertEntityTest(200, repository.first(EntityTest.class, "myIntValue > ?", new String[]{"200"}));
    }

    public void testFirst_DbEntityTest() {
        insert_4_DbEntityTest();

        assertDbEntityTest(100, repository.first(DbEntityTest.class));
        assertDbEntityTest(200, repository.first(DbEntityTest.class, "intValue > ?", new String[]{"200"}));
    }

    public void testFirst_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        assertDbEntityServerTest(100, repository.first(DbEntityServerTest.class));
        assertDbEntityServerTest(200, repository.first(DbEntityServerTest.class, "floatValue > ?", new String[]{"200"}));
    }

    public void testInsert_Entity() {
        insert_4_entityTest();

        assertEntityTest(100, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"101"}).get(0));
        assertEntityTest(200, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"201"}).get(0));
        assertEntityTest(300, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"301"}).get(0));
        assertEntityTest(400, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"401"}).get(0));
    }

    public void testInsert_DbEntityTest() {
        insert_4_DbEntityTest();

        assertDbEntityTest(100, repository.find(DbEntityTest.class, "intValue = ?", new String[]{"101"}).get(0));
        assertDbEntityTest(200, repository.find(DbEntityTest.class, "intValue = ?", new String[]{"201"}).get(0));
        assertDbEntityTest(300, repository.find(DbEntityTest.class, "intValue = ?", new String[]{"301"}).get(0));
        assertDbEntityTest(400, repository.find(DbEntityTest.class, "intValue = ?", new String[]{"401"}).get(0));
    }

    public void testInsert_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        assertDbEntityServerTest(100, repository.find(DbEntityServerTest.class, "intValue = ?", new String[]{"101"}).get(0));
        assertDbEntityServerTest(200, repository.find(DbEntityServerTest.class, "intValue = ?", new String[]{"201"}).get(0));
        assertDbEntityServerTest(300, repository.find(DbEntityServerTest.class, "intValue = ?", new String[]{"301"}).get(0));
        assertDbEntityServerTest(400, repository.find(DbEntityServerTest.class, "intValue = ?", new String[]{"401"}).get(0));
    }

    public void testInsertList_DbEntityTest() {

        List<DbEntityTest> list = new ArrayList<>(4);
        list.add(createDbEntityTestFromBaseValue(100));
        list.add(createDbEntityTestFromBaseValue(200));
        list.add(createDbEntityTestFromBaseValue(300));
        list.add(createDbEntityTestFromBaseValue(400));

        repository.insert(list);

        assertEquals(4, repository.findAll(DbEntityTest.class).size());
        assertDbEntityTest(100, repository.findAll(DbEntityTest.class).get(0));
        assertDbEntityTest(200, repository.findAll(DbEntityTest.class).get(1));
        assertDbEntityTest(300, repository.findAll(DbEntityTest.class).get(2));
        assertDbEntityTest(400, repository.findAll(DbEntityTest.class).get(3));
    }

    public void testInsertList_DbEntityServerTest() {

        List<DbEntityServerTest> list = new ArrayList<>(4);
        list.add(createDbEntityServerTestFromBaseValue(100));
        list.add(createDbEntityServerTestFromBaseValue(200));
        list.add(createDbEntityServerTestFromBaseValue(300));
        list.add(createDbEntityServerTestFromBaseValue(400));

        repository.insert(list);

        assertEquals(4, repository.findAll(DbEntityServerTest.class).size());
        assertDbEntityServerTest(100, repository.findAll(DbEntityServerTest.class).get(0));
        assertDbEntityServerTest(200, repository.findAll(DbEntityServerTest.class).get(1));
        assertDbEntityServerTest(300, repository.findAll(DbEntityServerTest.class).get(2));
        assertDbEntityServerTest(400, repository.findAll(DbEntityServerTest.class).get(3));
    }

    public void testInsertListInTx_DbEntityTest() {

        List<DbEntityTest> list = new ArrayList<>(4);
        list.add(createDbEntityTestFromBaseValue(100));
        list.add(createDbEntityTestFromBaseValue(200));
        list.add(createDbEntityTestFromBaseValue(300));
        list.add(createDbEntityTestFromBaseValue(400));

        list.add(null);
        try {
            repository.insertInTx(list);
        } catch (Exception ignored) {
        }

        assertEquals(0, repository.findAll(DbEntityTest.class).size());
    }

    public void testInsertListInTx_DbEntityServerTest() {

        List<DbEntityServerTest> list = new ArrayList<>(4);
        list.add(createDbEntityServerTestFromBaseValue(100));
        list.add(createDbEntityServerTestFromBaseValue(200));
        list.add(createDbEntityServerTestFromBaseValue(300));
        list.add(createDbEntityServerTestFromBaseValue(400));

        list.add(null);
        try {
            repository.insertInTx(list);
        } catch (Exception ignored) {
        }

        assertEquals(0, repository.findAll(DbEntityServerTest.class).size());
    }

    public void testUpdate_Entity() {
        insert_2_entityTest();
        final EntityTest e = createEntityFromBaseValue(300);
        repository.insert(e);

        repository.update(e, "myIntValue = ?", new String[]{"301"});

        assertEntityTest(300, repository.find(EntityTest.class, "myIntValue = ?", new String[]{"301"}).get(0));
    }

    public void testUpdateById_DbEntityTest() {
        insert_2_DbEntityTest();
        final DbEntityTest e = createDbEntityTestFromBaseValue(300);
        repository.insert(e);

        e.intValue = 1000;

        repository.updateById(e);

        assertEquals(1000, repository.findById(DbEntityTest.class, 3).intValue);
    }

    public void testUpdateById_DbEntityServerTest() {
        insert_2_DbEntityServerTest();
        final DbEntityServerTest e = createDbEntityServerTestFromBaseValue(300);
        repository.insert(e);

        e.intValue = 2000;

        repository.updateById(e);

        assertEquals(2000, repository.findById(DbEntityServerTest.class, 3).intValue);
    }

    public void testUpdateByIdServer_DbEntityServerTest() {
        insert_2_DbEntityServerTest();
        final DbEntityServerTest e = createDbEntityServerTestFromBaseValue(300);
        repository.insert(e);

        e.intValue = 3000;

        repository.updateByIdServer(e);

        assertEquals(3000, repository.findById(DbEntityServerTest.class, 3).intValue);
    }

    public void testUpdateListById_DbEntityTest() {
        insert_4_DbEntityTest();

        DbEntityTest e2 = new DbEntityTest();
        e2.setId(2);
        e2.intValue = 2002;
        DbEntityTest e3 = new DbEntityTest();
        e3.setId(3);
        e3.intValue = 3002;

        repository.updateById(Arrays.asList(e2, e3));

        assertEquals(2002, repository.findById(DbEntityTest.class, 2).intValue);
        assertEquals(null, repository.findById(DbEntityTest.class, 2).integerNullableValue);
        assertEquals(3002, repository.findById(DbEntityTest.class, 3).intValue);
        assertEquals(null, repository.findById(DbEntityTest.class, 3).integerNullableValue);
    }

    public void testUpdateListById_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        DbEntityServerTest e2 = new DbEntityServerTest();
        e2.setId(2);
        e2.intValue = 2002;
        DbEntityServerTest e3 = new DbEntityServerTest();
        e3.setId(3);
        e3.intValue = 3002;

        repository.updateById(Arrays.asList(e2, e3));

        assertEquals(2002, repository.findById(DbEntityServerTest.class, 2).intValue);
        assertEquals(null, repository.findById(DbEntityServerTest.class, 2).integerNullableValue);
        assertEquals(3002, repository.findById(DbEntityServerTest.class, 3).intValue);
        assertEquals(null, repository.findById(DbEntityServerTest.class, 3).integerNullableValue);
    }

    public void testUpdateListByIdServer_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        DbEntityServerTest e2 = new DbEntityServerTest();
        e2.setIdServer("213");
        e2.intValue = 2002;
        DbEntityServerTest e3 = new DbEntityServerTest();
        e3.setIdServer("313");
        e3.intValue = 3002;

        repository.updateByIdServer(Arrays.asList(e2, e3));

        assertEquals(2002, repository.findById(DbEntityServerTest.class, 2).intValue);
        assertEquals(null, repository.findById(DbEntityServerTest.class, 2).integerNullableValue);
        assertEquals(3002, repository.findById(DbEntityServerTest.class, 3).intValue);
        assertEquals(null, repository.findById(DbEntityServerTest.class, 3).integerNullableValue);
    }

    public void testUpdateListByIdInTx_DbEntityTest() {
        insert_4_DbEntityTest();

        DbEntityTest e2 = new DbEntityTest();
        e2.setId(2);
        e2.intValue = 2002;

        try {
            repository.updateByIdInTx(Arrays.asList(e2, null));
        } catch (Exception ignored) {
        }

        assertEquals(4, repository.findAll(DbEntityTest.class).size());
        assertDbEntityTest(100, repository.findAll(DbEntityTest.class).get(0));
        assertDbEntityTest(200, repository.findAll(DbEntityTest.class).get(1));
        assertDbEntityTest(300, repository.findAll(DbEntityTest.class).get(2));
        assertDbEntityTest(400, repository.findAll(DbEntityTest.class).get(3));
    }

    public void testUpdateListByIdInTx_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        DbEntityServerTest e2 = new DbEntityServerTest();
        e2.setId(2);
        e2.intValue = 2002;

        try {
            repository.updateByIdInTx(Arrays.asList(e2, null));
        } catch (Exception ignored) {
        }

        assertEquals(4, repository.findAll(DbEntityServerTest.class).size());
        assertDbEntityServerTest(100, repository.findAll(DbEntityServerTest.class).get(0));
        assertDbEntityServerTest(200, repository.findAll(DbEntityServerTest.class).get(1));
        assertDbEntityServerTest(300, repository.findAll(DbEntityServerTest.class).get(2));
        assertDbEntityServerTest(400, repository.findAll(DbEntityServerTest.class).get(3));
    }

    public void testUpdateListByIdServerInTx_DbEntityServerTest() {
        insert_4_DbEntityServerTest();

        DbEntityServerTest e2 = new DbEntityServerTest();
        e2.setIdServer("213");
        e2.intValue = 2002;

        try {
            repository.updateByIdServerInTx(Arrays.asList(e2, null));
        } catch (Exception ignored) {
        }

        assertEquals(4, repository.findAll(DbEntityServerTest.class).size());
        assertDbEntityServerTest(100, repository.findAll(DbEntityServerTest.class).get(0));
        assertDbEntityServerTest(200, repository.findAll(DbEntityServerTest.class).get(1));
        assertDbEntityServerTest(300, repository.findAll(DbEntityServerTest.class).get(2));
        assertDbEntityServerTest(400, repository.findAll(DbEntityServerTest.class).get(3));
    }

    //

    @SuppressWarnings("deprecation")
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
        assertEquals(String.valueOf(++base), en.string);
        assertEquals(null, en.stringNoInsert);
        assertEquals(null, en.stringNoSelect);
        assertEquals(null, en.stringTransient);
    }

    @SuppressWarnings("deprecation")
    private void assertDbEntityTest(int base, DbEntityTest en) {
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
        assertEquals(new Date(2015, 6, 20), en.date);
        assertEquals(null, en.dateNullable);
        assertEquals(String.valueOf(++base), en.string);
        assertEquals(null, en.stringNoInsert);
        assertEquals(null, en.stringNoSelect);
        assertEquals(null, en.stringTransient);

        assertEquals(new Date(2015, 6, 25), en.getDateCreate());
        assertEquals(new Date(2015, 6, 30), en.getDateModification());
    }

    @SuppressWarnings("deprecation")
    private void assertDbEntityServerTest(int base, DbEntityServerTest en) {
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
        assertEquals(new Date(2015, 7, 15), en.date);
        assertEquals(null, en.dateNullable);
        assertEquals(String.valueOf(++base), en.string);
        assertEquals(null, en.stringNoInsert);
        ++base;
        assertEquals(null, en.stringNoSelect);
        ++base;
        assertEquals(null, en.stringTransient);
        ++base;

        assertEquals(new Date(2015, 7, 20), en.getDateCreate());
        assertEquals(new Date(2015, 7, 25), en.getDateModification());
        assertEquals(String.valueOf(++base), en.getIdServer());
        assertEquals(new Date(2015, 7, 30), en.getDateUtcSent());
        assertEquals(false, (boolean) en.getIsSent());
    }

    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
    private DbEntityTest createDbEntityTestFromBaseValue(int base) {
        final DbEntityTest e = new DbEntityTest();
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
        e.date = new Date(2015, 6, 20);
        e.dateNullable = null;
        e.string = String.valueOf(++base);
        e.stringNoInsert = String.valueOf(++base);
        e.stringNoSelect = String.valueOf(++base);
        e.stringTransient = String.valueOf(++base);

        e.setDateCreate(new Date(2015, 6, 25));
        e.setDateModification(new Date(2015, 6, 30));
        return e;
    }

    @SuppressWarnings("deprecation")
    private DbEntityServerTest createDbEntityServerTestFromBaseValue(int base) {
        final DbEntityServerTest e = new DbEntityServerTest();
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
        e.date = new Date(2015, 7, 15);
        e.dateNullable = null;
        e.string = String.valueOf(++base);
        e.stringNoInsert = String.valueOf(++base);
        e.stringNoSelect = String.valueOf(++base);
        e.stringTransient = String.valueOf(++base);

        e.setDateCreate(new Date(2015, 7, 20));
        e.setDateModification(new Date(2015, 7, 25));
        e.setIdServer(String.valueOf(++base));
        e.setDateUtcSent(new Date(2015, 7, 30));
        e.setIsSent(false);
        return e;
    }

    private void insert_1_entityTest() {
        assertEquals(1, repository.insert(createEntityFromBaseValue(100)));
    }

    private void insert_2_entityTest() {
        insert_1_entityTest();
        assertEquals(2, repository.insert(createEntityFromBaseValue(200)));
    }

    private void insert_4_entityTest() {
        insert_2_entityTest();
        assertEquals(3, repository.insert(createEntityFromBaseValue(300)));
        assertEquals(4, repository.insert(createEntityFromBaseValue(400)));
    }

    private void insert_1_DbEntityTest() {
        final DbEntityTest e = createDbEntityTestFromBaseValue(100);
        repository.insert(e);
        assertEquals(1, e.getId());
    }

    private void insert_2_DbEntityTest() {
        insert_1_DbEntityTest();
        final DbEntityTest e = createDbEntityTestFromBaseValue(200);
        repository.insert(e);
        assertEquals(2, e.getId());
    }

    private void insert_4_DbEntityTest() {
        insert_2_DbEntityTest();
        final DbEntityTest e3 = createDbEntityTestFromBaseValue(300);
        repository.insert(e3);
        assertEquals(3, e3.getId());
        final DbEntityTest e4 = createDbEntityTestFromBaseValue(400);
        repository.insert(e4);
        assertEquals(4, e4.getId());
    }

    private void insert_1_DbEntityServerTest() {
        final DbEntityServerTest e = createDbEntityServerTestFromBaseValue(100);
        repository.insert(e);
        assertEquals(1, e.getId());
    }

    private void insert_2_DbEntityServerTest() {
        insert_1_DbEntityServerTest();
        final DbEntityServerTest e = createDbEntityServerTestFromBaseValue(200);
        repository.insert(e);
        assertEquals(2, e.getId());
    }

    private void insert_4_DbEntityServerTest() {
        insert_2_DbEntityServerTest();
        final DbEntityServerTest e3 = createDbEntityServerTestFromBaseValue(300);
        repository.insert(e3);
        assertEquals(3, e3.getId());
        final DbEntityServerTest e4 = createDbEntityServerTestFromBaseValue(400);
        repository.insert(e4);
        assertEquals(4, e4.getId());
    }
}