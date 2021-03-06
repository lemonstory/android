package com.xiaoningmeng.http;

import android.content.Context;

import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.Address;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AlbumRecommend;
import com.xiaoningmeng.bean.AuthorAlbums;
import com.xiaoningmeng.bean.AuthorList;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.CommentList;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.bean.Mine;
import com.xiaoningmeng.bean.PerasonalInfo;
import com.xiaoningmeng.bean.Rank;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.bean.SearchData;
import com.xiaoningmeng.bean.StoryList;
import com.xiaoningmeng.bean.TagAblumList;
import com.xiaoningmeng.bean.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * cookie: http://www.jianshu.com/p/ba5eab576cf7
 * retrofit2: http://blog.nex3z.com/2015/12/28/retrofit%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95%E7%AE%80%E4%BB%8B/
 */

public class LHttpRequest {

    private static LHttpRequest mInstance;
    public static Retrofit mRetrofit;

    static {
        Gson gson = new GsonBuilder().create();
        mRetrofit = new Retrofit.Builder().baseUrl(ConstantURL.URL)
                .client(MyApplication.getInstance().initOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static LHttpRequest getInstance() {

        if (mInstance == null) {
            mInstance = new LHttpRequest();
        }
        return mInstance;
    }

    // QQ联合
    public interface QQLoginRequest {

        @GET(ConstantURL.QQ_LOGIN)
        Call<JsonResponse<UserInfo>> getResult(@Query("accessToken") String accessToken,
                                               @Query("openId") String openId);
    }

    // QQ联合注册
    public interface QQLoginRegRequest {

        @GET(ConstantURL.QQ_LOGIN_REG)
        Call<JsonResponse<UserInfo>> getResult(@Query("accessToken") String accessToken,
                                               @Query("openId") String openId,
                                               @Query("nickName") String nickName);
    }

    // weChat联合
    public void WechatLoginRequest(Context context, String accessToken, String openId, JsonCallback<UserInfo> handler) {
        //        get(WECHAT_LOGIN).tag(context)
        //                .addParams("accessToken", accessToken)
        //                .addParams("openId", openId)
        //                .addParams("visituid", MyApplication.getInstance().getUid())
        //                .build().execute(handler);
    }

    // weChat联合注册
    public void WechatLoginRegRequest(Context context, String accessToken, String openId, String nickName, JsonCallback<UserInfo> handler) {
        //        get(WECHAT_REG).tag(context)
        //                .addParams("accessToken", accessToken)
        //                .addParams("openId", openId)
        //                .addParams("nickName", nickName)
        //                .addParams("visituid", MyApplication.getInstance().getUid())
        //                .build().execute(handler);
    }

    // 退出登录
    public interface LogoutRequest {

        @GET(ConstantURL.LOGOUT)
        Call<JsonResponse<String>> getResult();
    }

    //首页列表
    public interface IndexRequest {

        @GET(ConstantURL.INDEX)
        Call<JsonResponse<Index>> getResult();
    }

    //增加收藏
    public interface AddFavAlbumRequest {

        @GET(ConstantURL.ADD_FAV_ALBUM)
        Call<JsonResponse<String>> getResult(@Query("albumid") String albumId);
    }

    //删除收藏
    public interface DelFavAlbumRequest {

        @GET(ConstantURL.DEL_FAV_ALBUM)
        Call<JsonResponse<String>> getResult(@Query("albumid") String albumId);
    }

    //收藏列表
    public interface GetFavAlbumListRequest {

        @GET(ConstantURL.GET_FAV_LIST)
        Call<JsonResponse<List<AlbumInfo>>> getResult(@Query("startfavid") String startId,
                                                      @Query("direction") String direction,
                                                      @Query("len") int len);
    }

    //排行榜
    public interface RankListenerUserListRequest {

        @GET(ConstantURL.RANK_LISTENER_LIST)
        Call<JsonResponse<Rank>> getResult(@Query("len") int len);
    }

    //专辑详情
    public interface AlbumInfoRequest {

        @GET(ConstantURL.ALBUM_INFO)
        Call<JsonResponse<Album>> getResult(@Query("album_id") String albumId,
                                            @Query("storysPage") int storysPage);
    }

    //专辑评论
    public interface AlbumCommentRequest {

        @GET(ConstantURL.ALBUM_COMMENT)
        Call<JsonResponse<CommentList>> getResult(@Query("album_id") String albumId,
                                                  @Query("direction") String direction,
                                                  @Query("start_comment_id") String startCommentId,
                                                  @Query("len") int len);
    }

    //专辑故事
    public interface AlbumStorysRequest {

        @GET(ConstantURL.ALBUM_STORYS)
        Call<JsonResponse<StoryList>> getResult(@Query("album_id") int albumId,
                                                @Query("page") int page,
                                                @Query("len") int pageSize);
    }

    //全部分类
    public interface GetCategoryRequest {

        @GET(ConstantURL.CATEGORY)
        Call<JsonResponse<Category>> getResult();
    }

    //增加记录
    public interface AddRecordRequest {

        @FormUrlEncoded
        @POST(ConstantURL.ADD_RECORD)
        Call<JsonResponse<String>> getResult(@Field("content") String record);
    }

    //增加下载记录
    public interface AddDownRecordRequest {

        @FormUrlEncoded
        @POST(ConstantURL.SYNCDOWN)
        Call<JsonResponse<String>> getResult(@Field("syncdata") String record);
    }

    //热门搜索
    public interface GetHotSearchRequest {

        @GET(ConstantURL.HOT_SEARCH)
        Call<JsonResponse<List<SearchContent>>> getResult(@Query("len") int len);
    }

    //搜索
    public interface SearchRequest {

        @GET(ConstantURL.ALBUM_STORY_SEARCH)
        Call<JsonResponse<SearchData>> getResult(@Query("searchcontent") String searchContent,
                                                 @Query("searchtype") String searchtype,
                                                 @Query("page") int page,
                                                 @Query("len") int len);
    }

    //评论
    public interface AddCommentRequest {

        @GET(ConstantURL.COMMENT_ADD)
        Call<JsonResponse<String>> getResult(@Query("albumid") String albumId,
                                             @Query("content") String content,
                                             @Query("star_level") int starLevel);
    }

    //我的故事
    public interface MyStoryRequest {

        @GET(ConstantURL.MY_STORY)
        Call<JsonResponse<Mine>> getResult(@Query("direction") String direction,
                                           @Query("startalbumid") String startAlbumId,
                                           @Query("len") int len);
    }

    //反馈
    public interface FeedbackRequest {

        @GET(ConstantURL.FEEDBACK)
        Call<JsonResponse<String>> getResult(@Query("qq") String qq,
                                             @Query("tel") String tel,
                                             @Query("content") String content);
    }

    //上传头像
    public void setAvatarReq(Context context, String avatarFilePath, SaveCallback callback) {

        UploadFile.getInstance().asyncUpload(avatarFilePath, callback);
    }

    //个人页
    public interface GetPerasonalInfoRequest {

        @GET(ConstantURL.GET_HOME_INFO)
        Call<JsonResponse<PerasonalInfo>> getResult(@Query("uid") String uid,
                                                    @Query("direction") String direction,
                                                    @Query("isgetuserinfo") int isGetUserInfo,
                                                    @Query("startalbumid") String startAlbumId,
                                                    @Query("len") int len);
    }

    //获取个人信息
    public interface GetUserInfoRequest {

        @GET(ConstantURL.GET_USER_INFO)
        Call<JsonResponse<UserInfo>> getResult();
    }

    //设置个人信息
    public interface SetUserInfoRequest {

        @GET(ConstantURL.SET_USER_INFO)
        Call<JsonResponse<String>> getResult(@Query("uid") String uid,
                                             @Query("gender") String gender,
                                             @Query("birthday") String birthday,
                                             @Query("province") String province,
                                             @Query("city") String city,
                                             @Query("area") String area,
                                             @Query("phonenumber") String phonenumber,
                                             @Query("defaultaddressid") String defaultaddressid,
                                             @Query("avatartime") String avatartime);
    }

    //增加地址
    public interface AddAddressRequest {

        @GET(ConstantURL.ADD_ADDRESS)
        Call<JsonResponse<Address>> getResult(@Query("name") String name,
                                              @Query("phonenumber") String phoneNumber,
                                              @Query("province") String province,
                                              @Query("city") String city,
                                              @Query("area") String area,
                                              @Query("address") String address,
                                              @Query("ecode") String ecode);
    }

    //更新地址
    public interface SetAddressRequest {

        @GET(ConstantURL.SET_ADDRESS)
        Call<JsonResponse<Address>> getResult(@Query("addressid") String addressId,
                                              @Query("name") String name,
                                              @Query("phonenumber") String phoneNumber,
                                              @Query("province") String province,
                                              @Query("city") String city,
                                              @Query("area") String area,
                                              @Query("address") String address,
                                              @Query("ecode") String ecode);
    }

    //删除地址
    public interface DelAddressRequest {

        @GET(ConstantURL.DEL_ADDRESS)
        Call<JsonResponse<Address>> getResult(@Query("addressid") String addressId);
    }

    //得到地址列表
    public interface GetAddressListReqRequest {

        @GET(ConstantURL.GET_ADDRESS_LIST)
        Call<JsonResponse<List<Address>>> getResult();
    }

    //获取标签专辑列表
    public interface GetTagAblumListRequest {

        @GET(ConstantURL.GET_TAG_ALBUM_LIST)
        Call<JsonResponse<TagAblumList>> getResult(@Query("currenttagid") String tagId,
                                                   @Query("isgettag") int isGetTag,
                                                   @Query("direction") String direction,
                                                   @Query("startrelationid") String relationId,
                                                   @Query("specialtag") int specialtag,
                                                   @Query("len") int len);
    }

    //商城
    public void getShopItems(Context context, JsonCallback<String> handler, int page) {

        //        get(SHOP_INDEX).tag(context)
        //                .addParams("page", Integer.toString(page))
        //                .build().execute(handler);
    }

    //获取推荐专辑列表
    public interface GetAlbumRecommendRequest {

        @GET
        Call<JsonResponse<AlbumRecommend>> getResult(@Url String url,
                                                     @Query("min_age") int minAge,
                                                     @Query("max_age") int max_age,
                                                     @Query("page") int page,
                                                     @Query("len") int len);
    }

    //获取作者专辑列表
    public interface GetAuthorAlbumsRequest {

        @GET(ConstantURL.AUTHOR_ALBUM_LIST)
        Call<JsonResponse<AuthorAlbums>> getResult(@Query("author_id") int authorId,
                                                   @Query("start_album_id") int startAlbumId,
                                                   @Query("len") int len);
    }

    //获取作者列表
    public interface GetAuthorsRequest {

        @GET(ConstantURL.AUTHORS)
        Call<JsonResponse<AuthorList>> getResult(@Query("page") int page,
                                                 @Query("len") int len);
    }
}
