package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.ClassificationActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.Discover;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.UiUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DiscoverStoryAdapter extends BaseAdapter implements
		OnClickListener {

	private LayoutInflater mInflater;
	private Context mContext;
	private Discover mDiscover;
	public final static int TITLE_TYPE = 0;
	public final static int COVER_TYPE = 1;
	public final static int MORE_TYPE = 2;
	public final static int CLASSIFICATION_TYPE = 3;
	public final static int AD_TYPE = 4;
	private int mImgHeight;
	public final String STORY_TYPES[] = { "热门推荐", "同龄在听", "最新上架", "私人定制" };
	public final int DRAWABLE_TIP_TYPES[] = { R.drawable.home_hot_tip,
			R.drawable.home_same_tip, R.drawable.home_new_tip,
			R.drawable.home_private_tip };
	//--baidu ad start
	public SparseArray<AdView> map;
	//--baidu ad end

	public DiscoverStoryAdapter(Context context, Discover discover) {

		mContext = context;
		mDiscover = discover;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImgHeight = (int)(UiUtils.getInstance(mContext).getmScreenWidth()*0.25f);
		//--baidu ad start
		map = new SparseArray<>();
		//--baidu ad end
	}

	private void removeList(List<AlbumInfo> albumInfos){
		if(albumInfos != null) {
			int size = albumInfos.size() - 1;
			for (int i = size; i >= 4; i--) {
				albumInfos.remove(i);
			}
		}
	}

	@Override
	public int getCount() {
		int count = 0;
		count += getClassificationCount(mDiscover.getFirsttag());
		count += getCount(mDiscover.getHotrecommend());
		count += getCount(mDiscover.getSamgeage());
		count += getCount(mDiscover.getNewalbum());
		count += getCount(mDiscover.getPrivatecustom());
		return count;
	}

	@Override
	public Object getItem(int position) {

		return 0;
	}

	private int getClassificationCount(List<Tag> tags){
		if(tags != null){
			int size  = tags.size();
			return (size%4 == 0?size/4 :(size/4+1));
		}
		return 0;
	}

	
	private int getCount(List<AlbumInfo> list){
		if(list!= null){
			int size = list.size();
			return  3+(size%2 == 0?size/2 :(size/2+1));
		}
		return 0;
		
	}
	@Override
	public int getItemViewType(int position) {

		int classifitionSize = getClassificationCount(mDiscover.getFirsttag());
		if(classifitionSize != 0 && position <classifitionSize){
			return CLASSIFICATION_TYPE;
		}
		position -= classifitionSize;
		int hotSize = getCount(mDiscover.getHotrecommend());
		if(hotSize !=  0 && position < hotSize){
			if(position == 0){
				return TITLE_TYPE;
			}else if(position == hotSize -2){
				return MORE_TYPE;
			}else if(position == hotSize -1){
				return AD_TYPE;
			}else{
				return COVER_TYPE;	
			}
		}
		position -= hotSize;
		int simpleSize = getCount(mDiscover.getSamgeage());
		if(simpleSize !=  0 && position < simpleSize){
			if(position == 0){
				return TITLE_TYPE;
			}else if(position == simpleSize -2){
				return MORE_TYPE;
			}else if(position == simpleSize -1){
				return AD_TYPE;
			} else{
				return COVER_TYPE;	
			}
		}
		position -= simpleSize;
		int newSize = getCount(mDiscover.getNewalbum());
		if(newSize !=  0 && position < newSize){
			if(position == 0){
				return TITLE_TYPE;
			}else if(position == newSize -2){
				return MORE_TYPE;
			}else if(position == newSize -1){
				return AD_TYPE;
			}else{
				return COVER_TYPE;	
			}
		}
		position -= newSize;
		int privateSize = getCount(mDiscover.getPrivatecustom());
		if(privateSize !=  0 && position < privateSize){
			if(position == 0){
				return TITLE_TYPE;
			}else if(position == privateSize -2){
				return MORE_TYPE;
			}else if(position == privateSize -1){
				return AD_TYPE;
			}else{
				return COVER_TYPE;	
			}
		}
		return COVER_TYPE;
	}

	public int getType(int position) {
		position -=  getClassificationCount(mDiscover.getFirsttag());
		int hotSize = getCount(mDiscover.getHotrecommend());
		if(hotSize !=  0 && position < hotSize){
			return MoreActivity.HOT_MORE;
		}
		position -= hotSize;
		int simpleSize = getCount(mDiscover.getSamgeage());
		if(simpleSize !=  0 && position < simpleSize){
			return MoreActivity.SAME_MORE;
		}
		position -= simpleSize;
		int newSize = getCount(mDiscover.getNewalbum());
		if(newSize !=  0 && position < newSize){
			return MoreActivity.NEW_MORE;
		}
		position -= newSize;
		int privateSize = getCount(mDiscover.getPrivatecustom());
		if(privateSize !=  0 && position < privateSize){
			return MoreActivity.PRIVATE_MORE;
		}
		return 0;
		
	}

	//得到起始位置的AlbumInfo
	public int getCoverPosition(int position) {

		position -=  getClassificationCount(mDiscover.getFirsttag());
		int hotSize = getCount(mDiscover.getHotrecommend());
		if(hotSize !=  0 && position < hotSize){
			return (position - 1)*2;
		}
		position -= hotSize;
		int simpleSize = getCount(mDiscover.getSamgeage());
		if(simpleSize !=  0 && position < simpleSize){
			return (position - 1)*2;
		}
		position -= simpleSize;
		int newSize = getCount(mDiscover.getNewalbum());
		if(newSize !=  0 && position < newSize){
			return (position - 1)*2;
		}
		position -= newSize;
		int privateSize = getCount(mDiscover.getPrivatecustom());
		if(privateSize !=  0 && position < privateSize){
			return (position - 1)*2;
		}
		return (position - 1)*2;


	}

	@Override
	public int getViewTypeCount() {

		return 5;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int type = getItemViewType(position);
		int typePostion = getType(position);
			switch (type) {
				case CLASSIFICATION_TYPE:
					HeadViewHolder headViewHolder = null;
					if (convertView == null) {
						headViewHolder = new HeadViewHolder();
						convertView = mInflater.inflate(
								R.layout.layout_home_classification, null);
						headViewHolder.classify1Tv = (TextView) convertView.findViewById(R.id.tv_classify1);
						headViewHolder.classify2Tv = (TextView) convertView.findViewById(R.id.tv_classify2);
						headViewHolder.classify3Tv = (TextView) convertView.findViewById(R.id.tv_classify3);
						headViewHolder.classify4Tv = (TextView) convertView.findViewById(R.id.tv_classify4);
						headViewHolder.classify1Ll = convertView.findViewById(R.id.ll_classify1);
						headViewHolder.classify2Ll = convertView.findViewById(R.id.ll_classify2);
						headViewHolder.classify3Ll = convertView.findViewById(R.id.ll_classify3);
						headViewHolder.classify4Ll = convertView.findViewById(R.id.ll_classify4);
						headViewHolder.classify1Img = (SimpleDraweeView) convertView.findViewById(R.id.img_classify1);
						headViewHolder.classify2Img = (SimpleDraweeView) convertView.findViewById(R.id.img_classify2);
						headViewHolder.classify3Img = (SimpleDraweeView) convertView.findViewById(R.id.img_classify3);
						headViewHolder.classify4Img = (SimpleDraweeView) convertView.findViewById(R.id.img_classify4);
						convertView.setTag(headViewHolder);
					} else {
						headViewHolder = (HeadViewHolder) convertView.getTag();
					}
					int classifitionSize = mDiscover.getFirsttag().size();
					Tag tag = null;
					if (classifitionSize > position * 4) {
						tag = mDiscover.getFirsttag().get(position * 4);
						headViewHolder.classify1Ll.setVisibility(View.VISIBLE);
						Uri classify1ImgUri = Uri.parse(tag.getCover());
						headViewHolder.classify1Img.setImageURI(classify1ImgUri);
						headViewHolder.classify1Tv.setText(tag.getName());
						headViewHolder.classify1Ll.setTag(tag);
						headViewHolder.classify1Ll.setOnClickListener(this);
					} else {
						headViewHolder.classify1Ll.setVisibility(View.INVISIBLE);
					}
					if (classifitionSize > position * 4 + 1) {

						tag = mDiscover.getFirsttag().get(position * 4 + 1);
						Uri classify2ImgUri = Uri.parse(tag.getCover());
						headViewHolder.classify2Img.setImageURI(classify2ImgUri);
						headViewHolder.classify2Tv.setText(tag.getName());
						headViewHolder.classify2Ll.setVisibility(View.VISIBLE);
						headViewHolder.classify2Ll.setTag(tag);
						headViewHolder.classify2Ll.setOnClickListener(this);
					} else {
						headViewHolder.classify2Ll.setVisibility(View.INVISIBLE);
					}
					if (classifitionSize > position * 4 + 2) {
						tag = mDiscover.getFirsttag().get(position * 4 + 2);
						Uri classify3ImgUri = Uri.parse(tag.getCover());
						headViewHolder.classify3Img.setImageURI(classify3ImgUri);
						headViewHolder.classify3Tv.setText(tag.getName());
						headViewHolder.classify3Ll.setVisibility(View.VISIBLE);
						headViewHolder.classify3Ll.setTag(tag);
						headViewHolder.classify3Ll.setOnClickListener(this);
					} else {
						headViewHolder.classify3Ll.setVisibility(View.INVISIBLE);
					}
					if (classifitionSize > position * 4 + 3) {
						tag = mDiscover.getFirsttag().get(position * 4 + 3);
						Uri classify4ImgUri = Uri.parse(tag.getCover());
						headViewHolder.classify4Img.setImageURI(classify4ImgUri);
						headViewHolder.classify4Tv.setText(tag.getName());
						headViewHolder.classify4Ll.setVisibility(View.VISIBLE);
						headViewHolder.classify4Ll.setTag(tag);
						headViewHolder.classify4Ll.setOnClickListener(this);
					} else {
						headViewHolder.classify4Ll.setVisibility(View.INVISIBLE);
					}
					break;
				case TITLE_TYPE:
					TopViewHolder topViewHolder = null;
					if (convertView == null) {
						topViewHolder = new TopViewHolder();
						convertView = mInflater.inflate(
								R.layout.item_home_discover_top, null);
						topViewHolder.classifyTv = (TextView) convertView
								.findViewById(R.id.tv_classify_title);
						topViewHolder.topMoreTv = (TextView) convertView
								.findViewById(R.id.tv_classify_more);
						convertView.setTag(topViewHolder);
					} else {
						topViewHolder = (TopViewHolder) convertView.getTag();
					}
					topViewHolder.classifyTv.setText(STORY_TYPES[typePostion]);
					topViewHolder.classifyTv.setTag(typePostion);
					Drawable drawable = mContext.getResources().getDrawable(
							DRAWABLE_TIP_TYPES[typePostion]);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					topViewHolder.classifyTv.setCompoundDrawables(drawable, null, null, null);
					topViewHolder.topMoreTv.setTag(typePostion);
					topViewHolder.topMoreTv.setOnClickListener(this);
					topViewHolder.classifyTv.setOnClickListener(this);
					topViewHolder.topMoreTv.setOnClickListener(this);
					break;
				case COVER_TYPE:
					CenterViewHolder centerViewHolder = null;
					if (convertView == null) {
						centerViewHolder = new CenterViewHolder();
						convertView = mInflater.inflate(
								R.layout.item_home_discover_center, null);
						centerViewHolder.coverImg1 = (SimpleDraweeView) convertView
								.findViewById(R.id.img_story_cover1);
						centerViewHolder.titleTv1 = (TextView) convertView
								.findViewById(R.id.tv_story_cover1);
						centerViewHolder.tipTv1 = (TextView) convertView
								.findViewById(R.id.tv_story_tip1);
						centerViewHolder.storyRl1 = convertView.findViewById(R.id.rl_story1);
						centerViewHolder.recommendTv1 = (TextView) convertView
								.findViewById(R.id.tv_story_recommend1);
						centerViewHolder.coverImg2 = (SimpleDraweeView) convertView
								.findViewById(R.id.img_story_cover2);
						centerViewHolder.titleTv2 = (TextView) convertView
								.findViewById(R.id.tv_story_cover2);
						centerViewHolder.tipTv2 = (TextView) convertView
								.findViewById(R.id.tv_story_tip2);
						centerViewHolder.recommendTv2 = (TextView) convertView
								.findViewById(R.id.tv_story_recommend2);
						centerViewHolder.storyRl2 = convertView.findViewById(R.id.rl_story2);
						RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) centerViewHolder.coverImg1.getLayoutParams();
						lp1.height = mImgHeight;
						centerViewHolder.coverImg1.setLayoutParams(lp1);
						RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) centerViewHolder.coverImg2.getLayoutParams();
						lp2.height = mImgHeight;
						centerViewHolder.coverImg2.setLayoutParams(lp2);
						convertView.setTag(centerViewHolder);
					} else {
						centerViewHolder = (CenterViewHolder) convertView.getTag();
					}
					List<AlbumInfo> albumInfos = null;
					switch (typePostion) {
						case MoreActivity.HOT_MORE:
							albumInfos = mDiscover.getHotrecommend();
							break;
						case MoreActivity.SAME_MORE:
							albumInfos = mDiscover.getSamgeage();
							break;
						case MoreActivity.NEW_MORE:
							albumInfos = mDiscover.getNewalbum();
							break;
						case MoreActivity.PRIVATE_MORE:
							albumInfos = mDiscover.getPrivatecustom();
							break;
					}
					int coverPos = getCoverPosition(position);
					int albumsSize = albumInfos.size();
					if (albumsSize > coverPos) {
						AlbumInfo albumInfo = albumInfos.get(coverPos);
						centerViewHolder.storyRl1.setVisibility(View.VISIBLE);
						centerViewHolder.titleTv1.setText(albumInfo.getTitle());
						centerViewHolder.tipTv1.setText(albumInfo.getListennum() == 0 ? "" : albumInfo.getListennum() + "");
						Uri coverImg1Uri = Uri.parse(albumInfo.getCover());
						centerViewHolder.coverImg1.setBackgroundResource(Constant.getPosDrawable(coverPos));
						centerViewHolder.coverImg1.setImageURI(coverImg1Uri);
						if(albumInfo.getRecommenddesc() != null&& !"".equals(albumInfo.getRecommenddesc())) {
							centerViewHolder.recommendTv1.setText(albumInfo.getRecommenddesc());
						}else{
							centerViewHolder.recommendTv1.setText(" ");
						}
						centerViewHolder.storyRl1.setTag(albumInfo);
						centerViewHolder.storyRl1.setOnClickListener(this);
					} else {
						centerViewHolder.storyRl1.setVisibility(View.INVISIBLE);
					}
					if (albumsSize > coverPos + 1) {
						AlbumInfo albumInfo = albumInfos.get(coverPos + 1);
						centerViewHolder.storyRl2.setVisibility(View.VISIBLE);
						centerViewHolder.titleTv2.setText(albumInfo.getTitle());
						centerViewHolder.tipTv2.setText(albumInfo.getListennum() == 0 ?"" :albumInfo.getListennum() + "");
						if(albumInfo.getRecommenddesc() != null&& !"".equals(albumInfo.getRecommenddesc())) {
							centerViewHolder.recommendTv2.setText(albumInfo.getRecommenddesc());
						}else{
							centerViewHolder.recommendTv2.setText(" ");
						}

						Uri coverImg2Uri = Uri.parse(albumInfo.getCover());
						centerViewHolder.coverImg2.setBackgroundResource(Constant.getPosDrawable(coverPos + 1));
						centerViewHolder.coverImg2.setImageURI(coverImg2Uri);
						centerViewHolder.storyRl2.setTag(albumInfo);
						centerViewHolder.storyRl2.setOnClickListener(this);
					} else {
						centerViewHolder.storyRl2.setVisibility(View.INVISIBLE);
					}
					break;
				case MORE_TYPE:
					BottomViewHolder bottomViewHolder = null;
					if (convertView == null) {
						bottomViewHolder = new BottomViewHolder();
						convertView = mInflater.inflate(
								R.layout.item_home_discover_bottom, null);
						bottomViewHolder.moreTv = convertView
								.findViewById(R.id.rl_story_more);
						convertView.setTag(bottomViewHolder);
					} else {
						bottomViewHolder = (BottomViewHolder) convertView.getTag();
					}
					bottomViewHolder.moreTv.setTag(typePostion);
					bottomViewHolder.moreTv.setOnClickListener(this);
					break;
				case AD_TYPE:
					//--baidu ad start
					AdView adView = null;
					if(convertView == null) {
						convertView = mInflater.inflate(
								R.layout.item_album_ad, null);
					}
					FrameLayout adFl = (FrameLayout) convertView;
					adFl.removeAllViews();
					if (map.get(typePostion) != null) {
						adView = map.get(typePostion);
					} else{
						String adId = null;
						switch (typePostion) {
							case MoreActivity.HOT_MORE:
								adId = Constant.BAIDU_HOME1_ID;
								break;
							case MoreActivity.SAME_MORE:
								adId = Constant.BAIDU_HOME2_ID;
								break;
							case MoreActivity.NEW_MORE:
							case MoreActivity.PRIVATE_MORE:
								adId = Constant.BAIDU_HOME3_ID;
								break;
							}
						adView = new AdView(mContext, adId);
						adView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
						map.put(typePostion,adView);
					}
					adView.setVisibility(View.GONE);
					adView.setListener(new MyAdListener(adView));
					adFl.addView(adView);
					//--baidu ad end
				break;

			}
		return convertView;
	}



	static class HeadViewHolder{
		TextView classify1Tv;
		TextView classify2Tv;
		TextView classify3Tv;
		TextView classify4Tv;
		View classify1Ll;
		View classify2Ll;
		View classify3Ll;
		View classify4Ll;
		SimpleDraweeView classify1Img;
		SimpleDraweeView classify2Img;
		SimpleDraweeView classify3Img;
		SimpleDraweeView classify4Img;
	}

	static class TopViewHolder {

		TextView classifyTv;
		TextView topMoreTv;
	}

	static class CenterViewHolder {

		TextView titleTv1;
		TextView recommendTv1;
		TextView tipTv1;
		SimpleDraweeView coverImg1;
		View storyRl1;
		TextView titleTv2;
		TextView tipTv2;
		TextView recommendTv2;
		SimpleDraweeView coverImg2;
		View storyRl2;
	}

	static class BottomViewHolder {

		View moreTv;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_story1:
		case R.id.rl_story2:
			AlbumInfo albumInfo = (AlbumInfo) v.getTag();
			Intent intent = new Intent(mContext, AblumDetailActivity.class);
			intent.putExtra("albumId", albumInfo.getAlbumid());
			intent.putExtra("albumInfo",albumInfo);
			((BaseFragmentActivity) mContext).startActivityForNew(intent);
			break;
		case R.id.tv_classify_more:
		case R.id.tv_classify_title:
		case R.id.rl_story_more:
			int type = (int) v.getTag();
			Intent i= new Intent(mContext, MoreActivity.class);
			i.putExtra(MoreActivity.MORE_TYPE, type);
			((BaseFragmentActivity) mContext).startActivityForNew(i);
			break;
		case R.id.ll_classify1:
		case R.id.ll_classify2:
		case R.id.ll_classify3:
		case R.id.ll_classify4:
			Tag tag = (Tag) v.getTag();
			if (tag != null && tag.getId() != null) {
				Intent ii = new Intent(mContext,ClassificationActivity.class);
				ii.putExtra("classification",tag);
				ii.putExtra("classification_name", tag.getName());
				((BaseFragmentActivity) mContext).startActivityForNew(ii);
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("tagId",tag.getId());
				map.put("tagName",tag.getName());
				MobclickAgent.onEvent(mContext, "event_click_tag", map);
			}
			break;
		}
	}
	//--baidu ad start
	public static class MyAdListener implements AdViewListener {
		private AdView adView;

		public MyAdListener(AdView adView){
			this.adView = adView;
		}

		@Override
		public void onAdReady(AdView adView) {

		}

		@Override
		public void onAdShow(JSONObject jsonObject) {
			if(adView != null){
				adView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAdClick(JSONObject jsonObject) {

		}

		@Override
		public void onAdFailed(String s) {
			if(adView != null){
				adView.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAdSwitch() {

		}
	}
	//--baidu ad end
}