# AndroDbLite
Simple Sqlite database library for Android

- You write create and update database script
- You manage relationships, foreign key and index
- No needs to extend specific Object (for basic functionnality)
- Dedicate entity for managing insert, delete, update from _id, idServer, isDelete, isSent

## Maven dependencies

```
compile 'com.tgirard12:com.androdblite:0.2.0'
```

## Simple Usage

### Update your manifest.xml

```xml

<application>
	
	...
	
	<meta-data android:name="DB_NAME" android:value="MY_CUSTOM_NAME.sqlite"/>
	<meta-data android:name="DB_VERSION" android:value="1"/>

	...

</application 
```

### Design your entity

```java

@DbTable(name = "myCustomUser")
public class User {
	
	@DbColumn
	private long _id;

	@DbColumn(name = "myCustomName") // Custom column name
	private String name;

	@DbColumn
	private Date myDate;

	@DbColumn(select = false, insertUpdate = false) // not use in SELECT, INSERT or UPDATE
	private String transientField;

	// Getters and Setters

}
```

### Write dbcreate.sql

The dbcreate.sql file must be in the **assets/AndroDbLite/** directory. 
It will be used in **SqliteOpenHelper.onCreate()** method.
Line separator are authorized in the SQL script (the ; is the separator)

```sql
CREATE TABLE myCustomUser 
(
	_id           integer primary key autoincrement ,
	myCustomName  nvarchar(50),
	myDate        integer,
	
	-- Other columns

);  -- DON'T FORGET THE ; 

-- Other Tables

```

### Update your schema

In the **asset/AndroDbLite/** directory, create a file 1.sql, 2.sql, ... for each new database version.
Update your current database version in your **manifest.xml**

It's done, your schema will be update from previous to new version.

### Use the DbRepositoryImpl class

The simplest way for querying the database is using the DbRepositoryImpl class :

```java
public class MainActivity extends Activity {

	// Inject SINGLETON instance of DbRepositoyImpl
	DbRepository dbRepository;

	// ... 

	public void doActionOnDatabse(User user) {
	
		long _id = db.insert(user);
		user = db.find(User.class, "_id=?", new String[]{"1"});

		user.setName("Thomas");
		int nbRowUpdated = db.update(user, "_id=?", new String[]{"1"});
	}
}

```
## Advanced usages

### Managed types

     Java               SQL
- String           | nvarchar 
- int, Integer     | integer
- long, Long       | integer
- float, Float     | float
- double, Double   | float
- boolean, Boolean | boolean
- java.util.Date   | integer (Unix timestamp)

### Use DbEntity and DbEntityServer

// TBD

### Extends DbRepositoryImpl for custom query

// TBD

### Use Custom SQLiteOpenHelper

// TBD

### Missing Feature

You like AndroDbLite, some feature are missing, submit an issue or a pull request.

## Thanks to

- SugarDb for the inspiration

