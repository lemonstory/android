package com.xiaoningmeng;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.adapter.ViewThreadAdapter;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.Post;
import com.xiaoningmeng.fragment.KeyboardFragment;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewThreadActivity extends BaseFragmentActivity implements XListView.IXListViewListener,KeyboardFragment.OnFragmentInteractionListener {

    private ViewGroup loadingView;
    private XListView mListView;
    public ViewThreadAdapter mAdapter;
    private KeyboardFragment keyBoardfragment;
    private ForumThread forumThread = new ForumThread();
    private List<Post> mPosts = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    private int fid; //论坛ID
    private int tid; //帖子ID
    private int page; //当前页码
    private int maxPage;//最大页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_thread);
        initView();
//        this.setTitleName("帖子");
        fid = getIntent().getIntExtra("fid",1);
        tid = getIntent().getIntExtra("tid", 1);
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
        mListView.setFootViewNoMore(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        this.page = 1;
        this.maxPage = 1;
        mListView.setFootViewNoMore(true);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        setKeyBoardFragment(); //设置用户键盘
        Log.d("aaa", "Fragment by id:" + getSupportFragmentManager().findFragmentById(R.id.fl_keyboard));
        Log.d("aaa", "Fragment by tag:" + getSupportFragmentManager().findFragmentByTag("keyboardFragment"));
//        this.keyBoardfragment = (KeyboardFragment) this.getSupportFragmentManager().findFragmentByTag("keyboardFragment");
//
//        this.keyBoardfragment.setOnKeyBoardBarViewListener(new KeyboardFragment.KeyBoardBarViewListener() {
//
//            @Override
//            public void onSwitchImgClicked(Button view) {
//
//            }
//
//            @Override
//            public void OnSendBtnClick(String msg) {
//
//                Log.d("aaa", "ViewThreadActivity OnSendBtnClick is run");
//                sendForumThreadReplyData(fid, tid, msg);
//            }
//
//        });

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
        mListView.setFootViewNoMore(true);
    }

    @Override
    public void onLoadMore() {

        if(this.page < this.maxPage) {
            int size = mPosts.size();
            if (size > 0) {
                this.page++;
                requestPostsData(tid, page);
                ;
            } else {
                this.page = 1;
                requestPostsData(tid, page);
            }
        }
    }

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();

        if(this.page < this.maxPage) {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        }else {
            mListView.setPullLoadEnable(false);
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
            this.mPosts.addAll(mPosts);
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
                        Double tpp = 0.0;
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            Gson gson = new Gson();

                            if (jsonObject.has("thread")) {

                                ForumThread forumThread = gson.fromJson(jsonObject.getString("thread"), new TypeToken<ForumThread>() {
                                }.getType());
                                setForumThread(forumThread);

                            }

                            if (jsonObject.has("postlist")) {
                                List<Post> posts = gson.fromJson(jsonObject.getString("postlist"), new TypeToken<List<Post>>() {
                                }.getType());
                                setPosts(posts);
                            }

                            if (ViewThreadActivity.this.forumThread != null && jsonObject.has("tpp")) {

                                tpp = Double.parseDouble(jsonObject.getString("tpp"));

                                //TODO:接口里面有两个:replies，allreplies先用前者
                                if (ViewThreadActivity.this.forumThread.getReplies() != null) {
                                    replaysCount = Double.parseDouble(ViewThreadActivity.this.forumThread.getReplies());
                                    Double dMaxPage = Math.ceil(replaysCount / tpp);
                                    ViewThreadActivity.this.maxPage = dMaxPage.intValue();
                                }
                            }


                        } catch (JSONException e) {

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

    private void sendForumThreadReplyData(int fid, int tid,String message) {

        if (loadingView != null) {

            loadingView.setVisibility(View.INVISIBLE);
            loadingView.setClickable(false);
        }
        LHttpRequest.getInstance().sendReply(this,
                new LHttpHandler<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {
                        Double threadsCount = 0.0;
                        Double tpp = 0.0;
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            Log.d("aaa", data);
//                            if (jsonObject.has("forum_threadlist")) {
//                                Gson gson = new Gson();
//                                List<ForumThread> threads = gson.fromJson(jsonObject.getString("forum_threadlist"), new TypeToken<List<ForumThread>>() {
//                                }.getType());
//                                setForumsThreads(threads);
//                            }

//                            if (jsonObject.has("forum") && jsonObject.has("tpp")) {
//
//                                String forum = jsonObject.getString("forum");
//                                JSONObject forumJsonObject = new JSONObject(forum);
//                                tpp = Double.parseDouble(jsonObject.getString("tpp"));
//
//                                if (forumJsonObject.has("threadcount")) {
//                                    threadsCount = Double.parseDouble(forumJsonObject.getString("threadcount"));
//                                    Double dMaxPage = Math.ceil(threadsCount / tpp);
//                                    ForumDisplayActivity.this.maxPage = dMaxPage.intValue();
//                                }
//                            }
                        } catch (JSONException e) {

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
                }, fid, tid, message);
    }



}
