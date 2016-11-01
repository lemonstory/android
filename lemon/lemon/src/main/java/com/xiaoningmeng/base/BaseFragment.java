package com.xiaoningmeng.base;

import com.xiaoningmeng.R;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BaseFragment extends Fragment {

	protected boolean isLandscape;
	private ViewGroup parent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		} else {
			isLandscape = false;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T getViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	public void startActivityForNew(Intent intent) {
		startActivity(intent);
		animationForNew();
	}

	public void animationForNew() {
		getActivity().overridePendingTransition(R.anim.main_translatex100to0,
				R.anim.main_translatex0tof100);
	}

	public void animationForOld() {
		getActivity().overridePendingTransition(R.anim.main_translatexf100to0,
				R.anim.main_translatex0to100);
	}

	public BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}

	public void animationForBottom() {
		getActivity().overridePendingTransition(R.anim.main_translatey100to0,
				R.anim.main_translatey0tof100);
	}

	public void animationForOTop() {
		getActivity().overridePendingTransition(R.anim.main_translateyf100to0,
				R.anim.main_translatey0to100);
	}


	public void showEmptyTip(ViewGroup parentView, String tip,
			OnClickListener onClickListener) {

		if (parentView == null) {
			this.parent = ((ViewGroup) getView());
		} else {
			this.parent = parentView;
		}
		if(this.parent == null){
			return;
		}
		TextView emptyTv = (TextView) parent.findViewById(R.id.tv_empty_tip);
		if (emptyTv == null) {
			emptyTv = (TextView) View.inflate(getActivity(),R.layout.fragment_empty, null);
			parent.addView(emptyTv);
		}
		if (onClickListener != null)
			emptyTv.setOnClickListener(onClickListener);
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = parent.getChildAt(i);
			view.setVisibility(View.INVISIBLE);
		}
		emptyTv.setText(tip);
		emptyTv.setVisibility(View.VISIBLE);
	}
	

	public void showEmptyTip(ViewGroup parentView, String tip,int paddingTop) {

		if (parentView == null) {
			this.parent = ((ViewGroup) getView());
		} else {
			this.parent = parentView;
		}
		TextView emptyTv = (TextView) parent.findViewById(R.id.tv_empty_tip);
	
		if (emptyTv == null) {
			emptyTv = (TextView) View.inflate(getActivity(),
					R.layout.fragment_empty, null);
			if(parent instanceof RelativeLayout){
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				emptyTv.setLayoutParams(lp);
			}
			emptyTv.setPadding(0, paddingTop, 0, 0);
			parent.addView(emptyTv);
		}
		
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = parent.getChildAt(i);
			view.setVisibility(View.INVISIBLE);
		}
		emptyTv.setText(tip);
		emptyTv.setVisibility(View.VISIBLE);
	}

	public void hideEmptyTip() {
		if (parent != null) {
			TextView emptyTv = (TextView) parent
					.findViewById(R.id.tv_empty_tip);
			if (emptyTv != null && emptyTv.getVisibility() == View.VISIBLE) {
				int childCount = parent.getChildCount();
				for (int i = 0; i < childCount; i++) {
					View view = parent.getChildAt(i);
					view.setVisibility(View.VISIBLE);
				}
				emptyTv.setVisibility(View.INVISIBLE);
			}
		}
	}


}
