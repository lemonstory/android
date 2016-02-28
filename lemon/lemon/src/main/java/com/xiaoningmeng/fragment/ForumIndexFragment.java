package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
        requestForumListData(Constant.FRIST, Constant.FRIST_ID);
        return contentView;
    }

    public void initView() {

        mListView = (XListView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        loadingView = (ViewGroup)contentView.findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(this);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            loadingView.setClickable(false);
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

    private void onLoad() {

        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    public void onRefresh() {

        //TODO:会出现白屏的现象
        this.mForumList.clear();
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
        if(getView() == null){
            return;
        }
        loadingView.setClickable(false);
        loadingView.setVisibility(View.VISIBLE);
        ((TextView)loadingView.getChildAt(0)).setText("正在努力加载中");
        loadingView.getChildAt(1).setVisibility(View.VISIBLE);
    }

    private void requestForumListData(final String direction,String startId) {

        LHttpRequest.getInstance().getForumIndex(getActivity(),
                new LHttpHandler<String>(getActivity()) {

                    @Override
                    public void onGetDataSuccess(String data) {

                        loadingView.setVisibility(View.GONE);
                        try{
                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));

                            if(variablesObject.has("forumlist")) {
                                Gson gson = new Gson();
                                List<Forum> mforums = gson.fromJson(variablesObject.getString("forumlist"),new TypeToken<List<Forum>>() {}.getType());
                                setForums(mforums);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        loadingView.setVisibility(View.VISIBLE);
                        ((TextView)loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
                        loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
                        loadingView.setClickable(true);
                        loadingView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                reRequestLoading();
                                requestForumListData(Constant.FRIST, Constant.FRIST_ID);;
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