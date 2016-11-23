package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.ShopItem;
import com.xiaoningmeng.constant.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by gaoyong on 16/4/6.
 */
public class ShopAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ShopItem> mShopItems;

    public ShopAdapter(Context mContext, List<ShopItem> mShopItem) {

        this.mContext = mContext;
        this.mShopItems = mShopItem;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        int count = 0;
        if (this.mShopItems != null) {
            count = this.mShopItems.size() / 2 + (this.mShopItems.size() % 2 == 0 ? 0 : 1);
        }
        return count;
    }

    @Override
    public Object getItem(int position) {

        return null;
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
            convertView = mInflater.inflate(R.layout.item_shop, null);
            holder.mallTv1 = (TextView) convertView.findViewById(R.id.tv_mall1);
            holder.taobaoTv1 = (TextView) convertView.findViewById(R.id.tv_taobao1);
            holder.titleTv1 = (TextView) convertView.findViewById(R.id.tv_title1);
            holder.priceTv1 = (TextView) convertView.findViewById(R.id.tv_price1);
            holder.reservePriceTv1 = (TextView) convertView.findViewById(R.id.tv_reserve_price1);
            holder.picImg1 = (SimpleDraweeView) convertView.findViewById(R.id.img_pic1);
            holder.postFeeTv1 = (TextView) convertView.findViewById(R.id.tv_post_fee1);
            holder.itemRl1 = convertView.findViewById(R.id.rl_shop_item1);


            holder.mallTv2 = (TextView) convertView.findViewById(R.id.tv_mall2);
            holder.taobaoTv2 = (TextView) convertView.findViewById(R.id.tv_taobao2);
            holder.titleTv2 = (TextView) convertView.findViewById(R.id.tv_title2);
            holder.priceTv2 = (TextView) convertView.findViewById(R.id.tv_price2);
            holder.reservePriceTv2 = (TextView) convertView.findViewById(R.id.tv_reserve_price2);
            holder.picImg2 = (SimpleDraweeView) convertView.findViewById(R.id.img_pic2);
            holder.postFeeTv2 = (TextView) convertView.findViewById(R.id.tv_post_fee2);
            holder.itemRl2 = convertView.findViewById(R.id.rl_shop_item2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int coverPos = position * 2;
        int itemsSize = mShopItems.size();
        if (itemsSize > coverPos) {

            holder.itemRl1.setVisibility(View.VISIBLE);
            ShopItem item = this.mShopItems.get(coverPos);
            setViewValue(item, holder.mallTv1, holder.taobaoTv1, holder.titleTv1, holder.priceTv1, holder.reservePriceTv1, holder.picImg1, holder.postFeeTv1);
            holder.picImg1.setOnClickListener(this);
            holder.picImg1.setTag(item);
            holder.itemRl1.setTag(item);
            holder.itemRl1.setOnClickListener(this);
        } else {
            holder.itemRl1.setVisibility(View.INVISIBLE);
        }

        if (itemsSize > coverPos + 1) {

            holder.itemRl2.setVisibility(View.VISIBLE);
            ShopItem item = this.mShopItems.get(coverPos + 1);
            setViewValue(item, holder.mallTv2, holder.taobaoTv2, holder.titleTv2, holder.priceTv2, holder.reservePriceTv2, holder.picImg2, holder.postFeeTv2);
            holder.picImg2.setOnClickListener(this);
            holder.picImg2.setTag(item);
            holder.itemRl2.setTag(item);
            holder.itemRl2.setOnClickListener(this);
        } else {
            holder.itemRl2.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    public void setViewValue(ShopItem item, TextView mallTv, TextView taobaoTv, TextView titleTv, TextView priceTv, TextView reservePriceTv, SimpleDraweeView picImg, TextView postFeeTv) {

        String prefix = "";
        if (item.isMall()) {
            prefix = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            mallTv.setVisibility(View.VISIBLE);
            taobaoTv.setVisibility(View.INVISIBLE);
        } else {
            prefix = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            mallTv.setVisibility(View.INVISIBLE);
            taobaoTv.setVisibility(View.VISIBLE);
        }
        String title = prefix + item.getTitle();
        titleTv.setText(Html.fromHtml(title));
        priceTv.setText("￥" + item.getPrice());
        reservePriceTv.getPaint().setAntiAlias(true);//抗锯齿
        reservePriceTv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);//设置中划线并加清晰
        reservePriceTv.setText("￥" + item.getReserve_price());
        picImg.setImageURI(Uri.parse(item.getPic_url()));

        if (item.getPost_fee().equals("0.00")) {
            postFeeTv.setVisibility(View.VISIBLE);
        } else {
            postFeeTv.setVisibility(View.INVISIBLE);
        }
    }

    public void showItemDetailPage(Activity activity, String itemId, String itemTitle) {

        //实现参考SDK,demo
        AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
        alibcShowParams = new AlibcShowParams(OpenType.Auto, false);
        AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
        alibcTaokeParams = new AlibcTaokeParams(Constant.DEFAULT_TAOKE_PID, "", "");
        Map<String, String> exParams = new HashMap<>(); //yhhpass参数
        exParams.put(AlibcConstants.ISV_CODE, "xiaoningmeng");

        //商品详情page
        AlibcBasePage alibcDetailPage = new AlibcDetailPage(itemId);
        AlibcTrade.show(activity, alibcDetailPage, alibcShowParams, alibcTaokeParams, exParams, new AlibcTradeCallback() {

            @Override
            public void onTradeSuccess(TradeResult tradeResult) {
                //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
                Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                //打开电商组件，用户操作中错误信息回调。code：错误码；msg：错误信息
                Toast.makeText(mContext, "失败 " + code + msg,
                        Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("itemId", itemId);
        map.put("itemTitle", itemTitle);
        MobclickAgent.onEvent(mContext, "event_show_shopitem", map);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id != 0) {
            switch (id) {
                case R.id.img_pic1:
                case R.id.img_pic2:
                case R.id.rl_shop_item1:
                case R.id.rl_shop_item2:
                    ShopItem item = (ShopItem) v.getTag();
                    if (item != null) {
                        String openIid = item.getOpen_iid();
                        if (openIid != null && !openIid.equals("")) {
                            showItemDetailPage((Activity) mContext, item.getOpen_iid(), item.getTitle());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    static class ViewHolder {

        View itemRl1;
        TextView mallTv1;
        TextView taobaoTv1;
        TextView titleTv1;
        TextView priceTv1;
        TextView reservePriceTv1;
        SimpleDraweeView picImg1;
        TextView postFeeTv1;

        View itemRl2;
        TextView mallTv2;
        TextView taobaoTv2;
        TextView titleTv2;
        TextView priceTv2;
        TextView reservePriceTv2;
        SimpleDraweeView picImg2;
        TextView postFeeTv2;
    }
}
