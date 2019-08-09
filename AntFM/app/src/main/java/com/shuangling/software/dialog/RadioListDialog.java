package com.shuangling.software.dialog;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.AudioListAdapter;
import com.shuangling.software.adapter.RadioProgramListAdapter;
import com.shuangling.software.entity.RadioDetail;
import com.shuangling.software.fragment.RadioProgramListFragment;
import com.shuangling.software.fragment.SearchListFragment;
import com.shuangling.software.service.AudioPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
public class RadioListDialog extends BaseCircleDialog implements View.OnClickListener {


    private TabLayout tabPageIndicator;

    private ViewPager viewPager;

    private TextView close;

    private RadioDetail radioDetail;

    public static RadioListDialog getInstance(RadioDetail radioDetail) {
        RadioListDialog dialogFragment = new RadioListDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.radioDetail=radioDetail;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_radio_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        tabPageIndicator=view.findViewById(R.id.tabPageIndicator);
        viewPager=view.findViewById(R.id.viewPager);
        close=view.findViewById(R.id.close);

        List<View> views=new ArrayList<>();
        ListView listView0=new ListView(getContext());
        listView0.setDivider(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
        listView0.setVerticalScrollBarEnabled(false);
        listView0.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        RadioProgramListAdapter adapter0=new RadioProgramListAdapter(getContext(),radioDetail.getProgram_list().get(0));
        //adapter0.setInPlayBean(radioDetail.getIn_play());
        listView0.setAdapter(adapter0);
        views.add(listView0);
        ListView listView1=new ListView(getContext());
        listView1.setDivider(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
        listView1.setVerticalScrollBarEnabled(false);
        listView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        RadioProgramListAdapter adapter1=new RadioProgramListAdapter(getContext(),radioDetail.getProgram_list().get(1));
        adapter1.setInPlayBean(radioDetail.getIn_play());
        listView1.setAdapter(adapter1);
        views.add(listView1);
        ListView listView2=new ListView(getContext());
        listView2.setDivider(ContextCompat.getDrawable(getContext(),R.drawable.recycleview_divider_drawable));
        listView2.setVerticalScrollBarEnabled(false);
        listView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        RadioProgramListAdapter adapter2=new RadioProgramListAdapter(getContext(),radioDetail.getProgram_list().get(2));
        //adapter2.setInPlayBean(radioDetail.getIn_play());
        listView2.setAdapter(adapter2);
        views.add(listView2);

        ViewAdapter adapter=new ViewAdapter(views);
        viewPager.setAdapter(adapter);
        tabPageIndicator.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playOrder) {




        } else if(v.getId() == R.id.showOrder){



        }else if(v.getId() == R.id.close){
            dismiss();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }





    class ViewAdapter extends PagerAdapter {
        private List<View> datas;

        public ViewAdapter(List<View> list) {
            datas=list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "昨天";
            }else if(position==1){
                return "今天";
            }else{
                return "明天";
            }
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=datas.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(datas.get(position));
        }
    }

}
