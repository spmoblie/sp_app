package com.spshop.stylistpark.activity.profile;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppManager;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LangCurrTools.Currency;
import com.spshop.stylistpark.utils.LangCurrTools.Language;
import com.spshop.stylistpark.utils.LogUtil;

public class LanguageCurrencyActivity extends BaseActivity {

	private static final String TAG = "LanguageCurrencyActivity";
	
	private RelativeLayout rl_ZH, rl_CN, rl_EN;
	private TextView tv_ZH, tv_CN, tv_EN;
	private ImageView iv_ZH, iv_CN, iv_EN;
	
	private int dataType = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language_currency);
		
		AppManager.getInstance().addActivity(this); //添加Activity到堆栈
		LogUtil.i(TAG, "onCreate");
		
		dataType = getIntent().getIntExtra("dataType", 1);
		
		findViewById();
		initView();
	}
	
	
	private void findViewById() {
		rl_ZH = (RelativeLayout) findViewById(R.id.setting_rl_ZH);
		rl_CN = (RelativeLayout) findViewById(R.id.setting_rl_CN);
		rl_EN = (RelativeLayout) findViewById(R.id.setting_rl_EN);
		
		tv_ZH = (TextView) findViewById(R.id.setting_tv_zh_title);
		tv_CN = (TextView) findViewById(R.id.setting_tv_cn_title);
		tv_EN = (TextView) findViewById(R.id.setting_tv_en_title);
		
		iv_ZH = (ImageView) findViewById(R.id.setting_iv_zh_select);
		iv_CN = (ImageView) findViewById(R.id.setting_iv_cn_select);
		iv_EN = (ImageView) findViewById(R.id.setting_iv_en_select);
	}

	private void initView() {
		if (dataType == 1) { //语言
			setTitle(R.string.setting_language);
		}else { //货币
			setTitle(R.string.setting_currency);
			tv_ZH.setText(getString(R.string.currency_hkd));
			tv_CN.setText(getString(R.string.currency_rmb));
			tv_EN.setText(getString(R.string.currency_usd));
		}
		
		rl_ZH.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (dataType == 1) {
					Language lang = LangCurrTools.getLanguage(mContext);
					if (lang == Language.Zh) return;
					LangCurrTools.setLanguage(LanguageCurrencyActivity.this, Language.Zh);
				}else {
					Currency cur = LangCurrTools.getCurrency(mContext);
					if (cur == Currency.HKD) return;
					LangCurrTools.setCurrency(getApplicationContext(), Currency.HKD);
				}
				updateSettingData();
				startThread();
			}

		});
		
		rl_CN.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (dataType == 1) {
					Language lang = LangCurrTools.getLanguage(LanguageCurrencyActivity.this);
					if (lang == Language.Cn) return;
					LangCurrTools.setLanguage(LanguageCurrencyActivity.this, Language.Cn);
				}else {
					Currency cur = LangCurrTools.getCurrency(mContext);
					if (cur == Currency.RMB) return;
					LangCurrTools.setCurrency(getApplicationContext(), Currency.RMB);
				}
				updateSettingData();
				startThread();
			}
		});
		
		rl_EN.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (dataType == 1) {
					Language lang = LangCurrTools.getLanguage(LanguageCurrencyActivity.this);
					if (lang == Language.En) return;
					LangCurrTools.setLanguage(LanguageCurrencyActivity.this, Language.En);
				}else {
					Currency cur = LangCurrTools.getCurrency(mContext);
					if (cur == Currency.USD) return;
					LangCurrTools.setCurrency(getApplicationContext(), Currency.USD);
				}
				updateSettingData();
				startThread();
			}
		});
		setTick();
	}
	
	private void startThread() {
		setTick();
		finish();
	}
	
	@Override
    public void onBackPressed() {
        finish();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void setTick(){
		iv_ZH.setVisibility(View.GONE);
        iv_CN.setVisibility(View.GONE);
        iv_EN.setVisibility(View.GONE);
        switch (dataType) {
		case 1: //语言
			Language lang = LangCurrTools.getLanguage(this);
			switch (lang) {
			case En:
				iv_EN.setVisibility(View.VISIBLE);
				break;
			case Zh:
				iv_ZH.setVisibility(View.VISIBLE);
				break;
			case Cn:
				iv_CN.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			break;
		case 2: //货币
			Currency currency = LangCurrTools.getCurrency(this);
	        switch (currency) {
	        case USD:
	        	iv_EN.setVisibility(View.VISIBLE);
	            break;
	        case HKD:
	        	iv_ZH.setVisibility(View.VISIBLE);
	            break;
	        case RMB:
	        	iv_CN.setVisibility(View.VISIBLE);
	            break;
	        default:
	            break;
	        }
		}
	}

	private void updateSettingData() {
		if (SettingActivity.instance != null) {
			switch (dataType) {
			case 1:
				SettingActivity.instance.change_language = true; //切换App展示的语言
				AppApplication.loadSVData_category = true; //更新缓存的商品分类数据
				break;
			case 2:
				SettingActivity.instance.change_currency = true; //切换App展示的货币
				break;
			}
		}
	}
	
}
