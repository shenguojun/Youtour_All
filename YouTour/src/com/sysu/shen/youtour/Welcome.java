package com.sysu.shen.youtour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		ImageView logo = (ImageView) findViewById(R.id.fengqilogo);
		Animation logo_animation = AnimationUtils.loadAnimation(Welcome.this,
				R.anim.push_left_in);
		logo.setAnimation(logo_animation);
		logo_animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent it = new Intent(Welcome.this, MainActivity.class);
				startActivity(it);
				Welcome.this.finish();
				// overridePendingTransition(R.anim.slide_out_left,
				// R.anim.slide_in_right);
			}
		});
	}
}
