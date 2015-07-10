package com.androdblite.util;

import android.content.ContentValues;

import com.androdblite.annotation.DbColumn;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by tgirard on 26/06/15
 */
public class DbContentValueUtil {


    public static ContentValues getContentValues(List<Field> fieldList, Object entity) {
        final ContentValues cv = new ContentValues();
        String name;
        for (Field f : fieldList) {
            final DbColumn column = f.getAnnotation(DbColumn.class);
            if (column != null && column.insertUpdate()) {

                // Check use other Name
                name = DbReflexionUtil.getColumnName(f, column);
                f.setAccessible(true);

                // String
                if (f.getType().equals(String.class)) {
                    final String val = DbReflexionUtil.getStringValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // Integer
                if (f.getType().equals(Integer.class) || f.getType().equals(int.class)) {
                    final Integer val = DbReflexionUtil.getIntValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // Long
                if (f.getType().equals(Long.class) || f.getType().equals(long.class)) {
                    final Long val = DbReflexionUtil.getLongValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // Float
                if (f.getType().equals(Float.class) || f.getType().equals(float.class)) {
                    final Float val = DbReflexionUtil.getFloatValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // Double
                if (f.getType().equals(Double.class) || f.getType().equals(double.class)) {
                    final Double val = DbReflexionUtil.getDoubleValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // Boolean
                if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
                    final Boolean val = DbReflexionUtil.getBooleanValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val);
                    continue;
                }
                // java.util.Date
                if (f.getType().equals(Date.class)) {
                    final Date val = DbReflexionUtil.getDateValue(f, entity);
                    if (val == null)
                        cv.putNull(name);
                    else
                        cv.put(name, val.getTime());
                }
            }
        }
        return cv;
    }
}
