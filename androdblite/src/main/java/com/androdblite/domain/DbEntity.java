package com.androdblite.domain;

import com.androdblite.annotation.DbColumn;

import java.util.Date;

/**
 * Created by tgirard on 26/06/15
 */
public abstract class DbEntity {

    public static class DB {
        public static final String id = "_id";
        public static final String dateCreate = "dateCreate";
        public static final String dateModification = "dateModification";
    }

    @DbColumn(insertUpdate = false)
    public long _id;

    @DbColumn(insertUpdate = false, select = false)
    public boolean isDelete = false;

    @DbColumn(name = DB.dateCreate)
    public Date dateCreate;

    @DbColumn(name = DB.dateModification)
    public Date dateModification;
}
