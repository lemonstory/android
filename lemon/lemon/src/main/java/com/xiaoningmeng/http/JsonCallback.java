package com.xiaoningmeng.http;


import com.google.gson.Gson;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.DebugUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**网络请求回调
 * Created by huang on 2016/10/22.
 * 重写StringCallBack
 */

public abstract class JsonCallback<T> extends StringCallback{

    public static final int JSON_PARSE_CODE = 708;

    private ILoading mLoading;
    private int responseCode = JSON_PARSE_CODE;


    public JsonCallback(ILoading mLoading) {
        this.mLoading = mLoading;
    }

    public JsonCallback() {

    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if(mLoading != null){
            mLoading.startLoading();
        }
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if(mLoading != null){
            mLoading.stopLoading();
        }
        onFinish();
    }



    @Override
    public void onError(Call call, Exception e, int id) {
        onFailure(responseCode,e.getMessage());

    }


    @Override
    public boolean validateReponse(Response response, int id) {
        responseCode = response.code();
        return super.validateReponse(response, id);
    }

    public void onFinish(){

    }

    /**
     * 通过gson将response转换成所需的对象
     * @param response
     * @param id
     */
    @Override
    public void onResponse(final String response, int id) {

        Observable.just(response).map(new Func1<String, T>() {
            @Override
            public T call(String response) {
                int code = -1;
                String content = "";
                T t = null;
                try {
                    DebugUtils.e(response);
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(response);
                    Type type = getType();
                    code = jsonObject.has("code") ? jsonObject.getInt("code") : code;
                    DebugUtils.e("code = " + code);
                    //兼容DZ
                    if (Constant.REQ_SUCCESS_STATUS == code || jsonObject.has("Variables")) {
                        if (jsonObject.has("data")) {

                            content = jsonObject.getString("data");
                            if (type == String.class || type == Object.class) {
                                t = (T) content;
                            } else {
                                t = gson.fromJson(content, type);
                            }
                        }else if (jsonObject.has("Variables")) {
                            content = response;
                            if (type == String.class || type == Object.class) {
                                t = (T) content;
                            } else {
                                t = gson.fromJson(content, type);
                            }
                        }
                        else {
                            t = null;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return  t;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if(t != null){
                            onGetDataSuccess(t);
                        }else{
                            onFailure(responseCode,response);
                        }
                    }
                });
    }


    /**
     * 重写该方法得到错误码
     * @param statusCode 错误码
     * @param failureResponse 错误原因
     */
    public void onFailure(int statusCode,String failureResponse){
        onFailure(failureResponse);
    }

    /**
     * 该方法为了兼容2.6版本之前的错误回调
     * @param failureResponse
     */
    public void onFailure(String failureResponse){

    }
    /**
     * 取到返回结果的数据后调用，一般是data字段的数据
     *
     * @param data
     *            从返回结果中解析的data字段的值
     */
    public abstract void onGetDataSuccess(T data);

    /**
     * 获取当前的类型
     *
     * @return 泛型的类型
     */
    private Type getType() {
        Type type = String.class;
        Type mySuperClass = this.getClass().getGenericSuperclass();
        if (mySuperClass instanceof ParameterizedType)
            type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        return type;
    }

}
