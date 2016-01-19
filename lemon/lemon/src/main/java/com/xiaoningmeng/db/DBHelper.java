package com.xiaoningmeng.db;


import com.xiaoningmeng.utils.DebugUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper {

	private static DBHelper dbhelper = null;
	private SqliteHelper sqhelper;
	private Context ctx = null;
	public SQLiteDatabase sdb;
	public boolean isDatabaseOprating = false;
	private static final int version = 1; // 数据库版本
	private static final String DATABASE_NAME = "lemon";
	private static final String DATABASE_SUFFIX = ".db";
	public static final String TAB_HISTORY = "tab_history";
	public static final String TAB_STORY = "tab_story";
	public static final String TAB_SEARCH = "tab_search";

	public static DBHelper getInstance(Context context) {
		if (dbhelper == null) {
			dbhelper = new DBHelper(context);
		}
		return dbhelper;
	}

	private DBHelper(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * 数据库管理类
	 * 
	 * 
	 */
	private static class SqliteHelper extends SQLiteOpenHelper {
		public SqliteHelper(Context paramContext) {
			super(paramContext, DATABASE_NAME + DATABASE_SUFFIX, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_HISTORY);
			db.execSQL(SQL_CREATE_STORY);
			db.execSQL(SQL_CREATE_SEARCH);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}

	/**
	 * 打开连接
	 * 
	 * @return 是否正在使用
	 */
	public boolean open() {
		if (!isDatabaseOprating) {
			isDatabaseOprating = true;
			this.sqhelper = new SqliteHelper(ctx);
			this.sdb = this.sqhelper.getWritableDatabase();
			return true;
		}
		return false;
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		try {
			this.sqhelper.close();
			isDatabaseOprating = false;
			sdb.close();
		} catch (Exception e) {
			if (sdb != null)
				sdb.close();
			DebugUtils.exception(e);
		}
	}


	// 建表－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

	private static final String SQL_CREATE_HISTORY = "create table if not exists "
			+ TAB_HISTORY
			+ "(_id INTEGER PRIMARY KEY autoincrement,uid VARCHAR,albumid VARCHAR,storyid VARCHAR,uptime NUMBER,current INTEGER,title VARCHAR,intro VARCHAR,star_level VARCHAR,cover VARCHAR,fav INTEGER,listennum INTEGER,favnum INTEGER,commentnum INTEGER,upload INTEGER);";

	private static final String SQL_CREATE_STORY = "create table if not exists "
			+ TAB_STORY
			+ "(_id INTEGER PRIMARY KEY autoincrement,storyId VARCHAR,albumid VARCHAR,title VARCHAR,intro VARCHAR,times VARCHAR,file_size VARCHAR,mediapath VARCHAR,cover VARCHAR,playcover VARCHAR);";
	
	private static final String SQL_CREATE_SEARCH ="create table if not exists "
			+TAB_SEARCH
			+"(_id INTEGER PRIMARY KEY autoincrement,content VARCHAR,uptime NUMBER);";
}
