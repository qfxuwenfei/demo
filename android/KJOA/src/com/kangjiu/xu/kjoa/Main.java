package com.kangjiu.xu.kjoa;

import java.util.ArrayList;

import com.kangjiu.xu.set.ImmersedStatusbarUtils;
import com.kangjiu.xu.set.Pubset;
import com.kangjiu.xu.tool.Anim;
import com.kangjiu.xu.view.ErpPage1;
import com.kangjiu.xu.view.ErpPage2;
import com.kangjiu.xu.view.ErpPage3;
import com.kangjiu.xu.view.ErpPage4;
import com.kangjiu.xu.view.MainPage1;
import com.kangjiu.xu.view.MainPage2;
import com.kangjiu.xu.view.MainPage3;
import com.kangjiu.xu.view.MainPage4;
import com.kangjiu.xu.view.MainPage5;
import com.xu.kangjiu.kjoa.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements View.OnClickListener{
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	FragmentManager fragmentManager = getFragmentManager();
	private int page_index = -1;
	private ArrayList<RelativeLayout> bottoms = new ArrayList<RelativeLayout>();
	private ArrayList<ImageView> ivs = new ArrayList<ImageView>();
	private ArrayList<TextView> tvs = new ArrayList<TextView>();
	private ArrayList<Integer> bts = new ArrayList<Integer>();
	private int sel_color;
	private int no_sel_color;
	private ArrayList<Integer> bt = new ArrayList<Integer>();
	
	private TextView main_tex_company_name;
	private TextView main_txt_user_name;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title bar 即隐藏标题栏
	setContentView(R.layout.main);
	View topView = findViewById(R.id.lin);
	ImmersedStatusbarUtils.initAfterSetContentView(this, topView);
init_ui();
go_page(0);
init_data();
}



private void init_data() {
	main_tex_company_name.setText(Pubset.user_company_name);
	main_txt_user_name.setText(Pubset.user_real_name);
}
//初始化ui
private void init_ui() {

	
	Fragment f1 = new MainPage1();
	Fragment f2 = new MainPage2();
	Fragment f3 = new MainPage3();
	Fragment f4 = new MainPage4();
	Fragment f5 = new MainPage5();
	fragments.add(f1);
	fragments.add(f2);
	fragments.add(f3);
	fragments.add(f4);
	fragments.add(f5);
	
	RelativeLayout p1 = (RelativeLayout) findViewById(R.id.main_ly_bt1);
	RelativeLayout p2 = (RelativeLayout) findViewById(R.id.main_ly_bt2);
	RelativeLayout p3 = (RelativeLayout) findViewById(R.id.main_ly_bt3);
	RelativeLayout p4 = (RelativeLayout) findViewById(R.id.main_ly_bt4);
	RelativeLayout p5 = (RelativeLayout) findViewById(R.id.main_ly_bt5);
	p1.setOnClickListener((android.view.View.OnClickListener) this);
	p2.setOnClickListener((android.view.View.OnClickListener) this);
	p3.setOnClickListener((android.view.View.OnClickListener) this);
	p4.setOnClickListener((android.view.View.OnClickListener) this);
	p5.setOnClickListener((android.view.View.OnClickListener) this);

	bottoms.add(p1);
	bottoms.add(p2);
	bottoms.add(p3);
	bottoms.add(p4);
	bottoms.add(p5);
	
	ImageView im1 = (ImageView) findViewById(R.id.main_img_bt1);
	ImageView im2 = (ImageView) findViewById(R.id.main_img_bt2);
	ImageView im3 = (ImageView) findViewById(R.id.main_img_bt3);
	ImageView im4 = (ImageView) findViewById(R.id.main_img_bt4);
	ImageView im5 = (ImageView) findViewById(R.id.main_img_bt5);
	ivs.add(im1);
	ivs.add(im2);
	ivs.add(im3);
	ivs.add(im4);
	ivs.add(im5);

	TextView tv1 = (TextView) findViewById(R.id.main_txt_bt1);
	TextView tv2 = (TextView) findViewById(R.id.main_txt_bt2);
	TextView tv3 = (TextView) findViewById(R.id.main_txt_bt3);
	TextView tv4 = (TextView) findViewById(R.id.main_txt_bt4);
	TextView tv5 = (TextView) findViewById(R.id.main_txt_bt5);
	tvs.add(tv1);
	tvs.add(tv2);
	tvs.add(tv3);
	tvs.add(tv4);
	tvs.add(tv5);

	sel_color = getResources().getColor(R.color.active);
	no_sel_color = getResources().getColor(R.color.noactive);

	bt.add(R.drawable.home1);
	bt.add(R.drawable.xinxi1);
	bt.add(R.drawable.caiwu1);
	bt.add(R.drawable.tongxun1);
	bt.add(R.drawable.wode1);

	bts.add(R.drawable.home2);
	bts.add(R.drawable.xinxi2);
	bts.add(R.drawable.caiwu2);
	bts.add(R.drawable.tongxun2);
	bts.add(R.drawable.wode2);
	
	main_tex_company_name=(TextView)findViewById(R.id.main_tex_company_name);
	
	main_txt_user_name=(TextView)findViewById(R.id.main_txt_user_name);
}


private void set_color(int index) {
	for (int i = 0; i < 5; i++) {
		ivs.get(i).setImageResource(bts.get(i));
		tvs.get(i).setTextColor(no_sel_color);
	}
	ivs.get(index).setImageResource(bt.get(index));
	tvs.get(index).setTextColor(sel_color);

}

public void go_page(int index) {

	if (index != page_index) {
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (page_index > -1) {
			ft.hide(fragments.get(page_index));
		}
		if (!fragments.get(index).isAdded()) {
			ft.add(R.id.main_frame, fragments.get(index));
		}
		ft.show(fragments.get(index)).commit();
		set_color(index);
		page_index = index;
		Anim anim=new Anim(getApplication());
		anim.toggle(bottoms.get(page_index),bottoms.get(page_index));
		
	}

}


@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.main_ly_bt1:
		go_page(0);
		Toast.makeText(Main.this, "bt1", Toast.LENGTH_LONG).show();
		break;
	case R.id.main_ly_bt2:
		Toast.makeText(Main.this, "bt2", Toast.LENGTH_LONG).show();
		go_page(1);
		break;
	case R.id.main_ly_bt3:
		Toast.makeText(Main.this, "bt3", Toast.LENGTH_LONG).show();
		go_page(2);
		break;
	case R.id.main_ly_bt4:
		Toast.makeText(Main.this, "bt4", Toast.LENGTH_LONG).show();
		go_page(3);
		break;
	case R.id.main_ly_bt5:
		go_page(4);
		Toast.makeText(Main.this, "bt5", Toast.LENGTH_LONG).show();
		break;
	default:
		break;
	}
}


@Override
public void onBackPressed() { 
    new AlertDialog.Builder(this).setTitle("确认康久OA退出吗？") 
        .setIcon(android.R.drawable.ic_dialog_info) 
        .setPositiveButton("退出", new DialogInterface.OnClickListener() { 
     
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            // 点击“确认”后的操作 
                Main.this.finish(); 
     
            } 
        }) 
        .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
     
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            // 点击“返回”后的操作,这里不设置没有任何操作 
            } 
        }).show(); 
       }
}
