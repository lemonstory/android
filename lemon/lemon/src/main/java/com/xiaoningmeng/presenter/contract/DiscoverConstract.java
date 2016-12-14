package com.xiaoningmeng.presenter.contract;

import com.xiaoningmeng.base.BasePresenter;
import com.xiaoningmeng.base.BaseView;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;

import java.util.List;

/**
 * Created by huang on 2016/10/15.
 */

public interface DiscoverConstract {


    interface View<T> extends BaseView<T> {
        public void requestDataSuccess(List<IRecyclerItem> datas);
        public void requestBannderSuccess(Index.FocusBean focusBean);

    }

    interface Presenter extends BasePresenter {

        public void requestIndexData();
    }
}
