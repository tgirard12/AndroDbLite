package com.androdblite.domain;

import com.androdblite.DbColumn;
import com.androdblite.DbTable;

import java.util.Date;

/**
 * Created by tgirard on 30/06/15
 */
@DbTable("myCustomTable")
public class EntityTest {

    @DbColumn("myIntValue")
    public int intValue;
    @DbColumn("myIntegerNullableValue")
    public Integer integerNullableValue;

    @DbColumn("myLongValue")
    public long longValue;
    @DbColumn("myLongNullableValue")
    public Long longNullableValue;

    @DbColumn("myFloatValue")
    public float floatValue;
    @DbColumn("myFloatNullableValue")
    public Float floatNullableValue;

    @DbColumn("myDoubleValue")
    public double doubleValue;
    @DbColumn("myDoubleNullableValue")
    public Double doubleNullableValue;

    @DbColumn("myBooleanValue")
    public boolean booleanValue;
    @DbColumn("myBooleanNullableValue")
    public Boolean booleanNullableValue;

    @DbColumn("myDate")
    public Date date;

    @DbColumn("myDateNullable")
    public Date dateNullable;

    @DbColumn("myString")
    public String string;

    @DbColumn(value = "myStringNoSelect", select = false)
    public String stringNoSelect;

    @DbColumn(value = "myStringNoInsert", insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}
