package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.trade.TradeConstants;
import com.alibaba.sdk.android.trade.TradeService;
import com.alibaba.sdk.android.trade.callback.TradeProcessCallback;
import com.alibaba.sdk.android.trade.model.TaokeParams;
import com.alibaba.sdk.android.trade.model.TradeResult;
import com.alibaba.sdk.android.trade.page.Page;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.adapter.IndexAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.manager.DownloadApkManager;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.presenter.DiscoverPresenter;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoverFragment extends BaseFragment implements DiscoverConstract.View<DiscoverPresenter> {

    private ConvenientBanner<Index.FocusPicBean.ItemsBean> convenientBanner;// 顶部广告栏控件
    private RecyclerView mRecyclerView;
    private List<IRecyclerItem> mIndexDatas;
    private DiscoverPresenter mPresenter;
    private IndexAdapter mAdapter;
    private EmptyHelper mEmptyHelper;

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
        new DiscoverPresenter(getActivity(), this).subscribe();
        mPresenter.requestIndexData();

        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                IRecyclerItem iRecyclerItem = mAdapter.getItem(position);
                switch (iRecyclerItem.getItemType()) {
                    case Index.ALBUM_TYPE:
                        startAlbumInfoActivity(view, (AlbumInfo) iRecyclerItem);
                        break;
                    case Index.ALBUM_MORE_TYPE:
                        /*Index.AlbumSectionBean.ItemBean albumItemBean =
                                (Index.AlbumSectionBean.ItemBean) iRecyclerItem;
						startMoreActivity(1);*/
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
                    String linkUrl = data.getLinkurl();
                    //http里面有apk的下载,暂时不做改动
                    if (linkUrl != null) {
                        if (linkUrl.startsWith("http:") || linkUrl.startsWith("https:")) {
                            if (linkUrl.endsWith(".apk")) {
                                DownloadApkManager.getInstance().showDownloadDialog(getActivity(), linkUrl);
                            } else if (linkUrl.contains("taobao")) {
                                showTaobaoPage(linkUrl);
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
        if (convenientBanner != null)
            convenientBanner.startTurning(5000);
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_discover");
        }
        super.onResume();
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

        Intent intent = new Intent(getActivity(), AblumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getAlbumid());
        intent.putExtra("albumInfo", albumInfo);
        ((BaseActivity) getActivity()).startShareTransitionActivity(intent, view, "albumImage");
    }

    /*public void showLoadingTip() {
        if(mListView.getHeaderViewsCount() == 1){
            if(loadingView == null){
                loadingView = View.inflate(getActivity(),R.layout.fragment_loading, null);
                loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_banner_height), 0, 0);
            }
            mListView.addHeaderView(loadingView,null,false);
        }
    }

    public void hideLoadingTip() {

        if(loadingView != null && mListView.getHeaderViewsCount() > 1){
            mListView.removeHeaderView(loadingView);
        }
    }


    TextView emptyView;
    public void showEmptyTip() {
        if(mListView.getHeaderViewsCount() == 1 && getActivity() != null){
            if(emptyView == null && getActivity() != null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 if (inflater != null) {
                     emptyView = (TextView)inflater.inflate(R.layout.fragment_empty, null);
                 }
            }
            if(emptyView != null){
                emptyView.setText(getString(R.string.failure_tip));
                emptyView.setClickable(true);
                mListView.addHeaderView(emptyView,null,false);
                emptyView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    hideEmptyTip();
                    requestData();
                }
            });
            }
        }
    }

    public void hideEmptyTip() {
        if(emptyView != null && mListView.getHeaderViewsCount() > 1){
            emptyView.setClickable(false);
            mListView.removeHeaderView(emptyView);
        }
    }
*/

    public void showTaobaoPage(String url) {
        Map<String, Object> exParams = new HashMap<String, Object>();
        exParams.put(TradeConstants.ISV_CODE, "xiaoningmeng");
        Page page = new Page(url, exParams);
        TaokeParams taokeParams = new TaokeParams();
        taokeParams.pid = Constant.DEFAULT_TAOKE_PID;
        AlibabaSDK.getService(TradeService.class).show(page, taokeParams, getActivity(), null, new TradeProcessCallback() {
            @Override
            public void onPaySuccess(TradeResult tradeResult) {
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

}
