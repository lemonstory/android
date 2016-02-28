package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.adapter.ForumDisplayAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumDisplayActivity extends BaseActivity implements XListView.IXListViewListener{

    private Context mContext;
    private ViewGroup loadingView;
    private XListView mListView;
    private BaseAdapter mAdapter;
    private List<ForumThread> mForumThreads = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    private int fid; //论坛ID
    private int page; //当前页码
    private int maxPage;//最大页码
    private ImageView imgHeadRight;
    private String title;
    private String hash;
    private String formHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_forum_display);
        mContext = this;
        fid = getIntent().getIntExtra("fid", 1);
        page = getIntent().getIntExtra("page", 1);
        title = getIntent().getStringExtra("name");
        initView();
        mAdapter = new ForumDisplayAdapter(this, mForumThreads,fid);
        mListView.setAdapter(mAdapter);
        requestForumThreadsData(fid, page);
    }

    public void initView() {

        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        loadingView = (ViewGroup) findViewById(R.id.rl_loading);
        imgHeadRight = (ImageView) findViewById(R.id.img_head_right);
        setTitleName(title);
        setRightHeadIcon(R.drawable.new_thread_bg_selector);

        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(this);
        this.page = 1;
        this.maxPage = 1;
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            loadingView.setClickable(false);
        }

        imgHeadRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(UserAuth.auditUser(mContext, "登录后,才能批量故事喔.")){
                    Intent i = new Intent(ForumDisplayActivity.this,NewThreadActivity.class);
                    i.putExtra("fid", fid);
                    i.putExtra("hash",hash);
                    i.putExtra("formhash",formHash);
                    startActivityForNew(i);
                }
            }
        });
    }

    @Override
    public void onRefresh() {

        //TODO:会出现白屏的现象
        this.mForumThreads.clear();
        this.page = 1;
        requestForumThreadsData(fid, page);
        mListView.setPullLoadEnable(false);
    }

    @Override
    public void onLoadMore() {

        if(this.page < this.maxPage) {
            int size = mForumThreads.size();
            if (size > 0) {
                this.page++;
                requestForumThreadsData(fid, page);
            } else {
                this.page = 1;
                requestForumThreadsData(fid, page);
            }
        }else {
            mListView.stopLoadMore();
        }
    }

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();

        if(this.page < this.maxPage) {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        } else {
            mListView.setFootViewNoMore(true);
        }
    }

    public void setForumsThreads(List<ForumThread> threads) {
        if (threads != null && threads.size() > 0) {
            this.mForumThreads.addAll(threads);
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

    private void requestForumThreadsData(int fid, int page) {

        LHttpRequest.getInstance().getForumThreads(this,
                new LHttpHandler<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {

                        loadingView.setVisibility(View.GONE);
                        Double threadsCount = 0.0;
                        Double tpp = 0.0;
                        List<ForumThread> threads = new ArrayList<ForumThread>();

                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));

                            if (variablesObject.has("forum_threadlist")) {
                                Gson gson = new Gson();
                                threads = gson.fromJson(variablesObject.getString("forum_threadlist"), new TypeToken<List<ForumThread>>() {
                                }.getType());
                                setForumsThreads(threads);
                            }

                            if (variablesObject.has("forum") && variablesObject.has("tpp")) {

                                String forum = variablesObject.getString("forum");
                                JSONObject forumJsonObject = new JSONObject(forum);
                                tpp = Double.parseDouble(variablesObject.getString("tpp"));

                                if (forumJsonObject.has("threadcount")) {
                                    threadsCount = Double.parseDouble(forumJsonObject.getString("threadcount"));
                                    Double dMaxPage = Math.ceil(threadsCount / tpp);
                                    ForumDisplayActivity.this.maxPage = dMaxPage.intValue();
                                }
                            }

                            if(variablesObject.has("hash")) {
                                hash = variablesObject.getString("hash");
                            }

                            if(variablesObject.has("formhash")) {
                                formHash = variablesObject.getString("formhash");
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
                                    ForumDisplayActivity.this.requestForumThreadsData(ForumDisplayActivity.this.fid, ForumDisplayActivity.this.page);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                }, fid, page);
    }
}
