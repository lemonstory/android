package com.xiaoningmeng.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.orhanobut.logger.Logger;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.SearchContent;

import java.util.ArrayList;
import java.util.List;

public class SearchDao {

	private DBHelper dbhelper;
	private static  SearchDao mInstance;

	private SearchDao() {
		dbhelper = DBHelper.getInstance(MyApplication.getContext());
	}

	public static SearchDao getInstance(){
		if(mInstance == null){
			synchronized (SearchDao.class) {
				if(mInstance == null){
					mInstance = new SearchDao();
				}
			}
		}
		return mInstance;
	}
	
	public boolean addSearch(String content){
		
		if(findSearchContent(content)){
			updateSearchContent(content);
			return false;
		}else{
			addSearchContent(content);
			return true;
		}
	}

	
	private boolean findSearchContent(String content){
		try{
			if (dbhelper.open()) {
				Cursor cursor =dbhelper.sdb.rawQuery("select count(*) from " + DBHelper.TAB_SEARCH + " where content =?", new String[] {content});
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				return count > 0;
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
		} finally {
			dbhelper.close();
		}
		return false;
	}
	
	//更新数据
	private void updateSearchContent(String content){
		try {
			if (dbhelper.open()) {
				ContentValues values=new ContentValues();
				values.put("uptime", System.currentTimeMillis()/1000);
				dbhelper.sdb.update(DBHelper.TAB_SEARCH, values, "content=?", new String[]{content});
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
		} finally {
			dbhelper.close();
		}
	}
	

	

	private void addSearchContent(String content) {
		try {
			if (dbhelper.open()) {
				dbhelper.sdb.execSQL("insert into " + DBHelper.TAB_SEARCH + "(content,uptime)values(?,?)",
						new Object[] {content,System.currentTimeMillis()/1000});
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
		} finally {
			dbhelper.close();
		}
	}
	

	
	public List<SearchContent> getSearchContentList(int len){
		List<SearchContent> SearchContents = new ArrayList<>();
		Cursor cursor = null;
		try{
			if (dbhelper.open()) {
				String sql ="select * from "+DBHelper.TAB_SEARCH+ " order by uptime desc limit "+len+" offset 0";
				cursor = dbhelper.sdb.rawQuery(sql,new String[]{});
				if(cursor != null){
					if(cursor.moveToFirst()){
						do{
							SearchContents.add(parseSearchContent(cursor));
						}while(cursor.moveToNext());
					}
				}
			}
		}catch (Exception e) {
			Logger.e(e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			dbhelper.close();
		}
		return SearchContents;
	}

	
	
	
	private SearchContent parseSearchContent(Cursor cursor) {
		
		SearchContent searchContent = new SearchContent();
		String content = cursor.getString(cursor.getColumnIndex("content"));
		searchContent.setSearchcontent(content);
		searchContent.setUptime(cursor.getLong(cursor.getColumnIndex("uptime"))*1000);
		return searchContent;
	}
	
	
	public void clearSearch(){

		try {
			if (dbhelper.open()) {
				dbhelper.sdb.execSQL("delete from " + DBHelper.TAB_SEARCH,new Object[] {});
			}
		} catch (Exception e) {
			Logger.e(e.getMessage());
		} finally {
			dbhelper.close();
		}
	}
}
