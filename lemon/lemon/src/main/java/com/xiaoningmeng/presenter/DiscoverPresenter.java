package com.xiaoningmeng.presenter;

import android.content.Context;

import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.bean.SectionItemBeanDeserializer;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by huang on 2016/10/15.
 */

public class DiscoverPresenter implements DiscoverConstract.Presenter {

    private DiscoverConstract.View discoverView;
    private Context context;

    public DiscoverPresenter(Context context, DiscoverConstract.View discoverView) {

        this.discoverView = discoverView;
        this.context = context;
        this.discoverView.setPresenter(this);
    }


    @Override
    public void subscribe() {
        //nothing to do
    }

    @Override
    public void unsubscribe() {
        this.discoverView = null;
        this.context = null;
    }

    @Override
    public void requestIndexData() {

        JsonCallback<Index> jsonCallBack = new JsonCallback<Index>() {
            @Override
            public void onGetDataSuccess(Index data) {
                if (null != data) {

                    discoverView.requestBannderSuccess(data.getFocus());
                    Observable.just(data).map(new Func1<Index, List<IRecyclerItem>>() {

                        @Override
                        public List<IRecyclerItem> call(final Index data) {

                            ArrayList<IRecyclerItem> iRecyclerItems = new ArrayList<>();
                            for (int i = 0; i < data.getCategory().getItems().size(); i++) {

                                Index.CategoryBean.ItemBean itemBean = data.getCategory().getItems().get(i);
                                iRecyclerItems.add(itemBean);
                            }

                            for (int i = 0; i < data.getSection().getItems().size(); i++) {

                                Index.SectionItemBean itemBean = (Index.SectionItemBean) data.getSection().getItems().get(i);
                                iRecyclerItems.add(itemBean);
                                iRecyclerItems.addAll(itemBean.getItems());

                                if (data.getAd().getItems().size() > i) {
                                    iRecyclerItems.add(data.getAd().getItems().get(i));
                                }
                            }

                            return iRecyclerItems;
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<List<IRecyclerItem>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(List<IRecyclerItem> iRecyclerItems) {
                                    discoverView.requestDataSuccess(iRecyclerItems);
                                }
                            });
                }
            }
        };

        jsonCallBack.builder.registerTypeAdapter(Index.SectionItemBean.class,new SectionItemBeanDeserializer());
        LHttpRequest.getInstance().indexRequest(context,jsonCallBack);
    }
}
