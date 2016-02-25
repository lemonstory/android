package com.xiaoningmeng;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.view.HackyViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerPagerActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager mViewPager;
    private MenuItem menuLockItem;
    private int selectedImagePosition;
    private ArrayList<String> imagesUrl;
    private TextView paginationTv;
    private SamplePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_pager);

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        paginationTv = (TextView) findViewById(R.id.tv_pagination);
        selectedImagePosition = this.getIntent().getIntExtra("position",0);
        imagesUrl = (ArrayList<String>) this.getIntent().getSerializableExtra("imagesUrl");
        pagerAdapter = new SamplePagerAdapter(imagesUrl);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(selectedImagePosition);
        setPaginationText(selectedImagePosition + 1, imagesUrl.size());
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int current = position + 1;
            setPaginationText(current, imagesUrl.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void setPaginationText(int current,int count) {

        String pageination = String.format("%d / %d",current,count);
        paginationTv.setText(pageination);
    }

    PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View view, float x, float y){
            ImageViewerPagerActivity.this.back(view);
        }
    };

    class SamplePagerAdapter extends PagerAdapter {

        private ArrayList<String> imagesPath;

        public SamplePagerAdapter(ArrayList<String> imagesPath) {

            this.imagesPath = imagesPath;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return this.imagesPath.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            String imageAbsolutePath = this.imagesPath.get(position);
            ImageLoader.getInstance().displayImage(imageAbsolutePath, photoView, Constant.ALBUM_OPTIONS);
            container.addView(photoView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            photoView.setOnPhotoTapListener(onPhotoTapListener);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewpager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuLockItem = menu.findItem(R.id.menu_lock);
        toggleLockBtnTitle();
        menuLockItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toggleViewPagerScrolling();
                toggleLockBtnTitle();
                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            ((HackyViewPager) mViewPager).toggleLock();
        }
    }

    private void toggleLockBtnTitle() {
        boolean isLocked = false;
        if (isViewPagerActive()) {
            isLocked = ((HackyViewPager) mViewPager).isLocked();
        }
        String title = (isLocked) ? getString(R.string.menu_unlock) : getString(R.string.menu_lock);
        if (menuLockItem != null) {
            menuLockItem.setTitle(title);
        }
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }
}