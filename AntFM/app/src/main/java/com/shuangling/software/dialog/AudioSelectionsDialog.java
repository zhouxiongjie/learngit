package com.shuangling.software.dialog;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.RadioProgramListAdapter;
import com.shuangling.software.adapter.SelectionsGridViewAdapter;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.RadioDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class AudioSelectionsDialog extends BaseCircleDialog implements View.OnClickListener {


    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.selectionsLayout)
    LinearLayout selectionsLayout;
    Unbinder unbinder;


    public interface IselectAudio{
        public void onSelectedAudio(List<Integer> selected);
    }

    private SelectionsGridViewAdapter mSelectionsGridViewAdapter;
    private int mSelectionsPosition=-1;
    private List<AudioInfo> mAudios;
    private int mOrder;
    private IselectAudio mSelectAudio;
    private List<Integer> mSelect;
    public void setSelectAudio(IselectAudio selectAudio) {
        this.mSelectAudio = selectAudio;
    }

    public static AudioSelectionsDialog getInstance(List<AudioInfo> audios, int order,List<Integer> select) {
        AudioSelectionsDialog dialogFragment = new AudioSelectionsDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.mAudios=audios;
        dialogFragment.mOrder=order;
        dialogFragment.mSelect=select;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_audio_selections, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        unbinder = ButterKnife.bind(this, view);

        mSelectionsGridViewAdapter = new SelectionsGridViewAdapter(getActivity(), mAudios.size(),mOrder,mSelect);
        gridView.setAdapter(mSelectionsGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mSelectAudio!=null){
                    mSelectAudio.onSelectedAudio(mSelectionsGridViewAdapter.getItem(position));
                }
                dismiss();

            }
        });
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;

        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


}
