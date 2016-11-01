package com.xiaoningmeng.manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoningmeng.R;

/**
 * Created by huang on 2016/9/21.
 * 抽出RecyclerView emptyView
 */

public class EmptyHelper {

    public final static int LOADING = 0;

    public final static int EMPTY = 1;

    public final static int FAILURE = 2;

    public LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter mAdapter;

    public EmptyHelper(Context context, RecyclerView recyclerView, BaseQuickAdapter adapter) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mRecyclerView = recyclerView;
        this.mAdapter = adapter;
    }

    public void setEmptyView(int emptyStatus, boolean isFullScreen, String tip, View.OnClickListener onClickListener){
        View emptyView;
        switch (emptyStatus){
            case EMPTY:
            case FAILURE:
                emptyView = mInflater.inflate(isFullScreen ? R.layout.layout_empty
                        :R.layout.layout_empty2,(ViewGroup) mRecyclerView.getParent(),false);
                break;
            case LOADING:
            default:
                emptyView = mInflater.inflate(isFullScreen ? R.layout.layout_loading2
                        :R.layout.layout_loading3,(ViewGroup) mRecyclerView.getParent(),false);
                break;
        }
        if(isFullScreen) {
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT));
        }else{
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
        }
        ((TextView)emptyView.findViewById(R.id.tv_empty_tip)).setText(tip);
        if(emptyStatus == FAILURE && onClickListener != null){
            emptyView.setOnClickListener(onClickListener);
        }
        changedView(emptyView);
    }
    public void setEmptyView(int emptyStatus, boolean isFullScreen, String tip){
       setEmptyView(emptyStatus,isFullScreen,tip,null);
    }

    public void setEmptyView(int emptyStatus, String tip){
        setEmptyView(emptyStatus,true,tip,null);
    }

    private void changedView(View view) {

        mAdapter.setEmptyView(view);
        mAdapter.notifyItemChanged(0);
    }
}
