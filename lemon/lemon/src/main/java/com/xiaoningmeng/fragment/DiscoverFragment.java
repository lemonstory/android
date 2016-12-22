package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.AuthorAlbumsActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.IndexAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.presenter.DiscoverPresenter;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;
import com.xiaoningmeng.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoverFragment extends BaseFragment implements DiscoverConstract.View<DiscoverPresenter> {

    private Context mContext;
    private ConvenientBanner<Index.FocusBean.ItemsBean> convenientBanner;// 顶部广告栏控件
    private RecyclerView mRecyclerView;
    private List<IRecyclerItem> mIndexDatas;
    private DiscoverPresenter mPresenter;
    private IndexAdapter mAdapter;
    private EmptyHelper mEmptyHelper;
    private Boolean albumClickable;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = View.inflate(getActivity(), R.layout.fragment_discover, null);
        mContext = this.getActivity();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mIndexDatas = new ArrayList<>();
        mAdapter = new IndexAdapter(mIndexDatas);
        //mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.isFirstOnly(true);
        int spanCount = 4;
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), spanCount);
        mRecyclerView.setLayoutManager(manager);
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return mIndexDatas.get(position).getSpanSize();
            }
        });
        mEmptyHelper = new EmptyHelper(getContext(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, true, getString(R.string.loading_tip));
        mRecyclerView.setAdapter(mAdapter);
        DiscoverFragment.ItemOffsetDecoration itemDecoration = new DiscoverFragment.ItemOffsetDecoration(mContext, R.dimen.page_offset,R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        new DiscoverPresenter(getActivity(), this).subscribe();
        mPresenter.requestIndexData();
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {

            @Override
            public void SimpleOnItemClick(BaseQuickAdapter adapter, View view, int position) {

                IRecyclerItem iRecyclerItem = mAdapter.getItem(position);
                switch (iRecyclerItem.getItemType()) {

                    case Index.ALBUM_TYPE:
                        //增加开关，避免连续点击，Activity跳转动画出现错误
                        if (albumClickable) {
                            AlbumInfo albumInfo = (AlbumInfo) iRecyclerItem;
                            startAlbumInfoActivity(view, albumInfo);
                            albumClickable = false;
                        }
                        break;

                    case Index.ALBUM_MORE_TYPE:
                        break;

                    case Index.CATEGORY_TYPE:
                        Index.CategoryBean.ItemBean categoryInfo = (Index.CategoryBean.ItemBean) iRecyclerItem;
                        Uri categoryLinkUri = Uri.parse(categoryInfo.getLinkurl());
                        Intent intent = new Intent();
                        intent.setData(categoryLinkUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        DiscoverFragment.this.getActivity().startActivity(intent);
                        //点击计数
                        DiscoverFragment.this.tagCountStat(categoryInfo.getTitle(), categoryInfo.getLinkurl());
                        break;

                    case Index.AD_TYPE:
                        Index.AdBean.ItemsBean adInfo = (Index.AdBean.ItemsBean) iRecyclerItem;
                        AppUtils.openLinkUrl(null, DiscoverFragment.this, adInfo.getLinkurl());
                        break;

                    case Index.AUTHOR_TYPE:
                        Index.AuthorItemBean authorItem = (Index.AuthorItemBean) iRecyclerItem;
                        Intent authorIntent = new Intent(DiscoverFragment.this.getActivity(), AuthorAlbumsActivity.class);
                        authorIntent.putExtra("author_uid", authorItem.getUid());
                        authorIntent.putExtra("author", authorItem);
                        startActivityForNew(authorIntent);
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                super.onItemChildClick(adapter, view, position);
                IRecyclerItem iRecyclerItem = mAdapter.getItem(position);
                switch (iRecyclerItem.getItemType()) {
                    case Index.ALBUM_MORE_TYPE:
                        Index.SectionItemBean albumSectionItem = (Index.SectionItemBean) iRecyclerItem;
                        Uri albumSectionItemLinkUri = Uri.parse(albumSectionItem.getLinkurl());
                        Intent moreIntent = new Intent();
                        moreIntent.putExtra("pageTitle", albumSectionItem.getTitle());
                        moreIntent.setData(albumSectionItemLinkUri);
                        DiscoverFragment.this.getActivity().startActivity(moreIntent);
                        break;

                    case Index.AD_TYPE:
                        Index.AdBean.ItemsBean adInfo = (Index.AdBean.ItemsBean) iRecyclerItem;
                        Uri adLinkUri = Uri.parse(adInfo.getLinkurl());
                        Intent adIntent = new Intent();
                        adIntent.setData(adLinkUri);
                        adIntent.addCategory(Constant.DEFAULT_INTENT_CATEGORY);
                        DiscoverFragment.this.getActivity().startActivity(adIntent);
                        break;
                }
            }

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                super.onItemClick(adapter, view, position);
            }
        });
        return contentView;
    }

    @Override
    public void setPresenter(DiscoverPresenter presenter) {
        this.mPresenter = presenter;
    }

    public class ImageHolder implements Holder<Index.FocusBean.ItemsBean> {
        private SimpleDraweeView imageView;

        @Override
        public View createView(Context context) {
            imageView = new SimpleDraweeView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(final Context context, final int position, final Index.FocusBean.ItemsBean data) {

            final Uri coverUri = Uri.parse(data.getCover());
            imageView.setImageURI(coverUri);
            imageView.setBackgroundResource(R.color.home_discover_focus_picture_bg);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    albumClickable = true;
                    String linkUrl = data.getLinkurl();
                    AppUtils.openLinkUrl(null, DiscoverFragment.this, linkUrl);
                }
            });
        }
    }

    @Override
    public void onPause() {
        if (convenientBanner != null)
            convenientBanner.stopTurning();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (convenientBanner != null)
            convenientBanner.startTurning(5000);
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_discover");
        }
        albumClickable = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void requestDataSuccess(List<IRecyclerItem> datas) {
        mAdapter.addData(datas);
    }

    @Override
    public void requestBannderSuccess(Index.FocusBean focusBean) {
        if (focusBean != null && focusBean.getItems().size() != 0) {
            convenientBanner = new ConvenientBanner<>(getActivity());

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.home_banner_height));
            layoutParams.setMargins(0, 40, 0, 0);
            convenientBanner.setLayoutParams(layoutParams);

            mAdapter.addHeaderView(convenientBanner);
            convenientBanner.setPages(
                    new CBViewHolderCreator<ImageHolder>() {
                        @Override
                        public ImageHolder createHolder() {
                            return new ImageHolder();
                        }
                    }, focusBean.getItems())
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
        }

    }

    public void startAlbumInfoActivity(View view, AlbumInfo albumInfo) {

        Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getId());
        intent.putExtra("albumInfo", albumInfo);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                ((BaseActivity) getActivity()),
                new Pair<View, String>(view.findViewById(R.id.img_album_cover), "albumImage")
        );
        Bundle bundle = activityOptions.toBundle();
        startActivity(intent, bundle);
    }

    /**
     * 设置专辑间距
     */
    //http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mPageOffset;
        private int mItemOffset;
        private int mSpanCount = 2;
        private int lastSectionPos;
        private HashMap<Integer, Integer> tagSecctionMap = new HashMap<Integer, Integer>();

        public ItemOffsetDecoration(int pageOffset,int itemOffset) {

            mPageOffset = pageOffset;
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int pageOffsetId, @DimenRes int itemOffsetId) {

            this(mContext.getResources().getDimensionPixelSize(pageOffsetId),mContext.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {

            super.getItemOffsets(outRect, view, parent, state);
            int pos = parent.getChildAdapterPosition(view);
            int viewType = mAdapter.getItemViewType(pos);
            switch (viewType) {
                case Index.ALBUM_MORE_TYPE: {
                    lastSectionPos = pos;
                    break;
                }

                case Index.ALBUM_TYPE: {

                    int left = 0;
                    int right = 0;
                    int top = 0;
                    int bottom = 0;
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

                default:
                    break;
            }
        }
    }

    /**
     * 标签点击计数
     *
     * @param title
     * @param linkurl
     */
    private void tagCountStat(String title, String linkurl) {

        //计数统计
        Uri uri = Uri.parse(linkurl);
        String tagId = uri.getQueryParameter("tag_id");
        if (null != tagId) {
            HashMap<String, String> tagMap = new HashMap<String, String>();
            tagMap.put("tagId", tagId);
            tagMap.put("tagName", title);
            MobclickAgent.onEvent(getActivity(), "event_click_tag", tagMap);
        }
    }
}


