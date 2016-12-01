package com.xiaoningmeng.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.PhotoAlbumLVItem;

import java.io.File;
import java.util.ArrayList;

/**
 * 选择相册页面的
 * 
 * @author huangyanbin
 * 
 */
public class PhotoAlbumLVAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<PhotoAlbumLVItem> list;
	public PhotoAlbumLVAdapter(Context context, ArrayList<PhotoAlbumLVItem> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_photo_album, null);
			holder = new ViewHolder();

			holder.firstImageIV = (SimpleDraweeView) convertView
					.findViewById(R.id.select_img_gridView_img);
			holder.pathNameTV = (TextView) convertView
					.findViewById(R.id.select_img_gridView_path);
			holder.fileCountTV = (TextView) convertView
					.findViewById(R.id.file_count_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String filePath = list.get(position).getFirstImagePath();
		holder.firstImageIV.setTag(filePath);
		Uri fileUri = Uri.parse("file://" + filePath);
		holder.firstImageIV.setBackgroundResource(Constant.getPosDrawable(position));
		holder.firstImageIV.setImageURI(fileUri);
		holder.pathNameTV.setText(getPathNameToShow(list.get(position)));
		holder.fileCountTV.setText(list.get(position).getFileCount() + "");
		return convertView;
	}

	private class ViewHolder {
		SimpleDraweeView firstImageIV;
		TextView pathNameTV;
		TextView fileCountTV;
	}

	private String getPathNameToShow(PhotoAlbumLVItem item) {
		String absolutePath = item.getPathName();
		int lastSeparator = absolutePath.lastIndexOf(File.separator);
		return absolutePath.substring(lastSeparator + 1);
	}

}
