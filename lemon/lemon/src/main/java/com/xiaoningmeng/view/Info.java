package com.xiaoningmeng.view;

import android.graphics.RectF;
import android.widget.ImageView;

public class Info {
    // 内部图片在整个窗口的位置
    RectF mRect = new RectF();
    RectF mLocalRect = new RectF();
    RectF mImgRect = new RectF();
    RectF mWidgetRect = new RectF();
    float mScale;
    ImageView.ScaleType mScaleType;

    public Info(RectF rect, RectF local, RectF img, RectF widget, float scale,ImageView.ScaleType scaleType) {
        this.mRect.set(rect);
        this.mLocalRect.set(local);
        this.mImgRect.set(img);
        this.mWidgetRect.set(widget);
        this.mScale = scale;
        this.mScaleType = scaleType;
    }

	public RectF getmRect() {
		return mRect;
	}

	public void setmRect(RectF mRect) {
		this.mRect = mRect;
	}

	public RectF getmLocalRect() {
		return mLocalRect;
	}

	public void setmLocalRect(RectF mLocalRect) {
		this.mLocalRect = mLocalRect;
	}

	public RectF getmImgRect() {
		return mImgRect;
	}

	public void setmImgRect(RectF mImgRect) {
		this.mImgRect = mImgRect;
	}

	public RectF getmWidgetRect() {
		return mWidgetRect;
	}

	public void setmWidgetRect(RectF mWidgetRect) {
		this.mWidgetRect = mWidgetRect;
	}

	public float getmScale() {
		return mScale;
	}

	public void setmScale(float mScale) {
		this.mScale = mScale;
	}

	public ImageView.ScaleType getmScaleType() {
		return mScaleType;
	}

	public void setmScaleType(ImageView.ScaleType mScaleType) {
		this.mScaleType = mScaleType;
	}
    
    
}
