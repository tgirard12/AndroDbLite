package com.androdblite.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androdblite.domain.DbEntity;
import com.androdblite.domain.DbEntityServer;

import java.util.List;

/**
 * Created by tgirard on 26/06/15
 */
public interface DbRepository {

    // SqliteDatabase

    SQLiteDatabase getReadableDatabase();

    SQLiteDatabase getWritableDatabase();

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

    // Count

    long count(Class clazz);

    long count(Class clazz, String selection, String[] selectionArgs);

    //

    <T> T first(Class<T> clazz);

    <T> T first(Class<T> clazz, String selection, String[] selectionArgs);

    //

    long insert(Object entity, SQLiteDatabase writableDb);

    long insert(Object entity);

    List<Long> insert(List<Object> entity);

    List<Long> insertInTx(List<Object> entity);

    //

    DbEntity saveById(DbEntity entity);

    DbEntity saveByIdServer(DbEntityServer entity);

    DbEntity save(Object entity, String selection, String[] selectionArgs);

    List<DbEntity> saveById(List<DbEntity> entity);

    List<DbEntity> saveByIdServer(List<DbEntityServer> entity);

    List<DbEntity> saveByIdInTx(List<DbEntity> entity);

    List<DbEntity> saveByIdServerInTx(List<DbEntityServer> entity);

    // Delete

    void deleteById(DbEntity entity);

    void deleteByIdServer(DbEntityServer entity);

    void delete(Object entity, String selection, String[] selectionArgs);

    void deleteById(List<DbEntity> entity);

    void deleteByIdServer(List<DbEntityServer> entity);

    void deleteByIdInTx(List<DbEntity> entity);

    void deleteByIdServerInTx(List<DbEntityServer> entity);

}
