package com.shuangling.software.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.shuangling.software.R;
import com.shuangling.software.activity.BaseActivity;
import com.shuangling.software.activity.SingleAudioDetailActivity;
import com.shuangling.software.adapter.AudioListAdapter;
import com.shuangling.software.adapter.SelectionsGridViewAdapter;
import com.shuangling.software.customview.MyGridView;
import com.shuangling.software.dialog.AudioSelectionsDialog;
import com.shuangling.software.dialog.AudioSpeedDialog;
import com.shuangling.software.entity.Audio;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class AlbumAudiosFragment extends Fragment implements Handler.Callback {

    public static final int MSG_GET_AUDIOS_LIST = 0x1;
    @BindView(R.id.playAll)
    TextView playAll;
    @BindView(R.id.audioSort)
    TextView audioSort;
    @BindView(R.id.audioSelect)
    TextView audioSelect;
    @BindView(R.id.topBar)
    RelativeLayout topBar;
    @BindView(R.id.listView)
    ListView listView;
    Unbinder unbinder;
//    @BindView(R.id.gridView)
//    GridView gridView;
//    @BindView(R.id.selectionsLayout)
//    LinearLayout selectionsLayout;

    private int currentPage = 1;
    private int mAlbumId;
    private Handler mHandler;
    private AudioListAdapter mAdapter;

    private int mSequence = AudioPlayerService.POSITIVE;
    private List<AudioInfo> mAudios = new ArrayList<>();
    private List<Integer> mSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_audios_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        getAlbumAudios();
        return view;

    }


    private void getAlbumAudios() {
        mAlbumId = getArguments().getInt("albumId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.getAlbumAudios + mAlbumId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "" + Integer.MAX_VALUE);
        params.put("page", "" + currentPage);
        params.put("sort", "" + 0);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_AUDIOS_LIST;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_AUDIOS_LIST:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        JSONObject jo = jsonObject.getJSONObject("data");

                        List<Audio> audios = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Audio.class);
                        mAudios.clear();
                        mSelect=new ArrayList<>();
                        for (int i = 0; audios != null && i < audios.size(); i++) {
                            Audio audio = audios.get(i);
                            AudioInfo audioInfo = new AudioInfo();
                            audioInfo.setId(audio.getAudios().get(0).getId());
                            audioInfo.setIndex(i + 1);
                            audioInfo.setTitle(audio.getAudios().get(0).getTitle());
                            audioInfo.setUrl(audio.getAudios().get(0).getAudio().getUrl());
                            audioInfo.setDuration("" + audio.getAudios().get(0).getAudio().getDuration());
                            audioInfo.setPublish_at(audio.getAudios().get(0).getPublish_at());
                            mAudios.add(audioInfo);
                            mSelect.add(i + 1);
                        }

                        playAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mAudios.size()>1){
                                    AudioInfo audio = mAudios.get(0);
                                    Intent it = new Intent(getContext(), SingleAudioDetailActivity.class);
                                    it.putExtra("audioId", audio.getId());
                                    getContext().startActivity(it);
                                }

                            }
                        });

                        if (mAdapter == null) {
                            mAdapter = new AudioListAdapter(getContext(), mAudios, ((BaseActivity) getActivity()).mAudioPlayer);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                }
                            });
                            listView.setAdapter(mAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                                            ((BaseActivity)getActivity()).mAudioPlayer.setPlayerList(mAudios);
                                    AudioInfo audio = mAdapter.getItem(position);
                                    Intent it = new Intent(getContext(), SingleAudioDetailActivity.class);
                                    it.putExtra("audioId", audio.getId());
                                    getContext().startActivity(it);

                                }
                            });

                        } else {
                            mAdapter.updateView(mAudios);
                        }









                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @OnClick({R.id.audioSort, R.id.audioSelect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.audioSort:
                if (mSequence == AudioPlayerService.POSITIVE) {
                    mSequence = AudioPlayerService.INVERTED;
                    Drawable drawableLeft = getResources().getDrawable(R.drawable.order_dec);
                    audioSort.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                    audioSort.setText("降序");
                    Collections.reverse(mAudios);
                    mAdapter.updateView(mAudios);
                    mAdapter.notifyDataSetChanged();

                } else {
                    mSequence = AudioPlayerService.POSITIVE;
                    Drawable drawableLeft = getResources().getDrawable(R.drawable.order_asc);
                    audioSort.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                    audioSort.setText("正序");
                    Collections.reverse(mAudios);
                    mAdapter.updateView(mAudios);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.audioSelect:
                AudioSelectionsDialog dialog=AudioSelectionsDialog.getInstance(mAudios,mSequence,mSelect);
                dialog.setSelectAudio(new AudioSelectionsDialog.IselectAudio() {
                    @Override
                    public void onSelectedAudio(List<Integer> select) {
                        mSelect=select;
                        List<AudioInfo> selected=new ArrayList<>();
                        for(int i=0;i<select.size();i++){
                            if(mSequence==AudioPlayerService.POSITIVE){
                                selected.add(mAudios.get(select.get(i)-1));
                            }else{
                                selected.add(mAudios.get(mAudios.size()-select.get(i)));
                            }
                        }
                        mAdapter.updateView(selected);
                    }
                });
                dialog.show(getChildFragmentManager(), "AudioSelectionsDialog");
                break;
        }
    }
}
