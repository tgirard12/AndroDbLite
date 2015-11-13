package com.androdblite.internal;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by tgirard on 10/11/15
 */
public interface DbTableBinder<E> {

    String getTableName();

    ContentValues getContentValues(E entity);

    E getFromCursor(Cursor cursor);

}
