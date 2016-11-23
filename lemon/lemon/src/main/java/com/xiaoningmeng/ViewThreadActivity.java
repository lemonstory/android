package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.adapter.ViewThreadAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Attachment;
import com.xiaoningmeng.bean.ForumLoginVar;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.Post;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.ForumLoginEvent;
import com.xiaoningmeng.fragment.KeyboardFragment;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.ImageUtils;
import com.xiaoningmeng.view.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class ViewThreadActivity extends BaseActivity implements XListView.IXListViewListener,KeyboardFragment.OnFragmentInteractionListener {

    private Context mContext;
    private ViewGroup loadingView;
    private XListView mListView;
    public  ViewThreadAdapter mAdapter;
    private TextView title;
    private ImageView headRightImg;
    public KeyboardFragment keyBoardfragment;
    private ForumThread forumThread = new ForumThread();
    private ArrayList<Post> mPosts = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    private int tid; //主题ID
    private int pid; //帖子ID
    private String formHash;//表单验证使用
    private String hash;
    private int page; //当前页码
    private int maxPage;//最大页码
    private int perPage;
    public int repPid;
    public int repPost;
    public String noticeTrimStr;
    private ArrayList<File> addedImageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_view_thread);
        mContext = this;
        tid = getTidIdWithIntent();
        pid = getIntent().getIntExtra("pid",1);
        page = getIntent().getIntExtra("page", 1);
        initView();
        mAdapter = new ViewThreadAdapter(this,forumThread,mPosts);
        mListView.setAdapter(mAdapter);
        mListView.autoRefresh();
        EventBus.getDefault().register(this);
    }

    public void initView() {

        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mListView.setXListViewListener(this);
        mListView.setPullLoadEnable(false);
        loadingView = (ViewGroup) findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        loadingView.setVisibility(View.GONE);
        title = (TextView)findViewById(R.id.tv_head_title);
        title.setText("帖子");
        setShareIconVisible();
        this.page = 1;
        this.maxPage = 1;
        setKeyBoardfragment(); //设置用户键盘
        addedImageFiles = new ArrayList<>();
    }

    protected void onResume() {

        super.onResume();
        //处理键盘的发送按钮事件
        this.keyBoardfragment = (KeyboardFragment) getSupportFragmentManager().findFragmentByTag("keyboardFragment");
        if(null != this.keyBoardfragment) {
            this.keyBoardfragment.setmEditEmojiconHint("回复楼主:");
            this.keyBoardfragment.setOnKeyBoardBarViewListener(new KeyboardFragment.KeyBoardBarViewListener() {

                @Override
                public void onSwitchImgClick(ImageView view) {

                }
                @Override
                public void OnSendBtnClick(String msg) {

                    if(UserAuth.auditUser(mContext, "登录后,才能批量故事喔.")) {

                        setLoadingTip("正在发布");
                        sendForumThreadReplyData(tid, formHash, hash, msg);
                    }
                }
                @Override
                public void OnAddedImgInContainer(ArrayList<File> imagesfiles) {

                    addedImageFiles = imagesfiles;
                }
                @Override
                public void onAddImageControlClick(View view) {

                }
            });
        }

        //页面滑动时键盘收起
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:

//                        boolean isKeyboardVisible = UiUtils.isKeyboardShown(mListView.getRootView());
//                        if (isKeyboardVisible) {
                            keyBoardfragment.resetKeyboard();
//                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        MobclickAgent.onEvent(this, "event_show_forum_viewthread");
    }

    private int getTidIdWithIntent() {

        int tidWithIntent = 0;
        int tidWithExtar = 0;
        int tidWithData = 0;

        Intent intent = this.getIntent();
        Uri data = intent.getData();

        tidWithExtar = intent.getIntExtra("tid", 0);
        if(null != data) {
            tidWithData = Integer.parseInt(data.getQueryParameter("tid"));
        }

        if (tidWithExtar != 0) {
            tidWithIntent = tidWithExtar;
        } else if(tidWithData != 0) {
            tidWithIntent = tidWithData;
        }
        return tidWithIntent;
    }

    private void  setShareIconVisible() {

        headRightImg = (ImageView) findViewById(R.id.img_head_right);
        headRightImg.setImageResource(R.drawable.btn_player_share_normal);
        headRightImg.setClickable(false);
        headRightImg.setAlpha((float) 0.5);
        headRightImg.setVisibility(View.VISIBLE);
    }

    public void setShareIconClickable() {

        headRightImg.setClickable(true);
        headRightImg.setAlpha((float)1.0);
        headRightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//TODO:h5未完成
//                String shareIconUrl = "";
//                ArrayList<String> threadImgAttachmentsPath = getImgAttachmentsPathWithThread(forumThread, mPosts);
//                if (threadImgAttachmentsPath.size() > 0) {
//                    shareIconUrl = threadImgAttachmentsPath.get(0);
//                } else {
//                    shareIconUrl = Constant.SHARE_THREAD_DEFAULT_IMAGE_URL;
//                }
//
//                String url = String.format(Constant.VIEW_THREAD_URL,forumThread.getTid());
//
//                ShareBean shareBean = new ShareBean(forumThread.getSubject(), shareIconUrl, url);
                String app_name = ViewThreadActivity.this.getString(R.string.app_name);
                String app_desc = ViewThreadActivity.this.getString(R.string.app_desc);
                ShareBean shareBean = new ShareBean(app_name, app_desc,Constant.SHARE_OFFCAIL_ICON_URL,null,Constant.SHARE_OFFCAIL_URL);
                mController = new ShareDialog().show(ViewThreadActivity.this, shareBean);
            }
        });
    }

    public ArrayList<String> getImgAttachmentsPathWithThread(ForumThread forumThread,ArrayList<Post> posts) {

        ArrayList<String> imgAttachmentsPath = new ArrayList<String>();
        if (forumThread.getAttachment() != null && forumThread.getAttachment().equals("2")) {
            Post post = posts.get(0);
            if (post.getFirst().equals("1") && post.getAuthorid().equals(forumThread.getAuthorid())) {
                if (post.getImagelist() != null && post.getImagelist().size() > 0) {
                    int imgListCount = post.getImagelist().size();
                    for (int i = 0; i < imgListCount; i++) {
                        String aid = post.getImagelist().get(i);
                        Attachment attachment = post.getAttachments().get(aid);
                        String url = attachment.getUrl();
                        String path = attachment.getAttachment();
                        String absolutePath = ConstantURL.BBS_URL + url + path;
                        imgAttachmentsPath.add(absolutePath);
                    }
                }
            }
        }
        return imgAttachmentsPath;
    }

    private void setKeyBoardfragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyboard, KeyboardFragment.newInstance(),"keyboardFragment")
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private ShareAction mController;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult( requestCode, resultCode, data);
        this.keyBoardfragment.addedImageFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRefresh() {

        this.page = 1;
        requestPostsData(tid, page);
        mListView.setPullLoadEnable(false);
    }

    @Override
    public void onLoadMore() {

        if(this.page < this.maxPage) {
            int size = mPosts.size();
            if (size >= perPage) {
                this.page++;
                requestPostsData(tid, page);
            } else {
                this.page = 1;
                requestPostsData(tid, page);
            }
        }else {

            //获取到的数据需要做合并
            requestPostsData(tid, page);
        }
    }

    private void onLoad() {

        if(this.page < this.maxPage) {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        } else {
            mListView.setFootViewNoMore(true);
        }
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    public void setForumThread(ForumThread forumThread) {

        if (forumThread != null) {
            this.forumThread = forumThread;
            this.mAdapter.setForumThread(forumThread);
        }
    }

    public void setPosts(List<Post> mPosts) {

        if (mPosts != null && mPosts.size() > 0) {

            if(this.page == 1) {

                this.mPosts.clear();
            }

            for (Post post : mPosts) {

                int freq = Collections.frequency(this.mPosts, post);
                if(freq < 1) {
                    this.mPosts.add(post);
                }
            }
            if (mListView != null) {
                hideEmptyTip();
                //TODO:
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    private void hideEmptyTip() {

    }

    public void reRequestLoading() {

        mListView.autoRefresh();
    }

    private void requestPostsData(int tid, int page) {

        LHttpRequest.getInstance().getViewThread(this,
                new JsonCallback<String>() {

                    @Override
                    public void onGetDataSuccess(String data) {

                        loadingView.setVisibility(View.GONE);
                        Double replaysCount = 0.0;
                        Double ppp = 0.0;
                        ForumThread forumThread = null;
                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));

                            Gson gson = new Gson();

                            if (variablesObject.has("thread")) {

                                forumThread = gson.fromJson(variablesObject.getString("thread"), new TypeToken<ForumThread>() {
                                }.getType());
                                setForumThread(forumThread);
                                setShareIconClickable();
                            }

                            if (variablesObject.has("postlist")) {

                                List<Post> posts = gson.fromJson(variablesObject.getString("postlist"), new TypeToken<List<Post>>() {
                                }.getType());
                                setPosts(posts);
                            }

                            if(variablesObject.has("formhash")) {

                                formHash = variablesObject.getString("formhash");
                            }

                            if(variablesObject.has("hash")) {

                                hash = variablesObject.getString("hash");
                            }

                            if (forumThread.getReplies() != null && variablesObject.has("ppp")) {

                                ppp = Double.parseDouble(variablesObject.getString("ppp"));
                                perPage = Integer.valueOf(variablesObject.getString("ppp"));

                                //TODO:接口里面有两个:replies，allreplies先用前者
                                if (forumThread.getReplies() != null) {
                                    //+1是因为还有主贴
                                    replaysCount = Double.parseDouble(forumThread.getReplies()) + 1;
                                    Double dMaxPage = Math.ceil(replaysCount / ppp);
                                    ViewThreadActivity.this.maxPage = dMaxPage.intValue();
                                }
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {

                        if (loadingView != null) {
                            loadingView.setVisibility(View.VISIBLE);
                            ((TextView) loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
                            loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
                            loadingView.setClickable(true);
                            loadingView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    reRequestLoading();
                                    ViewThreadActivity.this.requestPostsData(ViewThreadActivity.this.tid, ViewThreadActivity.this.page);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                }, tid, page);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void sendForumThreadReplyData(final int tid,final String formHash,String hash,final String message) {

        final ArrayList<String>aids = new ArrayList<>();//dz上传图片后返回的图片标示符
        startLoading();
        //构建一个请求队列,先逐个上传图片,在上传文字
        if(addedImageFiles.size() > 0) {
            for (File file: addedImageFiles) {
                String compressFilePath =  ImageUtils.compress(file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, 80);
                final File fileData = new File(compressFilePath);
                //上传图片
                LHttpRequest.getInstance().forumUpload(this,new JsonCallback<String>() {
                    @Override
                    public void onGetDataSuccess(String data) {

                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                            if(variablesObject.has("ret")) {

                                JSONObject retObject = new JSONObject(variablesObject.getString("ret"));
                                String aId = retObject.getString("aId");
                                String isImage = retObject.getString("image");

                                if(isImage != null && isImage.equals("1") && aId != null && !aId.equals("")) {

                                    aids.add(aId);

                                    if(aids.size() == addedImageFiles.size()) {

                                        sendReplyRequest(tid,formHash,message,aids);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            stopLoading();
                            Toast.makeText(mContext,"系统错误",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {
                        stopLoading();
                        Toast.makeText(mContext,"请检查网络设置",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {

                        super.onFinish();
                        stopLoading();
                    }

                },fileData,hash);
            }
        }else {
            sendReplyRequest(tid,formHash,message,null);
        }
    }

    private void sendReplyRequest(int tid,String formHash, String message,ArrayList<String>aids) {

        LHttpRequest.getInstance().sendReply(this,
                new JsonCallback<String>() {

                    @Override
                    public void onGetDataSuccess(String data) {

                        stopLoading();
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject messageObject = new JSONObject(jsonObject.getString("Message"));
                            String messageVal = messageObject.getString("messageval");
                            String messageStr = messageObject.getString("messagestr");

                            if (messageVal.equals(Constant.FORUM_POST_REPLY_SUCCEED)) {

                                //关闭键盘
                                keyBoardfragment.resetKeyboard();

                                //TODO:待优化
                                //可见的最后一条与实际全量的最后一条距离相近时,页面向下滚动
                                int lastVisiblePosition = mListView.getLastVisiblePosition();
                                int maxPosition = mPosts.size();
                                if (maxPosition - lastVisiblePosition < 4) {
                                    onLoadMore();
                                    //页面滚动到最后
                                    mListView.smoothScrollToPosition(mPosts.size() - 1);
                                } else {
                                    onLoadMore();
                                    Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(mContext, messageStr, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            Toast.makeText(mContext,"系统错误",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {
                        stopLoading();
                        Toast.makeText(mContext,"请检查网络设置",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                        stopLoading();
                    }
                },tid,formHash,message,aids,repPid,repPost,noticeTrimStr);
    }

    public void onEventMainThread(ForumLoginEvent event) {

        ForumLoginVar forumLoginVar = event.forumLoginVar;
        formHash = forumLoginVar.getFormhash();
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
