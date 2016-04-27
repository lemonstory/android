package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.player.PlayerManager;

import java.util.List;

public class SearchStoryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Story> storys;
	private PlayingStory mPlayingStory;

	public SearchStoryListAdapter(Context context, List<Story> storys) {

		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.storys = storys;
		mPlayingStory = PlayerManager.getInstance().getPlayingStory();
	}

	@Override
	public int getCount() {
		return storys.size();
	}

	@Override
	public Story getItem(int position) {

		return storys.get(position);
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
			convertView = mInflater.inflate(
					R.layout.item_search_story_list, null);
			holder.numberTv = (TextView) convertView
					.findViewById(R.id.tv_play_list_number);
			holder.titleTv = (TextView) convertView
					.findViewById(R.id.tv_play_list_title);
			holder.statusImg = (ImageView) convertView
					.findViewById(R.id.img_play_list_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Story story = storys.get(position);
		getPlayView(holder,story);
		holder.numberTv.setText((position + 1) + "");
		holder.titleTv.setText(Html.fromHtml(story.getTitle()));
		return convertView;
	}



	static class ViewHolder {
		TextView numberTv;
		ImageView statusImg;
		TextView titleTv;
	}


	private void getPlayView(ViewHolder holder, Story story) {
		if (story.getAlbum_id().equals(mPlayingStory.albumid) && story.getMediapath().equals(mPlayingStory.mediapath)) {
			holder.numberTv.setVisibility(View.INVISIBLE);
			holder.statusImg.setVisibility(View.VISIBLE);
			holder.titleTv.setSelected(mPlayingStory.playState == PlayerManager.PlayState.RESUME ||mPlayingStory.playState == PlayerManager.PlayState.START||mPlayingStory.playState == PlayerManager.PlayState.PLAY);
		} else {
			holder.numberTv.setVisibility(View.VISIBLE);
			holder.statusImg.setVisibility(View.INVISIBLE);
			holder.titleTv.setSelected(false);
		}
	}

}