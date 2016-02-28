package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.ImageViewerPagerActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.ViewThreadActivity;
import com.xiaoningmeng.bean.Attachment;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.Post;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.utils.PostImageUtils;
import com.xiaoningmeng.utils.UiUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewThreadAdapter extends BaseAdapter {

    private static final String KEY_QUOTE_IN_ORIGINAL = "quote";
    private static final String KEY_MESSAGE_IN_ORIGINAL = "message";
    private static final String KEY_AUTHOR_IN_ORIGINAL = "author";

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Post> posts;
    private ArrayList<String> imagesUrl;
    public ForumThread forumThread;
    private WeakReference<ViewThreadActivity> weak;

    public ViewThreadAdapter(Context context,ForumThread forumThread, List<Post> posts) {

        this.weak = new WeakReference<ViewThreadActivity>((ViewThreadActivity)context);
        mContext = context;
        this.forumThread = forumThread;
        this.posts = posts;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imagesUrl = new ArrayList<String>();
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
            holder.ThreadAuthorIcontTv = (TextView) convertView.findViewById(R.id.tv_thread_author_icon);
            holder.datelineTv = (TextView) convertView
                    .findViewById(R.id.tv_dateline);
            holder.repliesTv = (TextView) convertView
                    .findViewById(R.id.tv_replies);
            holder.postIconImg = (ImageView) convertView.findViewById(R.id.iv_post_icon);
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

        Post post = posts.get(position);
        //TODO:avatartime是要保持一个定值，只有更新头像才变一次,如果每次请求都是新值，cdn的cache就用不上了,每次都会去取源，不够快，费用也会高
        String authorid = post.getAuthorid();
        String avatarTime = String.valueOf(post.getDbdateline());
        String avatarUrl = AvatarUtils.getAvatarUrl(authorid, avatarTime, 120);
        Uri avatarUri = Uri.parse(avatarUrl);
        holder.avatarImg.setImageURI(avatarUri);
        String original = post.getMessage();
        final HashMap quoteMessage = this.separateQuoteWithMessage(original);
        String author = (String) quoteMessage.get("author");
        String quote = (String) quoteMessage.get("quote");
        String message = (String) quoteMessage.get("message");
        holder.authorTV.setText(post.getAuthor());
        holder.datelineTv.setText(Html.fromHtml(post.getDateline()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ViewThreadActivity activity = weak.get();
                boolean isKeyboardVisible = UiUtils.isKeyboardShown(activity.getWindow().getDecorView().getRootView());
                if(!isKeyboardVisible) {

                    activity.repPid = Integer.parseInt(posts.get(position).getPid());
                    activity.repPost = Integer.parseInt(posts.get(position).getPid());
                    /**
                     * 沿用DZ的规则
                     * noticetrimstr:[quote][size=2][url=forum.php?mod=redirect&goto=findpost&pid=80&ptid=32][color=#999999]mT 发表于 2016-2-15 14:10[/color][/url][/size]
                     aaaaa[/quote]
                     *
                     */
                    activity.noticeTrimStr = String.format("[quote][size=2][url=forum.php?mod=redirect&goto=findpost&pid=%s&ptid=%s][color=#999999]%s 发表于 %s[/color][/url][/size]\n" +
                                    "%s[/quote]",
                            posts.get(position).getPid(),
                            posts.get(position).getTid(),
                            posts.get(position).getAuthor(),
                            posts.get(position).getDateline(),
                            (String) quoteMessage.get("message"));

                    String hint = String.format("回复%s:",posts.get(position).getAuthor());
                    activity.keyBoardfragment.setmEditEmojiconHint(hint);
                    activity.keyBoardfragment.showKeyboard();

                } else {

                    activity.repPid = 0;
                    activity.repPost = 0;
                    activity.noticeTrimStr = null;
                    activity.keyBoardfragment.resetKeyboard();
                }
            }
        });

        //楼主图标
        if (post.getAuthorid().equals(forumThread.getAuthorid())) {
            holder.ThreadAuthorIcontTv.setVisibility(View.VISIBLE);
        }else {
            holder.ThreadAuthorIcontTv.setVisibility(View.GONE);
        }

        if (post.getFirst().equals("1") && post.getPosition().equals("1")) {

            //楼主显示回帖数,回帖图标
            holder.postIconImg.setVisibility(View.VISIBLE);
            if (!forumThread.getReplies().equals("0")) {
                holder.repliesTv.setText(forumThread.getReplies());
            }
        }else {
            //回帖隐藏回帖数,回帖图标,显示楼层
            holder.postIconImg.setVisibility(View.GONE);
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

        if(quote != null && !quote.equals("")) {

            holder.quoteContainerRl.setVisibility(View.VISIBLE);
            holder.quoteAuthorTv.setText(author);
            holder.quoteMessageTv.setText(Html.fromHtml(quote));
        }else {

            holder.quoteContainerRl.setVisibility(View.GONE);
        }
        holder.messageTv.setText(Html.fromHtml(message));

        //帖子图片
        if (post.getImagelist() != null && post.getImagelist().size() > 0) {

            holder.imagesContainerLl.setVisibility(View.VISIBLE);
            imagesUrl.clear();
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
                Uri imgUri = Uri.parse(absolutePath);
                imagesUrl.add(absolutePath);
                /**
                 * 动态添加ImageView
                 <ImageView
                 android:layout_width="match_parent"
                 android:layout_height="match_parent"
                 android:layout_marginTop="@dimen/forum_item_padding"
                 android:src="@drawable/aaa"/>
                 */
                ViewThreadActivity activity = weak.get();
                HashMap<String,Integer> imageSize = PostImageUtils.parseImageSizeWithUrl(activity, absolutePath);

                SimpleDraweeView img = new SimpleDraweeView(mContext);
                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize.get("widthPx"),imageSize.get("heightPx"));
                params.setMargins(0, 8, 0, 0);
                params.gravity = Gravity.CENTER;
                img.setLayoutParams(params);
                img.setBackgroundResource(R.color.view_thread_image_background_color);
                img.setImageURI(imgUri);
                holder.imagesContainerLl.addView(img);
                img.setTag(i+"");
                img.setOnClickListener(postImageClickListener);
            }

        }else {
            holder.imagesContainerLl.setVisibility(View.GONE);
        }

        return convertView;
    }

    private HashMap separateQuoteWithMessage(String original) {

        HashMap<String , String> result = new HashMap<String , String>();
        result.put(KEY_QUOTE_IN_ORIGINAL,null);
        result.put(KEY_MESSAGE_IN_ORIGINAL,original);
        result.put(KEY_AUTHOR_IN_ORIGINAL,null);

        int divMarkIndex = original.indexOf("<div class=\"reply_wrap\">");
        if (divMarkIndex != -1) {

            int fontMarkStartIndex = original.indexOf("<font color=\"#999999\">");
            int fontMartStartLen = "<font color=\"#999999\">".length();
            int fontMarkEndIndex = original.indexOf("</font>");
            String quoteAuthorAndTime =  original.substring(fontMarkStartIndex + fontMartStartLen, fontMarkEndIndex);
            int blandInQuoteAuthorAndTimeIndex = quoteAuthorAndTime.indexOf(" ");
            String quoteAuthor = quoteAuthorAndTime.substring(0, blandInQuoteAuthorAndTimeIndex);
            result.put(KEY_AUTHOR_IN_ORIGINAL, quoteAuthor);

            int brMartFirstIndex = original.indexOf("<br />");
            int brMartLen = "<br />".length();
            int divCloseMarkFirstIndex = original.indexOf("</div>");
            int quoteMessageStartIndex = brMartFirstIndex + brMartLen + 1;
            String quoteMessage = original.substring(quoteMessageStartIndex,divCloseMarkFirstIndex);
            result.put(KEY_QUOTE_IN_ORIGINAL, quoteMessage);

            int brMarkSecondIndex = original.indexOf("<br />", divCloseMarkFirstIndex);
            int messageStartIndex = brMarkSecondIndex + brMartLen + 1;
            String message = original.substring(messageStartIndex);
            result.put(KEY_MESSAGE_IN_ORIGINAL,message);
        }

        return result;
    }

    View.OnClickListener postImageClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            final ViewThreadActivity activity = weak.get();
            int position = Integer.parseInt(v.getTag().toString());
            Intent i = new Intent(mContext,ImageViewerPagerActivity.class);
            i.putExtra("position",position);
            i.putExtra("imagesUrl",imagesUrl);
            activity.startActivityForNew(i);
        }
    };

    static class ViewHolder {

        FrameLayout containerFl;
        View headDividerView;
        View dividerView;
        ImageView avatarImg; //发布者头像
        TextView authorTV; //发布者名称
        TextView ThreadAuthorIcontTv;//楼主图标
        TextView datelineTv; //发布时间
        TextView repliesTv; //主贴回复数
        ImageView postIconImg;//楼主所在楼层的回帖图标
        TextView messageTv; //帖子正文

        RelativeLayout quoteContainerRl;//引用的容器
        TextView quoteAuthorTv;//引用的发布者名称
        TextView quoteMessageTv;//引用的发布内容
        LinearLayout imagesContainerLl; //帖子图片容器
    }
}