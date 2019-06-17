package com.shuangling.software.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shuangling.software.R;
import com.shuangling.software.activity.RadioListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DiscoverFragment extends Fragment implements OnClickListener {


    @BindView(R.id.activity)
    RelativeLayout activity;
    @BindView(R.id.live)
    RelativeLayout live;
    @BindView(R.id.classHelper)
    RelativeLayout classHelper;
    Unbinder unbinder;
    @BindView(R.id.top)
    RelativeLayout top;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descover, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.activity, R.id.live, R.id.classHelper})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.activity:
                break;
            case R.id.live:{
                    startActivity(new Intent(getContext(),RadioListActivity.class));
                }
                break;
            case R.id.classHelper:
                break;
        }
    }
}
