package com.shuangling.software.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuangling.software.R;
import com.shuangling.software.entity.Column;

public class LinkWebViewFragment extends Fragment implements Handler.Callback  {

    private Column mColumn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LinkWebViewFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mColumn = (Column) args.getSerializable("Column");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ink_web_view, container, false);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}