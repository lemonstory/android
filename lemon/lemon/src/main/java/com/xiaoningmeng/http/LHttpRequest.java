package com.xiaoningmeng.http;

import android.content.Context;

import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.Address;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AlbumRecommend;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.bean.HomeInfo;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.bean.Mine;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.bean.Rank;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.bean.SearchData;
import com.xiaoningmeng.bean.StoryList;
import com.xiaoningmeng.bean.TagDetail;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LHttpRequest {

	private static LHttpRequest mInstance;

	public static LHttpRequest getInstance() {

		if (mInstance == null) {
			mInstance = new LHttpRequest();
		}
		return mInstance;
	}



	// QQ联合
	public void QQLoginRequest(Context context, String accessToken,
							   String openId, JsonCallback<UserInfo> handler) {
		get(ConstantURL.QQ_LOGIN).tag(context)
				.addParams("accessToken", accessToken)
				.addParams("openId", openId).
				addParams("visituid", MyApplication.getInstance().getUid())
				.build().execute(handler);

	}

	// QQ联合注册
	public void QQLoginRegRequest(Context context, String accessToken,
								  String openId, String nickName, JsonCallback<UserInfo> handler) {
		get(ConstantURL.QQ_LOGIN_REG).tag(context)
				.addParams("accessToken", accessToken)
				.addParams("openId", openId)
				.addParams("nickName", nickName)
				.addParams("visituid", MyApplication.getInstance().getUid())
				.build().execute(handler);
	}

	// weChat联合
	public void WechatLoginRequest(Context context, String accessToken,
								   String openId, JsonCallback<UserInfo> handler) {
		get(ConstantURL.WECHAT_LOGIN).tag(context)
		.addParams("accessToken", accessToken)
		.addParams("openId", openId)
		.addParams("visituid", MyApplication.getInstance().getUid())
		.build().execute(handler);
	}

	//UC登录同步
	public void UCSyncLoginRequest(Context context, String url,
								   JsonCallback<String> handler) {
		
		get(url).tag(context).build().execute(handler);
	}


	// weChat联合注册
	public void WechatLoginRegRequest(Context context, String accessToken,
									  String openId, String nickName, JsonCallback<UserInfo> handler) {
		get(ConstantURL.WECHAT_REG).tag(context)
		.addParams("accessToken", accessToken)
		.addParams("openId", openId)
		.addParams("nickName", nickName)
		.addParams("visituid", MyApplication.getInstance().getUid())
		.build().execute(handler);
	}
	//暂时登陆
	public void loginRequest(Context context, JsonCallback<UserInfo> handler) {
		String url = ConstantURL.URL + "/userinfo/defaultlogin.php";
		get(url).tag(context)
			.addParams("username", "18701515649")
			.addParams("password", "11223344")
			.build().execute(handler);
	}

	// 退出
	public void logoutRequest(Context context, JsonCallback<String> handler) {
		get(ConstantURL.LOGOUT).tag(context).build().execute(handler);
	}

	/**
	 * get方法
	 * @param url
	 * @return
     */
	public static OkHttpRequestBuilder<GetBuilder> get(String url){

		return OkHttpUtils.get().addHeader("User-Agent", AppInfo.getInstance().getUAStr())
				.addParams("visituid", MyApplication.getInstance().getUid())
				.addHeader("FROM", "mobile").url(url);
	}

	/**
	 * post方法
	 * @param url
	 * @return
     */
	public static OkHttpRequestBuilder<PostFormBuilder> post(String url){
		return OkHttpUtils.post().url(url).addHeader("User-Agent", AppInfo.getInstance().getUAStr())
				.addHeader("FROM", "mobile").url(url);
	}



	//首页列表
	public void indexRequest(Context context, JsonCallback<Index> handler) {
		get(ConstantURL.INDEX).tag(context)
				.build().execute(handler);
	}

	//增加收藏
	public void addFavAlbumRequest(Context context, String albumId,
								   JsonCallback<String> handler) {
		get(ConstantURL.INDEX).tag(context)
		.addParams("albumid", albumId)
				.build().execute(handler);
	}

	//收藏列表
	public void getFavAlbumListRequest(Context context, String direction,
									   String startId, int len, JsonCallback<List<AlbumInfo>> handler) {
		if (startId == null)
			startId = "0";
		get(ConstantURL.GET_FAV_LIST).tag(context)
				.addParams("direction", direction)
				.addParams("startfavid", startId)
			.addParams("len", Integer.toString(len))
		.build().execute(handler);

	}

	//删除收藏
	public void delFavAlbumRequest(Context context, String albumId,
								   JsonCallback<String> handler) {
		get(ConstantURL.DEL_FAV_ALBUM).tag(context)
		.addParams("albumid", albumId)
				.build().execute(handler);
	}

	//更多
	public void getMoreList(Context context, int type, int p, int len, int isGetTag, String currentfirsttagid, JsonCallback<MoreAblum> handler) {
		String url = null;
		if (MoreActivity.HOT_MORE == type) {
			url = ConstantURL.HOT_RECOMMEND_LIST;
		} else if (MoreActivity.NEW_MORE == type) {
			url = ConstantURL.NEW_ONLINE_LIST;
		} else if (MoreActivity.SAME_MORE == type) {
			url = ConstantURL.SAME_AGE_LIST;
		}
		if (url != null) {
			get(url).tag(context)
					.addParams("currentfirsttagid", currentfirsttagid)
			.addParams("p", Integer.toString(p))
			.addParams("len", Integer.toString(len))
			.addParams("isgettag", Integer.toString(isGetTag))
					.build().execute(handler);
		}

	}

	//排行榜
	public void rankListenerUserListReq(Context context, int len,
										JsonCallback<Rank> handler) {
		get(ConstantURL.RANK_LISTENER_LIST).tag(context)
		.addParams("len", Integer.toString(len))
				.build().execute(handler);
	}

	//专辑详情
	public void albumInfoReq(Context context, String albumId, int storysPage,  String uid,
							 JsonCallback<Album> handler) {
		get(ConstantURL.ALBUM_INFO).tag(context)
		.addParams("album_id", albumId)
		.addParams("storys_page", Integer.toString(storysPage))
		.addParams("uid", uid)
				.build().execute(handler);
	}

	//专辑评论
	public void albumCommentReq(Context context,String albumId,String direction,String startCommentId,int len,String uid,
							 JsonCallback<Comment> handler) {
		get(ConstantURL.ALBUM_COMMENT).tag(context)
		.addParams("album_id", albumId)
		.addParams("direction", direction)
		.addParams("start_comment_id", startCommentId)
		.addParams("len", Integer.toString(len))
		.addParams("uid", uid)
				.build().execute(handler);
	}

	//专辑故事
	public void albumStorysReq(Context context,int albumId,int pageSize,int page,String uid,
								JsonCallback<StoryList> handler) {
		get(ConstantURL.ALBUM_STORYS).tag(context)
				.addParams("album_id", Integer.toString(albumId))
				.addParams("page", Integer.toString(page))
				.addParams("len", Integer.toString(pageSize))
				.addParams("uid", uid)
				.build().execute(handler);
	}


	//故事信息
	public void storyInfoReq(Context context, String storyId, String uid,
							 JsonCallback<String> handler) {
		get(ConstantURL.ALBUM_INFO).tag(context)
		.addParams("storyid", storyId)
		.addParams("uid", uid)
				.build().execute(handler);
	}

	//全部分类
	public void categoryReq(Context context,String uid,
								JsonCallback<Category> handler) {
		get(ConstantURL.CATEGORY).tag(context)
				.addParams("uid", uid)
				.build().execute(handler);
	}

	//增加记录
	public void addRecordReq(Context context, String recrod, JsonCallback<String> handler) {
		post(ConstantURL.ADD_RECORD).tag(context)
		.addParams("content", recrod)
		.build().execute(handler);
	}

	//增加下载记录
	public void addDownRecordReq(Context context, String recrod, JsonCallback<String> handler) {
		post(ConstantURL.SYNCDOWN).tag(context)
		.addParams("syncdata", recrod)
		.build().execute(handler);
	}

	//搜索
	public void searchReq(Context context, String searchContent, int len, int pager, String searchtype,
						  JsonCallback<SearchData> handler) {

		get(ConstantURL.ALBUM_STORY_SEARCH).tag(context)
		.addParams("searchcontent", searchContent)
		.addParams("len", Integer.toString(len))
		.addParams("page", Integer.toString(pager))
		.addParams("searchtype", searchtype)
				.build().execute(handler);
	}

	//评论
	public void addCommentReq(Context context, String albumId, String content,
							  int starLevel, JsonCallback<String> handler) {
		get(ConstantURL.COMMENT_ADD).tag(context)
		.addParams("albumid", albumId)
		.addParams("content", content)
		.addParams("star_level", Integer.toString(starLevel))
		.build().execute(handler);
	}

	//我的故事
	public void myStoryReq(Context context, String direction, String startId, int len, JsonCallback<Mine> handler) {
		get(ConstantURL.MY_STORY).tag(context)
				.addParams("isgetcount",direction == Constant.FRIST ? "1" :"0")
		.addParams("direction", direction)
		.addParams("startalbumid", startId)
		.addParams("len", Integer.toString(len))
				.build().execute(handler);
	}

	//反馈
	public void feedbackReq(Context context, String qq, String tel, String content, JsonCallback<String> handler) {

		GetBuilder builder = get(ConstantURL.FEEDBACK).tag(context)
		.addParams("content", content);
		if (qq != null && !qq.equals("")) {
			builder.addParams("qq", qq);
		}
		if (tel != null && !tel.equals("")) {
			builder.addParams("tel", tel);
		}
		builder.build().execute(handler);
	}

	//上传头像
	public void setAvatarReq(Context context, String avatarFilePath, SaveCallback callback) {
		//String path = ImageUtils.compress(avatarFilePath);
		UploadFile.getInstance().asyncUpload(avatarFilePath, callback);
	}

	//个人页
	public void getHomeInfoReq(Context ctx, String uid, String direction, String startAlbumId, int len, JsonCallback<HomeInfo> handler) {
		get(ConstantURL.GET_HOME_INFO).tag(ctx)
				.addParams("isgetuserinfo", direction == Constant.FRIST ?"1":"0")
		.addParams("direction", direction)
		.addParams("startalbumid", startAlbumId)
		.addParams("uid", uid)
		.addParams("len", Integer.toString(len))
				.build().execute(handler);
	}

	//个人信息
	public void getUserInfoReq(Context ctx, JsonCallback<UserInfo> handler) {

		get(ConstantURL.GET_USER_INFO).tag(ctx).build().execute(handler);
	}

	//设置个人信息
	public void setUserInfoReq(Context ctx, String nickName, String gender, String birthday,
							   String province, String city, String area, String phonenumber,
							   String defaultAddressId, String avatartime, JsonCallback<String> handler) {
		GetBuilder builder = get(ConstantURL.SET_USER_INFO).tag(ctx);
		if (nickName != null)
			builder.addParams("nickname", nickName);
		if (gender != null)
			builder.addParams("gender", gender);
		if (birthday != null)
			builder.addParams("birthday", birthday);
		if (province != null)
			builder.addParams("province", province);
		if (city != null)
			builder.addParams("city", city);
		if (area != null)
			builder.addParams("area", area);
		if (phonenumber != null)
			builder.addParams("phonenumber", phonenumber);
		if (defaultAddressId != null)
			builder.addParams("defaultaddressid", defaultAddressId);
		if (avatartime != null)
			builder.addParams("avatartime", avatartime);
		builder.build().execute(handler);
	}


	//增加地址
	public void addAddressReq(Context ctx, Address address, JsonCallback<Address> handler) {
		get(ConstantURL.ADD_ADDRESS).tag(ctx)
		.addParams("name", address.getName())
		.addParams("phonenumber", address.getPhonenumber())
		.addParams("province", address.getProvince())
		.addParams("city", address.getCity() == null ? " " : address.getCity())
		.addParams("area", address.getArea() == null ? " " : address.getArea())
		.addParams("address", address.getAddress())
		.addParams("ecode", address.getEcode())
				.build().execute(handler);
	}

	//更新地址
	public void setAddressReq(Context ctx, Address address, JsonCallback<Address> handler) {
		get(ConstantURL.SET_ADDRESS).tag(ctx)
		.addParams("addressid", address.getId())
		.addParams("name", address.getName())
		.addParams("phonenumber", address.getPhonenumber())
		.addParams("province", address.getProvince())
		.addParams("city", address.getCity() == null ? " " : address.getCity())
		.addParams("area", address.getArea() == null ? " " : address.getArea())
		.addParams("address", address.getAddress())
		.addParams("ecode", address.getEcode())
				.build().execute(handler);
	}

	//删除地址
	public void delAddressReq(Context ctx, String addressId, JsonCallback<Address> handler) {
		get(ConstantURL.DEL_ADDRESS).tag(ctx)
		.addParams("addressid", addressId)
				.build().execute(handler);
	}

	//得到地址列表
	public void getAddressListReq(Context ctx, JsonCallback<List<Address>> handler) {
		get(ConstantURL.GET_ADDRESS_LIST).tag(ctx)
				.build().execute(handler);
	}

	//热门搜索
	public void getHotSearchReq(Context ctx, int len, JsonCallback<List<SearchContent>> handler) {
		get(ConstantURL.HOt_SEARCH).tag(ctx)
		.addParams("len", Integer.toString(len))
				.build().execute(handler);
	}


	/**
	 * 1.3版本接口
	 */
	//得到标签专辑列表
	public void getTagAblumListReq(Context ctx, String tagId, int isGetTag, String direction, String relationId, String specialtag, int len, JsonCallback<TagDetail> handler) {
		GetBuilder builder = get(ConstantURL.GET_TAG_ALBUM_LIST).tag(ctx)
		.addParams("currenttagid", tagId)
		.addParams("isgettag", Integer.toString(isGetTag))
		.addParams("direction", direction)
		.addParams("startrelationid", relationId)
		.addParams("len", Integer.toString(len));
		if (specialtag != null) {
			builder.addParams(specialtag, "1");
		}
		builder.build().execute(handler);
	}

	//论坛首页
	public void getForumIndex(Context context, JsonCallback<String> handler) {
		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "4")
		.addParams("module", "forumindex")
		.build().execute(handler);
	}

	//论坛帖子列表
	public void getForumThreads(Context context, JsonCallback<String> handler, int fid, int page) {

		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "4")
		.addParams("module", "forumdisplay")
		.addParams("fid", Integer.toString(fid))
		.addParams("page", Integer.toString(page))
				.build().execute(handler);
	}

	//论坛帖子详情
	public void getViewThread(Context context, JsonCallback<String> handler, int tid, int page) {

		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "4")
		.addParams("module", "viewthread")
		.addParams("tid", Integer.toString(tid))
		.addParams("page", Integer.toString(page))
				.build().execute(handler);
	}

	//论坛帖子评论
	public void sendReply(Context context, JsonCallback<String> handler,int tid, String formHash, String message,ArrayList<String> aids, int reppid, int reppost, String noticetrimstr) {
		String url = ConstantURL.FORUM_INDEX + "?version=4&module=sendreply&replysubmit=yes&tid=" + tid + "&visituid=" + MyApplication.getInstance().getUid();
		GetBuilder builder = get(url).tag(context)
		//回帖
		.addParams("formhash", formHash)
		.addParams("replysubmit", "yes")
		.addParams("subject", "")
		.addParams("message", message)
		.addParams("usesig", "2")
		.addParams("visituid", MyApplication.getInstance().getUid())

		//回复某楼
		.addParams("reppid", Integer.toString(reppid))
		.addParams("reppost", Integer.toString(reppost))
		.addParams("noticetrimstr", noticetrimstr);

		if(aids != null && aids.size() > 0) {
			for (String aId : aids) {

				String attach = String.format("attachnew[%s][description]",aId);
				builder.addParams(attach,"");
			}
		}
		builder.build().execute(handler);
	}

	//论坛帖子上传图片
	public void forumUpload(Context context, JsonCallback<String> handler, File file, String formhash) {
		String url = ConstantURL.FORUM_INDEX + "?version=3&module=forumupload&operation=upload&uid=" + MyApplication.getInstance().getUid();
		get(url).tag(context)
		.addParams("uid", MyApplication.getInstance().getUid())
		.addParams("hash", formhash)
				.build().execute(handler);

	}

	//论坛发布帖子
	public void newThread(Context context, JsonCallback<String> handler, int fid, String subject,String message,ArrayList<String> aids,String forumHash) {
		String url = ConstantURL.FORUM_INDEX + "?version=4&module=newthread&fid=" + fid +"&formhash=" + forumHash + "&visituid=" + MyApplication.getInstance().getUid();
		PostFormBuilder builder = post(url).tag(context)
		.addParams("fid", Integer.toString(fid))
		.addParams("allownoticeauthor", "1")
		.addParams("usesig", "1")
		.addParams("wysiwyg","1")
		.addParams("subject",subject)
		.addParams("message",message)
		.addParams("topicsubmit","yes");

		if(aids != null && aids.size() > 0) {
			for (String aId : aids) {

				String attach = String.format("attachnew[%s][description]",aId);
				builder.addParams(attach,"");
			}
		}
		builder.build().execute(handler);
	}

	//我的帖子帖子列表
	public void getMyThread(Context context, JsonCallback<String> handler, String uid, int page) {

		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "4")
		.addParams("module", "mythread")
		.addParams("page", Integer.toString(page))
		.addParams("uid", uid)
				.build().execute(handler);
	}

	//我的消息->评论
	//论坛帖子详情
	public void getMyNoteList(Context context, JsonCallback<String> handler,int page) {

		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "3")
		.addParams("module", "mynotelist")
		.addParams("page", Integer.toString(page))
				.build().execute(handler);
	}

	//社区->个人信息
	public void getUserProfile(Context context, JsonCallback<String> handler,String uid) {

		get(ConstantURL.FORUM_INDEX).tag(context)
		.addParams("version", "4")
		.addParams("module", "profile")
		.addParams("uid", uid)
				.build().execute(handler);
	}

	//商城
	public void getShopItems(Context context, JsonCallback<String> handler,int page) {

		get(ConstantURL.SHOP_INDEX).tag(context)
		.addParams("page", Integer.toString(page))
				.build().execute(handler);
	}

	//得到标签专辑列表
	public void getAlbumRecommendReq(Context ctx, String url, String minAge, String maxAge, int page, int len, JsonCallback<AlbumRecommend> handler) {
		GetBuilder builder = get(url).tag(ctx)
				.addParams("min_age", minAge)
				.addParams("max_age", maxAge)
				.addParams("page", Integer.toString(page))
				.addParams("len", Integer.toString(len));
		builder.build().execute(handler);
	}
}
