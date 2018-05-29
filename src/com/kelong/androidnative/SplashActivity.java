package com.kelong.androidnative;

import com.kelong.file.SecuritySharedPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {
	
	private RelativeLayout rlSplash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
		startAnim();
	}
	
	 private void startAnim() {
	        // 渐变动画,从完全透明到完全不透明
	        AlphaAnimation alpha = new AlphaAnimation(0, 1);
	        // 持续时间 2 秒
	        alpha.setDuration(3000);
	        // 动画结束后，保持动画状态
	        alpha.setFillAfter(true);

	        // 设置动画监听器
	        alpha.setAnimationListener(new Animation.AnimationListener() {
	            @Override
	            public void onAnimationStart(Animation animation) {
	            }
	            @Override
	            public void onAnimationRepeat(Animation animation) {
	            }
	            // 动画结束时回调此方法
	            @Override
	            public void onAnimationEnd(Animation animation) {
	                // 跳转到下一个页面
	                jumpNextPage();
	            }
	        });

	        // 启动动画
	        rlSplash.startAnimation(alpha);
	    }

	    /**
	     * 跳转到下一个页面
	     */
	    private void jumpNextPage() {
	    	SecuritySharedPreference securitySharedPreference = new SecuritySharedPreference(getApplicationContext(), "security_prefs", Context.MODE_PRIVATE);
	    	boolean ignoreGuide = securitySharedPreference.getBoolean("ignoreGuide", false);
	        if(ignoreGuide) {
	        	startActivity(new Intent(SplashActivity.this, MainActivity.class));
	        } else {
		    	startActivity(new Intent(SplashActivity.this, GuideActivity.class));
	        }
	        finish();
	    }
}
