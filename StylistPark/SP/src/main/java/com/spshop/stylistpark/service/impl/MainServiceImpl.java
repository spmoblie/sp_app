
package com.spshop.stylistpark.service.impl;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.service.JsonParser;
import com.spshop.stylistpark.service.LoginJsonParser;
import com.spshop.stylistpark.service.MainService;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.wxapi.WXPayEntryActivity;

import org.apache.http.HttpEntity;

import java.util.List;

public class MainServiceImpl implements MainService {

	@Override
	public String getServerJSONString(String uri) throws Exception {
		HttpEntity entity = HttpUtil.getEntity(uri, null, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return jsonStr;
	}

	@Override
	public BaseEntity loadServerDatas(String tag, int requestCode,
		String uri, List<MyNameValuePair> params, int method) throws Exception {
		HttpEntity entity = HttpUtil.getEntity(uri, params, method);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		switch (requestCode) {
			case AppConfig.REQUEST_SV_GET_SESSIONS_CODE:
			case AppConfig.REQUEST_SV_GET_WX_SHARE_CODE:
			case AppConfig.REQUEST_SV_POST_SELECT_PAYMENT_CODE:
			case AppConfig.REQUEST_SV_POST_USE_BALANCE_CODE:
			case AppConfig.REQUEST_SV_POST_COLLECITON_CODE:
			case AppConfig.REQUEST_SV_POST_COMMENT_CODE:
			case AppConfig.REQUEST_SV_POST_LOGOUT_CODE:
			case AppConfig.REQUEST_SV_POST_FEED_BACK_CODE:
			case AppConfig.REQUEST_SV_POST_REGISTER_CODE:
			case AppConfig.REQUEST_SV_POST_RESET_PASSWORD_CODE:
			case AppConfig.REQUEST_SV_POST_EDIT_USER_INFO_CODE:
			case AppConfig.REQUEST_SV_CHECK_USER_EMAIL_STATUS:
			case AppConfig.REQUEST_SV_SEND_EMAIL_TO_USER:
			case AppConfig.REQUEST_SV_POST_AUTH_NAME:
			case AppConfig.REQUEST_SV_POST_CACEL_ORDER_CODE:
			case AppConfig.REQUEST_SV_POST_WITHDRAWALS_CODE:
			case AppConfig.REQUEST_SV_POST_COUPON_NO_CODE:
			case AppConfig.REQUEST_SV_POST_CHOOSE_COUPON_CODE:
			case AppConfig.REQUEST_SV_POST_SELECT_ADDRESS_CODE:
			case AppConfig.REQUEST_SV_POST_DELETE_ADDRESS_CODE:
			case AppConfig.REQUEST_SV_POST_EDIT_ADDRESS_CODE:
				return JsonParser.getCommonResult(jsonStr);

			case AppConfig.REQUEST_SV_POST_VERSION_CODE:
				return JsonParser.checkVersionUpdate(jsonStr);

			case AppConfig.REQUEST_SV_GET_SCREEN_VIDEO_CODE:
				return JsonParser.getScreenVideoLists(jsonStr);

			case AppConfig.REQUEST_SV_GET_HOME_SHOW_HEAD_CODE:
			return JsonParser.getHomeHeadDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_HOME_SHOW_LIST_CODE:
			case AppConfig.REQUEST_SV_GET_PRODUCT_LIST_CODE:
			case AppConfig.REQUEST_SV_GET_USER_PRODUCT_LIST:
				return JsonParser.getProductListDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_BRAND_PRODUCT_CODE:
				return JsonParser.getBrandProductLists(jsonStr);

			case AppConfig.REQUEST_SV_GET_SORT_LIST_CODE:
				return JsonParser.getSortListDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_BRANDS_LIST_CODE:
				return JsonParser.getSortBrandDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_SCREEN_LIST_CODE:
				return JsonParser.getScreenlistDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_BRAND_INFO_CODE:
				return JsonParser.getBrandInfo(jsonStr);

			case AppConfig.REQUEST_SV_GET_PRODUCT_DETAIL_CODE:
				return JsonParser.getProductDetailDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_PRODUCT_ATTR_CODE:
				return JsonParser.getProductAttrDatas(jsonStr);

			case AppConfig.REQUEST_SV_POST_CART_PRODUCT_CODE:
				return JsonParser.postCartProductData(jsonStr);

			case AppConfig.REQUEST_SV_GET_FIND_LIST_CODE:
				return JsonParser.getFindListDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_COMMENT_LIST_CODE:
				return JsonParser.getCommentListDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_CART_LIST_CODE:
				return JsonParser.getCartListDatas(jsonStr);

			case AppConfig.REQUEST_SV_POST_DELETE_GOODS_CODE:
			case AppConfig.REQUEST_SV_POST_CHANGE_GOODS_CODE:
				return JsonParser.postChangeGoods(jsonStr);

			case AppConfig.REQUEST_SV_GET_ORDER_CONFIRM_CODE:
				return JsonParser.getConfirmOrderData(jsonStr);

			case AppConfig.REQUEST_SV_POST_CONFIRM_ORDER_CODE:
				return JsonParser.postConfirmOrderData(jsonStr);

			case AppConfig.REQUEST_SV_POST_PAY_INFO_CODE:
				int payType = WXPayEntryActivity.PAY_ZFB;
				if (params.size() > 2) {
					payType = StringUtil.getInteger(params.get(1).getValue());
				}
				return JsonParser.postPayment(payType, jsonStr);

			case AppConfig.REQUEST_SV_GET_PAY_RESULT_CODE:
				return JsonParser.checkPaymentResult(jsonStr);

			case AppConfig.REQUEST_SV_POST_REGISTER_OAUTH_CODE:
			case AppConfig.REQUEST_SV_POST_ACCOUNT_LOGIN_CODE:
			case AppConfig.REQUEST_SV_POST_THIRD_PARTIES_LOGIN:
				return LoginJsonParser.postAccountLoginData(jsonStr);

			case AppConfig.REQUEST_SV_GET_ALIPAY_AUTHINFO_CODE:
				return LoginJsonParser.getAlipayAuthInfo(jsonStr);

			case AppConfig.REQUEST_SV_GET_ALIPAY_USERINFO_CODE:
				return LoginJsonParser.getAlipayUserInfo(jsonStr);

			case AppConfig.REQUEST_SV_GET_USERINFO_SUMMARY_CODE:
				return JsonParser.getUserInfoSummary(jsonStr);

			case AppConfig.REQUEST_SV_GET_MEMBER_LIST_CODE:
				return JsonParser.getMemberLists(jsonStr);

			case AppConfig.REQUEST_SV_GET_ORDER_LIST_CODE:
				if (params.get(0).getValue().equals("member_order")) {
					return JsonParser.getMemberOrderLists(jsonStr);
				} else {
					return JsonParser.getOrderLists(jsonStr);
				}

			case AppConfig.REQUEST_SV_GET_ORDER_DETAIL_CODE:
				return JsonParser.getOrderDetails(jsonStr);

			case AppConfig.REQUEST_SV_GET_LOGISTICS_DATA_CODE:
				return JsonParser.getLogisticsDatas(jsonStr);

			case AppConfig.REQUEST_SV_GET_BALANCE_DETAIL_LIST_CODE:
				return JsonParser.getBalanceDetailList(jsonStr);

			case AppConfig.REQUEST_SV_GET_COUPON_LIST_CODE:
				return JsonParser.getCouponLists(jsonStr);

			case AppConfig.REQUEST_SV_GET_ADDRESS_LIST_CODE:
				return JsonParser.getAddressLists(jsonStr);

			case AppConfig.REQUEST_SV_GET_COUNTRY_LIST_CODE:
				return JsonParser.getCountryLists(jsonStr);
		}
		return null;
	}

}
