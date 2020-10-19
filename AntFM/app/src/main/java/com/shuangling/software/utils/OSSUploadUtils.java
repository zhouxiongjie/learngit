package com.shuangling.software.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.MyApplication;
import com.shuangling.software.dialog.ChatDialog;
import com.shuangling.software.entity.OssInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.fragment.LiveChatFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.oss.OSSAKSKCredentialProvider;
import com.shuangling.software.oss.OssService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2019-01-25.
 */

public class OSSUploadUtils implements OSSCompletedCallback<PutObjectRequest, PutObjectResult> {


    private static OSSUploadUtils sOSSUploadUtils;

    private OnCallbackListener mOnCallbackListener;

    public OSSUploadUtils setOnCallbackListener(OnCallbackListener onCallbackListener) {
        this.mOnCallbackListener = onCallbackListener;
        return this;
    }

    public interface OnCallbackListener {

        void onSuccess(String url);

        void onSuccessAll();

        void onFailed();
    }



    private OssInfo mOssInfo;
    private OssService mOssService;
    //private String mUploadFilePath;
    //待上传成功数量
    private int mFileNumber;

    public static OSSUploadUtils getInstance(){
        if(sOSSUploadUtils==null){
            synchronized (OSSUploadUtils.class){
                if (sOSSUploadUtils == null) {
                    sOSSUploadUtils = new OSSUploadUtils();
                }
            }
        }
        return sOSSUploadUtils;
    }


    public void uploadFile(Context context,String filePath) {
        mFileNumber=1;
        //mUploadFilePath=filePath;
        String url = ServerInfo.serviceIP + ServerInfo.appOss;

        OkHttpUtils.get(url, null, new OkHttpCallback(context) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService=initOSS(mOssInfo.getHost(),mOssInfo.getBucket(),mOssInfo.getAccess_key_id(),mOssInfo.getAccess_key_secret(),mOssInfo.getExpiration(),mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);

                    // 2.把图片文件file上传到服务器
                    if(mOssInfo != null&&mOssService!=null){
                        mOssService.asyncUploadFile(mOssInfo.getDir()+filePath.substring(filePath.lastIndexOf(File.separator)+1),filePath,null,OSSUploadUtils.this);
                    }else{
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                    }
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());
            }
        });

    }



    public void uploadFile(Context context,List<String> filePaths) {

        mFileNumber=filePaths.size();

        String url = ServerInfo.serviceIP + ServerInfo.appOss;

        OkHttpUtils.get(url, null, new OkHttpCallback(context) {

            @Override
            public void onResponse(Call call, String response) throws IOException {


                String result = response;
                final JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mOssInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), OssInfo.class);
                    mOssService=initOSS(mOssInfo.getHost(),mOssInfo.getBucket(),mOssInfo.getAccess_key_id(),mOssInfo.getAccess_key_secret(),mOssInfo.getExpiration(),mOssInfo.getSecurity_token());
                    //mOssService.setCallbackAddress(Config.callbackAddress);

                    // 2.把图片文件file上传到服务器
                    if(mOssInfo != null&&mOssService!=null){

                        for(int i=0;i<filePaths.size();i++){
                            String filepath=filePaths.get(i);
                            mOssService.asyncUploadFile(mOssInfo.getDir()+filepath.substring(filepath.lastIndexOf(File.separator)+1),filepath,null,OSSUploadUtils.this);
                        }

                    }else{
                        ToastUtils.show("OSS初始化失败,请稍后再试");
                        if(mOnCallbackListener!=null){
                            mOnCallbackListener.onFailed();
                        }
                    }
                }


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                Log.e("test",exception.toString());
                if(mOnCallbackListener!=null){
                    mOnCallbackListener.onFailed();
                }
            }
        });

    }




    public OssService initOSS(String endpoint, String bucket, String accessKey, String accessKeySecret, String expiration,
                              String securityToken) {

//        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
//        https://help.aliyun.com/document_detail/31920.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
//        主账户使用方式
//        String AK = "******";
//        String SK = "******";
//        credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
//        以下是使用STS Sever方式。
//        如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
//        详见：https://help.aliyun.com/document_detail/31920.html
//        OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。

        OSSAKSKCredentialProvider oSSAKSKCredentialProvider;
        //使用自己的获取STSToken的类

        oSSAKSKCredentialProvider = new OSSAKSKCredentialProvider(accessKey,accessKeySecret,securityToken,expiration);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(MyApplication.getInstance().getApplicationContext(), endpoint,  new OSSStsTokenCredentialProvider(accessKey, accessKeySecret, securityToken), conf);
        OSSLog.enableLog();
        return new OssService(oss, bucket);

    }



    @Override
    public void onSuccess(PutObjectRequest request, PutObjectResult result) {

        synchronized (this){
            long id=Thread.currentThread().getId();
            mFileNumber--;
            if(mOnCallbackListener!=null){
                String filepath=request.getUploadFilePath();
                //mOnCallbackListener.onSuccess(mOssInfo.getHost()+"/"+mOssInfo.getDir()+mUploadFilePath.substring(mUploadFilePath.lastIndexOf(File.separator)+1));
                mOnCallbackListener.onSuccess(mOssInfo.getHost()+"/"+mOssInfo.getDir()+filepath.substring(filepath.lastIndexOf(File.separator)+1));

                if(mFileNumber==0){
                    //全部上传成功
                    mOnCallbackListener.onSuccessAll();
                }
            }
        }


    }

    @Override
    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
        if(mOnCallbackListener!=null){
            mOnCallbackListener.onFailed();
        }
    }
}
