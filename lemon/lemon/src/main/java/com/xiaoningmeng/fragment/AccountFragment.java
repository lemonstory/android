package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xiaoningmeng.PerasonalActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.RankActivity;
import com.xiaoningmeng.SettingActivity;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.utils.AvatarUtils;
import com.ypy.eventbus.EventBus;

public class AccountFragment extends BaseFragment implements OnClickListener {

	private View mAccountHeadView;
	private TextView mAccountNameTv;
	private TextView mAccountContentTv;
	private TextView mAccountUnloginTv;
	private ImageView mAccountAvatarView;
	private View recommendView;
	private UserInfo mUserInfo;
	//private RecommendAd recommendAd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_account, null);
		contentView.findViewById(R.id.rl_account_setting).setOnClickListener(
				this);
		contentView.findViewById(R.id.rl_account_perasonal).setOnClickListener(
				this);
		contentView.findViewById(R.id.rl_account_Ranking).setOnClickListener(
				this);
		contentView.findViewById(R.id.rl_account_shop).setOnClickListener(this);
		recommendView =  contentView.findViewById(R.id.rl_account_Recommend);
		//recommendView.setOnClickListener(this);
		mAccountHeadView = contentView.findViewById(R.id.ll_account_head);
		mAccountAvatarView = (ImageView) contentView.findViewById(R.id.img_account_perasonal);
		mAccountNameTv = (TextView) contentView.findViewById(R.id.tv_account_name);
		mAccountContentTv = (TextView) contentView.findViewById(R.id.tv_account_content);
		mAccountUnloginTv = (TextView) contentView.findViewById(R.id.tv_account_unlogin);
		mUserInfo = MyApplication.getInstance().userInfo;
		setUserInfo();
		EventBus.getDefault().register(this);
		//loadRemmend();
		return contentView;
	}

	/*private void loadRemmend() {
		RecommendAd.Builder builder = new RecommendAd.Builder(recommendView, Constant.BAIDU_AD_APP_ID);
		builder.setEventListener(new RecmdEventListener(){

			@Override
			public void onIconBindFailed(String reason) {
			}

			@Override
			public void onIconClick() {
			}

			@Override
			public void onIconShow() {
			}
			
		});
		recommendAd = builder.build();
		recommendAd.load(getActivity());
		
	}*/

	private void setUserInfo() {
		if (MyApplication.getInstance().isIsLogin() && mUserInfo != null) {
			mAccountHeadView.setVisibility(View.VISIBLE);
			mAccountUnloginTv.setVisibility(View.INVISIBLE);
			mAccountNameTv.setText(mUserInfo.getNickname());
			String age = mUserInfo.getAge() == null ?"":(mUserInfo.getAge()+"岁");
			String province = mUserInfo.getProvince() == null ?" ":(" "+mUserInfo.getProvince()+" ");
			String city = mUserInfo.getCity() == null?"":mUserInfo.getCity();
			mAccountContentTv.setText(age + province + city);
			String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
			ImageLoader.getInstance().displayImage(avatarUrl, mAccountAvatarView,Constant.AVARAR_OPTIONS);
		} else {
			mAccountHeadView.setVisibility(View.INVISIBLE);
			mAccountUnloginTv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account_setting:
			((BaseFragmentActivity) getActivity())
					.startActivityForNew(new Intent(getActivity(),
							SettingActivity.class));
			break;
		case R.id.rl_account_perasonal:
			if (UserAuth.auditUser(getActivity(), null)) {
				((BaseFragmentActivity) getActivity())
						.startActivityForNew(new Intent(getActivity(),
								PerasonalActivity.class));
			}
			break;
		case R.id.rl_account_shop:
			String shopUrl = OnlineConfigAgent.getInstance().getConfigParams(getActivity(),  Constant.SHOP_CONFIG_PARAMS);
			WebViewActivity.openWebView(getActivity(), shopUrl);
			break;
		case R.id.rl_account_Ranking:
			((BaseFragmentActivity) getActivity()).startActivityForNew(new Intent(getActivity(),RankActivity.class));
			break;
		case R.id.rl_account_Recommend:
			//((BaseFragmentActivity) getActivity()).startActivityForNew(new Intent(getActivity(),RecommendAdActivity.class));
			break;
		default:
			break;
		}
	}
	
	public void onEventMainThread(LoginEvent event){
		this.mUserInfo = event.userInfo;
		setUserInfo();
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
