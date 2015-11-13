package com.androdblite;

import com.androdblite.compiler.AndroDbLiteProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by ubuntu on 31/10/15
 */
public class AndroDbLiteProcessorTest {

    @Test
    public void testCompleteNormaleNames() {

        JavaFileObject source = JavaFileObjects.forSourceString("com.test.Entity", Joiner.on('\n').join(
                "package com.test;",
                "import  com.androdblite.DbTable;",
                "import  com.androdblite.DbId;",
                "import  com.androdblite.DbIdServer;",
                "import  com.androdblite.DbIsDelete;",
                "import  com.androdblite.DbColumn;",
                "import  java.util.Date;",
                "@DbTable",
                "public class Entity {",
                "   @DbId",
                "   public long id;",
                "   @DbIdServer",
                "   public long idServer;",
                "   @DbIsDelete",
                "   public boolean isDelete;",
                "   @DbColumn",
                "   public String myColumn1;",
                "   @DbColumn",
                "   public String myColumn2;",
                "}"));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("com/test/Entity_DB",
                Joiner.on('\n').join(
                        "package com.test;",
                        "import com.androdblite.internal.DbIdBinder;",
                        "import com.androdblite.internal.DbIdServerBinder;",
                        "import com.androdblite.internal.DbTableBinder;",
                        "import java.lang.Override;",
                        "import java.lang.String;",
                        " ",
                        "public class Entity_DB<E extends Entity> extends Entity implements DbTableBinder, DbIdBinder, DbIdServerBinder {",
                        "   public Class<E> clazz;",
                        "   public static final String _NAME = \"Entity\";",
                        "   public static final String ID = \"id\";",
                        "   public static final String ID_SERVER = \"idServer\";",
                        "   public static final String MY_COLUMN1 = \"myColumn1\";",
                        "   public static final String MY_COLUMN2 = \"myColumn2\";",
                        "   ",
                        "   @Override",
                        "   public String getTableName() {" +
                        "     return _NAME;",
                        "   }",
                        "   @Override",
                        "   public String getIdSelection() {",
                        "     return \"id=?\";",
                        "   }",
                        "   @Override",
                        "   public String[] getIdSelectionArgs() {",
                        "     return new String[] { String.valueOf( id ) };",
                        "   }",
                        "   @Override",
                        "   public String getIdServerSelection() {",
                        "     return \"idServer=?\";",
                        "   }",
                        "   @Override",
                        "   public String[] getIdServerSelectionArgs() {",
                        "     return new String[] { String.valueOf( idServer ) };",
                        "   }",
                        "   public String getMyColumn1Selection() {",
                        "     return \"myColumn1=?\";",
                        "   }",
                        "   public String getMyColumn2Selection() {",
                        "     return \"myColumn2=?\";",
                        "   }",
                        "}"
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new AndroDbLiteProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}
