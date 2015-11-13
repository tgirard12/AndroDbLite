package com.androdblite;

import java.util.Date;

/**
 * Created by tgirard on 13/11/15
 */
@DbTable("myCustomTable")
public class EntityNewName {

    @DbId("myId")
    long _id;

    @DbIdServer("myIdServer")
    String idServer;

    @DbIsDelete("myIsDelete")
    boolean isDelete;

    @DbColumn("myIntValue")
    int intValue;
    @DbColumn("myIntegerNullableValue")
    Integer integerNullableValue;

    @DbColumn("myLongValue")
    long longValue;
    @DbColumn("myLongNullableValue")
    Long longNullableValue;

    @DbColumn("myFloatValue")
    float floatValue;
    @DbColumn("myFloatNullableValue")
    Float floatNullableValue;

    @DbColumn("myDoubleValue")
    double doubleValue;
    @DbColumn("myDoubleNullableValue")
    Double doubleNullableValue;

    @DbColumn("myBooleanValue")
    boolean booleanValue;
    @DbColumn("myBooleanNullableValue")
    Boolean booleanNullableValue;

    @DbColumn("myDate")
    Date date;

    @DbColumn("myString")
    String string;

    @DbColumn(value = "myStringNoSelect", select = false)
    public String stringNoSelect;

    @DbColumn(value = "myStringNoInsert", insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}