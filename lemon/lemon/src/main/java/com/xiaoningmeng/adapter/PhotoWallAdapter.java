package com.xiaoningmeng.adapter;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xiaoningmeng.PhotoWallActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.ImageFile;
import com.xiaoningmeng.constant.Constant;

/**
 * 
 * 选择照片中GridView的适配器
 */

public class PhotoWallAdapter extends BaseAdapter {

	private PhotoWallActivity context;
	private ArrayList<ImageFile> imageFileList = null;
	private ImageLoader imageLoader;


	public PhotoWallAdapter(PhotoWallActivity context,
			ArrayList<ImageFile> imageFileList) {
		this.context = context;
		this.imageFileList = imageFileList;

		imageLoader = ImageLoader.getInstance();

	}

	@Override
	public int getCount() {
		return imageFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return imageFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_photo_wall, null);
			holder = new ViewHolder();
			holder.relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.main_photo_wall_item_rl);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.photo_wall_item_photo);
			holder.backView = convertView.findViewById(R.id.selected_back_view);
			holder.selectdStatusIV = (ImageView) convertView
					.findViewById(R.id.selected_status_iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.relativeLayout.setTag(position);
		holder.relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				context.dealAdd2ButtomLayout(v);
			}
		});

		ImageFile imageFile = imageFileList.get(position);

		if (imageFile.isSelected()) {
			holder.backView.setVisibility(View.VISIBLE);
			holder.selectdStatusIV.setVisibility(View.VISIBLE);

		} else {
			holder.backView.setVisibility(View.INVISIBLE);
			holder.selectdStatusIV.setVisibility(View.INVISIBLE);
		}

		String imagePath = "file://" + imageFile.getFilePath();
		imageLoader.displayImage(imagePath, holder.imageView, Constant.getSmallAlbumOptions(position));

		return convertView;
	}

	private class ViewHolder {
		ImageView imageView;
		View backView;
		ImageView selectdStatusIV;
		RelativeLayout relativeLayout;
	}

}
