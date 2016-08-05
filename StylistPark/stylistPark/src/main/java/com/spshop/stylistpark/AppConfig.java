package com.spshop.stylistpark;

import android.os.Environment;


public class AppConfig {
	
	/**
	 ******************************************* URL设置开始 ******************************************
	 */
	
	// 是否正式发布
	public static final boolean IS_PUBLISH = false;
	// 网络传输协议http
	public final static String APP_HTTP = "http://";
	// 网络传输协议https
	public final static String APP_HTTPS = "https://";
	// 域名1
	public final static String ENVIRONMENT_TEST_APP_1 = "www.spshop.com/";
	// 域名2
	public final static String ENVIRONMENT_TEST_APP_2 = "www.spshop.com/api/mobile/";
	// 域名3
	public final static String ENVIRONMENT_TEST_APP_3 = "192.168.11.155/api/mobile/";
	// 加载数据使用的域名
	public final static String ENVIRONMENT_PRESENT_URL_APP = APP_HTTP + ENVIRONMENT_TEST_APP_2;
	// 加载图片使用的域名
	public final static String ENVIRONMENT_PRESENT_IMG_APP = APP_HTTP + ENVIRONMENT_TEST_APP_1;
	// 分享URL使用的域名
	public final static String ENVIRONMENT_PRESENT_SHARE_URL = APP_HTTP + ENVIRONMENT_TEST_APP_1;

	// 商品拉取URL
	public static final String URL_COMMON_PRODUCT_URL = ENVIRONMENT_PRESENT_URL_APP + "product.php";
	// 购物相关URL
	public static final String URL_COMMON_FLOW_URL = ENVIRONMENT_PRESENT_IMG_APP + "flow.php";
	// 用户提交URL
	public static final String URL_COMMON_USER_URL = ENVIRONMENT_PRESENT_IMG_APP + "user.php";
	// 广告详情URL
	public static final String URL_COMMON_TOPIC_URL = ENVIRONMENT_PRESENT_IMG_APP + "topic-app.php";
	// 专题详情URL
	public static final String URL_COMMON_ARTICLE_URL = ENVIRONMENT_PRESENT_IMG_APP + "article-app.php";
	// 专题分享URL
	public static final String URL_COMMON_ARTICLE_SHARE_URL = ENVIRONMENT_PRESENT_IMG_APP + "article.php";
	// 专题评论URL
	public static final String URL_COMMON_COMMENT_URL = ENVIRONMENT_PRESENT_IMG_APP + "comment.php";
	// 用户拉取URL
	public static final String URL_COMMON_MY_URL = ENVIRONMENT_PRESENT_URL_APP + "my.php";
	// 其它通用URL
	public static final String URL_COMMON_INDEX_URL = ENVIRONMENT_PRESENT_URL_APP + "index.php";
	// 上传头像URL
	public static final String API_UPDATE_PROFILE = URL_COMMON_USER_URL + "?act=act_edit_profile";

	/**
	 ******************************************* URL设置结束 ******************************************
	 */
	
	/**
	 ******************************************* 全局常量设置开始 ******************************************
	 */
	
	// Error状态码：加载成功
	public static final int ERROR_CODE_SUCCESS = 1;
	// Error状态码：登录失效
	public static final int ERROR_CODE_LOGOUT = 999;
	
	// QQ AppID
	public static final String QQ_APP_ID = "1104891333";
	// QQ授权接口参数：Scope权限
	public static final String QQ_SCOPE = "all";
	// 微信AppID
	public static final String WX_APP_ID = "wx8102c9afda9cfb50";
	// 微信AppSecret
	public static final String WX_APP_SECRET = "13d5722c55ca04e7aa4763507c4a4c5e";
	// 微信商户号
	public static final String WX_MCH_ID = "1238090502";
	// 微信API密钥，在商户平台设置
	public static final String WX_API_KEY = "9d58967717873974810ad37a4fc0d76c";
	// 微博AppID
	public static final String WB_APP_ID = "2435385654";
	// 微博授权回调Url
	public static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	// 微博授权接口参数：Scope权限
	public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read," 
			+ "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
	// 支付宝AppID
	public static final String ZFB_APP_ID = "2088701914652108";
	// PayPal ClientID
	public static final String PAYPAL_CLIENT_ID = "credentials from developer.paypal.com";


	// 缓存Cookies文件名
	public static final String cookiesFileName = "cookies";
	// 缓存首页数据文件名
	public static final String homeAdsFileName = "homeAds";
	// 缓存品牌列表文件名
	public static final String brandsFileName = "brands";
	// 内置SD卡路径
	public static final String SDPATH = Environment.getExternalStorageDirectory().toString();
	// 长久存储apk包的路径，应用关闭时不清除
	public static final String SAVE_APK_PATH_LONG = SDPATH + "/stylistpark/apk/long/";
	// 长久存储文件的路径，应用关闭时不清除
	public static final String SAVE_TXT_PATH_LONG = SDPATH + "/stylistpark/txt/long/";
	// 临时存储文件的路径，应用关闭时要清除
	public static final String SAVE_TXT_PATH_TEMPORARY = SDPATH + "/stylistpark/txt/temporary/";
	// 长久存储图片的路径，应用关闭时不清除
	public static final String SAVE_IMAGE_PATH_LONG = SDPATH + "/stylistpark/image/long/";
	// 临时存储图片的路径，应用关闭时要清除
	public static final String SAVE_IMAGE_PATH_TEMPORARY = SDPATH + "/stylistpark/image/temporary/";
	// 长久存储多媒体的路径，应用关闭时不清除
	public static final String SAVE_MEDIA_PATH_LONG = SDPATH + "/stylistpark/media/long/";
	// 临时存储多媒体的路径，应用关闭时要清除
	public static final String SAVE_MEDIA_PATH_TEMPORARY = SDPATH + "/stylistpark/media/temporary/";

	/**
	 ******************************************* 全局常量设置结束 ******************************************
	 */
	
	/**
	 ******************************************* RequestCode参数设置开始 ******************************************
	 */
	
	// 校验Sessions
	public static final int REQUEST_SV_GET_SESSIONS_CODE = 0X0001;
	
	// 加载远程首页推广数据
	public static final int REQUEST_SV_GET_HOME_SHOW_HEAD_CODE = 0X1010;
	// 加载本地首页推广数据
	public static final int REQUEST_DB_GET_HOME_SHOW_HEAD_CODE = 0X1011;
	// 加载首页展示列表数据
	public static final int REQUEST_SV_GET_HOME_SHOW_LIST_CODE = 0X1012;
	// 加载本地商品分类数据
	public static final int REQUEST_DB_GET_CATEGORY_LIST_CODE = 0X1030;
	// 加载服务器商品分类数据
	public static final int REQUEST_SV_GET_CATEGORY_LIST_CODE = 0X1031;
	// 加载分类商品列表数据
	public static final int REQUEST_SV_GET_PRODUCT_LIST_CODE = 0X1040;
	// 加载所有品牌列表数据
	public static final int REQUEST_SV_GET_BRANDS_LIST_CODE = 0X1050;
	// 加载筛选品牌列表数据
	public static final int REQUEST_SV_GET_SCREEN_LIST_CODE = 0X1051;
	// 加载指定品牌相关数据
	public static final int REQUEST_SV_GET_BRAND_PROFILE_CODE = 0X1052;
	// 加载商品详情数据
	public static final int REQUEST_SV_GET_PRODUCT_DETAIL_CODE = 0X1060;
	// 提交加入购物车商品数据
	public static final int REQUEST_SV_POST_CART_PRODUCT_CODE = 0X1070;
	// 提交收藏商品请求
	public static final int REQUEST_SV_POST_COLLECITON_CODE = 0X1080;

	// 加载专题列表请求
	public static final int REQUEST_SV_GET_SPECIAL_LIST_CODE = 0X3001;
	// 提交专题评论请求
	public static final int REQUEST_SV_POST_COMMENT_CODE = 0X3020;

	// 加载购物车商品列表
	public static final int REQUEST_SV_GET_CART_LIST_CODE = 0X4010;
	// 删除购物车商品
	public static final int REQUEST_SV_POST_DELETE_GOODS_CODE = 0X4020;
	// 修改购物车商品数量
	public static final int REQUEST_SV_POST_CHANGE_GOODS_CODE = 0X4030;
	// 获取待确认的订单数据
	public static final int REQUEST_SV_GET_ORDER_CONFIRM_CODE = 0X4040;
	// 提交选择的支付方式
	public static final int REQUEST_SV_POST_SELECT_PAYMENT_CODE = 0X4041;
	// 提交确认订单数据
	public static final int REQUEST_SV_POST_CONFIRM_ORDER_CODE = 0X4042;
	// 提交支付请求
	public static final int REQUEST_SV_POST_PAY_INFO_CODE = 0X4050;
	// 查询支付结果
	public static final int REQUEST_SV_GET_PAY_RESULT_CODE = 0X4060;
	
	// 问题反馈
	public static final int REQUEST_SV_POST_FEED_BACK_CODE = 0X5001;
	// 提交注册信息
	public static final int REQUEST_SV_POST_REGISTER_CODE = 0X5002;
	// 提交第三方账号绑定信息
	public static final int REQUEST_SV_POST_REGISTER_OAUTH_CODE = 0X5003;
	// 提交账号密码登入信息
	public static final int REQUEST_SV_POST_ACCOUNT_LOGIN_CODE = 0X5004;
	// 提交第三方授权登入信息
	public static final int REQUEST_SV_POST_THIRD_PARTIES_LOGIN = 0X5005;
	// 刷新或续期access_token
	public static final int REQUEST_SV_GET_REFRESH_TOKEN_CODE = 0X5006;
	// 提交登出请求
	public static final int REQUEST_SV_POST_LOGOUT_CODE = 0X5007;
	// 提交重置密码请求
	public static final int REQUEST_SV_POST_RESET_PASSWORD_CODE = 0X5008;
	// 获取用户信息汇总
	public static final int REQUEST_SV_GET_USERINFO_SUMMARY_CODE = 0X5101;
	// 提交修改用户信息
	public static final int REQUEST_SV_POST_EDIT_USER_INFO_CODE = 0X5102;
	// 查询用户邮箱状态
	public static final int REQUEST_SV_CHECK_USER_EMAIL_STATUS = 0X5103;
	// 发送邮件给用户
	public static final int REQUEST_SV_SEND_EMAIL_TO_USER = 0X5104;
	// 提交实名认证用户姓名
	public static final int REQUEST_SV_POST_AUTH_NAME = 0X5105;
	// 获取用户收藏或浏览的商品列表
	public static final int REQUEST_SV_GET_USER_PRODUCT_LIST = 0X5210;
	// 获取会员列表
	public static final int REQUEST_SV_GET_MEMBER_LIST_CODE = 0X5220;
	// 获取订单列表
	public static final int REQUEST_SV_GET_ORDER_LIST_CODE = 0X5230;
	// 获取订单详情
	public static final int REQUEST_SV_GET_ORDER_DETAIL_CODE = 0X5231;
	// 提交取消订单
	public static final int REQUEST_SV_POST_CACEL_ORDER_CODE = 0X5232;
	// 获取账户余额明细列表
	public static final int REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE = 0X5240;
	// 提交提现申请
	public static final int REQUEST_SV_POST_WITHDRAWALS_CODE = 0X5241;
	// 获取红包列表
	public static final int REQUEST_SV_GET_BOUNS_LIST_CODE = 0X5250;
	// 添加红包
	public static final int REQUEST_SV_POST_BOUNS_NO_CODE = 0X5251;
	// 校验选择的红包
	public static final int REQUEST_SV_POST_CHOOSE_BOUNS_CODE = 0X5252;
	// 获取收货地址列表
	public static final int REQUEST_SV_GET_ADDRESS_LIST_CODE = 0X5260;
	// 选择收货地址为默认地址
	public static final int REQUEST_SV_POST_SELECT_ADDRESS_CODE = 0X5261;
	// 删除收货地址
	public static final int REQUEST_SV_POST_DELETE_ADDRESS_CODE = 0X5262;
	// 获取国家列表清单
	public static final int REQUEST_SV_GET_COUNTRY_LIST_CODE = 0X5263;
	// 编辑收货地址
	public static final int REQUEST_SV_POST_EDIT_ADDRESS_CODE = 0X5264;
	
	/**
	 ******************************************* RequestCode参数设置结束 ******************************************
	 */
	
	/**
	 ******************************************* 偏好设置Key值设置开始 ******************************************
	 */
	
	// 偏好设置Key-记录用户ID
	public static final String KEY_USER_ID = "user_id";
	// 偏好设置Key-记录用户登录名
	public static final String KEY_USER_LOGIN_NAME = "user_login_name";
	// 偏好设置Key-记录用户昵称
	public static final String KEY_USER_NICK_NAME = "user_nick_name";
	// 偏好设置Key-记录用户头像Url
	public static final String KEY_USER_HEAD_IMG_URL = "user_head_img_url";
	// 偏好设置Key-记录用户简介
	public static final String KEY_USER_INTRO = "user_intro";
	// 偏好设置Key-记录用户性别
	public static final String KEY_USER_SEX = "user_sex";
	// 偏好设置Key-记录用户生日
	public static final String KEY_USER_BIRTHDAY = "user_birthday";
	// 偏好设置Key-记录用户邮箱
	public static final String KEY_USER_EMAIL = "user_email";
	// 偏好设置Key-记录用户手机号码
	public static final String KEY_USER_PHONE = "user_phone";
	// 偏好设置Key-记录用户排名
	public static final String KEY_USER_RANK = "user_rank";
	// 偏好设置Key-记录用户级别
	public static final String KEY_USER_LEVEL = "user_level";
	// 偏好设置Key-记录用户身份认证状态
	public static final String KEY_USER_AUTH = "user_auth";
	// 偏好设置Key-记录用户默认收货地址Id
	public static final String KEY_USER_ADDRESS = "user_address";
	// 偏好设置Key-记录用户购物车中商品数量
	public static final String KEY_CART_NUM = "cart_num";
	// 偏好设置Key-记录用户的微信授权码
	public static final String KEY_WX_ACCESS_TOKEN = "wx_access_token";
	// 偏好设置Key-记录用户的微信校验码
	public static final String KEY_WX_REFRESH_TOKEN = "wx_refresh_token";
	// 偏好设置Key-记录用户的微信登录ID
	public static final String KEY_WX_OPEN_ID = "wx_open_id";
	// 偏好设置Key-记录用户的微信身份ID
	public static final String KEY_WX_UNION_ID = "wx_union_id";
	// 偏好设置Key-记录最近一次更新APP版本的时间
	public static final String KEY_UPDATE_VERSION_LAST_TIME = "update_version_last_time";
	// 偏好设置Key-记录同步远程服务器数据的日期
	public static final String KEY_LOAD_SV_DATA_DAY = "load_sv_data_day";
	// 偏好设置Key-记录是否重加载分类数据
	public static final String KEY_LOAD_CATEGORY_DATA = "load_category_data";
	// 偏好设置Key-记录推送服务的开关状态
	public static final String KEY_PUSH_STATUS = "push_status";
	// 偏好设置Key-记录商品搜索的历史记录
	public static final String KEY_SEARCH_WORDS_HISTORY = "search_words_history";
	// 偏好设置Key-记录首页当前的下标索引
	public static final String KEY_HOME_CURRENT_INDEX = "home_current_index";
	// 偏好设置Key-记录是否自动跳转到会员页面
	public static final String KEY_PUSH_PAGE_MEMBER = "push_page_member";
	// 偏好设置Key-记录视频加载地址
	public static final String KEY_VIDEO_LOAD_PATH = "video_load_path";
	// 偏好设置Key-记录视频缓存地址
	public static final String KEY_VIDEO_SAVE_PATH = "video_save_path";

	/**
	 ******************************************* 偏好设置Key值设置结束 ******************************************
	 */
	
	/**
	 ******************************************* Activity传参设置开始 ******************************************
	 */

	public static final String ACTIVITY_SELECT_PHOTO_LIST = "select_photo_list";
	public static final String ACTIVITY_SHOW_PHOTO_LIST = "show_photo_list";
	public static final String ACTIVITY_CLIP_PHOTO_PATH = "clip_photo_path";
	public static final String ACTIVITY_KEY_COLLAGE_URI = "key_collage_uri";
	public static final String ACTIVITY_CHANGE_USER_CONTENT = "change_user_content";
	public static final String ACTIVITY_SELECT_PAY_TYPE = "select_pay_type";
	public static final int ACTIVITY_SELECT_PHOTO_PICKER = 0X9001;
	public static final int ACTIVITY_SHOW_PHOTO_PICKER = 0X9002;
	public static final int ACTIVITY_GET_IMAGE_VIA_CAMERA = 0X9003;
	public static final int ACTIVITY_RELEASE_ON_SHELVES = 0X9004;
	public static final int ACTIVITY_GALLERY_CHOOSE_PHOTO = 0X9005;
	public static final int ACTIVITY_CHANGE_USER_NICK = 0X9006;
	public static final int ACTIVITY_CHANGE_USER_SEX = 0X9007;
	public static final int ACTIVITY_CHANGE_USER_EMAIL = 0X9008;
	public static final int ACTIVITY_CHOOSE_PAY_TYPE = 0X9009;
	
	/**
	 ******************************************* Activity传参设置结束 ******************************************
	 */
	
	/**
	 ******************************************* 广播参数设置开始 ******************************************
	 */

	public static final String RECEIVER_ACTION_HOME_DATA = "更新Home的数据";
	public static final String RECEIVER_ACTION_MAIN_DATA = "更新Main的数据";
	public static final String RECEIVER_ACTION_HOME_MSG_KEY = "传递数据给Home";
	public static final String RECEIVER_ACTION_MAIN_MSG_KEY = "传递数据给Main";

	/**
	 ******************************************* 广播参数设置结束 ******************************************
	 */
	
}