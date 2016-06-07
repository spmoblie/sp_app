package com.spshop.stylistpark.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.LogUtil;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppSQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = AppSQLiteOpenHelper.class.getSimpleName();

	Context mContext;
	String dbPath;
	String dbName;
	String dbFullPath;

	public AppSQLiteOpenHelper(Context context, String name) {
		super(context, name, null, 1);
		mContext = context;
		dbName = name;
		dbPath = context.getDatabasePath(dbName).getParentFile().getAbsolutePath();
		dbFullPath = dbPath + "/" + dbName;

		if (!checkDataBaseExist()) {
			this.getReadableDatabase();
			copyDatabase();
			close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.i(TAG, "db- onCreate()");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.i(TAG, "db- onUpgrade()");
	}

	public boolean checkDataBaseExist() {
		File dbFile = new File(dbFullPath);
		return dbFile.exists();
	}

	public void copyDatabase() {
		LogUtil.i(TAG, "db- copyDatabase()");

		AssetManager assetManager = mContext.getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(dbName);
			File outFile = new File(dbPath, dbName);
			out = new FileOutputStream(outFile);
			copyFile(in, out);
		} catch (IOException e) {
			Log.e("tag", "Failed to copy asset file: " + dbFullPath, e);
			ExceptionUtil.handle(mContext, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					ExceptionUtil.handle(mContext, e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					ExceptionUtil.handle(mContext, e);
				}
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
