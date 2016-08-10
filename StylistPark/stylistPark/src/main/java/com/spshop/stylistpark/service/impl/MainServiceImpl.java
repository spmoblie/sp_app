
package com.spshop.stylistpark.service.impl;

import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BounsEntity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.CategoryListEntity;
import com.spshop.stylistpark.entity.GoodsCartEntity;
import com.spshop.stylistpark.entity.LogisticsEntity;
import com.spshop.stylistpark.entity.MemberEntity;
import com.spshop.stylistpark.entity.MyNameValuePair;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.entity.UpdateVersionEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.service.JsonParser;
import com.spshop.stylistpark.service.LoginJsonParser;
import com.spshop.stylistpark.service.MainService;
import com.spshop.stylistpark.utils.HttpUtil;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.utils.StringUtil;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainServiceImpl implements MainService {

	@Override
	public BaseEntity checkLoginSession() throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "sessions"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public UpdateVersionEntity checkVersionUpdate(int type, String version) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "checkVersion"));
		params.add(new MyNameValuePair("type", String.valueOf(type)));
		params.add(new MyNameValuePair("version", version));
		//HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		//String jsonStr = HttpUtil.getString(entity);
		String jsonStr = version;
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.checkVersionUpdate(jsonStr);
	}

	@Override
	public ThemeEntity getHomeHeadDatas() throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "home"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getHomeHeadDatas(jsonStr);
	}

	@Override
	public ThemeEntity getSpecialListDatas(int topType, int page, int count) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "articles"));
		params.add(new MyNameValuePair("cat_id", String.valueOf(topType)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getSpecialListDatas(jsonStr);
	}

	@Override
	public BaseEntity postComment(int postId, String commentStr) throws Exception {
		String uri = AppConfig.URL_COMMON_COMMENT_URL;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", String.valueOf(postId));
		jsonObject.put("type", "1");
		jsonObject.put("content", commentStr);
		String jsonStrValue = jsonObject.toString();

		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("cmt", jsonStrValue));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public CategoryListEntity getCategoryListDatas() throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "menu"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCategoryListDatas(jsonStr);
	}

	@Override
	public CategoryListEntity getCategoryBrandDatas() throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "key"));
		params.add(new MyNameValuePair("cat_id", "0"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCategoryBrandDatas(jsonStr);
	}

	@Override
	public ProductListEntity getProductListDatas(int typeId, int dataType, int brandId,
												 int count, int page, String searchStr, String attrStr, int isStock) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "list"));
		params.add(new MyNameValuePair("cat_id", String.valueOf(typeId)));
		params.add(new MyNameValuePair("price", String.valueOf(dataType)));
		params.add(new MyNameValuePair("brand", String.valueOf(brandId)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		params.add(new MyNameValuePair("keyword", searchStr));
		params.add(new MyNameValuePair("number", String.valueOf(isStock)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getProductListDatas(jsonStr);
	}

	@Override
	public SelectListEntity getScreenlistDatas(int typeId, String allStr) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "key"));
		params.add(new MyNameValuePair("cat_id", String.valueOf(typeId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getScreenlistDatas(jsonStr, allStr);
	}

	@Override
	public ProductDetailEntity getProductDetailDatas(int goodsId) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "goods"));
		params.add(new MyNameValuePair("id", String.valueOf(goodsId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getProductDetailDatas(jsonStr);
	}

	@Override
	public BrandEntity getBrandProfile(int brandId, String allStr) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "brand"));
		params.add(new MyNameValuePair("id", String.valueOf(brandId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getBrandProfile(jsonStr, allStr);
	}

	@Override
	public ProductListEntity getBrandProductLists(int brandId, int dataType, int selectId, int count, int page) throws Exception {
		String uri = AppConfig.URL_COMMON_PRODUCT_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "brand_goods"));
		params.add(new MyNameValuePair("id", String.valueOf(brandId)));
		params.add(new MyNameValuePair("order", String.valueOf(dataType)));
		params.add(new MyNameValuePair("cat_id", String.valueOf(selectId)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getBrandProductLists(jsonStr);
	}

	@Override
	public GoodsCartEntity postCartProductData(int quick, int goodsId,
											   int id1, int id2, int buyNumber, int parent) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=add_to_cart";
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		if (id1 > 0) {
			jsonArray.put(String.valueOf(id1));
			if (id2 > 0) {
				jsonArray.put(String.valueOf(id2));
			}
		}
		jsonObject.put("quick", quick);
		jsonObject.put("spec", jsonArray);
		jsonObject.put("goods_id", goodsId);
		jsonObject.put("number", buyNumber);
		jsonObject.put("parent", parent);
		String jsonStrValue = jsonObject.toString();

		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("goods", jsonStrValue));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.postCartProductData(jsonStr);
	}

	@Override
	public BaseEntity postCollectionProduct(int goodsId) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=collect";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("id", String.valueOf(goodsId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity postFeedBackData(String cotentStr) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL + "?act=feed_back";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("cotentStr", cotentStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity postRegisterData(String emailStr, String passwordStr) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL + "?app=register";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("email", emailStr));
		params.add(new MyNameValuePair("password", passwordStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public UserInfoEntity postRegisterOauthData(String accountStr, String passwordStr, String loginType,
												String uid, String nickname, String sex, String headUrl) throws Exception {
		String uri = "";
		if (StringUtil.isNull(accountStr) || StringUtil.isNull(passwordStr)) {
			uri = AppConfig.URL_COMMON_USER_URL + "?act=oath_register";
		}else {
			uri = AppConfig.URL_COMMON_USER_URL + "?act=signin";
		}
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("username", accountStr));
		params.add(new MyNameValuePair("password", passwordStr));
		params.add(new MyNameValuePair("type", loginType));
		params.add(new MyNameValuePair("userid", uid));
		params.add(new MyNameValuePair("nickname", nickname));
		params.add(new MyNameValuePair("sex", sex));
		params.add(new MyNameValuePair("avatar", headUrl));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return LoginJsonParser.postAccountLoginData(jsonStr);
	}

	@Override
	public UserInfoEntity postAccountLoginData(String userStr, String passWordStr) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=signin";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("username", userStr));
		params.add(new MyNameValuePair("password", passWordStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return LoginJsonParser.postAccountLoginData(jsonStr);
	}

	@Override
	public UserInfoEntity postThirdPartiesLogin(String loginType, String postUid) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=oath_api";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("type", loginType));
		params.add(new MyNameValuePair("userid", postUid));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return LoginJsonParser.postAccountLoginData(jsonStr);
	}

	@Override
	public BaseEntity postLogoutRequest() throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "logout"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity postResetPasswordData(String emailStr) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=send_pwd_email";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("email", emailStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public GoodsCartEntity getCartListDatas() throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "cart"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCartListDatas(jsonStr);
	}

	@Override
	public GoodsCartEntity postDeleteGoods(int recId) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=delete_cart";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("id", String.valueOf(recId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.postChangeGoods(jsonStr);
	}

	@Override
	public GoodsCartEntity postChangeGoods(int recId, int cartNum, int goodsId) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=update_group_cart";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("rec_id", String.valueOf(recId)));
		params.add(new MyNameValuePair("number", String.valueOf(cartNum)));
		params.add(new MyNameValuePair("goods_id", String.valueOf(goodsId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.postChangeGoods(jsonStr);
	}

	@Override
	public OrderEntity getConfirmOrderData() throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "checkout"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getConfirmOrderData(jsonStr);
	}

	@Override
	public BaseEntity postSelectPayment(int payType) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=select_payment";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("payment", String.valueOf(payType)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public OrderEntity postConfirmOrderData(int payTypeCode, int payType, String bounsId,
											String invoiceStr, String buyerStr, String orderAmount) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=done";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("shipping", String.valueOf(payTypeCode)));
		params.add(new MyNameValuePair("payment", String.valueOf(payType)));
		params.add(new MyNameValuePair("bonus", bounsId));
		params.add(new MyNameValuePair("postscript", buyerStr));
		params.add(new MyNameValuePair("order_amount", orderAmount));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.postConfirmOrderData(jsonStr);
	}

	@Override
	public AddressEntity getAddressLists() throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("act", "address_list"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getAddressLists(jsonStr);
	}

	@Override
	public BaseEntity postSelectAddress(String addressId) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=is_address";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("id", addressId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity postDeleteAddress(String addressId) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=drop_address";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("id", addressId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public AddressEntity getCountryLists(int postId) throws Exception {
		String uri = AppConfig.URL_COMMON_INDEX_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "regions"));
		params.add(new MyNameValuePair("parent", String.valueOf(postId)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCountryLists(jsonStr);
	}

	@Override
	public BaseEntity postEditAddress(int addressId, int countryId, int proviceId, int cityId, int districtId,
									  String addressStr, String nameStr, String phoneStr, String emailStr) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_address";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("address_id", String.valueOf(addressId)));
		params.add(new MyNameValuePair("country", String.valueOf(countryId)));
		params.add(new MyNameValuePair("province", String.valueOf(proviceId)));
		params.add(new MyNameValuePair("city", String.valueOf(cityId)));
		params.add(new MyNameValuePair("district", String.valueOf(districtId)));
		params.add(new MyNameValuePair("address", addressStr));
		params.add(new MyNameValuePair("consignee", nameStr));
		params.add(new MyNameValuePair("mobile", phoneStr));
		params.add(new MyNameValuePair("email", emailStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public UserInfoEntity getUserInfoSummary() throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "my"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getUserInfoSummary(jsonStr);
	}

	@Override
	public BaseEntity postChangeUserInfo(String changeStr, String changeTypeKey) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=edit_profile";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair(changeTypeKey, changeStr));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity checkUserEmailStatus() throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "is_validated"));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity sendEmailToUser() throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=send_hash_mail";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public ProductListEntity getCollectionOrHistoryList(int count, int page, String typeKey) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", typeKey));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getProductListDatas(jsonStr);
	}

	@Override
	public MemberEntity getMemberLists(int status, int count, int page) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "member"));
		params.add(new MyNameValuePair("status", String.valueOf(status)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getMemberLists(jsonStr);
	}

	@Override
	public OrderEntity getMemberOrderLists(int status, int count, int page) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "member_order"));
		params.add(new MyNameValuePair("status", String.valueOf(status)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getMemberOrderLists(jsonStr);
	}

	@Override
	public OrderEntity getOrderLists(int status, int count, int page) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "order_list"));
		params.add(new MyNameValuePair("status", String.valueOf(status)));
		params.add(new MyNameValuePair("size", String.valueOf(count)));
		params.add(new MyNameValuePair("page", String.valueOf(page)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getOrderLists(jsonStr);
	}

	@Override
	public OrderEntity getOrderDetails(String orderId) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "order_detail"));
		params.add(new MyNameValuePair("order_id", orderId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getOrderDetails(jsonStr);
	}

	@Override
	public BaseEntity postCacelOrder(String orderId) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=cancel_order";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("order_id", orderId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public LogisticsEntity getLogisticsDatas(String typeStr, String postId) throws Exception {
		String uri = "http://www.kuaidi100.com/query";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("type", typeStr));
		params.add(new MyNameValuePair("postid", postId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getLogisticsDatas(jsonStr);
	}

	@Override
	public PaymentEntity postPayment(int payType, String orderID) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("app", "edit_payment"));
		params.add(new MyNameValuePair("pay_id", String.valueOf(payType)));
		params.add(new MyNameValuePair("order_id", orderID));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.postPayment(payType, jsonStr);
	}

	@Override
	public PaymentEntity checkPaymentResult(int payType, String orderSn) throws Exception {
		String uri = AppConfig.URL_COMMON_MY_URL + "?app=pay_result";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("payType", String.valueOf(payType)));
		params.add(new MyNameValuePair("orderSn", orderSn));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.checkPaymentResult(jsonStr);
	}

	@Override
	public BalanceDetailEntity getBalanceDetailList(int count, int page) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL;
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("act", "account_detail"));
		params.add(new MyNameValuePair("size", String.valueOf(page)));
		params.add(new MyNameValuePair("page", String.valueOf(count)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getBalanceDetailList(jsonStr);
	}

	@Override
	public BaseEntity postWithdrawalsData(String card, int amount) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=act_account";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("card", card));
		params.add(new MyNameValuePair("amount", String.valueOf(amount)));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BounsEntity getBounsLists(int status, int count, int page, String rootStr) throws Exception {
		String uri = "";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		if ("PostOrderActivity".equals(rootStr)) {
			uri = AppConfig.URL_COMMON_FLOW_URL;
			params.add(new MyNameValuePair("step", "change_bonus"));
		}else {
			uri = AppConfig.URL_COMMON_USER_URL;
			params.add(new MyNameValuePair("act", "bonus"));
			params.add(new MyNameValuePair("status", String.valueOf(status)));
		}
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_GET);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getBounsLists(jsonStr);
	}

	@Override
	public BaseEntity postBounsNoData(String bounsNo) throws Exception {
		String uri = AppConfig.URL_COMMON_USER_URL + "?act=add_bonus";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("bonus_sn", bounsNo));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

	@Override
	public BaseEntity postChooseBouns(String bounsId) throws Exception {
		String uri = AppConfig.URL_COMMON_FLOW_URL + "?step=is_bonus";
		List<MyNameValuePair> params = new ArrayList<MyNameValuePair>();
		params.add(new MyNameValuePair("bonus", bounsId));
		HttpEntity entity = HttpUtil.getEntity(uri, params, HttpUtil.METHOD_POST);
		String jsonStr = HttpUtil.getString(entity);
		LogUtil.i("JsonParser", jsonStr);
		return JsonParser.getCommonResult(jsonStr);
	}

}
