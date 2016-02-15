package com.xiaoningmeng.view.dialog;

import com.xiaoningmeng.R;
import com.xiaoningmeng.utils.UiUtils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class BaseDialog extends Dialog {

	public static final int INVALID = 0;

	private BaseDialog(Builder builder) {
		super(builder.context, builder.dialogStyle);
		Window window = this.getWindow();
		window.setGravity(builder.gravity);
		if (builder.animStyle == INVALID) {
			switch (builder.gravity) {
			case Gravity.CENTER:
				window.setWindowAnimations(R.style.center_dialog_animation);
				break;
			case Gravity.BOTTOM:
				window.setWindowAnimations(R.style.bottom_dialog_animation);
				break;
			default:
				break;
			}
		} else if(builder.animStyle == 1){
			//TODO
		}else{
			window.setWindowAnimations(builder.animStyle);
		}
		this.setCanceledOnTouchOutside(true);
	}

	public void show(View contentView) {

		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		LayoutParams params = win.getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);
		super.setContentView(contentView, params);
		show();
	}

	public void warpShow(View contentView) {

		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		super.setContentView(contentView, params);
		show();
	}

	/**
	 * 全屏显示
	 * 
	 * @param contentView
	 */
	public void fillShow(View contentView) {

		LayoutParams params = getWindow().getAttributes();
		params.height = UiUtils.getInstance(getContext()).getmScreenHeight();
		params.width = UiUtils.getInstance(getContext()).getmScreenWidth();
		getWindow().setAttributes(params);
		super.setContentView(contentView, params);
		show();

	}

	public static class Builder {
		private Context context;
		private int gravity;
		private int animStyle;
		private int dialogStyle = R.style.base_dialog_style;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}

		public Builder setAnimStyle(int animStyle) {
			this.animStyle = animStyle;
			return this;
		}

		public Builder setDialogStyle(int dialogStyle) {
			this.dialogStyle = dialogStyle;
			return this;
		}

		public BaseDialog create() {
			return new BaseDialog(this);
		}

	}
}
