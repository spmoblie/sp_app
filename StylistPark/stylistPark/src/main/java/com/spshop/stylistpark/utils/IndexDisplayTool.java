package com.spshop.stylistpark.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.IndexDisplay;

public class IndexDisplayTool {
	
	final static String TAG="IndexDisplayTool";
	
	
	/*
	 * return a list with empty index header. e.g. Pair{ "":[Abv,Agg,Aee] ,
	 * 													 "":[Bcc,Buu] }
	 */
	public static List<Pair<String, List<? extends IndexDisplay>>> buildIndexListEmptyHeader(List<? extends IndexDisplay> dataList){
		return buildIndexList(dataList,true);
	}
	
	public static List<Pair<String, List<? extends IndexDisplay>>> buildIndexList(List<? extends IndexDisplay> dataList){
		return buildIndexList(dataList,false);
	}
	
	//for sorted data
	private static List<Pair<String, List<? extends IndexDisplay>>> buildIndexList(List<? extends IndexDisplay> dataList,boolean emptyHeader){
		List<Pair<String, List<? extends IndexDisplay>>> result = new ArrayList<Pair<String, List<? extends IndexDisplay>>>();
		
		Pair<String, List<? extends IndexDisplay>> pair2Add=null;
	
		List<IndexDisplay> serachDisplay2Add=null;
		String lastIndex="";
		String curIndex="";
		try{
			
			for(int i=0;i<dataList.size();i++){

				IndexDisplay tmpRoute=(IndexDisplay)dataList.get(i);
				curIndex=tmpRoute.getFirstCharIndex();
				
				if(!curIndex.equalsIgnoreCase(lastIndex)){

					
					serachDisplay2Add=new ArrayList<IndexDisplay>();
					serachDisplay2Add.add(tmpRoute);
					
					if(emptyHeader){
						pair2Add=new Pair<String, List<? extends IndexDisplay>>("",serachDisplay2Add);
					}else{
						pair2Add=new Pair<String, List<? extends IndexDisplay>>(curIndex,serachDisplay2Add);
					}
					
					result.add(pair2Add);
				}else{
					serachDisplay2Add.add(tmpRoute);

				}
				lastIndex=curIndex;

			}
			
		}catch(Exception e){
			e.printStackTrace();
			result=null;
		}
		
		return result;

	}
	
	/*
	 * return a list with empty index header. e.g. Pair{ "":[Abv,Agg,Aee] ,
	 * 													 "":[Bcc,Buu] }
	 */
	public static ArrayList<Pair<String, List<? extends IndexDisplay>>> buildIndexListChineseAndEngEmptyHeader(Context context,List<? extends IndexDisplay> dataList){
		return buildIndexListChineseAndEng(context,dataList,true);
	}
	public static ArrayList<Pair<String, List<? extends IndexDisplay>>> buildIndexListChineseAndEng(Context context,List<? extends IndexDisplay> dataList){
		return buildIndexListChineseAndEng(context,dataList,false);
	}
	private static ArrayList<Pair<String, List<? extends IndexDisplay>>> buildIndexListChineseAndEng(Context context,List<? extends IndexDisplay> dataList,boolean emptyHeader) {
		ArrayList<Pair<String, List<? extends IndexDisplay>>> result = new ArrayList<Pair<String, List<? extends IndexDisplay>>>();

		//ArrayList<StationDAO> stationList = getStationList(keyword);
		try{
			SparseArray<String> index = Sorter.sortArrayWithStrokeOrder(
					dataList, new Sorter.SortValue<IndexDisplay>() {
						@Override
						public String getStringForSort(IndexDisplay obj) {
							
								return obj.getValueToBeSort();
					
						}
					}, context);
	
			int i = 0;
			ArrayList<IndexDisplay> l = null;
			String strokeString;
			for (IndexDisplay s : dataList) {
				if (index.indexOfKey(i) >= 0) {
					l = new ArrayList<IndexDisplay>();
					strokeString = index.get(i);
					if(emptyHeader){
						result.add(new Pair<String, List<? extends IndexDisplay>>("", l));
					}else{
						result.add(new Pair<String, List<? extends IndexDisplay>>(strokeString, l));
					}
					
				}
				l.add(s);
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
			result=null;
		}

		return result;

	}
	
	public static List<IndexDisplay> extractNumItem(List<? extends IndexDisplay> dataList){
		 List<IndexDisplay> result=new  ArrayList<IndexDisplay>();
		 
		 for(int i=0; i<dataList.size();i++){
			 IndexDisplay tmp=dataList.get(i);
			 if(isInteger(tmp.getFirstCharIndex())){
				 result.add(tmp);
				 dataList.remove(tmp);
				 i--;
			 }
			 
		 }
		 
		 return result;
		 
	}
	
	public static void  removeDuplicate(List<? extends IndexDisplay>... lists){
		if(lists==null || lists.length<0)
			return;
		
		List<? extends IndexDisplay> base=null;
		List<List<? extends IndexDisplay>> compareList=new ArrayList<List<? extends IndexDisplay>>();
			
		for(int i=0;i<lists.length;i++){
			
			List<? extends IndexDisplay> tmp=lists[i];
			if(tmp==null)
				continue;
			
			if(base==null){
				base=lists[i];
			}else{
				if(lists[i].size()>base.size()){
					compareList.add(base);
					base=lists[i];
				}else{
					compareList.add(lists[i]);
				}
			}
			
		}
		
		for(int i=0; i<base.size();i++){
			
			IndexDisplay cur=base.get(i);
			
			for(int j=0;j<compareList.size();j++){
				
				List<? extends IndexDisplay> tmpList=compareList.get(j);
				
				for(int k=0;k<tmpList.size();k++){
					IndexDisplay tar=tmpList.get(k);
					if(cur.getValueToBeSort().equalsIgnoreCase(tar.getValueToBeSort())){
						tmpList.remove(tar);
						k--;
					}
				}
			}
			
		}
		
	}
	

	
	public static int getStroke(String utfString)
			throws UnsupportedEncodingException {
		int strokes = 0;
		if (utfString != null) {
			if (utfString.length() > 0) {
				String firstChar = utfString.substring(0, 1);
				// unicode 轉成 Big5 編碼

				byte[] big5 = firstChar.getBytes("Big5-HKSCS");
				strokes = TraditionalChineseStrokeLookup(big5);
			}
		}
		return strokes;
	}

	private static int TraditionalChineseStrokeLookup(byte[] bytes) {
		int result = 0;
		int b1, b2;
		if (bytes.length < 2) {
			b1 = (int) bytes[0] & 0xFF;
			b2 = (int) 0 & 0xFF;
		} else {
			b1 = (int) bytes[0] & 0xFF;
			b2 = (int) bytes[1] & 0xFF;
		}

		int b = (b1 << 8) + b2;

		if ((b == 0xA440) || (b == 0xA441)) {
			result = 1;
		} else if (((b >= 0xA442) && (b <= 0xA453))
				|| ((b >= 0xC940) && (b <= 0xC944))) {
			result = 2;
		} else if (((b >= 0xA454) && (b <= 0xA47E))
				|| ((b >= 0xC945) && (b <= 0xC94C))) {
			result = 3;
		} else if (((b >= 0xA4A1) && (b <= 0xA4FD))
				|| ((b >= 0xC94D) && (b <= 0xC962))) {
			result = 4;
		} else if (((b >= 0xA4FE) && (b <= 0xA5DF))
				|| ((b >= 0xC963) && (b <= 0xC9AA))) {
			result = 5;
		} else if (((b >= 0xA5E0) && (b <= 0xA6E9))
				|| ((b >= 0xC9AB) && (b <= 0xCA59))) {
			result = 6;
		} else if (((b >= 0xA6EA) && (b <= 0xA8C2))
				|| ((b >= 0xCA5A) && (b <= 0xCBB0))) {
			result = 7;
		} else if (((b == 0xA260) || (b >= 0xA8C3) && (b <= 0xAB44))
				|| ((b >= 0xCBB1) && (b <= 0xCDDC))) {
			result = 8;
		} else if ((b == 0xA259) || (b == 0xF9DA)
				|| ((b >= 0xAB45) && (b <= 0xADBB))
				|| ((b >= 0xCDDD) && (b <= 0xD0C7))) {
			result = 9;
		} else if ((b == 0x8F4A) || (b == 0xA25A)
				|| ((b >= 0xADBC) && (b <= 0xB0AD))
				|| ((b >= 0xD0C8) && (b <= 0xD44A)) || (b == 0xFCF8)) {
			result = 10;
		} else if ((b == 0xA25B) || (b == 0xA25C)
				|| ((b >= 0xB0AE) && (b <= 0xB3C2))
				|| ((b >= 0xD44B) && (b <= 0xD850)) || (b == 0xFBA3)) {
			result = 11;
		} else if ((b == 0xF9DB) || ((b >= 0xB3C3) && (b <= 0xB6C2))
				|| ((b >= 0xD851) && (b <= 0xDCB0))) {
			result = 12;
		} else if ((b == 0xA25D) || (b == 0xA25F) || (b == 0xC6A1)
				|| (b == 0xFAC0) || (b == 0xFAEA) || (b == 0xF9D6)
				|| (b == 0xF9D8) || ((b >= 0xB6C3) && (b <= 0xB9AB))
				|| ((b >= 0xDCB1) && (b <= 0xE0EF))) {
			result = 13;
		} else if ((b == 0xF9DC) || ((b >= 0xB9AC) && (b <= 0xBBF4))
				|| ((b >= 0xE0F0) && (b <= 0xE4E5))) {
			result = 14;
		} else if ((b == 0xA261) || ((b >= 0xBBF5) && (b <= 0xBEA6))
				|| ((b >= 0xE4E6) && (b <= 0xE8F3))) {
			result = 15;
		} else if ((b == 0xA25E) || (b == 0xF9D7) || (b == 0xF9D9) || (b == 0x9378)
				|| ((b >= 0xBEA7) && (b <= 0xC074))
				|| ((b >= 0xE8F4) && (b <= 0xECB8))) {
			result = 16;
		} else if (((b >= 0xC075) && (b <= 0xC24E))
				|| ((b >= 0xECB9) && (b <= 0xEFB6))) {
			result = 17;
		} else if (((b >= 0xC24F) && (b <= 0xC35E))
				|| ((b >= 0xEFB7) && (b <= 0xF1EA))) {
			result = 18;
		} else if (((b >= 0xC35F) && (b <= 0xC454))
				|| ((b >= 0xF1EB) && (b <= 0xF3FC))) {
			result = 19;
		} else if ((b == 0x916F) || ((b >= 0xC455) && (b <= 0xC4D6))
				|| ((b >= 0xF3FD) && (b <= 0xF5BF))) {
			result = 20;
		} else if (((b >= 0xC4D7) && (b <= 0xC56A))
				|| ((b >= 0xF5C0) && (b <= 0xF6D5))) {
			result = 21;
		} else if (((b >= 0xC56B) && (b <= 0xC5C7))
				|| ((b >= 0xF6D6) && (b <= 0xF7CF))) {
			result = 22;
		} else if (((b >= 0xC5C8) && (b <= 0xC5F0))
				|| ((b >= 0xF7D0) && (b <= 0xF8A4))) {
			result = 23;
		} else if (((b >= 0xC5F1) && (b <= 0xC654))
				|| ((b >= 0xF8A5) && (b <= 0xF8ED))) {
			result = 24;
		} else if (((b >= 0xC655) && (b <= 0xC664))
				|| ((b >= 0xF8EE) && (b <= 0xF96A))
				|| // 25
				((b >= 0xC665) && (b <= 0xC66B))
				|| ((b >= 0xF96B) && (b <= 0xF9A1))
				|| // 26
				((b >= 0xC66C) && (b <= 0xC675))
				|| ((b >= 0xF9A2) && (b <= 0xF9B9))
				|| // 27
				((b >= 0xC676) && (b <= 0xC678))
				|| ((b >= 0xF9BA) && (b <= 0xF9C5))
				|| // 28
				((b >= 0xC679) && (b <= 0xC67C))
				|| ((b >= 0xF9C7) && (b <= 0xF9CB)) || // 29
				(b == 0xC67D) || ((b >= 0xF9CC) && (b <= 0xF9CF)) || // 30
				(b == 0xF9D0) || // 31
				(b == 0xC67E) || (b == 0xF9D1) || // 32
				(b == 0xF9C6) || (b == 0xF9D2) || // 33
				(b == 0xF9D3) || // 35
				(b == 0xF9D4) || // 36
				(b == 0xF9D5) // 48
		) {
			result = 25;
		}
		return result; // return 筆劃數
	}

	public static String getStrokeString(int stroke, Context ctx) {
		if (stroke == 0)
			return "#";

		if (stroke > 0 && stroke < 25)
			return String.format("%d %s", stroke, ctx.getString(R.string.filter_stroke));
		

		if (stroke == 25)
			return String.format("25 %s", ctx.getString(R.string.filter_stroke_more));

		return "#";
	}
	
	
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}
	
	
	public static class IndexDisplayNumberComparator implements Comparator<IndexDisplay> {
		

		public int compare(IndexDisplay obj1, IndexDisplay obj2) {
			int num1, num2;
			try{
				num1=Integer.parseInt(obj1.getFirstCharIndex()); 
				num2=Integer.parseInt(obj2.getFirstCharIndex()); 
				if(num1>num2)
					return 1;
				else if(num1<num2)
					return -1;
				else
					return 0;
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
		
		}
	}
	
	

}
