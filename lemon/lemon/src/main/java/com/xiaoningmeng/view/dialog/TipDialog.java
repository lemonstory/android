package com.xiaoningmeng.view.dialog;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.R;

public class TipDialog {

	public static final int TIME_COUNT = 1500;
	public static final int INVALID = -1;
	private DialogPlus dialog;
	private Builder builder;
	private View mTipView;

	private TipDialog() {
	}

	private TipDialog(final Builder builder) {

		this.builder = builder;
		Context context = builder.context;
		int margin = context.getResources().getDimensionPixelOffset(
				R.dimen.dialog_margin);
		if (builder.layoutId != INVALID) {
			mTipView = View.inflate(context, builder.layoutId, null);
		} else {
			if (builder.hasBtn) {
				mTipView = View.inflate(context, R.layout.dialog_btn_tip, null);
			} else {
				mTipView = View.inflate(context, R.layout.dialog_text_tip, null);
			}
		}
		TextView tipTextView = (TextView) mTipView
				.findViewById(R.id.tv_dialog_tip);
		if (builder.tipText != null) {
			tipTextView.setText(builder.tipText);
		}

		if (builder.enterText != null) {
			((TextView) mTipView.findViewById(R.id.tv_dialog_enter))
					.setText(builder.enterText);
		}
		int bgId = builder.isNeedBg ?R.color.transparent :R.drawable.dialog_white_bg;
		dialog = DialogPlus.newDialog(context)
				.setContentHolder(new ViewHolder(mTipView))
				.setGravity(Gravity.CENTER)
				.setInAnimation(R.anim.abc_fade_in)
				.setOutAnimation(R.anim.abc_fade_out)
				.setMargin(margin, 0, margin, 0)
				//.setIsTransparentBg(false)
//				.setShieldActionUp(builder.isShieldActionUp)
				.setOnClickListener(builder.onClickListener)
				.setContentBackgroundResource(bgId).create();
	}

	public void show() {

		dialog.show();

		if (builder.autoDismiss) {
			new CountDownTimer(TIME_COUNT, 500) {

				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					dialog.dismiss();
				}
			}.start();
		}
	}

	public void dimiss() {
		dialog.dismiss();
	}

	public static class Builder {

		private String tipText;
		private boolean hasBtn;
		private Context context;
		private boolean autoDismiss;
		private boolean isTransparent;
		//是否需要自带背景
		private boolean isNeedBg;
		private boolean isShieldActionUp;
		private int layoutId = INVALID;
		private String enterText;
		public com.orhanobut.dialogplus.OnClickListener onClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTipText(String tipText) {
			this.tipText = tipText;
			return this;
		}

		public Builder setEnterText(String enterText) {
			this.enterText = enterText;
			return this;
		}

		public Builder setTransparent(boolean isTransparent) {
			this.isTransparent = isTransparent;
			return this;
		}

		public Builder setHasBtn(boolean hasBtn) {
			this.hasBtn = hasBtn;
			return this;
		}

		public Builder setAutoDismiss(boolean autoDismiss) {
			this.autoDismiss = autoDismiss;
			return this;
		}

		public Builder setShieldActionUp(boolean isShieldActionUp) {
			this.isShieldActionUp = isShieldActionUp;
			return this;
		}

		public TipDialog create() {
			return new TipDialog(this);
		}

		public Builder setLayoutId(int layoutId) {
			this.layoutId = layoutId;
			return this;
		}

		public Builder setOnClickListener(
				com.orhanobut.dialogplus.OnClickListener onClickListener) {
			this.onClickListener = onClickListener;
			return this;
		}

		public Builder setNeedBg(boolean needBg) {
			isNeedBg = needBg;
			return this;
		}
	}

	
	public void setText(String text){
		if(mTipView != null)
			((TextView) mTipView.findViewById(R.id.tv_dialog_tip)).setText(text);
	}
}
