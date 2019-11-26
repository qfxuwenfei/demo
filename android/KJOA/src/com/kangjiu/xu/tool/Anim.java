package com.kangjiu.xu.tool;

import com.kangjiu.xu.kjoa.Login;
import com.xu.kangjiu.kjoa.R;

import android.app.Application;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class Anim {
Application app;
	public Anim(Application app) {
		this.app=app;
	}
	public void toggle( final View obj,final View obj1) {
		 Animation animation = AnimationUtils.loadAnimation( app.getBaseContext(),R.anim.an_out);
		 animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				obj.setVisibility(View.INVISIBLE);
				
				obj1.setVisibility(View.VISIBLE);
				Animation animation1 = AnimationUtils.loadAnimation(app.getBaseContext(),R.anim.an_in);
				obj1.startAnimation(animation1);
			}
		});
		 
		 obj.startAnimation(animation);
		
	}
}
