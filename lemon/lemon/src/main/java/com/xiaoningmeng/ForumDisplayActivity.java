package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.adapter.ForumDisplayAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ForumLoginVar;
import com.xiaoningmeng.bean.ForumNotice;
import com.xiaoningmeng.bean.ForumThread;
import com.xiaoningmeng.event.ForumLoginEvent;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.http.JsonForumResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.view.BadgeView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class ForumDisplayActivity extends BaseActivity implements XListView.IXListViewListener {

  private Context mContext;
  private ViewGroup loadingView;
  private XListView mListView;
  private ForumDisplayAdapter mAdapter;
  private List<ForumThread> mForumThreads = new ArrayList<>();
  private String tip = null;
  private View pbEmptyTip;
  private int fid; //论坛ID
  private int page; //当前页码
  private int maxPage;//最大页码
  private ImageView imgHeadRight;
  private ImageView addThreadIv;
  private String title;
  private String hash;
  private String formHash;
  private BadgeView badge;
  private ForumNotice notice;
  private String newMyPost;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    Fresco.initialize(this);
    setContentView(R.layout.activity_forum_display);
    mContext = this;
    newMyPost = getIntent().getStringExtra("newmypost");
    fid = getIntent().getIntExtra("fid", 1);
    page = getIntent().getIntExtra("page", 1);
    title = getIntent().getStringExtra("name");
    initView();
    mAdapter = new ForumDisplayAdapter(this, mForumThreads, fid);
    mAdapter.showLastPostTime = true;
    mListView.setAdapter(mAdapter);
    mListView.autoRefresh();
    EventBus.getDefault().register(this);
  }

  public void initView() {

    mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
    mListView.setXListViewListener(this);
    mListView.setPullLoadEnable(false);
    //autoRefresh会自动调用onRefresh()方法
    //去掉Oncreate中的requestForumThreadsData调用
    loadingView = (ViewGroup) findViewById(R.id.rl_loading);
    loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
    loadingView.setVisibility(View.GONE);
    pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
    imgHeadRight = (ImageView) findViewById(R.id.img_head_right);
    badge = new BadgeView(this, imgHeadRight);
    setRightHeadIcon(R.drawable.ic_message);
    addThreadIv = (ImageView) findViewById(R.id.iv_add);
    setHeadTitle(title);
    this.page = 1;
    this.maxPage = 1;

    addThreadIv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (UserAuth.getInstance().isLogin(mContext)) {
          Intent i = new Intent(ForumDisplayActivity.this, NewThreadActivity.class);
          i.putExtra("fid", fid);
          i.putExtra("hash", hash);
          i.putExtra("formhash", formHash);
          startActivityForResult(i, 0);
        }
      }
    });

    imgHeadRight.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (badge.isShown()) {
          badge.hide();
        }
        Intent i = new Intent(ForumDisplayActivity.this, MyNotelistActivity.class);
        startActivityForNew(i);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    setBadgeNum(newMyPost);
    MobclickAgent.onEvent(this, "event_show_forumdisplay");
  }

  @Override
  public void onRefresh() {

    ForumDisplayActivity.this.page = 1;
    requestForumThreadsData(fid, page);
    mListView.setPullLoadEnable(false);
  }

  @Override
  public void onLoadMore() {

    if (this.page < this.maxPage) {
      int size = mForumThreads.size();
      if (size > 0) {
        this.page++;
        requestForumThreadsData(fid, page);
      } else {
        this.page = 1;
        requestForumThreadsData(fid, page);
      }
    } else {
      mListView.stopLoadMore();
    }
  }

  private void onLoad() {

    if (this.page < this.maxPage) {
      mListView.setPullLoadEnable(true);
      mListView.setFootViewNoMore(false);
    } else {
      mListView.setFootViewNoMore(true);
    }
    mListView.stopRefresh();
    mListView.stopLoadMore();
  }

  public void setForumsThreads(List<ForumThread> threads) {
    if (threads != null && threads.size() > 0) {

      if (this.page == 1) {
        this.mForumThreads.clear();
      }
      this.mForumThreads.addAll(threads);
      if (mListView != null) {
        hideEmptyTip();
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  private void hideEmptyTip() {
  }

  public void reRequestLoading() {
    mListView.autoRefresh();
  }

  //badgeView 会对img_head_right做修改,所以需要对tv_head_title重新做下处理
  public void setHeadTitle(String title) {

    ViewParent parent = imgHeadRight.getParent();
    ViewGroup group = (ViewGroup) parent;
    group.setId(R.id.forum_display_badge_container_id);
    TextView headTitleView = (TextView) findViewById(R.id.tv_head_title);
    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
    lp.addRule(RelativeLayout.LEFT_OF, R.id.forum_display_badge_container_id);
    lp.addRule(RelativeLayout.RIGHT_OF, R.id.img_home_back);
    headTitleView.setLayoutParams(lp);
    headTitleView.setText(title);
  }

  public void setBadgeNum(String newMyPost) {

    if (UserAuth.getInstance().isLogin(this) && MyApplication.getInstance().userInfo != null) {

      imgHeadRight.setVisibility(View.VISIBLE);
      if (null != newMyPost) {
        int newMyPostInt = Integer.parseInt(newMyPost);
        if (newMyPostInt > 0) {
          badge.setText(newMyPost);
          badge.show();
        } else {
          badge.hide();
        }
      }
    } else {
      imgHeadRight.setVisibility(View.INVISIBLE);
    }
  }

  private void requestForumThreadsData(int fid, int page) {

    LHttpRequest.GetForumThreadsRequest getForumThreadsRequest = mRetrofit.create(LHttpRequest.GetForumThreadsRequest.class);
    Call<JsonForumResponse<JsonObject>> call = getForumThreadsRequest.getResult(ConstantURL.FORUM_INDEX, "forumdisplay", fid, page);
    call.enqueue(new Callback<JsonForumResponse<JsonObject>>() {

      @Override
      public void onResponse(Call<JsonForumResponse<JsonObject>> call, Response<JsonForumResponse<JsonObject>> response) {

        loadingView.setVisibility(View.GONE);
        onLoad();
        if (response.isSuccessful() && null != response.body().getVariables()) {

          JsonObject variablesObject = response.body().getVariables();
          Double threadsCount = 0.0;
          Double tpp = 0.0;
          List<ForumThread> threads = new ArrayList<ForumThread>();

          try {

            Gson gson = new Gson();
            if (variablesObject.has("forum_threadlist")) {
              threads = gson.fromJson(variablesObject.get("forum_threadlist"), new TypeToken<List<ForumThread>>() {
              }.getType());
              setForumsThreads(threads);
            }

            if (variablesObject.has("forum") && variablesObject.has("tpp")) {

              JsonObject forumJsonObject = variablesObject.get("forum").getAsJsonObject();
              tpp = Double.parseDouble(variablesObject.get("tpp").getAsString());

              if (forumJsonObject.has("threadcount")) {
                threadsCount = Double.parseDouble(forumJsonObject.get("threadcount").getAsString());
                Double dMaxPage = Math.ceil(threadsCount / tpp);
                ForumDisplayActivity.this.maxPage = dMaxPage.intValue();
              }
            }

            if (variablesObject.has("hash")) {
              hash = variablesObject.get("hash").getAsString();
            }

            if (variablesObject.has("formhash")) {
              formHash = variablesObject.get("formhash").getAsString();
            }

            if (variablesObject.has("notice")) {
              notice = gson.fromJson(variablesObject.get("notice"), ForumNotice.class);
              newMyPost = notice.getNewmypost();
              setBadgeNum(newMyPost);
            }
          } catch (JsonSyntaxException e) {

            e.printStackTrace();
          }
        } else {
          DebugUtils.e(response.toString());
        }
      }

      @Override
      public void onFailure(Call<JsonForumResponse<JsonObject>> call, Throwable t) {

        DebugUtils.e(t.toString());
        onLoad();
        if (loadingView != null) {
          loadingView.setVisibility(View.VISIBLE);
          ((TextView) loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
          loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
          loadingView.setClickable(true);
          loadingView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              reRequestLoading();
              ForumDisplayActivity.this.requestForumThreadsData(ForumDisplayActivity.this.fid, ForumDisplayActivity.this.page);
            }
          });
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 0) {
      if (resultCode == RESULT_OK) {
        mListView.autoRefresh();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void onEventMainThread(ForumLoginEvent event) {

    ForumLoginVar forumLoginVar = event.forumLoginVar;
    formHash = forumLoginVar.getFormhash();
  }

  @Override
  public void onDestroy() {

    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }
}
