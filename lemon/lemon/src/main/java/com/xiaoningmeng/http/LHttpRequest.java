package com.xiaoningmeng.http;

import android.content.Context;

import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.Address;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.Discover;
import com.xiaoningmeng.bean.HomeInfo;
import com.xiaoningmeng.bean.Mine;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.bean.Rank;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.bean.SearchData;
import com.xiaoningmeng.bean.TagDetail;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LHttpRequest {

	private static LHttpRequest mInstance;

	public static LHttpRequest getInstance() {

		if (mInstance == null) {
			mInstance = new LHttpRequest();
		}
		return mInstance;
	}

	//http://stackoverflow.com/questions/25091913/volley-string-request-error-while-passing-string-with-null-value-as-param
	private Map<String, String> checkParams(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			if (pairs.getValue() == null) {
				map.put(pairs.getKey(), "");
			}
		}
		return map;
	}

	// QQ联合
	public void QQLoginRequest(Context context, String accessToken,
							   String openId, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("accessToken", accessToken);
		params.put("openId", openId);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.QQ_LOGIN, params,
				handler);
	}

	// QQ联合注册
	public void QQLoginRegRequest(Context context, String accessToken,
								  String openId, String nickName, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("accessToken", accessToken);
		params.put("openId", openId);
		params.put("nickName", nickName);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.QQ_LOGIN_REG, params,
				handler);
	}

	//UC登录同步
	public void UCSyncLoginRequest(Context context, String url,
								   LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		LClient.getInstance().get(context, url, params,
				handler);
	}

	// weChat联合
	public void WechatLoginRequest(Context context, String accessToken,
								   String openId, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("accessToken", accessToken);
		params.put("openId", openId);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.WECHAT_LOGIN, params,
				handler);
	}

	// weChat联合注册
	public void WechatLoginRegRequest(Context context, String accessToken,
									  String openId, String nickName, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("accessToken", accessToken);
		params.put("openId", openId);
		params.put("nickName", nickName);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.WECHAT_REG, params,
				handler);
	}

	//暂时登陆
	public void loginRequest(Context context, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", "18701515649");
		params.put("password", "11223344");
		String url = ConstantURL.URL + "/userinfo/defaultlogin.php";
		LClient.getInstance().post(context, url, params, handler);
	}

	// 退出
	public void logoutRequest(Context context, LHttpHandler<String> handler) {
		LClient.getInstance().get(context, ConstantURL.LOGOUT, null, handler);
	}

	//首页列表
	public void indexRequest(Context context, LHttpHandler<Discover> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.INDEX, params, handler);
	}

	//增加收藏
	public void addFavAlbumRequest(Context context, String albumId,
								   LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.ADD_FAV_ALBUM, params,
				handler);
	}

	//收藏列表
	public void getFavAlbumListRequest(Context context, String direction,
									   String startId, int len, LHttpHandler<List<AlbumInfo>> handler) {
		HashMap<String, String> params = new HashMap<>();
		if (direction != null)
			params.put("direction", direction);
		if (startId == null)
			startId = "0";
		params.put("startfavid", startId);
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.GET_FAV_LIST, params,
				handler);
	}

	//删除收藏
	public void delFavAlbumRequest(Context context, String albumId,
								   LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.DEL_FAV_ALBUM, params,
				handler);
	}

	//更多
	public void getMoreList(Context context, int type, int p, int len, int isGetTag, String currentfirsttagid, LHttpHandler<MoreAblum> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("currentfirsttagid", currentfirsttagid);
		params.put("p", p + "");
		params.put("len", len + "");
		params.put("isgettag", isGetTag + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		String url = null;
		if (MoreActivity.HOT_MORE == type) {
			url = ConstantURL.HOT_RECOMMEND_LIST;
		} else if (MoreActivity.NEW_MORE == type) {
			url = ConstantURL.NEW_ONLINE_LIST;
		} else if (MoreActivity.SAME_MORE == type) {
			url = ConstantURL.SAME_AGE_LIST;
		}
		if (url != null) {
			LClient.getInstance().get(context, url, params, handler);
		}
	}

	/*//增加收听
	public void addListenerStoryRequest(Context context, String albumId,
			String storyId, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("storyid", storyId);
		params.put("visituid", MyApplication.getInstance().getUid());
		LClient.getInstance().get(context, ConstantURL.ADD_LISTEN_STORY,
				params, handler);
	}
	//收听列表
	public void getListenerListRequest(Context context, String direction,
			String startId, int len, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("direction", direction);
		if (startId == null) {
			startId = "0";
		}
		params.put("startid", startId);
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		LClient.getInstance().get(context, ConstantURL.GET_LISTEN_LIST, params,
				handler);
	}
	//取消收听
	public void delListenerStoryRequest(Context context, String albumId,
			String storyId, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("storyid", storyId);
		params.put("visituid", MyApplication.getInstance().getUid());
		LClient.getInstance().get(context, ConstantURL.DEL_LISTEN_STORY,
				params, handler);
	}*/
	//排行榜
	public void rankListenerUserListReq(Context context, int len,
										LHttpHandler<Rank> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.RANK_LISTENER_LIST, params, handler);
	}

	//专辑详情
	public void albumInfoReq(Context context, int len, String albumId, String uid,
							 LHttpHandler<Album> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("iscommentpage", "0");
		params.put("direction", Constant.FRIST);
		params.put("startid", Constant.FRIST_ID);
		params.put("len", len + "");
		params.put("uid", uid);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.ALBUM_INFO, params,
				handler);
	}

	//专辑详情
	public void storyListReq(Context context, int len, String direction, String startId, String albumId, String uid,
							 LHttpHandler<Album> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("iscommentpage", "1");
		params.put("direction", direction);
		params.put("startid", startId);
		params.put("len", len + "");
		params.put("uid", uid);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.ALBUM_INFO, params,
				handler);
	}

	//故事信息
	public void storyInfoReq(Context context, String storyId, String uid,
							 LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("storyid", storyId);
		params.put("uid", uid);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.ALBUM_INFO, params,
				handler);
	}

	//增加记录
	public void addRecordReq(Context context, String recrod, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("content", recrod);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().post(context, ConstantURL.ADD_RECORD, params,
				handler);
	}

	//增加下载记录
	public void addDownRecordReq(Context context, String recrod, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("syncdata", recrod);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().post(context, ConstantURL.SYNCDOWN, params, handler);
	}

	//搜索
	public void searchReq(Context context, String searchContent, int len, int pager, String searchtype,
						  LHttpHandler<SearchData> handler) {

		HashMap<String, String> params = new HashMap<>();
		params.put("searchcontent", searchContent);
		params.put("len", len + "");
		params.put("page", pager + "");
		params.put("searchtype", searchtype);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.ALBUM_STORY_SEARCH, params,
				handler);
	}

	//评论
	public void addCommentReq(Context context, String albumId, String content,
							  int starLevel, LHttpHandler<String> handler) {

		HashMap<String, String> params = new HashMap<>();
		params.put("albumid", albumId);
		params.put("content", content);
		params.put("star_level", starLevel + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.COMMENT_ADD, params,
				handler);
	}

	//我的故事
	public void myStoryReq(Context context, String direction, String startId, int len, LHttpHandler<Mine> handler) {
		HashMap<String, String> params = new HashMap<>();
		if (direction == Constant.FRIST) {
			params.put("isgetcount", "1");
		} else {
			params.put("isgetcount", "0");
		}
		params.put("direction", direction);
		params.put("startalbumid", startId);
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.MY_STORY, params, handler);
	}

	//反馈
	public void feedbackReq(Context context, String qq, String tel, String content, LHttpHandler<String> handler) {

		HashMap<String, String> params = new HashMap<>();
		params.put("content", content);
		if (qq != null && !qq.equals("")) {
			params.put("qq", qq);
		}
		if (tel != null && !tel.equals("")) {
			params.put("tel", tel);
		}
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.FEEDBACK, params, handler);
	}

	//上传头像
	public void setAvatarReq(Context context, String avatarFilePath, SaveCallback callback) {
		//String path = ImageUtils.compress(avatarFilePath);
		UploadFile.getInstance().asyncUpload(avatarFilePath, callback);
	}

	//个人页
	public void getHomeInfoReq(Context ctx, String uid, String direction, String startAlbumId, int len, LHttpHandler<HomeInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		if (direction == Constant.FRIST) {
			params.put("isgetuserinfo", "1");
		} else {
			params.put("isgetuserinfo", "0");
		}
		params.put("direction", direction);
		params.put("startalbumid", startAlbumId);
		params.put("uid", uid);
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.GET_HOME_INFO, params, handler);
	}

	//个人信息
	public void getUserInfoReq(Context ctx, LHttpHandler<UserInfo> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.GET_USER_INFO, params, handler);
	}

	//设置个人信息
	public void setUserInfoReq(Context ctx, String nickName, String gender, String birthday,
							   String province, String city, String area, String phonenumber,
							   String defaultAddressId, String avatartime, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		if (nickName != null)
			params.put("nickname", nickName);
		if (gender != null)
			params.put("gender", gender);
		if (birthday != null)
			params.put("birthday", birthday);
		if (province != null)
			params.put("province", province);
		if (city != null)
			params.put("city", city);
		if (area != null)
			params.put("area", area);
		if (phonenumber != null)
			params.put("phonenumber", phonenumber);
		if (defaultAddressId != null)
			params.put("defaultaddressid", defaultAddressId);
		if (avatartime != null)
			params.put("avatartime", avatartime);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.SET_USER_INFO, params, handler);
	}


	//增加地址
	public void addAddressReq(Context ctx, Address address, LHttpHandler<Address> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("name", address.getName());
		params.put("phonenumber", address.getPhonenumber());
		params.put("province", address.getProvince());
		params.put("city", address.getCity() == null ? " " : address.getCity());
		params.put("area", address.getArea() == null ? " " : address.getArea());
		params.put("address", address.getAddress());
		params.put("ecode", address.getEcode());
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.ADD_ADDRESS, params, handler);
	}

	//更新地址
	public void setAddressReq(Context ctx, Address address, LHttpHandler<Address> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("addressid", address.getId());
		params.put("name", address.getName());
		params.put("phonenumber", address.getPhonenumber());
		params.put("province", address.getProvince());
		params.put("city", address.getCity() == null ? " " : address.getCity());
		params.put("area", address.getArea() == null ? " " : address.getArea());
		params.put("address", address.getAddress());
		params.put("ecode", address.getEcode());
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.SET_ADDRESS, params, handler);
	}

	//删除地址
	public void delAddressReq(Context ctx, String addressId, LHttpHandler<Address> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("addressid", addressId);
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.DEL_ADDRESS, params, handler);
	}

	//得到地址列表
	public void getAddressListReq(Context ctx, LHttpHandler<List<Address>> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.GET_ADDRESS_LIST, params, handler);
	}

	//得到地址列表
	public void getHotSearchReq(Context ctx, int len, LHttpHandler<List<SearchContent>> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("len", len + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.HOt_SEARCH, params, handler);
	}


	/**
	 * 1.3版本接口
	 */
	//得到标签专辑列表
	public void getTagAblumListReq(Context ctx, String tagId, int isGetTag, String direction, String relationId, String specialtag, int len, LHttpHandler<TagDetail> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("currenttagid", tagId);
		params.put("isgettag", isGetTag + "");
		params.put("direction", direction);
		params.put("startrelationid", relationId);
		params.put("len", len + "");
		if (specialtag != null) {
			params.put(specialtag, 1 + "");
		}
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(ctx, ConstantURL.GET_TAG_ALBUM_LIST, params, handler);
	}

	//论坛首页
	public void getForumIndex(Context context, LHttpHandler<String> handler) {
		HashMap<String, String> params = new HashMap<>();
		params.put("version", "4");
		params.put("module", "forumindex");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.FORUM_INDEX, params,
				handler);
	}

	//论坛帖子列表
	public void getForumThreads(Context context, LHttpHandler<String> handler, int fid, int page) {

		HashMap<String, String> params = new HashMap<>();
		params.put("version", "4");
		params.put("module", "forumdisplay");
		params.put("fid", fid + "");
		params.put("page", page + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.FORUM_INDEX, params,
				handler);
	}

	//论坛帖子详情
	public void getViewThread(Context context, LHttpHandler<String> handler, int tid, int page) {

		HashMap<String, String> params = new HashMap<>();
		params.put("version", "4");
		params.put("module", "viewthread");
		params.put("tid", tid + "");
		params.put("page", page + "");
		params.put("visituid", MyApplication.getInstance().getUid());
		params = (HashMap<String, String>) checkParams(params);
		LClient.getInstance().get(context, ConstantURL.FORUM_INDEX, params,
				handler);
	}

	//论坛帖子评论
	public void sendReply(Context context, LHttpHandler<String> handler, int fid, int tid, String formHash, String message,ArrayList<String> aids, int reppid, int reppost, String noticetrimstr) {

		HashMap<String, String> params = new HashMap<>();
		//回帖
		params.put("formhash", formHash);
		params.put("replysubmit", "yes");
		params.put("subject", "");
		params.put("message", message);
		params.put("usesig", "2");
		params.put("visituid", MyApplication.getInstance().getUid());

		//回复某楼
		params.put("reppid", reppid + "");
		params.put("reppost", reppost + "");
		params.put("noticetrimstr", noticetrimstr);
		params = (HashMap<String, String>) checkParams(params);

		if(aids != null && aids.size() > 0) {
			for (String aId : aids) {

				String attach = String.format("attachnew[%s][description]",aId);
				params.put(attach,"");
			}
		}

		String url = ConstantURL.FORUM_INDEX + "?version=4&module=sendreply&replysubmit=yes&fid=" + fid + "&tid=" + tid + "&visituid=" + MyApplication.getInstance().getUid();
		LClient.getInstance().post(context, url, params, handler);
	}

	//论坛帖子上传图片
	public void forumUpload(Context context, LHttpHandler<String> handler, File file, String formhash) {

		HashMap<String, String> params = new HashMap<>();
		params.put("uid", MyApplication.getInstance().getUid());
		params.put("hash", formhash);
		params.put("operation","upload");
		params = (HashMap<String, String>) checkParams(params);
		String url = ConstantURL.FORUM_INDEX + "?version=3&module=forumupload&uid=" + MyApplication.getInstance().getUid();
		LClient.getInstance().post(context,url,"Filedata",file, params, handler);
	}

	//论坛发布帖子
	public void newThread(Context context, LHttpHandler<String> handler, int fid, String subject,String message,ArrayList<String> aids,String forumHash) {

		HashMap<String, String> params = new HashMap<>();
		params.put("fid", fid+"");
		params.put("allownoticeauthor", "1");
		params.put("usesig", "1");
		params.put("wysiwyg","1");
		params.put("subject",subject);
		params.put("message",message);
		params.put("topicsubmit","yes");

		if(aids != null && aids.size() > 0) {
			for (String aId : aids) {

				String attach = String.format("attachnew[%s][description]",aId);
				params.put(attach,"");
			}
		}
		params = (HashMap<String, String>) checkParams(params);
		String url = ConstantURL.FORUM_INDEX + "?version=4&module=newthread&fid=" + fid +"&formhash=" + forumHash + "&visituid=" + MyApplication.getInstance().getUid();
		LClient.getInstance().post(context, url, params, handler);
	}

}
