package com.xiaoningmeng.bean;

import java.util.List;

public class Discover {
	private List<AlbumInfo> hotrecommend;
	private List<FocusPic> focuspic;
	private List<AlbumInfo> newalbum;
	private List<AlbumInfo> samgeage;
	private List<AlbumInfo> privatecustom;
	private List<Tag> firsttag;

	public List<AlbumInfo> getHotrecommend() {
		return hotrecommend;
	}

	public void setHotrecommend(List<AlbumInfo> hotrecommend) {
		this.hotrecommend = hotrecommend;
	}

	public List<AlbumInfo> getNewalbum() {
		return newalbum;
	}

	public void setNewalbum(List<AlbumInfo> newalbum) {
		this.newalbum = newalbum;
	}

	public List<AlbumInfo> getSamgeage() {
		return samgeage;
	}

	public void setSamgeage(List<AlbumInfo> samgeage) {
		this.samgeage = samgeage;
	}

	public List<FocusPic> getFocuspic() {
		return focuspic;
	}

	public void setFocuspic(List<FocusPic> focuspic) {
		this.focuspic = focuspic;
	}

	public List<AlbumInfo> getPrivatecustom() {
		return privatecustom;
	}

	public void setPrivatecustom(List<AlbumInfo> privatecustom) {
		this.privatecustom = privatecustom;
	}

	public List<Tag> getFirsttag() {
		return firsttag;
	}

	public void setFirsttag(List<Tag> firsttag) {
		this.firsttag = firsttag;
	}


	@Override
	public String toString() {

		return "hotrecommend"+(hotrecommend==null ? 0 :hotrecommend.size())
		+"focuspic"+(focuspic==null ?0 :focuspic.size())
				+"newalbum"+(newalbum==null ?0 :newalbum.size())
		+"samgeage"+(samgeage==null ?0 :samgeage.size())
		+"firsttag"+ (firsttag==null ?0 :firsttag.size());
	}
}
