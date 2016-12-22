package com.xiaoningmeng.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BaseFragment extends Fragment {

	protected boolean isLandscape;
	private ViewGroup parent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
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
		getActivity().overridePendingTransition(com.xiaoningmeng.R.anim.main_translatex100to0,
				com.xiaoningmeng.R.anim.main_translatex0tof100);
	}

	public void animationForOld() {
		getActivity().overridePendingTransition(com.xiaoningmeng.R.anim.main_translatexf100to0,
				com.xiaoningmeng.R.anim.main_translatex0to100);
	}

	public BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}

	public void animationForBottom() {
		getActivity().overridePendingTransition(com.xiaoningmeng.R.anim.main_translatey100to0,
				com.xiaoningmeng.R.anim.main_translatey0tof100);
	}

	public void animationForOTop() {
		getActivity().overridePendingTransition(com.xiaoningmeng.R.anim.main_translateyf100to0,
				com.xiaoningmeng.R.anim.main_translatey0to100);
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
		TextView emptyTv = (TextView) parent.findViewById(com.xiaoningmeng.R.id.tv_empty_tip);
		if (emptyTv == null) {
			emptyTv = (TextView) View.inflate(getActivity(), com.xiaoningmeng.R.layout.fragment_empty, null);
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
		TextView emptyTv = (TextView) parent.findViewById(com.xiaoningmeng.R.id.tv_empty_tip);
	
		if (emptyTv == null) {
			emptyTv = (TextView) View.inflate(getActivity(),
					com.xiaoningmeng.R.layout.fragment_empty, null);
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
					.findViewById(com.xiaoningmeng.R.id.tv_empty_tip);
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

	protected View getFooterView() {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		View view = layoutInflater.inflate(com.xiaoningmeng.R.layout.list_footer_view, null);
		//view.findViewById(R.id.tv).setVisibility(View.GONE);
		view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "click View", Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}
}
