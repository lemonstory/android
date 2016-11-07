package com.xiaoningmeng.view.dialog;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.R;

public class TopDialog {

	public static final int TIME_COUNT = 1500;
	public static final int INVALID = -1;
	private DialogPlus dialog;
	
	public static TopDialog create(Context context, ViewGroup decorView,String tip){
		return new TopDialog(context, decorView,tip);
	}
	


	private TopDialog(Context context,ViewGroup decorView, String tip) {

		View mTipView = View.inflate(context, R.layout.dialog_top_tip, null);
		TextView tipTextView = (TextView) mTipView.findViewById(R.id.tv_dialog_tip);
		if (tip != null) {
			tipTextView.setText(tip);
		}
		dialog = DialogPlus.newDialog(context)
				.setContentHolder(new ViewHolder(mTipView))
				//.setDecorView(decorView)
				.setGravity(Gravity.TOP)
				//.setIsTransparentBg(true)
				//.setBackgroundColorResourceId(R.color.logout_bg_normal)
				.create();
	}
	

	public void show() {
		dialog.show();
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

	public void dimiss() {
		dialog.dismiss();
	}

}
