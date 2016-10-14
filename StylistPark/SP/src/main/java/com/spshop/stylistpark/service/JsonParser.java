package com.spshop.stylistpark.service;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.AddressEntity;
import com.spshop.stylistpark.entity.BalanceDetailEntity;
import com.spshop.stylistpark.entity.BaseEntity;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.SortListEntity;
import com.spshop.stylistpark.entity.CommentEntity;
import com.spshop.stylistpark.entity.CouponEntity;
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
		BaseEntity baseEn = new BaseEntity();
		getCommonKeyValue(baseEn, jsonObject);
		return baseEn;
	}

	/**
	 * 检查版本更新
	 */
	public static UpdateVersionEntity checkVersionUpdate(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		return new UpdateVersionEntity(0, "success", jsonObject.getString("desc"),
				jsonObject.getString("version"), jsonObject.getString("url"), jsonObject.getBoolean("force"));
	}

	/**
	 * 解析首页展示数据
	 */
	public static ThemeEntity getHomeHeadDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		ThemeEntity mainEn = new ThemeEntity();
		getCommonKeyValue(mainEn, jsonObject);
		// 解析广告
		if (StringUtil.notNull(jsonObject, "ad")) {
			JSONArray datas = jsonObject.getJSONArray("ad");
			ThemeEntity adEn = new ThemeEntity();
			ThemeEntity childEn;
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
		if (StringUtil.notNull(jsonObject, "best")) {
			ProductListEntity goodsEn = new ProductListEntity();
			goodsEn.setMainLists(getProductListsFormJson(jsonObject, "best"));
			mainEn.setGoodsEn(goodsEn);
		}
		// 解析今日专题
		/*if (StringUtil.notNull(jsonObj, "paida")) {
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
		if (StringUtil.notNull(jsonObject, "activity")) {
			JSONArray datas = jsonObject.getJSONArray("activity");
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
	public static ThemeEntity getFindListDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		ThemeEntity mainEn = new ThemeEntity();
		getCommonKeyValue(mainEn, jsonObject);
		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray datas = jsonObject.getJSONArray("data");
			ThemeEntity childEn;
			List<ThemeEntity> peidaLists = new ArrayList<ThemeEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new ThemeEntity();
				childEn.setId(StringUtil.getInteger(item.getString("article_id")));
				childEn.setType(StringUtil.getInteger(item.getString("open_type")));
				childEn.setClickNum(StringUtil.getInteger(item.getString("click_count")));
				childEn.setTitle(item.getString("description"));
				childEn.setNick(item.getString("nickname"));
				childEn.setAvatar(item.getString("avatar"));
				childEn.setImgUrl(item.getString("file_url"));
				childEn.setVdoUrl(item.getString("keywords"));
				peidaLists.add(childEn);
			}
			mainEn.setMainLists(peidaLists);
		}
		return mainEn;
	}

	/**
	 * 解析专题列表数据
	 */
	public static CommentEntity getCommentListDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		CommentEntity mainEn = new CommentEntity();
		getCommonKeyValue(mainEn, jsonObject);
		mainEn.setPageSize(StringUtil.getInteger(jsonObject.getString("size")));
		mainEn.setDataTotal(StringUtil.getInteger(jsonObject.getString("count")));

		if (StringUtil.notNull(jsonObject, "comments")) {
			JSONArray datas = jsonObject.getJSONArray("comments");
			CommentEntity childEn;
			List<CommentEntity> peidaLists = new ArrayList<CommentEntity>();
			for (int j = 0; j < datas.length(); j++) {
				JSONObject item = datas.getJSONObject(j);
				childEn = new CommentEntity();
				childEn.setCommentId(item.getString("id"));
				childEn.setAvatar(item.getString("avatar"));
				childEn.setUserNick(item.getString("nickname"));
				childEn.setContent(item.getString("content"));
				childEn.setAddTime(item.getString("add_time"));
				peidaLists.add(childEn);
			}
			mainEn.setMainLists(peidaLists);
		}
		return mainEn;
	}

	/**
	 * 解析商品分类数据
	 */
	public static SortListEntity getSortListDatas(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		SortListEntity mainEn = new SortListEntity();
		getCommonKeyValue(mainEn, jsonObject);
		SortListEntity en, child1, child2;
		List<SortListEntity> childLists1, mainLists;
		// 解析父分类
		mainLists = new ArrayList<SortListEntity>();
		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray datas = jsonObject.getJSONArray("data");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new SortListEntity();
				en.setTypeId(StringUtil.getInteger((item.getString("id"))));
				en.setImageUrl(item.getString("ico"));
				en.setName(item.getString("name"));
				// 解析第一子分类
				childLists1 = new ArrayList<SortListEntity>();
				if (StringUtil.notNull(item, "list")) {
					JSONArray data1 = item.getJSONArray("list");
					int show = 0;
					for (int j = 0; j < data1.length(); j++) {
						JSONObject item1 = data1.getJSONObject(j);
						child1 = new SortListEntity();
						child1.setTypeId(StringUtil.getInteger((item1.getString("id"))));
						child1.setImageUrl(item1.getString("cat_ico"));
						child1.setName(item1.getString("name"));
						show = StringUtil.getInteger(item1.getString("show_in_nav"));
						if (show == 1) {
							childLists1.add(child1);
						}
						// 解析第二子分类
						//childLists2 = new ArrayList<SortListEntity>();
						if (StringUtil.notNull(item1, "list")) {
							JSONArray data2 = item1.getJSONArray("list");
							for (int k = 0; k < data2.length(); k++) {
								JSONObject item2 = data2.getJSONObject(k);
								child2 = new SortListEntity();
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
		mainEn.setMainLists(mainLists);
		return mainEn;
	}

	/**
	 * 解析分类品牌数据
	 */
	public static SortListEntity getSortBrandDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		SortListEntity mainEn = new SortListEntity();
		getCommonKeyValue(mainEn, jsonObject);
		// 解析父分类
		mainEn.setTypeId(StringUtil.getInteger(jsonObject.getString("id")));
		mainEn.setName(jsonObject.getString("title"));
		// 解析子分类
		List<BrandEntity> brandLists = new ArrayList<BrandEntity>();
		if (StringUtil.notNull(jsonObject, "list")) {
			BrandEntity brandEn;
			JSONArray datas = jsonObject.getJSONArray("list");
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
		ProductListEntity mainEn = new ProductListEntity();
		getCommonKeyValue(mainEn, jsonObject);
		if (jsonObject.has("name")) {
			mainEn.setSortName(jsonObject.getString("name"));
		}
		mainEn.setMainLists(getProductListsFormJson(jsonObject, "data"));
		return mainEn;
	}

	/**
	 * 解析筛选列表数据
	 */
	public static SelectListEntity getScreenlistDatas(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		SelectListEntity mainEn = new SelectListEntity();
		getCommonKeyValue(mainEn, jsonObject);
		List<SelectListEntity> mainLists = new ArrayList<SelectListEntity>();
		// 获取品牌分类
		SelectListEntity brandEn = new SelectListEntity();
		brandEn.setTypeName(jsonObject.getString("title"));

		SelectListEntity childEn;
		List<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
		childLists.add(new SelectListEntity(0, AppApplication.getInstance().getString(R.string.all), ""));
		if (StringUtil.notNull(jsonObject, "list")) {
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
		/*childEn = null;
		childLists = null;
		if (StringUtil.notNull(jsonObject, "attr_list")) {
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
		// 获取其它分类
		mainEn.setMainLists(mainLists);
		return mainEn;
	}

	/**
	 * 解析商品详情数据
	 */
	public static ProductDetailEntity getProductDetailDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		ProductDetailEntity mainEn = new ProductDetailEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONObject goods = jsonObject.getJSONObject("data");
			mainEn.setId(StringUtil.getInteger(goods.getString("goods_id")));
			mainEn.setName(goods.getString("goods_name"));
			//mainEn.setStockNum(StringUtil.getInteger(goods.getString("goods_number")));
			//mainEn.setMailCountry(goods.getString("suppliers_country"));
			//mainEn.setPromoteTime(StringUtil.getLong(goods.getString("promote_time")));
			mainEn.setFullPrice(goods.getString("prices"));
			mainEn.setSellPrice(goods.getString("price"));
			mainEn.setComputePrice(StringUtil.getDouble(goods.getString("bag_price")));
			mainEn.setDiscount(goods.getString("sale"));
			mainEn.setIsCollection(goods.getString("collect"));
			mainEn.setIsVideo(StringUtil.getInteger(goods.getString("is_video")));
			mainEn.setVideoUrl(goods.getString("goods_sn"));
		}

		if (StringUtil.notNull(jsonObject, "brand")) {
			JSONObject brands = jsonObject.getJSONObject("brand");
			mainEn.setBrandId(brands.getString("id"));
			mainEn.setBrandLogo(brands.getString("logo"));
			mainEn.setBrandName(brands.getString("name"));
			mainEn.setBrandCountry(brands.getString("country"));
		}

		ProductDetailEntity en;
		if (StringUtil.notNull(jsonObject, "promotion")) {
			JSONArray pros = jsonObject.getJSONArray("promotion");
			List<ProductDetailEntity> promotionLists = new ArrayList<ProductDetailEntity>();
			for (int i = 0; i < pros.length(); i++) {
				JSONObject ps = pros.getJSONObject(i);
				en = new ProductDetailEntity();
				en.setPromotionType(ps.getString("type"));
				en.setPromotionName(ps.getString("act_name"));
				promotionLists.add(en);
			}
			mainEn.setPromotionLists(promotionLists);
		}

		if (StringUtil.notNull(jsonObject, "image")) {
			List<ProductDetailEntity> imgLists = new ArrayList<ProductDetailEntity>();
			JSONArray datas = jsonObject.getJSONArray("image");
			for (int i = 0; i < datas.length(); i++) {
				JSONObject item = datas.getJSONObject(i);
				en = new ProductDetailEntity();
				en.setImgId(item.getString("img_id"));
				en.setImgMinUrl(item.getString("thumb_url"));
				en.setImgMaxUrl(item.getString("img_url"));
				imgLists.add(en);
			}
			mainEn.setImgLists(imgLists);
		}

		return mainEn;
	}

	/**
	 * 解析获取商品属性数据
	 */
	public static ProductAttrEntity getProductAttrDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		ProductAttrEntity mainEn = new ProductAttrEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (mainEn.getErrCode() == 6) {
			mainEn.setGoodsId(StringUtil.getInteger(jsonObject.getString("goods_id")));
			mainEn.setFristImgUrl(jsonObject.getString("goods_thumb"));
			mainEn.setComputePrice(StringUtil.getDouble(jsonObject.getString("price")));
			// 解析商品attr
			mainEn.setAttrLists(getProductAttrLists(jsonObject, "message"));
			// 解析商品sku
			mainEn.setSkuLists(getProductSkuLists(jsonObject, "sku"));
		}
		return mainEn;
	}

	/**
	 * 解析指定品牌相关信息
	 */
	public static BrandEntity getBrandInfo(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		BrandEntity mainEn = new BrandEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONObject data = jsonObject.getJSONObject("data");
			mainEn.setBrandId(data.getString("id"));
			mainEn.setName(data.getString("name"));
			mainEn.setDefineURL(data.getString("banner"));
			mainEn.setLogoUrl(data.getString("logo"));
			mainEn.setDesc(data.getString("desc"));
			mainEn.setFavourable(data.getString("favourable"));
			mainEn.setEndTime(StringUtil.getLong(data.getString("time")));
		}

		if (StringUtil.notNull(jsonObject, "cat")) {
			JSONArray cats = jsonObject.getJSONArray("cat");
			SelectListEntity selectEn = new SelectListEntity();
			SelectListEntity childEn;
			List<SelectListEntity> childLists = new ArrayList<SelectListEntity>();
			// 获取筛选分类
			childLists.add(new SelectListEntity(0, AppApplication.getInstance().getString(R.string.all), ""));
			for (int i = 0; i < cats.length(); i++) {
				JSONObject item = cats.getJSONObject(i);
				childEn = new SelectListEntity();
				childEn.setChildId(StringUtil.getInteger(item.getString("cat_id")));
				childEn.setChildShowName(item.getString("cat_name"));
				//childEn.setChildLogoUrl(item.getString("url"));
				childLists.add(childEn);
			}
			selectEn.setChildLists(childLists);
			mainEn.setSelectEn(selectEn);
		}
		return mainEn;
	}

	/**
	 * 解析指定品牌商品列表数据
	 */
	public static ProductListEntity getBrandProductLists(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);
		ProductListEntity mainEn = new ProductListEntity();
		getCommonKeyValue(mainEn, jsonObject);
		mainEn.setMainLists(getProductListsFormJson(jsonObject, "goods"));
		return mainEn;
	}

	/**
	 * 解析提交商品数据结果
	 */
	public static GoodsCartEntity postCartProductData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		GoodsCartEntity mainEn = new GoodsCartEntity();
		getCommonKeyValue(mainEn, jsonObject);
		if (StringUtil.notNull(jsonObject, "content")) {
			mainEn.setGoodsTotal(StringUtil.getInteger(jsonObject.getString("content")));
		}
		return mainEn;
	}

	/**
	 * 解析购物车商品列表
	 */
	public static GoodsCartEntity getCartListDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		GoodsCartEntity mainEn = new GoodsCartEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "goods_list")) {
			JSONArray carts = jsonObject.getJSONArray("goods_list");
			mainEn.setChildLists(getCartProductLists(carts));
		}

		if (StringUtil.notNull(jsonObject, "total")) {
			JSONObject total = jsonObject.getJSONObject("total");
			mainEn.setGoodsTotal(StringUtil.getInteger(total.getString("goods_number")));
			mainEn.setAmount(total.getString("goods_amount"));
		}
		return mainEn;
	}

	/**
	 * 解析修改购物车商品数量结果
	 */
	public static GoodsCartEntity postChangeGoods(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		GoodsCartEntity mainEn = new GoodsCartEntity();
		getCommonKeyValue(mainEn, jsonObject);
		if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setGoodsTotal(StringUtil.getInteger(jsonObject.getString("order_number")));
			mainEn.setAmount(jsonObject.getString("order_amount"));
			if (jsonObject.has("number")) {
				mainEn.setSkuNum(StringUtil.getInteger(jsonObject.getString("number")));
			}
		}
		return mainEn;
	}

	/**
	 * 解析待确认订单数据
	 */
	public static OrderEntity getConfirmOrderData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		OrderEntity mainEn = new OrderEntity();
		getCommonKeyValue(mainEn, jsonObject);

		mainEn.setGoodsLists(getConfirmGoodsFormJson(jsonObject, "cart"));

		if (StringUtil.notNull(jsonObject, "address")) {
			JSONObject addObj = jsonObject.getJSONObject("address");
			AddressEntity addressEn = new AddressEntity();
			addressEn.setAddressId(StringUtil.getInteger(addObj.getString("address_id")));
			addressEn.setName(addObj.getString("consignee"));
			addressEn.setPhone(addObj.getString("mobile"));
			addressEn.setAddress(addObj.getString("address"));
			mainEn.setAddressEn(addressEn);
		}

		if (StringUtil.notNull(jsonObject, "total")) {
			JSONObject data = jsonObject.getJSONObject("total");
			mainEn.setPayId(StringUtil.getInteger(data.getString("pay_id")));
			mainEn.setPayTypeCode(StringUtil.getInteger(data.getString("shipping_id")));
			mainEn.setGoodsTotal(StringUtil.getInteger(data.getString("real_goods_count")));
			mainEn.setPriceTotal(data.getString("formated_goods_price"));
			mainEn.setPriceFee(data.getString("shipping_fee_formated"));
			mainEn.setPriceCharges(data.getString("pay_fee_formated"));
			mainEn.setPriceCoupon(data.getString("bonus_formated"));
			mainEn.setCouponId(data.getString("bonus_id"));
			mainEn.setPriceDiscount(data.getString("discount_formated"));
			mainEn.setPricePay(data.getString("amount_formated"));
			mainEn.setOrderAmount(data.getString("amount"));
		}
		return mainEn;
	}

	/**
	 * 解析提交确认订单结果
	 */
	public static OrderEntity postConfirmOrderData(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		OrderEntity mainEn = new OrderEntity();
		getCommonKeyValue(mainEn, jsonObject);
		if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setOrderNo(jsonObject.getString("content"));
		}
		return mainEn;
	}

	/**
	 * 解析收货地址列表
	 */
	public static AddressEntity getAddressLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		AddressEntity mainEn = new AddressEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray data = jsonObject.getJSONArray("data");
			AddressEntity addrEn;
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
		return mainEn;
	}

	/**
	 * 解析区域列表
	 */
	public static AddressEntity getCountryLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		AddressEntity mainEn = new AddressEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "regions")) {
			JSONArray data = jsonObject.getJSONArray("regions");
			AddressEntity addrEn;
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
		return mainEn;
	}

	/**
	 * 解析用户信息汇总
	 */
	public static UserInfoEntity getUserInfoSummary(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		UserInfoEntity mainEn = new UserInfoEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONObject data = jsonObject.getJSONObject("data");
			mainEn.setUserId(data.getString("user_id"));
			mainEn.setShareId(data.getString("share"));
			if (StringUtil.notNull(jsonObject, "name")) {
				mainEn.setUserName(data.getString("name"));
			}
			if (StringUtil.notNull(jsonObject, "name_id")) {
				mainEn.setUserNameID(data.getString("name_id"));
			}
			mainEn.setUserNick(data.getString("nickname"));
			mainEn.setUserAvatar(data.getString("avatar"));
			mainEn.setUserIntro(data.getString("intro"));
			mainEn.setGenderCode(StringUtil.getInteger(data.getString("sex")));
			mainEn.setBirthday(data.getString("birthday"));
			mainEn.setUserEmail(data.getString("email"));
			mainEn.setUserRankCode(StringUtil.getInteger(data.getString("user_rank")));
			mainEn.setUserRankName(data.getString("user_rank_name"));
			mainEn.setOrder_1(StringUtil.getInteger(data.getString("order_1")));
			mainEn.setOrder_2(StringUtil.getInteger(data.getString("order_2")));
			mainEn.setOrder_3(StringUtil.getInteger(data.getString("order_3")));
			mainEn.setOrder_4(StringUtil.getInteger(data.getString("order_4")));
			mainEn.setCartTotal(StringUtil.getInteger(data.getString("cart")));
			mainEn.setMoney(data.getString("money"));
			//mainEn.setCoupon(data.getString("bonus"));
			mainEn.setMemberNum(data.getString("member"));
			mainEn.setMemberOrder(data.getString("share"));
		}
		return mainEn;
	}

	/**
	 * 解析会员列表
	 */
	public static MemberEntity getMemberLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		MemberEntity mainEn = new MemberEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray data = jsonObject.getJSONArray("data");
			MemberEntity en;
			List<MemberEntity> mainLists = new ArrayList<MemberEntity>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				en = new MemberEntity();
				en.setUserId(item.getString("user_id"));
				en.setMemberNick(item.getString("nickname"));
				en.setGender(item.getString("sex"));
				en.setAvatar(item.getString("avatar"));
				//en.setMemberRank(StringUtil.getInteger(item.getString("user_rank")));
				//en.setOrderCount(item.getString("affiliate_count"));
				en.setOrderMoney(item.getString("affiliate_money"));
				//en.setLastLogin(item.getString("last_login"));
				mainLists.add(en);
			}
			mainEn.setMainLists(mainLists);
		}
		return mainEn;
	}

	/**
	 * 解析会员订单列表
	 */
	public static OrderEntity getMemberOrderLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		OrderEntity mainEn = new OrderEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray data = jsonObject.getJSONArray("data");
			OrderEntity en;
			UserInfoEntity infoEn;
			List<OrderEntity> mainLists = new ArrayList<OrderEntity>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				en = new OrderEntity();

				infoEn = new UserInfoEntity();
				infoEn.setUserNick(item.getString("user_name"));
				infoEn.setUserAvatar(item.getString("avatar"));
				infoEn.setUserRankCode(StringUtil.getInteger(item.getString("user_rank")));
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
		return mainEn;
	}

	/**
	 * 解析订单列表
	 */
	public static OrderEntity getOrderLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		OrderEntity mainEn = new OrderEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "orders")) {
			JSONArray data = jsonObject.getJSONArray("orders");
			OrderEntity en;
			List<OrderEntity> mainLists = new ArrayList<OrderEntity>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				en = new OrderEntity();
				en.setOrderId(item.getString("order_id"));
				en.setOrderNo(item.getString("order_sn"));
				//en.setStatus(StringUtil.getInteger(item.getString("status")));
				en.setStatusName(item.getString("handler"));
				//en.setPriceTotal(item.getString("total_fee"));
				//en.setGoodsTotalStr(item.getString("count"));

				/*long createTime = StringUtil.getLong(item.getString("add_time"))*1000;
				en.setCreateTime(createTime);
				en.setValidTime(createTime + 1800000); //有效时间30分钟*/
				en.setGoodsLists(getProductListsFormJson2(item, "goods"));
				mainLists.add(en);
			}
			mainEn.setMainLists(mainLists);
		}
		return mainEn;
	}

	/**
	 * 解析订单详情
	 */
	public static OrderEntity getOrderDetails(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		OrderEntity mainEn = new OrderEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray datas = jsonObject.getJSONArray("data");
			JSONObject data = datas.getJSONObject(0);
			mainEn.setOrderId(data.getString("order_id"));
			mainEn.setOrderNo(data.getString("order_sn"));
			mainEn.setStatus(StringUtil.getInteger(data.getString("status")));
			mainEn.setStatusName(data.getString("handler"));
			mainEn.setLogisticsName(data.getString("shipping_name"));
			mainEn.setLogisticsNo(data.getString("invoice_no"));
			mainEn.setGoodsTotalStr(data.getString("count"));
			mainEn.setPriceTotalName(data.getString("goods_amount_name"));
			mainEn.setPriceTotal(data.getString("goods_amount"));
			mainEn.setPriceFeeName(data.getString("shipping_fee_name"));
			mainEn.setPriceFee(data.getString("shipping_fee"));
			mainEn.setPriceCouponName(data.getString("bonus_name"));
			mainEn.setPriceCoupon(data.getString("bonus"));
			mainEn.setPriceDiscountName(data.getString("discount_name"));
			mainEn.setPriceDiscount(data.getString("discount"));
			mainEn.setPricePaidName(data.getString("money_paid_name"));
			mainEn.setPricePaid(data.getString("money_paid"));
			mainEn.setPricePayName(data.getString("order_amount_name"));
			mainEn.setPricePay(data.getString("order_amount"));
			mainEn.setPayId(StringUtil.getInteger(data.getString("pay_id")));
			mainEn.setPayType(data.getString("pay_name"));
			//mainEn.setInvoiceName(data.getString("inv_name"));
			//mainEn.setInvoiceType(data.getString("inv_type"));
			//mainEn.setInvoicePayee(data.getString("inv_payee"));
			//mainEn.setBuyerName(data.getString("postscript_name"));
			//mainEn.setBuyer(data.getString("postscript"));

			long createTime = StringUtil.getLong(data.getString("add_time"))*1000;
			mainEn.setCreateTime(createTime);
			mainEn.setValidTime(createTime + 1800000); //有效时间30分钟

			AddressEntity addrEn = new AddressEntity();
			addrEn.setName(data.getString("consignee"));
			addrEn.setPhone(data.getString("mobile"));
			addrEn.setCountry(data.getString("address_name"));
			addrEn.setAddress(data.getString("address"));
			mainEn.setAddressEn(addrEn);
			mainEn.setGoodsLists(getProductListsFormJson2(data, "goods_list"));
		}
		return mainEn;
	}

	/**
	 * 解析物流信息列表
	 */
	public static LogisticsEntity getLogisticsDatas(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		LogisticsEntity mainEn = new LogisticsEntity();
		if (jsonObject.has("status")) {
			mainEn.setErrCode(Integer.valueOf(jsonObject.getString("status")));
		}
		if (mainEn.getErrCode() == 200) {
			if (StringUtil.notNull(jsonObject, "data")) {
				JSONArray datas = jsonObject.getJSONArray("data");
				LogisticsEntity en;
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
		PaymentEntity mainEn = new PaymentEntity();
		getCommonKeyValue(mainEn, jsonObject);
		switch (payType) {
			case WXPayEntryActivity.PAY_ZFB: //支付宝支付
				mainEn.setAlipay(jsonObject.getString("content"));
				break;
			case WXPayEntryActivity.PAY_WEIXI: //微信支付
				JSONObject data = jsonObject.getJSONObject("content");
				mainEn.setPrepayid(data.getString("prepayid"));
				mainEn.setNoncestr(data.getString("noncestr"));
				mainEn.setTimestamp(data.getString("timestamp"));
				mainEn.setSign(data.getString("sign"));
				break;
			case WXPayEntryActivity.PAY_UNION: //银联支付
				mainEn.setAlipay(jsonObject.getString("content"));
				break;
			case WXPayEntryActivity.PAY_PAL: //PayPal支付
				mainEn.setAlipay(jsonObject.getString("content"));
				break;
		}
		return mainEn;
	}

	/**
	 * 解析查询的支付结果
	 */
	public static PaymentEntity checkPaymentResult(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		PaymentEntity mainEn = new PaymentEntity();
		getCommonKeyValue(mainEn, jsonObject);
		return mainEn;
	}

	/**
	 * 解析余额明细列表
	 */
	public static BalanceDetailEntity getBalanceDetailList(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		BalanceDetailEntity mainEn = new BalanceDetailEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (mainEn.getErrCode() == AppConfig.ERROR_CODE_SUCCESS) {
			mainEn.setAmount(StringUtil.getDouble(jsonObject.getString("amount")));
			mainEn.setStatus(StringUtil.getInteger(jsonObject.getString("content")));
			mainEn.setStatusHint(jsonObject.getString("message"));

			if (StringUtil.notNull(jsonObject, "account")) {
				JSONArray data = jsonObject.getJSONArray("account");
				BalanceDetailEntity en;
				List<BalanceDetailEntity> mainLists = new ArrayList<BalanceDetailEntity>();
				for (int i = 0; i < data.length(); i++) {
					JSONObject item = data.getJSONObject(i);
					en = new BalanceDetailEntity();
					en.setLogId(item.getString("log_id"));
					en.setChangeDesc(item.getString("change_desc"));
					en.setChangeTime(item.getString("change_time"));
					en.setType(item.getString("change_type"));
					en.setChangeMoney(item.getString("amount"));
					mainLists.add(en);
				}
				mainEn.setMainLists(mainLists);
			}
		}
		return mainEn;
	}

	/**
	 * 解析优惠券列表
	 */
	public static CouponEntity getCouponLists(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		CouponEntity mainEn = new CouponEntity();
		getCommonKeyValue(mainEn, jsonObject);

		if (StringUtil.notNull(jsonObject, "data")) {
			JSONArray data = jsonObject.getJSONArray("data");
			CouponEntity en;
			List<CouponEntity> mainLists = new ArrayList<CouponEntity>();
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.getJSONObject(i);
				en = new CouponEntity();
				en.setCouponId(item.getString("bonus_id"));
				en.setTypeName(item.getString("type_name"));
				en.setCurrency(item.getString("currency"));
				en.setCouponMoney(item.getString("type_money"));
				en.setCouponLimit(item.getString("min_goods_amount"));
				en.setStatusType(StringUtil.getInteger(item.getString("class")));
				en.setStatusName(item.getString("status"));
				en.setStartDate(item.getString("use_start_date"));
				en.setEndDate(item.getString("use_end_date"));
				mainLists.add(en);
			}
			mainEn.setMainLists(mainLists);
		}
		return mainEn;
	}

	/**
	 * 解析JSON获取展示商品列表
	 */
	private static ArrayList<ProductListEntity> getProductListsFormJson(JSONObject jsonObj, String key) throws JSONException {
		ArrayList<ProductListEntity> mainLists = new ArrayList<ProductListEntity>();
		if (StringUtil.notNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en;
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
		if (StringUtil.notNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en;
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
		if (StringUtil.notNull(jsonObj, key)) {
			JSONArray datas = jsonObj.getJSONArray(key);
			ProductListEntity en;
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
			ProductDetailEntity proEn;
			for (int i = 0; i < carts.length(); i++) {
				JSONObject item = carts.getJSONObject(i);
				proEn = new ProductDetailEntity();
				proEn.setId(StringUtil.getInteger(item.getString("goods_id")));
				proEn.setName(item.getString("goods_name"));
				proEn.setBrandName(item.getString("brand"));
				proEn.setImgMinUrl(item.getString("thumb"));
				proEn.setAttrStr(item.getString("goods_attr"));
				proEn.setSellPrice(item.getString("goods_price"));
				proEn.setRecId(StringUtil.getInteger(item.getString("rec_id")));
				proEn.setCartNum(StringUtil.getInteger(item.getString("goods_number")));
				proEn.setStockNum(StringUtil.getInteger(item.getString("total_number")));
				childLists.add(proEn);
			}
		}
		return childLists;
	}

	/**
	 * 解析获取商品属性值
	 */
	private static ArrayList<ProductAttrEntity> getProductAttrLists(JSONObject jsonObject, String key) throws JSONException {
		ArrayList<ProductAttrEntity> attrLists = new ArrayList<ProductAttrEntity>();
		if (StringUtil.notNull(jsonObject, key)) {
			JSONArray attr = jsonObject.getJSONArray(key);
			ProductAttrEntity listEn;
			for (int i = 0; i < attr.length(); i++) {
				JSONObject as = attr.getJSONObject(i);
				listEn = new ProductAttrEntity();
				listEn.setAttrId(StringUtil.getInteger(as.getString("attr_id")));
				listEn.setAttrName(as.getString("name"));

				ArrayList<ProductAttrEntity> asLists = new ArrayList<ProductAttrEntity>();
				ProductAttrEntity asEn;
				JSONArray list = as.getJSONArray("values");
				for (int j = 0; j < list.length(); j++) {
					JSONObject ls = list.getJSONObject(j);
					asEn = new ProductAttrEntity();
					asEn.setAttrId(StringUtil.getInteger(ls.getString("goods_attr_id")));
					asEn.setSkuNum(StringUtil.getInteger(ls.getString("number")));
					asEn.setAttrName(ls.getString("label"));
					asEn.setAttrPrice(StringUtil.getDouble(ls.getString("price")));
					asEn.setAttrImg(ls.getString("thumb_url"));
					asLists.add(asEn);
				}
				listEn.setAttrLists(asLists);
				attrLists.add(listEn);
			}
		}
		return attrLists;
	}

	/**
	 * 解析JSON获取商品SKU
	 */
	private static ArrayList<ProductAttrEntity> getProductSkuLists(JSONObject jsonObject, String key) throws JSONException {
		ArrayList<ProductAttrEntity> skuLists = new ArrayList<ProductAttrEntity>();
		if (StringUtil.notNull(jsonObject, key)) {
			JSONArray sku = jsonObject.getJSONArray(key);
			ProductAttrEntity skuEn;
			for (int i = 0; i < sku.length(); i++) {
				JSONObject ks = sku.getJSONObject(i);
				skuEn = new ProductAttrEntity();
				skuEn.setSku_key(ks.getString("goods_attr"));
				skuEn.setSku_value(StringUtil.getInteger(ks.getString("product_number")));
				skuLists.add(skuEn);
			}
		}
		return skuLists;
	}

	private static void getCommonKeyValue(BaseEntity baseEn, JSONObject jsonObj) throws JSONException{
		if (jsonObj.has("error")) {
			baseEn.setErrCode(StringUtil.getInteger(jsonObj.getString("error")));
		}
		if (jsonObj.has("message")) {
			baseEn.setErrInfo(jsonObj.getString("message"));
		}
		if (jsonObj.has("size")) {
			baseEn.setPageSize(StringUtil.getInteger(jsonObj.getString("size")));
		}
		if (jsonObj.has("count")) {
			baseEn.setDataTotal(StringUtil.getInteger(jsonObj.getString("count")));
		}
	}

}
