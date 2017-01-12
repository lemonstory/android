package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.DownloadActivity;
import com.xiaoningmeng.FavActivity;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.MineHistoryAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Mine;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.db.HistoryDao;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.OnDownloadCountChangedListener;
import com.xiaoningmeng.event.FavEvent;
import com.xiaoningmeng.event.HistoryEvent;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;


public class MineFragment extends BaseFragment implements OnClickListener,XListView.IXListViewListener,OnDownloadCountChangedListener {
	
	private XListView mListView;
	private MineHistoryAdapter mAdapter;
	private BaseActivity mContext;
	private TextView mDownloadCountTv;
	private TextView mFavCountTv;
	private List<ListenerAlbum> mAlbumList;
	private List<ListenerAlbum> mDBHistoryAlbum;
	private List<HistoryEvent> mHistoryEvents = new ArrayList<>();
	private UserInfo mLoginUserInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View contentView = View.inflate(getActivity(), R.layout.fragment_mine, null);
		mContext = (BaseActivity) getActivity();
		DownLoadClientImpl.getInstance().setOnDownloadCountChangedListener(this);
		mDBHistoryAlbum = HistoryDao.getInstance().getHistoryAlbums();
		mLoginUserInfo = UserAuth.getInstance().getLoginUserInfo(getActivity());
		initView(contentView);
		requestListenerData(Constant.FRIST,Constant.FRIST_ID);
		EventBus.getDefault().register(this);
		return contentView;
	}

	private void initView(View contentView) {

		mListView = (XListView) contentView.findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		View headView = View.inflate(getActivity(),R.layout.fragment_mine_head, null);
		headView.findViewById(R.id.rl_mine_download).setOnClickListener(this);
		mDownloadCountTv = (TextView) headView.findViewById(R.id.tv_mine_download_count);
		mFavCountTv = (TextView)headView.findViewById(R.id.tv_mine_fav_count);
		if (null != mLoginUserInfo) {
			if (mLoginUserInfo.getFavcount().matches("-?\\d+")) {
				int loginUserFavCount = Integer.parseInt(mLoginUserInfo.getFavcount());
				if (loginUserFavCount > 0) {
					mFavCountTv.setText(loginUserFavCount);
				}
			}
		}
		int downloadSize = DownLoadClientImpl.getInstance().getDownloadCount();
		if (downloadSize > 0) {
			mDownloadCountTv.setText(downloadSize + "");
		}
		headView.findViewById(R.id.rl_mine_fav).setOnClickListener(this);
		mListView.addHeaderView(headView);
		showLoadingTip();
		mAlbumList = new ArrayList<>();
		mAdapter = new MineHistoryAdapter(mContext,mAlbumList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position -2;
				if(pos >= 0 && mAlbumList.size() > 0 && pos < mAlbumList.size()) {
					ListenerAlbum listenerAlbum = mAlbumList.get(pos);
					AlbumInfo albumInfo = listenerAlbum.getAlbuminfo();
					Intent intent = new Intent(mContext, AlbumDetailActivity.class);
					intent.putExtra("albumId", albumInfo.getId());
					intent.putExtra("albumInfo", albumInfo);
					intent.putExtra("playstoryid", listenerAlbum.getPlaystoryid());
					intent.putExtra("playtimes", listenerAlbum.getPlaytimes());
					mContext.startShareTransitionActivity(intent,view.findViewById(R.id.img_mine_item_cover),"albumImage");
				}
			}
		});
	}

	private void requestListenerData(final String direction,String startId) {

		if (UserAuth.getInstance().isLogin(this.getActivity())) {

			LHttpRequest.MyStoryRequest myStoryRequest = mRetrofit.create(LHttpRequest.MyStoryRequest.class);
			Call<JsonResponse<Mine>> call = myStoryRequest.getResult(direction, startId, Constant.MAX_REQ_LEN);
			call.enqueue(new Callback<JsonResponse<Mine>>() {

				@Override
				public void onResponse(Call<JsonResponse<Mine>> call, Response<JsonResponse<Mine>> response) {

					onLoad();
					if (response.isSuccessful() && response.body().isSuccessful()) {

						Mine data = response.body().getData();
						List<ListenerAlbum> albums = data.getListenalbumlist();
						if (direction == Constant.FRIST) {
							hideLoadingTip();
							mAlbumList.clear();
							addHistoryAlbum(mDBHistoryAlbum);
							if (albums.size() == Constant.MAX_REQ_LEN) {
								mListView.setPullLoadEnable(true);
							} else if (albums.size() < Constant.MAX_REQ_LEN) {
								mListView.setPullLoadEnable(false);
							}
						} else {
							if (albums.size() < Constant.MAX_REQ_LEN) {
								mListView.setFootViewNoMore(true);
							}
						}
						addHistoryAlbum(albums);
						mAdapter.notifyDataSetChanged();
						if (mAlbumList.size() == 0) {
							showEmptyTip(null, "收听的故事会出现在这里喔", null);
						}
					} else {
						DebugUtils.e(response.toString());
					}
				}

				@Override
				public void onFailure(Call<JsonResponse<Mine>> call, Throwable t) {

					onLoad();
					mListView.postDelayed(new Runnable() {

						@Override
						public void run() {
							if(direction == Constant.FRIST && mAlbumList.size() == 0){

								hideLoadingTip();
								showEmptyTip(null, "请连接网络后点击屏幕重试", new OnClickListener() {

									@Override
									public void onClick(View v) {
										hideEmptyTip();
										showLoadingTip();
										requestListenerData(Constant.FRIST,Constant.FRIST_ID);

									}
								});

							}
						}
					}, 500);
				}
			});

		}else{

			hideLoadingTip();
			mListView.setPullLoadEnable(true);
			List<ListenerAlbum> dbHistoryAlbum = HistoryDao.getInstance().getUnloginHistoryAlbums(mAlbumList.size(), Constant.MAX_REQ_LEN);
			if(dbHistoryAlbum.size()>0){
				addHistoryAlbum(dbHistoryAlbum);
			}else{
				mListView.setFootViewNoMore(true);
			}
			if(mAlbumList.size() == 0){
				showEmptyTip(null, "收听的故事会出现在这里喔", null);
			}
			mAdapter.notifyDataSetChanged();
			onLoad();
		}
	}


	
	private void addHistoryAlbum(List<ListenerAlbum> historyAlbums){
		
		if(historyAlbums != null && historyAlbums.size() >0){
			for(ListenerAlbum album : historyAlbums){
			}
			mAlbumList.addAll(historyAlbums);
		}
	}
	@Override
	public void onRefresh() {

		if(mAlbumList.size()>0){
			requestListenerData(Constant.UP, mAlbumList.get(0).getAlbumid());
		}else{
			requestListenerData(Constant.FRIST, Constant.FRIST_ID);
		}
	}

	@Override
	public void onLoadMore() {
		int size= mAlbumList.size();
		if(size > 0){
			requestListenerData(Constant.DOWN, mAlbumList.get(size-1).getAlbumid());
		}else{
			requestListenerData(Constant.FRIST, Constant.FRIST_ID);
		}
	}
	

	private void onLoad() {

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_mine_download:
			((BaseActivity) getActivity()).startActivityForNew(new Intent(getActivity(),DownloadActivity.class));
			break;
		case R.id.rl_mine_fav:
			if (UserAuth.getInstance().isLogin(mContext)) {
				((BaseActivity) getActivity()).startActivityForNew(new Intent(getActivity(),FavActivity.class));
			} else {
				((BaseActivity) getActivity()).startActivityForNew(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	
	public void onEventMainThread(HistoryEvent historyEvent){
		mHistoryEvents.add(historyEvent);
	}
	
	public void onEventMainThread(LoginEvent loginEvent){

		mDBHistoryAlbum = HistoryDao.getInstance().getHistoryAlbums();
		mAlbumList.clear();
		hideEmptyTip();
		requestListenerData(Constant.FRIST,Constant.FRIST_ID);
	}
	
	public void onEventMainThread(FavEvent favEvent){
		
		String favCount = mFavCountTv.getText().toString();
		int fav = 0;
		if(!"".equals(favCount)){
			fav = Integer.parseInt(favCount);
		}
		fav = favEvent.fav == 1? fav+1 : fav-1;
		mFavCountTv.setText(fav+"");
	}
	
	public void onResume() {
		
		super.onResume();
		if(mHistoryEvents.size() >0){
			hideEmptyTip();
			for(HistoryEvent historyEvent :mHistoryEvents){
				if(historyEvent.listenerAlbum !=null)
					update(historyEvent);
			}
			mHistoryEvents.clear();
			mAdapter.notifyDataSetChanged();
		}
		MobclickAgent.onEvent(mContext, "event_show_mystory");
	}

	private void update(HistoryEvent historyEvent) {
		int albumSzie = mAlbumList.size();
		for(int i = 0; i <albumSzie ; i++){
			ListenerAlbum album = mAlbumList.get(i);
			if(null != album && null != album.getAlbumid() && null != historyEvent && null != historyEvent.albumId) {
				if(album.getAlbumid().equals(historyEvent.albumId)){
					mAlbumList.remove(album);
					break;
				}
			}
		}
		mAlbumList.add(0,historyEvent.listenerAlbum);
	}
	
	View loadingView;
	public void showLoadingTip() {
		if(mListView.getHeaderViewsCount() == 2){
			if(loadingView == null){
				loadingView = View.inflate(getActivity(),R.layout.fragment_loading, null);
				loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
			}
			mListView.addHeaderView(loadingView,null,false);
		}
	}
	
	public void hideLoadingTip() {
		
		if(loadingView != null && mListView.getHeaderViewsCount() > 2){
			mListView.removeHeaderView(loadingView);
		}
	}
	
	TextView emptyView;
	@Override
	public void showEmptyTip(ViewGroup parentView, String tip,
			OnClickListener onClickListener) {
		
		if(mListView.getHeaderViewsCount() == 2){
			if(emptyView == null && getActivity() != null){
				emptyView = (TextView) View.inflate(getActivity(),R.layout.fragment_empty, null);
				emptyView.setText(tip);
				emptyView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
			}
			if(onClickListener != null){
				emptyView.setClickable(true);
				emptyView.setOnClickListener(onClickListener);
			}else{
				emptyView.setClickable(false);
			}
		mListView.addHeaderView(emptyView,null,false);
		mListView.setPullLoadEnable(false);
		}

	}
	
	
	@Override
	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount()>2){
			mListView.removeHeaderView(emptyView);
		}
		mListView.setPullLoadEnable(false);
	}

	@Override
	public void notifyDownloadCountChanged(final int downloadCount) {
		if(mDownloadCountTv != null)
			mDownloadCountTv.setText((downloadCount >=0 ?downloadCount :0)+ "");
	}
}
