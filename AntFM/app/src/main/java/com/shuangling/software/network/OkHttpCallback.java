package com.shuangling.software.network;

import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public abstract class OkHttpCallback {




    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    public abstract void onFailure(Call call, Exception e);

    //public abstract void onResponse(Call call, Response response) throws IOException;

    public abstract void onResponse(Call call, String response) throws IOException;

    public OkHttpCallback(Context context){
        mContext= context;
    }


}
