package com.androdblite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.androdblite.AndroDbLite;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by tgirard on 26/06/15
 */
public class DbRepositoryImpl implements DbRepository {

    public static SQLiteOpenHelper sqLiteOpenHelper;

    public static String idSelection = DbEntity.DB.id + "=? ";
    public static String idServerSelection = DbEntityServer.DB.idServer + "=? ";

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
    public SQLiteDatabase getDatabase() {
        return sqLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public long count(Class clazz) {
        final String table = DbReflexionUtil.getTableName(clazz);
        final String query = "select count(*) from " + table;
        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, query);
        }
        return DatabaseUtils.longForQuery(getDatabase(), query, null);
    }

    @Override
    public long count(Class clazz, String selection, String[] selectionArgs) {
        final String table = DbReflexionUtil.getTableName(clazz);

        final StringBuilder str = new StringBuilder();
        str.append("select count(*) from ").append(table);
        if (selection != null)
            str.append(" where ").append(selection);

        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, str.toString());
            Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
        }
        return DatabaseUtils.longForQuery(
                getDatabase(),
                str.toString(),
                selectionArgs);
    }

    @Override
    public int delete(Class clazz, SQLiteDatabase database, String selection, String[] selectionArgs) {
        final String table = DbReflexionUtil.getTableName(clazz);
        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, "delete from " + table + " where " + selection);
            Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
        }
        return database.delete(table, selection, selectionArgs);
    }

    @Override
    public int delete(Class clazz, String selection, String[] selectionArgs) {
        return delete(clazz, getDatabase(), selection, selectionArgs);
    }

    @Override
    public void deleteById(DbEntity entity) {
        delete(entity.getClass(), idSelection, new String[]{String.valueOf(entity.getId())});
    }

    @Override
    public void deleteByIdServer(DbEntityServer entity) {
        delete(entity.getClass(), idServerSelection, new String[]{entity.getIdServer()});
    }

    @Override
    public void deleteById(List<? extends DbEntity> entities) {
        for (DbEntity dbEntity : entities) {
            deleteById(dbEntity);
        }
    }

    @Override
    public void deleteByIdServer(List<? extends DbEntityServer> entities) {
        for (DbEntityServer dbEntityServer : entities) {
            deleteById(dbEntityServer);
        }
    }

    @Override
    public void deleteByIdInTx(List<? extends DbEntity> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntity dbEntity : entities) {
                delete(dbEntity.getClass(),
                        database,
                        idSelection,
                        new String[]{String.valueOf(dbEntity.getId())});
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void deleteByIdServerInTx(List<? extends DbEntityServer> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntityServer dbEntityServer : entities) {
                delete(dbEntityServer.getClass(),
                        database,
                        idServerSelection,
                        new String[]{String.valueOf(dbEntityServer.getIdServer())});
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public <T> boolean exist(Class<T> clazz, SQLiteDatabase database, String selection, String[] selectionArgs) {
        final String table = DbReflexionUtil.getTableName(clazz);

        final StringBuilder str = new StringBuilder();
        str.append("select count(*) from ").append(table);
        if (selection != null)
            str.append(" where ").append(selection);
        str.append(" limit 1");

        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, str.toString());
            Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
        }
        return DatabaseUtils.longForQuery(
                getDatabase(),
                str.toString(),
                selectionArgs) > 0;
    }

    @Override
    public <T> boolean exist(Class<T> clazz, String selection, String[] selectionArgs) {
        return exist(clazz, getDatabase(), selection, selectionArgs);
    }

    @Override
    public boolean existById(DbEntity dbEntity) {
        return exist(dbEntity.getClass(), getDatabase(),
                idSelection, new String[]{String.valueOf(dbEntity.getId())});
    }

    @Override
    public boolean existByIdServer(DbEntityServer dbEntityServer) {
        return exist(dbEntityServer.getClass(), getDatabase(),
                idServerSelection, new String[]{String.valueOf(dbEntityServer.getIdServer())});
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
        try {
            if (AndroDbLite.dActive()) {
                final StringBuilder str = new StringBuilder("select " + Arrays.toString(columns) + " from " + table);
                if (selection != null)
                    str.append(" where ").append(selection);
                if (groupBy != null)
                    str.append(" group by ").append(groupBy);
                if (orderBy != null)
                    str.append(" order by ").append(orderBy);
                if (limit != null)
                    str.append(" limit ").append(limit);

                Log.d(AndroDbLite.TAG, str.toString());
                Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
            }
            c = getDatabase().query(table, columns, selection, selectionArgs, groupBy, null, orderBy, limit);
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

        if (AndroDbLite.dActive()) {
            final StringBuilder str = new StringBuilder("select " + Arrays.toString(columns) + " from " + table);
            if (selection != null)
                str.append(" where ").append(selection);
            if (groupBy != null)
                str.append(" group by ").append(groupBy);
            if (orderBy != null)
                str.append(" order by ").append(orderBy);
            if (limit != null)
                str.append(" limit ").append(limit);

            Log.d(AndroDbLite.TAG, str.toString());
            Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
        }
        return getDatabase().query(table, columns, selection, selectionArgs, groupBy, null, orderBy, limit);
    }

    @Override
    public <T> T first(Class<T> clazz) {
        return first(clazz, null, null);
    }

    @Override
    public <T> T first(Class<T> clazz, String selection, String[] selectionArgs) {
        final List<T> all = find(clazz, selection, selectionArgs, null, "1", null);
        if (all.size() == 1)
            return all.get(0);
        return null;
    }

    @Override
    public long insert(Object entity, SQLiteDatabase database) {

        final String table = DbClassCache.getTableName(entity.getClass());
        final List<Field> fieldList = DbClassCache.getFields(entity.getClass());
        final ContentValues contentValues = DbContentValueUtil.getContentValues(fieldList, entity);

        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, "insert into " + table +
                    " values (" + contentValues + ")");
        }
        return database.insertOrThrow(table, null, contentValues);
    }

    @Override
    public long insert(Object entity) {
        return insert(entity, getDatabase());
    }

    @Override
    public void insert(DbEntity entity) {
        entity.setId(insert(entity, getDatabase()));
    }

    @Override
    public void insert(List<? extends DbEntity> entities) {

        for (DbEntity dbEntity : entities) {
            insert(dbEntity);
        }
    }

    @Override
    public void insertInTx(List<? extends DbEntity> entities) {

        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntity dbEntity : entities) {
                insert(dbEntity);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void saveById(DbEntity entity) {
        if (!existById(entity))
            insert(entity);
        else
            updateById(entity);
    }

    @Override
    public void saveByIdServer(DbEntityServer entity) {
        if (!existByIdServer(entity))
            insert(entity);
        else
            updateByIdServer(entity);
    }

    @Override
    public void saveById(List<? extends DbEntity> entities) {
        for (DbEntity dbEntity : entities) {
            saveById(dbEntity);
        }
    }

    @Override
    public void saveByIdServer(List<? extends DbEntityServer> entities) {
        for (DbEntityServer dbEntityServer : entities) {
            saveByIdServer(dbEntityServer);
        }
    }

    @Override
    public void saveByIdInTx(List<? extends DbEntity> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntity dbEntity : entities) {
                saveById(dbEntity);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void saveByIdServerInTx(List<? extends DbEntityServer> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntityServer dbEntityServer : entities) {
                saveByIdServer(dbEntityServer);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public long update(Object entity, SQLiteDatabase database, String selection, String[] selectionArgs) {

        final String table = DbClassCache.getTableName(entity.getClass());
        final List<Field> fieldList = DbClassCache.getFields(entity.getClass());
        final ContentValues contentValues = DbContentValueUtil.getContentValues(fieldList, entity);

        if (AndroDbLite.dActive()) {
            Log.d(AndroDbLite.TAG, "update " + table +
                    " set " + contentValues +
                    " where " + selection);
            Log.d(AndroDbLite.TAG, selection + Arrays.toString(selectionArgs));
        }
        return database.update(table, contentValues, selection, selectionArgs);
    }

    @Override
    public long update(Object entity, String selection, String[] selectionArgs) {
        return update(entity, getDatabase(), selection, selectionArgs);
    }

    @Override
    public void updateById(DbEntity entity) {
        update(entity, getDatabase(), idSelection, new String[]{String.valueOf(entity.getId())});
    }

    @Override
    public void updateByIdServer(DbEntityServer entity) {
        update(entity, getDatabase(), idServerSelection, new String[]{entity.getIdServer()});
    }

    @Override
    public void updateById(List<? extends DbEntity> entities) {
        for (DbEntity dbEntity : entities) {
            update(dbEntity, getDatabase(), idSelection, new String[]{String.valueOf(dbEntity.getId())});
        }
    }

    @Override
    public void updateByIdServer(List<? extends DbEntityServer> entities) {
        for (DbEntityServer dbEntity : entities) {
            update(dbEntity, getDatabase(), idServerSelection, new String[]{dbEntity.getIdServer()});
        }
    }

    @Override
    public void updateByIdInTx(List<? extends DbEntity> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntity dbEntity : entities) {
                update(dbEntity, getDatabase(), idSelection, new String[]{String.valueOf(dbEntity.getId())});
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void updateByIdServerInTx(List<? extends DbEntityServer> entities) {
        final SQLiteDatabase database = getDatabase();
        try {
            database.beginTransaction();
            for (DbEntityServer dbEntity : entities) {
                update(dbEntity, getDatabase(), idServerSelection, new String[]{String.valueOf(dbEntity.getIdServer())});
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
