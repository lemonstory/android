package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.R;
import com.xiaoningmeng.ViewThreadActivity;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ForumThread;

import java.util.List;

/**
 * Created by gaoyong on 16/1/22.
 */
public class ForumDisplayAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ForumThread> threadList;
    private int fid;

    public ForumDisplayAdapter(Context context, List<ForumThread> threads,int fid) {

        mContext = context;
        this.threadList = threads;
        this.fid = fid;

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return threadList.size();
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
            convertView = mInflater.inflate(R.layout.item_forum_display, null);
            holder.dividerView = convertView.findViewById(R.id.v_forum_divider);
            holder.iconImg = (ImageView) convertView.findViewById(R.id.iv_img_icon);
            holder.digestImg = (ImageView) convertView.findViewById(R.id.iv_digest_icon);
            holder.hotImg = (ImageView) convertView.findViewById(R.id.iv_hot_icon);
            holder.subjectTv = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.avatarImg = (ImageView) convertView.findViewById(R.id.cv_avatar);
            holder.postIconImg = (ImageView) convertView.findViewById(R.id.iv_post_icon);
            holder.authorTv = (TextView) convertView.findViewById(R.id.tv_author);
            holder.lastpostTV = (TextView) convertView.findViewById(R.id.tv_lastpost);
            holder.repliesTV = (TextView) convertView.findViewById(R.id.tv_replies);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ForumThread thread = threadList.get(position);

        //帖子附件有图标示
        holder.iconImg = (ImageView) convertView.findViewById(R.id.iv_img_icon);
        //精华帖标示
        holder.digestImg = (ImageView) convertView.findViewById(R.id.iv_digest_icon);
        //热帖标示
        holder.hotImg = (ImageView) convertView.findViewById(R.id.iv_hot_icon);
        //用户头像
        //avatarUrl
        //ImageLoader.getInstance().displayImage(avatarUrl, holder.avatarImg,Constant.AVARAR_OPTIONS);

        holder.subjectTv.setText(thread.getSubject());
        holder.authorTv.setText(thread.getAuthor());
        holder.lastpostTV.setText(Html.fromHtml(thread.getLastpost()));

        if (!thread.getReplies().equals("0")) {

            holder.postIconImg.setVisibility(View.VISIBLE);
            holder.repliesTV.setVisibility(View.VISIBLE);
            holder.repliesTV.setText(thread.getReplies());
        }else {

            holder.postIconImg.setVisibility(View.INVISIBLE);
            holder.repliesTV.setVisibility(View.INVISIBLE);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tid = threadList.get(position).getTid();
                Intent i = new Intent(mContext, ViewThreadActivity.class);
                i.putExtra("fid",fid);
                i.putExtra("tid", Integer.parseInt(tid));
                i.putExtra("page", 1);
                ((BaseActivity) mContext).startActivityForNew(i);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        View headDividerView;
        View dividerView;
        ImageView iconImg;
        ImageView digestImg;
        ImageView hotImg;
        TextView subjectTv;
        TextView authorTv;
        ImageView avatarImg;
        ImageView postIconImg;
        TextView lastpostTV;
        TextView repliesTV;
    }
}