package com.kangjiu.xu.view;

import com.kangjiu.xu.kjoa.ErpMain;
import com.xu.kangjiu.kjoa.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


public class ErpPage2 extends Fragment implements OnClickListener{
	ErpMain ma;
	public ErpPage2(ErpMain ma) {
		this.ma = ma;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.erppage2, null);
		return view;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	

}
