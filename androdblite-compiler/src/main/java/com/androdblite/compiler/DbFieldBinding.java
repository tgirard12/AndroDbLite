package com.androdblite.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tgirard on 06/11/15
 */
public class DbFieldBinding {

    public String packageName;
    public String className;
    public List<DbColumnBinding> fieldBindings;

    public DbColumnBinding dbTable;
    public DbColumnBinding dbFieldId;
    public DbColumnBinding dbFieldIdServer;
    public DbColumnBinding dbFieldIsDelete;

    public DbFieldBinding() {
        fieldBindings = new ArrayList<>(10);
    }

}
