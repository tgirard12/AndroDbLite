package com.androdblite.domain;

import com.androdblite.DbColumn;

import java.util.Date;

/**
 * @author tgirard12
 */
public abstract class DbEntity {

    public static class DB {
        public static final String id = "_id";
        public static final String dateCreate = "dateCreate";
        public static final String dateModification = "dateModification";
    }

    @DbColumn(insertUpdate = false)
    long _id;

    @DbColumn(insertUpdate = false, select = false)
    boolean isDelete = false;

    @DbColumn(DB.dateCreate)
    Date dateCreate;

    @DbColumn(DB.dateModification)
    Date dateModification;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }
}
