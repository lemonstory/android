package com.xiaoningmeng;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.adapter.ForumDisplayAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.ForumName;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.http.LHttpRequest;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.http.JsonCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyThreadActivity extends BaseActivity implements XListView.IXListViewListener{

    private Context mContext;
    private ViewGroup loadingView;
    private XListView mListView;
    private ForumDisplayAdapter mAdapter;
    private List<ForumThread> mForumThreads = new ArrayList<ForumThread>();
    private Map<String,ForumName> mForumNames = new HashMap<String,ForumName>();
    private String tip = null;
    private View pbEmptyTip;
    private String uid;
    private String nickname;
    private int page; //当前页码
    private ImageView imgHeadRight;
    private int perpage;
    private int threadCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_my_thread);
        mContext = this;
        initView();
        mAdapter = new ForumDisplayAdapter(this, mForumThreads,0);
        mAdapter.showForumName = true;
        mListView.setAdapter(mAdapter);
        mListView.autoRefresh();
    }

    public void initView() {

        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(this);
        loadingView = (ViewGroup) findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        loadingView.setVisibility(View.GONE);
        this.page = 1;
        this.uid = getIntent().getStringExtra("uid");
        this.nickname = getIntent().getStringExtra("nickname");
        if (MyApplication.getInstance().isIsLogin() && MyApplication.getInstance().userInfo.getNickname().equals(nickname)) {
            setTitleName("我的帖子");
        }else {
            setTitleName(this.nickname + "的帖子");
        }
    }

    @Override
    public void onRefresh() {

        this.page = 1;
        if (null != uid && !uid.equals("")) {
            requestMyThreadData(uid,page);
            mListView.setPullLoadEnable(false);
        }
    }

    @Override
    public void onLoadMore() {

        if(this.threadCount == this.perpage) {
            this.page++;
            requestMyThreadData(uid,page);
        }else {
            mListView.stopLoadMore();
        }
    }

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();

        if(this.threadCount < this.perpage) {
            mListView.setFootViewNoMore(true);
        } else {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        }
    }

    public void setForumsThreads(List<ForumThread> threads) {

        if (threads != null && threads.size() > 0 && threadCount > 0) {

            if(this.page == 1) {
                this.mForumThreads.clear();
            }
            this.mForumThreads.addAll(threads);
            if (mListView != null) {
                hideEmptyTip();
                mAdapter.notifyDataSetChanged();
            }
        }else {
            showEmptyTip("发布的帖子会出现在这里喔");
        }
    }

    public void setForumNames(Map<String,ForumName> forumNames) {
        if (null != forumNames) {
            mAdapter.forumNames.putAll(forumNames);
        }
    }

    TextView emptyView;
    public void showEmptyTip(String tip) {
        if(mListView.getHeaderViewsCount() == 1){
            if(emptyView == null){
                emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
            }
            emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
            emptyView.setText(tip);
            emptyView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
            mListView.addHeaderView(emptyView,null,false);
            mListView.setPullLoadEnable(false);
        }

    }


    public void hideEmptyTip() {
        if(emptyView != null && mListView.getHeaderViewsCount()>1){
            mListView.removeHeaderView(emptyView);
        }
    }

    public void reRequestLoading() {

        mListView.autoRefresh();
    }

    private void requestMyThreadData(String uid, int page) {

        if (uid != null && !uid.equals("") && page != 0) {
            LHttpRequest.getInstance().getMyThread(this,
                    new JsonCallback<String>() {

                        @Override
                        public void onGetDataSuccess(String data) {

                            loadingView.setVisibility(View.GONE);
                            List<ForumThread> threadList = new ArrayList<ForumThread>();
                            try {

                                JSONObject jsonObject = new JSONObject(data);
                                JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                                JSONObject dataObject = new JSONObject(variablesObject.getString("data"));

                                if (dataObject.has("forumnames")) {

                                    Gson gson = new Gson();
                                    mForumNames = gson.fromJson(dataObject.getString("forumnames"), new TypeToken<Map<String,ForumName>>() {
                                    }.getType());

                                    setForumNames(mForumNames);
                                }

                                if (dataObject.has("threadlist") && dataObject.has("threadcount")) {

                                    Gson gson = new Gson();
                                    threadList = gson.fromJson(dataObject.getString("threadlist"), new TypeToken<List<ForumThread>>() {
                                    }.getType());
                                    threadCount = Integer.parseInt(dataObject.getString("threadcount"));
                                    setForumsThreads(threadList);
                                }

                                if (variablesObject.has("perpage")) {
                                    perpage = Integer.parseInt(variablesObject.getString("perpage"));
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
                                        MyThreadActivity.this.requestMyThreadData(MyThreadActivity.this.uid,MyThreadActivity.this.page);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFinish() {

                            onLoad();
                            super.onFinish();
                        }
                    },uid,page);
        }
    }
}