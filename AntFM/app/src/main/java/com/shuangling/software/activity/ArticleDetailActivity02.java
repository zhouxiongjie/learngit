package com.shuangling.software.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.AudioRecyclerAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

@EnableDragToClose()
public class ArticleDetailActivity02 extends BaseAudioActivity implements Handler.Callback{

    public static final String TAG = "AlbumDetailActivity";


    public static final int MSG_GET_RELATED_POST = 0x3;

    public static final int MSG_GET_COMMENTS = 0x4;

    public static final int MSG_WRITE_COMMENTS = 0x5;

    public static final int MSG_PRAISE = 0x6;

    public static final int REQUEST_LOGIN = 0x8;

    public static final int MSG_DELETE_COMMENT = 0xb;



    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.divideOne)
    ImageView divideOne;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
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
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;



    private Handler mHandler;
    private int mArticleId;
    private List<ColumnContent> mPostContents;
    private List<Comment> mComments=new ArrayList<>();
    boolean mScrollToTop = false;
    private DialogFragment mCommentDialog;
    private AudioRecyclerAdapter mAdapter;

    private HeadViewHolder mHeadViewHolder;
    private int currentPage=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_audio_detail);
        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        init();

    }


    @Override
    protected void onNewIntent(Intent intent) {

        mArticleId = getIntent().getIntExtra("articleId", 0);

        getComments(0);
        getRelatedPosts();
        super.onNewIntent(intent);
    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.webView)
        public WebView webView;


        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void init() {
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));//设置

        mHandler = new Handler(this);

        mArticleId = getIntent().getIntExtra("articleId", 0);
        activityTitle.setMoreAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new AudioRecyclerAdapter(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycleview_divider_drawable));
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(mAdapter);
        ViewGroup headView = (ViewGroup) getLayoutInflater().inflate(R.layout.article_top_layout, recyclerView, false);
        mHeadViewHolder = new HeadViewHolder(headView);
        mAdapter.addHeaderView(headView);


        String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;
        url = url + "?app=android&size=" + 1+"&multiple="+CommonUtils.getFontSize();

        WebView webView=mHeadViewHolder.webView;
        WebSettings s = webView.getSettings();
        CommonUtils.setWebviewUserAgent(s);
        s.setTextZoom(100);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        s.setJavaScriptEnabled(true);       //js
        s.setDomStorageEnabled(true);       //localStorage

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



//                int w = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                int h = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                //重新测量
//                view.measure(w, h);
//                mWebViewHeight = view.getHeight();
//                Log.i(TAG, "WEBVIEW高度:" + view.getHeight());


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
                long id = Thread.currentThread().getId();
                Log.i("onProgressChanged", "" + newProgress);
//                if (newProgress == 100) {
//                    progressBar.setVisibility(GONE);
//                } else {
//                    if (progressBar.getVisibility() == GONE)
//                        progressBar.setVisibility(VISIBLE);
//                    progressBar.setProgress(newProgress);
//                }
                super.onProgressChanged(view, newProgress);
            }

            // 设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        //webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
        //webView.loadUrl("https://github.com/756718646/RichWebList");
        //webView.loadUrl("http://www-cms-c.staging.slradio.cn/posts/27750");
        //webView.loadUrl("http://www.baidu.com");
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage++;
                getComments(1);
            }
        });


        getRelatedPosts();
    }




    private void getRelatedPosts() {
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mArticleId);
        params.put("is_mixed", "1" );
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




    //type  0 正常   1 加载更多
    private void getComments(final int type) {
        if(type==0){
            currentPage=1;
        }
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mArticleId);
        params.put("page", "" + currentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
                msg.arg1=type;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }


    private void praise(String commentId, final View view) {
        String url = ServerInfo.serviceIP + ServerInfo.praise;
        Map<String, String> params = new HashMap<>();
        params.put("id", commentId);

        OkHttpUtils.put(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_PRAISE;
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.obj = view;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }





    private void deleteComment(Comment comment) {


        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + comment.getId());

        OkHttpUtils.delete(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_DELETE_COMMENT;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    private void writeComments(String content, String parentId, String topCommentId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mArticleId);
        params.put("type", "1");
        params.put("content", content);
        params.put("parent_id", parentId);
        params.put("base_comment_id", topCommentId);
        params.put("source_type", "3");
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_WRITE_COMMENTS;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.writeComment, R.id.commentNumLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {


            case R.id.writeComment: {

                if (User.getInstance() == null) {

                    Intent it = new Intent(this, NewLoginActivity.class);
                    it.putExtra("bindPhone",true);
                    startActivityForResult(it, REQUEST_LOGIN);
                }else if (User.getInstance() !=null&&TextUtils.isEmpty(User.getInstance().getPhone())) {

                    Intent it = new Intent(this, BindPhoneActivity.class);
                    //it.putExtra("hasLogined",true);
                    startActivity(it);
                }else {
                    mCommentDialog = new CircleDialog.Builder()
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(true)
                            .setInputManualClose(true)
                            .setTitle("发表评论")
//                        .setSubTitle("发表评论")
                            .setInputHint("写评论")
//                        .setInputText("默认文本")
                            .autoInputShowKeyboard()
                            .setInputCounter(200)
                            .configInput(new ConfigInput() {
                                @Override
                                public void onConfig(InputParams params) {
                                    params.styleText = Typeface.NORMAL;
                                }
                            })
                            .setNegative("取消", null)
                            .setPositiveInput("发表", new OnInputClickListener() {
                                @Override
                                public void onClick(String text, View v) {
                                    if (TextUtils.isEmpty(text)) {
                                        ToastUtils.show("请输入内容");
                                    } else {
                                        mCommentDialog.dismiss();
                                        //发送评论
                                        writeComments(text, "0", "0");

                                    }
                                }
                            })
                            .show(getSupportFragmentManager());
                }

            }
            break;
            case R.id.commentNumLayout:
                if (mScrollToTop) {
                    mScrollToTop = false;
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    mScrollToTop = true;
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    llm.scrollToPositionWithOffset(mPostContents != null ? 1 + mPostContents.size() : 1, 0);
                    llm.setStackFromEnd(false);
                }
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {

            case MSG_GET_RELATED_POST: {
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mPostContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        mAdapter.setPostContents(mPostContents);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case MSG_GET_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    int totalNumber = jsonObject.getJSONObject("data").getInteger("total");
                    mAdapter.setTotalComments(totalNumber);
                    commentNumber.setText("" + totalNumber);

                    List<Comment> comments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);

                    if (msg.arg1 == 0) {
                        mComments=comments;
                    } else if (msg.arg1 == 1) {

                        if (refreshLayout.getState() == RefreshState.Loading) {
                            if(comments==null||comments.size()==0){
                                refreshLayout.finishLoadMoreWithNoMoreData();
                            }else{
                                refreshLayout.finishLoadMore();
                            }
                        }
                        mComments.addAll(comments);
                    }


                    if(mComments==null||mComments.size()==0){
                        refreshLayout.setEnableLoadMore(false);
                    }
                    mAdapter.setComments(mComments);

                    mAdapter.setOnPraise(new AudioRecyclerAdapter.OnPraise() {
                        @Override
                        public void praiseItem(Comment comment, View v) {
                            if (User.getInstance() != null) {
                                praise("" + comment.getId(), v);
                            } else {
                                startActivityForResult(new Intent(ArticleDetailActivity02.this, NewLoginActivity.class), REQUEST_LOGIN);
                            }

                        }

                        @Override
                        public void deleteItem(final Comment comment, View v) {
                            new CircleDialog.Builder()
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)

                                    .setText("确认删除此评论?")
                                    .setNegative("取消", null)
                                    .setPositive("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            deleteComment(comment);

                                        }
                                    })
                                    .show(getSupportFragmentManager());
                        }
                    });
                }
            }
            break;
            case MSG_DELETE_COMMENT:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("删除成功");
                        getComments(0);
                    }

                } catch (Exception e) {

                }
                break;
            case MSG_WRITE_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    ToastUtils.show("发表成功");
                    getComments(0);

                }
            }
            break;
            case MSG_PRAISE:
                try {

                    String result = msg.getData().getString("response");
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        TextView textView = (TextView) msg.obj;
                        if (jsonObject.getInteger("data") == 1) {
                            //取消点赞
                            textView.setActivated(true);
                            textView.setText("" + (Integer.parseInt(textView.getText().toString()) - 1));
                        } else {
                            //点赞成功
                            textView.setActivated(false);
                            textView.setText("" + (Integer.parseInt(textView.getText().toString()) + 1));

                        }
                        //getComments();

                    }

                } catch (Exception e) {

                }
                break;

        }
        return false;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getComments(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
