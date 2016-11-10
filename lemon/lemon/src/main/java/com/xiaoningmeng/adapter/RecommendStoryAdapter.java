package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.UiUtils;

import java.util.List;

public class RecommendStoryAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private List<AlbumInfo> mAlbumInfos;
	private boolean mIsNeedLoadXml;
	private LayoutInflater mInflater;
	private int mImgHeight;

	public RecommendStoryAdapter(Context context, List<AlbumInfo> albumInfos,boolean isNeedLoadXml){
		mContext = context;
		mAlbumInfos = albumInfos;
		mIsNeedLoadXml = isNeedLoadXml;
		mImgHeight = (int)(UiUtils.getInstance(mContext).getmScreenWidth()*0.27f);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	@Override
	public int getCount() {

		return mAlbumInfos.size()/3+ (mAlbumInfos.size() % 3== 0 ? 0 : 1);
	}

	@Override
	public Object getItem(int position) {

		return 0;
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null || !mIsNeedLoadXml){
			holder = new ViewHolder();
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.item_recommend, null);
			}
			holder.coverImg1 = (SimpleDraweeView) convertView
					.findViewById(R.id.img_story_cover1);
			holder.titleTv1 = (TextView) convertView
					.findViewById(R.id.tv_story_cover1);
			holder.tipTv1 = (TextView) convertView
					.findViewById(R.id.tv_story_tip1);
			holder.storyRl1 = convertView.findViewById(R.id.rl_story1);

			holder.coverImg2 = (SimpleDraweeView) convertView
					.findViewById(R.id.img_story_cover2);
			holder.titleTv2 = (TextView) convertView
					.findViewById(R.id.tv_story_cover2);
			holder.tipTv2 = (TextView) convertView
					.findViewById(R.id.tv_story_tip2);
			holder.storyRl2 = convertView.findViewById(R.id.rl_story2);

			holder.coverImg3 = (SimpleDraweeView) convertView
					.findViewById(R.id.img_story_cover3);
			holder.titleTv3 = (TextView) convertView
					.findViewById(R.id.tv_story_cover3);
			holder.tipTv3 = (TextView) convertView
					.findViewById(R.id.tv_story_tip3);
			holder.storyRl3 = convertView.findViewById(R.id.rl_story3);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
			List<AlbumInfo> albumInfos = mAlbumInfos;
			int coverPos  = position * 3;
			int albumsSize = albumInfos.size();
			if( albumsSize> coverPos){
				AlbumInfo albumInfo = albumInfos.get(coverPos);
				holder.storyRl1.setVisibility(View.VISIBLE);
				holder.titleTv1.setText(Html.fromHtml(albumInfo.getTitle()));
				String listenerAlbumCount = albumInfo.getListennum();
				if(!"0".equals(listenerAlbumCount)) {
					holder.tipTv1.setText(albumInfo.getListennum());
					holder.tipTv1.setVisibility(View.VISIBLE);
				}else{
					holder.tipTv1.setVisibility(View.INVISIBLE);
				}
				Uri coverImg1Uri = Uri.parse(albumInfo.getCover());
				holder.coverImg1.setBackgroundColor(Constant.getPosDrawable(coverPos));
				holder.coverImg1.setImageURI(coverImg1Uri);
				RelativeLayout.LayoutParams lp1= (RelativeLayout.LayoutParams) holder.coverImg1.getLayoutParams();
				lp1.height = mImgHeight;
				holder.coverImg1.setLayoutParams(lp1);

				holder.storyRl1.setTag(albumInfo);
				holder.storyRl1.setOnClickListener(this);
			}else{
				holder.storyRl1.setVisibility(View.INVISIBLE);
			}
			if(albumsSize > coverPos + 1){
				AlbumInfo albumInfo = albumInfos.get(coverPos+1);
				holder.storyRl2.setVisibility(View.VISIBLE);
				holder.titleTv2.setText(Html.fromHtml(albumInfo.getTitle()));
				String listenerAlbumCount = albumInfo.getListennum();
				if(!"0".equals(listenerAlbumCount)) {
					holder.tipTv2.setText(albumInfo.getListennum() + "");
					holder.tipTv2.setVisibility(View.VISIBLE);
				}else{
					holder.tipTv2.setVisibility(View.INVISIBLE);
				}
				Uri coverImgUri = Uri.parse(albumInfo.getCover());
				holder.coverImg2.setBackgroundResource(Constant.getPosDrawable(coverPos + 1));
				holder.coverImg2.setImageURI(coverImgUri);
				RelativeLayout.LayoutParams lp1= (RelativeLayout.LayoutParams) holder.coverImg2.getLayoutParams();
				lp1.height = mImgHeight;
				holder.coverImg2.setLayoutParams(lp1);
				holder.storyRl2.setTag(albumInfo);
				holder.storyRl2.setOnClickListener(this);
			}else{
				holder.storyRl2.setVisibility(View.INVISIBLE);
			}
			if(albumsSize > coverPos + 2){
				AlbumInfo albumInfo = albumInfos.get(coverPos+2);
				holder.storyRl3.setVisibility(View.VISIBLE);
				holder.titleTv3.setText(Html.fromHtml(albumInfo.getTitle()));
				String listenerAlbumCount = albumInfo.getListennum();
				if(!"0".equals(listenerAlbumCount)) {
					holder.tipTv3.setText(albumInfo.getListennum() + "");
					holder.tipTv3.setVisibility(View.VISIBLE);
				}else{
					holder.tipTv3.setVisibility(View.INVISIBLE);
				}
				Uri coverImg3Uri = Uri.parse(albumInfo.getCover());
				holder.coverImg3.setBackgroundResource(Constant.getPosDrawable(coverPos + 2));
				holder.coverImg3.setImageURI(coverImg3Uri);
				RelativeLayout.LayoutParams lp1= (RelativeLayout.LayoutParams) holder.coverImg3.getLayoutParams();
				lp1.height = mImgHeight;
				holder.coverImg3.setLayoutParams(lp1);
				holder.storyRl3.setTag(albumInfo);
				holder.storyRl3.setOnClickListener(this);
			}else{
				holder.storyRl3.setVisibility(View.INVISIBLE);
			}
		return convertView;
	}

	static class ViewHolder {

		TextView titleTv1;
		TextView tipTv1;
		SimpleDraweeView coverImg1;
		View storyRl1;
		TextView titleTv2;
		TextView tipTv2;
		SimpleDraweeView coverImg2;
		View storyRl2;
		TextView titleTv3;
		TextView tipTv3;
		SimpleDraweeView coverImg3;
		View storyRl3;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_story1:
		case R.id.rl_story2:
		case R.id.rl_story3:
			AlbumInfo albumInfo = (AlbumInfo) v.getTag();
			Intent intent = new Intent(mContext, AlbumDetailActivity.class);
			intent.putExtra("albumId", albumInfo.getAlbumid());
			intent.putExtra("albumInfo",albumInfo);
			((BaseActivity) mContext).startShareTransitionActivity(intent,v,"albumImage");
			break;
		case R.id.tv_classify_more:
		case R.id.tv_classify_title:
		case R.id.rl_story_more:
			int type = (int) v.getTag();
			Intent i= new Intent(mContext, MoreActivity.class);
			i.putExtra(MoreActivity.MORE_TYPE, type);
			((BaseActivity) mContext).startActivityForNew(i);
			break;

		default:
			break;
		}

	}
}