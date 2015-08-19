package com.androdblite.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androdblite.domain.DbEntity;
import com.androdblite.domain.DbEntityServer;

import java.util.List;

/**
 * @author tgirard12
 */
public interface DbRepository {

    // SqliteDatabase

    SQLiteDatabase getReadableDatabase();

    SQLiteDatabase getWritableDatabase();

    // Count

    long count(Class clazz);

    long count(Class clazz, String selection, String[] selectionArgs);

    // Delete

    int delete(Class clazz, SQLiteDatabase database, String selection, String[] selectionArgs);

    int delete(Class clazz, String selection, String[] selectionArgs);

    void deleteById(DbEntity entity);

    void deleteByIdServer(DbEntityServer entity);

    void deleteById(List<? extends DbEntity> entities);

    void deleteByIdServer(List<? extends DbEntityServer> entities);

    void deleteByIdInTx(List<? extends DbEntity> entities);

    void deleteByIdServerInTx(List<? extends DbEntityServer> entities);

    // Exist

    <T> boolean exist(Class<T> clazz, SQLiteDatabase database, String selection, String[] selectionArgs);

    <T> boolean exist(Class<T> clazz, String selection, String[] selectionArgs);

    boolean existById(DbEntity dbEntity);

    boolean existByIdServer(DbEntityServer dbEntityServer);

    // Find

    <T> List<T> findAll(Class<T> clazz);

    <T> T findById(Class<T> clazz, long id);

    <T> T findByIdServer(Class<T> clazz, String idServer);

    <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs);

    <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy);

    <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, String limit);

    <T> List<T> find(Class<T> clazz, String selection, String[] selectionArgs, String orderBy, String limit, String groupBy);

    Cursor findCursor(Class clazz, String selection, String[] selectionArgs);

    Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy);

    Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy, String limit);

    Cursor findCursor(Class clazz, String selection, String[] selectionArgs, String orderBy, String limit, String groupBy);

    //

    <T> T first(Class<T> clazz);

    <T> T first(Class<T> clazz, String selection, String[] selectionArgs);

    //

    long insert(Object entity, SQLiteDatabase writableDb);

    long insert(Object entity);

    void insert(DbEntity entity);

    void insert(List<? extends DbEntity> entities);

    void insertInTx(List<? extends DbEntity> entities);

    // Save

    void saveById(DbEntity entity);

    void saveByIdServer(DbEntityServer entity);

    void saveById(List<? extends DbEntity> entities);

    void saveByIdServer(List<? extends DbEntityServer> entities);

    void saveByIdInTx(List<? extends DbEntity> entities);

    void saveByIdServerInTx(List<? extends DbEntityServer> entities);

    // Update

    long update(Object entity, SQLiteDatabase database, String selection, String[] selectionArgs);

    long update(Object entity, String selection, String[] selectionArgs);

    void updateById(DbEntity entity);

    void updateByIdServer(DbEntityServer entity);

    void updateById(List<? extends DbEntity> entities);

    void updateByIdServer(List<? extends DbEntityServer> entities);

    void updateByIdInTx(List<? extends DbEntity> entities);

    void updateByIdServerInTx(List<? extends DbEntityServer> entities);


}
