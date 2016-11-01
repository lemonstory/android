package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.adapter.MyNoteListAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.NoteList;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.KeyboardFragment;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.ImageUtils;
import com.xiaoningmeng.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyNotelistActivity extends BaseActivity implements XListView.IXListViewListener,
        KeyboardFragment.OnFragmentInteractionListener {

    private Context mContext;
    private ViewGroup loadingView;
    private XListView mListView;
    public MyNoteListAdapter mAdapter;
    private TextView title;
    private ImageView headRightImg;
    public FrameLayout keyboardFl;
    public KeyboardFragment keyBoardfragment;
    private ForumThread forumThread = new ForumThread();
    private ArrayList<NoteList> mNoteList = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    public int fid; //论坛ID
    public int tid; //帖子ID
    private String formHash;//表单验证使用
    private String hash;
    private int page; //当前页码
    private int count;//最大页码
    private int perPage;
    public int repPid;
    public int repPost;
    public String noticeTrimStr;
    private ArrayList<File> addedImageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_my_notelist);
        initView();
        mContext = this;
        page = 1;
        mAdapter = new MyNoteListAdapter(this,mNoteList);
        mListView.setAdapter(mAdapter);
        mListView.autoRefresh();
    }

    public void initView() {

        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mListView.setXListViewListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setFootViewNoMore(true);
        loadingView = (ViewGroup) findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        loadingView.setVisibility(View.GONE);
        title = (TextView)findViewById(R.id.tv_head_title);
        title.setText("我的消息");
        keyboardFl = (FrameLayout) findViewById(R.id.fl_keyboard);
        setKeyBoardfragment(); //设置用户键盘
        addedImageFiles = new ArrayList<>();
        this.page = 1;
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
                        sendForumThreadReplyData(fid, tid, formHash, hash, msg);
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

                        boolean isKeyboardVisible = UiUtils.isKeyboardShown(mListView.getRootView());
                        if (isKeyboardVisible) {

                            keyboardFl.setVisibility(View.INVISIBLE);
                            keyBoardfragment.resetKeyboard();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
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
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        this.keyBoardfragment.addedImageFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {

        this.page = 1;
        requestMyNoteListData(page);
        mListView.setPullLoadEnable(false);
    }

    @Override
    public void onLoadMore() {

        if(this.count == this.perPage) {
            this.page++;
            requestMyNoteListData(page);
        }else {
            mListView.stopLoadMore();
        }
    }

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();

        if(this.count < this.perPage) {
            mListView.setFootViewNoMore(true);
        } else {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        }
    }

    public void setNoteLists(List<NoteList> noteList) {

        if (mNoteList != null && mNoteList.size() > 0) {
            if (this.page == 1) {
                this.mNoteList.clear();
            }
        }
        this.mNoteList.addAll(noteList);
        if (mListView != null) {
            hideEmptyTip();
            mAdapter.notifyDataSetChanged();
        }
    }


    private void hideEmptyTip() {

    }

    public void reRequestLoading() {

        mListView.autoRefresh();
    }

    private void requestMyNoteListData(int page) {

        LHttpRequest.getInstance().getMyNoteList(this,
                new JsonCallback<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {

                        loadingView.setVisibility(View.GONE);
                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));

                            Gson gson = new Gson();
                            if (variablesObject.has("list")) {

                                List<NoteList> noteLists = gson.fromJson(variablesObject.getString("list"), new TypeToken<List<NoteList>>() {
                                }.getType());
                                setNoteLists(noteLists);
                            }

                            if (variablesObject.has("count")) {

                                count = Integer.parseInt(variablesObject.getString("count"));
                            }

                            if (variablesObject.has("perpage")) {

                                perPage = Integer.parseInt(variablesObject.getString("perpage"));
                            }

                            if (variablesObject.has("formhash")) {

                                formHash = variablesObject.getString("formhash");
                            }

                            if (variablesObject.has("hash")) {

                                hash = variablesObject.getString("hash");
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureResponse) {
                        if (loadingView != null) {
                            loadingView.setVisibility(View.VISIBLE);
                            ((TextView) loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
                            loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
                            loadingView.setClickable(true);
                            loadingView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    reRequestLoading();
                                    MyNotelistActivity.this.requestMyNoteListData(MyNotelistActivity.this.page);
                                }
                            });
                        }
                    }



                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                }, page);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void sendForumThreadReplyData(final int fid,final int tid,final String formHash,String hash,final String message) {

        final ArrayList<String>aids = new ArrayList<>();//dz上传图片后返回的图片标示符
        startLoading();
        //构建一个请求队列,先逐个上传图片,在上传文字
        if(addedImageFiles.size() > 0) {

            for (File file: addedImageFiles) {

                String compressFilePath =  ImageUtils.compress(file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, 80);
                final File fileData = new File(compressFilePath);

                //上传图片
                LHttpRequest.getInstance().forumUpload(this,new JsonCallback<String>(this) {
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
                            Toast.makeText(mContext, "系统错误", Toast.LENGTH_SHORT).show();
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
                        keyboardFl.setVisibility(View.INVISIBLE);
                    }

                },fileData,hash);
            }
        }else {
            sendReplyRequest(tid,formHash,message,null);
        }
    }

    private void sendReplyRequest(int tid,String formHash, String message,ArrayList<String>aids) {

        LHttpRequest.getInstance().sendReply(this,
                new JsonCallback<String>(this) {

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
                                int maxPosition = mNoteList.size();
                                if (maxPosition - lastVisiblePosition < 4) {
                                    onLoadMore();
                                    //页面滚动到最后
                                    mListView.smoothScrollToPosition(mNoteList.size() - 1);
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
                    public void onFailure( String responseString) {
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
}

