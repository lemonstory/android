package com.xiaoningmeng.fragment;

import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.PerasonalActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.view.dialog.BaseDialog;
import com.xiaoningmeng.view.picker.DatePicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class WeclomeFragment extends BaseFragment implements OnClickListener {

	private View mFemaleView;
	private View mMaleView;
	private int sex = 0;
	private TextView mYearEt;
	private TextView mMonthEt;
	private TextView mDayEt;
	private BaseDialog mDialog;
	private View mAgeView;
	private BaseFragmentActivity mContext;
	private  static boolean isBrithEdit;
	public static boolean isGenderEdit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int page = getArguments().getInt("page");
		mContext = (BaseFragmentActivity) getActivity();
		View containerView;
		if (page == 0) {
			containerView = inflater.inflate(R.layout.fragment_welcome1, null);
			mYearEt = (TextView) containerView.findViewById(R.id.et_login_year);
			mMonthEt = (TextView) containerView.findViewById(R.id.et_login_month);
			mDayEt = (TextView) containerView.findViewById(R.id.et_login_day);
			containerView.findViewById(R.id.ll_birth).setOnClickListener(this);
		} else {
			containerView = inflater.inflate(R.layout.fragment_welcome2, null);
			mFemaleView = containerView.findViewById(R.id.ll_login_female);
			mFemaleView.setOnClickListener(this);
			mMaleView = containerView.findViewById(R.id.ll_login_male);
			mMaleView.setOnClickListener(this);
		}
		return containerView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_login_female:
			sex = 2;
			mFemaleView.setSelected(true);
			mMaleView.setSelected(false);
			modifySex(sex);
			break;
		case R.id.ll_login_male:
			sex = 1;
			mMaleView.setSelected(true);
			mFemaleView.setSelected(false);
			modifySex(sex);
			break;
		case R.id.ll_birth:
			initAgeDialog();
			break;
		}
	}
	
	private void initAgeDialog() {
		
		if(mDialog == null){
			mDialog = new BaseDialog.Builder(mContext)
			.setGravity(android.view.Gravity.BOTTOM)
			.setAnimStyle(R.style.bottom_dialog_animation).create();
		}
		if (mAgeView == null) {
			mAgeView = View.inflate(mContext, R.layout.dialog_modify_age, null);
			final DatePicker mDataPicker = (DatePicker) mAgeView.findViewById(R.id.datePicker);

			mAgeView.findViewById(R.id.tv_dialog_select).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							String birthday = mDataPicker.getDate();
							String[] data= mDataPicker.getDateArray();
							mYearEt.setText(data[0]);
							mMonthEt.setText(data[1]);
							mDayEt.setText(data[2]);
							int ageBegin = PerasonalActivity.getAge(birthday);
							final int age = ageBegin <0 ? 0 : ageBegin;
							mDialog.dismiss();
							LHttpRequest.getInstance().setUserInfoReq(mContext, null, null, birthday, null, null, null, null, null,null, new LHttpHandler<String>(mContext) {

								@Override
								public void onGetDataSuccess(String data) {
									MyApplication.getInstance().userInfo.setAge(age+"");
									isBrithEdit = true;
									goHomeActivity();
								}
							});

						}
					});
			mAgeView.findViewById(R.id.tv_dialog_cancel).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							mDialog.dismiss();
						}
					});
		}
		mDialog.show(mAgeView);
	}
	
	
	private void modifySex(final int sex){
		LHttpRequest.getInstance().setUserInfoReq(mContext, null, sex+"", null, null, null, null, null, null,null, new LHttpHandler<String>(mContext) {

			@Override
			public void onGetDataSuccess(String data) {
				MyApplication.getInstance().userInfo.setGender(sex+"");
				isGenderEdit = true;
				goHomeActivity();
				
			}
			
		});
		
	}
	
	private void goHomeActivity(){
		
		if(isBrithEdit && isGenderEdit){
			
			startActivityForNew(new Intent(mContext, HomeActivity.class));
			mContext.oldFinish();
		}
	}
}
