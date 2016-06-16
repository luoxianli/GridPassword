package com.kayak.gridpasswordview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.kayak.gridpasswordview.GridPasswordView;
import com.kayak.gridpasswordview.demo.R;

/**
 * 
 * @author Luo Xianli
 * 
 */
public class MainActivity extends Activity {

	GridPasswordView view1, view2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		view1 = (GridPasswordView) findViewById(R.id.gpv_normal);
		view2 = (GridPasswordView) findViewById(R.id.gpv_normal2);
		view2.setRandomNum(true);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toast("第一个密码是:" + view1.getPassWord());
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toast("第二个密码是:" + view2.getPassWord());
			}
		});
	}

	public void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
