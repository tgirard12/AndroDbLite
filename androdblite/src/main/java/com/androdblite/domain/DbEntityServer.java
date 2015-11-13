package com.androdblite.domain;

import com.androdblite.DbColumn;

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

    @DbColumn(DB.idServer)
    String idServer;

    @DbColumn(DB.isSent)
    Boolean isSent;

    @DbColumn(DB.dateUtcSent)
    Date dateUtcSent;

    public String getIdServer() {
        return idServer;
    }

    public void setIdServer(String idServer) {
        this.idServer = idServer;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    public Date getDateUtcSent() {
        return dateUtcSent;
    }

    public void setDateUtcSent(Date dateUtcSent) {
        this.dateUtcSent = dateUtcSent;
    }
}
