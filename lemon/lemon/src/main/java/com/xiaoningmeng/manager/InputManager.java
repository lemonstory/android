package com.xiaoningmeng.manager;

import com.xiaoningmeng.view.KeyboardRelativeLayout;
import com.xiaoningmeng.view.KeyboardRelativeLayout.IOnKeyboardStateChangedListener;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

public class InputManager implements IOnKeyboardStateChangedListener {
	private InputMethodManager mInputMethodManager;// 键盘
	private ScrollView scrollview;
	private OnKeyBoardChangeListener listener;
	private Context mContext;

	public InputManager(Context context, ScrollView scrollview,
			KeyboardRelativeLayout rl_keyborad) {
		this(context, scrollview, rl_keyborad, null);
	}

	public InputManager(Context context, ScrollView scrollview,
			KeyboardRelativeLayout rl_keyborad,
			OnKeyBoardChangeListener listener) {
		mContext = context;
		mInputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		this.scrollview = scrollview;
		rl_keyborad.setOnKeyboardStateChangedListener(this);
		this.listener = listener;

	}

	public void hideSoftInput(EditText editText) {

		if (mInputMethodManager.isActive()) {
			if (editText.isFocusable()) {
				mInputMethodManager.hideSoftInputFromWindow(
						editText.getWindowToken(), 0);
			} else {
				mInputMethodManager.hideSoftInputFromWindow(
						editText.getWindowToken(), 0);
			}
		}
	}

	@Override
	public void onKeyboardStateChanged(int state) {
		switch (state) {
		case KeyboardRelativeLayout.KEYBOARD_STATE_HIDE:
			if (listener != null) {
				listener.hide();
			}
			break;
		case KeyboardRelativeLayout.KEYBOARD_STATE_SHOW:
			scrollview.smoothScrollBy(0, 150);
			if (listener != null) {
				listener.show();
			}
			break;
		}
	}

	public interface OnKeyBoardChangeListener {

		void hide();

		void show();
	}
}
