package com.spshop.stylistpark.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.spshop.stylistpark.AppConfig;

import java.util.HashMap;
import java.util.Map;

public class UserTracker {
	private static final String TAG = "UserTracker";
	private static final UserTracker instance = new UserTracker();

	private static String APP_VER = null;

	public static interface Action {
		public static final String EVENT_VIEW_LOGIN = "View login page";
		public static final String EVENT_CLICK_LOGIN = "Click loogin";
			
		public static final String EVENT_VIEW_APP_LAUNCH_TUTORIAL = "View app launch tutorial";
		public static final String EVENT_CLICK_TUTORIAL_SKIP = "Click tutorial skip";
		public static final String EVENT_CLICK_TUTORIAL_START_APP = "Click tutorial start app";
			
		public static final String EVENT_VIEW_SIDE_MENU = "View side menu";
		public static final String EVENT_CLICK_MAIN_PAGE = "Click main page";
		public static final String EVENT_CLICK_LOGOUT = "Click logout";
		
		public static final String EVENT_VIEW_MAIN = "View main page";
		public static final String EVENT_CLICK_MAIN_CREATE = "Click main create";
		public static final String EVENT_CLICK_FAVOURITE_LIST = "Click favorite list";
		public static final String EVENT_CLICK_LOOKBOOK_LIST = "Click lookbook list";
		
		public static final String EVENT_VIEW_SETTING = "View setting page";
		
		public static final String EVENT_VIEW_TNC = "View Tnc";
		
		public static final String EVENT_VIEW_SETTING_TUTORIAL = "View setting tutorial";
		
		public static final String EVENT_VIEW_CREATE_LOOKBOOK_MENU = "View create lookbook menu";
		public static final String EVENT_VIEW_FREE_COLLAGE_GENERATOR = "View free collage generator";
		public static final String EVENT_VIEW_TEMPLATE_COLLAGE_GENERATOR = "View template collage generator";
		public static final String EVENT_VIEW_MULTIANGLE_GENERATOR = "View multiangle collage generator";
		public static final String EVENT_CLICK_FREE_COLLAGE_DRAFT = "Click free collage draft";
		public static final String EVENT_CLICK_FREE_COLLAGE_NEW = "Click free collage new";
		public static final String EVENT_CLICK_FREE_COLLAGE_SAVE_DRAFT = "Click free collage save draft";
		public static final String EVENT_CLICK_TEMPLATE_SAVE_DRAFT = "Click template save draft";
		public static final String EVENT_CLICK_TEMPLATE_CHANGE_TEMPLATE = "Click template change template";
		public static final String EVENT_CLICK_MULTIANGLE_CHANGE_PRODUCT = "Click multiangle change product";
		public static final String EVENT_CLICK_COLLAGE_GENERATOR_MENU = "Click collage generator menu";
		public static final String EVENT_CLICK_COLLAGE_GENERATOR_SAVE = "Click collage generator save";
		public static final String EVENT_CLICK_COLLAGE_GENERATOR_RESTART = "Click collage generator retart";
		public static final String EVENT_CLICK_COLLAGE_GENERATOR_LEAVE = "Click collage generator leave";
		public static final String EVENT_CLICK_UP_LAYER = "Click collage up layer";
		public static final String EVENT_CLICK_LOW_LAYER = "Click collage low layer";
		public static final String EVENT_CLICK_REDO = "Click redo";
		public static final String EVENT_CLICK_UNDO = "Click undo";
		public static final String EVENT_CLICK_DELETE = "Click delete";
		public static final String EVENT_CLICK_PRODUCT_CAT_OPEN = "Click product cat to open list";
		
		public static final String EVENT_PRODUCT_LIST_SEARCH = "Search product list";
		public static final String EVENT_CLICK_PRODUCT_VIEW = "Click product view";
		public static final String EVENT_CLICK_MODEL_VIEW = "Click model view";
		public static final String EVENT_CLICK_PRODUCT_LIST_ITEM = "Click product list item";
		public static final String EVENT_PRODUCT_LIST_DECORATION_SEARCH = "Search product list decoration";
		public static final String EVENT_CLICK_PRODUCT_LIST_DECORATION_ITEM = "Click product list decoration item";
		public static final String EVENT_VIEW_FILTER = "View filter page";
		public static final String EVENT_PRODUCT_LIST_FILTER = "Filter product list";
		public static final String EVENT_VIEW_DECORATION_FILTER = "View filter page";
		public static final String EVENT_DECORATION_FILTER = "Filter decoration";
		public static final String EVENT_VIEW_LOOKBOOK_DESCRIPTION_FORM = "View lookbook description form page";
		public static final String EVENT_CLICK_SAMPLE_TEXT = "Click sample text";
		public static final String EVENT_CLICK_LOOKBOOK_SAVE = "Click lookbook save";
		public static final String EVENT_VIEW_LOOKBOOK_SHARE = "View lookbook share page";
		public static final String EVENT_SHARE_LOOKBOOK_EMAIL = "Share lookbook email";
		public static final String EVENT_SHARE_LOOKBOOK_WECHAT_MOMENTS = "Share lookbook wechat moments";
		public static final String EVENT_SHARE_LOOKBOOK_WECHAT_CONTACTS = "Share lookbook wechat contacts";
		public static final String EVENT_SHARE_LOOKBOOK_WEIBO = "Share lookbook weibo";
		public static final String EVENT_SHARE_LOOKBOOK_FB = "Share lookbook facebook";
		public static final String EVENT_SHARE_LOOKBOOK_IG = "Share lookbook instagram";
		public static final String EVENT_CLICK_LOOKBOOK_SHARE_BACK = "Click lookbook share back";
		public static final String EVENT_CLICK_LOOKBOOK_SHARE_NEW = "Click lookbook share new";
		
		public static final String EVENT_VIEW_EDIT_PROFILE = "View edit profile page";
		public static final String EVENT_CLICK_EDIT_PROFILE_SAVE = "Click edit profile save";
		public static final String EVENT_CLICK_EDIT_PROFILE_CANCEL = "Click edit profile cancel";
		public static final String EVENT_VIEW_SELECT_TEMPLATE = "View select template page";
		public static final String EVENT_CLICK_SELECT_TEMPLATE_SELECT = "Click select template select";
		public static final String EVENT_CLICK_TEMPLATE_DRAFT = "Click template draft";
		public static final String EVENT_CLICK_TEMPLATE_NEW = "Click template new";
		public static final String EVENT_VIEW_CUSTOMER_LIST = "View customer list page";
		public static final String EVENT_CUSTOMER_LIST_SEARCH = "Search customer list";
		public static final String EVENT_VIEW_CUSTOMER_INFO = "View customer info page";
		public static final String EVENT_CLICK_CUSTOMER_INFO_HISTORY_ITEM = "Click customer info history item";
		public static final String EVENT_VIEW_REPORT_LIST = "View report list page";
		public static final String EVENT_REPORT_LIST_SEARCH = "Search report list";
		public static final String EVENT_VIEW_REPORT_INFO = "View report info page";
		public static final String EVENT_CLICK_REPORT_INFO_PRODUCT_ITEM = "Click report info product item";
		public static final String EVENT_VIEW_SP_NEWS = "View sp news page";
		public static final String EVENT_VIEW_SP_NEWS_POPUP = "View sp news popup";
		public static final String EVENT_CLICK_SP_NEWS_POPUP_VIEW = "Click sp news popup view";
	}

	public static interface Key {
		public static final String KEY_CATEGORY_IDS = "CategoryIds";
		public static final String KEY_Keyword = "keyword";
	}

	public static interface Value {
		
	}

	private boolean enable = true;

	private UserTracker() {
		// comment line for code analysis
	}

	public static UserTracker getInstance() {
		return instance;
	}

	private static final String APP_START = "AppStart";
	private static final String APP_CLOSE = "AppEnd";

	public void initTracker(Context context) {
		FlurryAgent.setReportLocation(false);
		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setLogLevel(Log.VERBOSE);
		FlurryAgent.onStartSession(context, AppConfig.FLURRY_API_KEY);
//		FlurryAgent.init(context, AppConfig.FLURRY_API_KEY);
		if(APP_VER == null){
			try {
				APP_VER = context.getPackageManager()
					    .getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				Log.e(TAG, TAG, e);
			}
		}
		if(APP_VER != null){
			FlurryAgent.setVersionName(APP_VER);
		}

	}

	public void endTracker(Context context) {
		FlurryAgent.onEndSession(context);
	}

	public void trackAppStart() {
		if (enable) {
			FlurryAgent.logEvent(APP_START);
		}
	}

	public void trackViewLoaded(String view) {
		if (enable) {
			FlurryAgent.logEvent("View: " + view);
		}
	}

	public void trackUserAction(String action, Map<String, String> params) {
		if (enable) {
			if (params != null) {
				FlurryAgent.logEvent(action, params);
				Log.i(TAG, "trackUserAction() : "+action+" : params != null");
			} else {
				FlurryAgent.logEvent(action);
				Log.i(TAG, "trackUserAction() : "+action);
			}
		}
	}

	public void trackSearchAction(String view, String target,
			Map<String, String> params) {
		if (enable) {
			Map<String, String> dict = new HashMap<String, String>();
			dict.put(view, "withScreen");
			if (params != null) {
				dict.putAll(params);
			}
			FlurryAgent.logEvent("Search: " + target, dict);
		}
	}

	public void trackAppClose() {
		if (enable) {
			FlurryAgent.logEvent(APP_CLOSE);
		}
	}

	public void releaseTracker(Context context) {
		FlurryAgent.onEndSession(context);
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
