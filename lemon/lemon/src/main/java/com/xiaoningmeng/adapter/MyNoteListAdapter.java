package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoningmeng.MyNotelistActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.ViewThreadActivity;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.bean.NoteList;
import com.xiaoningmeng.bean.NoteVar;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.utils.UiUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyNoteListAdapter extends BaseAdapter {

    private static final String KEY_QUOTE_IN_ORIGINAL = "quote";
    private static final String KEY_MESSAGE_IN_ORIGINAL = "message";
    private static final String KEY_AUTHOR_IN_ORIGINAL = "author";

    private LayoutInflater mInflater;
    private Context mContext;
    private List<NoteList> mNoteList;
    private ArrayList<String> imagesUrl;
    public ForumThread forumThread;
    private WeakReference<MyNotelistActivity> weak;

    public MyNoteListAdapter(Context context,List<NoteList> noteList) {

        this.weak = new WeakReference<MyNotelistActivity>((MyNotelistActivity)context);
        mContext = context;
        this.forumThread = forumThread;
        this.mNoteList = noteList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imagesUrl = new ArrayList<String>();
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
    }

    @Override
    public int getCount() {
        return mNoteList.size();
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
            holder.quoteTextTv = (TextView) convertView.findViewById(R.id.tv_quote_text);
            holder.quoteAuthorTv = (TextView) convertView.findViewById(R.id.tv_quote_author);
            holder.quoteMessageTv = (TextView) convertView.findViewById(R.id.tv_quote_message);
            holder.imagesContainerLl = (LinearLayout) convertView.findViewById(R.id.ll_images_container);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NoteList noteList = mNoteList.get(position);
        //TODO:avatartime是要保持一个定值，只有更新头像才变一次,如果每次请求都是新值，cdn的cache就用不上了,每次都会去取源，不够快，费用也会高
        String authorid = noteList.getAuthorid();
        String avatarTime = noteList.getDbdateline();
        String avatarUrl = AvatarUtils.getAvatarUrl(authorid, avatarTime, 120);
        Uri avatarUri = Uri.parse(avatarUrl);
        holder.avatarImg.setImageURI(avatarUri);
        String original = noteList.getMessage();
        final NoteVar notevar = noteList.getNotevar();
        final HashMap quoteMessage = this.separateQuoteWithMessage(original);
        String quote = (String) quoteMessage.get("quote");
        String message = (String) quoteMessage.get("message");
        holder.authorTV.setText(noteList.getAuthor());
        holder.datelineTv.setText(Html.fromHtml(noteList.getDateline()));
        holder.repliesTv.setText("回复");
        holder.repliesTv.setTextColor(mContext.getResources().getColor(R.color.orage_color));

        holder.postIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MyNotelistActivity activity = weak.get();
                activity.keyboardFl.setVisibility(View.VISIBLE);
                boolean isKeyboardVisible = UiUtils.isKeyboardShown(activity.getWindow().getDecorView().getRootView());
                if(!isKeyboardVisible) {

                    activity.repPid = Integer.parseInt(noteList.getFrom_id());
                    activity.repPost = Integer.parseInt(noteList.getFrom_id());
                    activity.tid = Integer.parseInt(noteList.getNotevar().getTid());

                            /**
                             * 沿用DZ的规则
                             * noticetrimstr:[quote][size=2][url=forum.php?mod=redirect&goto=findpost&pid=80&ptid=32][color=#999999]mT 发表于 2016-2-15 14:10[/color][/url][/size]
                             aaaaa[/quote]
                             *
                             */
                            String quoteStr = "";
                            if (((String) quoteMessage.get("message")).length() > Constant.FORUM_QUOTE_STRING_LEN) {

                                quoteStr = (String) ((String) quoteMessage.get("message")).substring(0, Constant.FORUM_QUOTE_STRING_LEN) + "...";
                            }else {
                                quoteStr = (String) quoteMessage.get("message");
                            }
                            activity.noticeTrimStr = String.format("[quote][size=2][url=forum.php?mod=redirect&goto=findpost&pid=%s&ptid=%s][color=#999999]%s 发表于 %s[/color][/url][/size]\n" +
                                            "%s[/quote]",
                                    noteList.getFrom_id(),
                                    noteList.getNotevar().getTid(),
                                    noteList.getAuthor(),
                                    noteList.getDateline(),
                                    quoteStr);

                    String hint = String.format("回复%s:",noteList.getAuthor());
                    activity.keyBoardfragment.setmEditEmojiconHint(hint);
                    activity.keyBoardfragment.showKeyboard();
                }
            }
        });


        //点击跳转到指定帖子楼层
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tid = notevar.getTid();
                String pid = notevar.getPid();
                Intent i = new Intent(mContext, ViewThreadActivity.class);
                i.putExtra("tid", Integer.parseInt(tid));
                i.putExtra("pid",Integer.parseInt(pid));
                i.putExtra("page", 1);
                ((MyNotelistActivity) mContext).startActivityForNew(i);


            }
        });

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

            holder.quoteTextTv.setText("回复了你的评论:");
            holder.quoteAuthorTv.setText(Html.fromHtml(quote));
            holder.quoteMessageTv.setVisibility(View.GONE);
        }else {

            holder.quoteTextTv.setText("评论了你的帖子:");
            holder.quoteAuthorTv.setText(Html.fromHtml(notevar.getSubject()));
            holder.quoteMessageTv.setVisibility(View.GONE);
        }
        holder.messageTv.setText(Html.fromHtml(message));
        holder.imagesContainerLl.setVisibility(View.GONE);
        holder.ThreadAuthorIcontTv.setVisibility(View.GONE);
        return convertView;
    }

    private HashMap separateQuoteWithMessage(String original) {

        HashMap<String , String> result = new HashMap<String , String>();
        result.put(KEY_QUOTE_IN_ORIGINAL,null);
        result.put(KEY_MESSAGE_IN_ORIGINAL,original);
        result.put(KEY_AUTHOR_IN_ORIGINAL,null);

        int divMarkIndex = original.indexOf("<div class=\"quote\">");
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
        TextView quoteTextTv;//引用
        TextView quoteAuthorTv;//引用的发布者名称
        TextView quoteMessageTv;//引用的发布内容
        LinearLayout imagesContainerLl; //帖子图片容器
    }
}
