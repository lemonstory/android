package com.xiaoningmeng;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.adapter.ForumDisplayAdapter;
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

    private ViewGroup loadingView;
    private XListView mListView;
    private BaseAdapter mAdapter;
    private List<ForumThread> mForumThreads = new ArrayList<>();
    private String tip = null;
    private View pbEmptyTip;
    private int fid; //论坛ID
    private int page; //当前页码
    private int maxPage;//最大页码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_display);
        initView();
        fid = getIntent().getIntExtra("fid", 1);
        page = getIntent().getIntExtra("page", 1);
        mAdapter = new ForumDisplayAdapter(this, mForumThreads,fid);
        mListView.setAdapter(mAdapter);
        requestForumThreadsData(fid, page);
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

    }

    @Override
    public void onRefresh() {

        //TODO:会出现白屏的现象
        this.mForumThreads.clear();
        this.page = 1;
        requestForumThreadsData(fid, page);
        mListView.setPullLoadEnable(false);
        mListView.setFootViewNoMore(true);
    }

    @Override
    public void onLoadMore() {

        if(this.page < this.maxPage) {
            int size = mForumThreads.size();
            if (size > 0) {
                this.page++;
                requestForumThreadsData(fid, page);
                ;
            } else {
                this.page = 1;
                requestForumThreadsData(fid, page);
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

        if (loadingView != null) {

            loadingView.setVisibility(View.INVISIBLE);
            loadingView.setClickable(false);
        }
        LHttpRequest.getInstance().getForumThreads(this,
                new LHttpHandler<String>(this) {

                    @Override
                    public void onGetDataSuccess(String data) {
                        Double threadsCount = 0.0;
                        Double tpp = 0.0;
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.has("forum_threadlist")) {
                                Gson gson = new Gson();
                                List<ForumThread> threads = gson.fromJson(jsonObject.getString("forum_threadlist"), new TypeToken<List<ForumThread>>() {
                                }.getType());
                                setForumsThreads(threads);
                            }

                            if (jsonObject.has("forum") && jsonObject.has("tpp")) {

                                String forum = jsonObject.getString("forum");
                                JSONObject forumJsonObject = new JSONObject(forum);
                                tpp = Double.parseDouble(jsonObject.getString("tpp"));

                                if (forumJsonObject.has("threadcount")) {
                                    threadsCount = Double.parseDouble(forumJsonObject.getString("threadcount"));
                                    Double dMaxPage = Math.ceil(threadsCount / tpp);
                                    ForumDisplayActivity.this.maxPage = dMaxPage.intValue();
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
