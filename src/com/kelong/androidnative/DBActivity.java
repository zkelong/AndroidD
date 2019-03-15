package com.kelong.androidnative;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import com.kelong.db.DBManager;
import com.kelong.utils.ApplicationConfig;

public class DBActivity extends Activity {

	private static final int DB_VERSION = 1;
	private DBManager dbManager;
	private int index = 0;
	private String databaseFile;

	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_db);
		tvResult = getView(R.id.tv_result);

		databaseFile = ApplicationConfig.dbPath + ApplicationConfig.dbName;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// dbManagerf.closeDataBase();
		if (dbManager != null) {
			dbManager.closeDataBase();
		}
	}

	// 返回
	public void back(View view) {

	}

	// 数据库文件是否存在
	public void exists(View view) {
		// 创建databases目录（不存在时）
		File file = new File(databaseFile);
		if (!file.exists()) {
			showResult("Db do not exist!!" + databaseFile);
		} else {
			showResult("Db exist!!" + databaseFile);
		}
	}

	// 创建数据库
	public void create(View view) {
		dbManager = new DBManager(this, ApplicationConfig.dbName, DB_VERSION);
		showResult("已创建");
	}

	// 判断表存在否
	public void existsTable(View view) {
		if (dbManager != null) {
			//class.getName() 包含包路径
			//class.getSimpleName() 只有类名
			boolean exist = dbManager.existTable(Person.class.getSimpleName());
			if (exist) {
				showResult("Table exist!!" + Person.class.getSimpleName());
			} else {
				showResult("Table do not exist!!" + Person.class.getSimpleName());
			}
		}
	}

	// 创建表
	public void createTable(View view) {
		if (dbManager != null) {
			dbManager.createTable(Person.class);
			showResult("已创建表");
		}
	}

	// 插入数据
	public void insert(View view) {
		index++;
		Person person = new Person("name" + index, 2, true);
		if (dbManager != null) {
			dbManager.insert(person);
		}
		findAll();
	}

	// 更新数据
	public void update(View view) {
		if (dbManager != null) {
			ContentValues values = new ContentValues();
			values.put("age", 34);
			dbManager.updateById(Person.class, values, 1);
		}
		findAll();
	}

	// 删除数据
	public void delete(View view) {
		int id = index--;
		if (dbManager != null) {
			dbManager.deleteById(Person.class, id);
			findAll();
		}
	}

	// 获取所有数据
	private void findAll() {
		if (dbManager != null) {
			List<Person> list = dbManager.findAll(Person.class);
			StringBuilder sb = new StringBuilder();
			for (int i = 0, size = list.size(); i < size; i++) {
				sb.append(list.get(i).toString()).append("\n");
			}
			showResult(sb.toString());
		}
	}

	public void execSQL(View v) {
		String sql = "select * from Person where id > 1";
		String result = "Result:\n";
		if (dbManager != null) {
			Cursor cursor = dbManager.execSelectSQL(sql);
			if (cursor != null && cursor.moveToFirst()) {
				result += "ColumnNames: ";
				String[] columnNames = cursor.getColumnNames();
				for(int i = 0; i < columnNames.length; i++) {
					result += columnNames[i] + "  ";
				}
				result += "\n";
				do{
					result += cursor.getInt(0)+ "  ";
					result += cursor.getString(1)+ "  ";
					result += cursor.getString(2)+ "  ";
					result += cursor.getInt(3)+ "  ";
					result += "\n";
				}while (cursor.moveToNext());
			}
		}
		showResult(result);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int id) {
		return (T) findViewById(id);
	}

	private void showResult(String txt) {
		tvResult.setText(txt);
	}
}