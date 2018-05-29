package com.kelong.androidnative;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import com.kelong.file.SecuritySharedPreference;
import com.kelong.ui.PageDots;
import com.kelong.utils.DensityUtils;

public class GuideActivity extends Activity {

    private RelativeLayout mRoot;
    private PageDots points;
    /**
     * 功能引导页
     */
    private ViewPager mVpGuide;
    /**
     * 功能引导页展示的 ImageView 集合
     */
    private List<ImageView> mImageList;
    private Button mBtnStart;
    /**
     * 功能引导页展示的图片集合
     */
    private static int[] mImageIds = new int[]{R.drawable.guide1,
            R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
    	mRoot = (RelativeLayout) findViewById(R.id.guide_root);
        mVpGuide = (ViewPager) findViewById(R.id.vp_guide);
        mBtnStart = (Button) findViewById(R.id.btn_start);

        mImageList = new ArrayList<>();
        // 将要展示的 3 张图片存入 ImageView 集合中
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView image = new ImageView(this);
            // 将图片设置给对应的 ImageView
            image.setBackgroundResource(mImageIds[i]);

            mImageList.add(image);
        }

        mVpGuide.setAdapter(new GuideAdapter());
        mVpGuide.addOnPageChangeListener(new GuidePageChangeListener());

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按钮一旦被点击，更新 SharedPreferences
                SecuritySharedPreference securitySharedPreference = new SecuritySharedPreference(getApplicationContext(), "security_prefs", Context.MODE_PRIVATE);
                SecuritySharedPreference.SecurityEditor securityEditor = securitySharedPreference.edit();
                securityEditor.putBoolean("ignoreGuide", true);
                securityEditor.apply();
                // 跳转到主页面
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        });
        initDots();
    }
    
    private void initDots() {
    	points = new PageDots(this, mImageIds.length);
		mRoot.addView(points); //先添加到视图
		RelativeLayout.LayoutParams params = (LayoutParams) points.getLayoutParams();
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.bottomMargin = DensityUtils.dp2px(this, 20);
		points.setLayoutParams(params);
    }

    /**
     * 适配器
     */
    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageList.get(position));
            return mImageList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageList.get(position));
        }
    }

    /**
     * 滑动监听
     */
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        // 页面滑动时回调此方法
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        	points.setRedPointLeftMargin(position, positionOffset, positionOffsetPixels);
        }

        // 某个页面被选中时回调此方法
        @Override
        public void onPageSelected(int position) {
            // 如果是最后一个页面，按钮可见，否则不可见
            if (position == mImageIds.length - 1) {
                mBtnStart.setVisibility(View.VISIBLE);
            } else {
                mBtnStart.setVisibility(View.INVISIBLE);
            }
        }
    }
}