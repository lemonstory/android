package com.xiaoningmeng;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.adapter.CategoryAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends BaseActivity implements PlayObserver {
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private EmptyHelper mEmptyHelper;
    private List<IRecyclerItem> mCategoryDatas;
    private ImageView mWaveImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setTitleName("全部分类");
        mCategoryDatas = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.play_flag_wave_01);
        initAdapter();
        requestCategoryData();
    }


    public void initAdapter() {

        mAdapter = new CategoryAdapter(this, mCategoryDatas);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(this, mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
        View footerView = this.getFooterView();
        mAdapter.addFooterView(footerView, 0);
        int spanCount = 4;
        GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(manager);
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return mCategoryDatas.get(position).getSpanSize();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(18));
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {


                    }

                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


                    }

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemClick(adapter, view, position);
                        int viewType = mAdapter.getItemViewType(position);
                        switch (viewType) {
                            case Category.TYPE_AGE_LEVEL:
                                Category.AgeLevelBean.AgeItemsBean ageItem = (Category.AgeLevelBean.AgeItemsBean) mAdapter.getItem(position);
                                String ageItemLinkurl = ageItem.getLinkurl();
                                DebugUtils.d("ageItemLinkurl = " + ageItemLinkurl);
                                Intent ageLevelIntent = new Intent();
                                ageLevelIntent.putExtra("pageTitle","最新上架");
                                Uri uri = Uri.parse(ageItemLinkurl);
                                ageLevelIntent.setData(uri);
                                CategoryActivity.this.startActivity(ageLevelIntent);
                                //TODO 打开年龄页面
                                break;
                            case Category.TYPE_TAG:
                                Category.TagBean.TagItemsBean.ChildItemsBean tagItem = (Category.TagBean.TagItemsBean.ChildItemsBean) mAdapter.getItem(position);
                                Uri tagItemLinkuri = Uri.parse(tagItem.getLinkurl());
                                Intent intent = new Intent();
                                intent.setData(tagItemLinkuri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                CategoryActivity.this.startActivity(intent);
                        }

                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
        PlayWaveManager.getInstance().mContext = null;
    }

    private void requestCategoryData() {
        LHttpRequest.getInstance().categoryReq(this,
                MyApplication.getInstance().getUid(),
                new JsonCallback<Category>() {

                    @Override
                    public void onGetDataSuccess(Category data) {

                        if (data != null) {
                            if (data.getAge_level().getTotal() != null && Integer.parseInt(data.getAge_level().getTotal()) > 0) {
                                List<Category.AgeLevelBean.AgeItemsBean> ageItemsList = data.getAge_level().getItems();
                                if (ageItemsList != null && ageItemsList.size() > 0) {
                                    mCategoryDatas.addAll(ageItemsList);
                                }
                            }

                            if (data.getTag().getTotal() != null && Integer.parseInt(data.getTag().getTotal()) > 0) {
                                List<Category.TagBean.TagItemsBean> tagItemsList = data.getTag().getItems();
                                if (tagItemsList != null && tagItemsList.size() > 0) {
                                    for (int i = 0; i < tagItemsList.size(); i++) {
                                        Category.TagBean.TagItemsBean itemBean = tagItemsList.get(i);
                                        mCategoryDatas.add(itemBean);
                                        List<Category.TagBean.TagItemsBean.ChildItemsBean> tagChildItemsList = itemBean.getChild_items();
                                        if (tagChildItemsList != null && tagChildItemsList.size() > 0) {
                                            mCategoryDatas.addAll(tagChildItemsList);
                                        }
                                    }
                                }
                            }
                            mAdapter.setNewData(mCategoryDatas);

                        } else {
                            mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_tip));
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, String failureResponse) {
                        Toast.makeText(CategoryActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;
        HashMap<Integer, Integer> tagSecctionMap = new HashMap<Integer, Integer>();
        int lastSectionPos;
        int spanCount = 2;

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            int pos = parent.getChildAdapterPosition(view);
            int viewType = mAdapter.getItemViewType(pos);

            if (viewType == Category.TYPE_SECTION) {
                lastSectionPos = pos;
            }

            if (viewType == Category.TYPE_TAG) {
                int relativePos = 0;
                if (pos > lastSectionPos && !tagSecctionMap.containsKey(pos)) {
                    tagSecctionMap.put(pos, lastSectionPos);
                } else {
                    lastSectionPos = tagSecctionMap.get(pos);
                }
                relativePos = pos - lastSectionPos;
                outRect.right = mSpace;
                outRect.top = 0;
                outRect.bottom = 0;
                if (relativePos % spanCount == 0) {
                    outRect.left = 0;
                } else {
                    outRect.left = mSpace;
                }
            }
        }
    }
}
