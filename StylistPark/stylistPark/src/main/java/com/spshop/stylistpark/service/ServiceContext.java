package com.spshop.stylistpark.service;

import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BounsEntity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.CategoryListEntity;
import com.spshop.stylistpark.entity.GoodsCartEntity;
import com.spshop.stylistpark.entity.LogisticsEntity;
import com.spshop.stylistpark.entity.MemberEntity;
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.entity.UpdateVersionEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.service.impl.MainServiceImpl;
import com.spshop.stylistpark.utils.APIResult;

import org.json.JSONArray;

import java.util.List;


/**
 * 根据具体的任务调用相应的接口去执行任务并返回任务结果
 */
public class ServiceContext {

	private static ServiceContext sc;
	private MainService ms;

	private ServiceContext() {
		this.ms = new MainServiceImpl();
	}

	public static ServiceContext getServiceContext() {
		if (sc == null) {
			sc = new ServiceContext();
		}
		return sc;
	}

	/**
	 * 校验Sessions是否失效
	 */
	public BaseEntity checkLoginSession() throws Exception {
		return ms.checkLoginSession();
	}
	
	/**
	 * 检查版本更新
	 */
	public UpdateVersionEntity checkVersionUpdate(int type, String version) throws Exception {
		return ms.checkVersionUpdate(type, version);
	}

	/**
	 * 获取首页展示数据
	 */
	public ThemeEntity getHomeHeadDatas() throws Exception {
		return ms.getHomeHeadDatas();
	}

	/**
	 * 获取专题列表数据
	 */
	public ThemeEntity getSpecialListDatas(int topType, int page, int count) throws Exception {
		return ms.getSpecialListDatas(topType, page, count);
	}

	/**
	 * 提交专题评论数据
	 */
	public BaseEntity postComment(int postId, String commentStr) throws Exception {
		return ms.postComment(postId, commentStr);
	}

	/**
	 * 获取商品分类数据
	 */
	public CategoryListEntity getCategoryListDatas() throws Exception {
		return ms.getCategoryListDatas();
	}

	/**
	 * 获取分类品牌数据
	 */
	public CategoryListEntity getCategoryBrandDatas() throws Exception {
		return ms.getCategoryBrandDatas();
	}

	/**
	 * 获取商品列表数据
	 */
	public ProductListEntity getProductListDatas(int typeId, int dataType, int brandId, 
			int count, int page, String searchStr, String attrStr, int isStock) throws Exception {
		return ms.getProductListDatas(typeId, dataType, brandId, count, page, searchStr, attrStr, isStock);
	}

	/**
	 * 获取筛选列表数据
	 */
	public SelectListEntity getScreenlistDatas(int typeId, String allStr) throws Exception {
		return ms.getScreenlistDatas(typeId, allStr);
	}

	/**
	 * 获取商品详情数据
	 */
	public ProductDetailEntity getProductDetailDatas(int goodsId) throws Exception {
		return ms.getProductDetailDatas(goodsId);
	}

	/**
	 * 获取指定品牌相关信息
	 */
	public BrandEntity getBrandProfile(int brandId, String allStr) throws Exception {
		return ms.getBrandProfile(brandId, allStr);
	}

	/**
	 * 获取指定品牌商品列表数据
	 */
	public ProductListEntity getBrandProductLists(
			int brandId, int dataType, int selectId, int count, int page) throws Exception {
		return ms.getBrandProductLists(brandId, dataType, selectId, count, page);
	}

	/**
	 * 提交加入购物车商品数据
	 */
	public GoodsCartEntity postCartProductData(int quick, int goodsId, 
			int id1, int id2, int buyNumber, int parent) throws Exception {
		return ms.postCartProductData(quick, goodsId, id1, id2, buyNumber, parent);
	}

	/**
	 * 提交收藏商品
	 */
	public BaseEntity postCollectionProduct(int goodsId) throws Exception {
		return ms.postCollectionProduct(goodsId);
	}

	/**
	 * 问题反馈
	 */
	public BaseEntity postFeedBackData(String cotentStr) throws Exception {
		return ms.postFeedBackData(cotentStr);
	}

	/**
	 * 提交注册信息
	 */
	public BaseEntity postRegisterData(String emailStr, String passwordStr) throws Exception {
		return ms.postRegisterData(emailStr, passwordStr);
	}
	
	/**
	 * 提交第三方账号绑定信息
	 */
	public BaseEntity postRegisterOauthData(String accountStr, String passwordStr, String loginType,
			String uid, String nickname, String sex, String headUrl) throws Exception {
		return ms.postRegisterOauthData(accountStr, passwordStr, loginType, uid, nickname, sex, headUrl);
	}

	/**
	 * 提交账号密码登入信息
	 */
	public UserInfoEntity postAccountLoginData(String userStr, String passWordStr) throws Exception {
		return ms.postAccountLoginData(userStr, passWordStr);
	}

	/**
	 * 提交第三方登入信息
	 */
	public UserInfoEntity postThirdPartiesLogin(String loginType, String postUid) throws Exception {
		return ms.postThirdPartiesLogin(loginType, postUid);
	}

	/**
	 * 提交登出请求
	 */
	public BaseEntity postLogoutRequest() throws Exception {
		return ms.postLogoutRequest();
	}

	/**
	 * 提交重置密码请求
	 */
	public BaseEntity postResetPasswordData(String emailStr) throws Exception {
		return ms.postResetPasswordData(emailStr);
	}

	/**
	 * 加载购物车商品列表
	 */
	public GoodsCartEntity getCartListDatas() throws Exception {
		return ms.getCartListDatas();
	}

	/**
	 * 删除购物车中的商品
	 */
	public GoodsCartEntity postDeleteGoods(int recId) throws Exception {
		return ms.postDeleteGoods(recId);
	}

	/**
	 * 修改购物车中商品数量
	 */
	public GoodsCartEntity postChangeGoods(int recId, int cartNum, int goodsId) throws Exception {
		return ms.postChangeGoods(recId, cartNum, goodsId);
	}

	/**
	 * 获取待确认订单数据
	 */
	public OrderEntity getConfirmOrderData() throws Exception{
		return ms.getConfirmOrderData();
	}

	/**
	 * 提交选择的支付方式
	 */
	public BaseEntity postSelectPayment(int payType) throws Exception{
		return ms.postSelectPayment(payType);
	}

	/**
	 * 提交确认订单的数据
	 */
	public OrderEntity postConfirmOrderData(int payTypeCode, int payType, String bounsId, 
			String invoiceStr, String buyerStr, String orderAmount) throws Exception {
		return ms.postConfirmOrderData(payTypeCode, payType, bounsId, invoiceStr, buyerStr, orderAmount);
	}
	
	/**
	 * 获取收货地址列表
	 */
	public AddressEntity getAddressLists() throws Exception {
		return ms.getAddressLists();
	}

	/**
	 * 设置默认收货地址
	 */
	public BaseEntity postSelectAddress(String addressId) throws Exception {
		return ms.postSelectAddress(addressId);
	}

	/**
	 * 删除收货地址
	 */
	public BaseEntity postDeleteAddress(String addressId) throws Exception {
		return ms.postDeleteAddress(addressId);
	}

	/**
	 * 获取国家列表
	 */
	public AddressEntity getCountryLists(int postId) throws Exception {
		return ms.getCountryLists(postId);
	}

	/**
	 * 编辑收货地址
	 */
	public BaseEntity postEditAddress(int addressId, int countryId, int proviceId, int cityId, int districtId,
			String addressStr, String nameStr, String phoneStr, String emailStr) throws Exception {
		return ms.postEditAddress(addressId, countryId, proviceId, cityId,
				districtId, addressStr, nameStr, phoneStr, emailStr);
	}

	/**
	 * 获取用户信息汇总
	 */
	public UserInfoEntity getUserInfoSummary() throws Exception {
		return ms.getUserInfoSummary();
	}

	/**
	 * 修改用户信息
	 */
	public BaseEntity postChangeUserInfo(String changeStr, String changeTypeKey) throws Exception {
		return ms.postChangeUserInfo(changeStr, changeTypeKey);
	}

	/**
	 * 查询用户邮箱状态
	 */
	public BaseEntity checkUserEmailStatus() throws Exception {
		return ms.checkUserEmailStatus();
	}

	/**
	 * 发送邮件给用户
	 */
	public BaseEntity sendEmailToUser() throws Exception {
		return ms.sendEmailToUser();
	}

	/**
	 * 获取“收藏商品”或“浏览记录”商品列表
	 */
	public ProductListEntity getCollectionOrHistoryList(int count, int page, String typeKey) throws Exception {
		return ms.getCollectionOrHistoryList(count, page, typeKey);
	}

	/**
	 * 获取会员列表
	 */
	public MemberEntity getMemberLists(int status, int count, int page) throws Exception {
		return ms.getMemberLists(status, count, page);
	}
	
	/**
	 * 获取会员订单列表
	 */
	public OrderEntity getMemberOrderLists(int status, int count, int page) throws Exception {
		return ms.getMemberOrderLists(status, count, page);
	}

	/**
	 * 获取订单列表
	 */
	public OrderEntity getOrderLists(int status, int count, int page) throws Exception {
		return ms.getOrderLists(status, count, page);
	}

	/**
	 * 获取订单详情
	 */
	public OrderEntity getOrderDetails(String orderId) throws Exception {
		return ms.getOrderDetails(orderId);
	}

	/**
	 * 提交取消订单
	 */
	public BaseEntity postCacelOrder(String orderId) throws Exception {
		return ms.postCacelOrder(orderId);
	}

	/**
	 * 获取物流信息
	 */
	public LogisticsEntity getLogisticsDatas(String typeStr, String postId) throws Exception {
		return ms.getLogisticsDatas(typeStr, postId);
	}
	
	/**
	 * 提交支付请求
	 */
	public PaymentEntity postPayment(int payType, String orderID) throws Exception {
		return ms.postPayment(payType, orderID);
	}
	
	/**
	 * 查询支付结果
	 */
	public PaymentEntity checkPaymentResult(int payType, String orderSn) throws Exception {
		return ms.checkPaymentResult(payType, orderSn);
	}

	/**
	 * 查询余额明细
	 */
	public BalanceDetailEntity getBalanceDetailList(int count, int page) throws Exception {
		return ms.getBalanceDetailList(count, page);
	}

	/**
	 * 申请提现
	 */
	public BaseEntity postWithdrawalsData(String card, int amount) throws Exception {
		return ms.postWithdrawalsData(card, amount);
	}

	/**
	 * 获取红包列表
	 */
	public BounsEntity getBounsLists(int status, int count, int page, String rootStr) throws Exception {
		return ms.getBounsLists(status, count, page, rootStr);
	}

	/**
	 * 添加红包
	 */
	public BaseEntity postBounsNoData(String bounsNo) throws Exception {
		return ms.postBounsNoData(bounsNo);
	}

	/**
	 * 校验选择的红包
	 */
	public BaseEntity postChooseBouns(String bounsId) throws Exception {
		return ms.postChooseBouns(bounsId);
	}
	
	/**
	 * 获取搭配的商品列表
	 */
	public Object[] getCollageProductList(String userId, String typeId, String color, String brand, 
			String price, String keyword, Boolean isSelected, String endKey) throws Exception{
		return ms.getCollageProductList(userId, typeId, color, brand, price, keyword, isSelected, endKey);
	}
	
	/**
	 * 获取搭配的品牌列表
	 */
	public List<BrandEntity> getCollageBrandList() throws Exception{
		return ms.getCollageBrandList();
	}
	
	/**
	 * 获取搭配模板列表
	 */
	public JSONArray getCollageTemplateList() throws Exception{
		return ms.getCollageTemplateList();
	}
	
	/**
	 * 提交创建的搭配数据
	 */
	public APIResult submitLookBook(String userId, String sessionKey, String lookBookType, String title,
			String descripton, String filePath, String[] productIdList, String html, String mobileHtml) throws Exception {
		return ms.submitLookBook(userId, sessionKey, lookBookType, title, descripton, filePath, productIdList, html, mobileHtml);
	}

}
