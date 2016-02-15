package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.ForumIndexAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.Forum;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumIndexFragment extends BaseFragment  implements IXListViewListener {

    private ViewGroup loadingView;
    private XListView mListView;
    private BaseAdapter mAdapter;
    private List<Forum> mForumList = new ArrayList<>();
    private View contentView;
    private String tip = null;
    private View pbEmptyTip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = View.inflate(getActivity(),R.layout.fragment_forum_index, null);
        initView();
        mAdapter = new ForumIndexAdapter(getActivity(),mForumList);
        mListView.setAdapter(mAdapter);
        showEmptyTip(loadingView, "正在努力加载中 o(>ω<)o", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
        requestForumListData(Constant.FRIST, Constant.FRIST_ID);
        return contentView;
    }

    public void initView() {

        mListView = (XListView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        loadingView = (ViewGroup)contentView.findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        mListView.setPullLoadEnable(mForumList.size() == 10);
        mListView.setPullRefreshEnable(false);
        mListView.setXListViewListener(this);
        mListView.setFootViewNoMore(true);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
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


    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    @Override
    public void onRefresh() {
        if(mForumList.size()>0){
            requestForumListData(Constant.UP, mForumList.get(0).getFId());
        }else{
            requestForumListData(Constant.FRIST, Constant.FRIST_ID);
        }
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

    private void requestForumListData(final String direction,String startId) {
        LHttpRequest.getInstance().getForumIndex(getActivity(),
                new LHttpHandler<String>(getActivity()) {

                    @Override
                    public void onGetDataSuccess(String data) {
                        try{
                            JSONObject jsonObject = new JSONObject(data);
                            if(jsonObject.has("forumlist")) {
                                Gson gson = new Gson();
                                List<Forum> mforums = gson.fromJson(jsonObject.getString("forumlist"),new TypeToken<List<Forum>>() {}.getType());
                                setForums(mforums);
                            }
                        }catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                });
    }

}