package com.androdblite.domain;

import com.androdblite.annotation.DbColumn;
import com.androdblite.annotation.DbTable;

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

    @DbColumn(name = "myMyCustomColumnName")
    public String string;

    @DbColumn(select = false)
    public String stringNoSelect;

    @DbColumn(insertUpdate = false)
    public String stringNoInsert;

    public String stringTransient;
}
