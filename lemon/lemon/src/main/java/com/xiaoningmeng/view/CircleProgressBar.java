package com.xiaoningmeng.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.xiaoningmeng.R;


public class CircleProgressBar extends View {
	/**
	 * 画笔对象的引用 因为渲染会对整个画笔进行渲染，所以定义了两个
	 */
	private Paint paint;
	private Paint paint1;

	/**
	 * 圆环的颜色
	 */
	private int roundColor;

	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;

	/**
	 * 定义一个渲染
	 */
	private SweepGradient sweepGradient = null;

	private int[] colors = { getResources().getColor(R.color.circle_progressBar_begin),
			getResources().getColor(R.color.circle_progressBar_end),
			getResources().getColor(R.color.circle_progressBar_begin)};
	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;

	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 最大进度
	 */
	private int max;

	/**
	 * 当前进度
	 */
	private int progress;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textIsDisplayable;

	/**
	 * 进度的风格，实心或者空心
	 */
	private int style;

	public static final int STROKE = 0;
	public static final int FILL = 1;

	public CircleProgressBar(Context context) {
		this(context, null);
	}

	public CircleProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();
		paint1 = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.CircleProgressBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(
				R.styleable.CircleProgressBar_roundColor2, Color.RED);
		roundProgressColor = mTypedArray.getColor(
				R.styleable.CircleProgressBar_roundProgressColor2, Color.BLUE);
		textColor = mTypedArray.getColor(
				R.styleable.CircleProgressBar_textColor2, Color.GREEN);
		textSize = mTypedArray.getDimension(
				R.styleable.CircleProgressBar_textSize2, 15);
		roundWidth = mTypedArray.getDimension(
				R.styleable.CircleProgressBar_roundWidth2, 5);
		max = mTypedArray.getInteger(R.styleable.CircleProgressBar_max2, 100);
		textIsDisplayable = mTypedArray.getBoolean(
				R.styleable.CircleProgressBar_textIsDisplayable2, false);
		style = mTypedArray.getInt(R.styleable.CircleProgressBar_style2, 0);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/**
		 * 画最外层的大圆环
		 */
		int centre = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (centre - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(centre, centre, radius, paint); // 画出圆环

		/**
		 * 画进度百分比
		 */
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
		int percent = (int) (((float) progress / (float) max) * 100); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
		float textWidth = paint.measureText(percent + "%"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		if (textIsDisplayable && percent != 0 && style == STROKE) {
			canvas.drawText(percent + "%", centre - textWidth / 2, centre
					+ textSize / 2, paint); // 画出进度百分比
		}

		/**
		 * 画圆弧 ，画圆环的进度
		 */

		// 设置进度是实心还是空心
		paint1.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint1.setColor(roundProgressColor); // 设置进度的颜色
		RectF oval = new RectF(centre - radius, centre - radius, centre
				+ radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
		paint1.setStyle(Paint.Style.STROKE);

		// 定义一个梯度渲染，由于梯度渲染是从三点钟方向开始，所以再让他逆时针旋转90°，从0点开始
		sweepGradient = new SweepGradient(centre, centre, colors, null);
		Matrix matrix = new Matrix();
		matrix.setRotate(-90, centre, centre);
		sweepGradient.setLocalMatrix(matrix);

		switch (style) {
		case STROKE: {
			paint1.setAntiAlias(true);
			paint1.setStyle(Paint.Style.STROKE);
			paint1.setShader(sweepGradient);
			canvas.drawArc(oval, -90, 360 * progress / max, false, paint1); // 根据进度画圆弧
			break;
		}
		case FILL: {
			paint1.setStyle(Paint.Style.FILL_AND_STROKE);
			paint1.setShader(sweepGradient);
			if (progress != 0)
				canvas.drawArc(oval, -90, 360 * progress / max, true, paint1); // 根据进度画圆弧
			break;
		}
		}
	}

	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max <= 0) {
			max = 100;
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			postInvalidate();
		}

	}

	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}
}
