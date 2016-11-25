package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.xiaoningmeng.DownloadActivity;
import com.xiaoningmeng.DownloadStoryActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.DownloadAlbumAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.utils.UiUtils;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.util.List;

public class DownloadFragment extends BaseFragment {

	private SwipeMenuListView mListView;
	private int type = 0;
	private DownloadAlbumAdapter mAdapter;
	private List<AlbumInfo> downLoads;
	private DownloadActivity mContext;
	private ViewGroup contentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			type = bundle.getInt(DownloadActivity.FRAGMENT_POS);

		}
		mContext = (DownloadActivity) getActivity();
		contentView = (ViewGroup) View.inflate(getActivity(),
				R.layout.fragment_download, null);
		mListView = (SwipeMenuListView) contentView.findViewById(R.id.listView);
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				SwipeMenuItem openItem = new SwipeMenuItem(mContext);
				openItem.setBackground(new ColorDrawable(getResources()
						.getColor(R.color.logout_bg_normal)));
				openItem.setWidth(UiUtils.getInstance(mContext).DipToPixels(90));
				openItem.setTitle("删除");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		// set creator
		mListView.setMenuCreator(creator);
		mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					showDelDialog(position);
					break;
				}
				return false;
			}

		});
		if (type == DownloadStoryActivity.HISTORY_TYPE) {
			downLoads = DownLoadClientImpl.getInstance().mHistoryAlbumList;
		} else {
			downLoads = DownLoadClientImpl.getInstance().mDownloadAlbumList;
		}
		mAdapter = new DownloadAlbumAdapter(mContext, type, downLoads);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				Intent i = new Intent(mContext, DownloadStoryActivity.class);
				i.putExtra("type", type);
				i.putExtra("album", downLoads.get(pos));
				startActivityForNew(i);
			}
		});
		if (downLoads.size() == 0) {
			showEmptyTip(contentView, type == DownloadStoryActivity.HISTORY_TYPE?
					"下载的故事会出现在这里":"还没有下载任何故事", null);
		}
		return contentView;
	}

	private TipDialog tipDialog;

	private void showDelDialog(final int position) {
		tipDialog = new TipDialog.Builder(mContext)
				.setHasBtn(true)
				.setTipText("确定删除该故事辑下的所有故事？")
				.setEnterText("删除")
				.setTransparent(false)
				.setOnClickListener(
						new com.orhanobut.dialogplus.OnClickListener() {

							@Override
							public void onClick(DialogPlus dialog, View view) {
								switch (view.getId()) {
								case R.id.tv_dialog_enter:
									delDownloadAlbum(position);
									tipDialog.dimiss();
									break;
								case R.id.tv_dialog_cancel:
									tipDialog.dimiss();
									break;
								}
							}
						}).create();
		tipDialog.show();
	}

	private void delDownloadAlbum(int position) {
		if (downLoads != null && downLoads.size() > position) {
			String albumId = downLoads.get(position).getId();
			List<AudioDownLoad> downLoads;
			if (type == DownloadStoryActivity.HISTORY_TYPE) {
				downLoads = DownLoadClientImpl.getInstance().mHistoryMap
						.get(albumId);
			} else {
				downLoads = DownLoadClientImpl.getInstance().mDownloadMap
						.get(albumId);
			}
			AudioDownLoad[] downloadArray = new AudioDownLoad[downLoads.size()];
			downLoads.toArray(downloadArray);
			for (AudioDownLoad downLoad : downloadArray) {
				DownLoadClientImpl.getInstance().cancel(downLoad);
			}
		}
	}

	public void notifyDataChanged() {

		if (downLoads == null || downLoads.size() == 0) {
			
			showEmptyTip(contentView, type == DownloadStoryActivity.HISTORY_TYPE?
					"下载的故事会出现在这里":"还没有下载任何故事", null);
		} else {
			hideEmptyTip();
		}
		if(mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

}
