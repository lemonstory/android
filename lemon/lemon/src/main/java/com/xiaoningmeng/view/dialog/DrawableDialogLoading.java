package com.xiaoningmeng.view.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.R;
import com.xiaoningmeng.http.ILoading;

public class DrawableDialogLoading implements ILoading {

	private BaseDialog mDialog;
	private Context mContext;
	private View loadingView;
	private TextView mLoadingTv;

	public DrawableDialogLoading(Context context) {

		mContext = context;
		mDialog = new BaseDialog.Builder(context).setGravity(Gravity.CENTER)
				.setDialogStyle(R.style.loading_dialog_style).setAnimStyle(1).create();
		mDialog.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loadingView = inflater.inflate(R.layout.dialog_drawable_loading, null);
		mLoadingTv = (TextView) loadingView.findViewById(R.id.tv_empty_tip);
	}

	/**
	 * 开始加载
	 */
	@Override
	public void startLoading() {

		if (mContext != null&& mDialog != null) {
			mDialog.warpShow(loadingView);
		}
	}

	/**
	 * 完成加载
	 */
	@Override
	public void stopLoading() {

		if (mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

	public void setLoadingTip(String loading){
		mLoadingTv.setText(loading);
	}
}
