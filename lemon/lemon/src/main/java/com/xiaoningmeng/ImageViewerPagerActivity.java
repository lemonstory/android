package com.xiaoningmeng;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.view.HackyViewPager;

import java.util.ArrayList;

import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

public class ImageViewerPagerActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager mViewPager;
    private MenuItem menuLockItem;
    private int selectedImagePosition;
    private ArrayList<String> imagesUrl;
    private TextView paginationTv;
    private TextView imageLoadingTv;
    private SamplePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_pager);
        Fresco.initialize(this);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        paginationTv = (TextView) findViewById(R.id.tv_pagination);
        imageLoadingTv = (TextView) findViewById(R.id.tv_loading_progress);
        selectedImagePosition = this.getIntent().getIntExtra("position",0);
        imagesUrl = (ArrayList<String>) this.getIntent().getSerializableExtra("imagesUrl");
        pagerAdapter = new SamplePagerAdapter(imagesUrl);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(selectedImagePosition);
        if (selectedImagePosition >= 0 && imagesUrl != null && imagesUrl.size() > 0) {
            setPaginationText(selectedImagePosition + 1, imagesUrl.size());
        }
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

   /* PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View view, float x, float y){
            ImageViewerPagerActivity.this.back(view);
        }
    };*/

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

            int count = 0;
            if (this.imagesPath != null) {
                count = this.imagesPath.size();
            }
            return count;
        }

        @Override
        public View instantiateItem(ViewGroup viewGroup, int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            String imageAbsolutePath = this.imagesPath.get(position);
            controller.setUri(Uri.parse(imageAbsolutePath));
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                ImageViewerPagerActivity.this.back(view);
            }
        });
            try {
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return photoDraweeView;
        }
       /* @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            String imageAbsolutePath = this.imagesPath.get(position);
            HashMap<String,Integer> imageSize = PostImageUtils.parseImageSizeWithUrl(ImageViewerPagerActivity.this, imageAbsolutePath);
            ImageSize targetSize = new ImageSize(imageSize.get("widthPx"),imageSize.get("heightPx"));
            ImageLoader.getInstance().displayImage(imageAbsolutePath,(ImageAware) new ImageViewAware(photoView),(DisplayImageOptions)null, targetSize, imageLoadingListener, imageLoadingProgressListener);
            container.addView(photoView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            photoView.setOnPhotoTapListener(onPhotoTapListener);
            return photoView;
        }*/

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

   /* SimpleImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {

            imageLoadingTv.setText("0%");
            imageLoadingTv.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            imageLoadingTv.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            imageLoadingTv.setVisibility(View.GONE);
        }
    };

    ImageLoadingProgressListener imageLoadingProgressListener = new ImageLoadingProgressListener() {
        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {

            imageLoadingTv.setText(Integer.toString(Math.round(100.0f * current / total)) + "%");
        }
    };*/

}
