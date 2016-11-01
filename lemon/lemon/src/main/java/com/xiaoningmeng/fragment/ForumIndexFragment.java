package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.ForumIndexAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.Forum;
import com.xiaoningmeng.bean.ForumNotice;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumIndexFragment extends BaseFragment  implements IXListViewListener {

    private ViewGroup loadingView;
    private XListView mListView;
    private ForumIndexAdapter mAdapter;
    private List<Forum> mForumList = new ArrayList<>();
    private View contentView;
    private String tip = null;
    private View pbEmptyTip;
    private ForumNotice notice;
    public String newMyPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = View.inflate(getActivity(),R.layout.fragment_forum_index, null);
        initView();
        mAdapter = new ForumIndexAdapter(getActivity(),mForumList);
        mListView.setAdapter(mAdapter);
        mListView.autoRefresh();
        return contentView;
    }

    public void initView() {

        loadingView = (ViewGroup)contentView.findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        loadingView.setVisibility(View.GONE);
        mListView = (XListView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mListView.setXListViewListener(this);
        mListView.setPullLoadEnable(false);
    }

    public void onResume() {

        super.onResume();
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_forum");
        }
    }

    public void hideEmptyTip() {

        super.hideEmptyTip();
        pbEmptyTip.setVisibility(View.INVISIBLE);
    }

    public void setForums(List<Forum> mforums) {
        if(mforums != null && mforums.size() >0){
            this.mForumList.addAll(mforums);
            if(mListView != null){

                hideEmptyTip();
                //TODO:
                mAdapter.notifyDataSetChanged();
            }
        }else{
            if(mListView != null){
                showEmptyTip(loadingView, "没有圈子 o(>ω<)o", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
            }
        }
    }

    public void setMessageBadgeNum(ForumNotice notice) {

        if (MyApplication.getInstance().isIsLogin() && MyApplication.getInstance().userInfo != null) {
            HomeActivity activity = (HomeActivity) this.getActivity();
            newMyPost = notice.getNewmypost();
            //将newMyPost传给ForumIndexAdapter是为了在打开帖子详情页是,详情页有又上角即刻出现badgeNumber
            mAdapter.newMyPost = newMyPost;
            int newMyPostInt = Integer.parseInt(notice.getNewmypost());
            if (newMyPostInt > 0) {
                activity.messageBadge.setText(newMyPost);
                activity.messageBadge.show();
            } else {
                activity.messageBadge.hide();
            }
        }
    }

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    public void onRefresh() {

        requestForumListData(Constant.FRIST, Constant.FRIST_ID);
        mListView.setPullLoadEnable(false);
    }

    @Override
    public void onLoadMore() {
        int size= mForumList.size();
        if(size > 0){
            requestForumListData(Constant.DOWN, mForumList.get(size - 1).getFId());
        }else{
            requestForumListData(Constant.FRIST, Constant.FRIST_ID);
        }
    }

    public void reRequestLoading(){

        mListView.autoRefresh();
        if(getView() == null){
            return;
        }
    }

    private void requestForumListData(final String direction,String startId) {

        LHttpRequest.getInstance().getForumIndex(getActivity(),
                new JsonCallback<String>() {

                    @Override
                    public void onGetDataSuccess(String data) {
                        loadingView.setVisibility(View.GONE);
                        try{
                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                            Gson gson = new Gson();
                            if(variablesObject.has("forumlist")) {

                                List<Forum> mforums = gson.fromJson(variablesObject.getString("forumlist"),new TypeToken<List<Forum>>() {}.getType());
                                if (direction == Constant.FRIST) {
                                    mForumList.clear();
                                }
                                setForums(mforums);
                            }

                            if(variablesObject.has("notice")) {
                                notice = gson.fromJson(variablesObject.getString("notice"),ForumNotice.class);
                                setMessageBadgeNum(notice);
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure( String responseString) {
                        loadingView.setVisibility(View.VISIBLE);
                        ((TextView)loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
                        loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
                        loadingView.setClickable(true);
                        loadingView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                reRequestLoading();
                                requestForumListData(Constant.FRIST, Constant.FRIST_ID);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                });
    }

}