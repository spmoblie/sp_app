package com.spshop.stylistpark.service;

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
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.PaymentEntity;
import com.spshop.stylistpark.entity.ProductAttrEntity;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.entity.ThemeEntity;
import com.spshop.stylistpark.entity.UpdateVersionEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.wxapi.WXPayEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonParser {

	public static BaseEntity getCommonResult(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		String message = "";
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		if (jsonObject.has("message")) {
			message = jsonObject.getString("message");
		}
		return new BaseEntity(errCode, message);
	}

	/**
	 * 检查版本更新
	 */
	public static UpdateVersionEntity checkVersionUpdate(String jsonStr) throws JSONException {
		//JSONObject jsonObject = new JSONObject(jsonStr);
		//int errCode = Integer.parseInt(jsonObject.getString("error"));
		//JSONObject item = jsonObject.getJSONObject("data");
		return new UpdateVersionEntity(0, "", "desc", jsonStr, "url", false);
	}

	/**
	 * 解析首页展示数据
	 */
	public static ThemeEntity getHomeHeadDatas(String jsonStr) throws JSONException {
		JSONObject jsonObj = new JSONObject(jsonStr);
		ThemeEntity mainEn = new ThemeEntity();
		if (jsonObj.has("error")) {
			mainEn.setErrCode(Integer.parseInt(jsonObj.getString("error")));
		}
		// 解析广告
		if (!StringUtil.isNull(jsonObj, "ad")) {
			JSONArray datas = jsonObj.getJSONArray("ad");
			ThemeEntity adEn = new ThemeEntity();
			ThemeEntity childEn = null;
			List<ThemeEntity> adLists = new ArrayList<ThemeEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new ThemeEntity();
				childEn.setId(StringUtil.getInteger(item.getString("topic_id")));
				childEn.setTitle(item.getString("title"));
				childEn.setImgUrl(item.getString("title_pic"));
				adLists.add(childEn);
			}
			adEn.setMainLists(adLists);
			mainEn.setAdEn(adEn);
		}
		// 解析热销商品
		if (!StringUtil.isNull(jsonObj, "best")) {
			ProductListEntity goodsEn = new ProductListEntity();
			goodsEn.setMainLists(getProductListsFormJson(jsonObj, "best"));
			mainEn.setGoodsEn(goodsEn);
		}
		// 解析今日专题
		/*if (!StringUtil.isNull(jsonObj, "paida")) {
			JSONArray datas = jsonObj.getJSONArray("paida");
			ThemeEntity peidaEn = new ThemeEntity();
			ThemeEntity childEn = null;
			List<ThemeEntity> peidaLists = new ArrayList<ThemeEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new ThemeEntity();
				childEn.setId(StringUtil.getInteger(item.getString("id")));
				childEn.setTitle(item.getString("title"));
				childEn.setImgUrl(item.getString("file_url"));
				peidaLists.add(childEn);
			}
			peidaEn.setMainLists(peidaLists);
			mainEn.setPeidaEn(peidaEn);
		}*/
		// 限时活动
		if (!StringUtil.isNull(jsonObj, "activity")) {
			JSONArray datas = jsonObj.getJSONArray("activity");
			ThemeEntity saleEn = new ThemeEntity();
			ThemeEntity childEn = null;
			List<ThemeEntity> saleLists = new ArrayList<ThemeEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new ThemeEntity();
				childEn.setId(StringUtil.getInteger(item.getString("id")));
				childEn.setTitle(item.getString("name"));
				childEn.setImgUrl(item.getString("logo"));
				childEn.setType(StringUtil.getInteger(item.getString("act_range")));
				childEn.setEndTime(StringUtil.getLong(item.getString("end_time")));
				saleLists.add(childEn);
			}
			saleEn.setMainLists(saleLists);
			mainEn.setSaleEn(saleEn);
		}
		return mainEn;
	}

	/**
	 * 解析专题列表数据
	 */
	public static ThemeEntity getSpecialListDatas(String jsonStr) throws JSONException {
		JSONObject jsonObj = new JSONObject(jsonStr);
		ThemeEntity eventEn = new ThemeEntity();
		if (jsonObj.has("error")) {
			eventEn.setErrCode(Integer.parseInt(jsonObj.getString("error")));
		}
		if (jsonObj.has("count")) {
			eventEn.setCountTotal(StringUtil.getInteger(jsonObj.getString("count")));
		}
		if (!StringUtil.isNull(jsonObj, "data")) {
			JSONArray datas = jsonObj.getJSONArray("data");
			ThemeEntity childEn = null;
			List<ThemeEntity> peidaLists = new ArrayList<ThemeEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new ThemeEntity();
				childEn.setId(StringUtil.getInteger(item.getString("article_id")));
				childEn.setType(StringUtil.getInteger(item.getString("open_type")));
				childEn.setClickNum(StringUtil.getInteger(item.getString("click_count")));
				childEn.setTitle(item.getString("title"));
				childEn.setMebName(item.getString("nickname"));
				childEn.setMebUrl(item.getString("avatar"));
				childEn.setImgUrl(item.getString("file_url"));
				childEn.setVdoUrl(item.getString("keywords"));
				peidaLists.add(childEn);
			}
			eventEn.setMainLists(peidaLists);
		}
		return eventEn;
	}

	/**
	 * 解析商品分类数据
	 */
	public static CategoryListEntity getCategoryListDatas(String jsonStr) throws JSONException{
		CategoryListEntity mainEn = null;
		CategoryListEntity en = null;
		CategoryListEntity child1, child2;
		List<CategoryListEntity> childLists1 = null;
		//List<CategoryListEntity> childLists2 = null;
		List<CategoryListEntity> mainLists = null;
		JSONObject jsonObject = new JSONObject(jsonStr);
		// 解析父分类
		mainLists = new ArrayList<CategoryListEntity>();
		if (!StringUtil.isNull(jsonObject, "data")) {
			JSONArray datas = jsonObject.getJSONArray("data");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new CategoryListEntity();
				en.setTypeId(StringUtil.getInteger((item.getString("id"))));
				en.setImageUrl(item.getString("ico"));
				en.setName(item.getString("name"));
				// 解析第一子分类
				childLists1 = new ArrayList<CategoryListEntity>();
				if (!StringUtil.isNull(item, "list")) {
					JSONArray data1 = item.getJSONArray("list");
					int show = 0;
					for (int j = 0; j < data1.length(); j++) {
						JSONObject item1 = data1.getJSONObject(j);
						child1 = new CategoryListEntity();
						child1.setTypeId(StringUtil.getInteger((item1.getString("id"))));
						child1.setImageUrl(item1.getString("cat_ico"));
						child1.setName(item1.getString("name"));
						show = StringUtil.getInteger(item1.getString("show_in_nav"));
						if (show == 1) {
							childLists1.add(child1);
						}
						// 解析第二子分类
						//childLists2 = new ArrayList<CategoryListEntity>();
						if (!StringUtil.isNull(item1, "list")) {
							JSONArray data2 = item1.getJSONArray("list");
							for (int k = 0; k < data2.length(); k++) {
								JSONObject item2 = data2.getJSONObject(k);
								child2 = new CategoryListEntity();
								child2.setTypeId(StringUtil.getInteger((item2.getString("id"))));
								child2.setImageUrl(item2.getString("cat_ico"));
								child2.setName(item2.getString("name"));
								//childLists2.add(child2);
								show = StringUtil.getInteger(item2.getString("show_in_nav"));
								if (show == 1) {
									childLists1.add(child2);
								}
							}
						}
						//child1.setChildLists(childLists2);
						//childLists1.add(child1);
					}
				}
				en.setChildLists(childLists1);
				mainLists.add(en);
			}
		}
		mainEn = new CategoryListEntity(0, "su", mainLists);
		return mainEn;
	}

	/**
	 * 解析分类品牌数据
	 */
	public static CategoryListEntity getCategoryBrandDatas(String jsonStr) throws JSONException {
		CategoryListEntity mainEn;
		BrandEntity brandEn;
		List<BrandEntity> brandLists;
		JSONObject brand = new JSONObject(jsonStr);
		// 解析父分类
		mainEn = new CategoryListEntity();
		mainEn.setTypeId(StringUtil.getInteger(brand.getString("id")));
		mainEn.setName(brand.getString("title"));
		// 解析子分类
		brandLists = new ArrayList<BrandEntity>();
		if (!StringUtil.isNull(brand, "list")) {
			JSONArray datas = brand.getJSONArray("list");
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				brandEn = new BrandEntity();
				brandEn.setBrandId(item.getString("brand_id"));
				brandEn.setName(item.getString("brand_name"));
				brandEn.setDefineURL(item.getString("brand_logo"));
				brandLists.add(brandEn);
			}
		}
		mainEn.setBrandLists(brandLists);
		return mainEn;
	}

	/**
	 * 解析商品列表数据
	 */
	public static ProductListEntity getProductListDatas(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		ProductListEntity mainEn = new ProductListEntity(1, "");
		if (jsonObject.has("error")) {
			int errCode  = Integer.valueOf(jsonObject.getString("error"));
			mainEn.setErrCode(errCode);
			if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
				if (jsonObject.has("count")) {
					mainEn.setTotal(StringUtil.getInteger(jsonObject.getString("count")));
				}
				mainEn.setMainLists(getProductListsFormJson(jsonObject, "data"));
			}
		}else {
			if (jsonObject.has("count")) {
				mainEn.setTotal(StringUtil.getInteger(jsonObject.getString("count")));
			}
			if (jsonObject.has("name")) {
				mainEn.setCategoryName(jsonObject.getString("name"));
			}
			mainEn.setMainLists(getProductListsFormJson(jsonObject, "data"));
		}
		return mainEn;
	}

	/**
	 * 解析筛选列表数据
	 */
	public static SelectListEntity getScreenlistDatas(String jsonStr, String allStr) throws JSONException{
		SelectListEntity mainEn, brandEn, childEn;
		List<SelectListEntity> childLists = null;
		List<SelectListEntity> mainLists = null;
		JSONObject jsonObject = new JSONObject(jsonStr);
		// 获取品牌分类
		brandEn = new SelectListEntity();
		brandEn.setTypeName(jsonObject.getString("title"));

		mainLists = new ArrayList<SelectListEntity>();
		childLists = new ArrayList<SelectListEntity>();
		childLists.add(new SelectListEntity(0, allStr, ""));
		if (!StringUtil.isNull(jsonObject, "list")) {
			JSONArray brandLists = jsonObject.getJSONArray("list");
			for (int i = 0; i < brandLists.length(); i++) {
				JSONObject item = brandLists.getJSONObject(i);
				childEn = new SelectListEntity();
				childEn.setChildId(StringUtil.getInteger(item.getString("brand_id")));
				childEn.setChildShowName(item.getString("brand_name"));
				childEn.setChildLogoUrl(item.getString("brand_logo"));
				childLists.add(childEn);
			}
		}
		brandEn.setChildLists(childLists);
		mainLists.add(brandEn);
		// 获取其它分类
		/*childEn = null;
		childLists = null;
		if (!StringUtil.isNull(jsonObject, "attr_list")) {
			JSONArray otherDatas = jsonObject.getJSONArray("attr_list");
			for (int i = 0; i < otherDatas.length(); i++) {
				JSONObject items = otherDatas.getJSONObject(i);
				otherEn = new SelectListEntity();
				otherEn.setTypeId(StringUtil.getInteger(items.getString("attr_id")));
				otherEn.setTypeName(items.getString("attr_name"));

				childLists = new ArrayList<SelectListEntity>();
				childLists.add(new SelectListEntity(0, allStr, ""));
				JSONArray childDatas = items.getJSONArray("list");
				for (int j = 0; j < childDatas.length(); j++) {
					JSONObject item = childDatas.getJSONObject(j);
					childEn = new SelectListEntity();
					childEn.setChildId(StringUtil.getInteger(item.getString("goods_attr_id")));
					childEn.setChildParamName(item.getString("value"));
					childEn.setChildShowName(item.getString("attr_value"));
					childLists.add(childEn);
				}
				otherEn.setChildLists(childLists);
				mainLists.add(otherEn);
			}
		}*/
		mainEn = new SelectListEntity(0, "su", mainLists);
		return mainEn;
	}

	/**
	 * 解析商品详情数据
	 */
	public static ProductDetailEntity getProductDetailDatas(String jsonStr) throws JSONException {
		ProductDetailEntity mainEn = null;
		ProductDetailEntity en = null;
		List<ProductDetailEntity> imgLists = null;
		List<ProductDetailEntity> promotionLists = null;
		ProductAttrEntity attrEn = new ProductAttrEntity();
		JSONObject jsonObject = new JSONObject(jsonStr);
		JSONObject goods = jsonObject.getJSONObject("data");
		mainEn = new ProductDetailEntity();
		mainEn.setId(StringUtil.getInteger(goods.getString("goods_id")));
		mainEn.setName(goods.getString("goods_name"));
		//mainEn.setStockNum(StringUtil.getInteger(goods.getString("goods_number")));
		//mainEn.setMailCountry(goods.getString("suppliers_country"));
		//mainEn.setPromoteTime(StringUtil.getLong(goods.getString("promote_time")));
		//mainEn.setCurrency(goods.getString("currency"));
		mainEn.setFullPrice(goods.getString("prices"));
		mainEn.setSellPrice(goods.getString("price"));
		mainEn.setComputePrice(StringUtil.getInteger(goods.getString("bag_price")));
		mainEn.setDiscount(goods.getString("sale"));
		mainEn.setIsCollection(goods.getString("collect"));
		mainEn.setIsVideo(StringUtil.getInteger(goods.getString("is_video")));
		mainEn.setVideoUrl(goods.getString("goods_sn"));
		
		JSONObject brands = jsonObject.getJSONObject("brand");
		mainEn.setBrandId(brands.getString("id"));
		mainEn.setBrandLogo(brands.getString("logo"));
		mainEn.setBrandName(brands.getString("name"));
		mainEn.setBrandCountry(brands.getString("country"));
		
		promotionLists = new ArrayList<ProductDetailEntity>();
		if (!StringUtil.isNull(jsonObject, "promotion")) {
			JSONArray pros = jsonObject.getJSONArray("promotion");
			for (int i = 0; i < pros.length(); i++) {
				JSONObject ps = pros.getJSONObject(i);
				en = new ProductDetailEntity();
				en.setPromotionType(ps.getString("type"));
				en.setPromotionName(ps.getString("act_name"));
				promotionLists.add(en);
			}
		}
		mainEn.setPromotionLists(promotionLists);
		
		imgLists = new ArrayList<ProductDetailEntity>();
		if (!StringUtil.isNull(jsonObject, "image")) {
			JSONArray datas = jsonObject.getJSONArray("image");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new ProductDetailEntity();
				en.setImgId(item.getString("img_id"));
				en.setImgMinUrl(item.getString("thumb_url"));
				en.setImgMaxUrl(item.getString("img_url"));
				imgLists.add(en);
			}
		}
		mainEn.setImgLists(imgLists);
		// 解析商品attr
		ArrayList<ProductAttrEntity> attrLists = new ArrayList<ProductAttrEntity>();
		if (!StringUtil.isNull(jsonObject, "type")) {
			JSONArray attr = jsonObject.getJSONArray("type");
			ProductAttrEntity listEn = null;
			for (int i = 0; i < attr.length(); i++) {
				JSONObject as = attr.getJSONObject(i);
				listEn = new ProductAttrEntity();
				listEn.setAttrId(StringUtil.getInteger(as.getString("attr_id")));
				listEn.setAttrName(as.getString("name"));
				
				ArrayList<ProductAttrEntity> asLists = new ArrayList<ProductAttrEntity>();
				ProductAttrEntity asEn = null;
				JSONArray list = as.getJSONArray("values");
				for (int j = 0; j < list.length(); j++) {
					JSONObject ls = list.getJSONObject(j);
					asEn = new ProductAttrEntity();
					asEn.setAttrId(StringUtil.getInteger(ls.getString("id")));
					asEn.setSkuNum(StringUtil.getInteger(ls.getString("number")));
					asEn.setAttrName(ls.getString("label"));
					asEn.setAttrPrice(StringUtil.getInteger(ls.getString("price")));
					asEn.setAttrImg(ls.getString("thumb_url"));
					asLists.add(asEn);
				}
				listEn.setAttrLists(asLists);
				attrLists.add(listEn);
			}
		}
		attrEn.setAttrLists(attrLists);
		// 解析商品sku
		ArrayList<ProductAttrEntity> skuLists = new ArrayList<ProductAttrEntity>();
		if (!StringUtil.isNull(jsonObject, "sku")) {
			JSONArray sku = jsonObject.getJSONArray("sku");
			ProductAttrEntity skuEn = null;
			for (int i = 0; i < sku.length(); i++) {
				JSONObject ks = sku.getJSONObject(i);
				skuEn = new ProductAttrEntity();
				skuEn.setSku_key(ks.getString("goods_attr"));
				skuEn.setSku_value(StringUtil.getInteger(ks.getString("product_number")));
				skuLists.add(skuEn);
			}
		}
		attrEn.setSkuLists(skuLists);
		mainEn.setAttrEn(attrEn);
		return mainEn;
	}

	/**
	 * 解析指定品牌相关信息
	 */
	public static BrandEntity getBrandProfile(String jsonStr, String allStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		JSONObject data = jsonObject.getJSONObject("data");
		BrandEntity brandEn = new BrandEntity();
		brandEn.setBrandId(data.getString("id"));
		brandEn.setName(data.getString("name"));
		brandEn.setDefineURL(data.getString("banner"));
		brandEn.setLogoUrl(data.getString("logo"));
		brandEn.setDesc(data.getString("desc"));
		brandEn.setFavourable(data.getString("favourable"));
		brandEn.setEndTime(StringUtil.getLong(data.getString("time")));

		if (!StringUtil.isNull(jsonObject, "cat")) {
			JSONArray cats = jsonObject.getJSONArray("cat");
			SelectListEntity selectEn = new SelectListEntity();
			SelectListEntity childEn;
			List<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
			// 获取筛选分类
			childLists.add(new SelectListEntity(0, allStr, ""));
			for (int i = 0; i < cats.length(); i++) {
				JSONObject item = cats.getJSONObject(i);
				childEn = new SelectListEntity();
				childEn.setChildId(StringUtil.getInteger(item.getString("cat_id")));
				childEn.setChildShowName(item.getString("cat_name"));
				//childEn.setChildLogoUrl(item.getString("url"));
				childLists.add(childEn);
			}
			selectEn.setChildLists(childLists);
			brandEn.setSelectEn(selectEn);
		}
		return brandEn;
	}

	/**
	 * 解析指定品牌商品列表数据
	 */
	public static ProductListEntity getBrandProductLists(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		ProductListEntity mainEn = new ProductListEntity(1, "");
		if (jsonObject.has("total")) {
			mainEn.setTotal(StringUtil.getInteger(jsonObject.getString("total")));
		}
		mainEn.setMainLists(getProductListsFormJson(jsonObject, "goods"));
		return mainEn;
	}

	/**
	 * 解析提交商品数据结果
	 */
	public static GoodsCartEntity postCartProductData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		String errInfo = "";
		if (jsonObject.has("message")) {
			errInfo = jsonObject.getString("message");
		}
		GoodsCartEntity cartEn = new GoodsCartEntity(errCode, errInfo);
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			cartEn.setGoodsTotal(StringUtil.getInteger(jsonObject.getString("content")));
		}
		return cartEn;
	}

	/**
	 * 解析购物车商品列表
	 */
	public static GoodsCartEntity getCartListDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		GoodsCartEntity cartEn = new GoodsCartEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			JSONArray carts = jsonObject.getJSONArray("cart");
			cartEn.setChildLists(getCartProductLists(carts));
			
			JSONObject total = jsonObject.getJSONObject("total");
			cartEn.setCurrency(total.getString("currency"));
			cartEn.setGoodsTotal(StringUtil.getInteger(total.getString("number")));
			cartEn.setAmount(total.getString("amount"));
		}
		return cartEn;
	}

	/**
	 * 解析修改购物车商品数量结果
	 */
	public static GoodsCartEntity postChangeGoods(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		String msg = "";
		if (jsonObject.has("message")) {
			msg = jsonObject.getString("message");
		}
		GoodsCartEntity cartEn = new GoodsCartEntity(errCode, msg);
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			cartEn.setGoodsTotal(StringUtil.getInteger(jsonObject.getString("order_number")));
			cartEn.setAmount(jsonObject.getString("order_amount"));
			if (jsonObject.has("number")) {
				cartEn.setSkuNum(StringUtil.getInteger(jsonObject.getString("number")));
			}
		}
		return cartEn;
	}

	/**
	 * 解析待确认订单数据
	 */
	public static OrderEntity getConfirmOrderData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = 1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		OrderEntity orderEn = new OrderEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			orderEn.setGoodsLists(getConfirmGoodsFormJson(jsonObject, "cart"));

			if (!StringUtil.isNull(jsonObject, "address")) {
				JSONObject addObj = jsonObject.getJSONObject("address");
				AddressEntity addressEn = new AddressEntity();
				addressEn.setAddressId(StringUtil.getInteger(addObj.getString("address_id")));
				addressEn.setName(addObj.getString("consignee"));
				addressEn.setPhone(addObj.getString("mobile"));
				addressEn.setAddress(addObj.getString("address"));
				orderEn.setAddressEn(addressEn);
			}

			JSONObject data = jsonObject.getJSONObject("total");
			orderEn.setPayId(StringUtil.getInteger(data.getString("pay_id")));
			orderEn.setPayTypeCode(StringUtil.getInteger(data.getString("shipping_id")));
			orderEn.setGoodsTotal(StringUtil.getInteger(data.getString("real_goods_count")));
			orderEn.setPriceTotal(data.getString("goods_price_formated"));
			orderEn.setPriceFee(data.getString("shipping_fee_formated"));
			orderEn.setPriceCharges(data.getString("pay_fee_formated"));
			orderEn.setPriceBonus(data.getString("bonus_formated"));
			orderEn.setBounsId(data.getString("bonus_id"));
			orderEn.setPriceDiscount(data.getString("discount_formated"));
			orderEn.setPricePay(data.getString("amount_formated"));
			orderEn.setOrderAmount(data.getString("amount"));
		}
		return orderEn;
	}

	/**
	 * 解析提交确认订单结果
	 */
	public static OrderEntity postConfirmOrderData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = 1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		OrderEntity orderEn = new OrderEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (jsonObject.has("message")) {
				orderEn.setErrInfo(jsonObject.getString("message"));
			}
			orderEn.setOrderNo(jsonObject.getString("content"));
		}
		return orderEn;
	}

	/**
	 * 解析收货地址列表
	 */
	public static AddressEntity getAddressLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		AddressEntity mainEn = new AddressEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray data = jsonObject.getJSONArray("data");
				AddressEntity addrEn = null;
				List<AddressEntity> mainLists = new ArrayList<AddressEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					addrEn = new AddressEntity();
					addrEn.setAddressId(StringUtil.getInteger(item.getString("address_id")));
					addrEn.setDefaultId(StringUtil.getInteger(item.getString("user_address")));
					addrEn.setName(item.getString("consignee"));
					addrEn.setPhone(item.getString("mobile"));
					addrEn.setEmail(item.getString("email"));
					addrEn.setCountryId(StringUtil.getInteger(item.getString("country")));
					addrEn.setProviceId(StringUtil.getInteger(item.getString("province")));
					addrEn.setCityId(StringUtil.getInteger(item.getString("city")));
					addrEn.setDistrictId(StringUtil.getInteger(item.getString("district")));
					addrEn.setEditAdd(item.getString("address1"));
					addrEn.setAddress(item.getString("address"));
					mainLists.add(addrEn);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析国家列表
	 */
	public static AddressEntity getCountryLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		AddressEntity mainEn = new AddressEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (!StringUtil.isNull(jsonObject, "regions")) {
				JSONArray data = jsonObject.getJSONArray("regions");
				AddressEntity addrEn = null;
				List<AddressEntity> mainLists = new ArrayList<AddressEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					addrEn = new AddressEntity();
					addrEn.setCountryId(StringUtil.getInteger(item.getString("region_id")));
					addrEn.setCountry(item.getString("region_name"));
					mainLists.add(addrEn);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析用户信息汇总
	 */
	public static UserInfoEntity getUserInfoSummary(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		UserInfoEntity infoEn = new UserInfoEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONObject data = jsonObject.getJSONObject("data");
				infoEn.setUserId(data.getString("user_id"));
				infoEn.setUserName(data.getString("user_name"));
				infoEn.setUserNick(data.getString("nickname"));
				infoEn.setHeadImg(data.getString("avatar"));
				infoEn.setUserIntro(data.getString("intro"));
				infoEn.setSexCode(StringUtil.getInteger(data.getString("sex")));
				infoEn.setBirthday(data.getString("birthday"));
				infoEn.setUserEmail(data.getString("email"));
				infoEn.setUserPhone(data.getString("mobile_phone"));
				infoEn.setAuth(StringUtil.isNull(data.getString("name")) ? false : true);
				infoEn.setUserRankType(StringUtil.getInteger(data.getString("user_rank")));
				infoEn.setUserRankName(data.getString("user_rank_name"));
				infoEn.setOrder_1(StringUtil.getInteger(data.getString("order_1")));
				infoEn.setOrder_2(StringUtil.getInteger(data.getString("order_2")));
				infoEn.setOrder_3(StringUtil.getInteger(data.getString("order_3")));
				infoEn.setOrder_4(StringUtil.getInteger(data.getString("order_4")));
				infoEn.setCartTotal(StringUtil.getInteger(data.getString("cart")));
				infoEn.setMoney(data.getString("money"));
				infoEn.setBonus(data.getString("bonus"));
				infoEn.setMemberNum(data.getString("member"));
				infoEn.setMemberOrder(data.getString("share"));
			}
		}
		return infoEn;
	}

	/**
	 * 解析会员列表
	 */
	public static MemberEntity getMemberLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		MemberEntity mainEn = new MemberEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setCountTotal(StringUtil.getInteger(jsonObject.getString("count")));
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray data = jsonObject.getJSONArray("data");
				MemberEntity en = null;
				List<MemberEntity> mainLists = new ArrayList<MemberEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new MemberEntity();
					en.setUserId(item.getString("user_id"));
					en.setUserName(item.getString("nickname"));
					en.setUserSex(item.getString("sex"));
					en.setHeadImg(item.getString("avatar"));
					//en.setMemberRank(StringUtil.getInteger(item.getString("user_rank")));
					//en.setOrderCount(item.getString("affiliate_count"));
					en.setOrderMoney(item.getString("affiliate_money"));
					en.setLastLogin(item.getString("last_login"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}
	
	/**
	 * 解析会员订单列表
	 */
	public static OrderEntity getMemberOrderLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		OrderEntity mainEn = new OrderEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setOrderTotal(StringUtil.getInteger(jsonObject.getString("count")));
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray data = jsonObject.getJSONArray("data");
				OrderEntity en = null;
				UserInfoEntity infoEn = null;
				List<OrderEntity> mainLists = new ArrayList<OrderEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new OrderEntity();
					
					infoEn = new UserInfoEntity();
					infoEn.setUserName(item.getString("user_name"));
					infoEn.setHeadImg(item.getString("avatar"));
					infoEn.setUserRankType(StringUtil.getInteger(item.getString("user_rank")));
					en.setUserInfo(infoEn);
					
					en.setOrderId(item.getString("order_id"));
					en.setStatusName(item.getString("handler"));
					en.setPriceTotal(item.getString("goods_amount"));
					en.setPricePaid(item.getString("user_amount"));
					en.setBuyer(item.getString("order_time"));
					en.setGoodsLists(getProductListsFormJson2(item, "goods_list"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析订单列表
	 */
	public static OrderEntity getOrderLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		OrderEntity mainEn = new OrderEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setOrderTotal(StringUtil.getInteger(jsonObject.getString("count")));
			if (!StringUtil.isNull(jsonObject, "orders")) {
				JSONArray data = jsonObject.getJSONArray("orders");
				OrderEntity en = null;
				List<OrderEntity> mainLists = new ArrayList<OrderEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new OrderEntity();
					en.setOrderId(item.getString("order_id"));
					en.setOrderNo(item.getString("order_sn"));
					//en.setStatus(StringUtil.getInteger(item.getString("status")));
					en.setStatusName(item.getString("handler"));
					en.setPriceTotal(item.getString("total_fee"));
					en.setGoodsTotalStr(item.getString("count"));

					/*long createTime = StringUtil.getLong(item.getString("add_time"))*1000;
					en.setCreateTime(createTime);
					en.setValidTime(createTime + 1800000); //有效时间30分钟*/
					en.setGoodsLists(getProductListsFormJson2(item, "goods"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析订单详情
	 */
	public static OrderEntity getOrderDetails(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		OrderEntity orderEn = new OrderEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray datas = jsonObject.getJSONArray("data");
				JSONObject data = datas.getJSONObject(0);
				orderEn.setOrderId(data.getString("order_id"));
				orderEn.setOrderNo(data.getString("order_sn"));
				orderEn.setStatus(StringUtil.getInteger(data.getString("status")));
				orderEn.setStatusName(data.getString("handler"));
				orderEn.setLogisticsName(data.getString("shipping_name"));
				orderEn.setLogisticsNo(data.getString("invoice_no"));
				orderEn.setGoodsTotalStr(data.getString("count"));
				orderEn.setCurrency(data.getString("currencys"));
				orderEn.setPriceTotalName(data.getString("goods_amount_name"));
				orderEn.setPriceTotal(data.getString("goods_amount"));
				orderEn.setPriceFeeName(data.getString("shipping_fee_name"));
				orderEn.setPriceFee(data.getString("shipping_fee"));
				orderEn.setPriceBonusName(data.getString("bonus_name"));
				orderEn.setPriceBonus(data.getString("bonus"));
				orderEn.setPriceDiscountName(data.getString("discount_name"));
				orderEn.setPriceDiscount(data.getString("discount"));
				orderEn.setPricePaidName(data.getString("money_paid_name"));
				orderEn.setPricePaid(data.getString("money_paid"));
				orderEn.setPricePayName(data.getString("order_amount_name"));
				orderEn.setPricePay(data.getString("order_amount"));
				orderEn.setPayId(StringUtil.getInteger(data.getString("pay_id")));
				orderEn.setPayType(data.getString("pay_name"));
				//orderEn.setInvoiceName(data.getString("inv_name"));
				//orderEn.setInvoiceType(data.getString("inv_type"));
				//orderEn.setInvoicePayee(data.getString("inv_payee"));
				//orderEn.setBuyerName(data.getString("postscript_name"));
				//orderEn.setBuyer(data.getString("postscript"));
				
				long createTime = StringUtil.getLong(data.getString("add_time"))*1000;
				orderEn.setCreateTime(createTime);
				orderEn.setValidTime(createTime + 1800000); //有效时间30分钟
				
				AddressEntity addrEn = new AddressEntity();
				addrEn.setName(data.getString("consignee"));
				addrEn.setPhone(data.getString("mobile"));
				addrEn.setCountry(data.getString("address_name"));
				addrEn.setAddress(data.getString("address"));
				orderEn.setAddressEn(addrEn);
				orderEn.setGoodsLists(getProductListsFormJson2(data, "goods_list"));
			}
		}
		return orderEn;
	}

	/**
	 * 解析物流信息列表
	 */
	public static LogisticsEntity getLogisticsDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errorCode = -1;
		if (jsonObject.has("status")) {
			errorCode = Integer.valueOf(jsonObject.getString("status"));
		}
		LogisticsEntity mainEn = new LogisticsEntity(errorCode, "");
		if (errorCode == 200) {
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray datas = jsonObject.getJSONArray("data");
				LogisticsEntity en = null;
				ArrayList<LogisticsEntity> mainLists = new ArrayList<LogisticsEntity>();
				for (int i = 0; i < datas.length(); i++) {
					JSONObject item = datas.getJSONObject(i);
					en = new LogisticsEntity();
					en.setMsgContent(item.getString("context"));
					en.setMsgTime(item.getString("time"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析提交支付返回数据
	 */
	public static PaymentEntity postPayment(int payType, String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		PaymentEntity payEn = new PaymentEntity(errCode, "");
		if (errCode == 15) {
			switch (payType) {
				case WXPayEntryActivity.PAY_ZFB: //支付宝支付
					payEn.setAlipay(jsonObject.getString("content"));
					break;
				case WXPayEntryActivity.PAY_WEIXI: //微信支付
					JSONObject data = jsonObject.getJSONObject("content");
					payEn.setPrepayid(data.getString("prepayid"));
					payEn.setNoncestr(data.getString("noncestr"));
					payEn.setTimestamp(data.getString("timestamp"));
					payEn.setSign(data.getString("sign"));
				break;
			case WXPayEntryActivity.PAY_UNION: //银联支付
				payEn.setAlipay(jsonObject.getString("content"));
				break;
			case WXPayEntryActivity.PAY_PAL: //PayPal支付
				payEn.setAlipay(jsonObject.getString("content"));
				break;
			}
		}
		return payEn;
	}

	/**
	 * 解析查询的支付结果
	 */
	public static PaymentEntity checkPaymentResult(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = Integer.parseInt(jsonObject.getString("errCode"));
		PaymentEntity payEn = new PaymentEntity(errCode, "");
		if (errCode == 0) {
			JSONObject data = jsonObject.getJSONObject("data");
			payEn = new PaymentEntity(errCode, "", "", "", "", "",
					data.getString("trade_state"), data.getString("trade_state_desc"));
		}
		return payEn;
	}

	/**
	 * 解析余额明细列表
	 */
	public static BalanceDetailEntity getBalanceDetailList(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		BalanceDetailEntity mainEn = new BalanceDetailEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setCountTotal(StringUtil.getInteger(jsonObject.getString("count")));
			mainEn.setAmount(StringUtil.getInteger(jsonObject.getString("amount")));
			mainEn.setStatus(StringUtil.getInteger(jsonObject.getString("content")));
			mainEn.setStatusHint(jsonObject.getString("message"));
			if (!StringUtil.isNull(jsonObject, "account")) {
				JSONArray data = jsonObject.getJSONArray("account");
				BalanceDetailEntity en = null;
				List<BalanceDetailEntity> mainLists = new ArrayList<BalanceDetailEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new BalanceDetailEntity();
					en.setChangeDesc(item.getString("change_desc"));
					en.setChangeTime(item.getString("change_time"));
					en.setType(item.getString("type"));
					en.setChangeMoney(item.getString("amount"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析红包列表
	 */
	public static BounsEntity getBounsLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		int errCode = -1;
		if (jsonObject.has("error")) {
			errCode = Integer.valueOf(jsonObject.getString("error"));
		}
		BounsEntity mainEn = new BounsEntity(errCode, "");
		if (errCode == AppConfig.ERROR_CODE_SUCCESS) {
			if (jsonObject.has("count")) {
				mainEn.setCountTotal(StringUtil.getInteger(jsonObject.getString("count")));
			}
			if (!StringUtil.isNull(jsonObject, "data")) {
				JSONArray data = jsonObject.getJSONArray("data");
				BounsEntity en = null;
				List<BounsEntity> mainLists = new ArrayList<BounsEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new BounsEntity();
					en.setBounsId(item.getString("bonus_id"));
					en.setTypeName(item.getString("type_name"));
					en.setCurrency(item.getString("currency"));
					en.setBounsMoney(item.getString("type_money"));
					en.setBounsLimit(item.getString("min_goods_amount"));
					en.setStatusType(StringUtil.getInteger(item.getString("class")));
					en.setStatusName(item.getString("status"));
					en.setStartDate(item.getString("use_start_date"));
					en.setEndDate(item.getString("use_end_date"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析JSON获取展示商品列表
	 */
	private static ArrayList<ProductListEntity> getProductListsFormJson(JSONObject jsonObj, String key) throws JSONException {
		ArrayList<ProductListEntity> mainLists = new ArrayList<ProductListEntity>();
		if (!StringUtil.isNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en = null;
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new ProductListEntity();
				en.setId(StringUtil.getInteger((item.getString("goods_id"))));
				en.setImageUrl(item.getString("goods_thumb"));
				en.setBrand(item.getString("brand_name"));
				en.setName(item.getString("goods_name"));
				en.setFullPrice(item.getString("prices"));
				en.setSellPrice(item.getString("price"));
				en.setDiscount(item.getString("sale"));
				mainLists.add(en);
			}
		}
		return mainLists;
	}
	
	/**
	 * 解析JSON获取已生成订单相关商品
	 */
	private static ArrayList<ProductListEntity> getProductListsFormJson2(JSONObject jsonObj, String key) throws JSONException {
		ArrayList<ProductListEntity> mainLists = new ArrayList<ProductListEntity>();
		if (!StringUtil.isNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en = null;
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new ProductListEntity();
				en.setId(StringUtil.getInteger((item.getString("id"))));
				en.setImageUrl(item.getString("thumb"));
				en.setName(item.getString("name"));
				en.setBrand(item.getString("brand"));
				en.setSellPrice(item.getString("price"));
				en.setTotal(StringUtil.getInteger(item.getString("number")));
				en.setAttr(item.getString("attr"));
				mainLists.add(en);
			}
		}
		return mainLists;
	}
	
	/**
	 * 解析JSON获取待确认订单相关商品
	 */
	private static ArrayList<ProductListEntity> getConfirmGoodsFormJson(JSONObject jsonObj, String key) throws JSONException {
		ArrayList<ProductListEntity> mainLists = new ArrayList<ProductListEntity>();
		if (!StringUtil.isNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en = null;
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new ProductListEntity();
				en.setId(StringUtil.getInteger((item.getString("goods_id"))));
				en.setImageUrl(item.getString("thumb"));
				en.setName(item.getString("goods_name"));
				en.setBrand(item.getString("brand"));
				en.setSellPrice(item.getString("goods_price"));
				en.setTotal(StringUtil.getInteger(item.getString("goods_number")));
				en.setAttr(item.getString("goods_attr"));
				mainLists.add(en);
			}
		}
		return mainLists;
	}

	/**
	 * 解析JSON获取购物车商品
	 */
	private static ArrayList<ProductDetailEntity> getCartProductLists(JSONArray carts) throws JSONException {
		ArrayList<ProductDetailEntity> childLists = new ArrayList<ProductDetailEntity>();
		if (carts != null && !carts.equals("")) {
			ProductDetailEntity proEn = null;
			for (int i = 0; i < carts.length(); i++) {
				JSONObject item = carts.getJSONObject(i);
				proEn = new ProductDetailEntity();
				proEn.setId(StringUtil.getInteger(item.getString("goods_id")));
				proEn.setName(item.getString("goods_name"));
				proEn.setBrandName(item.getString("brand"));
				proEn.setImgMinUrl(item.getString("thumb"));
				proEn.setAttrStr(item.getString("goods_attr"));
				proEn.setCurrency(item.getString("currency"));
				proEn.setSellPrice(item.getString("goods_price"));
				proEn.setRecId(StringUtil.getInteger(item.getString("rec_id")));
				proEn.setCartNum(StringUtil.getInteger(item.getString("goods_number")));
				proEn.setStockNum(StringUtil.getInteger(item.getString("total_number")));
				childLists.add(proEn);
			}
		}
		return childLists;
	}

}
