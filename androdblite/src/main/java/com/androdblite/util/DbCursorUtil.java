package com.androdblite.util;

import android.database.Cursor;

import com.androdblite.AndroDbLiteError;
import com.androdblite.annotation.DbColumn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tgirard on 01/07/15
 */
public class DbCursorUtil {

    /**
     * From first position to last
     *
     * @param clazz     c
     * @param fieldList f
     * @param cursor    c
     * @param <T>       t
     * @return r
     */
    public <T> List<T> getEntitiesFromCursor(Class<T> clazz, List<Field> fieldList, Cursor cursor) {

        List<T> listResult = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                final T entity = getEntityFromCursor(clazz, fieldList, cursor);
                listResult.add(entity);
            } while (cursor.moveToNext());
        }
        return listResult;
    }

    public <T> T getEntityFromCursor(Class<T> clazz, List<Field> fieldList, Cursor cursor) {

        T entity = null;
        try {
            entity = null;
            entity = clazz.newInstance();
            int col;
            String column;

            for (Field f : fieldList) {

                final DbColumn anot = f.getAnnotation(DbColumn.class);

                // Check excluse select
                if (anot != null && anot.select()) {

                    // Check use other Name
                    column = DbReflexionUtil.getColumnName(f, anot);
                    if ((col = cursor.getColumnIndex(column)) != -1 && !cursor.isNull(col)) {

                        f.setAccessible(true);
                        if (f.getType().equals(String.class)) {
                            f.set(entity, cursor.getString(col));
                            continue;
                        }
                        if (f.getType().equals(Long.class) || f.getType().equals(long.class)) {
                            f.set(entity, cursor.getLong(col));
                            continue;
                        }
                        if (f.getType().equals(Integer.class) || f.getType().equals(int.class)) {
                            f.set(entity, cursor.getInt(col));
                            continue;
                        }
                        if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
                            f.set(entity, cursor.getInt(col) > 0);
                            continue;
                        }
                        if (f.getType().equals(Float.class) || f.getType().equals(float.class)) {
                            f.set(entity, cursor.getFloat(col));
                            continue;
                        }
                        if (f.getType().equals(Double.class) || f.getType().equals(double.class)) {
                            f.set(entity, cursor.getDouble(col));
                            continue;
                        }
                        if (f.getType().equals(Date.class)) {
                            f.set(entity, new Date(cursor.getLong(col)));
                        }
                    }
                }
            }

        } catch (InstantiationException e) {
            throw new AndroDbLiteError("getEntityFromCursor() exception:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new AndroDbLiteError("getEntityFromCursor() exception:" + e.getMessage(), e);
        }
        return entity;
    }
}
