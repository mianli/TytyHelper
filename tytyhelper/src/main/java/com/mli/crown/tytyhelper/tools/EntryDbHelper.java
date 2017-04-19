package com.mli.crown.tytyhelper.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.mli.crown.tytyhelper.bean.LoginInfo;
import com.mli.crown.tytyhelper.bean.SimpleLoginInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crown on 2017/3/22.
 */
public class EntryDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "loginInfo.db";
	public static final String TABLE_NAME = "loginInfo";
	private static final int VERSION = 1;

	private Context mContext;
	public EntryDbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE_NAME + "(" + LoginInfo._ID
			+ " integer primary key autoincrement," + LoginInfo.USER_NAME + " text not null,"
			+ LoginInfo.PASSWORD + " text not null,"
			+ LoginInfo.DESC + " text not null,"
			+ LoginInfo.SAVE_TIME + " number(64));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("暂时不要对数据库进行更新");
	}

	public long saveInfo(String username, String password, String desc) {
		if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LoginInfo.USER_NAME, username);
		values.put(LoginInfo.PASSWORD, password);
		values.put(LoginInfo.DESC, desc);
		values.put(LoginInfo.SAVE_TIME, System.currentTimeMillis());
		long result = db.insert(TABLE_NAME, null, values);
		db.close();
		return result;
	}

	public List<LoginInfo> Search(String username) {
		Cursor cursor;
		if((cursor = search(username)).getCount() > 0) {
			List<LoginInfo> list = new ArrayList<>();
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.USER_NAME));
				String password = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.PASSWORD));
				String desc = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.DESC));
				long saveTime = cursor.getInt(cursor.getColumnIndexOrThrow(LoginInfo.SAVE_TIME));

				list.add(new LoginInfo(name, password, desc, saveTime));
			}
			return list;
		}
		return null;
	}

	private Cursor search(String username) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + LoginInfo.USER_NAME + " = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{username});
		return cursor;
	}

	public boolean isExist(SimpleLoginInfo info) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + LoginInfo.USER_NAME + " = ? and " + LoginInfo.PASSWORD +" = ? and " + LoginInfo.DESC + " = ?";
		Cursor cursor = db.rawQuery(sql, new String[] {info.getUsername(), info.getPasswrod(), info.getDesc()});
		if(cursor.getCount() != 0) {
			return true;
		}
		return false;
	}

	public boolean isExist(String username) {
		if(username == null) {
			return false;
		}
		Cursor cursor = search(username);
		int result = cursor.getCount();
		cursor.close();
		return result > 0;
	}

	public void delete(String username, String password) {
		String sql = "delete from " + TABLE_NAME + " where " + LoginInfo.USER_NAME + " = ? and " +
			LoginInfo.PASSWORD + " = ?";// + username;

		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql, new String[] {username, password});
		db.close();
//		db.delete(TABLE_NAME, " where " + LoginInfo.USER_NAME + " = ?", new String[]{username});
	}

	public List<LoginInfo> getList() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor == null) {
			return null;
		}
		List<LoginInfo> list = new ArrayList<>();
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String name = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.USER_NAME));
				String password = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.PASSWORD));
				String desc = cursor.getString(cursor.getColumnIndexOrThrow(LoginInfo.DESC));
				long saveTime = cursor.getInt(cursor.getColumnIndexOrThrow(LoginInfo.SAVE_TIME));

				list.add(new LoginInfo(name, password, desc, saveTime));
			}while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return list;
	}

	public void clearTable() {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from " + TABLE_NAME;
		db.execSQL(sql);
	}

}
