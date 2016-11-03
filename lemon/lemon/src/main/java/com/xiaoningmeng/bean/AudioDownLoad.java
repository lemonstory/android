package com.xiaoningmeng.bean;

import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadState;
import com.xiaoningmeng.utils.AudioUtils;

import org.litepal.crud.DataSupport;

import java.io.File;

/**
 * 下载对象Model
 * */
public class AudioDownLoad extends DataSupport implements Comparable<AudioDownLoad>{

	private int id;
	private String uid;
	private String audioId;
	private String name;
	private String url;
	private String savepath;
	protected String tempPath;
	protected String realPath;
	private long startPos;
	private long endPos;// 内容长度 传-1意味未知长度
	private long curPos;// 当前已下载进度
	private long loadPos;
	private int status;
	private String type;// 下载文件的格式类型
	private long addTime;
	private int position;
	private String storyId;
	private String title;
	private String intro;
	private String times;
	private String file_size;
	private String cover;
	private String playcover;
	private String albumid;
	public boolean isCheck;
	private boolean isUpload; //是否已经上传
	

	public AudioDownLoad() {

	}

	public AudioDownLoad(Story story,int position) {

		this(0, -1, DownLoadState.DOWN_CONNECT, "mp3", System
				.currentTimeMillis(), story,position);
	}

	private AudioDownLoad(long startPos, long endPos, int status, String type,
			long addTime, Story story,int position) {
		this.url = story.getMediapath();
		String fileName = AudioUtils.generateByUrl(url);
		//String uid = MyApplication.getInstance().getUid();
		this.uid = "";
		this.audioId = fileName;
		this.name = fileName;
		this.savepath = Constant.SAVE_PATH;
		this.startPos = startPos;
		this.endPos = endPos;
		this.status = status;
		this.type = type;
		this.addTime = addTime;
		this.storyId = story.getId();
		this.albumid = story.getAlbum_id();
		this.title = story.getTitle();
		this.intro = story.getIntro();
		this.times = story.getTimes();
		this.playcover = story.getPlaycover();
		this.file_size = story.getFile_size();
		this.isUpload = false;
		this.position = position;
	}

	public String getRealPath() {
		return savepath + File.separator + name + "." + type;
	}

	public String getTempPath() {
		return savepath + File.separator + name + ".temp";
	}

	public String getType() {
		return type;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCurPos() {
		return curPos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setCurPos(long curPos) {
		this.curPos = curPos;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public long getEndPos() {
		return endPos;
	}

	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAudioId() {
		return audioId;
	}

	public void setAudioId(String audioId) {
		this.audioId = audioId;
	}

	public long getLoadPos() {
		return loadPos;
	}

	public void setLoadPos(long loadPos) {
		this.loadPos = loadPos;
	}

	public String getSavepath() {
		return savepath;
	}

	public void setSavepath(String savepath) {
		this.savepath = savepath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public String getAlbumid() {
		return albumid;
	}

	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}

	public int getProgress() {

		if (endPos != 0)
			return (int) (startPos * 100 / endPos);
		return 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Story getStory() {
		return new Story(storyId, albumid, title, intro, times + "", file_size,
				url, cover,playcover);
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	
	

	public String getPlaycover() {
		return playcover;
	}

	public void setPlaycover(String playcover) {
		this.playcover = playcover;
	}

	@Override
	public int compareTo(AudioDownLoad another) {
		if(another != null){
			if(this.position == another.getPosition()){
				return 0;
			}
			return this.position >another.getPosition() ? 1 : -1;
		}
		return -1;
	}

	
}
