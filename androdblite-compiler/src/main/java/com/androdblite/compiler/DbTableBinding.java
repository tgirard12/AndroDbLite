package com.androdblite.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tgirard on 06/11/15
 */
public class DbTableBinding {

    public String packageName;
    public String className;
    public String classOriginalName;
    public List<DbColumnBinding> dbColumnBindings;

    public DbColumnBinding dbTable;
    public DbColumnBinding dbColumnId;
    public DbColumnBinding dbColumnIdServer;
    public DbColumnBinding dbColumnIsDelete;
    public DbTypes dbType;

    public DbTableBinding() {
        dbColumnBindings = new ArrayList<>(10);
    }

}
