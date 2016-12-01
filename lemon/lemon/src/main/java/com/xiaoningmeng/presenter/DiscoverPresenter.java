package com.xiaoningmeng.presenter;

import android.content.Context;

import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;
import com.xiaoningmeng.http.JsonCallback;

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
        LHttpRequest.getInstance().indexRequest(context, new JsonCallback<Index>() {
            @Override
            public void onGetDataSuccess(Index data) {
                if (null != data) {
                    discoverView.requestBannderSuccess(data.getFocus_pic());
                    Observable.just(data).map(new Func1<Index, List<IRecyclerItem>>() {

                        @Override
                        public List<IRecyclerItem> call(final Index data) {

                            ArrayList<IRecyclerItem> iRecyclerItems = new ArrayList<>();
                            for (int i = 0; i < data.getContent_category().getItems().size(); i++) {
                                Index.ContentCategoryBean.ItemBean itemBean = data.getContent_category().getItems().get(i);
                                iRecyclerItems.add(itemBean);
                            }

                            for (int i = 0; i < data.getAlbum_section().getItems().size(); i++) {
                                Index.AlbumSectionBean.ItemBean itemBean = data.getAlbum_section().getItems().get(i);
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
        });
    }
}
