package com.xiaoningmeng.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoningmeng.utils.UiUtils;
import com.xiaoningmeng.R;

import java.util.List;

public class TabIndicatorView extends LinearLayout implements
		View.OnClickListener, OnFocusChangeListener {

	private static final float FOOTER_LINE_HEIGHT = 4.0f;

	private static final int FOOTER_COLOR = 0xFFFFC445;

	private static final float FOOTER_TRIANGLE_HEIGHT = 10;

	private int mCurrentScroll = 0;
	// 选项卡所依赖的viewpager
	private ViewPager mViewPager;

	// 选项卡普通状态下的字体颜色
	private ColorStateList mTextColor;
	private ColorStateList mTextColorSelected;

	// 普通状态和选中状态下的字体大小
	private float mTextSizeNormal;
	private float mTextSizeSelected;

	//private Path mPath = new Path();

	private Paint mPaintFooterLine;
	private Paint mPaintFooterTriangle;
	private float mFooterTriangleHeight;
	private boolean isWarpText;
	// 滚动条的高度
	private float mFooterLineHeight;

	// 当前选项卡的下标，从0开始
	private int mSelectedTab = 0;
	public static final int BSSEEID = 0xffff00;

	private boolean mChangeOnClick = true;
	public static int SPACING = 15;
	public int mScreenWidth;

	// 单个选项卡的宽度
	private int mPerItemWidth = 0;

	// 表示选项卡总共有几个
	private int mTotal = 0;

	public TabIndicatorView(Context context) {
		super(context);
		initDraw(FOOTER_LINE_HEIGHT, FOOTER_COLOR);
	}

	public TabIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setOnFocusChangeListener(this);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.tab_indicatorview);
		int footerColor = a.getColor(
				R.styleable.tab_indicatorview_footerColor1, FOOTER_COLOR);
		mTextColor = a
				.getColorStateList(R.styleable.tab_indicatorview_normaltextColor);
		mTextColorSelected = a
				.getColorStateList(R.styleable.tab_indicatorview_textColorSelected);
		mTextSizeNormal = a.getDimension(
				R.styleable.tab_indicatorview_textSizeNormal, 0);
		mTextSizeSelected = a
				.getDimension(R.styleable.tab_indicatorview_textSizeSelected,
						mTextSizeNormal);
		mFooterLineHeight = a.getDimension(
				R.styleable.tab_indicatorview_footerLineHeight1,
				FOOTER_LINE_HEIGHT);
		mFooterTriangleHeight = a.getDimension(
				R.styleable.tab_indicatorview_footerTriangleHeight,
				FOOTER_TRIANGLE_HEIGHT);
		isWarpText = a.getBoolean(R.styleable.tab_indicatorview_isWarpText,
				true);
		initDraw(mFooterLineHeight, footerColor);
		mScreenWidth = UiUtils.getInstance(context).getmScreenWidth();
		a.recycle();
	}

	@SuppressLint("NewApi")
	public TabIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(true);
		setOnFocusChangeListener(this);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.tab_indicatorview);
		int footerColor = a.getColor(
				R.styleable.tab_indicatorview_footerColor1, FOOTER_COLOR);
		mTextColor = a
				.getColorStateList(R.styleable.tab_indicatorview_normaltextColor);
		mTextColorSelected = a
				.getColorStateList(R.styleable.tab_indicatorview_textColorSelected);
		mTextSizeNormal = a.getDimension(
				R.styleable.tab_indicatorview_textSizeNormal, 0);
		mTextSizeSelected = a
				.getDimension(R.styleable.tab_indicatorview_textSizeSelected,
						mTextSizeNormal);
		mFooterLineHeight = a.getDimension(
				R.styleable.tab_indicatorview_footerLineHeight1,
				FOOTER_LINE_HEIGHT);
		mFooterTriangleHeight = a.getDimension(
				R.styleable.tab_indicatorview_footerTriangleHeight,
				FOOTER_TRIANGLE_HEIGHT);
		initDraw(mFooterLineHeight, footerColor);
		mScreenWidth = UiUtils.getInstance(context).getmScreenWidth();
		a.recycle();
	}

	private void initDraw(float footerLineHeight, int footerColor) {
		mPaintFooterLine = new Paint();
		mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterLine.setStrokeWidth(footerLineHeight);
		mPaintFooterLine.setColor(footerColor);
		mPaintFooterTriangle = new Paint();
		mPaintFooterTriangle.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterTriangle.setColor(footerColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		float scroll_x = 0;
		if (mTotal != 0) {
			mPerItemWidth = getWidth() / mTotal;
			scroll_x = (mCurrentScroll - (mSelectedTab * getWidth())) / mTotal;
		} else {
			mPerItemWidth = getWidth();
			scroll_x = mCurrentScroll;
		}
		//Path path = mPath;
		//path.rewind();
		float left_x;
		float right_x;
		float offset = SPACING;
		if (isWarpText) {
			left_x = mSelectedTab * mPerItemWidth + offset + scroll_x + 15;
			right_x = (mSelectedTab + 1) * mPerItemWidth - offset + scroll_x
					- 15;
		} else {
			left_x = mSelectedTab * mPerItemWidth + offset + scroll_x;
			right_x = (mSelectedTab + 1) * mPerItemWidth - offset + scroll_x;
		}
		float top_y = getHeight() - mFooterLineHeight - mFooterTriangleHeight;
		float bottom_y = getHeight() - mFooterLineHeight;
		RectF r2=new RectF();                           //RectF对象
		r2.left=left_x;                                 //左边
		r2.top=top_y;                                 //上边
		r2.right=right_x;                                   //右边
		r2.bottom=bottom_y;                              //下边
		canvas.drawRoundRect(r2, 5, 5, mPaintFooterTriangle);        //绘制圆角矩形
	/*	path.moveTo(left_x, top_y);
		path.lineTo(right_x, top_y);
		path.lineTo(right_x, bottom_y);
		path.lineTo(left_x, bottom_y);
		path.close();*/
		//canvas.drawPath(path, mPaintFooterTriangle);
	}

	// 当页面滚动的时候，重新绘制滚动条
	public void onScrolled(int h) {
		mCurrentScroll = h * getWidth() / mScreenWidth;
		invalidate();
	}

	// 当页面切换的时候，重新绘制滚动条
	public synchronized void onSwitched(int position) {
		if (mSelectedTab == position) {
			return;
		}
		setCurrentTab(position);
		invalidate();
	}

	// 初始化选项卡
	public void init(int startPos, String[] tabs, ViewPager mViewPager) {
		removeAllViews();
		this.mViewPager = mViewPager;
		this.mTotal = tabs.length;
		for (int i = 0; i < mTotal; i++) {
			add(i, tabs[i]);
		}
		setCurrentTab(startPos);
		invalidate();
	}

	// 初始化选项卡
	public void init(int startPos, List<String> tabs, ViewPager mViewPager) {
		removeAllViews();
		this.mViewPager = mViewPager;
		this.mTotal = tabs.size();
		for (int i = 0; i < mTotal; i++) {
			add(i, tabs.get(i));
		}
		setCurrentTab(startPos);
		invalidate();
	}

	protected void add(int pos, String item) {

		TextView tab = new TextView(getContext());
		LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
		lp.weight = 1;
		lp.gravity = Gravity.CENTER;
		tab.setGravity(Gravity.CENTER);
		// tab.setPadding(SPACING, 10, SPACING, 10);
		tab.setLayoutParams(lp);
		if (mTextColor != null) {
			tab.setTextColor(mTextColor);
		}
		if (mTextSizeNormal > 0) {
			tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
		}
		tab.setText(item);
		tab.setId(pos + BSSEEID);
		addView(tab);
	}

	public void setDisplayedPage(int index) {
		mSelectedTab = index;
	}

	public void setChangeOnClick(boolean changeOnClick) {
		mChangeOnClick = changeOnClick;
	}

	public boolean getChangeOnClick() {
		return mChangeOnClick;
	}

	@Override
	public void onClick(View v) {

		int position = v.getId() - BSSEEID;
		setCurrentTab(position);
	}

	public int getTabCount() {
		int children = getChildCount();
		return children;
	}

	// 设置当前选项卡
	public synchronized void setCurrentTab(int index) {
		if (index < 0 || index >= getTabCount()) {
			return;
		}
		View oldTab = getChildAt(mSelectedTab);
		oldTab.setSelected(false);
		setTabTextSize(oldTab, false);
		mSelectedTab = index;
		View newTab = getChildAt(mSelectedTab);
		newTab.setSelected(true);
		setTabTextSize(newTab, true);
		mViewPager.setCurrentItem(mSelectedTab);
		invalidate();
	}

	private void setTabTextSize(View tab, boolean selected) {

		TextView titleText = (TextView) tab;
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				selected ? mTextSizeSelected : mTextSizeNormal);
		titleText.setTextColor(selected ? mTextColorSelected : mTextColor);

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == this && hasFocus && getTabCount() > 0) {
			getChildAt(mSelectedTab).requestFocus();
			return;
		}
		if (hasFocus) {
			int i = 0;
			int numTabs = getTabCount();
			while (i < numTabs) {
				if (getChildAt(i) == v) {
					setCurrentTab(i);
					break;
				}
				i++;
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mCurrentScroll == 0 && mSelectedTab != 0) {
			mCurrentScroll = (getWidth() + mViewPager.getPageMargin())
					* mSelectedTab;
		}
	}

	public int getSelectedTab() {
		return mSelectedTab;
	}

}
