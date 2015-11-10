package com.androdblite.compiler;

/**
 * Created by tgirard on 06/11/15
 */
public class DbColumnBinding {

    public String fieldName;
    public String fieldOriginalName;
    public String fieldValue;

    public DbColumnBinding(String fieldName, String fieldOriginalName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldOriginalName = fieldOriginalName;
        this.fieldValue = fieldValue;
    }



}
