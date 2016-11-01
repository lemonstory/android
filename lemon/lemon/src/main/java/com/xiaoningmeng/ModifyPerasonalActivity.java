package com.xiaoningmeng;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.xiaoningmeng.adapter.AddressAdapter;
import com.xiaoningmeng.adapter.AreaAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Address;
import com.xiaoningmeng.bean.City;
import com.xiaoningmeng.bean.Province;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.bean.Zone;
import com.xiaoningmeng.db.AreaDao;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.TelUtil;
import com.xiaoningmeng.utils.UiUtils;
import com.xiaoningmeng.view.dialog.BaseDialog;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.xiaoningmeng.view.picker.AreaPicker;

public class ModifyPerasonalActivity extends BaseActivity implements
		OnClickListener {

	private int type = 0;
	public static final int MODIFY_NAME = 0;
	public static final int MODIFY_SEX = 1;
	public static final int MODIFY_GOODS_ADDRESS = 2;
	public static final int ADD_GOODS_ADDRESS = 7;
	public static final int MODIFY_PHONE = 3;
	public static final int MODIFY_ADDRESS_PROVINCES = 4;
	public static final int MODIFY_ADDRESS_CITIES = 5;
	public static final int MODIFY_ADDRESS_ZONES = 6;
	private EditText mModifyNameEt;
	private ImageView mModifyDelImg;
	private EditText mModifyPhoneEt;
	private ImageView mFemaleImg;
	private ImageView mMaleImg;
	private UserInfo mUserInfo;
	private int mSex;
	private String mPhone;
	private String mName;
	private XListView mListView;
	private Province province;
	private City city;
	private Zone zone;
	private List<Province> provinces;
	private List<City> cities;
	private List<Zone> zones;
	private AreaAdapter<?> mAdapter;
	private BaseDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra("type", 0);
		mUserInfo = MyApplication.getInstance().userInfo;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setLoadingTip("修改中");
				
			}
		}, 500);
		//Slidr.attach(this);
		switch (type) {
		case MODIFY_NAME:
			initModifyName();
			setRightHeadText("保存");
			break;
		case ADD_GOODS_ADDRESS:
			initAddGoodsAddress();
			break;
		case MODIFY_ADDRESS_ZONES:
		case MODIFY_ADDRESS_PROVINCES:
		case MODIFY_ADDRESS_CITIES:
			initModifyAddress();
			break;
		case MODIFY_SEX:
			initModifySex();
			setRightHeadText("保存");
			break;
		case MODIFY_PHONE:
			initModifyPhone();
			setRightHeadText("保存");
			break;
		case MODIFY_GOODS_ADDRESS:
			initModifyGoodsAddress();
			break;
		default:
			break;
		}

	}
	
	private EditText mNameEt;
	private EditText mPhoneEt;
	private TextView mAreaTv;
	private EditText mZipCodeEt;
	private EditText mStreetEt;
	private String[] mAreas;
	private Address mAddress = null;

	private void initAddGoodsAddress() {
		setTheme(R.style.PickTheme);
		setContentView(R.layout.activity_modify_goods_address);
		if(getIntent().getSerializableExtra("address")!= null){
			mAddress = (Address) getIntent().getSerializableExtra("address");
		}
		setTitleName("收货地址");
		setRightHeadText("保存");
		mDialog = new BaseDialog.Builder(this)
			.setGravity(android.view.Gravity.BOTTOM)
			.setAnimStyle(R.style.bottom_dialog_animation).create();
		mNameEt = (EditText) findViewById(R.id.et_address_name);
		mPhoneEt = (EditText) findViewById(R.id.et_address_phone);
		mZipCodeEt= (EditText)findViewById(R.id.et_address_zip_code);
		mStreetEt = (EditText)findViewById(R.id.et_address_street);
		mAreaTv = (TextView)findViewById(R.id.tv_address_area);
		mPhoneEt.setInputType(EditorInfo.TYPE_CLASS_PHONE); 
		mZipCodeEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		if(mAddress != null){
			mAreas = new String[3];
			mAreas[0] = mAddress.getProvince();
			mAreas[1] = mAddress.getCity();
			mAreas[2] = mAddress.getArea();
			mNameEt.setText(mAddress.getName());
			mPhoneEt.setText(mAddress.getPhonenumber());
			String area = mAddress.getArea()!= null?mAddress.getArea() :"";
			mAreaTv.setText(mAddress.getProvince()+mAddress.getCity()+ area);
			mStreetEt.setText(mAddress.getAddress());
			mZipCodeEt.setText(mAddress.getEcode());
		}
	}

	private View mAreaView;

	private void initAreaDialog() {
		if (mAreaView == null) {
			mAreaView = View.inflate(this, R.layout.dialog_modify_address, null);
			mAreaView.findViewById(R.id.tv_dialog_cancel).setOnClickListener(this);
			final AreaPicker mAreaPicker = (AreaPicker) mAreaView.findViewById(R.id.areaPicker);
			mAreaView.findViewById(R.id.tv_dialog_select).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							mAreas = mAreaPicker.getArea();
							mAreaTv.setText(mAreaPicker.getAreaName());
							mDialog.dismiss();

						}
					});
		}
		mDialog.show(mAreaView);
	}

	private List<Address> mAddresses;
	private AddressAdapter mAddressAdapter;
	private View mAddressDividerV;
	private void initModifyGoodsAddress() {
		setContentView(R.layout.activity_modify_address);
		setTitleName("收货地址");
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mAddresses = new ArrayList<>();
		mAddressAdapter = new AddressAdapter(this, mAddresses);
		View headView = new View(this);
		headView.setLayoutParams( new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 
				getResources().getDimensionPixelSize(R.dimen.account_item_margin_right)));
		headView.setBackgroundColor(getResources().getColor(R.color.home_bg));
		mListView.addHeaderView(headView,null,false);
		View footView = View.inflate(this, R.layout.layout_add_address, null);
		mAddressDividerV = footView.findViewById(R.id.v_add_address);
		mListView.addFooterView(footView,null,false);
		mListView.setAdapter(mAddressAdapter);
		requestAddresses();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position -2;
				Address address = mAddresses.get(pos);
				Intent i = new Intent(ModifyPerasonalActivity.this, ModifyPerasonalActivity.class);
				i.putExtra("type", ModifyPerasonalActivity.ADD_GOODS_ADDRESS);
				i.putExtra("address", address);
				startActivityForResult(i, 0);
				//saveUserInfo(null, null, null, null,null, null, null, address);
			}
		});
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				SwipeMenuItem openItem = new SwipeMenuItem(
						ModifyPerasonalActivity.this);
				openItem.setBackground(new ColorDrawable(getResources()
						.getColor(R.color.logout_bg_normal)));
				openItem.setWidth(UiUtils.getInstance(
						ModifyPerasonalActivity.this).DipToPixels(90));
				openItem.setTitle("删除");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};

		// set creator
		((SwipeMenuListView) mListView).setMenuCreator(creator);
		((SwipeMenuListView) mListView).setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
		((SwipeMenuListView) mListView).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					delAddress(position);
					break;
				}
				return false;
			}

		
		});
	}
	
	private void delAddress(final int position) {
		String addressId = mAddresses.get(position).getId();
		LHttpRequest.getInstance().delAddressReq(ModifyPerasonalActivity.this, addressId, new JsonCallback<Address>(this) {

			@Override
			public void onGetDataSuccess(Address data) {
				mAddresses.remove(position);
				mAddressAdapter.notifyDataSetChanged();
				mAddressDividerV.setVisibility(mAddresses.size() == 0? View.GONE:View.VISIBLE);
			}
			
		});
	}
	private void requestAddresses(){
		LHttpRequest.getInstance().getAddressListReq(this, new JsonCallback<List<Address>>() {

			@Override
			public void onGetDataSuccess(List<Address> data) {
				if(data != null){
					mAddresses.addAll(data);
					mAddressAdapter.notifyDataSetChanged();
					mAddressDividerV.setVisibility(mAddresses.size() == 0? View.GONE:View.VISIBLE);
				}
			}
		});
	}
	

	private void initModifyAddress() {
		setContentView(R.layout.activity_modify_address);
		setTitleName("地区");
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		View headView = View.inflate(this, R.layout.layout_area_head, null);
		mListView.addHeaderView(headView);
		AreaDao areaDao = new AreaDao(this);
		if (type == MODIFY_ADDRESS_PROVINCES) {
			provinces = areaDao.getAllProvinces();
			mAdapter = new AreaAdapter<Province>(this, provinces,
					MODIFY_ADDRESS_PROVINCES);
		} else if (type == MODIFY_ADDRESS_CITIES) {
			province = getIntent().getParcelableExtra("province");
			cities = areaDao.getAllCityByProId(province.getProSort());
			mAdapter = new AreaAdapter<City>(this, cities,
					MODIFY_ADDRESS_CITIES);
		} else if (type == MODIFY_ADDRESS_ZONES) {
			setRightHeadText("保存");
			province = getIntent().getParcelableExtra("province");
			city = getIntent().getParcelableExtra("city");
			zones = areaDao.getAllZoneByCityId(city.getCitySort());
			mAdapter = new AreaAdapter<Zone>(this, zones, MODIFY_ADDRESS_ZONES);
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 2;
				mAdapter.setSlelectPosition(pos);
				mAdapter.notifyDataSetChanged();
				if (type == MODIFY_ADDRESS_PROVINCES) {
					province = provinces.get(pos);
					Intent i = new Intent(ModifyPerasonalActivity.this,
							ModifyPerasonalActivity.class);
					i.putExtra("type", MODIFY_ADDRESS_CITIES);
					i.putExtra("province", province);
					startActivityForResult(i, 0);
				} else if (type == MODIFY_ADDRESS_CITIES) {
					city = cities.get(pos);
					Intent i = new Intent(ModifyPerasonalActivity.this,
							ModifyPerasonalActivity.class);
					i.putExtra("type", MODIFY_ADDRESS_ZONES);
					i.putExtra("province", province);
					i.putExtra("city", city);
					startActivityForResult(i, 0);
				} else if (type == MODIFY_ADDRESS_ZONES) {
					if (zones.size() > 0) {
						zone = zones.get(pos);
					}
				}
			}
		});
	}

	private void initModifyName() {

		setContentView(R.layout.activity_modify_perasonal_name);
		mModifyDelImg = (ImageView) findViewById(R.id.img_perasonal_modify);
		mModifyNameEt = (EditText) findViewById(R.id.et_perasonal_modify);
		setTitleName("宝宝姓名");
		if (mUserInfo != null)
			mName = mUserInfo.getNickname();
		mModifyNameEt.setText(mName);
		mModifyNameEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mModifyNameEt.getText().toString().length() == 0)
					mModifyDelImg.setVisibility(View.INVISIBLE);
				else if (mModifyNameEt.getText().toString().length() > 0
						&& mModifyNameEt.hasFocus()) {
					mModifyDelImg.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initModifySex() {
		setContentView(R.layout.activity_modify_perasonal_sex);
		setTitleName("宝宝性别");
		mFemaleImg = (ImageView) findViewById(R.id.img_perasonal_select_female);
		mMaleImg = (ImageView) findViewById(R.id.img_perasonal_select_male);
		
		if (mUserInfo != null && mUserInfo.getGender()!= null){
			mSex = Integer.parseInt(mUserInfo.getGender());
		}
		mFemaleImg.setVisibility(mSex == 2 ? View.VISIBLE : View.INVISIBLE);
		mMaleImg.setVisibility(mSex == 1 ? View.VISIBLE : View.INVISIBLE);
	}

	private void initModifyPhone() {
		setContentView(R.layout.activity_modify_perasonal_name);
		mModifyDelImg = (ImageView) findViewById(R.id.img_perasonal_modify);
		mModifyPhoneEt = (EditText) findViewById(R.id.et_perasonal_modify);
		mModifyPhoneEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		setTitleName("手机号码");
		if (mUserInfo != null) {
			mPhone = mUserInfo.getPhonenumber();
			mModifyPhoneEt.setText(mPhone);
		}
		mModifyPhoneEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mModifyPhoneEt.getText().toString().length() == 0)
					mModifyDelImg.setVisibility(View.INVISIBLE);
				else if (mModifyPhoneEt.getText().toString().length() > 0
						&& mModifyPhoneEt.hasFocus()) {
					mModifyDelImg.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mModifyDelImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mModifyPhoneEt.setText("");
			}
		});
	}

	public static void start(Context context, int type) {
		Intent i = new Intent(context, ModifyPerasonalActivity.class);
		i.putExtra("type", type);
		((BaseActivity) context).startActivityForResult(i, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_perasonal_select_female:
			mFemaleImg.setVisibility(View.VISIBLE);
			mMaleImg.setVisibility(View.INVISIBLE);
			mSex = 2;
			break;
		case R.id.ll_perasonal_select_male:
			mFemaleImg.setVisibility(View.INVISIBLE);
			mMaleImg.setVisibility(View.VISIBLE);
			mSex = 1;
			break;
		case R.id.ll_add_adress:
			start(this, ADD_GOODS_ADDRESS);
			break;
		case R.id.ll_select_area:
			initAreaDialog();
			break;
		case R.id.tv_dialog_cancel:
			mDialog.dismiss();
			break;
		case R.id.tv_head_right:
			switch (type) {
			case MODIFY_NAME:
				modifyName();
				break;
			case MODIFY_GOODS_ADDRESS:
				break;
			case ADD_GOODS_ADDRESS:
				uploadAddress();
				break;
			case MODIFY_SEX:
				saveUserInfo(null, mSex+"", null, null, null, null, null, null);
				break;
			case MODIFY_PHONE:
				modifyPhone();
				break;
			case MODIFY_ADDRESS_ZONES:
				saveUserInfo(null, null, null, province.getProName(), city.getCityName(), zone != null ?zone.getZoneName():null, null, null);
				break;
			default:
				break;
			}
		}
	}

	private void modifyPhone() {
		mPhone = mModifyPhoneEt.getText().toString();
		if (!TelUtil.isTel(mPhone)) {
			new TipDialog.Builder(this).setAutoDismiss(true)
					.setTransparent(false).setTipText("请输入正确的手机号码")
					.create().show();
		} else {
			saveUserInfo(null, null, null, null, null, null, mPhone, null);
		}
	}

	private void modifyName() {
		mName = mModifyNameEt.getText().toString();
		if(mName== null || "".equals(mName)){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("您还没有输入任何文字")
			.create().show();
			//Toast.makeText(this, "您还没有输入任何文字", Toast.LENGTH_SHORT).show();
		}else if(mName.length()>12){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("昵称最多不能超过12个字")
			.create().show();
			//Toast.makeText(this, "昵称最多不能超过12个字", Toast.LENGTH_SHORT).show();
		}else{
			saveUserInfo(mName, null, null, null, null, null, null, null);
		}
	}
	
	private void uploadAddress() {
		String addressName = mNameEt.getText().toString();
		if(addressName.length() == 0|| "".equals(addressName.trim())){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("请填写收货人")
			.create().show();
			return;
		}
		String phone = mPhoneEt.getText().toString();
		if(!TelUtil.isTel(phone)){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("请填写电话号码")
			.create().show();
			return;
		}
		String street = mStreetEt.getText().toString();
		if(street.length() == 0|| "".equals(street.trim())){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("请填写街道地址")
			.create().show();
			return;
		}
		if(mAreas == null){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("请选择地区")
			.create().show();
			return;
		}
		String zipCode = mZipCodeEt.getText().toString();
		if(!TelUtil.isZipNO(zipCode)){
			new TipDialog.Builder(this).setAutoDismiss(true)
			.setTransparent(false).setTipText("请填写正确的邮编编号")
			.create().show();
			return;
		}
		final Address address = new Address(mAddress== null ? null: mAddress.getId(),addressName, phone, mAreas[0], mAreas[1], mAreas[2], street, zipCode);
		if(mAddress == null){
			LHttpRequest.getInstance().addAddressReq(this, address, new JsonCallback<Address>(this) {
			@Override
			public void onGetDataSuccess(Address data) {
				Intent i = new Intent(ModifyPerasonalActivity.this,ModifyPerasonalActivity.class);
				i.putExtra("address", data);
				setResult(ADD_GOODS_ADDRESS,i);
				finish();
			}
			});
		}else{
			
			LHttpRequest.getInstance().setAddressReq(this, address, new JsonCallback<Address>(this) {
			
			@Override
			public void onGetDataSuccess(Address data) {
				Intent i = new Intent(ModifyPerasonalActivity.this,ModifyPerasonalActivity.class);
				i.putExtra("address", data);
				data.setId(address.getId());
				setResult(ADD_GOODS_ADDRESS,i);
				finish();
			}
			});
		}
		
	}



	public void saveUserInfo(final String nickName,final String gender,String birthday,final String provinceStr,final String cityStr,final String area,final String phonenumber,final Address address){
		LHttpRequest.getInstance().setUserInfoReq(this, nickName, gender, birthday, provinceStr, cityStr, area, phonenumber, address == null ? null: address.getId(),null, new JsonCallback<String>(this) {

			@Override
			public void onGetDataSuccess(String data) {
				Toast.makeText(ModifyPerasonalActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
				if(nickName != null){
					MyApplication.getInstance().userInfo.setNickname(nickName);
					return;
				}
				if(gender != null){
					MyApplication.getInstance().userInfo.setGender(gender);
					return;
				}
				if(province != null){
					MyApplication.getInstance().userInfo.setProvince(provinceStr);
					return;
				}
				if(city !=null){
					MyApplication.getInstance().userInfo.setCity(cityStr);
					return;
				}
				if(area != null){
					MyApplication.getInstance().userInfo.setArea(area);
					return;
				}
				if(phonenumber != null){
					MyApplication.getInstance().userInfo.setPhonenumber(phonenumber);
					return;
				}
				if(address != null){
					mAddressAdapter.notifyDataSetChanged();
					MyApplication.getInstance().userInfo.setAddressinfo(address);
				}
			}
			
			@Override
			public void onFinish() {
				if(province != null){
					Intent i = new Intent(ModifyPerasonalActivity.this, PerasonalActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					i.setAction("modifyAddress");
					i.putExtra("province", province);
					i.putExtra("city", city);
					if (zone != null)
						i.putExtra("zone", zone);
					startActivityForNew(i);
				}
				if(address == null){
					finish();
				}
				super.onFinish();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == ADD_GOODS_ADDRESS){
			if(data != null){
				Address address = (Address) data.getSerializableExtra("address");
				if(address!= null){
					boolean update = false;
					for(int i= 0; i<mAddresses.size();i++){
						if(mAddresses.get(i).getId().equals(address.getId())){
							mAddresses.remove(i);
							mAddresses.add(i, address);
							update = true;
							break;
						}
					}
					if(!update){
						mAddresses.add(0,address);
					}
					mAddressDividerV.setVisibility(mAddresses.size() == 0? View.GONE:View.VISIBLE);
					mAddressAdapter.notifyDataSetChanged();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


}
