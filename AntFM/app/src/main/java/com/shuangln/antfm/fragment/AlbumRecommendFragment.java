package com.shuangln.antfm.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.shuangln.antfm.R;


public class AlbumRecommendFragment extends Fragment implements OnClickListener {
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {  
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        return view;  

    }



	@Override
	public void onClick(View v) {

		switch (v.getId()) {


		default:
			break;
		}
	}


}
