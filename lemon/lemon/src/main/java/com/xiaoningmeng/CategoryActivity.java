package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.adapter.CategoryAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class CategoryActivity extends BaseActivity implements PlayObserver {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private List<IRecyclerItem> mCategoryDatas;
    private ImageView mWaveImg;
    private View notDataView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);
        setTitleName("全部分类");
        mContext = this;
        mCategoryDatas = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
        initAdapter();
        mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
        requestCategoryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
        MobclickAgent.onEvent(this, "event_category");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
        PlayWaveManager.getInstance().mContext = null;
    }


    public void initAdapter() {

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter = new CategoryAdapter(this, mCategoryDatas);
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
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(mContext, R.dimen.page_offset, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {


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
                                Logger.d("ageItemLinkurl = " + ageItemLinkurl);
                                Intent ageLevelIntent = new Intent();
                                ageLevelIntent.putExtra("pageTitle", "最新上架");
                                Uri uri = Uri.parse(ageItemLinkurl);
                                ageLevelIntent.setData(uri);
                                CategoryActivity.this.startActivity(ageLevelIntent);

                                //记数统计
                                HashMap<String, String> ageMap = new HashMap<String, String>();
                                ageMap.put("ageTitle", ageItem.getTitle());
                                MobclickAgent.onEvent(CategoryActivity.this, "event_age_level", ageMap);

                                //TODO 打开年龄页面
                                break;
                            case Category.TYPE_TAG:
                                Category.TagBean.TagItemsBean.ChildItemsBean tagItem = (Category.TagBean.TagItemsBean.ChildItemsBean) mAdapter.getItem(position);
                                Uri tagItemLinkuri = Uri.parse(tagItem.getLinkurl());
                                Intent intent = new Intent();
                                intent.setData(tagItemLinkuri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                CategoryActivity.this.startActivity(intent);

                                //计数统计
                                HashMap<String, String> tagMap = new HashMap<String, String>();
                                tagMap.put("tagId", tagItem.getId());
                                tagMap.put("tagName", tagItem.getName());
                                MobclickAgent.onEvent(CategoryActivity.this, "event_click_tag", tagMap);
                        }
                    }
                }
        );
    }

    private void requestCategoryData() {

        LHttpRequest.GetCategoryRequest getCategoryRequest = mRetrofit.create(LHttpRequest.GetCategoryRequest.class);
        Call<JsonResponse<Category>> call = getCategoryRequest.getResult();
        call.enqueue(new Callback<JsonResponse<Category>>() {

            @Override
            public void onResponse(Call<JsonResponse<Category>> call, Response<JsonResponse<Category>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {
                    Category data = response.body().getData();
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
                        mAdapter.loadMoreEnd();

                    } else {
                        TextView emptyTip = (TextView) notDataView.findViewById(R.id.tv_empty_tip);
                        emptyTip.setText(getString(R.string.empty_tip));
                        mAdapter.setEmptyView(notDataView);
                    }
                } else {

                    Logger.e(response.toString());
                    Toast.makeText(CategoryActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                    mAdapter.loadMoreFail();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<Category>> call, Throwable t) {

                Logger.e(t.toString());
                Toast.makeText(CategoryActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                mAdapter.loadMoreFail();
            }
        });
    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);
    }


    //http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mPageOffset;
        private int mItemOffset;
        private int mSpanCount = 2;
        private HashMap<Integer, Integer> tagSecctionMap = new HashMap<Integer, Integer>();
        private int lastSectionPos;


        public ItemOffsetDecoration(int pageOffset, int itemOffset) {

            mPageOffset = pageOffset;
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int pageOffsetId, @DimenRes int itemOffsetId) {

            this(mContext.getResources().getDimensionPixelSize(pageOffsetId), mContext.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            super.getItemOffsets(outRect, view, parent, state);
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;
            int pos = parent.getChildAdapterPosition(view);
            int viewType = mAdapter.getItemViewType(pos);

            switch (viewType) {

                case Category.TYPE_SECTION:

                    lastSectionPos = pos;
                    break;

                case Category.TYPE_TAG: {

                    int relativePos = 0;
                    if (pos > lastSectionPos && !tagSecctionMap.containsKey(pos)) {
                        tagSecctionMap.put(pos, lastSectionPos);
                    } else {
                        lastSectionPos = tagSecctionMap.get(pos);
                    }
                    relativePos = pos - lastSectionPos;
                    if (relativePos % mSpanCount == 0) {
                        left = mItemOffset;
                        right = mPageOffset;
                    } else {
                        left = mPageOffset;
                        right = mItemOffset;
                    }
                    outRect.set(left, top, right, bottom);
                    break;
                }
            }
        }
    }
}
