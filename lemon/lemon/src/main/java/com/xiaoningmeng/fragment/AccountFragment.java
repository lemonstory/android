package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AccountActivity;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.RankActivity;
import com.xiaoningmeng.SettingActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.event.LogoutEvent;
import com.xiaoningmeng.utils.AvatarUtils;

import de.greenrobot.event.EventBus;

public class AccountFragment extends BaseFragment implements OnClickListener {

    private View mAccountHeadView;
    private TextView mAccountNameTv;
    private TextView mAccountContentTv;
    private TextView mAccountUnloginTv;
    private ImageView mAccountAvatarView;
    //private View recommendView;
    private UserInfo mUserInfo;
    //private RecommendAd recommendAd;
    private RelativeLayout mAccountPostView;
    private View mAccountRankingDivider;
    private RelativeLayout mAccountRankingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_account, null);
        contentView.findViewById(R.id.rl_account_setting).setOnClickListener(this);
        contentView.findViewById(R.id.rl_account_perasonal).setOnClickListener(this);
        contentView.findViewById(R.id.rl_account_Ranking).setOnClickListener(this);
//		contentView.findViewById(R.id.rl_account_shop).setOnClickListener(this);
        //recommendView =  contentView.findViewById(R.id.rl_account_Recommend);
//		recommendView.setOnClickListener(this);
        mAccountHeadView = contentView.findViewById(R.id.ll_account_head);
        mAccountAvatarView = (ImageView) contentView.findViewById(R.id.img_account_perasonal);
        mAccountNameTv = (TextView) contentView.findViewById(R.id.tv_account_name);
        mAccountContentTv = (TextView) contentView.findViewById(R.id.tv_account_content);
        mAccountUnloginTv = (TextView) contentView.findViewById(R.id.tv_account_unlogin);
        mAccountRankingDivider = contentView.findViewById(R.id.account_ranking_divider);
        mAccountRankingView = (RelativeLayout) contentView.findViewById(R.id.rl_account_Ranking);
        mUserInfo = MyApplication.getInstance().userInfo;
        setUserInfo();
        EventBus.getDefault().register(this);
        return contentView;
    }

    public void onResume() {

        super.onResume();
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_account");
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mAccountRankingView.getLayoutParams();

        if (UserAuth.getInstance().isLogin(this.getActivity()) && mUserInfo != null) {
            mAccountHeadView.setVisibility(View.VISIBLE);
            mAccountUnloginTv.setVisibility(View.INVISIBLE);
            linearParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.account_item_inv), 0, 0);
            mAccountRankingView.setLayoutParams(linearParams);
            mAccountNameTv.setText(mUserInfo.getNickname());
            String age = mUserInfo.getAge() == null ? "" : (mUserInfo.getAge() + "岁");
            String province = mUserInfo.getProvince() == null ? " " : (" " + mUserInfo.getProvince() + " ");
            String city = mUserInfo.getCity() == null ? "" : mUserInfo.getCity();
            mAccountContentTv.setText(age + province + city);
            String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
            Uri avatarUri = Uri.parse(avatarUrl);
            mAccountAvatarView.setImageURI(avatarUri);

        } else {

            mAccountHeadView.setVisibility(View.INVISIBLE);
            mAccountUnloginTv.setVisibility(View.VISIBLE);
            linearParams.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.account_item_inv), 0, 0);
            mAccountRankingView.setLayoutParams(linearParams);
            Uri avatarUri = new Uri.Builder().scheme(UriUtil.LOCAL_RESOURCE_SCHEME).path(String.valueOf(R.drawable.bg_avatar_default)).build();
            mAccountAvatarView.setImageURI(avatarUri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_account_setting:
                Intent accountSettingIntent = new Intent(getActivity(), SettingActivity.class);
                accountSettingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                ((BaseActivity) getActivity()).startActivityForNew(accountSettingIntent);
                break;
            case R.id.rl_account_perasonal:

                if (UserAuth.getInstance().isLogin(getActivity())) {
                    ((BaseActivity) getActivity()).startActivityForNew(new Intent(getActivity(), AccountActivity.class));
                } else {
                    ((BaseActivity) getActivity()).startActivityForNew(new Intent(getActivity(), LoginActivity.class));
                }
                break;
//		case R.id.rl_account_shop:
//			String shopUrl = OnlineConfigAgent.getInstance().getConfigParams(getActivity(),  Constant.SHOP_CONFIG_PARAMS);
//			WebViewActivity.openWebView(getActivity(), shopUrl);
//			break;
            case R.id.rl_account_Ranking:
                Intent rankIntent = new Intent(getActivity(), RankActivity.class);
                rankIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                ((BaseActivity) getActivity()).startActivityForNew(rankIntent);
                break;
            //case R.id.rl_account_Recommend:
            //((BaseFragmentActivity) getActivity()).startActivityForNew(new Intent(getActivity(),RecommendAdActivity.class));
            //	break;

            default:
                break;
        }
    }

    public void onEventMainThread(LoginEvent event) {

        this.mUserInfo = event.userInfo;
        setUserInfo();
//		getUserProfileData(this.mUserInfo.getUid());
    }

    public void onEventMainThread(LogoutEvent event) {

        Logger.d("------- onEventMainThread : LogoutEvent -------");
        setUserInfo();
    }

//	public void getUserProfileData(String uid) {
//
//		LHttpRequest.getInstance().getUserProfile(this.getActivity(),
//				new LHttpHandler<String>(this.getActivity()) {
//
//					@Override
//					public void onGetDataSuccess(String data) {
//
//						try {
//
//							JSONObject jsonObject = new JSONObject(data);
//							JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
//
//							Gson gson = new Gson();
//							if (variablesObject.has("space")) {
//
//								//设置帖子数
//								JSONObject spaceObject = new JSONObject(variablesObject.getString("space"));
//								if (spaceObject.has("uid") && spaceObject.has("threads")) {
//									if (mUserInfo.getUid().equals(spaceObject.getString("uid"))) {
//										mAccountPostTv.setText(spaceObject.getString("threads"));
//									}
//								}
//							}
//
//						} catch (JSONException e) {
//
//							e.printStackTrace();
//						}
//					}
//				},uid);
//	}

}
