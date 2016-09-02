package com.spshop.stylistpark.service;

import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.CouponEntity;
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

public interface MainService {

	BaseEntity checkLoginSession() throws Exception;

	UpdateVersionEntity checkVersionUpdate(int type, String version) throws Exception;
	
	ThemeEntity getHomeHeadDatas() throws Exception;

	ThemeEntity getSpecialListDatas(int topType, int page, int count) throws Exception;

	BaseEntity postComment(int postId, String commentStr) throws Exception;

	CategoryListEntity getCategoryListDatas() throws Exception;

	CategoryListEntity getCategoryBrandDatas() throws Exception;

	ProductListEntity getProductListDatas(int typeId, int dataType, int brandId, 
			int count, int page, String searchStr, String attrStr, int isStock) throws Exception;

	SelectListEntity getScreenlistDatas(int typeId, String allStr) throws Exception;

	ProductDetailEntity getProductDetailDatas(int goodsId) throws Exception;
	
	BrandEntity getBrandProfile(int brandId, String allStr) throws Exception;

	ProductListEntity getBrandProductLists(int brandId, int dataType, int selectId, int count, int page) throws Exception;

	GoodsCartEntity postCartProductData(int quick, int goodsId, int id1, int id2, int buyNumber, int parent) throws Exception;

	BaseEntity postCollectionProduct(int goodsId) throws Exception;

	BaseEntity postFeedBackData(String cotentStr) throws Exception;

	BaseEntity postRegisterData(String emailStr, String passwordStr) throws Exception;
	
	UserInfoEntity postRegisterOauthData(String accountStr, String passwordStr, String loginType,
			String uid, String nickname, String sex, String headUrl) throws Exception;

	UserInfoEntity postAccountLoginData(String userStr, String passWordStr) throws Exception;
	
	UserInfoEntity postThirdPartiesLogin(String loginType, String postUid) throws Exception;

	BaseEntity postLogoutRequest() throws Exception;
	
	BaseEntity postResetPasswordData(String emailStr) throws Exception;

	GoodsCartEntity getCartListDatas() throws Exception;

	GoodsCartEntity postDeleteGoods(int recId) throws Exception;

	GoodsCartEntity postChangeGoods(int recId, int cartNum, int goodsId) throws Exception;

	OrderEntity getConfirmOrderData() throws Exception;

	BaseEntity postSelectPayment(int payType) throws Exception;

	OrderEntity postConfirmOrderData(int payTypeCode, int payType, String couponId,
			String invoiceStr, String buyerStr, String orderAmount) throws Exception;

	AddressEntity getAddressLists() throws Exception;

	BaseEntity postSelectAddress(String addressId) throws Exception;

	BaseEntity postDeleteAddress(String addressId) throws Exception;
	
	AddressEntity getCountryLists(int postId) throws Exception;

	BaseEntity postEditAddress(int addressId, int countryId, int proviceId, int cityId, int districtId,
			String addressStr, String nameStr, String phoneStr, String emailStr) throws Exception;

	UserInfoEntity getUserInfoSummary() throws Exception;

	BaseEntity postChangeUserInfo(String changeStr, String changeTypeKey) throws Exception;

	BaseEntity checkUserEmailStatus() throws Exception;

	BaseEntity sendEmailToUser() throws Exception;

	BaseEntity postAuthData(String name, String number) throws Exception;

	ProductListEntity getCollectionOrHistoryList(int count, int page, String typeKey) throws Exception;

	MemberEntity getMemberLists(int status, int count, int page) throws Exception;
	
	OrderEntity getMemberOrderLists(int status, int count, int page) throws Exception;

	OrderEntity getOrderLists(int status, int count, int page) throws Exception;

	OrderEntity getOrderDetails(String orderId) throws Exception;

	BaseEntity postCacelOrder(String orderId) throws Exception;

	LogisticsEntity getLogisticsDatas(String typeStr, String postId) throws Exception;

	PaymentEntity postPayment(int payType, String orderID) throws Exception;

	PaymentEntity checkPaymentResult(int payType, String orderSn) throws Exception;

	BalanceDetailEntity getBalanceDetailList(int count, int page) throws Exception;

	BaseEntity postWithdrawalsData(String card, double amount) throws Exception;

	CouponEntity getCouponLists(int status, int count, int page, String rootStr) throws Exception;

	BaseEntity postCouponNoData(String couponNo) throws Exception;

	BaseEntity postChooseCoupon(String couponId) throws Exception;

}

