package com.xiaoningmeng;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.adapter.CategoryAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private EmptyHelper mEmptyHelper;
    private List<IRecyclerItem> mCategoryDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mCategoryDatas = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        initAdapter();
        requestCategoryData();
    }


    public void initAdapter() {

        mAdapter = new CategoryAdapter(this, mCategoryDatas);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(this, mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
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
        //mRecyclerView.addItemDecoration(new BaseFragment.SpaceItemDecoration(18));
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

                        DebugUtils.d("AblumSimilarFragment  --> onItemClick RUN! Position = " + position);
                        super.onItemClick(adapter, view, position);
                    }
                }
        );
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
}
