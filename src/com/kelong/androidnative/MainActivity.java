package com.kelong.androidnative;

import com.kelong.utils.ApplicationConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends Activity {

	long eTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initApplication();

		this.setContentView(R.layout.activity_main);
		
		startActivity(new Intent(MainActivity.this, DBActivity.class));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("kl", keyCode + ":" + KeyEvent.KEYCODE_BACK);
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			Log.d("kl", "in.....xx");
			if(System.currentTimeMillis() - eTime < 2000) {
				Log.d("kl", "in.....yy");
				finish();
			} else {
				Toast.makeText(this, "再点一次退出游戏", Toast.LENGTH_SHORT).show();
				Log.d("kl", "in.....dd");
				eTime = System.currentTimeMillis();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initApplication() {
		if(ApplicationConfig.dbPath == null) {
			ApplicationConfig.dbPath = "data/data/" + this.getPackageName()
					+ "/databases/";
		}
	}

}
