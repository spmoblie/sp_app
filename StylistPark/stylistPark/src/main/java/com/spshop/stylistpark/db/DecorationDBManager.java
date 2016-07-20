package com.spshop.stylistpark.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.spshop.stylistpark.entity.Decoration;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DecorationDBManager {

	public static final String TAG = DecorationDBManager.class.getSimpleName();
	public static DecorationDBManager instance;
	private static Context mContext;

	public static final String DB_NAME = "DecorationElement.db";
	public static final String NAME_DECOR_TEMP_DB = "DecorationElementTemp.db";

	private List<Decoration> decorationList = null;
	private AppSQLiteOpenHelper dmHelper, dTempMHelper;

	public synchronized static DecorationDBManager getInstance(Context context) {
		if (instance == null) {
			mContext = context;
			instance = new DecorationDBManager(context);
		}
		return instance;
	}

	private DecorationDBManager(Context appContext) {
		dmHelper = new AppSQLiteOpenHelper(appContext, DB_NAME);
		dTempMHelper = new AppSQLiteOpenHelper(appContext, NAME_DECOR_TEMP_DB);
	}

	public List<Decoration> getAllDecorationFromTemp() {
		List<Decoration> result = null;
		SQLiteDatabase db = null;
		String sql;
		Cursor c = null;
		try {
			db = dTempMHelper.getReadableDatabase();
			sql = "Select * from decotation_Element";
			LogUtil.i(TAG, "SQL=" + sql);

			c = db.rawQuery(sql, null);
			if (c.moveToFirst()) {
				if (result == null) {
					result = new ArrayList<Decoration>();
				}
				do {
					Decoration decoration = new Decoration(c);
					result.add(decoration);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			result = null;
		} finally {
			if (c != null)
				c.close();
			if (db != null)
				db.close();
		}
		decorationList = result;
		return decorationList;
	}

	public List<Decoration> getAllDecoration() {
		if (decorationList != null) {
			return decorationList;
		}
		List<Decoration> result = null;
		SQLiteDatabase db = null;
		String sql;
		Cursor c = null;
		try {
			db = dmHelper.getReadableDatabase();
			sql = "Select * from decotation_Element";
			LogUtil.i(TAG, "SQL=" + sql);

			c = db.rawQuery(sql, null);
			if (c.moveToFirst()) {
				if (result == null) {
					result = new ArrayList<Decoration>();
				}
				do {
					Decoration decoration = new Decoration(c);
					result.add(decoration);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			result = null;
		} finally {
			if (c != null)
				c.close();
			if (db != null)
				db.close();
		}
		decorationList = result;
		return decorationList;
	}

	public List<Decoration> searchDecoration(String keyword, String color) {
		List<Decoration> result = null;
		SQLiteDatabase db = null;
		String sql;
		List<String> whereList;
		Cursor c = null;
		try {
			db = dmHelper.getReadableDatabase();
			sql = "Select * from decotation_Element where";
			whereList = new ArrayList<String>();
			if (keyword != null) {
				whereList.add(" keyword like '%" + keyword + "%' ");
			}
			if (color != null) {
				whereList.add(" color like '%" + color + "%' ");
			}
			for (int i = 0; i < whereList.size(); i++) {
				if (i == 0) {
					sql += whereList.get(i);
				} else {
					sql += " and " + whereList.get(i);
				}
			}
			LogUtil.i(TAG, "SQL=" + sql);
			
			c = db.rawQuery(sql, null);
			if (c.moveToFirst()) {
				if (result == null) {
					result = new ArrayList<Decoration>();
				}
				do {
					Decoration decoration = new Decoration(c);
					result.add(decoration);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
			result = null;
		} finally {
			if (c != null)
				c.close();
			if (db != null)
				db.close();
		}
		return result;
	}

	public void copyDatabase() {
		dmHelper.copyDatabase();
	}

}
