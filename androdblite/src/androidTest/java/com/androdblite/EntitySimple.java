package com.androdblite;

import java.util.Date;

/**
 * Created by tgirard on 13/11/15
 */
@DbTable
public class EntitySimple {

    @DbId
    long _id;

    @DbIdServer
    String idServer;

    @DbIsDelete
    boolean isDelete;

    @DbColumn
    int intValue;
    @DbColumn
    Integer integerNullableValue;

    @DbColumn
    long longValue;
    @DbColumn
    Long longNullableValue;

    @DbColumn
    float floatValue;
    @DbColumn
    Float floatNullableValue;

    @DbColumn
    double doubleValue;
    @DbColumn
    Double doubleNullableValue;

    @DbColumn
    boolean booleanValue;
    @DbColumn
    Boolean booleanNullableValue;

    @DbColumn
    Date date;

    @DbColumn
    String string;

    @DbColumn(select = false)
    public String stringNoSelect;

    @DbColumn(insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}