package com.kelong.androidnative;

import com.kelong.ui.PageDots;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.main_root);
		
	}
}
