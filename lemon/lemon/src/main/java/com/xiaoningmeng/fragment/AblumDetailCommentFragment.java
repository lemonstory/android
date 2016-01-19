package com.xiaoningmeng.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumCommentAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

public class AblumDetailCommentFragment extends BaseFragment  implements IXListViewListener {

	private XListView mListView;
	private BaseAdapter mAdapter;
	private  List<Comment> mComments = new ArrayList<>();
	private View contentView;
	private String mAblumId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = View.inflate(getActivity(),R.layout.fragment_ablum_detail_comment, null);
		mListView = (XListView) contentView
				.findViewById(R.id.id_stickynavlayout_innerscrollview);
		mListView.setPullLoadEnable(mComments.size() == 10);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mAdapter = new AblumCommentAdapter(getActivity(),mComments);
		mListView.setAdapter(mAdapter);
		return contentView;
	}

	public void setComments(String ablumId,List<Comment> comments) {
		mAblumId = ablumId;
		if(comments != null && comments.size() >0){
			this.mComments.addAll(comments);
			if(mListView != null){
				if(comments.size() >=10){
					mListView.setPullLoadEnable(true);
				}
				hideEmptyTip();
				mAdapter.notifyDataSetChanged();
			}
		}else{
			if(mListView != null){
				showEmptyTip(null, "没有评论 o(>ω<)o", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
			}
		}
	}
	
	public void addComment(Comment comment){
		hideEmptyTip();
		mComments.add(0,comment);
		mAdapter.notifyDataSetChanged();
	}
	



	@Override
	public void onRefresh() {
		/*if(mComments != null && mComments.size() >0){
			String startId = mComments.get(0).getId();
			requestStoryListData(Constant.UP,startId);
		}*/
		
	}
	
	
	private void onLoad() {

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onLoadMore() {
		if(mComments != null && mComments.size() >0){
			String startId = mComments.get(mComments.size()-1).getId();
			requestStoryListData(Constant.DOWN,startId);
		}
	}

	private void requestStoryListData(final String direction,String startId) {
		LHttpRequest.getInstance().storyListReq(getActivity(),10,direction,startId, mAblumId,
			MyApplication.getInstance().getUid(),
			new LHttpHandler<Album>(getActivity()) {

				@Override
				public void onGetDataSuccess(Album data) {
					if(data != null && data.getCommentlist() != null){
						if(Constant.DOWN.equals(direction)&& data.getCommentlist().size() < 10){
							mListView.setFootViewNoMore(true);	
						}
						mComments.addAll(data.getCommentlist());
						mAdapter.notifyDataSetChanged();
					}else{
						mListView.setFootViewNoMore(true);
					}
					
				}
				
				@Override
				public void onFinish() {
					onLoad();
					super.onFinish();
				}
		});
	}
		
	}