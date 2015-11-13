package com.androdblite.domain;

import com.androdblite.DbColumn;
import com.androdblite.DbTable;

import java.util.Date;

/**
 * Created by tgirard on 30/06/15
 */
@DbTable
public class DbEntityTest extends DbEntity {

    @DbColumn
    public int intValue;
    @DbColumn
    public Integer integerNullableValue;

    @DbColumn
    public long longValue;
    @DbColumn
    public Long longNullableValue;

    @DbColumn
    public float floatValue;
    @DbColumn
    public Float floatNullableValue;

    @DbColumn
    public double doubleValue;
    @DbColumn
    public Double doubleNullableValue;

    @DbColumn
    public boolean booleanValue;
    @DbColumn
    public Boolean booleanNullableValue;

    @DbColumn
    public Date date;

    @DbColumn
    public Date dateNullable;

    @DbColumn
    public String myString;

    @DbColumn(value = "myMyCustomColumnName")
    public String string;

    @DbColumn(select = false)
    public String stringNoSelect;

    @DbColumn(insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}
