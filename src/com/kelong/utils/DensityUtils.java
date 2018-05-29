package com.kelong.utils;

import android.content.Context;

/**
 * 常用单位转换的辅助类 http://www.open-open.com/lib/view/open1392184864254.html
 * http://blog.csdn.net/feng88724/article/details/6599821
 * 
 * @author zill
 * @version 4.0
 * @created 2016-3-10
 */
public class DensityUtils {

	private DensityUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dpVal
	 * @return
	 */
	public static int dp2px(Context context, float dpVal) {
		// return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// dpVal, context.getResources().getDisplayMetrics());
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpVal * scale + 0.5f);
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param spVal
	 * @return
	 */
	public static int sp2px(Context context, float spVal) {
		// return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
		// spVal, context.getResources().getDisplayMetrics());
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spVal * fontScale + 0.5f);
	}

	/**
	 * px转dp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2dp(Context context, float pxVal) {
		// final float scale =
		// context.getResources().getDisplayMetrics().density;
		// return (pxVal / scale);
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxVal / scale + 0.5f);
	}

	/**
	 * px转sp
	 * 
	 * @param context
	 * @param pxVal
	 * @return
	 */
	public static float px2sp(Context context, float pxVal) {
		// return (pxVal /
		// context.getResources().getDisplayMetrics().scaledDensity);
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxVal / fontScale + 0.5f);
	}

}
