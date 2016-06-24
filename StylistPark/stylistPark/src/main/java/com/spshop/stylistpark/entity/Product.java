package com.spshop.stylistpark.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.spshop.stylistpark.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable{
	
	String TAG="Product";

	private String mItemId = null;
	private String mName = null;
	private String mColor = null;
	private String mBrand = null;
	private String mPrice = null;
	private String mImgUrl = null;
	private String mThumbUrl = null;
	private String mModelImgUrl = null;
	private String mModelThumbUrl = null;
	private List<List<String>> mImgList = null;
//	private List<List<String>> mVideoList = null;
	private String mVideoList = null;
	private boolean mIsSelected = false;
	private JSONObject json;
	
	public Product() {
		// comment line for code analysis
	}

	public Product(JSONObject json) {
		if (json == null) {
			return;
		}
		this.json = json;
		JSONArray tmpJsonArr = null;
		JSONObject tmpJsonObj = null;
		List<String> tmpUrlList = null;
		try {
			try {
				mItemId = json.getString("itemId");
			} catch (JSONException e) {
				e.printStackTrace();
				try {
					mItemId = json.getString("productId");
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
			try {
				mName = json.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mColor = json.getString("color");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mBrand = json.getString("brand");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mPrice = json.getString("price");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mImgUrl = json.getString("imgUrl");
				if(mImgUrl!=null && !mImgUrl.isEmpty()){
					mImgUrl=AppConfig.PRODUCT_JSON_DOMAIN+mImgUrl;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
//				mThumbUrl =  URLEncoder.encode(AppConfig.PRODUCT_JSON_DOMAIN+json.getString("thumbUrl"), "utf-8");
				mThumbUrl =  json.getString("thumbUrl");
				if(mThumbUrl!=null && !mThumbUrl.isEmpty()){
					mThumbUrl=AppConfig.PRODUCT_JSON_DOMAIN+mThumbUrl;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mModelImgUrl = json.getString("modelImgUrl");
				if(mModelImgUrl!=null && !mModelImgUrl.isEmpty()){
					mModelImgUrl=AppConfig.PRODUCT_JSON_DOMAIN+mModelImgUrl;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mModelThumbUrl = json.getString("modelThumbUrl");
				if(mModelThumbUrl!=null && !mModelThumbUrl.isEmpty()){
					mModelThumbUrl=AppConfig.PRODUCT_JSON_DOMAIN+mModelThumbUrl;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//thumb
			if(mThumbUrl==null || mThumbUrl.isEmpty()){
				mThumbUrl=mModelThumbUrl;
			}
			if(mModelThumbUrl==null || mModelThumbUrl.isEmpty() ){
				mModelThumbUrl=mThumbUrl;
			}
			//large bm
			if(mImgUrl==null || mImgUrl.isEmpty()){
				mImgUrl=mModelImgUrl;
			}
			if(mModelImgUrl==null || mModelImgUrl.isEmpty() ){
				mModelImgUrl=mImgUrl;
			}
			
			try {
				tmpJsonArr = json.getJSONArray("imgList");
				if (tmpJsonArr != null) {
					mImgList = new ArrayList<List<String>>();
					for (int i = 0; i < tmpJsonArr.length(); i++) {
						try {
							tmpJsonObj = tmpJsonArr.getJSONObject(i);
							tmpUrlList = new ArrayList<String>();
							tmpUrlList.add( AppConfig.PRODUCT_JSON_DOMAIN+tmpJsonObj.getString("imgUrl"));
							tmpUrlList.add( AppConfig.PRODUCT_JSON_DOMAIN+tmpJsonObj.getString("thumbUrl"));
							mImgList.add(tmpUrlList);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
//				tmpJsonArr = json.getJSONArray("videoList");
//				if (tmpJsonArr != null) {
//					mVideoList = new ArrayList<List<String>>();
//					for (int i = 0; i < tmpJsonArr.length(); i++) {
//						try {
//							tmpJsonObj = tmpJsonArr.getJSONObject(i);
//							tmpUrlList = new ArrayList<String>();
//							tmpUrlList.add(tmpJsonObj.getString("videoUrl"));
//							tmpUrlList.add(tmpJsonObj.getString("thumbUrl"));
//							mVideoList.add(tmpUrlList);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				}
				mVideoList =  AppConfig.PRODUCT_JSON_DOMAIN+json.getString("videoUrl");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				mIsSelected = json.getBoolean("isSelected");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getItemId() {
		return mItemId;
	}

	public String getName() {
		return mName;
	}

	public String getColor() {
		return mColor;
	}

	public String getBrand() {
		return mBrand;
	}

	public String getPrice() {
		return mPrice;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public String getThumbUrl() {
		Log.d(TAG,"mThumbUrl="+mThumbUrl);
		return mThumbUrl;
	}

	public String getModelImgUrl() {
		return mModelImgUrl;
	}

	public String getModelThumbUrl() {
		return mModelThumbUrl;
	}

	public List<List<String>> getImgList() {
		return mImgList;
	}

//	public List<List<String>> getVideoList() {
//		return mVideoList;
//	}
	public String getVideoList() {
		return mVideoList;
	}

	public boolean isSelected() {
		return mIsSelected;
	}
	
	public void display() {
		Log.d("Product",
				" getItemId = " + getItemId()
				+ " getName = " + getName()
				+ " getColor = " + getColor()
				+ " getBrand = " + getBrand()
				+ " getPrice = " + getPrice()
				+ " getImgUrl = " + getImgUrl()
				+ " getThumbUrl = " + getThumbUrl()
				+ " getModelImgUrl = " + getModelImgUrl()
				+ " getModelThumbUrl = " + getModelThumbUrl()
				+ " getImgList = " + ((getImgList() == null)?getImgList():getImgList().size())
//				+ " getVideoList = " + ((getVideoList() == null)?getVideoList():getVideoList().size())
				+ " getVideoList = " + getVideoList()
				+ " isSelected = " + isSelected());
	}
	
	/*
	 * Parcelable part
	 */
	
	 // example constructor that takes a Parcel and gives you an object populated with it's values
    private Product(Parcel in) {
    	mItemId=in.readString();
    	mName = in.readString();
    	mColor =in.readString();
    	mBrand =in.readString();
    	mPrice =in.readString();
    	mImgUrl =in.readString();
    	mThumbUrl =in.readString();
    	mModelImgUrl =in.readString();
    	mModelThumbUrl =in.readString();
    	
    	if(mImgList==null)
    		mImgList=new ArrayList<List<String>>(); 
    	in.readList(mImgList, null);
    	
    	mVideoList=in.readString();
    	mIsSelected = in.readByte() != 0; 
    }
	
	 // write your object's data to the passed-in Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {

		out.writeString(mItemId);
		out.writeString(mName);
		out.writeString(mColor);
		out.writeString(mBrand);
		out.writeString(mPrice);
		out.writeString(mImgUrl);
		out.writeString(mThumbUrl);
		out.writeString(mModelImgUrl);
		out.writeString(mModelThumbUrl);
		out.writeList(mImgList);
		out.writeString(mVideoList);
		out.writeByte((byte) (mIsSelected ? 1 : 0));
	
    }
    
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

    public JSONObject getJson()
    {
        return json;
    }

}