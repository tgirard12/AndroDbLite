package com.androdblite.compiler;

/**
 * Created by tgirard on 06/11/15
 */
public class DbColumnBinding {

    /**
     * Name of the static field in upperUnderscoreCase
     */
    public String fieldName;
    /**
     * Name of original Field
     */
    public String fieldOriginalName;
    /**
     * Custom columnName in database
     */
    public String fieldValue;

    public boolean canInsertUpdate = true;
    public boolean canSelect = true;
    public boolean isPrimitiveType = true;

    public DbColumnBinding(String fieldName, String fieldOriginalName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldOriginalName = fieldOriginalName;
        this.fieldValue = fieldValue;
    }



}
