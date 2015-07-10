package com.androdblite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androdblite.AndroDbLiteException;
import com.androdblite.DbHelper;
import com.androdblite.domain.DbEntity;
import com.androdblite.domain.DbEntityServer;
import com.androdblite.util.DbClassCache;
import com.androdblite.util.DbContentValueUtil;
import com.androdblite.util.DbCursorUtil;
import com.androdblite.util.DbManifestUtil;
import com.androdblite.util.DbReflexionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tgirard on 26/06/15
 */
public class DbRepositoryImpl implements DbRepository {

    public static SQLiteOpenHelper sqLiteOpenHelper;

    /**
     * Init a defaut SqliOpenHelper => DbHelper
     *
     * @param context c
     * @throws AndroDbLiteException
     */
    public DbRepositoryImpl(Context context) throws AndroDbLiteException {
        sqLiteOpenHelper = new DbHelper(
                context,
                DbManifestUtil.getDatabaseName(context),
                null,
                DbManifestUtil.getDatabaseVersion(context));
    }

    public DbRepositoryImpl(SQLiteOpenHelper sqLiteOpenHelper) {
        DbRepositoryImpl.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return sqLiteOpenHelper.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return sqLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        return find(clazz, null, null, null, null, null);
    }

    @Override
    public <T> T findById(Class<T> clazz, long id) {

        final List<T> list = find(clazz, DbEntityServer.DB.id + "=?", new String[]{String.valueOf(id)}, null, "1", null);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public <T> T findByIdServer(Class<T> clazz, String idServer) {
        final List<T> list = find(clazz, DbEntityServer.DB.idServer + "=?", new String[]{idServer}, null, "1", null);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    @Override
    public <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs) {
        return find(clazz, selection, selectionArgs, null, null, null);
    }

    @Override
    public <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy) {
        return find(clazz, selection, selectionArgs, orderBy, null, null);
    }

    @Override
    public <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, String limit) {
        return find(clazz, selection, selectionArgs, orderBy, limit, null);
    }

    @Override
    public <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, String limit, String groupBy) {
        final String table = DbClassCache.getTableName(clazz);
        final String[] columns = DbClassCache.getColumnsSelect(clazz);
        final List<Field> fieldList = DbClassCache.getFields(clazz);

        Cursor c = null;
        List<T> listResult = new ArrayList<>();
        try {
            c = getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, null, orderBy, limit);
            return DbCursorUtil.getEntitiesFromCursor(clazz, fieldList, c);

        } finally {
            if (c != null)
                c.close();
        }
    }

    @Override
    public Cursor findCursor(Class clazz, String selection, String[] selectionArgs) {
        return findCursor(clazz, selection, selectionArgs, null, null, null);
    }

    @Override
    public Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy) {
        return findCursor(clazz, selection, selectionArgs, orderBy, null, null);
    }

    @Override
    public Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy, String limit) {
        return findCursor(clazz, selection, selectionArgs, orderBy, limit, null);
    }

    @Override
    public Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy, String limit, String groupBy) {
        final String table = DbClassCache.getTableName(clazz);
        final String[] columns = DbClassCache.getColumnsSelect(clazz);

        return getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, null, orderBy, limit);
    }

    @Override
    public long count(Class clazz) {
        final String table = DbReflexionUtil.getTableName(clazz);
        return DatabaseUtils.longForQuery(getReadableDatabase(), "select count(*) from " + table, null);
    }

    @Override
    public long count(Class clazz, String selection, String[] selectionArgs) {
        final String table = DbReflexionUtil.getTableName(clazz);
        return DatabaseUtils.longForQuery(
                getReadableDatabase(),
                "select count(*) from " + table + " where " + selection,
                selectionArgs);
    }

    @Override
    public <T> T first(Class<T> clazz) {
        return first(clazz, null, null);
    }

    @Override
    public <T> T first(Class<T> clazz, String selection, String[] selectionArgs) {
        final List<T> all = find(clazz, selection, selectionArgs, null, "1", null);
        if (all.size() == 1)
            return all.get(1);
        return null;
    }

    @Override
    public long insert(Object entity, SQLiteDatabase writableDb) {

        final String table = DbClassCache.getTableName(entity.getClass());
        final List<Field> fieldList = DbClassCache.getFields(entity.getClass());
        final ContentValues contentValues = DbContentValueUtil.getContentValues(fieldList, entity);

        return writableDb.insertOrThrow(table, null, contentValues);
    }

    @Override
    public long insert(Object entity) {
        return insert(entity, getWritableDatabase());
    }

    @Override
    public List<Long> insert(List<Object> entities) {

        final ArrayList<Long> longs = new ArrayList<Long>(entities.size());
        for (Object entity : entities) {
            longs.add(insert(entity));
        }
        return longs;
    }

    @Override
    public List<Long> insertInTx(List<Object> entities) {

        final SQLiteDatabase writableDatabase = getWritableDatabase();
        final ArrayList<Long> longs = new ArrayList<>(entities.size());
        try {
            writableDatabase.beginTransaction();
            for (Object entity : entities) {
                longs.add(insert(entity, writableDatabase));
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
        return longs;
    }

    @Override
    public DbEntity saveById(DbEntity entity) {
        return null;
    }

    @Override
    public DbEntity saveByIdServer(DbEntityServer entity) {
        return null;
    }

    @Override
    public DbEntity save(Object entity, String selection, String[] selectionArgs) {
        return null;
    }

    @Override
    public List<DbEntity> saveById(List<DbEntity> entity) {
        return null;
    }

    @Override
    public List<DbEntity> saveByIdServer(List<DbEntityServer> entity) {
        return null;
    }

    @Override
    public List<DbEntity> saveByIdInTx(List<DbEntity> entity) {
        return null;
    }

    @Override
    public List<DbEntity> saveByIdServerInTx(List<DbEntityServer> entity) {
        return null;
    }

    @Override
    public void deleteById(DbEntity entity) {

    }

    @Override
    public void deleteByIdServer(DbEntityServer entity) {

    }

    @Override
    public void delete(Object entity, String selection, String[] selectionArgs) {

    }

    @Override
    public void deleteById(List<DbEntity> entity) {

    }

    @Override
    public void deleteByIdServer(List<DbEntityServer> entity) {

    }

    @Override
    public void deleteByIdInTx(List<DbEntity> entity) {

    }

    @Override
    public void deleteByIdServerInTx(List<DbEntityServer> entity) {

    }
}
