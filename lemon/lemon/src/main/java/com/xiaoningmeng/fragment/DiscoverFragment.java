package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
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
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.adapter.IndexAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.manager.DownloadApkManager;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.presenter.DiscoverPresenter;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;
import com.xiaoningmeng.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscoverFragment extends BaseFragment implements DiscoverConstract.View<DiscoverPresenter> {

    private ConvenientBanner<Index.FocusPicBean.ItemsBean> convenientBanner;// 顶部广告栏控件
    private RecyclerView mRecyclerView;
    private List<IRecyclerItem> mIndexDatas;
    private DiscoverPresenter mPresenter;
    private IndexAdapter mAdapter;
    private EmptyHelper mEmptyHelper;
    private Boolean albumClickable;
    private boolean isAlbumLeft = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = View.inflate(getActivity(),
                R.layout.fragment_discover, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mIndexDatas = new ArrayList<>();
        mAdapter = new IndexAdapter(mIndexDatas);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
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
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(18));
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
                        /*Index.AlbumSectionBean.ItemBean albumItemBean =
                                (Index.AlbumSectionBean.ItemBean) iRecyclerItem;
						startMoreActivity(1);*/
                        break;

                    case Index.CATEGORY_TYPE:
                        Index.ContentCategoryBean.ItemBean categoryInfo = (Index.ContentCategoryBean.ItemBean) iRecyclerItem;
                        Uri linkUri = Uri.parse(categoryInfo.getLinkurl());
                        linkUri.getPath();
                        Intent intent = new Intent();
                        intent.setData(linkUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        DiscoverFragment.this.getActivity().startActivity(intent);
                        break;

                }
            }
        });
        return contentView;
    }

    private void startMoreActivity(int type) {
        Intent i = new Intent(getActivity(), MoreActivity.class);
        i.putExtra(MoreActivity.MORE_TYPE, type);
        getBaseActivity().startActivityForNew(i);
    }


    @Override
    public void setPresenter(DiscoverPresenter presenter) {
        this.mPresenter = presenter;
    }

    public class ImageHolder implements Holder<Index.FocusPicBean.ItemsBean> {
        private SimpleDraweeView imageView;

        @Override
        public View createView(Context context) {
            imageView = new SimpleDraweeView(context);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(final Context context, final int position, final Index.FocusPicBean.ItemsBean data) {

            final Uri coverUri = Uri.parse(data.getCover());
            imageView.setImageURI(coverUri);
            imageView.setBackgroundResource(R.color.home_discover_focus_picture_bg);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    albumClickable = true;
                    String linkUrl = data.getLinkurl();
                    //http里面有apk的下载,暂时不做改动
                    if (linkUrl != null) {
                        if (linkUrl.startsWith("http:") || linkUrl.startsWith("https:")) {
                            if (linkUrl.endsWith(".apk")) {
                                DownloadApkManager.getInstance().showDownloadDialog(getActivity(), linkUrl);
                            } else if (linkUrl.contains("taobao")) {
                                AppUtils.showTaobaoPage(getActivity(), linkUrl);
                            } else {
                                WebViewActivity.openWebView(context, data.getLinkurl());
                            }
                        } else if (linkUrl.startsWith("xnm:")) {
                            Uri linkUri = Uri.parse(linkUrl);
                            Intent intent = new Intent();
                            intent.setData(linkUri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
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
    public void requestBannderSuccess(Index.FocusPicBean focusPicBean) {
        if (focusPicBean != null && focusPicBean.getItems().size() != 0) {
            convenientBanner = new ConvenientBanner<>(getActivity());
            convenientBanner.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.home_banner_height)));
            mAdapter.addHeaderView(convenientBanner);
            convenientBanner.setPages(
                    new CBViewHolderCreator<ImageHolder>() {
                        @Override
                        public ImageHolder createHolder() {
                            return new ImageHolder();
                        }
                    }, focusPicBean.getItems())
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
        }

    }

    public void startAlbumInfoActivity(View view, AlbumInfo albumInfo) {

        Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getAlbumid());
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
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;
        int lastSectionPos;
        int spanCount = 2;
        HashMap<Integer, Integer> tagSecctionMap = new HashMap<Integer, Integer>();

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            int viewType = mAdapter.getItemViewType(pos);
            switch (viewType) {

                case Index.ALBUM_MORE_TYPE: {
                    lastSectionPos = pos;
                    break;
                }

                case Index.ALBUM_TYPE: {

                    outRect.right = mSpace;
                    outRect.top = 0;
                    outRect.bottom = 0;
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
                break;

                default:
                    break;
            }
        }
    }

}


