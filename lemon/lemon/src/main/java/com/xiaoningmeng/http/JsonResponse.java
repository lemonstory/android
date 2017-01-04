package com.xiaoningmeng.http;


import static com.xiaoningmeng.constant.Constant.REQ_SUCCESS_STATUS;

/**
 * Created by gaoyong on 2016/12/27.
 */

public class JsonResponse<T> {

    /**
     * code : int
     * data : 泛型
     */

    private int code;
    private T data;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccessful() {

        if (REQ_SUCCESS_STATUS == getCode()) {
            return true;
        } else {
            return false;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
