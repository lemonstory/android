package com.xiaoningmeng.adapter;

import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

public class MineHistoryAdapter extends BaseQuickAdapter<ListenerAlbum, BaseViewHolder> {

    public MineHistoryAdapter(@LayoutRes int layoutResId, @Nullable List<ListenerAlbum> data) {
        super(layoutResId, data);
    }

    public MineHistoryAdapter(@Nullable List<ListenerAlbum> data) {
        super(data);
    }

    public MineHistoryAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

	@Override
    protected void convert(BaseViewHolder helper, ListenerAlbum item) {

        int position = helper.getLayoutPosition();
        SimpleDraweeView coverImg = (SimpleDraweeView) helper.getView(R.id.img_mine_item_cover);
        View historyTopDividerV = helper.getView(R.id.v_history_top_divider);
        View historyBottomDividerV = helper.getView(R.id.v_history_bottom_divider);
        View historyDividerV = helper.getView(R.id.v_top_history_divider);
        RelativeLayout historyRl = helper.getView(R.id.rl_history);
        TextView storyTv = (TextView) helper.getView(R.id.tv_mine_story);
        TextView titleTv = (TextView) helper.getView(R.id.tv_fav_title);
        TextView playTimeTv = (TextView) helper.getView(R.id.tv_add_time);

        AlbumInfo albumInfo = item.getAlbuminfo();
        Calendar explainCalendar = Calendar.getInstance();
        explainCalendar.setTimeInMillis(Long.parseLong(item.getUptime()) * 1000);

        historyTopDividerV.setVisibility(View.GONE);
        historyDividerV.setVisibility(position != 0 ? View.GONE : View.VISIBLE);
        titleTv.setText(albumInfo.getTitle());
        Story story = albumInfo.getStoryinfo();
        if (null != story) {
            storyTv.setText(story.getTitle());
            if (item.getPlaytimes() >= Integer.parseInt(story.getTimes()) - 3) {
                playTimeTv.setText("播放完成");
            }else{
                playTimeTv.setText("播放至" + TimeUtils.getShortTimeShot(item.getPlaytimes()));
            }
		}else{
            storyTv.setText("");
            playTimeTv.setText("暂无播放信息");
        }

		//http://www.fresco-cn.org/docs/rounded-corners-and-circles.html
		//并非所有的图片分支部分都可以实现圆角，目前只有占位图片和实际图片可以实现圆角
        Uri coverImgUri = Uri.parse(item.getAlbuminfo().getCover());
        GenericDraweeHierarchy hierarchy = coverImg.getHierarchy();
        hierarchy.setPlaceholderImage(Constant.getPosDrawable(position));
        coverImg.setImageURI(coverImgUri);

//		if(position == getCount()-1){
//			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.historyBottomDividerV.getLayoutParams();
//			lp.addRule(RelativeLayout.ALIGN_LEFT,0);
//			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
//		}else{
//			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.historyBottomDividerV.getLayoutParams();
//			lp.addRule(RelativeLayout.ALIGN_LEFT,R.id.tv_fav_title);
//			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
//		}
//		return convertView;
    }


}