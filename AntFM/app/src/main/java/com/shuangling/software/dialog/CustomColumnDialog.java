package com.shuangling.software.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.shuangling.software.R;
import com.shuangling.software.adapter.MoreColumnGridViewAdapter;
import com.shuangling.software.adapter.MyColumnGridViewAdapter;
import com.shuangling.software.customview.DragGridView;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.RadioSet;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 注销框
 * Created by hupei on 2017/4/5.
 */
@EnableDragToClose()
public class CustomColumnDialog extends BaseCircleDialog {


    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.edit)
    TextView edit;
    @BindView(R.id.dragGridView)
    DragGridView dragGridView;
    @BindView(R.id.gridView)
    MyGridView gridView;
    Unbinder unbinder;

    private List<Column> mColumns;
    private Column mCurrentColumn;
    private List<Column> mCustomColumns=new ArrayList<>();
    private List<Column> mMoreColumns=new ArrayList<>();

    private MyColumnGridViewAdapter mDragGridViewAdapter;
    private MoreColumnGridViewAdapter mMoreGridViewAdapter;

    private OnCloseClickListener mOnCloseClickListener;
    public void setOnCloseClickListener(OnCloseClickListener onCloseClickListener) {
        this.mOnCloseClickListener = onCloseClickListener;
    }

    public interface OnCloseClickListener {
        void close();
        void switchClo(Column column,boolean initial);
        void refreshRed();

    }

    public static CustomColumnDialog getInstance(List<Column> columns,Column currentColumn) {
        CustomColumnDialog dialogFragment = new CustomColumnDialog();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        dialogFragment.mColumns=columns;
        dialogFragment.mCurrentColumn=currentColumn;
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.dialog_custom_column, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);

        SharedPreferencesUtils.putPreferenceTypeValue("all_column", SharedPreferencesUtils.PreferenceType.String, JSON.toJSONString(mColumns));
        String customColumn=SharedPreferencesUtils.getStringValue("custom_column","");
        if(TextUtils.isEmpty(SharedPreferencesUtils.getStringValue("custom_column",""))){
            //第一次自定义栏目
//            if(mColumns.size()>8){
//                for(int i=0;i<mColumns.size();i++){
//                  if(i<8){
//                      mCustomColumns.add(mColumns.get(i));
//                  }else{
//                      mMoreColumns.add(mColumns.get(i));
//                  }
//                }
//            }else{
                mCustomColumns=mColumns;
//            }
        }else{
            try{
                mCustomColumns= JSONObject.parseArray(customColumn, Column.class);

                List<Column> tempColumns=new ArrayList<>();
                List<Integer> columnIds=new ArrayList<>();
                for(int i=0;i<mCustomColumns.size();i++){
                    columnIds.add(mCustomColumns.get(i).getId());
                }
                Iterator<Column> iterator = mColumns.iterator();
                while (iterator.hasNext()) {
                    Column column = iterator.next();
                    if (columnIds.contains(column.getId())) {
                        tempColumns.add(column);
                        iterator.remove();

                    }
                }
                mMoreColumns=mColumns;
                columnIds.clear();
                for(int i=0;i<tempColumns.size();i++){
                    columnIds.add(tempColumns.get(i).getId());
                }
                tempColumns.clear();

                iterator = mCustomColumns.iterator();
                while (iterator.hasNext()) {
                    Column column = iterator.next();
                    if (columnIds.contains(column.getId())) {
                        tempColumns.add(column);

                    }
                }
                mCustomColumns=tempColumns;

            }catch (Exception e){

            }


        }

        mDragGridViewAdapter=new MyColumnGridViewAdapter(getContext(),mCustomColumns,mCurrentColumn );
        dragGridView.setEditorTextView(edit);
        dragGridView.setAdapter(mDragGridViewAdapter);
        dragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                Column temp = mCustomColumns.get(from);
                if(from < to){
                    for(int i=from; i<to; i++){
                        Collections.swap(mCustomColumns, i, i+1);
                    }
                }else if(from > to){
                    for(int i=from; i>to; i--){
                        Collections.swap(mCustomColumns, i, i-1);
                    }
                }
                mCustomColumns.set(to, temp);
                mDragGridViewAdapter.notifyDataSetChanged();
            }
        });

        dragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,	int position, long id) {
                // TODO Auto-generated method stub

                if(dragGridView.isDrag()&&position!=0&&position!=1){

                    Column column=mCustomColumns.remove(position);

                    mDragGridViewAdapter.notifyDataSetChanged();
                    mMoreColumns.add(0,column);
                    mMoreGridViewAdapter.notifyDataSetChanged();
                }else{
                    if(mOnCloseClickListener!=null){
                        Column column=mCustomColumns.get(position);

                        String customColumn=SharedPreferencesUtils.getStringValue("custom_column","");
                        String custom_column=JSON.toJSONString(mCustomColumns);
                        if(!custom_column.equals(customColumn)){
                            SharedPreferencesUtils.putPreferenceTypeValue("custom_column", SharedPreferencesUtils.PreferenceType.String,JSON.toJSONString(mCustomColumns));
                            mOnCloseClickListener.switchClo(column,true);
                            dismiss();
                        }else{
                            mOnCloseClickListener.switchClo(column,false);
                            dismiss();
                        }



                    }
                }

            }
        });

        //更多栏目显示
        mMoreGridViewAdapter=new MoreColumnGridViewAdapter(getContext(), mMoreColumns);
        gridView.setAdapter(mMoreGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                Column column=mMoreColumns.remove(position);
                mMoreGridViewAdapter.notifyDataSetChanged();
                mCustomColumns.add(column);
                mDragGridViewAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


    @OnClick({R.id.close, R.id.edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.close:
                String customColumn=SharedPreferencesUtils.getStringValue("custom_column","");
                String custom_column=JSON.toJSONString(mCustomColumns);
                if(!custom_column.equals(customColumn)){
                    SharedPreferencesUtils.putPreferenceTypeValue("custom_column", SharedPreferencesUtils.PreferenceType.String,JSON.toJSONString(mCustomColumns));
                    if(mOnCloseClickListener!=null){
                        mOnCloseClickListener.close();
                    }
                }
                dismiss();
                break;
            case R.id.edit:
                if(edit.getText().equals("编辑")){
                    mDragGridViewAdapter.setIsEditor(true);
                    dragGridView.setDrag(true);
                    edit.setText("完成");
                }else{
                    mDragGridViewAdapter.setIsEditor(false);
                    dragGridView.setDrag(false);
                    edit.setText("编辑");

                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        if(mOnCloseClickListener!=null){
            mOnCloseClickListener.refreshRed();
        }
        super.onDestroy();
    }
}
