package com.spshop.stylistpark.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spshop.stylistpark.entity.CategoryListEntity;
import com.spshop.stylistpark.utils.ExceptionUtil;

public class CategoryDBService {
	
	private static final String TABLE_NAME = "category";
	private static final String NAME_01 = "type_id";
	private static final String NAME_02 = "image_url";
	private static final String NAME_03 = "name";
	private static final String NAME_04 = "data_type";
	
	private DBOpenHelper dbOpenHelper;
	private Context context;
	private static CategoryDBService instance = null;

	public CategoryDBService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
		this.context = context;
	}

	public synchronized static CategoryDBService getInstance(Context ctx) {
		if (null == instance) {
			instance = new CategoryDBService(ctx);
		}
		return instance;
	}

	public void save(CategoryListEntity en, int dataType) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME_01, en.getTypeId());
		values.put(NAME_02, en.getImageUrl());
		values.put(NAME_03, en.getName());
		values.put(NAME_04, dataType);
		db.insert(TABLE_NAME, null, values);
	}

	public boolean delete(String id) {
		boolean floot = false;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = db.delete(TABLE_NAME, NAME_01+"=?", new String[] { id });
		if (count == 1) {
			floot = true;
		}
		return floot;
	}

	public void deleteAll() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete(TABLE_NAME, "", null);
	}

	public boolean update(CategoryListEntity en, int dataType) {
		boolean floot = false;
		
		CategoryListEntity le = find(en.getTypeId()+"");
		if (le != null) {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(NAME_01, en.getTypeId());
			values.put(NAME_02, en.getImageUrl());
			values.put(NAME_03, en.getName());
			values.put(NAME_04, dataType);
			int count = db.update(TABLE_NAME, values, NAME_01+"=?", new String[] { en.getTypeId()+"" });
			if (count == 1) {
				floot = true;
			}
		}else {
			save(en, dataType);
			floot = true;
		}
		return floot;
	}

	public CategoryListEntity find(String id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, NAME_01+"=?", new String[] { id }, null, null, null, "1");
		try {
			if (cursor.moveToFirst()) {
				return new CategoryListEntity(
						cursor.getInt(cursor.getColumnIndex(NAME_01)),
						cursor.getString(cursor.getColumnIndex(NAME_02)),
						cursor.getString(cursor.getColumnIndex(NAME_03)), 
						new ArrayList<CategoryListEntity>());
			}
			return null;
		} catch (Exception e) {
			ExceptionUtil.handle(context, e);
			return null;
		} finally {
			cursor.close();
		}
	}

	public List<CategoryListEntity> getListData(int dataType) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "data_type=?", new String[] { dataType+"" }, null, null, null, null);
		try {
			List<CategoryListEntity> lists = new ArrayList<CategoryListEntity>();
			while (cursor.moveToNext()) {
				lists.add(new CategoryListEntity(
						cursor.getInt(cursor.getColumnIndex(NAME_01)),
						cursor.getString(cursor.getColumnIndex(NAME_02)),
						cursor.getString(cursor.getColumnIndex(NAME_03)), 
						new ArrayList<CategoryListEntity>()));
			}
//			if (dataType > 0 && lists.size() > 0) { //获取子级分类
//				for (int i = 0; i < lists.size(); i++) {
//					lists.get(i).setChildLists(getListData(lists.get(i).getTypeId()));
//				}
//			}
			return lists;
		} catch (Exception e) {
			ExceptionUtil.handle(context, e);
			return null;
		} finally {
			cursor.close();
		}
	}
	
	public int getCount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+TABLE_NAME, null);
		try {
			cursor.moveToFirst();
			return cursor.getInt(0);
		} catch (Exception e) {
			ExceptionUtil.handle(context, e);
			return 0;
		} finally {
			cursor.close();
		}
	}
}
