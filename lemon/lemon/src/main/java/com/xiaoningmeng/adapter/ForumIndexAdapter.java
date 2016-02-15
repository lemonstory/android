package com.xiaoningmeng.adapter;

import java.util.List;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.ForumDisplayActivity;
import com.xiaoningmeng.PerasonalCenterActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.Forum;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.view.RatingBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class ForumIndexAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Forum> forums;

    public ForumIndexAdapter(Context context, List<Forum> forums) {

        mContext = context;
        this.forums = forums;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return forums.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

                convertView = mInflater.inflate(
                        R.layout.item_forum_index, null);
                holder.containerFl = (FrameLayout)convertView.findViewById(R.id.fl_container);
                holder.dividerView = convertView
                        .findViewById(R.id.v_forum_divider);
                holder.iconImg = (ImageView) convertView
                        .findViewById(R.id.img_forum_icon);
                holder.nameTv = (TextView) convertView
                        .findViewById(R.id.tv_forum_name);
                holder.descriptionTv = (TextView) convertView
                        .findViewById(R.id.tv_forum_description);
                holder.todayposts = (TextView) convertView
                        .findViewById(R.id.tv_forum_todayposts);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String fid = forums.get(position).getFId();
                Intent i = new Intent(mContext,ForumDisplayActivity.class);
                i.putExtra("fid", Integer.parseInt(fid));
                i.putExtra("page",1);
                ((BaseFragmentActivity)mContext).startActivityForNew(i);
            }
        });

        Forum forum = forums.get(position);
        holder.nameTv.setText(forum.getName());
        holder.descriptionTv.setText(forum.getDescription());
        if (!forum.getPosts().equals("0")) {
            holder.todayposts.setText(forum.getPosts());
        }
        String avatarUrl = forum.getIcon();
        ImageLoader.getInstance().displayImage(avatarUrl, holder.iconImg,Constant.AVARAR_OPTIONS);


        return convertView;
    }

    static class ViewHolder {
        FrameLayout containerFl;
        View headDividerView;
        View dividerView;
        ImageView iconImg;
        TextView nameTv;
        TextView descriptionTv;
        TextView todayposts;
    }
}