package com.xiaoningmeng.view;

import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 软键盘弹起事件监听
 * */
public class KeyboardRelativeLayout extends RelativeLayout {

	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;

	private boolean mHasInit = false;
	private boolean mHasKeyboard = false;
	public int mHeight;

	public int mSoftInputHeight = 0;

	private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;

	public KeyboardRelativeLayout(Context context) {
		super(context);
	}

	public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnKeyboardStateChangedListener(
			IOnKeyboardStateChangedListener onKeyboardStateChangedListener) {
		this.onKeyboardStateChangedListener = onKeyboardStateChangedListener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!mHasInit) {
			mHasInit = true;
			mHeight = b;

			if (onKeyboardStateChangedListener != null) {
				onKeyboardStateChangedListener
						.onKeyboardStateChanged(KEYBOARD_STATE_INIT);
			}
		} else {
			mHeight = mHeight < b ? b : mHeight;
		}
		// mHeight代表键盘的真实高度,b代表在窗口中的高度
		// mHeight>b ，说明键盘隐藏
		if (mHasInit && mHeight > b) {
			mHasKeyboard = true;
			if (onKeyboardStateChangedListener != null) {
				onKeyboardStateChangedListener
						.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);

			}
		}
		if (mHasInit && mHasKeyboard && mHeight == b) { // mHeight = b 说明已经弹出
			mHasKeyboard = false;
			if (onKeyboardStateChangedListener != null) {
				onKeyboardStateChangedListener
						.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);
			}
		}

		// /Log.i("yesongsong", "mheight= "+ mHeight);
		// Log.i("yesongsong", "mHeight = "+mHeight);
	}

	public int getSoftInputHeight() {

		return mHeight;

	}

	public interface IOnKeyboardStateChangedListener {
		void onKeyboardStateChanged(int state);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);

		int height = oldh - h;

		if (height > 0) {
			mSoftInputHeight = height;
		}

		Log.i("yesongsong", "...mSoftInputHeight...:" + mSoftInputHeight);
	}

}
