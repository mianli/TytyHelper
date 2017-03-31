package com.mli.crown.tytyhelper.tools.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mli.crown.tytyhelper.tools.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crown on 2017/3/23.
 */
public class DownloadDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "download.db";
	private static final String TABLE_NAME = "download";

	public DownloadDbHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE_NAME + "(" + DownloadBean._ID
			+ " integer primary key autoincrement,"
			+ DownloadBean.URL + " text not null,"
			+ DownloadBean.TOTAL_LENGTH + " number(64), "
			+ DownloadBean.BREAK_POINTS + " number(64), "
			+ DownloadBean.START_TIME + " number(64),"
			+ DownloadBean.END_TIME + " number(64));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public synchronized void saveDownloadInfo(String url, long totalLength) {
		if(url == null) {
			return;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DownloadBean.URL, url);
		values.put(DownloadBean.TOTAL_LENGTH, totalLength);
		values.put(DownloadBean.BREAK_POINTS, 0);
		values.put(DownloadBean.START_TIME, System.currentTimeMillis());
		values.put(DownloadBean.END_TIME, 0L);
		db.insert(TABLE_NAME, null, values);
		db.close();
		Log.i("----------------->插入成功" + url);
	}

	private synchronized Cursor search(String url) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + DownloadBean.URL + " = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{url});
		return cursor;
	}

	public synchronized DownloadBean searchUrl(String url) {
		Cursor cursor;
		if((cursor = search(url)).getCount() > 0) {
			DownloadBean downloadBean = null;
			cursor.moveToFirst();
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean._ID));
				long totalLength = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.TOTAL_LENGTH));
				long breakPoints = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.BREAK_POINTS));
				long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.START_TIME));
				long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.END_TIME));
				downloadBean = new DownloadBean(id, url, totalLength, breakPoints, startTime, endTime);
			}while (cursor.moveToNext());
			cursor.close();
			return downloadBean;
		}
		cursor.close();
		return null;
	}

	public synchronized boolean isExist(String url) {
		if(url == null) {
			return false;
		}
		Cursor cursor = search(url);
		int result = cursor.getCount();
		cursor.close();
		return result > 0;
	}

	public synchronized List<DownloadBean> getDownloadList(String downloadUrl) {
		Cursor cursor;
		if((cursor = search(downloadUrl)).getCount() > 0) {
			List<DownloadBean> list = new ArrayList<>();
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean._ID));
				String url = cursor.getString(cursor.getColumnIndexOrThrow(DownloadBean.URL));
				long totalLength = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.TOTAL_LENGTH));
				long breakPoints = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.BREAK_POINTS));
				long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.START_TIME));
				long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.END_TIME));
				list.add(new DownloadBean(id, url, totalLength, breakPoints, startTime, endTime));
			}
			cursor.close();
			return list;
		}
		cursor.close();
		return null;
	}

	public synchronized List<DownloadBean> getDownloadList() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME ;
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.getCount() > 0) {
			Log.i(cursor.getCount() + "");
			List<DownloadBean> list = new ArrayList<>();
			cursor.moveToFirst();
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean._ID));
				String url = cursor.getString(cursor.getColumnIndexOrThrow(DownloadBean.URL));
				long totalLength = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.TOTAL_LENGTH));
				long breakPoints = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.BREAK_POINTS));
				long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.START_TIME));
				long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadBean.END_TIME));
				list.add(new DownloadBean(id, url, totalLength, breakPoints, startTime, endTime));
			}while (cursor.moveToNext());
			cursor.close();
			return list;
		}
		cursor.close();
		return null;
	}

	public synchronized void delete(String url) {
		String sql = "delete from " + TABLE_NAME + " where " + DownloadBean.URL + " = ?";
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql, new String[] {url});
		db.close();
	}

	public synchronized void updateBreakPoints(String url, long breakPoints) {
		try{
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DownloadBean.BREAK_POINTS, breakPoints);
			db.update(TABLE_NAME, values, DownloadBean.URL + "=?", new String[]{url});
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void updateContentLength(String url, long contentlength) {
		try{
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DownloadBean.TOTAL_LENGTH, contentlength);
			db.update(TABLE_NAME, values, DownloadBean.URL + "=?", new String[]{url});
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
