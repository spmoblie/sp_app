package com.spshop.stylistpark.utils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sorter {
	
	private static final String TAG = "Sorter";
	private static final int MAX_STROKE = 25;

	private static SparseIntArray strokeCache = new SparseIntArray();

//	private static final boolean SORT_ORDER_BY_LANG = false;

	public static interface SortValue<T> {
		public String getStringForSort(T obj);
	}

	public static SparseArray<String> sortArrayWithStrokeOrder(List<String> list, Context ctx) {
		return sortArrayWithStrokeOrder(list, new SortValue<String>() {
			@Override
			public String getStringForSort(String obj) {
				// To change body of implemented methods use File | Settings |
				// File Templates.
				return obj;

			}
		}, ctx);
	}

	public static SparseArray<String> sortArrayWithStrokeOrder(List list,
			final SortValue sortValue, Context ctx) {
		ArrayList[] objectInStrokeOrder = new ArrayList[MAX_STROKE + 2];
		SparseArray<String> indexNameMap = new SparseArray<String>();

		for (int i = 0; i <= MAX_STROKE + 1; i++) {
			objectInStrokeOrder[i] = new ArrayList();
		}
		try {
			for (Object o : list) {
				int stroke = getStroke(sortValue.getStringForSort(o));
				if (stroke != -1) {
					objectInStrokeOrder[stroke].add(o);
				}
			}

			list.clear();

//			boolean shouldPutEnglishFirst = !SORT_ORDER_BY_LANG|| Tools.getIsEng();
			boolean shouldPutEnglishFirst=true;
			
			if (shouldPutEnglishFirst) {
				sortEnglishList(objectInStrokeOrder[0], indexNameMap, 0,
						sortValue);
				list.addAll(objectInStrokeOrder[0]);
			}

			for (int i = 1; i <= MAX_STROKE + 1; i++) {

				if (!shouldPutEnglishFirst && i > MAX_STROKE) {
					sortEnglishList(objectInStrokeOrder[0], indexNameMap,
							list.size(), sortValue);
					list.addAll(objectInStrokeOrder[0]);
				}

				indexNameMap.put(list.size(), IndexDisplayTool.getStrokeString(i, ctx));
				ArrayList l = objectInStrokeOrder[i];

				Collections.sort(l, new Comparator<Object>() {

					@Override
					public int compare(Object exit1, Object exit2) {
						String name1 = sortValue.getStringForSort(exit1);
						String name2 = sortValue.getStringForSort(exit2);

						int firstCharDiff = name1.substring(0, 1)
								.compareToIgnoreCase(name2.substring(0, 1));
						if (firstCharDiff != 0)
							return firstCharDiff;

						try {
							int count = Math.min(name1.length(), name2.length());
							for (int i = 1; i < count; i++) {
								String c1 = name1.substring(i, i + 1);
								String c2 = name2.substring(i, i + 1);
								int d = getStroke(c1) - getStroke(c2);
								if (d != 0)
									return d;

								d = c1.compareToIgnoreCase(c2);
								if (d != 0)
									return d;
							}
						} catch (Exception ex) {
							Log.e(TAG, TAG, ex);
						}

						return name1.compareToIgnoreCase(name2);
					}
				});

				list.addAll(l);
			}
		} catch (Exception ex) {
			Log.e(TAG, TAG, ex);
		} finally {
			strokeCache.clear();
		}

		return indexNameMap;
	}

	public static SparseArray<String> updateStrokeOrderIndex(List list,
			final SortValue sortValue, Context ctx) {
		ArrayList[] objectInStrokeOrder = new ArrayList[MAX_STROKE + 2];
		SparseArray<String> indexNameMap = new SparseArray<String>();

		for (int i = 0; i <= MAX_STROKE + 1; i++) {
			objectInStrokeOrder[i] = new ArrayList();
		}
		try {
			for (Object o : list) {
				objectInStrokeOrder[getStroke(sortValue.getStringForSort(o))]
						.add(o);
			}

			int count = 0;

//			boolean shouldPutEnglishFirst = !SORT_ORDER_BY_LANG|| Tools.getIsEng();
			boolean shouldPutEnglishFirst=true;
			
			if (shouldPutEnglishFirst) {
				getEnglishListIndex(objectInStrokeOrder[0], indexNameMap, 0,
						sortValue);
				count += objectInStrokeOrder[0].size();
			}

			for (int i = 1; i <= MAX_STROKE + 1; i++) {

				if (!shouldPutEnglishFirst && i > MAX_STROKE) {
					getEnglishListIndex(objectInStrokeOrder[0], indexNameMap,
							count, sortValue);
					count += objectInStrokeOrder[0].size();
				}

				indexNameMap.put(count, IndexDisplayTool.getStrokeString(i, ctx));
				count += objectInStrokeOrder[i].size();
			}
		} catch (Exception ex) {
			Log.e(TAG, TAG, ex);
		} finally {
			strokeCache.clear();
		}

		return indexNameMap;
	}

	private static void sortEnglishList(ArrayList enList,
			SparseArray<String> map, int offset, final SortValue sortValue) {
		Collections.sort(enList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return sortValue.getStringForSort(o1).compareToIgnoreCase(
						sortValue.getStringForSort(o2));
			}
		});

		getEnglishListIndex(enList, map, offset, sortValue);
	}

	private static void getEnglishListIndex(ArrayList list,
			SparseArray<String> map, int offset, SortValue sortValue) {
		int i = offset;
		SparseBooleanArray cache = new SparseBooleanArray();

		for (Object o : list) {
			char firstChar = Character.toUpperCase(sortValue
					.getStringForSort(o).charAt(0));
			if (cache.indexOfKey(firstChar) < 0) {
				map.put(i, Character.toString(firstChar));
				cache.put(firstChar, true);
			}
			i++;
		}
	}

	private static int getStroke(String string) {
		int stroke = 0;
		if (string == null || string.isEmpty()) {
			return -1;
		}

		char c = string.charAt(0);

		if (strokeCache.indexOfKey(c) >= 0){
			return strokeCache.get(c);
		}
		try {
			stroke = Math.min(IndexDisplayTool.getStroke(string), MAX_STROKE);
			if (stroke == 0 && !Character.isLetter(c)){
				stroke = MAX_STROKE + 1;
			}
			strokeCache.put(c, stroke);
		} catch (Exception ex) {
			Log.e(TAG, TAG, ex);
		}
		return stroke;
	}
}
