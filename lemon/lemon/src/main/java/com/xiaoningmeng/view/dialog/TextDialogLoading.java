package com.xiaoningmeng.view.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.http.ILoading;
import com.xiaoningmeng.R;

public class TextDialogLoading implements ILoading {

	private BaseDialog mDialog;
	private Context mContext;
	private AnimationDrawable mLoadingAnimDrawable;
	private View loadingView;
	private TextView mLoadingTv;

	public TextDialogLoading(Context context) {

		mContext = context;
		mDialog = new BaseDialog.Builder(context).setGravity(Gravity.CENTER)
				.setDialogStyle(R.style.loading_dialog_style).create();
		mDialog.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loadingView = inflater.inflate(R.layout.dialog_login_loading, null);
		mLoadingTv = (TextView) loadingView.findViewById(R.id.tv_dialog_loading);
	}

	/**
	 * 开始加载
	 */
	@Override
	public void startLoading() {

		if (mContext != null) {
			ImageView progressView = (ImageView) loadingView
					.findViewById(R.id.iv_dialog_loading);
			if (mLoadingAnimDrawable == null) {
				mLoadingAnimDrawable = (AnimationDrawable) mContext
						.getResources().getDrawable(
								R.drawable.loading_anim_list);
			}
			progressView.setImageDrawable(mLoadingAnimDrawable);
			mLoadingAnimDrawable.start();
			if (mDialog != null) {
				mDialog.warpShow(loadingView);
			}
		}
	}

	/**
	 * 完成加载
	 */
	@Override
	public void stopLoading() {

		if (mLoadingAnimDrawable != null)
			mLoadingAnimDrawable.stop();
		if (mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
	}
	
	public void setLoadingTip(String loading){
		mLoadingTv.setText(loading);
	}
	
	

}
