package com.shuangling.software.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.shuangling.software.R;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class ImgTextFragment extends Fragment implements Handler.Callback {
//    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.noData)
    TextView noData;
    Unbinder unbinder;
    private Handler mHandler;
    private String mStreamName;
    private int mRoomId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        Bundle args = getArguments();
        mStreamName = args.getString("streamName");
        mRoomId = args.getInt("roomId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        FrameLayout fl_content = view.findViewById(R.id.fl_web_container);
        webView = PreloadWebView.getInstance().getWebView(getActivity());
        fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init();
        return view;
    }

    private void init() {
//        WebSettings s = webView.getSettings();
//        CommonUtils.setWebviewUserAgent(s);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//
//        }
//        webView.getSettings().setBlockNetworkImage(false);
//        s.setTextZoom(100);
//        s.setJavaScriptEnabled(true);       //js
//        s.setDomStorageEnabled(true);       //localStorage
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getImgText();
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
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        //webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        webView.loadUrl("file:///android_asset/live_img_text.html");
//        webView.loadDataWithBaseURL();
    }

    private void getImgText() {
        String url = ServerInfo.live + "/v1/play_text";
        Map<String, String> params = new HashMap<>();
        params.put("room_id", "" + mRoomId);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        JSONObject jo = jsonObject.getJSONObject("data");
                        if (jo != null) {
                            String text = jo.getString("text");
                            String js = "javascript:_getImgPreviewContent('" + text + "')";
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (TextUtils.isEmpty(text)) {
                                        noData.setVisibility(View.VISIBLE);
                                    } else {
                                        webView.loadUrl(js);
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.e("test", exception.toString());
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        }
        return false;
    }

    @Override
    public void onDestroyView() {
//        webView.destroy();
        unbinder.unbind();
        super.onDestroyView();
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
