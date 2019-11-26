package com.kangjiu.xu.kjoa;

import java.util.ArrayList;

import com.kangjiu.xu.set.ImmersedStatusbarUtils;
import com.kangjiu.xu.tool.Anim;
import com.kangjiu.xu.view.ErpPage1;
import com.kangjiu.xu.view.ErpPage2;
import com.kangjiu.xu.view.ErpPage3;
import com.kangjiu.xu.view.ErpPage4;
import com.xu.kangjiu.kjoa.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ErpMain extends Activity implements OnClickListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// remove title bar ¼´Òþ²Ø±êÌâÀ¸

		setContentView(R.layout.erp);
		View topView = findViewById(R.id.lin);
		ImmersedStatusbarUtils.initAfterSetContentView(this, topView);

		init_view();
		go_page(0);
	}

	private void init_view() {
		Fragment f1 = new ErpPage1(this);
		Fragment f2 = new ErpPage2(this);
		Fragment f3 = new ErpPage3(this);
		Fragment f4 = new ErpPage4(this);
		fragments.add(f1);
		fragments.add(f2);
		fragments.add(f3);
		fragments.add(f4);

		RelativeLayout p1 = (RelativeLayout) findViewById(R.id.layout_p1);
		RelativeLayout p2 = (RelativeLayout) findViewById(R.id.layout_p2);
		RelativeLayout p3 = (RelativeLayout) findViewById(R.id.layout_p3);
		RelativeLayout p4 = (RelativeLayout) findViewById(R.id.layout_p4);
		p1.setOnClickListener((android.view.View.OnClickListener) this);
		p2.setOnClickListener((android.view.View.OnClickListener) this);
		p3.setOnClickListener((android.view.View.OnClickListener) this);
		p4.setOnClickListener((android.view.View.OnClickListener) this);

		bottoms.add(p1);
		bottoms.add(p2);
		bottoms.add(p3);
		bottoms.add(p4);

		ImageView im1 = (ImageView) findViewById(R.id.img_1);
		ImageView im2 = (ImageView) findViewById(R.id.img_2);
		ImageView im3 = (ImageView) findViewById(R.id.img_3);
		ImageView im4 = (ImageView) findViewById(R.id.img_4);
		ivs.add(im1);
		ivs.add(im2);
		ivs.add(im3);
		ivs.add(im4);

		TextView tv1 = (TextView) findViewById(R.id.tv_1);
		TextView tv2 = (TextView) findViewById(R.id.tv_2);
		TextView tv3 = (TextView) findViewById(R.id.tv_3);
		TextView tv4 = (TextView) findViewById(R.id.tv_4);
		tvs.add(tv1);
		tvs.add(tv2);
		tvs.add(tv3);
		tvs.add(tv4);

		sel_color = getResources().getColor(R.color.active);
		no_sel_color = getResources().getColor(R.color.noactive);

		bts.add(R.drawable.home);
		bts.add(R.drawable.list);
		bts.add(R.drawable.print);
		bts.add(R.drawable.my);

		bt.add(R.drawable.home_g);
		bt.add(R.drawable.list_g);
		bt.add(R.drawable.print_g);
		bt.add(R.drawable.my_g);
		
		
	}

	public void go_page(int index) {

		if (index != page_index) {

			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			if (page_index > -1) {
				ft.hide(fragments.get(page_index));
			}
			if (!fragments.get(index).isAdded()) {
				ft.add(R.id.frame, fragments.get(index));
			}
			ft.show(fragments.get(index)).commit();
			set_color(index);
			page_index = index;
			Anim anim=new Anim(getApplication());
			anim.toggle(bottoms.get(page_index),bottoms.get(page_index));
		}

	}

	private void set_color(int index) {
		for (int i = 0; i < 4; i++) {
			ivs.get(i).setImageResource(bts.get(i));
			tvs.get(i).setTextColor(no_sel_color);
		}
		ivs.get(index).setImageResource(bt.get(index));
		tvs.get(index).setTextColor(sel_color);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.layout_p1:

			go_page(0);
			break;
		case R.id.layout_p2:

			go_page(1);
			break;

		case R.id.layout_p3:

			go_page(2);
			break;
		case R.id.layout_p4:

			go_page(3);
			break;


		default:
			break;
		}

	}
}
