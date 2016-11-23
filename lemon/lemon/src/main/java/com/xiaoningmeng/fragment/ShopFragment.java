package com.xiaoningmeng.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.constants.AlibcConstants;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.baoyz.swipemenu.xlistview.XListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.ShopAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.ShopItem;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShopFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends BaseFragment implements View.OnClickListener, XListView.IXListViewListener {

    private XListView mListView;
    private ArrayList<ShopItem> mItemslist;
    private ArrayList<ShopItem> mPageItemslist;
    private View contentView;
    private ViewGroup loadingView;
    private BaseActivity mContext;
    private View pbEmptyTip;
    private ShopAdapter mShopAdapter;
    private OnFragmentInteractionListener mListener;
    private int page;
    private int pageSize;

    public ShopFragment() {
        // Required empty public constructor
    }

    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_shop, container, false);
        contentView.findViewById(R.id.my_orders_ib).setOnClickListener(this);
        contentView.findViewById(R.id.my_carts_ib).setOnClickListener(this);
        mContext = (BaseActivity) getActivity();
        initView();
        this.mItemslist = new ArrayList<>();
        this.mPageItemslist = new ArrayList<>();
        this.mShopAdapter = new ShopAdapter(getActivity(), this.mItemslist);
        this.mListView.setAdapter(this.mShopAdapter);
        mListView.autoRefresh();
        return contentView;
    }

    public void initView() {

        loadingView = (ViewGroup) contentView.findViewById(R.id.rl_loading);
        loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        pbEmptyTip = loadingView.findViewById(R.id.pb_empty_tip);
        loadingView.setVisibility(View.GONE);
        mListView = (XListView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mListView.setXListViewListener(this);
        mListView.setPullLoadEnable(false);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onEvent(mContext, "event_shop");
    }

    @Override
    public void onRefresh() {

        this.page = 0;
        requestItemListData(page);
        mListView.setPullLoadEnable(false);
    }

    @Override
    public void onLoadMore() {

        if (mPageItemslist.size() > 0) {
            this.page++;
            requestItemListData(page);
        } else {
            mListView.stopLoadMore();
        }
    }

    private void onLoad() {

        if (mPageItemslist.size() > 0) {
            mListView.setPullLoadEnable(true);
            mListView.setFootViewNoMore(false);
        } else {
            mListView.setFootViewNoMore(true);
        }
        mListView.stopRefresh();
        mListView.stopLoadMore();
    }

    public void reRequestLoading() {

        mListView.autoRefresh();
        if (getView() == null) {
            return;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //打开我的订单页
    public void showMyOrdersPage(View view) {

        //实现参考SDK,demo
        AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
        alibcShowParams = new AlibcShowParams(OpenType.Auto, false);
        AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
        alibcTaokeParams = new AlibcTaokeParams(Constant.DEFAULT_TAOKE_PID, "", "");
        Map<String, String> exParams = new HashMap<>(); //yhhpass参数
        exParams.put(AlibcConstants.ISV_CODE, "xiaoningmeng");
        int orderType = 0;//订单页面参数，仅在H5方式下有效
        Boolean isAllOrder = true; //分域显示我的订单，或者全部显示我的订单
        AlibcBasePage alibcBasePage = new AlibcMyOrdersPage(orderType, isAllOrder);
        AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new AlibcTradeCallback() {
            @Override
            public void onTradeSuccess(TradeResult tradeResult) {
                Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity(), "失败 " + i + s,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //打开购物车
    public void showMyCartsPage(View view) {

        //实现参考SDK,demo
        AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
        alibcShowParams = new AlibcShowParams(OpenType.Auto, false);
        AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
        alibcTaokeParams = new AlibcTaokeParams(Constant.DEFAULT_TAOKE_PID, "", "");
        Map<String, String> exParams = new HashMap<>(); //yhhpass参数
        exParams.put(AlibcConstants.ISV_CODE, "xiaoningmeng");


        AlibcBasePage alibcBasePage = new AlibcMyCartsPage();
        AlibcTrade.show(getActivity(), alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new AlibcTradeCallback() {
            @Override
            public void onTradeSuccess(TradeResult tradeResult) {
                Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity(), "失败 " + i + s,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.my_orders_ib:
                showMyOrdersPage(v);
                break;

            case R.id.my_carts_ib:
                showMyCartsPage(v);
                break;

            default:
                break;

        }
    }

    public void setShopItems(ArrayList<ShopItem> shopItems) {

        if (shopItems != null && shopItems.size() > 0) {
            //this.mItemslist.addAll(shopItems);
            if (this.page == 0) {

                this.mItemslist.clear();
            }

            for (ShopItem item : shopItems) {

                int freq = Collections.frequency(this.mItemslist, item);
                if (freq < 1) {
                    this.mItemslist.add(item);
                }
            }

            if (mListView != null) {
                //hideEmptyTip();
                mShopAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setViewTitle(String title) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            homeActivity.mShopTitle = title;
            homeActivity.mTitleTv.setText(homeActivity.mShopTitle);
        }
    }

    private void requestItemListData(final int page) {

        LHttpRequest.getInstance().getShopItems(getActivity(),
                new JsonCallback<String>() {

                    @Override
                    public void onGetDataSuccess(String data) {

                        loadingView.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            Gson gson = new Gson();
                            if (jsonObject.has("items")) {

                                mPageItemslist = gson.fromJson(jsonObject.getString("items"), new TypeToken<ArrayList<ShopItem>>() {
                                }.getType());
                                setShopItems(mPageItemslist);
                            }

                            if (jsonObject.has("title")) {
                                String title = jsonObject.getString("title");
                                setViewTitle(title);
                            }

                            if (jsonObject.has("page_size")) {
                                pageSize = jsonObject.getInt("page_size");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {
                        loadingView.setVisibility(View.VISIBLE);
                        ((TextView) loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
                        loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
                        loadingView.setClickable(true);
                        loadingView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                reRequestLoading();
                                requestItemListData(page);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        onLoad();
                        super.onFinish();
                    }
                }, page);
    }

}
