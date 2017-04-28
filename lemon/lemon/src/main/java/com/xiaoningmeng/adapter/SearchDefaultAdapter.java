package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.SearchActivity;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.db.SearchDao;
import com.xiaoningmeng.view.FlowLayout;

import java.util.List;

public class SearchDefaultAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater mInflater;
	private Context mContext;
	public static final int HEAD_TYPE =0;
	public static final int ALBUM_TYPE =1;
	private List<SearchContent> hostSearch;
	private List<SearchContent> lastSearch;
	private MarginLayoutParams lp;


	public SearchDefaultAdapter(Context context, List<SearchContent> hostSearch, List<SearchContent> lastSearch) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.hostSearch = hostSearch;
		this.lastSearch = lastSearch;
		lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT,mContext.getResources().getDimensionPixelOffset(R.dimen.search_et_height));
		lp.rightMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.base_margin);
		lp.topMargin =mContext.getResources().getDimensionPixelOffset(R.dimen.base_margin);
	
	}

	@Override
	public int getCount() {
		if(lastSearch == null || lastSearch.size() == 0){
			return 2;
		}
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
	
				if(position == 0 || position == 2){
					return HEAD_TYPE;
				}else{
					return ALBUM_TYPE;
				}
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if(type == HEAD_TYPE){
				convertView = mInflater.inflate(R.layout.item_search_head, null);
				holder.headTv = (TextView) convertView.findViewById(R.id.tv_search_head);
				holder.clearTv = (TextView) convertView.findViewById(R.id.tv_search_clear);
			}else{
				convertView = mInflater.inflate(R.layout.item_search_default2, null);
				holder.flowLayout = (FlowLayout) convertView.findViewById(R.id.flowlayout);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(type == HEAD_TYPE){
			if(holder.headTv != null){
				holder.headTv.setText(position == 0 && lastSearch != null && lastSearch.size() > 0 ?"最近搜索" :"热门搜索");
				holder.clearTv.setVisibility(position == 0 && lastSearch.size() > 0 ?View.VISIBLE:View.INVISIBLE);
				holder.clearTv.setOnClickListener(onClearClickListener);
			}
		}else{
			if(holder.flowLayout != null){
				initTag(holder.flowLayout, position == 1&& lastSearch != null && lastSearch.size() > 0 ?lastSearch:hostSearch);
			}
		}
		return convertView;
	}
	
	private OnClickListener onClearClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(lastSearch.size()>0){
				lastSearch.clear();
				notifyDataSetChanged();
				SearchDao.getInstance().clearSearch();
			}	
		}
	};
	
	static class ViewHolder {
		TextView headTv;
		TextView clearTv;
		FlowLayout flowLayout;
	}

	public void initTag(FlowLayout mFlowLayout,List<SearchContent> searchContents){
		
		int allSize =  searchContents.size();
		int minWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.home_discover_btn);
		int childCount = mFlowLayout.getChildCount();
		if(childCount > allSize){
			for(int i = childCount -1; i>= allSize;i--){
				mFlowLayout.removeViewAt(i);
			}
		}
   	 	for(int i = 0; i <allSize; i ++){
   	 		TextView view;
   	 	if(mFlowLayout.getChildAt(i)!= null){
   	 		view = (TextView) mFlowLayout.getChildAt(i);
   	 	}else{
   	 		view = new TextView(mContext);
   	 		view.setMinWidth(minWidth);
   	 		ColorStateList csl= mContext.getResources().getColorStateList(R.color.search_flow_color_selector);
   	 		view.setTextColor(csl);
        	view.setBackgroundResource(R.drawable.bg_edittext_round_2);
        	view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        	view.setGravity(Gravity.CENTER);
        	mFlowLayout.addView(view,lp);
   	 	}
   	 	view.setOnClickListener(this);
   	 	String content = searchContents.get(i).getSearchcontent();
	 	view.setText(content);
	 	view.setTag(content);
   	 }
}

	@Override
	public void onClick(View v) {
		String content = (String) v.getTag();
		if(content != null){
			if(mContext instanceof SearchActivity) {
				((SearchActivity) mContext).search1(content);
			}else if(mContext instanceof  HomeActivity){
				if(content != null){
					((HomeActivity)mContext).search(content);
				}
			}
		}
	}
}