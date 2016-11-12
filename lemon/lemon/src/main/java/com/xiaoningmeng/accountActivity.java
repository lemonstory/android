package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BasePohotoActivity;
import com.xiaoningmeng.base.BasePohotoActivity.IUploadCall;
import com.xiaoningmeng.bean.Address;
import com.xiaoningmeng.bean.City;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Province;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.bean.Zone;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.view.dialog.BaseDialog;
import com.xiaoningmeng.view.picker.DatePicker;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.greenrobot.event.EventBus;

public class AccountActivity extends BasePohotoActivity implements
		OnClickListener, PlayObserver,IUploadCall{

	private BaseDialog mDialog;
	private View mAgeView;
	private TextView mSexTv;
	private TextView mAddressTv;
	private SimpleDraweeView mAvatarImg;
	private ProgressBar mUploadingProgress;
	private TextView mNameTv;
	private TextView mPhoneTv;
	private TextView mGoodsAddressTv;
	private TextView mAgeTv;
	private UserInfo mUserInfo;
	private int type = -1;
	private ImageView mWaveImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.PickTheme);
		setContentView(R.layout.activity_account);
		mUserInfo = MyApplication.getInstance().userInfo;
		initView();
		PlayerManager.getInstance().register(this);
	}

	public void initView() {
		setTitleName("个人信息");
		mWaveImg = (ImageView) findViewById(R.id.img_head_right);
		setRightHeadIcon(R.drawable.play_flag_wave_01);
		mSexTv = (TextView) findViewById(R.id.tv_perasonal_sex);
		mAddressTv = (TextView) findViewById(R.id.tv_perasonal_address);
		mAvatarImg = (SimpleDraweeView) findViewById(R.id.img_perasonal_icon);
		mAvatarImg.setOnClickListener(this);
		mNameTv = (TextView) findViewById(R.id.tv_perasonal_name);
		mPhoneTv = (TextView) findViewById(R.id.tv_perasonal_phone);
		mGoodsAddressTv = (TextView) findViewById(R.id.tv_perasonal_goods_address);
		mAgeTv = (TextView) findViewById(R.id.tv_perasonal_age);
		mUploadingProgress = (ProgressBar) findViewById(R.id.pb_perasonal_progress);
		setUserView();
		requestUserInfo();
		
	new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setLoadingTip("修改中");
				
			}
		}, 500);
	}

	private void setUserView() {
		if (mUserInfo != null) {
			mNameTv.setText(mUserInfo.getNickname());
			String province = mUserInfo.getProvince() == null ?" ":(" "+mUserInfo.getProvince()+" ");
			String city = mUserInfo.getCity() == null?"":mUserInfo.getCity();
			String area = mUserInfo.getArea() == null?"":(" "+mUserInfo.getArea());
			mAddressTv.setText(province+city+area);
			if(mUserInfo.getGender() != null){
				mSexTv.setText(Integer.parseInt(mUserInfo.getGender()) == 1 ? "男" : "女");
			}else{
				mSexTv.setText("");
			}
			mPhoneTv.setText(mUserInfo.getPhonenumber());
			mAgeTv.setText(mUserInfo.getAge());
			Address address = mUserInfo.getAddressinfo();
			if(address != null && address.getProvince()!= null){
				mGoodsAddressTv.setText(address.getProvince()+" "+address.getCity()+(address.getArea()!= null ?" "+address.getArea():" ")+address.getAddress());
			}
			String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
			mAvatarImg.setImageURI(Uri.parse(avatarUrl));
			//ImageLoader.getInstance().displayImage(avatarUrl, mAvatarImg,Constant.AVARAR_OPTIONS);
		}		
	}

	public void requestUserInfo(){
		LHttpRequest.getInstance().getUserInfoReq(this, new JsonCallback<UserInfo>() {
			
			@Override
			public void onGetDataSuccess(UserInfo data) {
				if(data != null){
					MyApplication.getInstance().userInfo = data;
					mUserInfo= data;
					DataSupport.deleteAll(UserInfo.class);
					mUserInfo.save();
					setUserView();
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_perasonal_icon:
			selectPic(this,true);
			break;
		case R.id.rl_perasonal_address:
			type = AccountModifyInfoActivity.MODIFY_ADDRESS_PROVINCES;
			AccountModifyInfoActivity.start(this, type);
			break;
		case R.id.rl_perasonal_name:
			type = AccountModifyInfoActivity.MODIFY_NAME;
			AccountModifyInfoActivity.start(this, type);
			break;
		case R.id.rl_perasonal_sex:
			type = AccountModifyInfoActivity.MODIFY_SEX;
			AccountModifyInfoActivity.start(this, type);
			break;
		case R.id.rl_perasonal_phone:
			type = AccountModifyInfoActivity.MODIFY_PHONE;
			AccountModifyInfoActivity.start(this, type);
			break;
		case R.id.rl_perasonal_goods_address:
			type = AccountModifyInfoActivity.MODIFY_GOODS_ADDRESS;
			AccountModifyInfoActivity.start(this, type);
			break;
		case R.id.rl_perasonal_age:
			initAgeDialog();
			break;
		case R.id.img_perasonal_icon:
			String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
			Intent i = new Intent(this,ImageViewerPagerActivity.class);
			ArrayList<String> imageUrls = new ArrayList<>();
			imageUrls.add(avatarUrl);
			i.putStringArrayListExtra("imagesUrl",imageUrls);
			startActivity(i);
			break;
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
		}
		return super.onKeyDown(keyCode, event);
	}
	

	private void initAgeDialog() {
		
		if(mDialog == null){
			mDialog = new BaseDialog.Builder(this)
			.setGravity(android.view.Gravity.BOTTOM)
			.setAnimStyle(R.style.bottom_dialog_animation).create();
		}
		if (mAgeView == null) {
			mAgeView = View.inflate(this, R.layout.dialog_modify_age, null);
			final DatePicker mDataPicker = (DatePicker) mAgeView
					.findViewById(R.id.datePicker);

			mAgeView.findViewById(R.id.tv_dialog_select).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							String birthday = mDataPicker.getDate();
							int ageBegin = getAge(birthday);
							final int age = ageBegin <0 ? 0 : ageBegin;
							mDialog.dismiss();
							LHttpRequest.getInstance().setUserInfoReq(AccountActivity.this, null, null, birthday, null, null, null, null, null,null, new JsonCallback<String>(AccountActivity.this) {

								@Override
								public void onGetDataSuccess(String data) {
									mAgeTv.setText(age+"");
									MyApplication.getInstance().userInfo.setAge(age+"");
									EventBus.getDefault().post(mUserInfo);
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
	
	public static int getAge(String birth){
		Date birthDay;
		try {
			birthDay = new SimpleDateFormat("yyyy-MM-dd").parse(birth);
			 Calendar cal = Calendar.getInstance();

		        if (cal.before(birthDay)) {
		           return 0;
		        }
		        int yearNow = cal.get(Calendar.YEAR);
		        int monthNow = cal.get(Calendar.MONTH);
		        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		        cal.setTime(birthDay);

		        int yearBirth = cal.get(Calendar.YEAR);
		        int monthBirth = cal.get(Calendar.MONTH);
		        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		        int age = yearNow - yearBirth;
		        if (monthNow <= monthBirth) {
		            if (monthNow == monthBirth) {
		                //monthNow==monthBirth
		                if (dayOfMonthNow < dayOfMonthBirth) {
		                    age--;
		                } else {
		                    //do nothing
		                }
		            } else {
		                //monthNow>monthBirth
		                age--;
		            }
		        } else {
		            //monthNow<monthBirth
		            //donothing
		        }
		        return age;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return 0;
    }



	private void uploadAvatar(String filePath) {

		File avatarFile = new File(filePath);
		mAvatarImg.setImageURI(Uri.fromFile(avatarFile));
		mUploadingProgress.setVisibility(View.VISIBLE);
		if(avatarFile.exists()){
			LHttpRequest.getInstance().setAvatarReq(this, filePath,new SaveCallback(){
						
						@Override
						public void onProgress(String arg0, int arg1, int arg2) {
							
						}
						
						@Override
						public void onFailure(String arg0, OSSException arg1) {
							String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
							mAvatarImg.setImageURI(Uri.parse(avatarUrl));
							Toast.makeText(AccountActivity.this, "修改头像失败", Toast.LENGTH_SHORT).show();
							mUploadingProgress.setVisibility(View.INVISIBLE);
							
						}
						
						@Override
						public void onSuccess(String arg0) {
							uploadAvatartime();
							
						}

					
						
					});
		}else{
			Toast.makeText(this, "修改头像失败", Toast.LENGTH_SHORT).show();
		}
		
	}

	private void uploadAvatartime() {
		final long avatartime = System.currentTimeMillis()/1000;
		LHttpRequest.getInstance().setUserInfoReq(AccountActivity.this, null, null, null, null, null, null, null, null,avatartime+"", new JsonCallback<String>() {

			@Override
			public void onGetDataSuccess(String data) {
				MyApplication.getInstance().userInfo.setAvatartime(avatartime+"");
				mUserInfo.setAvatartime(avatartime+"");
				EventBus.getDefault().post(mUserInfo);
				mUploadingProgress.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onFailure(String responseString) {
				String avatarUrl = AvatarUtils.getAvatarUrl(mUserInfo.getUid(), mUserInfo.getAvatartime(), -1);
				mAvatarImg.setImageURI(Uri.parse(avatarUrl));
				Toast.makeText(AccountActivity.this, "修改头像失败", Toast.LENGTH_SHORT).show();
				mUploadingProgress.setVisibility(View.INVISIBLE);

			}
		});
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if ("modifyAddress".equals(intent.getAction())) {
			Province province = intent.getParcelableExtra("province");
			City city = intent.getParcelableExtra("city");
			Zone zone = null;
			if (intent.hasExtra("zone")) {
				zone = intent.getParcelableExtra("zone");
			}
			mAddressTv.setText(province.getProName() + " " + city.getCityName()
					+ (zone != null ? " " + zone.getZoneName() : ""));
			mUserInfo = MyApplication.getInstance().userInfo;
			EventBus.getDefault().post(mUserInfo);
		}else{
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
		if (type == -1) {
			return;
		}
		mUserInfo = MyApplication.getInstance().userInfo;
		switch (type) {
		case AccountModifyInfoActivity.MODIFY_NAME:
			mNameTv.setText(mUserInfo.getNickname());
			EventBus.getDefault().post(mUserInfo);
			break;
		case AccountModifyInfoActivity.MODIFY_GOODS_ADDRESS:
			Address address = mUserInfo.getAddressinfo();
			if(address != null && address.getProvince() != null){
				mGoodsAddressTv.setText(address.getProvince()+" "+address.getCity()+
						(address.getArea()!= null ?" "+address.getArea():" ")+address.getAddress());
			}
			break;
		case AccountModifyInfoActivity.MODIFY_SEX:
			if(mUserInfo.getGender() != null){
				mSexTv.setText(Integer.parseInt(mUserInfo.getGender()) == 1 ? "男" : "女");
			}
			break;
		case AccountModifyInfoActivity.MODIFY_PHONE:
			mPhoneTv.setText(mUserInfo.getPhonenumber());
			break;
		default:
			break;
		}

	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);

	}


	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		if(mUserInfo!= null){
			DataSupport.deleteAll(UserInfo.class);
			mUserInfo.save();
		}
		super.onDestroy();
	}

	@Override
	public void gpuback(File file) {
		uploadAvatar(file.getAbsolutePath());
		
	}

}
