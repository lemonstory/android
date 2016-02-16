package com.xiaoningmeng;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenu.xlistview.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.adapter.ViewThreadAdapter;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.Post;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.KeyboardFragment;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.UiUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewThreadActivity extends BaseFragmentActivity implements XListView.IXListViewListener,KeyboardFragment.OnFragmentInteractionListener {

    private Context mContext;
    private ViewGroup loadingView;
    private XListView mListView;
    public  ViewThreadAdapter mAdapter;
    public KeyboardFragment keyBoardfragment;
    private ForumThread forumThread = new ForumThread();
    private List<Post> mPosts = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    private int fid; //论坛ID
    private int tid; //帖子ID
    private String formHash;//表单验证使用
    private int page; //当前页码
    private int maxPage;//最大页码
    private int perPage;
    public int repPid;
    public int repPost;
    public String noticeTrimStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_thread);
        initView();
//        this.setTitleName("帖子");
        mContext = this;
        fid = getIntent().getIntExtra("fid",1);
        tid = getIntent().getIntExtra("tid", 1);
        formHash = getIntent().getStringExtra("formHash");
        page = getIntent().getIntExtra("page", 1);

        mAdapter = new ViewThreadAdapter(this,forumThread,mPosts);
        mListView.setAdapter(mAdapter);
        requestPostsData(tid, page);
    }

    public void initView() {

        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        loadingView = (ViewGroup) findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(this);
        this.page = 1;
        this.maxPage = 1;
        mListView.setFootViewNoMore(true);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        setKeyBoardFragment(); //设置用户键盘

        //处理键盘的发送按钮事件
        getSupportFragmentManager().executePendingTransactions();
        this.keyBoardfragment = (KeyboardFragment) getSupportFragmentManager().findFragmentByTag("keyboardFragment");
        if(null != this.keyBoardfragment) {

            this.keyBoardfragment.setOnKeyBoardBarViewListener(new KeyboardFragment.KeyBoardBarViewListener() {

                @Override
                public void onSwitchImgClicked(Button view) {

                }

                @Override
                public void OnSendBtnClick(String msg) {

                    sendForumThreadReplyData(fid, tid,formHash,msg);
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
                        if(isKeyboardVisible) {
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


    private void setKeyBoardFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_keyboard, KeyboardFragment.newInstance(),"keyboardFragment")
                .commit();
    }

    @Override
    public void onRefresh() {

        //TODO:会出现白屏的现象
        this.mPosts.clear();
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

        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setPullLoadEnable(true);
        if(this.page < this.maxPage) {
            mListView.setFootViewNoMore(false);
        } else {
            mListView.setFootViewNoMore(true);
        }
    }

    public void setForumThread(ForumThread forumThread) {

        if (forumThread != null) {
            this.forumThread = forumThread;
            this.mAdapter.setForumThread(forumThread);
        }
    }

    public void setPosts(List<Post> mPosts) {

        if (mPosts != null && mPosts.size() > 0) {

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
        } else {
            if (mListView != null) {
                //showEmptyTip(loadingView, "没有圈子 o(>ω<)o", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
            }
        }
    }


    private void hideEmptyTip() {

    }

    public void reRequestLoading() {

        if (loadingView != null) {

            loadingView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);
            ((TextView) loadingView.getChildAt(0)).setText("正在努力加载中");
            loadingView.getChildAt(1).setVisibility(View.VISIBLE);
        }
    }

    private void requestPostsData(int tid, int page) {

        if (loadingView != null) {

            loadingView.setVisibility(View.INVISIBLE);
            loadingView.setClickable(false);
        }
        LHttpRequest.getInstance().getViewThread(this,
                new LHttpHandler<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {

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

                            }

                            if (variablesObject.has("postlist")) {

                                List<Post> posts = gson.fromJson(variablesObject.getString("postlist"), new TypeToken<List<Post>>() {
                                }.getType());
                                setPosts(posts);
                            }

                            if(variablesObject.has("formhash")) {

                                formHash = variablesObject.getString("formhash");
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
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

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

    private void sendForumThreadReplyData(int fid, int tid,String formHash, String message) {

        if (loadingView != null) {

            loadingView.setVisibility(View.INVISIBLE);
            loadingView.setClickable(false);
        }
        LHttpRequest.getInstance().sendReply(this,
                new LHttpHandler<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {
                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject messageObject = new JSONObject(jsonObject.getString("Message"));
                            String messageVal = messageObject.getString("messageval");
                            String messageStr = messageObject.getString("messagestr");

                            if(messageVal.equals(Constant.FORUM_POST_REPLY_SUCCEED)) {

                                //关闭键盘
                                keyBoardfragment.resetKeyboard();

                                //TODO:待优化
                                //可见的最后一条与实际全量的最后一条距离相近时,页面向下滚动
                                int lastVisiblePosition =  mListView.getLastVisiblePosition();
                                int maxPosition = mPosts.size();
                                if(maxPosition - lastVisiblePosition < 4) {
                                    onLoadMore();
                                    //页面滚动到最后
                                    mListView.smoothScrollToPosition(mPosts.size() - 1);
                                }else {
                                    onLoadMore();
                                    Toast.makeText(mContext, "发送成功",Toast.LENGTH_SHORT).show();
                                }

                            }else {

                                Toast.makeText(mContext,messageStr,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

//                        if (loadingView != null) {
//                            loadingView.setVisibility(View.VISIBLE);
//                            ((TextView) loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
//                            loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
//                            loadingView.setClickable(true);
//                            loadingView.setOnClickListener(new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    reRequestLoading();
//                                    ForumDisplayActivity.this.requestForumThreadsData(ForumDisplayActivity.this.fid, ForumDisplayActivity.this.page);
//                                }
//                            });
//                        }
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                }, fid, tid,formHash, message,repPid,repPost,noticeTrimStr);
    }





}
