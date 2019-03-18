package com.kelong.ui;

import com.kelong.androidnative.R;
import com.kelong.utils.DensityUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PageDots extends RelativeLayout {

	private RelativeLayout root;
	private LinearLayout lly;
	private View red_dot;
	private Context mContext;
	/**
     * 相邻小灰点之间的距离
     */
    private int mDotDistance;

	public PageDots(Context context, int count) {
		super(context);
		mContext = context;
		((Activity) getContext()).getLayoutInflater().inflate(
				R.layout.page_dot, this);
		root = findViewById(R.id.page_dot_root);
		lly = findViewById(R.id.page_dot_ly);
		setDots(count);
		
		// 获取控件树，对 layout 结束事件进行监听
		lly.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // OnGlobalLayoutListener可能会被多次触发，
                        // 因此在得到了高度之后，要将 OnGlobalLayoutListener 注销掉
                    	lly.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // 计算小灰点之间的距离
                        mDotDistance = lly.getChildAt(1).getLeft() - lly.getChildAt(0).getLeft();
                        // 获取小红点的布局参数
                        RelativeLayout.LayoutParams params = (LayoutParams) red_dot.getLayoutParams();
                        // 修改小红点的左边缘和父控件(RelativeLayout)左边缘的距离
                        params.leftMargin = lly.getChildAt(0).getLeft();
                        // 修改小红点的布局参数
                        red_dot.setLayoutParams(params);
                    }
                });
	}

	private void setDots(int num) {
		for (int i = 0; i < num; i++) {
			View dot = new View(mContext);
			lly.addView(dot);//, mParams);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dot.getLayoutParams();
			layoutParams.width = DensityUtils.dp2px(mContext, 10);
			layoutParams.height = DensityUtils.dp2px(mContext, 10);
			if (i > 0) {				
				layoutParams.leftMargin = DensityUtils.dp2px(mContext, 10);
			}
			dot.setBackgroundResource(R.drawable.shape_dot_gray);
		}
		red_dot = new View(mContext);
		root.addView(red_dot);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) red_dot.getLayoutParams();
		layoutParams.width = DensityUtils.dp2px(mContext, 10);
		layoutParams.height = DensityUtils.dp2px(mContext, 10);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		red_dot.setBackgroundResource(R.drawable.shape_dot_red);
	}
	
	public void setRedPointLeftMargin(int position, float positionOffset, int positionOffsetPixels) {
		// 页面滑动过程中，小红点移动的距离
        int distance = (int) (mDotDistance * (positionOffset + position));
        // 获取小红点的布局参数
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) red_dot.getLayoutParams();
        // 修改小红点的左边缘和父控件(RelativeLayout)左边缘的距离
        params.leftMargin = distance;
        // 修改小红点的布局参数
        red_dot.setLayoutParams(params);
	}
}
