package com.shuangling.software.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.shuangling.software.R;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.ContentActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.activity.ui.WebProgress;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.PreloadWebView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class LinkWebViewFragment extends Fragment implements Handler.Callback  {
    private static final int LOGIN_RESULT = 0x1;
    public static final int MSG_GET_DETAIL = 0x2;
    private static final int SHARE_SUCCESS = 0x3;
    private static final int SHARE_FAILED = 0x4;
    private Column mColumn;
    private Handler mhandle;
    private WebView webView;
    WebProgress progressBar;
    private Handler mHandler;
    private String mUrl;
    private String mTitle;
    private int mActivityId;
    private String mJumpUrl;
    private boolean mAddParams;

    public LinkWebViewFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mhandle = new Handler(this);
        mColumn = (Column) args.getSerializable("Column");
//        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ink_web_view, container, false);
        FrameLayout fl_content = view.findViewById(R.id.fl_web_container_fragment);
        webView = PreloadWebView.getInstance().getWebView(getContext());
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init(view);
        return view;
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void init(View view) {
        mUrl = mColumn.getLink_address();
        mTitle = mColumn.getName();
        mAddParams = CommonUtils.isInlink(mUrl);
        if (mAddParams){//内链不初始化进度条

        }else{//外链初始化进度条
            View stubView  = ((ViewStub) view.findViewById(R.id.viewStub_fragment)).inflate();
            progressBar = stubView.findViewById(R.id.progress);
        }
        String url = initUrl(mUrl);
        mHandler = new Handler(this);
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (progressBar != null) progressBar.show();
                return true;
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressBar != null) progressBar.show();
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) progressBar.hide();
                super.onPageFinished(view, url);
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

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            // 设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                long id = Thread.currentThread().getId();
//                Log.i("onProgressChanged", "" + newProgress);
                if (newProgress == 100) {
                    if (progressBar != null) progressBar.hide();
                } else {
                    if (progressBar != null) progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mTitle == null && title != null && !title.startsWith("http")) {
//                    activtyTitle.setTitleText(title);
//                    activtyTitle.setTitle(title);
                }
            }
        });
       webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl(url);
        if (progressBar != null) {
            progressBar.show();
            progressBar.setColor(CommonUtils.getTranslucentThemeColor(getContext()),CommonUtils.getThemeColor(getContext()));
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    private String initUrl(String url) {
        if (mAddParams) {//内链
            if (User.getInstance() == null) {
                if (MainActivity.sCurrentCity != null) {
                    if (url.contains("?")) {
                        url = url + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    }
                } else {
                    if (url.contains("?")) {
                        url = url + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?app=android" + "&multiple=" + CommonUtils.getFontSize();
                    }
                }
            } else {
                if (MainActivity.sCurrentCity != null) {
                    if (url.contains("?")) {
                        url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&city=" + MainActivity.sCurrentCity.getCode() + "&multiple=" + CommonUtils.getFontSize();
                    }
                } else {
                    if (url.contains("?")) {
                        url = url + "&Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    } else {
                        url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android" + "&multiple=" + CommonUtils.getFontSize();
                    }
                }
            }
//            if (mActivityId != -1) {
//                url = url + "&qaa_act_id=" + mActivityId + "&multiple=" + CommonUtils.getFontSize();
//            }
            return url;
        } else {
            return url;
        }

    }

    private final class JsToAndroid {
        @JavascriptInterface
        public void backEvent(String str) {
//            long id = Thread.currentThread().getId();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    finish();
                }
            });
        }

        @JavascriptInterface
        public void loginEvent(final String bindPhone) {
            mJumpUrl = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        @JavascriptInterface
        public void loginEvent(final String bindPhone, String url) {
            mJumpUrl = url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    if (bindPhone.equals("0")) {
                        it.putExtra("bindPhone", false);
                    } else {
                        it.putExtra("bindPhone", true);
                    }
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        @JavascriptInterface
        public void bindPhoneEvent(final String url) {
            mJumpUrl = url;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //showShare();
                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                    it.putExtra("hasLogined", true);
                    startActivityForResult(it, LOGIN_RESULT);
                }
            });
        }

        //        @JavascriptInterface
//        public void shareEvent(final String str) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    showShare(str);
//                }
//            });
//
//
//        }
        @JavascriptInterface
        public void shareEvent(final String id, final String title, final String des, final String url, final String logo) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showShare(id, title, des, url, logo);
                }
            });
        }

        @JavascriptInterface
        public void pageEvent(final String id, final String type, final String title) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //1音频、2专辑、3文章、4视频、5专题、7图集
                    if (type.equals("1")) {
                        Intent it = new Intent(getContext(), AudioDetailActivity.class);
                        it.putExtra("audioId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("2")) {
                        Intent it = new Intent(getContext(), AlbumDetailActivity.class);
                        it.putExtra("albumId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("3")) {
                        Intent it = new Intent(getContext(), ArticleDetailActivity02.class);
                        it.putExtra("articleId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("4")) {
                        Intent it = new Intent(getContext(), VideoDetailActivity.class);
                        it.putExtra("videoId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("5")) {
                        Intent it = new Intent(getContext(), SpecialDetailActivity.class);
                        it.putExtra("specialId", Integer.parseInt(id));
                        startActivity(it);
                    } else if (type.equals("7")) {
                        Intent it = new Intent(getContext(), GalleriaActivity.class);
                        it.putExtra("galleriaId", Integer.parseInt(id));
                        startActivity(it);
                    }
                }
            });
        }

        @JavascriptInterface
        public void cateEvent(final String typeId, final String columnId, final String name) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent it = new Intent(getContext(), ContentActivity.class);
                    Column column = new Column();
                    column.setName(name);
                    column.setId(Integer.parseInt(columnId));
                    it.putExtra("column", column);
                    startActivity(it);
                }
            });
        }

        @JavascriptInterface
        public void confirmToBrowser(final String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
    private void showShare(String platform, final String title, final String desc, final String logo, final String url) {
        final String cover;
        if (logo.startsWith("http://")) {
            cover = logo.replace("http://", "https://");
        } else {
            cover = logo;
        }
        OnekeyShare oks = new OnekeyShare();
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
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //点击新浪微博
                String chanel = "1";
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    chanel = "2";
                    //限制微博分享的文字不能超过20
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(title + url);
                } else if (QQ.NAME.equals(platform.getName())) {
                    chanel = "3";
                    paramsToShare.setTitle(title);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(desc);
                } else if (Wechat.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                    paramsToShare.setText(desc);
                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                } else if (WechatMoments.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                } else if (WechatFavorite.NAME.equals(platform.getName())) {
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setUrl(url);
                    if (!TextUtils.isEmpty(cover)) {
                        paramsToShare.setImageUrl(cover);
                    }
                }
//shareStatistics(chanel, "" + mArticle.getId(), ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?app=android");
            }
        });
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


}