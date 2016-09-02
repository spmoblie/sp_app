package com.spshop.stylistpark.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	
	//将原先的表名修改为备用表名
	//private String CREATE_TEMP_MESSAGE = "alter table message rename to _temp_message";
	//将备用表中的数据复制到新建的表中（注意' '是为新加的字段插入默认值的必须加上，否则就会出错）。
	//private String INSERT_DATA = "insert into message select *,'' from _temp_message";
	//删除备用的表
	//private String DROP_MESSAGE = "drop table _temp_message";
	//如果表不存在则创建表
	private String CREATE_CATEGORY = "create table if not exists category("
			+ " _id integer primary key autoincrement," 
			+ " type_id integer,"
			+ " image_url text," 
			+ " name text," 
			+ " data_type integer);";


	public DBOpenHelper(Context context) {
		super(context, "stylistpark.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//商品分类数据库表
		db.execSQL(CREATE_CATEGORY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
