package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.ViewThreadActivity;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Attachment;
import com.xiaoningmeng.bean.ForumName;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.utils.ImageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoyong on 16/1/22.
 */
public class ForumDisplayAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ForumThread> threadList;
    public Map<String,ForumName> forumNames = new HashMap<String,ForumName>();
    private int fid;
    public boolean showForumName = false;
    public boolean showLastPostTime = false;
    public static int FORUM_IMAGE_WIDTH = 300;
    public static int FORUM_IMAGE_HEIGHT = 300;

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
            holder.digestImg = (TextView) convertView.findViewById(R.id.iv_digest_icon);
            holder.hotImg = (TextView) convertView.findViewById(R.id.iv_hot_icon);
            holder.subjectTv = (TextView) convertView.findViewById(R.id.tv_subject);
            holder.avatarImg = (ImageView) convertView.findViewById(R.id.cv_avatar);
            holder.postIconImg = (ImageView) convertView.findViewById(R.id.iv_post_icon);
            holder.authorTv = (TextView) convertView.findViewById(R.id.tv_author);
            holder.forumNameTv = (TextView) convertView.findViewById(R.id.tv_forum_name);
            holder.lastpostTv = (TextView) convertView.findViewById(R.id.tv_lastpost);
            holder.repliesTv = (TextView) convertView.findViewById(R.id.tv_replies);
            holder.imagesContainerLl = (LinearLayout) convertView.findViewById(R.id.ll_images_container);
            holder.imageF = (SimpleDraweeView) convertView.findViewById(R.id.iv_image_f);
            holder.imageS = (SimpleDraweeView) convertView.findViewById(R.id.iv_image_s);
            holder.imageT = (SimpleDraweeView) convertView.findViewById(R.id.iv_image_t);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ForumThread thread = threadList.get(position);
        String attachment = thread.getAttachment();
        holder.imageF.setVisibility(View.INVISIBLE);
        holder.imageF.setImageURI(null);
        holder.imageF.setBackgroundResource(R.color.view_thread_image_background_color);

        holder.imageS.setVisibility(View.INVISIBLE);
        holder.imageS.setImageURI(null);
        holder.imageS.setBackgroundResource(R.color.view_thread_image_background_color);

        holder.imageT.setVisibility(View.INVISIBLE);
        holder.imageT.setImageURI(null);
        holder.imageT.setBackgroundResource(R.color.view_thread_image_background_color);

        //附件,0无附件 1普通附件 2有图片附件
        if(!attachment.equals("2")) {
            holder.imagesContainerLl.setVisibility(View.GONE);
        }else {
            if (null != thread.getImagelist()) {
                holder.imagesContainerLl.setVisibility(View.VISIBLE);
                int imgListCount = thread.getImagelist().size();
                if (imgListCount > 0) {

                    for (int i = 0; i < imgListCount; i++) {

                        String aid = thread.getImagelist().get(i);
                        Attachment attachmentImg = thread.getAttachments().get(aid);
                        String url = attachmentImg.getUrl();
                        String path = attachmentImg.getAttachment();
                        String absolutePath;
                        //支持远程附件
                        if (!url.startsWith("http")) {
                            absolutePath = ConstantURL.BBS_URL + url + path;
                        }else {
                            absolutePath = url + path;
                        }
                        Uri imgUri = Uri.parse(absolutePath);
                        switch (i) {
                            case 0:
                                holder.imageF.setVisibility(View.VISIBLE);
                                ImageUtils.displayImage(mContext, holder.imageF, imgUri, FORUM_IMAGE_WIDTH, FORUM_IMAGE_HEIGHT);
                                break;
                            case 1:
                                holder.imageS.setVisibility(View.VISIBLE);
                                ImageUtils.displayImage(mContext, holder.imageS, imgUri, FORUM_IMAGE_WIDTH, FORUM_IMAGE_HEIGHT);
                                break;
                            case 2:
                                holder.imageT.setVisibility(View.VISIBLE);
                                ImageUtils.displayImage(mContext, holder.imageT, imgUri, FORUM_IMAGE_WIDTH, FORUM_IMAGE_HEIGHT);
                                break;
                        }
                    }
                }
            }else {
                holder.imagesContainerLl.setVisibility(View.GONE);
            }
        }
        //精华帖标示
        holder.digestImg = (TextView) convertView.findViewById(R.id.iv_digest_icon);
        boolean isDigest = thread.getDigest().equals("1") ? true : false;
        if(!isDigest) {
            holder.digestImg.setVisibility(View.GONE);
        }else {
            holder.digestImg.setVisibility(View.VISIBLE);
        }
        //热帖标示
        holder.hotImg = (TextView) convertView.findViewById(R.id.iv_hot_icon);
        String heats = thread.getHeats();
        String heatlevel = thread.getHeatlevel();
        //heats:帖子热度
        //heatlevel:帖子热度级别
        if(heats != null && !heats.equals("0") && heatlevel != null && !heatlevel.equals("0")) {
            holder.hotImg.setVisibility(View.VISIBLE);
        }else {
            holder.hotImg.setVisibility(View.GONE);
        }

        //用户头像
        //TODO:avatartime是要保持一个定值，只有更新头像才变一次,如果每次请求都是新值，cdn的cache就用不上了,每次都会去取源，不够快，费用也会高
        String authorid = thread.getAuthorid();
        String avatarTime = String.valueOf(thread.getDbdateline());
        String avatarUrl = AvatarUtils.getAvatarUrl(authorid, avatarTime, 120);
        Uri avatarUri = Uri.parse(avatarUrl);
        holder.avatarImg.setImageURI(avatarUri);
        String content = thread.getSubject() + "<br>" + thread.getMessage();
        if (holder.digestImg.getVisibility() == View.VISIBLE) {
            content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + content;
        }
        if (holder.hotImg.getVisibility() == View.VISIBLE) {
            content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + content;
        }
        holder.subjectTv.setText(Html.fromHtml(content));
        holder.authorTv.setText(thread.getAuthor());

        if (showLastPostTime) {
            holder.lastpostTv.setVisibility(View.VISIBLE);
            String lastPost = thread.getLastpost().replace("&nbsp;", "");
            holder.lastpostTv.setText(lastPost);
        }else {
            holder.lastpostTv.setVisibility(View.INVISIBLE);
        }

        if (showForumName) {
            holder.forumNameTv.setVisibility(View.VISIBLE);
            if (null != forumNames && null != thread.getFid() && !"".equals(thread.getFid())) {
                String fid = thread.getFid();
                String forumName = forumNames.get(fid).getName();
                holder.forumNameTv.setText("来自"+forumName);
            }
        }else {
            holder.forumNameTv.setVisibility(View.INVISIBLE);
        }

        //如果forumNameTv和lastpostTv同时出现,会出现重叠
        RelativeLayout.LayoutParams lastpostTvLp = (RelativeLayout.LayoutParams)holder.lastpostTv.getLayoutParams();
        RelativeLayout.LayoutParams forumNameTvLp = (RelativeLayout.LayoutParams)holder.forumNameTv.getLayoutParams();
        //https://github.com/himanshu-soni/ChatMessageView/issues/4
        final boolean hasSdk17 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        if (!thread.getReplies().equals("0")) {

            lastpostTvLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
            if (hasSdk17) {
                lastpostTvLp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
            }else {
                lastpostTvLp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_user);
            }
            forumNameTvLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
            if (hasSdk17) {
                forumNameTvLp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
            }else {
                forumNameTvLp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_user);
            }

            holder.postIconImg.setVisibility(View.VISIBLE);
            holder.repliesTv.setVisibility(View.VISIBLE);
            holder.repliesTv.setText(thread.getReplies());

        }else {

            lastpostTvLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (hasSdk17) {
                lastpostTvLp.addRule(RelativeLayout.ALIGN_PARENT_END);
            }else {
                lastpostTvLp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_user);
            }

            forumNameTvLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (hasSdk17) {
                forumNameTvLp.addRule(RelativeLayout.ALIGN_PARENT_END);
            }else {
                forumNameTvLp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.rl_user);
            }
            holder.postIconImg.setVisibility(View.INVISIBLE);
            holder.repliesTv.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tid = threadList.get(position).getTid();
                Intent i = new Intent(mContext, ViewThreadActivity.class);
                i.putExtra("fid",fid);
                i.putExtra("tid", Integer.parseInt(tid));
                i.putExtra("page", 1);
                ((BaseActivity) mContext).startActivityForNew(i);
            }
        };

        convertView.setOnClickListener(itemClickListener);
        holder.imagesContainerLl.setOnClickListener(itemClickListener);
        holder.imageF.setOnClickListener(itemClickListener);
        holder.imageS.setOnClickListener(itemClickListener);
        holder.imageT.setOnClickListener(itemClickListener);
        return convertView;
    }

    static class ViewHolder {

        View headDividerView;
        View dividerView;
        TextView digestImg;
        TextView hotImg;
        TextView subjectTv;
        TextView authorTv;
        ImageView avatarImg;
        ImageView postIconImg;
        TextView forumNameTv;
        TextView lastpostTv;
        TextView repliesTv;
        LinearLayout imagesContainerLl;
        SimpleDraweeView imageF;
        SimpleDraweeView imageS;
        SimpleDraweeView imageT;
    }
}