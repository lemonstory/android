package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.Attachment;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.Post;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.utils.AvatarUtils;

import java.util.List;

public class ViewThreadAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Post> posts;
    public ForumThread forumThread;
    public String aaa;

    public ViewThreadAdapter(Context context,ForumThread forumThread, List<Post> posts) {

        mContext = context;
        this.forumThread = forumThread;
        this.posts = posts;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    @Override
    public int getCount() {
        return posts.size();
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
                    R.layout.item_view_thread, null);
            holder.containerFl = (FrameLayout)convertView.findViewById(R.id.fl_container);
            holder.dividerView = convertView
                    .findViewById(R.id.v_forum_divider);

            holder.avatarImg = (ImageView) convertView
                    .findViewById(R.id.cv_avatar);
            holder.authorTV = (TextView) convertView
                    .findViewById(R.id.tv_author);
            holder.datelineTv = (TextView) convertView
                    .findViewById(R.id.tv_dateline);
            holder.repliesTv = (TextView) convertView
                    .findViewById(R.id.tv_replies);
            holder.postIconImg = (ImageView) convertView.findViewById(R.id.iv_post_icon);
            holder.iconImg = (ImageView) convertView
                    .findViewById(R.id.iv_img_icon);
            holder.digestIconImg = (ImageView) convertView
                    .findViewById(R.id.iv_digest_icon);
            holder.hotIconImg = (ImageView) convertView
                    .findViewById(R.id.iv_hot_icon);
            holder.messageTv = (TextView) convertView
                    .findViewById(R.id.tv_message);

            holder.quoteContainerRl = (RelativeLayout)convertView.findViewById(R.id.rl_quote_container);
            holder.quoteAuthorTv = (TextView) convertView.findViewById(R.id.tv_quote_author);
            holder.quoteMessageTv = (TextView) convertView.findViewById(R.id.tv_quote_message);
            holder.imagesContainerLl = (LinearLayout) convertView.findViewById(R.id.ll_images_container);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String fid = posts.get(position).getTid();
//                Intent i = new Intent(mContext, ForumDisplayActivity.class);
//                i.putExtra("fid", Integer.parseInt(fid));
//                i.putExtra("page", 1);
//                ((BaseFragmentActivity) mContext).startActivityForNew(i);
            }
        });

        Post post = posts.get(position);
        //TODO:avatartime是要保持一个定值，只有更新头像才变一次,如果每次请求都是新值，cdn的cache就用不上了,每次都会去取源，不够快，费用也会高
        String authorid = post.getAuthorid();
        String avatarTime = String.valueOf(post.getDbdateline());
        String avatarUrl = AvatarUtils.getAvatarUrl(authorid, avatarTime, 120);
        ImageLoader.getInstance().displayImage(avatarUrl, holder.avatarImg, Constant.AVARAR_OPTIONS);
        holder.authorTV.setText(post.getAuthor());
        holder.datelineTv.setText(Html.fromHtml(post.getDateline()));
        if (post.getFirst().equals("1") && post.getPosition().equals("1")) {

            //楼主显示回帖数,回帖图标
            holder.postIconImg.setVisibility(View.VISIBLE);
            holder.iconImg.setVisibility(View.VISIBLE);
            holder.digestIconImg.setVisibility(View.VISIBLE);
            holder.hotIconImg.setVisibility(View.VISIBLE);
            if (!forumThread.getReplies().equals("0")) {
                holder.repliesTv.setText(forumThread.getReplies());
            }
        }else {
            //回帖隐藏回帖数,回帖图标,显示楼层
            holder.postIconImg.setVisibility(View.GONE);
            holder.iconImg.setVisibility(View.GONE);
            holder.digestIconImg.setVisibility(View.GONE);
            holder.hotIconImg.setVisibility(View.GONE);
            holder.repliesTv.setText(post.getPosition() + "楼");
        }

        //处理引用内容
        //示例:
        // <div class="reply_wrap">
        //  <a href="http://bbs.xiaoningmeng.net/forum.php?mod=redirect&amp;goto=findpost&amp;pid=73&amp;ptid=32">
        //      <font color="#999999">admin 发表于 2016-1-25 09:58</font>
        //  </a>
        //  <br />
        //  回帖2
        // </div>
        // <br />
        // 回帖-2-回复1

        String message = post.getMessage();
        int divMarkIndex = message.indexOf("<div class=\"reply_wrap\">");
        if (divMarkIndex != -1) {

            holder.quoteContainerRl.setVisibility(View.VISIBLE);
            int fontMarkStartIndex = message.indexOf("<font color=\"#999999\">");
            int fontMartStartLen = "<font color=\"#999999\">".length();
            int fontMarkEndIndex = message.indexOf("</font>");
            String quoteAuthorAndTime =  message.substring(fontMarkStartIndex + fontMartStartLen, fontMarkEndIndex);
            int blandInQuoteAuthorAndTimeIndex = quoteAuthorAndTime.indexOf(" ");
            String quoteAuthor = quoteAuthorAndTime.substring(0, blandInQuoteAuthorAndTimeIndex);
            holder.quoteAuthorTv.setText(quoteAuthor);
            int brMartFirstIndex = message.indexOf("<br />");
            int brMartLen = "<br />".length();
            int divCloseMarkFirstIndex = message.indexOf("</div>");
            int quoteMessageStartIndex = brMartFirstIndex + brMartLen + 2;
            String quoteMessage = message.substring(quoteMessageStartIndex,divCloseMarkFirstIndex);
            holder.quoteMessageTv.setText(Html.fromHtml(quoteMessage));
            int brMarkSecondIndex = message.indexOf("<br />", divCloseMarkFirstIndex);
            int messageStartIndex = brMarkSecondIndex + brMartLen + 1;
            message = message.substring(messageStartIndex);

        }else {
            holder.quoteContainerRl.setVisibility(View.GONE);
        }
        holder.messageTv.setText(Html.fromHtml(message));

        //帖子图片
        if (post.getImagelist() != null && post.getImagelist().size() > 0) {

            holder.imagesContainerLl.setVisibility(View.VISIBLE);
            int chindViewCount = holder.imagesContainerLl.getChildCount();
            if (chindViewCount > 0) {
                holder.imagesContainerLl.removeAllViews();
            }

            int imgListCount = post.getImagelist().size();
            for (int i = 0; i < imgListCount; i++) {

                String aid = post.getImagelist().get(i);
                Attachment attachment = post.getAttachments().get(aid);
                String url = attachment.getUrl();
                String path = attachment.getAttachment();
                String absolutePath = ConstantURL.BBS_URL + url + path;
                /**
                 * 动态添加ImageView
                 <ImageView
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:layout_marginTop="@dimen/forum_item_padding"
                 android:src="@drawable/aaa"/>
                 */

                ImageView img = new ImageView(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 0);
                img.setLayoutParams(params);

                //TODO:修改Constant.ALBUM_OPTIONS
                ImageLoader.getInstance().displayImage(absolutePath, img, Constant.ALBUM_OPTIONS);
                holder.imagesContainerLl.addView(img);
            }

        }else {
            holder.imagesContainerLl.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {

        FrameLayout containerFl;
        View headDividerView;
        View dividerView;
        ImageView avatarImg; //发布者头像
        TextView authorTV; //发布者名称
        TextView datelineTv; //发布时间
        TextView repliesTv; //主贴回复数
        ImageView postIconImg;//楼主所在楼层的回帖图标
        ImageView iconImg;//帖子有图标示
        ImageView digestIconImg;//精华帖标示
        ImageView hotIconImg;//热帖标示
        TextView messageTv; //帖子正文

        RelativeLayout quoteContainerRl;//引用的容器
        TextView quoteAuthorTv;//引用的发布者名称
        TextView quoteMessageTv;//引用的发布内容

        LinearLayout imagesContainerLl; //帖子图片容器


    }
}