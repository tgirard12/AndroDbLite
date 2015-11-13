package com.androdblite.compiler;

/**
 * Created by tgirard on 12/11/15
 */
public enum DbTypes {

    INT("int"),
    INTEGER("java.lang.Integer"),
    LONG("long"),
    LONG_OBJ("java.lang.Long"),
    FLOAT("float"),
    FLOAT_OBJ("java.lang.Float"),
    DOUBLE("double"),
    DOUBLE_OBJ("java.lang.Double"),
    BOOLEAN("boolean"),
    BOOLEAN_OBJ("java.lang.Boolean"),
    STRING("java.lang.String"),
    DATE("java.util.Date");

    private final String fullName;

    private DbTypes(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static DbTypes valueOfFullName(String name) {
        if (name == null)
            return null;
        if (name.equals(STRING.fullName))
            return STRING;
        if (name.equals(INT.fullName))
            return INT;
        if (name.equals(LONG.fullName))
            return LONG;
        if (name.equals(FLOAT.fullName))
            return FLOAT;
        if (name.equals(DOUBLE.fullName))
            return DOUBLE;
        if (name.equals(BOOLEAN.fullName))
            return BOOLEAN;
        if (name.equals(DATE.fullName))
            return DATE;
        if (name.equals(INTEGER.fullName))
            return INTEGER;
        if (name.equals(LONG_OBJ.fullName))
            return LONG_OBJ;
        if (name.equals(FLOAT_OBJ.fullName))
            return FLOAT_OBJ;
        if (name.equals(DOUBLE_OBJ.fullName))
            return DOUBLE_OBJ;
        if (name.equals(BOOLEAN_OBJ.fullName))
            return BOOLEAN_OBJ;
        return null;
    }
}
