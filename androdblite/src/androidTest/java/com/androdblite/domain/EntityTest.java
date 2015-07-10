package com.androdblite.domain;

import com.androdblite.annotation.DbColumn;
import com.androdblite.annotation.DbTable;

import java.util.Date;

/**
 * Created by tgirard on 30/06/15
 */
@DbTable(name = "myCustomTable")
public class EntityTest {

    @DbColumn(name = "myIntValue")
    public int intValue;
    @DbColumn(name = "myIntegerNullableValue")
    public Integer integerNullableValue;

    @DbColumn(name = "myLongValue")
    public long longValue;
    @DbColumn(name = "myLongNullableValue")
    public Long longNullableValue;

    @DbColumn(name = "myFloatValue")
    public float floatValue;
    @DbColumn(name = "myFloatNullableValue")
    public Float floatNullableValue;

    @DbColumn(name = "myDoubleValue")
    public double doubleValue;
    @DbColumn(name = "myDoubleNullableValue")
    public Double doubleNullableValue;

    @DbColumn(name = "myBooleanValue")
    public boolean booleanValue;
    @DbColumn(name = "myBooleanNullableValue")
    public Boolean booleanNullableValue;

    @DbColumn(name = "myDate")
    public Date date;

    @DbColumn(name = "myDateNullable")
    public Date dateNullable;

    @DbColumn(name = "myString")
    public String string;

    @DbColumn(name = "myStringNoSelect", select = false)
    public String stringNoSelect;

    @DbColumn(name = "myStringNoInsert", insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}
