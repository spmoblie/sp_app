package com.spshop.stylistpark.activity.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.district.selector.wheel.adapter.ArrayWheelAdapter;
import com.district.selector.wheel.model.CityModel;
import com.district.selector.wheel.model.DistrictModel;
import com.district.selector.wheel.model.ProvinceModel;
import com.district.selector.wheel.service.XmlParserHandler;
import com.district.selector.wheel.widget.OnWheelChangedListener;
import com.district.selector.wheel.widget.WheelView;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.common.SelectListActivity;
import com.spshop.stylistpark.adapter.SelectListAdapter;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

@SuppressLint("UseSparseArrays")
public class AddressEditActivity extends BaseActivity implements
		OnClickListener, OnWheelChangedListener {

	private static final int TYPE_CODE_COUNTRY = 1001;
	private static final int TYPE_CODE_PROVICE = 1002;
	private static final int TYPE_CODE_CITY = 1003;
	private static final int TYPE_CODE_DISTRICT = 1004;
	
	/**
	 * 所有省
	 */
	private String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 区 values - 邮编
	 */
	private Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	private String mProvinceName;
	/**
	 * 当前市的名称
	 */
	private String mCityName;
	/**
	 * 当前区的名称
	 */
	private String mDistrictName;

	private WheelView mViewProvince, mViewCity, mViewDistrict;
	private Spinner sp_country, sp_province, sp_city, sp_district;
	private ArrayAdapter<String> ap_country, ap_province, ap_city, ap_district;
	private EditText et_name, et_phone, et_email, et_address;
	private LinearLayout ll_select_area, ll_country_main, ll_province_main, ll_city_main, ll_district_main;
	private TextView tv_country, tv_province, tv_city, tv_district;
	private AddressEntity data;
	private List<String> ls_country = new ArrayList<String>();
	private List<String> ls_province = new ArrayList<String>();
	private List<String> ls_city = new ArrayList<String>();
	private List<String> ls_district = new ArrayList<String>();
	private SparseIntArray sa_id_country = new SparseIntArray(); 
	private SparseIntArray sa_id_province = new SparseIntArray(); 
	private SparseIntArray sa_id_city = new SparseIntArray(); 
	private SparseIntArray sa_id_district = new SparseIntArray(); 
	private SparseArray<AddressEntity> sa_ae_country = new SparseArray<AddressEntity>(); 
	private SparseArray<AddressEntity> sa_ae_province = new SparseArray<AddressEntity>(); 
	private SparseArray<AddressEntity> sa_ae_city = new SparseArray<AddressEntity>(); 
	private SparseArray<AddressEntity> sa_ae_district = new SparseArray<AddressEntity>(); 
	private int postId = 0;
	private int addressId = 0;
	private int id_country, id_province, id_city, id_district;
	private int countryId, provinceId, cityId, districtId;
	private int countryPos, provincePos, cityPos, districtPos;
	private int typeCode = TYPE_CODE_COUNTRY;
	private String nameStr, phoneStr, emailStr, addressStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_edit);
		
		data = (AddressEntity) getIntent().getSerializableExtra("data");

		findViewById();
		initView();
	}

	private void findViewById() {
		et_name = (EditText) findViewById(R.id.new_address_et_name);
		et_phone = (EditText) findViewById(R.id.new_address_et_phone);
		et_email = (EditText) findViewById(R.id.new_address_et_email);
		tv_country = (TextView) findViewById(R.id.new_address_tv_country);
		tv_province = (TextView) findViewById(R.id.new_address_tv_province);
		tv_city = (TextView) findViewById(R.id.new_address_tv_city);
		tv_district = (TextView) findViewById(R.id.new_address_tv_district);
		et_address = (EditText) findViewById(R.id.new_address_et_address);
		sp_country = (Spinner) findViewById(R.id.new_address_sp_country);
		sp_province = (Spinner) findViewById(R.id.new_address_sp_province);
		sp_city = (Spinner) findViewById(R.id.new_address_sp_city);
		sp_district = (Spinner) findViewById(R.id.new_address_sp_district);
		ll_country_main = (LinearLayout) findViewById(R.id.new_address_ll_country_main);
		ll_province_main = (LinearLayout) findViewById(R.id.new_address_ll_province_main);
		ll_city_main = (LinearLayout) findViewById(R.id.new_address_ll_city_main);
		ll_district_main = (LinearLayout) findViewById(R.id.new_address_ll_district_main);
		
		ll_select_area = (LinearLayout) findViewById(R.id.new_address_ll_bottom);
		mViewProvince = (WheelView) findViewById(R.id.wheel_province);
		mViewCity = (WheelView) findViewById(R.id.wheel_city);
		mViewDistrict = (WheelView) findViewById(R.id.wheel_district);
	}

	private void initView() {
		setTitle(R.string.title_delivery_info);
		setBtnRight(getString(R.string.save));

		initViewListener();
		getSVDatas();
		
		if (data != null) {
			et_name.setText(data.getName());
			et_phone.setText(data.getPhone());
			et_email.setText(data.getEmail());
			et_address.setText(data.getEditAdd());
			addressId = data.getAddressId();
			id_country = data.getCountryId();
			id_province = data.getProviceId();
			id_city = data.getCityId();
			id_district = data.getDistrictId();
		}
	}

	private void initViewListener() {
		//btn_confirm.setOnClickListener(this);
		//mViewProvince.addChangingListener(this);
		//mViewCity.addChangingListener(this);
		//mViewDistrict.addChangingListener(this);
		//setUpData();

		ll_country_main.setOnClickListener(this);
		ll_province_main.setOnClickListener(this);
		ll_city_main.setOnClickListener(this);
		ll_district_main.setOnClickListener(this);

		tv_country.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				selectCountryData(countryPos);
			}
		});
		tv_province.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				selectProvinceData(provincePos);
			}
		});
		tv_city.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				selectCityData(cityPos);
			}
		});
		tv_district.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				selectDistrictData(districtPos);
			}
		});
	}

	private void initCountrySpinner() {
		if (ls_country.size() > 0) {
			/*ap_country = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ls_country);
			ap_country.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_country.setAdapter(ap_country);
			sp_country.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					changeTextViewStyle(view);
					selectCountryData(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
				
			});*/
			// 编辑地址时定位域
			countryPos = sa_id_country.get(id_country);
			changeTextViewShow(tv_country, countryPos, ls_country);
		}else {
			clearTypeDatas(ap_province, ls_province, sa_ae_province, sa_id_province);
			initProvinceSpinner();
		}
	}
	
	private void initProvinceSpinner() {
		if (ls_province.size() > 0) {
			ll_province_main.setVisibility(View.VISIBLE);
			/*ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ls_province);
			sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_province.setAdapter(sp_adapter);
			sp_province.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					changeTextViewStyle(view);
					selectProvinceData(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
				
			});*/
			// 编辑地址时定位省
			provincePos = sa_id_province.get(id_province);
			changeTextViewShow(tv_province, provincePos, ls_province);
		}else {
			ll_province_main.setVisibility(View.GONE);
			provinceId = 0;
			clearTypeDatas(ap_city, ls_city, sa_ae_city, sa_id_city);
			initCitySpinner();
		}
	}
	
	private void initCitySpinner() {
		if (ls_city.size() > 0) {
			ll_city_main.setVisibility(View.VISIBLE);
			/*ArrayAdapter<String> sp_adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ls_city);
			sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_city.setAdapter(sp_adapter);
			sp_city.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					changeTextViewStyle(view);
					selectCityData(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
				
			});*/
			// 编辑地址时定位市
			cityPos = sa_id_city.get(id_city);
			changeTextViewShow(tv_city, cityPos, ls_city);
		}else {
			ll_city_main.setVisibility(View.GONE);
			cityId = 0;
			clearTypeDatas(ap_district, ls_district, sa_ae_district, sa_id_district);
			initDistrictSpinner();
		}
	}
	
	private void initDistrictSpinner() {
		if (ls_district.size() > 0) {
			ll_district_main.setVisibility(View.VISIBLE);
			/*ap_district = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ls_district);
			ap_district.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_district.setAdapter(ap_district);
			sp_district.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					changeTextViewStyle(view);
					selectDistrictData(position);
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
				
			});*/
			// 编辑地址时定位区
			districtPos = sa_id_district.get(id_district);
			changeTextViewShow(tv_district, districtPos, ls_district);
		}else {
			ll_district_main.setVisibility(View.GONE);
			districtId = 0;
		}
	}

	private void changeTextViewStyle(View view) {
		TextView tv_show = (TextView) view;
		tv_show.setTextSize(14);
	}

	private void changeTextViewShow(TextView showView, int showPos, List<String> ls_datas) {
		if (showPos >= 0 && showPos < ls_datas.size()) {
			showView.setText(ls_datas.get(showPos));
		}
	}

	private void selectCountryData(int position) {
		AddressEntity ae = sa_ae_country.get(position);
		if (ae != null) {
			typeCode = TYPE_CODE_PROVICE;
			countryId = ae.getCountryId();
			postId = countryId;
			provinceId = 0;
			clearTypeDatas(ap_province, ls_province, sa_ae_province, sa_id_province);
			getSVDatas();
		}
	}
	
	private void selectProvinceData(int position) {
		AddressEntity ae = sa_ae_province.get(position);
		if (ae != null) {
			typeCode = TYPE_CODE_CITY;
			provinceId = ae.getCountryId();
			postId = provinceId;
			cityId = 0;
			clearTypeDatas(ap_city, ls_city, sa_ae_city, sa_id_city);
			getSVDatas();
		}
	}
	
	private void selectCityData(int position) {
		AddressEntity ae = sa_ae_city.get(position);
		if (ae != null) {
			typeCode = TYPE_CODE_DISTRICT;
			cityId = ae.getCountryId();
			postId = cityId;
			districtId = 0;
			clearTypeDatas(ap_district, ls_district, sa_ae_district, sa_id_district);
			getSVDatas();
		}
	}
	
	private void selectDistrictData(int position) {
		AddressEntity ae = sa_ae_district.get(position);
		if (ae != null) {
			districtId = ae.getCountryId();
		}
	}

	/**
	 * 加载服务器数据
	 */
	private void getSVDatas() {
		startAnimation();
		request(AppConfig.REQUEST_SV_GET_COUNTRY_LIST_CODE);
	}

	@Override
	public void OnListenerRight() {
		LogUtil.i("address", "countryId="+countryId+" provinceId="+provinceId+" cityId="+cityId +" districtId="+districtId);
		// 判定姓名
		nameStr = et_name.getText().toString();
		if (nameStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.address_error_name_empty), 1000);
			return;
		} 
		// 判定手机号
		phoneStr = et_phone.getText().toString();
		if (phoneStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.address_error_phone_empty), 1000);
			return;
		} 
//		// 判定邮箱
//		emailStr = et_email.getText().toString();
//		if (emailStr.isEmpty()) {
//			CommonTools.showToast(getString(R.string.login_input_email), 1000);
//			return;
//		} else if (!StringUtil.isEmail(emailStr)) {
//			CommonTools.showToast(getString(R.string.login_email_format_error), 1000);
//			return;
//		}
		// 判定区域信息
		if (countryId == 0) {
			CommonTools.showToast(getString(R.string.address_error_load_area), 1000);
			return;
		}
//		// 判定国家及省、市、区
//		if (!StringUtil.isNull(mCountryName)) { //中国
//			mProviceName = tv_province.getText().toString();
//			mCityName = tv_city.getText().toString();
//			mDistrictName = tv_district.getText().toString();
//			if (mProviceName.isEmpty() || mCityName.isEmpty() || mDistrictName.isEmpty()) {
//				CommonTools.showToast(getString(R.string.address_error_area_empty), 1000);
//				return;
//			} 
//		}
		// 判定详细地址
		addressStr = et_address.getText().toString();
		if (addressStr.isEmpty()) {
			CommonTools.showToast(getString(R.string.address_error_address_empty), 1000);
			return;
		}
		requestEditAddress();
	}

	/**
	 * 请求修改收货地址
	 */
	private void requestEditAddress() {
		startAnimation();
		request(AppConfig.REQUEST_SV_POST_EDIT_ADDRESS_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_address_ll_country_main: //选择国
			startSelectListActivity(AppConfig.ACTIVITY_CHOOSE_ADD_COUNTRY);
			break;
		case R.id.new_address_ll_province_main: //选择省
			startSelectListActivity(AppConfig.ACTIVITY_CHOOSE_ADD_PROVINCE);
			break;
		case R.id.new_address_ll_city_main: //选择市
			startSelectListActivity(AppConfig.ACTIVITY_CHOOSE_ADD_CITY);
			break;
		case R.id.new_address_ll_district_main: //选择区
			startSelectListActivity(AppConfig.ACTIVITY_CHOOSE_ADD_DISTRICT);
			break;
		case R.id.new_address_ll_area: //省、市、区View
			if (ll_select_area.getVisibility() == View.GONE) {
				ll_select_area.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}else {
				ll_select_area.setVisibility(View.GONE);
			}
			break;
		case R.id.new_address_btn_confirm: //选择省、市、区
			ll_select_area.setVisibility(View.GONE);
			if (mProvinceName.isEmpty() || mCityName.isEmpty() || mDistrictName.isEmpty()) {
				updateCities();
			}
			tv_province.setText(mProvinceName);
			tv_city.setText(mCityName);
			tv_district.setText(mDistrictName);
			break;
		default:
			break;
		}
	}

	private void startSelectListActivity(int typeCode) {
		Intent intent = new Intent(mContext, SelectListActivity.class);
		SelectListEntity selectEn = getAreaListEntity(typeCode);
		intent.putExtra("data", selectEn);
		intent.putExtra("dataType", SelectListAdapter.DATA_TYPE_8);
		startActivityForResult(intent, typeCode);
	}

	private SelectListEntity getAreaListEntity(int typeCode) {
		SelectListEntity selectEn = new SelectListEntity();
		List<String> ls_datas = null;
		int currentPos = 0;
		switch (typeCode) {
			case AppConfig.ACTIVITY_CHOOSE_ADD_COUNTRY:
				ls_datas = ls_country;
				currentPos = countryPos;
				selectEn.setTypeName(getString(R.string.address_choose_country));
				break;
			case AppConfig.ACTIVITY_CHOOSE_ADD_PROVINCE:
				ls_datas = ls_province;
				currentPos = provincePos;
				selectEn.setTypeName(getString(R.string.address_choose_province));
				break;
			case AppConfig.ACTIVITY_CHOOSE_ADD_CITY:
				ls_datas = ls_city;
				currentPos = cityPos;
				selectEn.setTypeName(getString(R.string.address_choose_city));
				break;
			case AppConfig.ACTIVITY_CHOOSE_ADD_DISTRICT:
				ls_datas = ls_district;
				currentPos = districtPos;
				selectEn.setTypeName(getString(R.string.address_choose_district));
				break;
		}
		if (ls_datas != null) {
			List<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
			for (int i = 0; i < ls_datas.size(); i++) {
				SelectListEntity childEn = new SelectListEntity();
				childEn.setChildId(i);
				childEn.setChildShowName(ls_datas.get(i));
				childLists.add(childEn);
				if (currentPos == i) { //标记选中项
					selectEn.setSelectEn(childEn);
				}
			}
			selectEn.setChildLists(childLists);
		}
		return selectEn;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int selectId = data.getExtras().getInt(AppConfig.ACTIVITY_SELECT_LIST_POSITION, 0);
			if (requestCode == AppConfig.ACTIVITY_CHOOSE_ADD_COUNTRY) { //选择国
				if (countryPos != selectId) {
					countryPos = selectId;
					changeTextViewShow(tv_country, countryPos, ls_country);
				}
			} else if (requestCode == AppConfig.ACTIVITY_CHOOSE_ADD_PROVINCE) { //选择省
				if (provincePos != selectId) {
					provincePos = selectId;
					changeTextViewShow(tv_province, provincePos, ls_province);
				}
			} else if (requestCode == AppConfig.ACTIVITY_CHOOSE_ADD_CITY) { //选择市
				if (cityPos != selectId) {
					cityPos = selectId;
					changeTextViewShow(tv_city, cityPos, ls_city);
				}
			} else if (requestCode == AppConfig.ACTIVITY_CHOOSE_ADD_DISTRICT) { //选择区
				if (districtPos != selectId) {
					districtPos = selectId;
					changeTextViewShow(tv_district, districtPos, ls_district);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		LogUtil.i(TAG, "onResume");
		// 页面开始
		AppApplication.onPageStart(this, TAG);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		AppApplication.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

	@Override
	public Object doInBackground(int requestCode) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_COUNTRY_LIST_CODE:
			params.add(new MyNameValuePair("app", "regions"));
			params.add(new MyNameValuePair("parent", String.valueOf(postId)));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_GET_COUNTRY_LIST_CODE, uri, params, HttpUtil.METHOD_GET);

		case AppConfig.REQUEST_SV_POST_EDIT_ADDRESS_CODE:
			uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_address";
			params.add(new MyNameValuePair("address_id", String.valueOf(addressId)));
			params.add(new MyNameValuePair("country", String.valueOf(countryId)));
			params.add(new MyNameValuePair("province", String.valueOf(provinceId)));
			params.add(new MyNameValuePair("city", String.valueOf(cityId)));
			params.add(new MyNameValuePair("district", String.valueOf(districtId)));
			params.add(new MyNameValuePair("address", addressStr));
			params.add(new MyNameValuePair("consignee", nameStr));
			params.add(new MyNameValuePair("mobile", phoneStr));
			params.add(new MyNameValuePair("email", emailStr));
			return sc.loadServerDatas(TAG, AppConfig.REQUEST_SV_POST_EDIT_ADDRESS_CODE, uri, params, HttpUtil.METHOD_POST);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		super.onSuccess(requestCode, result);
		switch (requestCode) {
		case AppConfig.REQUEST_SV_GET_COUNTRY_LIST_CODE:
			if (result != null) {
				List<AddressEntity> ae_lists = ((AddressEntity) result).getMainLists();
				switch (typeCode) {
				case TYPE_CODE_COUNTRY:
					getSpinnerDatas(ae_lists, ls_country, sa_ae_country, sa_id_country);
					initCountrySpinner();
					break;
				case TYPE_CODE_PROVICE:
					getSpinnerDatas(ae_lists, ls_province, sa_ae_province, sa_id_province);
					initProvinceSpinner();
					break;
				case TYPE_CODE_CITY:
					getSpinnerDatas(ae_lists, ls_city, sa_ae_city, sa_id_city);
					initCitySpinner();
					break;
				case TYPE_CODE_DISTRICT:
					getSpinnerDatas(ae_lists, ls_district, sa_ae_district, sa_id_district);
					initDistrictSpinner();
					stopAnimation();
					break;
				default:
					getSpinnerDatas(ae_lists, ls_country, sa_ae_country, sa_id_country);
					initCountrySpinner();
					break;
				}
				if (ae_lists == null || ae_lists.size() == 0) {
					stopAnimation();
				}
			}else {
				stopAnimation();
				CommonTools.showToast(getString(R.string.address_error_load_area), 3000);
			}
			break;
		case AppConfig.REQUEST_SV_POST_EDIT_ADDRESS_CODE:
			stopAnimation();
			if (result != null) {
				BaseEntity baseEn = (BaseEntity) result;
				if (baseEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
					CommonTools.showToast(getString(R.string.save_ok), 1000);
					updateActivityData(8);
					updateActivityData(9);
					finish();
				}else if (baseEn.getErrCode() == AppConfig.ERROR_CODE_LOGOUT) {
					// 登入超时，交BaseActivity处理
				}else {
					if (StringUtil.isNull(baseEn.getErrInfo())) {
						showServerBusy();
					}else {
						CommonTools.showToast(baseEn.getErrInfo(), 2000);
					}
				}
			}else {
				showServerBusy();
			}
			break;
		}
	}

	private void getSpinnerDatas(List<AddressEntity> ae_lists, List<String> lists,
			SparseArray<AddressEntity> sa_ae, SparseIntArray sa_id) {
		if (ae_lists == null) return;
		clearTypeDatas(null, lists, sa_ae, sa_id);
		AddressEntity ae;
		for (int i = 0; i < ae_lists.size(); i++) {
			ae = ae_lists.get(i);
			lists.add(ae.getCountry());
			sa_ae.put(i, ae);
			sa_id.put(ae.getCountryId(), i);
		}
	}
	
	private void clearTypeDatas(ArrayAdapter<String> adapter, List<String> lists, 
			SparseArray<AddressEntity> sa_ae, SparseIntArray sa_id) {
		lists.clear();
		sa_ae.clear();
		sa_id.clear();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		super.onFailure(requestCode, state, result);
	}

	@SuppressWarnings("unused")
	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(AddressEditActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		// 获取省
		int pCurrent = mViewProvince.getCurrentItem();
		if (mProvinceDatas != null 
				&& pCurrent >= 0 && pCurrent < mProvinceDatas.length) {
			mProvinceName = mProvinceDatas[pCurrent];
			String[] cities = mCitisDatasMap.get(mProvinceName);
			if (cities == null) {
				cities = new String[] { "" };
			}
			mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
			mViewCity.setCurrentItem(0);
			updateAreas();
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		// 获取市
		int pCurrent = mViewCity.getCurrentItem();
		String[] citys = mCitisDatasMap.get(mProvinceName);
		if (citys != null && pCurrent >= 0 && pCurrent < citys.length) {
			mCityName = citys[pCurrent];
			String[] areas = mDistrictDatasMap.get(mCityName);
			if (areas == null) {
				areas = new String[] { "" };
			}
			mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
			mViewDistrict.setCurrentItem(0);
			// 获取区
			int dCurrent = mViewDistrict.getCurrentItem();
			updateDistrict(dCurrent);
		}
	}
	
	/**
	 * 获取区WheelView的信息
	 */
	private void updateDistrict(int dCurrent){
		String[] districts = mDistrictDatasMap.get(mCityName);
		if (districts != null && dCurrent >= 0 && dCurrent < districts.length) {
			mDistrictName = districts[dCurrent];
		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			updateDistrict(newValue);
		}
	}

	/**
	 * 解析省市区的XML数据
	 */
	protected void initProvinceDatas() {
		InputStream input = null;
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getAssets();
		try {
			input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mProvinceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0)
							.getDistrictList();
					mDistrictName = districtList.get(0).getName();
				}
			}
			// */
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					String[] distrinctNameArray = new String[districtList
							.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList
							.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getName(),
								districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Exception e) {
			ExceptionUtil.handle(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					ExceptionUtil.handle(e);
				}
			}
		}
	}

}
