package com.shuangling.software.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.base.BaseAdapter;
import com.shuangling.software.base.BaseHolder;
import com.shuangling.software.customview.HeaderViewPager;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.HeaderScrollHelper;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

@EnableDragToClose()
public class ArticleDetailActivity01 extends BaseAudioActivity implements Handler.Callback{

    public static final int MSG_GET_RELATED_POST = 0x3;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.topHead)
    LinearLayout topHead;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.scrollableLayout)
    HeaderViewPager scrollableLayout;
    @BindView(R.id.writeComment)
    TextView writeComment;
    @BindView(R.id.commentsIcon)
    ImageView commentsIcon;
    @BindView(R.id.commentNumber)
    TextView commentNumber;
    @BindView(R.id.commentNumLayout)
    FrameLayout commentNumLayout;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private List<String> datas = new ArrayList<>();
    private ListAdapter adapter;

    private int mArticleId;
    private Handler mHandler;
    private List<ColumnContent> mPostContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details01);
        ImmersionBar.with(this).statusBarDarkFont(true).fitsSystemWindows(true).keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE).init();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mHandler = new Handler(this);
        mArticleId = getIntent().getIntExtra("articleId", 0);

        String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;

        url = url + "?app=android&size=" + 1+"&multiple="+CommonUtils.getFontSize();

        for (int i = 0; i < 30; i++) {
            datas.add("");
        }
        webView.loadUrl("https://github.com/756718646/RichWebList");
        adapter = new ListAdapter(datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //滚动绑定
        scrollableLayout.setCurrentScrollableContainer(new HeaderScrollHelper.ScrollableContainer() {
            @Override
            public View getScrollableView() {
                return recyclerView;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });
    }



    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mArticleId);
        params.put("is_mixed", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_RELATED_POST;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what){
            case MSG_GET_RELATED_POST: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mPostContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        //mAdapter.setPostContents(mPostContents);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }


    class ListAdapter extends BaseAdapter<String> {

        public ListAdapter(List<String> data) {
            super(R.layout.default_item, data);
        }

        @Override
        protected void convert(BaseHolder helper, String item) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }
}
