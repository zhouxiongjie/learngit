package com.shuangling.software.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;
import com.shuangling.software.entity.LiveRoomDetail;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.GsonUtils;
import com.shuangling.software.utils.ServerInfo;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;


/**
 * 直播相关的接口
 */
public class APILiving extends  API {
    /**
     * http://yapi.slradio.cn:3000/project/220/interface/api/13881
     */
    public static void getRoomDetail(Context context, String streamName, APICallBack<LiveRoomDetail> callBack) {
        String url = ServerInfo.live + "/v3/get_room_details_c" + "?stream_name=" + streamName;
        OkHttpUtils.get(url, null, new OkHttpCallback(context) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.d("LiveDetail",response);

               handleResult(response, new APICallBack<String>() {
                   @Override
                   public void onSuccess(String data) {
                       List<LiveRoomDetail> roomDetails =  GsonUtils.gsonToList(data,LiveRoomDetail.class);
                       if(roomDetails.size()>0){
                           callBack.onSuccess(roomDetails.get(0));
                       }else{
                           callBack.onFail(ERR_API_ERROR);
                       }
                   }
                   @Override
                   public void onFail(String error) {
                       callBack.onFail(error);
                   }
               });
            }
            @Override
            public void onFailure(Call call, Exception exception) {
                callBack.onFail(callError(call, exception));
            }
        });
    }
}
