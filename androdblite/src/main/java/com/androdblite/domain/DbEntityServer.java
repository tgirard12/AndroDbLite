package com.androdblite.domain;

import com.androdblite.annotation.DbColumn;

import java.util.Date;

/**
 * Created by tgirard on 26/06/15
 */
public abstract class DbEntityServer extends DbEntity {

    public static class DB extends DbEntity.DB {
        public static final String idServer = "idServer";
        public static final String isSent = "isSent";
        public static final String dateUtcSent = "dateUtcSent";
    }

    @DbColumn(name = DB.idServer)
    public String idServer;

    @DbColumn(name = DB.isSent)
    public Boolean isSent;

    @DbColumn(name = DB.dateUtcSent)
    public Date dateUtcSent;

}
