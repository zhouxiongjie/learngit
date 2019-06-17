package com.shuangling.software.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuangling.software.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AlbumIntroduceFragment extends Fragment {


    @BindView(R.id.introduction)
    TextView introduction;
    Unbinder unbinder;

    private String mIntroduction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mIntroduction=getArguments().getString("introduction");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_introduce, container, false);

        unbinder = ButterKnife.bind(this, view);
        introduction.setText(mIntroduction);
        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
