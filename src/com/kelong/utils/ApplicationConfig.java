package com.kelong.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.kelong.androidnative.R;

public class ApplicationConfig {
	public static Context context;
	//数据库
	public static String dbPath = null;
	public final static String dbName = "data.db";

	//导航页
	public static int[] getGuideImages() {
		TypedArray ar = context.getResources().obtainTypedArray(R.array.splash_images);
		int len = ar.length();
		int[] resIds = new int[len];
		for (int i = 0; i < len; i++)
			resIds[i] = ar.getResourceId(i, 0);

		ar.recycle();
		return resIds;
	}
}
