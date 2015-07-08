package com.androdblite.util;

import android.text.TextUtils;

import com.androdblite.AndroDbLite;
import com.androdblite.AndroDbLiteError;
import com.androdblite.annotation.DbColumn;
import com.androdblite.annotation.DbEntity;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tgirard on 26/06/15
 */
public class DbReflexionUtil {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(AndroDbLite.TAG);

    public static List<Field> getFields(Class<?> clazz) {
        final ArrayList<Field> fields = new ArrayList<>();
        addDeclaredAndInheritedFields(clazz, fields);

        Iterator<Field> itF = fields.iterator();
        while (itF.hasNext()) {
            Field f = itF.next(); // must be called before you can call i.remove()
            if (f.getAnnotation(DbColumn.class) == null)
                itF.remove();
        }
        return fields;
    }

    public static void addDeclaredAndInheritedFields(Class<?> c, Collection<Field> fields) {
        fields.addAll(Arrays.asList(c.getDeclaredFields()));
        Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            addDeclaredAndInheritedFields(superClass, fields);
        }
    }

    public static String getTableName(Class clazz) {
        final DbEntity dbEntity = (DbEntity) clazz.getAnnotation(DbEntity.class);
        if (dbEntity == null)
            throw new AndroDbLiteError("Class {} must be annotate with @DbEntity");

        if (TextUtils.isEmpty(dbEntity.name()))
            return clazz.getName();
        else
            return dbEntity.name();
    }

    public static String getColumnName(Field f, DbColumn column) {
        if (TextUtils.isEmpty(column.name()))
            return f.getName();
        else
            return column.name();
    }

    public static String getStringValue(Field f, Object entity) {
        try {
            return (String) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getStringValue() > error {}", ex);
        }
    }

    public static Integer getIntValue(Field f, Object entity) {
        try {
            return (Integer) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getIntValue() > error {}", ex);
        }
    }

    public static Long getLongValue(Field f, Object entity) {
        try {
            return (Long) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getLongValue() > error {}", ex);
        }
    }

    public static Float getFloatValue(Field f, Object entity) {
        try {
            return (Float) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getFloatValue() > error {}", ex);
        }
    }

    public static Double getDoubleValue(Field f, Object entity) {
        try {
            return (Double) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getDoubleValue() > error {}", ex);
        }
    }

    public static Boolean getBooleanValue(Field f, Object entity) {
        try {
            return (Boolean) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getBooleanValue() > error {}", ex);
        }
    }

    public static Date getDateValue(Field f, Object entity) {
        try {
            return (Date) f.get(entity);
        } catch (IllegalAccessException ex) {
            throw new AndroDbLiteError("getDateValue() > error {}", ex);
        }
    }
}
