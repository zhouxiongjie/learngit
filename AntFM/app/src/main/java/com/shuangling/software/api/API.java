package com.shuangling.software.api;

import com.alibaba.fastjson.JSONObject;
import com.shuangling.software.entity.LiveMenu;

import java.util.Iterator;

import okhttp3.Call;

public class API {


    public static final String ERR_API_ERROR = "请求失败，接口错误";
;
    public  static  void handleResult(String response,APICallBack<String> callBack) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                String data = jsonObject.getString("data");
                callBack.onSuccess(data);
            }else{
                String msg = jsonObject.getString("msg");
                if(msg != null){
                    callBack.onFail(msg);
                }else{
                    callBack.onFail(ERR_API_ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onFail(ERR_API_ERROR);
        }
    }
    
    public  static  String callError(Call call, Exception exception) {
        return "请求失败," + exception.toString();
    }

}
