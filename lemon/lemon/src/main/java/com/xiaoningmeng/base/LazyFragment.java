package com.xiaoningmeng.base;


/**
 * Created by feishang on 2016/8/8.
 * 懒加载Fragment
 * 参考资料:http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1021/1813.html
 */

public abstract class LazyFragment extends BaseFragment {

    protected boolean isVisible;
    protected boolean isLoadData;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (!isLoadData) {
            lazyLoad();
        }
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }
}
