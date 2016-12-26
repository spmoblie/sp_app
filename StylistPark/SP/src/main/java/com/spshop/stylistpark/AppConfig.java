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
	public final static String ENVIRONMENT_TEST_APP_1 = "spshop.com/";
	// 域名2
	public final static String ENVIRONMENT_TEST_APP_2 = "spshop.com/api/mobile/";
	// 域名3
	public final static String ENVIRONMENT_TEST_APP_3 = "192.168.11.155/api/mobile/";
	// 图片域名
	public final static String ENVIRONMENT_PRESENT_IMG_APP = "";
	// 推广域名
	public final static String ENVIRONMENT_PRESENT_SHARE_URL = APP_HTTP + ENVIRONMENT_TEST_APP_1;
	// Data域名1
	public final static String ENVIRONMENT_PRESENT_DATA_URL_1 = APP_HTTP + ENVIRONMENT_TEST_APP_1;
	// Data域名2
	public final static String ENVIRONMENT_PRESENT_DATA_URL_2 = APP_HTTP + ENVIRONMENT_TEST_APP_2;


	// 商品拉取URL
	public static final String URL_COMMON_PRODUCT_URL = ENVIRONMENT_PRESENT_DATA_URL_2 + "product.php";
	// 用户拉取URL
	public static final String URL_COMMON_MY_URL = ENVIRONMENT_PRESENT_DATA_URL_2 + "my.php";
	// 其它通用URL
	public static final String URL_COMMON_INDEX_URL = ENVIRONMENT_PRESENT_DATA_URL_2 + "index.php";
	// 购物相关URL
	public static final String URL_COMMON_FLOW_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "flow.php";
	// 用户提交URL
	public static final String URL_COMMON_USER_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "index.php";
	// 商品详情URL
	public static final String URL_COMMON_GOODS_DETAIL_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "app_goods.php";
	// 广告详情URL
	public static final String URL_COMMON_TOPIC_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "topic-app.php";
	// 专题详情URL
	public static final String URL_COMMON_ARTICLE_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "article-app.php";
	// 专题推广URL
	public static final String URL_COMMON_ARTICLE_SHARE_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "article.php";
	// 专题评论URL
	public static final String URL_COMMON_COMMENT_URL = ENVIRONMENT_PRESENT_DATA_URL_1 + "comment.php";

	// 上传头像URL
	public static final String API_UPDATE_PROFILE = URL_COMMON_USER_URL + "?act=act_edit_profile";
	// SP微信公众号
	public static final String SP_WECHAT_PUBLIC = "http://weixin.qq.com/r/MnXVzWXE-jiBrSGu9yAg";
	// 联系客服URL
	public static final String API_CUSTOMER_SERVICE = "http://webim.qiao.baidu.com/im/gateway?siteid=3888057&type=n&ucid=6374202";

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

	// SP加盟计划ID
	public static final int SP_JION_PROGRAM_ID = 1;
	// 货品发货地ID
	public static final int SP_GOODS_START_HK = 49;
	// 加载缓冲时间
	public static final int LOADING_TIME = 500;
	// 进入循播时间
	public static final int TO_SCREEN_VIDEO_TIME = 1000 * 60;

	// QQ AppID
	public static final String QQ_APP_ID = "1104891333";
	// QQ授权接口参数：Scope权限
	public static final String QQ_SCOPE = "all";
	// 微信AppID
	public static final String WX_APP_ID = "wxe75d3ed35d5ec0a3";
	// 微信AppSecret
	public static final String WX_APP_SECRET = "6dbb91d8aa799a13237179092ab690c8";
	// 微信商户号
	public static final String WX_MCH_ID = "1376997902";
	// 微博AppID
	public static final String WB_APP_ID = "2435385654";
	// 微博授权回调Url
	public static final String WB_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	// 微博授权接口参数：Scope权限
	public static final String WB_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,"
			+ "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
	// PayPal ClientID
	public static final String PAYPAL_CLIENT_ID = "AafTOrDvoUh5MjlMYyUlZOh0ZyyBDpZCSqObK_6By8mPrZZVW8XGHWk7nIfPlvZ9kI_hkbl0_UN1Ka-2";


	// 缓存Cookies文件名
	public static final String cookiesFileName = "cookies";
	// 缓存首页数据文件名
	public static final String homeAdsFileName = "homeAds";
	// 缓存品牌列表文件名
	public static final String brandsFileName = "brands";
	// 内置SD卡路径
	public static final String SDPATH = Environment.getExternalStorageDirectory().toString();
	// 应用缓存路径，应用关闭时须清除
	public static final String SAVE_PATH_APK_DICE = SDPATH + "/stylistpark/apk/SP_AD/";
	// 文本保存路径，应用关闭时不清除
	public static final String SAVE_PATH_TXT_SAVE = SDPATH + "/stylistpark/txt/SP_TS/";
	// 文本缓存路径，应用关闭时须清除
	public static final String SAVE_PATH_TXT_DICE = SDPATH + "/stylistpark/txt/SP_TD/";
	// 图片保存路径，应用关闭时不清除
	public static final String SAVE_PATH_IMAGE_SAVE = SDPATH + "/stylistpark/image/SP_IS/";
	// 图片缓存路径，应用关闭时须清除
	public static final String SAVE_PATH_IMAGE_DICE = SDPATH + "/stylistpark/image/SP_ID/";
	// 媒体保存路径，应用关闭时不清除
	public static final String SAVE_PATH_MEDIA_SAVE = SDPATH + "/stylistpark/media/SP_MS/";
	// 媒体缓存路径，应用关闭时须清除
	public static final String SAVE_PATH_MEDIA_DICE = SDPATH + "/stylistpark/media/SP_MD/";

	/**
	 ******************************************* 全局常量设置结束 ******************************************
	 */

	/**
	 ******************************************* RequestCode参数设置开始 ******************************************
	 */

	// 校验Sessions
	public static final int REQUEST_SV_GET_SESSIONS_CODE = 0X0001;
	// 检测版本更新
	public static final int REQUEST_SV_POST_VERSION_CODE = 0X0002;
	// 微信分享反馈
	public static final int REQUEST_SV_GET_WX_SHARE_CODE = 0X0003;
	// 加载循播视频
	public static final int REQUEST_SV_GET_SCREEN_VIDEO_CODE = 0X0004;
	// 加载循播图片
	public static final int REQUEST_SV_GET_SCREEN_IMAGE_CODE = 0X0005;
	// 校验管理员密码
	public static final int REQUEST_SV_POST_ADMIN_PASS_CODE = 0X0006;

	// 加载远程首页推广数据
	public static final int REQUEST_SV_GET_HOME_SHOW_HEAD_CODE = 0X1010;
	// 加载本地首页推广数据
	public static final int REQUEST_DB_GET_HOME_SHOW_HEAD_CODE = 0X1011;
	// 加载首页展示列表数据
	public static final int REQUEST_SV_GET_HOME_SHOW_LIST_CODE = 0X1012;
	// 加载本地商品分类数据
	public static final int REQUEST_DB_GET_SORT_LIST_CODE = 0X1030;
	// 加载服务器商品分类数据
	public static final int REQUEST_SV_GET_SORT_LIST_CODE = 0X1031;
	// 加载分类商品列表数据
	public static final int REQUEST_SV_GET_PRODUCT_LIST_CODE = 0X1040;
	// 加载品牌商品列表数据
	public static final int REQUEST_SV_GET_BRAND_PRODUCT_CODE = 0X1041;
	// 加载所有品牌列表数据
	public static final int REQUEST_SV_GET_BRANDS_LIST_CODE = 0X1050;
	// 加载筛选品牌列表数据
	public static final int REQUEST_SV_GET_SCREEN_LIST_CODE = 0X1051;
	// 加载指定品牌相关数据
	public static final int REQUEST_SV_GET_BRAND_INFO_CODE = 0X1052;
	// 加载商品详情数据
	public static final int REQUEST_SV_GET_PRODUCT_DETAIL_CODE = 0X1060;
	// 加载商品属性数据
	public static final int REQUEST_SV_GET_PRODUCT_ATTR_CODE = 0X1061;
	// 提交加入购物车商品数据
	public static final int REQUEST_SV_POST_CART_PRODUCT_CODE = 0X1070;
	// 提交关注商品请求
	public static final int REQUEST_SV_POST_COLLECITON_CODE = 0X1080;

	// 加载专题列表请求
	public static final int REQUEST_SV_GET_FIND_LIST_CODE = 0X3001;
	// 加载专题评论列表
	public static final int REQUEST_SV_GET_COMMENT_LIST_CODE = 0X3020;
	// 提交专题评论请求
	public static final int REQUEST_SV_POST_COMMENT_CODE = 0X3021;

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
	// 提交余额支付选项
	public static final int REQUEST_SV_POST_USE_BALANCE_CODE = 0X4042;
	// 提交确认订单数据
	public static final int REQUEST_SV_POST_CONFIRM_ORDER_CODE = 0X4043;
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
	// 获取支付宝授权信息
	public static final int REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE = 0X5006;
	// 获取支付宝用户信息
	public static final int REQUEST_SV_GET_ALIPAY_USERINFO_CODE = 0X5007;
	// 刷新或续期微信access_token
	public static final int REQUEST_SV_GET_WX_ACCESS_TOKEN_CODE = 0X5008;
	// 提交登出请求
	public static final int REQUEST_SV_POST_LOGOUT_CODE = 0X5009;
	// 提交重置密码请求
	public static final int REQUEST_SV_POST_RESET_PASSWORD_CODE = 0X5010;
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
	// 查看物流信息
	public static final int REQUEST_SV_GET_LOGISTICS_DATA_CODE = 0X5233;
	// 获取账户余额明细列表
	public static final int REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE = 0X5240;
	// 提交提现申请
	public static final int REQUEST_SV_POST_WITHDRAWALS_CODE = 0X5241;
	// 获取优惠券列表
	public static final int REQUEST_SV_GET_COUPON_LIST_CODE = 0X5250;
	// 添加优惠券
	public static final int REQUEST_SV_POST_COUPON_NO_CODE = 0X5251;
	// 校验选择的优惠券
	public static final int REQUEST_SV_POST_CHOOSE_COUPON_CODE = 0X5252;
	// 获取收货地址列表
	public static final int REQUEST_SV_GET_ADDRESS_LIST_CODE = 0X5260;
	// 获取自提点列表
	public static final int REQUEST_SV_GET_PICKUP_LIST_CODE = 0X5261;
	// 选择收货地址为默认地址
	public static final int REQUEST_SV_POST_SELECT_ADDRESS_CODE = 0X5262;
	// 删除收货地址
	public static final int REQUEST_SV_POST_DELETE_ADDRESS_CODE = 0X5263;
	// 获取国家列表清单
	public static final int REQUEST_SV_GET_COUNTRY_LIST_CODE = 0X5264;
	// 编辑收货地址
	public static final int REQUEST_SV_POST_EDIT_ADDRESS_CODE = 0X5265;

	/**
	 ******************************************* RequestCode参数设置结束 ******************************************
	 */

	/**
	 ******************************************* 偏好设置Key值设置开始 ******************************************
	 */

	// 偏好设置Key-记录用户ID
	public static final String KEY_USER_ID = "user_id";
	// 偏好设置Key-记录分享ID
	public static final String KEY_SHARE_ID = "share_id";
	// 偏好设置Key-记录登入账号
	public static final String KEY_LOGIN_ACCOUNT = "login_account";
	// 偏好设置Key-记录用户姓名
	public static final String KEY_USER_NAME = "user_name";
	// 偏好设置Key-记录用户身份ID
	public static final String KEY_USER_NAME_ID = "user_name_id";
	// 偏好设置Key-记录用户昵称
	public static final String KEY_USER_NICK = "user_nick";
	// 偏好设置Key-记录用户头像
	public static final String KEY_USER_AVATAR_URL = "user_avatar_url";
	// 偏好设置Key-记录用户简介
	public static final String KEY_USER_INTRO = "user_intro";
	// 偏好设置Key-记录用户性别
	public static final String KEY_USER_GENDER = "user_gender";
	// 偏好设置Key-记录用户生日
	public static final String KEY_USER_BIRTHDAY = "user_birthday";
	// 偏好设置Key-记录用户邮箱
	public static final String KEY_USER_EMAIL = "user_email";
	// 偏好设置Key-记录用户手机号码
	public static final String KEY_USER_PHONE = "user_phone";
	// 偏好设置Key-记录用户账户余额
	public static final String KEY_USER_MONEY = "user_money";
	// 偏好设置Key-记录用户等级编号
	public static final String KEY_USER_RANK_CODE = "user_rank_code";
	// 偏好设置Key-记录用户等级名称
	public static final String KEY_USER_RANK_NAME = "user_rank_name";
	// 偏好设置Key-记录用户购物车中商品数量
	public static final String KEY_CART_NUM = "cart_num";
	// 偏好设置Key-记录是否屏保播放视频
	public static final String KEY_IS_SCREEN_PLAY_VIDEO = "is_screen_play_video";
	// 偏好设置Key-记录是否屏保播放图片
	public static final String KEY_IS_SCREEN_PLAY_IMAGE= "is_screen_play_image";
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
	public static final String KEY_LOAD_SORT_DATA = "load_sort_data";
	// 偏好设置Key-记录推送服务的开关状态
	public static final String KEY_PUSH_STATUS = "push_status";
	// 偏好设置Key-记录商品搜索的历史记录
	public static final String KEY_SEARCH_WORDS_HISTORY = "search_words_history";
	// 偏好设置Key-记录首页当前的下标索引
	public static final String KEY_HOME_CURRENT_INDEX = "home_current_index";
	// 偏好设置Key-记录是否自动跳转到会员页面
	public static final String KEY_PUSH_PAGE_MEMBER = "push_page_member";
	// 偏好设置Key-记录循播视频的当前下标
	public static final String KEY_SCREEN_VIDEO_POSITION = "screen_video_position";
	// 偏好设置Key-记录循播图片的当前下标
	public static final String KEY_SCREEN_IMAGE_POSITION = "screen_image_position";
	// 偏好设置Key-记录更新循播数据的时间
	public static final String KEY_UPDATE_LOOP_DATA_DAY = "update_loop_date_day";

	/**
	 ******************************************* 偏好设置Key值设置结束 ******************************************
	 */

	/**
	 ******************************************* Activity传参设置开始 ******************************************
	 */

	public static final String ACTIVITY_SELECT_PHOTO_LIST = "select_photo_list";
	public static final String ACTIVITY_SHOW_PHOTO_LIST = "show_photo_list";
	public static final String ACTIVITY_CLIP_PHOTO_PATH = "clip_photo_path";
	public static final String ACTIVITY_CHANGE_USER_CONTENT = "change_user_content";
	public static final String ACTIVITY_SELECT_LIST_POSITION = "select_list_position";
	public static final int ACTIVITY_SELECT_PHOTO_PICKER = 0X9001;
	public static final int ACTIVITY_SHOW_PHOTO_PICKER = 0X9002;
	public static final int ACTIVITY_GET_IMAGE_VIA_CAMERA = 0X9003;
	public static final int ACTIVITY_GALLERY_CHOOSE_PHOTO = 0X9004;
	public static final int ACTIVITY_CHANGE_USER_NICK = 0X9005;
	public static final int ACTIVITY_CHANGE_USER_GENDER = 0X9006;
	public static final int ACTIVITY_CHANGE_USER_INTRO = 0X9007;
	public static final int ACTIVITY_CHANGE_USER_EMAIL = 0X9008;
	public static final int ACTIVITY_CHOOSE_PAY_TYPE = 0X9009;
	public static final int ACTIVITY_CHOOSE_ADD_COUNTRY = 0X9010;
	public static final int ACTIVITY_CHOOSE_ADD_PROVINCE = 0X9011;
	public static final int ACTIVITY_CHOOSE_ADD_CITY = 0X9012;
	public static final int ACTIVITY_CHOOSE_ADD_DISTRICT = 0X9013;

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