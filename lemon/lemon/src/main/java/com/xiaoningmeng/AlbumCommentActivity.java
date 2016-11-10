package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.adapter.AblumCommentAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumCommentActivity extends BaseActivity {

	private RecyclerView mRecyclerView;
	private EmptyHelper mEmptyHelper;
	private AblumCommentAdapter mAdapter;
	private List<Comment.DataBean.ItemsBean> mComments = new ArrayList<>();
	private String mAblumId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		DebugUtils.d("AlbumCommentActivity ---------> onCreate");
		setContentView(R.layout.activity_ablum_comment);
		mAdapter = new AblumCommentAdapter(mComments);
		mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		Intent intent = this.getIntent();
		mAblumId = intent.getStringExtra("albumId");
		initAdapter();
		mRecyclerView.addOnItemTouchListener(
				new OnItemChildClickListener() {
					@Override
					public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

						DebugUtils.d("AlbumCommentActivity  --> SimpleOnItemChildClick RUN! Position = " + position);
					}

					@Override
					public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

						super.onItemChildClick(adapter,view,position);
						DebugUtils.d("AlbumCommentActivity  --> onItemChildClick RUN! Position = " + position);
					}

					@Override
					public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

						super.onItemClick(adapter, view, position);
						DebugUtils.d("AlbumCommentActivity  --> onItemClick RUN! Position = " + position);
					}
				}
		);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initAdapter() {

		mRecyclerView.setAdapter(mAdapter);
		this.requestAlbumCommentData("down","0");
	}



//	public void setComments(String ablumId,List<CommentInfo> comments) {
//		mAblumId = ablumId;
//		if(comments != null && comments.size() != 0) {
//			mComments.addAll(comments);
//		}
//		flag = true;
//	}
//
//	public void addComment(CommentInfo comment){
//		hideEmptyTip();
//		mComments.add(0,comment);
//		mAdapter.notifyDataSetChanged();
//	}
//
//
//	@Override
//	public void onRefresh() {
//
//
//	}
//
//
//	private void onLoad() {
//
//		mListView.stopRefresh();
//		mListView.stopLoadMore();
//	}
//
//	@Override
//	public void onLoadMore() {
//		if(mComments != null && mComments.size() >0){
//			String startId = mComments.get(mComments.size()-1).getId();
//			requestStoryListData(Constant.DOWN,startId);
//		}
//	}

	private void requestAlbumCommentData(final String direction,String startId) {
		LHttpRequest.getInstance().albumCommentReq(this,mAblumId,direction,startId,100,
			MyApplication.getInstance().getUid(),
			new JsonCallback<Comment>() {

				@Override
				public void onGetDataSuccess(Comment data) {

					DebugUtils.d("data = " + data);
//					if(data != null && data.getCommentlist() != null){
//						if(Constant.DOWN.equals(direction)&& data.getCommentlist().size() < 10){
//							//mListView.setFootViewNoMore(true);
//						}
						mComments.addAll(data.getData().getItems());
						mAdapter.setNewData(mComments);
//						mAdapter.notifyDataSetChanged();
//					}else{
//						mListView.setFootViewNoMore(true);
//					}
					
				}
				
				@Override
				public void onFinish() {
//					onLoad();
					super.onFinish();
				}
		});
	}

	private void writeComment() {
		if (UserAuth.auditUser(this, "登录后,才能评论故事喔.")) {
			Intent i = new Intent(this, CommentWriteActivity.class);
			i.putExtra("albumId", mAblumId);
			startActivityForResult(i, 0);
		}
	}
}