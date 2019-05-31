package com.shuangln.antfm.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.shuangln.antfm.R;
import com.shuangln.antfm.activity.AudioDetailActivity;
import com.shuangln.antfm.activity.BaseActivity;
import com.shuangln.antfm.adapter.AudioListAdapter;
import com.shuangln.antfm.entity.Audio;
import com.shuangln.antfm.network.OkHttpCallback;
import com.shuangln.antfm.network.OkHttpUtils;
import com.shuangln.antfm.service.AudioPlayerService;
import com.shuangln.antfm.service.IAudioPlayer;
import com.shuangln.antfm.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


public class AlbumAudiosFragment extends Fragment implements OnClickListener, Handler.Callback {

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

    private int currentPage = 1;
    private int totalPage = 1;

    private int mAlbumId;
    private Handler mHandler;
    private AudioListAdapter mAdapter;

//    private IAudioPlayer mAudioPlayer;
    private List<Audio> mAudios;

//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mAudioPlayer=IAudioPlayer.Stub.asInterface(service);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

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
//        Intent it = new Intent(getContext(), AudioPlayerService.class);
//        getContext().bindService(it, mConnection, Context.BIND_AUTO_CREATE);

        return view;

    }


    private void getAlbumAudios() {
        mAlbumId = getArguments().getInt("albumId", 0);

        String url = ServerInfo.serviceIP + ServerInfo.getAlbumAudios + mAlbumId;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageSize", "" + Integer.MAX_VALUE);

        params.put("page", "" + currentPage);
        params.put("sort", "" + AudioPlayerService.sSequence);

        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_AUDIOS_LIST;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, IOException exception) {


            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            default:
                break;
        }
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
                        if (jo.getInteger("total") > 0) {
                            mAudios = JSONObject.parseArray(jo.getJSONArray("data").toJSONString(), Audio.class);

                            if(mAdapter==null){
                                mAdapter = new AudioListAdapter(getContext(),mAudios);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    }
                                });
                                listView.setAdapter(mAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        try{
                                            ((BaseActivity)getActivity()).mAudioPlayer.setPlayerList(mAudios);
                                            Audio audio=mAdapter.getItem(position);
                                            Intent it=new Intent(getContext(),AudioDetailActivity.class);
                                            it.putExtra("Audio",audio);
                                            getContext().startActivity(it);
                                        }catch (RemoteException e){
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }else{
                                mAdapter.setData(mAudios);
                                mAdapter.notifyDataSetChanged();
                            }
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
}
