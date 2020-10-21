package com.shuangling.software.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.R;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.WebViewActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.dialog.ShareLivePosterDialog;
import com.shuangling.software.dialog.SharePosterDialog;
import com.shuangling.software.entity.LiveRoomInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_PICTURES;


public class InviteRankFragment extends Fragment implements Handler.Callback {

    private static final int LOGIN_RESULT = 0x1;

    public static final int SHARE_FAILED = 0x2;

    public static final int SHARE_SUCCESS = 0x3;

    @BindView(R.id.webView)
    WebView webView;


    Unbinder unbinder;
    private Handler mHandler;
    private LiveRoomInfo mLiveRoomInfo;
    private String mJumpUrl;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        Bundle args = getArguments();
        mLiveRoomInfo=(LiveRoomInfo)args.getSerializable("LiveRoomInfo");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;

    }



    private void init() {

        WebSettings s = webView.getSettings();
        CommonUtils.setWebviewUserAgent(s);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        s.setTextZoom(100);
        s.setJavaScriptEnabled(true);       //js
        s.setDomStorageEnabled(true);       //localStorage
//        webView.setWebViewClient(new WebViewClient());


        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true

                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                //progressBar.setVisibility(View.GONE);

                super.onPageFinished(view, url);
                //getImgText();


            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {

                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				view.loadData(errorHtml, "text/html; charset=UTF-8", null);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            ;

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            ;

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            ;

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        });

        webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        String url=mLiveRoomInfo.getH5_index();
        url=url.substring(0,url.lastIndexOf("/"));
        url=url+"/inv-rank";
        if(User.getInstance()!=null){
            if(!TextUtils.isEmpty(url)){
                url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize()+"&stream_name="+mLiveRoomInfo.getStream_name();
            }
        }

        webView.loadUrl(url);

        //getImgText();
    }


    private final class JsToAndroid {


        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            mJumpUrl=null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    if(bindPhone.equals("0")){
                        it.putExtra("bindPhone",false);
                    }else{
                        it.putExtra("bindPhone",true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });


        }



        @JavascriptInterface
        public void loginEvent(final String bindPhone,String url) {
            mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    if(bindPhone.equals("0")){
                        it.putExtra("bindPhone",false);
                    }else{
                        it.putExtra("bindPhone",true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });

        }

        @JavascriptInterface
        public void bindPhoneEvent(final String url) {
            mJumpUrl=url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //showShare();
                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                    it.putExtra("hasLogined",true);
                    startActivityForResult(it, LOGIN_RESULT);

                }
            });
        }

        @JavascriptInterface
        public void posterPage(String shareUrl) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showPosterShare(shareUrl);
                }
            });

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_RESULT) {

            String url=mLiveRoomInfo.getH5_index();
            url=url.substring(0,url.lastIndexOf("/"));
            url=url+"/inv-rank";
            if(User.getInstance()!=null){
                if(!TextUtils.isEmpty(url)){
                    url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android"+"&multiple="+CommonUtils.getFontSize()+"&stream_name="+mLiveRoomInfo.getStream_name();
                }
            }
            webView.loadUrl(url);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPosterShare(String shareUrl){

        ShareLivePosterDialog dialog =  ShareLivePosterDialog.getInstance(mLiveRoomInfo,shareUrl);
        dialog.setShareHandler(new ShareLivePosterDialog.ShareHandler() {
            @Override
            public void onShare(String platform,Bitmap bitmap) {
                showShareImage(platform,bitmap);

            }

            @Override
            public void download(Bitmap bitmap) {

                final Bitmap saveBitmap =bitmap;

                //获取写文件权限
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if(granted){
                                    Random rand = new Random();
                                    int randNum = rand.nextInt(1000);
                                    File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString()+randNum+".png");
                                    CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                    ToastUtils.show("图片保存成功");

                                    //发送广播更新相册
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(tempFile);
                                    intent.setData(uri);
                                    getActivity().sendBroadcast(intent);

                                }else{
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });

            }


        });
        dialog.show(getChildFragmentManager(), "ShareDialog");

    }



    private void showShareImage(String platform, final Bitmap bitmap) {


        final Bitmap saveBitmap =bitmap;


        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
        if (!qq.isClientValid()) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        final Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (!sina.isClientValid()) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }


        Random rand = new Random();
        int randNum = rand.nextInt(1000);
        final String childPath =  CommonUtils.getCurrentTimeString()+randNum+".png";




        if(QQ.NAME.equals(platform)){

            //获取写文件权限
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if(granted){

                                final  File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), childPath);
                                CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                //ToastUtils.show("图片保存成功");

                                //发送广播更新相册
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.setData(uri);
                                getActivity().sendBroadcast(intent);

                                // oks.setImagePath(filePath);

                                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                    //自定义分享的回调想要函数
                                    @Override
                                    public void onShare(Platform platform, final  Platform.ShareParams paramsToShare) {

                                        paramsToShare.setShareType(Platform.SHARE_IMAGE);
                                        // paramsToShare.setImageData(bitmap);
                                        paramsToShare.setImagePath(tempFile.getAbsolutePath());

                                    }
                                });


                            }else{
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });




        }else{
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                //自定义分享的回调想要函数
                @Override
                public void onShare(Platform platform, final  Platform.ShareParams paramsToShare) {
                    paramsToShare.setShareType(Platform.SHARE_IMAGE);
                    paramsToShare.setImageData(bitmap);
                }
            });

        }



        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_FAILED;
                msg.obj = arg2.getMessage();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                Message msg = Message.obtain();
                msg.what = SHARE_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        // 启动分享GUI
        oks.show(getContext());
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

        }
        return false;
    }


    @Override
    public void onDestroyView() {
        webView.destroy();
        unbinder.unbind();
        super.onDestroyView();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
