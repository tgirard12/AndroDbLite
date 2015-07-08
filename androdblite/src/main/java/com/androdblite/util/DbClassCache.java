package com.androdblite.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tgirard on 26/06/15
 */
public class DbClassCache {

    static Map<Class<?>, List<Field>> classes = new HashMap<>();
    static Map<Class<?>, String> tables = new HashMap<>();

    public static List<Field> getFields(Class<?> clazz) {
        if (!classes.containsKey(clazz)) {
            List<Field> list = DbReflexionUtil.getFields(clazz);
            classes.put(clazz, list);
            return list;
        }
        return classes.get(clazz);
    }




    public static void clearCache() {
        classes.clear();
        classes = new HashMap<>();
        tables.clear();
        tables = new HashMap<>();
    }

    public static String getTableName(Class clazz) {
        if (!tables.containsKey(clazz)) {
            final String tableName = DbReflexionUtil.getTableName(clazz);
            tables.put(clazz, tableName);
            return tableName;
        }
        return tables.get(clazz);
    }

    public static <T> String[] getColumnsSelect(Class<T> clazz) {
        return new String[0];
    }
}
