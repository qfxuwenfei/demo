package com.kangjiu.xu.view;

import com.kangjiu.xu.kjoa.ErpMain;
import com.kangjiu.xu.kjoa.Main;
import com.xu.kangjiu.kjoa.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainPage1 extends Fragment implements View.OnClickListener{

	
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.mainpage1, null);
	return view;
}

@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		init_ui();
	}

private void init_ui() {
	LinearLayout main_ly_erp=(LinearLayout)getActivity().findViewById(R.id.main_ly_erp);
	main_ly_erp.setOnClickListener(this);
}
private void open_erp() {
	Intent intent=new Intent(getActivity(),ErpMain.class);
	
	startActivity(intent);
	getActivity(). overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
}

@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.main_ly_erp:
		open_erp();
		break;

	default:
		break;
	}
}
}
