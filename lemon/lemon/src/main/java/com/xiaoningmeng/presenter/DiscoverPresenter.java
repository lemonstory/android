package com.xiaoningmeng.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.bean.SectionItemBeanDeserializer;
import com.xiaoningmeng.http.ConstantURL;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.presenter.contract.DiscoverConstract;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Index.SectionItemBean.class, new SectionItemBeanDeserializer());
        Gson gson = gsonBuilder.create();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(ConstantURL.URL)
                .client(MyApplication.getInstance().initOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LHttpRequest.IndexRequest indexRequest = mRetrofit.create(LHttpRequest.IndexRequest.class);
        Call<JsonResponse<Index>> call = indexRequest.getResult();
        call.enqueue(new Callback<JsonResponse<Index>>() {

            @Override
            public void onResponse(Call<JsonResponse<Index>> call, Response<JsonResponse<Index>> response) {

                if (response.isSuccessful()) {
                    Index data = response.body().getData();
                    if (response.body().isSuccessful() && null != data) {

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
                    } else {
                        DebugUtils.e("response.body().toString = " + response.body().toString());
                    }

                } else {
                    DebugUtils.e("response.toString = " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<Index>> call, Throwable t) {
                DebugUtils.e("Fail : " + t.toString());
            }
        });
    }
}
