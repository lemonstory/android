package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo extends DataSupport implements Parcelable,IRecyclerItem {

	protected String id;
	protected String favid;
	private String albumid;
	private String title;
	private String intro;
	private String star_level;
	private String cover;
	private int fav;
    private String s_cover;
    private String add_time;
    private String update_time;
    private String listennum;
    private int favnum;
    private int commentnum;
    protected List<Story> storylist = new ArrayList<>();
	protected Story storyinfo;
	protected String recommenddesc;
	protected String age_str;

	@Override
	public int getSpanSize() {
		return 2;
	}
	@Override
	public int getItemType() {
		return Index.ALBUM_TYPE;
	}

	public AlbumInfo() {
	}
	


	public AlbumInfo(Parcel in) {
		id = in.readString();
		albumid = in.readString();
		title = in.readString();
		intro = in.readString();
		star_level = in.readString();
		cover = in.readString();
		fav = in.readInt();
		s_cover = in.readString();
		add_time = in.readString();
		update_time=in.readString();
		listennum = in.readString();
		favnum = in.readInt();
		commentnum = in.readInt();
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

	public String getStar_level() {
		return star_level;
	}

	public void setStar_level(String star_level) {
		this.star_level = star_level;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getId() {
		if (id == null) {
			id = albumid;
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlbumid() {
		if (albumid == null) {
			albumid = id;
		}
		return albumid;
	}

	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}


	public int getFav() {
		return fav;
	}

	public void setFav(int fav) {
		this.fav = fav;
	}
	
	


	public String getFavid() {
		return favid;
	}

	public void setFavid(String favid) {
		this.favid = favid;
	}

	public String getS_cover() {
		return s_cover;
	}

	public void setS_cover(String s_cover) {
		this.s_cover = s_cover;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getListennum() {
		return listennum;
	}

	public void setListennum(String listennum) {
		this.listennum = listennum;
	}
	
	public List<Story> getStorylist() {
		return storylist;
	}

	public void setStorylist(List<Story> storylist) {
		this.storylist = storylist;
	}

	
	public int getFavnum() {
		return favnum;
	}

	public void setFavnum(int favnum) {
		this.favnum = favnum;
	}

	public int getCommentnum() {
		return commentnum;
	}

	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}


	@Override
	public int describeContents() {

		return 0;
	}

	public Story getStoryinfo() {
		return storyinfo;
	}

	public void setStoryinfo(Story storyinfo) {
		this.storyinfo = storyinfo;
	}

	public String getAge_str() {
		return age_str;
	}

	public void setAge_str(String age_str) {
		this.age_str = age_str;
	}

	public String getRecommenddesc() {
		return recommenddesc;
	}

	public void setRecommenddesc(String recommenddesc) {
		this.recommenddesc = recommenddesc;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(id);
		out.writeString(albumid);
		out.writeString(title);
		out.writeString(intro);
		out.writeString(star_level);
		out.writeString(cover);
		out.writeInt(fav);
		out.writeString(s_cover);
		out.writeString(add_time);
		out.writeString(update_time);
		out.writeString(listennum);
		out.writeInt(favnum);
		out.writeInt(commentnum);
		out.writeString(age_str);

	}

	public static final Parcelable.Creator<AlbumInfo> CREATOR = new Creator<AlbumInfo>() {

		@Override
		public AlbumInfo createFromParcel(Parcel source) {

			return new AlbumInfo(source);
		}

		@Override
		public AlbumInfo[] newArray(int size) {

			return new AlbumInfo[size];
		}

	};

	public static Parcelable.Creator<AlbumInfo> getCreator() {
		return CREATOR;
	}

	@Override
	public String toString() {
		return "AlbumInfo{" +
				"id='" + id + '\'' +
				", favid='" + favid + '\'' +
				", albumid='" + albumid + '\'' +
				", title='" + title + '\'' +
				", intro='" + intro + '\'' +
				", star_level='" + star_level + '\'' +
				", cover='" + cover + '\'' +
				", fav=" + fav +
				", s_cover='" + s_cover + '\'' +
				", add_time='" + add_time + '\'' +
				", update_time='" + update_time + '\'' +
				", listennum='" + listennum + '\'' +
				", favnum=" + favnum +
				", commentnum=" + commentnum +
				", storylist=" + storylist +
				", storyinfo=" + storyinfo +
				", recommenddesc='" + recommenddesc + '\'' +
				", age_str='" + age_str + '\'' +
				'}';
	}
}
