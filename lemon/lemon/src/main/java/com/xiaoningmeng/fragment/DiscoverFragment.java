package com.xiaoningmeng.fragment;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.ConvenientBanner.Transformer;
import com.bigkoo.convenientbanner.CBPageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.ClassificationActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.adapter.DiscoverStoryAdapter;
import com.xiaoningmeng.adapter.MoreAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.Discover;
import com.xiaoningmeng.bean.FocusPic;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

public class DiscoverFragment extends BaseFragment {

	private ConvenientBanner<FocusPic> convenientBanner;// 顶部广告栏控件
	private XListView mListView;
	private DiscoverStoryAdapter mAdapter;
	private BaseAdapter mTempAdapter;//临时adpter

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_discover, null);
		mListView = (XListView) contentView.findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mTempAdapter = new MoreAdapter(getActivity(), new ArrayList<AlbumInfo>());
		requestData();
		return contentView;
	}

	private void requestData() {
		showLoadingTip();
		mListView.setAdapter(mTempAdapter);
		LHttpRequest.getInstance().indexRequest(getActivity(),
				new LHttpHandler<Discover>(getActivity()) {

					@Override
					public void onGetDataSuccess(Discover data) {

						hideLoadingTip();
						List<FocusPic> focusPics = data.getFocuspic();
						if(focusPics!= null && focusPics.size() !=0){
							convenientBanner = new ConvenientBanner<>(getActivity());
							convenientBanner.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
									getResources().getDimensionPixelSize(R.dimen.home_banner_height)));
							mListView.addHeaderView(convenientBanner,null,false);
							convenientBanner.setPages(new CBViewHolderCreator<ImageHolder>() {
										@Override
										public ImageHolder createHolder() {
											return new ImageHolder();
										}
									}, focusPics)
									.setPageIndicator(new int[] { R.drawable.ic_page_indicator,R.drawable.ic_page_indicator_focused })
									.setPageTransformer(Transformer.DefaultTransformer);
						}
						mAdapter = new DiscoverStoryAdapter(getActivity(), data);
						mListView.setAdapter(mAdapter);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						mListView.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								hideLoadingTip();
								showEmptyTip();
							}
						}, 500);
						
					}
				});
		
	}



	public class ImageHolder implements CBPageAdapter.Holder<FocusPic> {
		private ImageView imageView;

		@Override
		public View createView(Context context) {
			imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			return imageView;
		}

		@Override
		public void UpdateUI(final Context context, final int position, final FocusPic data) {
			ImageLoader.getInstance().displayImage(data.getCover(), imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String linkUrl = data.getLinkurl();
					if(linkUrl != null){
						if(linkUrl.startsWith("http:")){
							WebViewActivity.openWebView(context, data.getLinkurl());
						}else if(linkUrl.startsWith("xnm:")){
							if(linkUrl.contains("albumid")){
								String ablumId = linkUrl.substring(linkUrl.lastIndexOf("=")+1,linkUrl.length());
								Intent intent = new Intent(getActivity(), AblumDetailActivity.class);
								intent.putExtra("albumId",ablumId);
								((BaseFragmentActivity) getActivity()).startActivityForNew(intent);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void onPause() {
		if(convenientBanner!= null)
			convenientBanner.stopTurning();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(convenientBanner!= null)
			convenientBanner.startTurning(5000);
		super.onResume();
	}
	
	View loadingView;
	public void showLoadingTip() {
		if(mListView.getHeaderViewsCount() == 1){
			if(loadingView == null){
				loadingView = View.inflate(getActivity(),R.layout.fragment_loading, null);
				loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_banner_height), 0, 0);
			}
			mListView.addHeaderView(loadingView,null,false);
		}
	}
	
	public void hideLoadingTip() {
		
		if(loadingView != null && mListView.getHeaderViewsCount() > 1){
			mListView.removeHeaderView(loadingView);
		}
	}
	
	
	TextView emptyView;
	public void showEmptyTip() {
		if(mListView.getHeaderViewsCount() == 1){
			if(emptyView == null){
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			     if (inflater != null) { 
			    	 emptyView = (TextView)inflater.inflate(R.layout.fragment_empty, null);
			     }
			}
			if(emptyView != null){
				emptyView.setText("请连接网络后点击屏幕重试");
				emptyView.setClickable(true);
				mListView.addHeaderView(emptyView,null,false);
				emptyView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hideEmptyTip();
					requestData();
				}
			});
			}
		}
	}
	
	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount() > 1){
			emptyView.setClickable(false);
			mListView.removeHeaderView(emptyView);
		}
	}

}
