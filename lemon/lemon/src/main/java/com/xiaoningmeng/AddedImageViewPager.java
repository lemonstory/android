package com.xiaoningmeng;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.event.AddedImageEvent;
import com.xiaoningmeng.view.HackyViewPager;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import uk.co.senab.photoview.PhotoView;

public class AddedImageViewPager extends BaseActivity {

    private static final String ISLOCKED_ARG = "isLocked";

    private ViewPager mViewPager;
    private MenuItem menuLockItem;
    private int selectedImagePosition;
    private ArrayList<File> addedImageFiles;
    private TextView headPaginationTv;
    private ImageView deleteAddedImgIv;
    private SamplePagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_image_view_pager);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        headPaginationTv = (TextView) findViewById(R.id.tv_head_pagination);
        deleteAddedImgIv = (ImageView) findViewById(R.id.img_head_right);
        deleteAddedImgIv.setOnClickListener(deleteAddedImgClick);
        selectedImagePosition = this.getIntent().getIntExtra("position",0);
        addedImageFiles = (ArrayList<File>) this.getIntent().getSerializableExtra("addedImageFiles");
        pagerAdapter = new SamplePagerAdapter(addedImageFiles);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(selectedImagePosition);
        setHeadPagination(selectedImagePosition + 1, addedImageFiles.size());
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }
    }

    /**
     *
     * 删除PageView里面的子View实现费劲了,参考了下面的文章,后续还可以在优化
     * 参考文章:
     *      http://blog.5ibc.net/p/15228.html
     *      http://blog.csdn.net/leo8573/article/details/7893841
     */
    View.OnClickListener deleteAddedImgClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int addedImageFilesCount = addedImageFiles.size();
            int delPosition = mViewPager.getCurrentItem();
            File image = addedImageFiles.get(delPosition);
            addedImageFiles.remove(delPosition);
            pagerAdapter.notifyDataSetChanged();
            int current = mViewPager.getCurrentItem() + 1;
            setHeadPagination(current, addedImageFiles.size());
            EventBus.getDefault().post(new AddedImageEvent(image, delPosition));

            if (addedImageFilesCount == 1) {
                AddedImageViewPager.this.back(mViewPager);
            }
        }
    };

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int current = position + 1;
            setHeadPagination(current,addedImageFiles.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    
    public void setHeadPagination(int current,int count) {

        String pageination = String.format("%d / %d",current,count);
        headPaginationTv.setText(pageination);
    }

    static class SamplePagerAdapter extends PagerAdapter {

        private ArrayList<File> imageFiles;

        public SamplePagerAdapter(ArrayList<File> files) {

            this.imageFiles = files;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return this.imageFiles.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            File file = this.imageFiles.get(position);
            //photoView.setImageURI会出现OutOfMemoryError,参考下面的方案处理
            //参考文档
            //  http://stackoverflow.com/questions/2191407/changing-imageview-content-causes-outofmemoryerror
            if(((BitmapDrawable)photoView.getDrawable())!=null) {
                ((BitmapDrawable)photoView.getDrawable()).getBitmap().recycle();
            }
            photoView.setImageURI(Uri.fromFile(file));
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            photoView.invalidate();
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

