package com.kelong.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sqlite 不能加密，可以对敏感字段进行加密再存储，读取后再解密
 * 可以用开源工具 SQLCipher，apk会增大约 6M 左右：git clone git://github.com/sqlcipher/android-database-sqlcipher.git 
 * @author Administrator
 *
 */

public class DBManager {

	private SQLiteDatabase db = null;
	private MySqLiteHelper mHelper;
	private Context mContext;
	private String mDbName;
	
	/**
	 * 打开数据库，数据库不存在时，创建一个
	 * @param context
	 * @param dbName
	 * @param dbVersion
	 */
	public DBManager(Context context, String dbName, int dbVersion) {
		mContext = context;
		mDbName = dbName;
		mHelper = new MySqLiteHelper(context, dbName, dbVersion);
		/*
		 * getWritableDatabase:它会调用并返回一个可以读写数据库的对象;在第一次调用时会调用onCreate的方法;
		 * 当数据库存在时会调用onOpen方法;结束时调用onClose方法 当磁盘已经满了时--抛异常；
		 * getReadableDatabase:它会调用并返回一个可以读写数据库的对象
		 * ;在第一次调用时会调用onCreate的方法;当数据库存在时会调用onOpen方法;结束时调用onClose方法
		 * 当磁盘已经满了时--不会返回读写数据库的对象，而是仅仅返回一个读数据库的对象
		 */
		db = mHelper.getWritableDatabase();
	}
	
	/**
     * 删除数据库
     *
     * @return 成功返回true，否则返回false
     */
    public boolean deleteDataBase() {
        return mContext.deleteDatabase(mDbName);
    }
	
	/**
	 * 关闭数据库
	 */
	public void closeDataBase() {
		db.close();
        mHelper = null;
        db = null;
	}
	
	/**
	 * 执行 sql  execSQL()方法可以执行insert、delete、update和CREATE TABLE之类有更改行为的SQL语句；
	 * @param sql
	 */
	public void execSQL(String sql) {
		db.execSQL(sql);
	}
	
	/**
	 * 执行 sql  rawQuery()方法可以执行select语句。
	 * @param sql
	 */
	public Cursor execSelectSQL(String sql) {
		return db.rawQuery(sql, null);

	}

	/**
	 * 判断表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean existTable(String tableName) {
		boolean ret = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "'";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 创建表格
	 * @param mClass
	 * @param isNew
	 */
	public void createTable(Class<?> mClass) {
		mHelper.createTable(db, mClass);
	}
	
	/**
     * 插入一条数据
    *
    * @param obj
    * @return 返回-1代表插入数据库失败，否则成功
    * @throws IllegalAccessException
    */
   public long insert(Object obj) {
       Class<?> modeClass = obj.getClass();
       Field[] fields = modeClass.getDeclaredFields(); //Field java反射成员变量
       ContentValues values = new ContentValues();

       for (Field fd : fields) {
           fd.setAccessible(true);
           String fieldName = fd.getName();
           //剔除主键id值得保存，由于框架默认设置id为主键自动增长
           if (fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("_id")) {
               continue;
           }
           putValues(values, fd, obj);
       }
       return db.insert(DBUtils.getTableName(modeClass), null, values);
   }
   
   /**
    * 更新一条记录
    *
    * @param clazz  类
    * @param values 更新对象
    * @param id     更新id索引
    */
   public void updateById(Class<?> clazz, ContentValues values, long id) {
       db.update(clazz.getSimpleName(), values, "id=" + id, null);
   }
   
   /**
    * 删除记录一条记录
    *
    * @param clazz 需要删除的类名
    * @param id    需要删除的 id索引
    */
   public void deleteById(Class<?> clazz, long id) {
       db.delete(DBUtils.getTableName(clazz), "id=" + id, null);
   }
   
   /**
    * ContentValues:ContentValues 和HashTable类似都是一种存储的机制 
    * 但是两者最大的区别就在于，contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
    * 在忘数据库中插入数据的时候，首先应该有一个ContentValues的对象
    * @param values
    * @param fd
    * @param obj
    */
   private void putValues(ContentValues values, Field fd, Object obj) {
       Class<?> clazz = values.getClass();
       try {
           Object[] parameters = new Object[]{fd.getName(), fd.get(obj)};
           Class<?>[] parameterTypes = getParameterTypes(fd, fd.get(obj), parameters);
           Method method = clazz.getDeclaredMethod("put", parameterTypes);
           method.setAccessible(true);
           method.invoke(values, parameters);
       } catch (NoSuchMethodException e) {
           e.printStackTrace();
       } catch (InvocationTargetException e) {
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       }
   }
   
   /**
    * 查询数据库中所有的数据
    *
    * @param clazz
    * @param <T>   以 List的形式返回数据库中所有数据
    * @return 返回list集合
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws NoSuchMethodException
    * @throws InvocationTargetException
    */
   public <T> List<T> findAll(Class<T> clazz) {
       Cursor cursor = db.query(clazz.getSimpleName(), null, null, null, null, null, null);
       return getEntity(cursor, clazz);
   }
   
   /**
    * 得到反射方法中的参数类型
    *
    * @param field
    * @param fieldValue
    * @param parameters
    * @return
    */
   private Class<?>[] getParameterTypes(Field field, Object fieldValue, Object[] parameters) {
       Class<?>[] parameterTypes;
       if (isCharType(field)) {
           parameters[1] = String.valueOf(fieldValue);
           parameterTypes = new Class[]{String.class, String.class};
       } else {
           if (field.getType().isPrimitive()) {
               parameterTypes = new Class[]{String.class, getObjectType(field.getType())};
           } else if ("java.util.Date".equals(field.getType().getName())) {
               parameterTypes = new Class[]{String.class, Long.class};
           } else {
               parameterTypes = new Class[]{String.class, field.getType()};
           }
       }
       return parameterTypes;
   }
   
   /**
    * 是否是字符类型
    *
    * @param field
    * @return
    */
   private boolean isCharType(Field field) {
       String type = field.getType().getName();
       return type.equals("char") || type.endsWith("Character");
   }
   
   /**
    * 得到对象的类型
    *
    * @param primitiveType
    * @return
    */
   private Class<?> getObjectType(Class<?> primitiveType) {
       if (primitiveType != null) {
           if (primitiveType.isPrimitive()) {
               String basicTypeName = primitiveType.getName();
               if ("int".equals(basicTypeName)) {
                   return Integer.class;
               } else if ("short".equals(basicTypeName)) {
                   return Short.class;
               } else if ("long".equals(basicTypeName)) {
                   return Long.class;
               } else if ("float".equals(basicTypeName)) {
                   return Float.class;
               } else if ("double".equals(basicTypeName)) {
                   return Double.class;
               } else if ("boolean".equals(basicTypeName)) {
                   return Boolean.class;
               } else if ("char".equals(basicTypeName)) {
                   return Character.class;
               }
           }
       }
       return null;
   }
   
   /**
    * 从数据库得到实体类
    *
    * @param cursor
    * @param clazz
    * @param <T>
    * @return
    */
   private <T> List<T> getEntity(Cursor cursor, Class<T> clazz) {
       List<T> list = new ArrayList<>();
       try {
           if (cursor != null && cursor.moveToFirst()) {
               do {
                   Field[] fields = clazz.getDeclaredFields();
                   T modeClass = clazz.newInstance();
                   for (Field field : fields) {
                       Class<?> cursorClass = cursor.getClass();
                       String columnMethodName = getColumnMethodName(field.getType());
                       Method cursorMethod = cursorClass.getMethod(columnMethodName, int.class);

                       Object value = cursorMethod.invoke(cursor, cursor.getColumnIndex(field.getName()));

                       if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                           if ("0".equals(String.valueOf(value))) {
                               value = false;
                           } else if ("1".equals(String.valueOf(value))) {
                               value = true;
                           }
                       } else if (field.getType() == char.class || field.getType() == Character.class) {
                           value = ((String) value).charAt(0);
                       } else if (field.getType() == Date.class) {
                           long date = (Long) value;
                           if (date <= 0) {
                               value = null;
                           } else {
                               value = new Date(date);
                           }
                       }
                       String methodName = makeSetterMethodName(field);
                       Method method = clazz.getDeclaredMethod(methodName, field.getType());
                       method.invoke(modeClass, value);
                   }
                   list.add(modeClass);
               } while (cursor.moveToNext());
           }

       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (cursor != null) {
               cursor.close();
           }
       }
       return list;
   }

   private String getColumnMethodName(Class<?> fieldType) {
       String typeName;
       if (fieldType.isPrimitive()) {
           typeName = DBUtils.capitalize(fieldType.getName());
       } else {
           typeName = fieldType.getSimpleName();
       }
       String methodName = "get" + typeName;
       if ("getBoolean".equals(methodName)) {
           methodName = "getInt";
       } else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
           methodName = "getString";
       } else if ("getDate".equals(methodName)) {
           methodName = "getLong";
       } else if ("getInteger".equals(methodName)) {
           methodName = "getInt";
       }
       return methodName;
   }


   private boolean isPrimitiveBooleanType(Field field) {
       Class<?> fieldType = field.getType();
       if ("boolean".equals(fieldType.getName())) {
           return true;
       }
       return false;
   }

   private String makeSetterMethodName(Field field) {
       String setterMethodName;
       String setterMethodPrefix = "set";
       if (isPrimitiveBooleanType(field) && field.getName().matches("^is[A-Z]{1}.*$")) {
           setterMethodName = setterMethodPrefix + field.getName().substring(2);
       } else if (field.getName().matches("^[a-z]{1}[A-Z]{1}.*")) {
           setterMethodName = setterMethodPrefix + field.getName();
       } else {
           setterMethodName = setterMethodPrefix + DBUtils.capitalize(field.getName());
       }
       return setterMethodName;
   }
	
	//////////////////////////////////////////////////////////

	/**
	 * 数据库帮助类
	 */
	class MySqLiteHelper extends SQLiteOpenHelper {

		public MySqLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		/**
		 * 构造函数
		 * 
		 * @param context 上下文环境
		 * @param db_name 数据库名字
		 * @param db_version
		 * @param clazz
		 */
		public MySqLiteHelper(Context context, String db_name, int db_version) {
			// 上下文环境，数据库名字，游标工程(可选，通常为null)，版本
			// 默认在 data/data/<`package_name>/database/
			// 下创建，在里面的文件也相对有安全性，删除应用的时候也会一起删除掉
			this(context, db_name, null, db_version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 创建数据库调用
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// db.execSQL("DROP TABLE IF EXISTS" +
			// DBUtils.getTableName(mClazz));
			// createTable(db);
		}

		/**
		 * 根据制定类名创建表
		 */
		private void createTable(SQLiteDatabase db, Class<?> mClass) {
			db.execSQL(getCreateTableSql(mClass));
		}

		/**
		 * 得到建表语句
		 *
		 * @param clazz 指定类
		 * @return sql语句
		 */
		private String getCreateTableSql(Class<?> clazz) {
			StringBuilder sb = new StringBuilder();
			String tabName = clazz.getSimpleName();
			// 创建表格，记住 SQLite的id 前一定要加 _ ,即_id
			sb.append("create table if not exists ").append(tabName).append(" (id  INTEGER PRIMARY KEY AUTOINCREMENT, ");
			Field[] fields = clazz.getDeclaredFields();
			for (Field fd : fields) {
				String fieldName = fd.getName();
				String fieldType = fd.getType().getName();
				if (fieldName.equalsIgnoreCase("_id")
						|| fieldName.equalsIgnoreCase("id")) {
					continue;
				} else {
					sb.append(fieldName)
							.append(DBUtils.getColumnType(fieldType))
							.append(", ");
				}
			}
			int len = sb.length();
			sb.replace(len - 2, len, ")");
			return sb.toString();
		}
	}
	
}
