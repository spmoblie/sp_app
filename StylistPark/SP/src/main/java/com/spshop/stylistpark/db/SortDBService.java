package com.spshop.stylistpark.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spshop.stylistpark.entity.SortListEntity;
import com.spshop.stylistpark.utils.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;

public class SortDBService {
	
	private static final String TABLE_NAME = "sort";
	private static final String NAME_01 = "type_id";
	private static final String NAME_02 = "image_url";
	private static final String NAME_03 = "name";
	private static final String NAME_04 = "data_type";
	
	private DBOpenHelper dbOpenHelper;
	private static SortDBService instance = null;

	private SortDBService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}

	private static synchronized void syncInit(Context context) {
		if (instance == null) {
			instance = new SortDBService(context);
		}
	}

	public static SortDBService getInstance(Context ctx) {
		if (instance == null) {
			syncInit(ctx);
		}
		return instance;
	}

	public void save(SortListEntity en, int dataType) {
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

	public boolean update(SortListEntity en, int dataType) {
		boolean floot = false;
		
		SortListEntity le = find(en.getTypeId()+"");
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

	public SortListEntity find(String id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, NAME_01+"=?", new String[] { id }, null, null, null, "1");
		try {
			if (cursor.moveToFirst()) {
				return new SortListEntity(
						cursor.getInt(cursor.getColumnIndex(NAME_01)),
						cursor.getString(cursor.getColumnIndex(NAME_02)),
						cursor.getString(cursor.getColumnIndex(NAME_03)), 
						new ArrayList<SortListEntity>());
			}
			return null;
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			return null;
		} finally {
			cursor.close();
		}
	}

	public List<SortListEntity> getListData(int dataType) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, "data_type=?", new String[] { dataType+"" }, null, null, null, null);
		try {
			List<SortListEntity> lists = new ArrayList<SortListEntity>();
			while (cursor.moveToNext()) {
				lists.add(new SortListEntity(
						cursor.getInt(cursor.getColumnIndex(NAME_01)),
						cursor.getString(cursor.getColumnIndex(NAME_02)),
						cursor.getString(cursor.getColumnIndex(NAME_03)), 
						new ArrayList<SortListEntity>()));
			}
//			if (dataType > 0 && lists.size() > 0) { //获取子级分类
//				for (int i = 0; i < lists.size(); i++) {
//					lists.get(i).setChildLists(getListData(lists.get(i).getTypeId()));
//				}
//			}
			return lists;
		} catch (Exception e) {
			ExceptionUtil.handle(e);
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
			ExceptionUtil.handle(e);
			return 0;
		} finally {
			cursor.close();
		}
	}
}
