package com.xiaoningmeng.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.event.HistoryEvent;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class HistoryDao {

	private DBHelper dbhelper;
	private static  HistoryDao mInstance;

	private HistoryDao() {
		dbhelper = DBHelper.getInstance(MyApplication.getContext());
	}

	public static HistoryDao getInstance(){
		if(mInstance == null){
			synchronized (HistoryDao.class) {
				if(mInstance == null){
					mInstance = new HistoryDao();
				}
			}
		}
		return mInstance;
	}
	//新增历史数据
	public void add(final AlbumInfo albumInfo,final String storyId,final int current){
			String albumId = albumInfo.getAlbumid();
			String uid = MyApplication.getInstance().getUid();
			long uploadTime = System.currentTimeMillis()/1000;
			ListenerAlbum listenerAlbum = new ListenerAlbum(uid, storyId, albumInfo.getAlbumid(), uploadTime+"",current,albumInfo);
			if(findHistoryAlbum(albumId)){
					deleteAlbumStory(albumId);
					updateAlbum(listenerAlbum);
			}else{
					addHistoryAlbum(uid,albumId,albumInfo, storyId,0,uploadTime);
			}
			if(albumInfo != null && albumInfo.getStoryinfo() !=null) {
				addHistoryStory(albumId, albumInfo.getStoryinfo());
			}
			EventBus.getDefault().post(new HistoryEvent(listenerAlbum, albumId, storyId));
		}
	
	private boolean  findHistoryAlbum(String albumId){
		try{
			if (dbhelper.open()) {
				Cursor cursor =dbhelper.sdb.rawQuery("select count(*) from " + DBHelper.TAB_HISTORY + " where albumid =?", new String[] {albumId});
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				return count > 0;
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
		return false;
	}

	//删除专辑上次播放的故事信息
	private void deleteAlbumStory(String albumId){
		try{
			if (dbhelper.open()) {
				dbhelper.sdb.rawQuery("delete from " + DBHelper.TAB_STORY + " where albumid=?", new String[] {albumId});
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
	}


	//更新数据
	private void updateAlbum(ListenerAlbum listenerAlbum){
		try {
			if (dbhelper.open()) {
				ContentValues values=new ContentValues();
				values.put("storyid", listenerAlbum.getPlaystoryid());
				values.put("uptime", Long.parseLong(listenerAlbum.getUptime()));
				values.put("upload", 0);
				values.put("current", listenerAlbum.getPlaytimes());
				dbhelper.sdb.update(DBHelper.TAB_HISTORY, values, "albumid=?", new String[]{listenerAlbum.getAlbumid()});
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
	}

	
	//上传之后修改数据库的数据
	public void uploadRecord(String albumId){
		try {
			if (dbhelper.open()) {
				ContentValues values=new ContentValues();
				values.put("upload", 1);
				dbhelper.sdb.update(DBHelper.TAB_HISTORY, values, "albumid=?", new String[]{albumId});
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
	}
	

	private void addHistoryAlbum(String uid,String albumId,AlbumInfo albumInfo,String storyId,int current,long uploadTime) {
		try {
			if (dbhelper.open()) {
				dbhelper.sdb.execSQL("insert into " + DBHelper.TAB_HISTORY + "(uid,albumid,storyid,uptime,current,title,intro,star_level,cover,fav,listennum,favnum,commentnum,upload)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] {uid,albumId,storyId,uploadTime,current,albumInfo.getTitle(),albumInfo.getIntro(),albumInfo.getStar_level(),
						albumInfo.getCover(),albumInfo.getFav(),albumInfo.getListennum(),albumInfo.getFavnum(),albumInfo.getCommentnum(),0});
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
	}
	
	public void addHistoryStory(String ablumId,Story story){

		try {
			if (story != null && dbhelper.open()) {
					dbhelper.sdb.execSQL("insert into " + DBHelper.TAB_STORY + "(storyId,albumid,title,intro,times,file_size,mediapath,cover,playcover)values(?,?,?,?,?,?,?,?,?)",
							new Object[] {story.getStoryId(),ablumId,story.getTitle(),story.getIntro(),story.getTimes(),story.getFile_size(),story.getMediapath(),story.getCover(),story.getPlaycover()});
			}
		} catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			dbhelper.close();
		}
	}

	public List<ListenerAlbum> getHistoryAlbums(){
		List<ListenerAlbum> albums = new ArrayList<>();
		Cursor cursor = null;
		try{
			if (dbhelper.open()) {
				cursor = dbhelper.sdb.rawQuery("select * from "+DBHelper.TAB_HISTORY+ " where upload=? order by uptime desc",new String[]{"0"});
				if(cursor != null){
					if(cursor.moveToFirst()){
						do{
							albums.add(parseAlbum(cursor));
						}while(cursor.moveToNext());
					}
				}
			}
		}catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			dbhelper.close();
		}
		return albums;
	}
	
	public List<ListenerAlbum> getUnloginHistoryAlbums(int beginPosition,int len){
		List<ListenerAlbum> albums = new ArrayList<>();
		Cursor cursor = null;
		try{
			if (dbhelper.open()) {
				String sql ="select * from "+DBHelper.TAB_HISTORY+ " order by uptime desc limit "+len+" offset "+beginPosition;
				cursor = dbhelper.sdb.rawQuery(sql,new String[]{});
				if(cursor != null){
					if(cursor.moveToFirst()){
						do{
							ListenerAlbum albumInfo = parseAlbum(cursor);
							albums.add(albumInfo);
						}while(cursor.moveToNext());
					}
				}
			}
		}catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			dbhelper.close();
		}
		return albums;
	}
	
	private ListenerAlbum getHistoryAlbumById(String ablumId){
		Cursor cursor = null;
		ListenerAlbum albumInfo = null;
		try{

			String sql ="select * from "+DBHelper.TAB_HISTORY+ " where albumid=?";
			cursor = dbhelper.sdb.rawQuery(sql,new String[]{ablumId});
			if(cursor != null){
				if(cursor.moveToFirst()){
					albumInfo = parseAlbum(cursor);
				}
			}
		}catch (Exception e) {
			DebugUtils.exception(e);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			dbhelper.close();
		}
		return albumInfo;
	}


	private Story getHistoryStoriesByAlbumId(String ablumId,String storyId){

		Story story =null;
		String ablumIdArg = "";
		String storyIdArg = "";
		if (ablumId != null) {
			ablumIdArg = ablumId;
		}
		if (storyId != null) {
			storyIdArg = storyId;
		}

		Cursor cursor = null;
		cursor = dbhelper.sdb.rawQuery("select * from "+DBHelper.TAB_STORY+ " where albumid=? and storyId=?",new String[]{ablumIdArg,storyIdArg});
		if(cursor != null){
			if(cursor.moveToFirst()){
				do{
					story =parseStory(cursor);
				}while(cursor.moveToNext());
			}
		}
		return story;
	}
	
	private ListenerAlbum parseAlbum(Cursor cursor) {
		ListenerAlbum album = new ListenerAlbum();
		album.setUid(cursor.getString(cursor.getColumnIndex("uid")));
		String ablumId = cursor.getString(cursor.getColumnIndex("albumid"));
		album.setAlbumid(ablumId);
		album.setPlaystoryid(cursor.getString(cursor.getColumnIndex("storyid")));
		album.setUptime(cursor.getLong(cursor.getColumnIndex("uptime"))+"");
		album.setPlaytimes(cursor.getInt(cursor.getColumnIndex("current")));
		AlbumInfo albuminfo = new AlbumInfo();
		albuminfo.setAlbumid(ablumId);
		albuminfo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		albuminfo.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
		albuminfo.setStar_level(cursor.getString(cursor.getColumnIndex("star_level")));
		albuminfo.setCover(cursor.getString(cursor.getColumnIndex("cover")));
		albuminfo.setFav(cursor.getInt(cursor.getColumnIndex("fav")));
		albuminfo.setListennum(cursor.getInt(cursor.getColumnIndex("listennum")));
		albuminfo.setFavnum(cursor.getInt(cursor.getColumnIndex("favnum")));
		albuminfo.setCommentnum(cursor.getInt(cursor.getColumnIndex("commentnum")));
		albuminfo.setStoryinfo(getHistoryStoriesByAlbumId(ablumId,album.getPlaystoryid()));
		album.setAlbuminfo(albuminfo);
		return album;
	}
	private Story parseStory(Cursor cursor) {
		Story story = new Story();
		story.setStoryId(cursor.getString(cursor.getColumnIndex("storyId")));
		story.setAlbum_id(cursor.getString(cursor.getColumnIndex("albumid")));
		story.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		story.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
		story.setTimes(cursor.getString(cursor.getColumnIndex("times")));
		story.setFile_size(cursor.getString(cursor.getColumnIndex("file_size")));
		story.setMediapath(cursor.getString(cursor.getColumnIndex("mediapath")));
		story.setCover(cursor.getString(cursor.getColumnIndex("cover")));
		story.setPlaycover(cursor.getString(cursor.getColumnIndex("playcover")));

		return story;
	}
	
	
}
