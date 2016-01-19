package com.xiaoningmeng.adapter;

import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.DownloadActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadClientImpl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class DownloadAlbumAdapter extends BaseAdapter implements
		OnClickListener {

	private LayoutInflater mInflater;
	private DownloadActivity mContext;
	private int type;
	private List<AlbumInfo> downLoads;

	public DownloadAlbumAdapter(DownloadActivity context, int type,
			List<AlbumInfo> downLoads) {

		mContext = context;
		this.type = type;
		this.downLoads = downLoads;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return downLoads.size();
	}

	@Override
	public AlbumInfo getItem(int position) {

		return downLoads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_album_download, null);
			holder.coverImg = (ImageView) convertView
					.findViewById(R.id.img_download_item_cover);
			holder.storyTitle = (TextView) convertView
					.findViewById(R.id.tv_download_title);
			holder.downSizeTv = (TextView) convertView
					.findViewById(R.id.tv_download_size);
			holder.downStatusTv = (TextView) convertView
					.findViewById(R.id.tv_download_status);
			convertView.setTag(holder);
			holder.dividerView = convertView.findViewById(R.id.v_download_divider);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlbumInfo albumInfo = getItem(position);
		ImageLoader.getInstance().displayImage(albumInfo.getCover(),
				holder.coverImg,Constant.getSmallAlbumOptions(position));
		holder.storyTitle.setText(albumInfo.getTitle());
		if (type == 0) {
			HashMap<String, List<AudioDownLoad>> map = DownLoadClientImpl
					.getInstance().mHistoryMap;
			holder.downStatusTv.setText("已下载");
			holder.downSizeTv.setText(map.get(albumInfo.getAlbumid()).size()
					+ "");

		} else {
			HashMap<String, List<AudioDownLoad>> map = DownLoadClientImpl
					.getInstance().mDownloadMap;
			holder.downStatusTv.setText("下载中");
			holder.downSizeTv.setText(map.get(albumInfo.getAlbumid()).size()
					+ "");
		}
		RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
		lp.addRule(RelativeLayout.RIGHT_OF,position == getCount() -1 ? 0 : R.id.img_download_item_cover);
		return convertView;
	}

	static class ViewHolder {
		ImageView coverImg;
		TextView storyTitle;
		TextView downSizeTv;
		TextView downStatusTv;
		View dividerView;
	}

	@Override
	public void onClick(View v) {

	}
}