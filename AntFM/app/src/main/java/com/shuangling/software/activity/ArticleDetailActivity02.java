package com.shuangling.software.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.previewlibrary.GPreviewBuilder;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.ArticleRecyclerAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.dialog.ShareDialog;
import com.shuangling.software.dialog.SharePosterDialog;
import com.shuangling.software.entity.Article;
import com.shuangling.software.entity.ArticleVoicesInfo;
import com.shuangling.software.entity.AudioInfo;
import com.shuangling.software.entity.ColumnContent;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.ImageInfo;
import com.shuangling.software.entity.User;
import com.shuangling.software.event.CommonEvent;
import com.shuangling.software.fragment.PicturePreviewFragment;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.service.AudioPlayerService;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.FloatWindowUtil;
import com.shuangling.software.utils.ImageLoader;
import com.shuangling.software.utils.PreloadWebView;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;
import com.shuangling.software.utils.TimeUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.youngfeng.snake.annotations.EnableDragToClose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jake.share.frdialog.dialog.FRDialog;
import cn.jake.share.frdialog.interfaces.FRDialogClickListener;
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
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.shuangling.software.service.AudioPlayerService.PLAY_ORDER;
import static com.shuangling.software.utils.CommonUtils.NETWORKTYPE_WIFI;

//@EnableDragToClose()
public class ArticleDetailActivity02 extends BaseAudioActivity implements Handler.Callback {
    public static final String TAG = "ArticleDetailActivity02";
    public static final int MSG_GET_RELATED_POST = 0x1;//相关文章
    public static final int MSG_GET_COMMENTS = 0x2;//评论列表
    public static final int MSG_WRITE_COMMENTS = 0x3;//写评论
    public static final int MSG_PRAISE = 0x4;//点赞
    public static final int REQUEST_LOGIN = 0x5;
    public static final int MSG_DELETE_COMMENT = 0x6;//删除评论
    public static final int SHARE_FAILED = 0x7;
    public static final int SHARE_SUCCESS = 0x8;
    public static final int MSG_GET_DETAIL = 0x9;//文章详情
    public static final int MSG_GET_VOICES = 0xa;//朗读文章
    public static final int MSG_ATTENTION_CALLBACK = 0xb;//关注
    public static final int MSG_COLLECT_CALLBACK = 0xc;//收藏
    public static final int MSG_LIKE_CALLBACK = 0xd;//点赞
    public static final int REQUEST_REPORT = 0xe;
    public static final int REQUEST_PERMISSION_CODE = 0x0110;
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
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;//评论列表
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.logo)
    SimpleDraweeView logo;//Toolbar中间的图片
    @BindView(R.id.organizationLogo)
    SimpleDraweeView organizationLogo;
    @BindView(R.id.organization)
    TextView organization;
    @BindView(R.id.attention)
    RelativeLayout attention;
    @BindView(R.id.attentionIcon)
    FontIconView attentionIcon;
    @BindView(R.id.organizationLayout)
    RelativeLayout organizationLayout;
    @BindView(R.id.img_more)
    ImageView imgMore;
    @BindView(R.id.titleBar)
    RelativeLayout titleBar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.networkError)
    RelativeLayout networkError;
    private boolean showMerchant = false;
    private Handler mHandler;
    private int mArticleId;
    private List<ColumnContent> mPostContents;//相关推荐
    private List<Comment> mComments = new ArrayList<>();
    boolean mScrollToTop = false;
    private DialogFragment mCommentDialog;
    private ArticleRecyclerAdapter mAdapter;
    private HeadViewHolder mHeadViewHolder;
    private int currentPage = 1;
    private Article mArticle;
    private ArticleVoicesInfo mArticleVoicesInfo;
    //    private ViewSkeletonScreen mViewSkeletonScreen;
    private boolean firstTime = true;
    private boolean isPlaying = false;
    private int mScrollY;
    private int firstItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail02);
//        CommonUtils.transparentStatusBar(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mArticleId = getIntent().getIntExtra("articleId", 0);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {//当Activity被设以singleTop模式启动，当需要再次响应此Activity启动需求时，会复用栈顶的已有Activity，还会调用onNewIntent方法
        mArticleId = intent.getIntExtra("articleId", 0);
        init();
        super.onNewIntent(intent);
    }

    /**
     * 原生文章标题、作者信息、WebView文章详情、原生阅读数、点赞、收藏
     */
    public class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.articleTitle)
        TextView articleTitle;
        @BindView(R.id.playVoice)
        FontIconView playVoice;
        @BindView(R.id.organizationLogo)
        SimpleDraweeView organizationLogo;
        @BindView(R.id.organization)
        TextView organization;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.attention)
        TextView attention;
        @BindView(R.id.organizationLayout)
        RelativeLayout organizationLayout;
        //@BindView(R.id.webView)
        WebView webView;
        @BindView(R.id.playTimes)
        TextView playTimes;
        @BindView(R.id.praiseSum)
        TextView praiseSum;
        @BindView(R.id.praiseLayout)
        RelativeLayout praiseLayout;
        @BindView(R.id.collect)
        TextView collect;
        @BindView(R.id.collectLayout)
        RelativeLayout collectLayout;
        @BindView(R.id.divide_line)
        View divide_line;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            FrameLayout fl_content = view.findViewById(R.id.fl_web_container);
            webView = PreloadWebView.getInstance().getWebView(ArticleDetailActivity02.this);
            fl_content.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void init() {
//        if (mViewSkeletonScreen == null) {
//            mViewSkeletonScreen = Skeleton.bind(root)//骨骼图
//                    .load(R.layout.skeleton_article_detail)
//                    .shimmer(false)
//                    .angle(20)
//                    .duration(1000)
//                    .color(R.color.shimmer_color)
//                    .show();
//        }
        if (MyApplication.getInstance().getStation() != null && !TextUtils.isEmpty(MyApplication.getInstance().getStation().getLogo2())) {
            Uri uri = Uri.parse(MyApplication.getInstance().getStation().getLogo2());
            ImageLoader.showThumb(uri, logo, CommonUtils.dip2px(161), CommonUtils.dip2px(28));//Toolbar中间的图片
        }
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));// 评论
        if (mHandler == null) mHandler = new Handler(this);
//        getArticleDetail();//文章详情 待WebView加载完成再请求
        imgBack.setOnClickListener(v -> finish());
        imgMore.setOnClickListener(v -> {
            if (mArticle != null) {
                ShareDialog dialog = ShareDialog.getInstance(mArticle.getIs_collection() != 0, mArticle.getIs_user_report() != 0);
                dialog.setIsShowPosterButton(true);
                dialog.setShareHandler(new ShareDialog.ShareHandler() {
                    @Override
                    public void onShare(String platform) {//分享至微信/QQ等
                        if (mArticle != null) {
                            String logo = "";
                            if (mArticle.getArticle().getCovers().size() > 0) {
                                logo = mArticle.getArticle().getCovers().get(0);
                            }
                            String url;
                            if (User.getInstance() != null) {
                                url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?from_user_id=" + User.getInstance().getId() + "&from_url=" + ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;
                            } else {
                                url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId + "?from_url=" + ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;
                            }
                            showShare(platform, mArticle.getTitle(), mArticle.getDes(), logo, url);
                        }
                    }

                    @Override
                    public void poster() {//点击分享海报
                        showPosterShare();
                    }

                    @Override
                    public void report() {//举报按钮
                        if (User.getInstance() == null) {
                            Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                            it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                            startActivityForResult(it, REQUEST_LOGIN);
                        } else {
                            Intent it = new Intent(ArticleDetailActivity02.this, ReportActivity.class);
                            it.putExtra("id", String.valueOf(mArticle.getId()));
                            startActivityForResult(it, REQUEST_REPORT);
                        }
                    }

                    @Override
                    public void copyLink() {//获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData clipData = ClipData.newPlainText("Label", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(clipData);
                        ToastUtils.show("复制成功，可以发给朋友们了。");
                    }

                    @Override
                    public void refresh() {//刷新
                        init();
                    }

                    @Override
                    public void collectContent() {//收藏
                        if (User.getInstance() == null) {
                            Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                            it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                            startActivityForResult(it, REQUEST_LOGIN);
                        } else {
                            if (mArticle.getIs_collection() == 0) {
                                collect(true);
                            } else {
                                collect(false);
                            }
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "ShareDialog");
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ArticleRecyclerAdapter(this);//
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int srcollY = recyclerView.getScrollY();
                int verticalOffset = recyclerView.computeVerticalScrollOffset();
                int height1 = mHeadViewHolder.articleTitle.getHeight();
                int height2 = mHeadViewHolder.organizationLayout.getHeight();
                if (verticalOffset > height1 + height2 && !showMerchant) {
                    //显示商户
                    showMerchant = true;
                    organizationLayout.setVisibility(View.VISIBLE);
                    logo.setVisibility(View.INVISIBLE);
                } else if (verticalOffset < height1 + height2 && showMerchant) {
                    //隐藏商户
                    showMerchant = false;
                    organizationLayout.setVisibility(View.INVISIBLE);
                    logo.setVisibility(View.VISIBLE);
                }
            }
        });
        ViewGroup headView = (ViewGroup) getLayoutInflater().inflate(R.layout.article_top_layout, recyclerView, false);
        mHeadViewHolder = new HeadViewHolder(headView);// 文章详情页面原生头部、webview、原生阅读次数、点赞、收藏
        mAdapter.addHeaderView(headView);
//        String url = ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId;//
//        int size = 1;//小图
//        int netLoad = SharedPreferencesUtils.getIntValue(SettingActivity.NET_LOAD, 0);
//        if (netLoad == 0 || CommonUtils.getNetWorkType(this) == NETWORKTYPE_WIFI) {
//            size = 2;//大图
//        }
//        if (User.getInstance() == null) {
//            url = url + "?app=android&size=" + size + "&multiple=" + CommonUtils.getFontSize();
//        } else {
//            url = url + "?Authorization=" + User.getInstance().getAuthorization() + "&app=android&size=" + size + "&multiple=" + CommonUtils.getFontSize();
//        }

        mHeadViewHolder.webView.setWebChromeClient(new InsideWebChromeClient());//Web浏览器Client
        mHeadViewHolder.webView.setWebViewClient(new InsideWebViewClient());//Web视图Client
        mHeadViewHolder.webView.addJavascriptInterface(new JsToAndroid(), "clientJS");
//        mHeadViewHolder.webView.loadUrl(url);
        mHeadViewHolder.webView.loadUrl("file:///android_asset/app_article_static.html");//
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {//评论
            currentPage++;
            getComments(1);
        });
    }

    /**
     * Js调用Java本地代码
     */
    private final class JsToAndroid {
        @JavascriptInterface
        public void viewImgEvent(final String imgs) {//            {"imgs":["https://sl-cdn.slradio.cn/vms/merchants/2019/20191223/1577078953/e252c3bea3040ac6eb2cc985d5f298de"],"index":"0"}
            JSONObject jo = JSON.parseObject(imgs);
            final JSONArray ja = jo.getJSONArray("imgs");
            final int index = jo.getInteger("index");
            mHandler.post(() -> {
                ArrayList<ImageInfo> images = new ArrayList<>();
                for (int i = 0; i < ja.size(); i++) {
                    ImageInfo image = new ImageInfo((String) ja.get(i));
                    images.add(image);
                    Rect bounds = new Rect(CommonUtils.getScreenWidth() / 2, CommonUtils.getScreenHeight() / 2, CommonUtils.getScreenWidth() / 2, CommonUtils.getScreenHeight() / 2);
                    image.setBounds(bounds);
                }
//                    GPreviewBuilder.from(ArticleDetailActivity02.this)
//                            .setData(images)
//                            .setCurrentIndex(index)
//                            .setDrag(true, 0.6f)
//                            .setSingleFling(true)
//                            .setType(GPreviewBuilder.IndicatorType.Number)
//                            .start();

                GPreviewBuilder.from(ArticleDetailActivity02.this)
                        .setData(images)
                        .setCurrentIndex(index)
                        .setUserFragment(PicturePreviewFragment.class)
                        .setDrag(true, 0.6f)
                        .setSingleFling(true)
                        .setType(GPreviewBuilder.IndicatorType.Number)
                        .start();
            });
        }
    }

    private void getRelatedPosts() {//相关文章
        String url = ServerInfo.serviceIP + ServerInfo.getRelatedRecommend;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(mArticleId));
        params.put("is_mixed", "1");
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) {
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
    private void getComments(final int type) {//获取评论列表
        if (type == 0) {
            currentPage = 1;
        }
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(mArticleId));
        params.put("page", String.valueOf(currentPage));
        params.put("page_size", String.valueOf(Constant.PAGE_SIZE));
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
                msg.arg1 = type;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    /**
     * 点赞
     */
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

    /**
     * 删除评论
     */
    private void deleteComment(Comment comment) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(comment.getId()));
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

    /**
     * 写评论
     */
    private void writeComments(String content, String parentId, String topCommentId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(mArticleId));
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

    /**
     * 关注
     *
     * @param follow 关注/取消关注
     */
    public void attention(final boolean follow) {
        String url = ServerInfo.serviceIP + ServerInfo.attention;
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(mArticle.getMerchant_id()));
        if (follow) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_ATTENTION_CALLBACK;
                msg.arg1 = follow ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(CommonEvent event) {
        if (event.getEventName().equals("onFontSizeChanged")) {
            init();
        }
    }

    @OnClick({R.id.writeComment, R.id.commentNumLayout, R.id.refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.writeComment: {//写评论
                if (User.getInstance() == null) {
                    Intent it = new Intent(this, NewLoginActivity.class);
                    it.putExtra("bindPhone", true);
                    it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                    Intent it = new Intent(this, BindPhoneActivity.class);
                    //it.putExtra("hasLogined",true);
                    startActivity(it);
                } else {
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
                            .configPositive(new ConfigButton() {
                                @Override
                                public void onConfig(ButtonParams params) {
                                    //按钮字体颜色
                                    params.textColor = CommonUtils.getThemeColor(ArticleDetailActivity02.this);
                                }
                            })
                            .setNegative("取消", null)
                            .setPositiveInput("发表", new OnInputClickListener() {
                                @Override
                                public void onClick(String text, View v) {
                                    if (TextUtils.isEmpty(text)) {
                                        ToastUtils.show("请输入内容");
                                    } else {
                                        CommonUtils.closeInputMethod(ArticleDetailActivity02.this);
                                        //发送评论
                                        writeComments(text, "0", "0");
                                        mCommentDialog.dismiss();
                                    }
                                }
                            })
                            .show(getSupportFragmentManager());
                }
            }
            break;
            case R.id.commentNumLayout://点击底部评论框
                if (mScrollToTop) {
                    mScrollToTop = false;
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (firstItem == 0) {
                        llm.scrollToPositionWithOffset(firstItem, -mScrollY);
                    } else {
                        llm.scrollToPositionWithOffset(firstItem, 0);
                    }
                } else {
                    mScrollToTop = true;
                    mScrollY = recyclerView.computeVerticalScrollOffset();
                    firstItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    llm.scrollToPositionWithOffset(mPostContents != null ? 1 + mPostContents.size() : 1, 0);
                    llm.setStackFromEnd(false);
                }
                break;
            case R.id.refresh://没有网络时，中间的刷新按钮
                init();
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_DETAIL://获得文章详情
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    networkError.setVisibility(View.GONE);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mArticle = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Article.class);
                        mHeadViewHolder.articleTitle.setText(mArticle.getTitle());
                        if (noData.getVisibility() == View.VISIBLE) {
                            noData.setVisibility(View.GONE);
                        }

                        int size = 1;//小图
                        int netLoad = SharedPreferencesUtils.getIntValue(SettingActivity.NET_LOAD, 0);
                        if (netLoad == 0 || CommonUtils.getNetWorkType(this) == NETWORKTYPE_WIFI) {
                            size = 2;//大图
                        }
                        String js = "javascript:_renderRich('" + mArticle.getArticle().getContent() + "','" + CommonUtils.getFontSize() + "','" + size + "')";//
                        runOnUiThread(() -> {
                            mHeadViewHolder.webView.loadUrl(js);//
//                                mViewSkeletonScreen.hide();//隐藏骨骼图
                        });

//                        getRelatedPosts();//相关推荐
//                        getComments(0);//获取评论列表
//                        articleVoices();//朗读文章   此三个接口调用等WebView加载完成onPageFinished再执行
                        if (mArticle.getAuthor_info() != null && mArticle.getAuthor_info().getMerchant() != null) {
                            if (!TextUtils.isEmpty(mArticle.getAuthor_info().getMerchant().getLogo())) {
                                Uri uri = Uri.parse(mArticle.getAuthor_info().getMerchant().getLogo());
                                int width = CommonUtils.dip2px(50);
                                int height = width;
                                ImageLoader.showThumb(uri, mHeadViewHolder.organizationLogo, width, height);
                                ImageLoader.showThumb(uri, organizationLogo, width, height);
                            }
                            mHeadViewHolder.organization.setText(mArticle.getAuthor_info().getMerchant().getName());//作者/组织名称
                            organization.setText(mArticle.getAuthor_info().getMerchant().getName());//作者/组织名称
                        }
                        mHeadViewHolder.organizationLogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mArticle.getAuthor_info() != null && mArticle.getAuthor_info().getMerchant() != null) {
                                    Intent it = new Intent(ArticleDetailActivity02.this, WebViewActivity.class);
                                    it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + mArticle.getAuthor_info().getMerchant().getId());
                                    startActivity(it);
                                }
                            }
                        });
                        organizationLogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mArticle.getAuthor_info() != null && mArticle.getAuthor_info().getMerchant() != null) {
                                    Intent it = new Intent(ArticleDetailActivity02.this, WebViewActivity.class);
                                    it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + mArticle.getAuthor_info().getMerchant().getId());
                                    startActivity(it);
                                }
                            }
                        });
                        mHeadViewHolder.time.setText(TimeUtil.formatDateTime(mArticle.getPublish_at()));
                        if (mArticle.getIs_follow() == 1) {
                            mHeadViewHolder.attention.setText("已关注");
                            mHeadViewHolder.attention.setActivated(false);
                            attentionIcon.setText(getResources().getString(R.string.attention_yes));
                            attention.setActivated(false);
                        } else {
                            mHeadViewHolder.attention.setText("关注");
                            mHeadViewHolder.attention.setActivated(true);
                            attentionIcon.setText(getResources().getString(R.string.attention_no));
                            attention.setActivated(true);
                        }
                        mHeadViewHolder.attention.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else {
                                    if (mArticle != null && mArticle.getIs_follow() == 0) {
                                        attention(true);
                                    } else if (mArticle != null) {
                                        attention(false);
                                    }
                                }
                            }
                        });
                        attention.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else {
                                    if (mArticle != null && mArticle.getIs_follow() == 0) {
                                        attention(true);
                                    } else if (mArticle != null) {
                                        attention(false);
                                    }
                                }
                            }
                        });
                        mHeadViewHolder.playTimes.setText(String.format(Locale.CHINESE, "%s人阅读", CommonUtils.getShowNumber(mArticle.getView())));
                        mHeadViewHolder.praiseSum.setActivated(mArticle.getIs_likes() == 0);
                        mHeadViewHolder.praiseSum.setText(String.valueOf(mArticle.getLike()));
                        if (mArticle.getIs_collection() == 0) {
                            mHeadViewHolder.collect.setActivated(true);
                            mHeadViewHolder.collect.setText("收藏");
                        } else {
                            mHeadViewHolder.collect.setActivated(false);
                            mHeadViewHolder.collect.setText("已收藏");
                        }
                        mHeadViewHolder.praiseLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    //startActivityForResult(new Intent(ArticleDetailActivity02.this, NewLoginActivity.class), REQUEST_LOGIN);
                                    Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else {
                                    like(mArticle.getIs_likes() == 0);//点赞/取消赞
                                }
                            }
                        });
                        mHeadViewHolder.collectLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (User.getInstance() == null) {
                                    //startActivityForResult(new Intent(ArticleDetailActivity02.this, NewLoginActivity.class), REQUEST_LOGIN);
                                    Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else {
                                    if (mArticle.getIs_collection() == 0) {
                                        collect(true);
                                    } else {
                                        collect(false);
                                    }
                                }
                            }
                        });
                    } else if (jsonObject != null && jsonObject.getIntValue("code") == 403001) {
                        //资源不存在
                        noData.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_GET_VOICES://朗读文章
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mArticleVoicesInfo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ArticleVoicesInfo.class);
                        if (mArticleVoicesInfo != null && mArticleVoicesInfo.getVoices().size() > 0) {
                            mHeadViewHolder.playVoice.setVisibility(View.VISIBLE);
                            mHeadViewHolder.playVoice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isPlaying) {
                                        isPlaying = !isPlaying;
                                        if (firstTime) {
                                            firstTime = false;
                                            if (mArticleVoicesInfo != null && mArticleVoicesInfo.getVoices().size() > 0) {
                                                ArticleVoicesInfo.VoicesBean vb = mArticleVoicesInfo.getVoices().get(0);
                                                List<AudioInfo> audioInfos = new ArrayList<>();
                                                for (int i = 0; vb != null && vb.getAudio() != null && i < vb.getAudio().size(); i++) {
                                                    ArticleVoicesInfo.VoicesBean.AudioBean audio = vb.getAudio().get(i);
                                                    AudioInfo audioInfo = new AudioInfo();
                                                    audioInfo.setId(audio.getId());
                                                    audioInfo.setIndex(i + 1);
                                                    audioInfo.setIsRadio(2);
                                                    audioInfo.setArticleId(mArticleId);
                                                    audioInfo.setVideo_id(audio.getVideo_id());
                                                    audioInfos.add(audioInfo);
                                                }
                                                try {
                                                    AudioPlayerService.sPlayOrder = PLAY_ORDER;
                                                    mAudioPlayer.playAudio(audioInfos.get(0));
                                                    mAudioPlayer.setPlayerList(audioInfos);
                                                } catch (RemoteException ignore) {
                                                }
                                                if (!FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                                                    if (MyApplication.getInstance().remindPermission) {
                                                        MyApplication.getInstance().remindPermission = false;
                                                        showFloatWindowPermission();
                                                    }
                                                }
                                            }
                                        } else {
                                            try {
                                                mAudioPlayer.start();
                                            } catch (RemoteException ignore) {
                                            }
                                        }
                                    } else {
                                        isPlaying = !isPlaying;
                                        try {
                                            mAudioPlayer.pause();
                                        } catch (RemoteException ignore) {
                                        }
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_GET_RELATED_POST: {//获取相关文章
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        mPostContents = JSONObject.parseArray(jsonObject.getJSONArray("data").toJSONString(), ColumnContent.class);
                        Iterator<ColumnContent> iterator = mPostContents.iterator();
                        while (iterator.hasNext()) {
                            ColumnContent columnContent = iterator.next();
                            if (columnContent.getType() != 1 &&
                                    columnContent.getType() != 2 &&
                                    columnContent.getType() != 3 &&
                                    columnContent.getType() != 4 &&
                                    columnContent.getType() != 5 &&
                                    columnContent.getType() != 7) {
                                iterator.remove();
                            }
                        }
                        mAdapter.setPostContents(mPostContents);//相关推荐
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHeadViewHolder.divide_line.setVisibility(View.VISIBLE);//网页加载完成才显示分割线，以免出现闪屏问题
            }
            break;
            case MSG_GET_COMMENTS: {// 获取评论列表
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    int totalNumber = jsonObject.getJSONObject("data").getInteger("comment_total");
                    mAdapter.setTotalComments(totalNumber);//总评论数字
                    commentNumber.setText(String.valueOf(totalNumber));
                    List<Comment> comments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);
                    if (msg.arg1 == 0) {
                        mComments = comments;
                    } else if (msg.arg1 == 1) {
                        if (refreshLayout.getState() == RefreshState.Loading) {
                            if (comments == null || comments.size() == 0) {
                                refreshLayout.finishLoadMoreWithNoMoreData();
                            } else {
                                refreshLayout.finishLoadMore();
                            }
                        }
                        mComments.addAll(comments);
                    }
                    if (mComments == null || mComments.size() == 0) {
                        refreshLayout.setEnableLoadMore(false);
                    }
                    mAdapter.setComments(mComments);//文章评论
                    mAdapter.setOnPraise(new ArticleRecyclerAdapter.OnPraise() {
                        @Override
                        public void praiseItem(Comment comment, View v) {
                            if (User.getInstance() != null) {
                                praise(String.valueOf(comment.getId()), v);
                            } else {
                                //startActivityForResult(new Intent(ArticleDetailActivity02.this, NewLoginActivity.class), REQUEST_LOGIN);
                                Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                startActivityForResult(it, REQUEST_LOGIN);
                            }
                        }

                        @Override
                        public void deleteItem(final Comment comment, View v) {
//                            new CircleDialog.Builder()
//                                    .setCanceledOnTouchOutside(false)
//                                    .setCancelable(false)
//                                    .setText("确认删除此评论?")
//                                    .setNegative("取消", null)
//                                    .setPositive("确定", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            deleteComment(comment);
//                                        }
//                                    })
//                                    .show(getSupportFragmentManager());

                            new QMUIDialog.MessageDialogBuilder(ArticleDetailActivity02.this)
                                    //.setTitle("确认删除此评论?")
                                    .setMessage("确认删除此评论？")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setSkinManager(QMUISkinManager.defaultInstance(ArticleDetailActivity02.this))
                                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {
                                            deleteComment(comment);
                                            dialog.dismiss();
                                        }
                                    })
                                    .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

                        }
                    });
                    mAdapter.setOnItemReply(new ArticleRecyclerAdapter.OnItemReply() {
                        @Override
                        public void replyItem(final Comment comment) {
                            if (User.getInstance() == null) {
                                Intent it = new Intent(ArticleDetailActivity02.this, NewLoginActivity.class);
                                it.putExtra("bindPhone", true);
                                it.putExtra("jump_url", ServerInfo.h5IP + ServerInfo.getArticlePage + mArticleId);
                                startActivityForResult(it, REQUEST_LOGIN);
                            } else if (TextUtils.isEmpty(User.getInstance().getPhone())) {
                                Intent it = new Intent(ArticleDetailActivity02.this, BindPhoneActivity.class);
                                //it.putExtra("hasLogined",true);
                                startActivity(it);
                            } else {
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
                                        .setNegative(getString(android.R.string.cancel), null)
                                        .configPositive(new ConfigButton() {
                                            @Override
                                            public void onConfig(ButtonParams params) {
                                                //按钮字体颜色
                                                params.textColor = CommonUtils.getThemeColor(ArticleDetailActivity02.this);
                                            }
                                        })
                                        .setPositiveInput("发表", new OnInputClickListener() {
                                            @Override
                                            public void onClick(String text, View v) {
                                                if (TextUtils.isEmpty(text)) {
                                                    ToastUtils.show("请输入内容");
                                                } else {
                                                    CommonUtils.closeInputMethod(ArticleDetailActivity02.this);
                                                    //发送评论
                                                    writeComments(text, String.valueOf(comment.getId()), String.valueOf(comment.getId()));
                                                    mCommentDialog.dismiss();
                                                }
                                            }
                                        })
                                        .show(getSupportFragmentManager());
                            }
                        }
                    });
                }
            }
            break;
            case MSG_DELETE_COMMENT://删除评论
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("删除成功");
                        getComments(0);
                    }
                } catch (Exception ignore) {
                }
                break;
            case MSG_WRITE_COMMENTS: {//写评论
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    ToastUtils.show("发表成功");
                    getComments(0);
                }
            }
            break;
            case MSG_PRAISE://点赞
                try {
                    String result = msg.getData().getString("response");
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        TextView textView = (TextView) msg.obj;
                        if (jsonObject.getInteger("data") == 1) {
                            //取消点赞
                            textView.setActivated(true);
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) - 1));
                        } else {
                            //点赞成功
                            textView.setActivated(false);
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
                        }
                        //getComments();
                    }
                } catch (Exception ignore) {
                }
                break;
            case MSG_ATTENTION_CALLBACK://关注
                try {
                    String result = (String) msg.obj;
                    boolean follow = msg.arg1 == 1;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (follow) {
                            mHeadViewHolder.attention.setText("已关注");
                            mArticle.setIs_follow(1);
                            mHeadViewHolder.attention.setActivated(false);
                            attentionIcon.setText(getResources().getString(R.string.attention_yes));
                            //attention.setText("已关注");
                            mArticle.setIs_follow(1);
                            attention.setActivated(false);
                        } else {
                            mHeadViewHolder.attention.setText("关注");
                            mArticle.setIs_follow(0);
                            mHeadViewHolder.attention.setActivated(true);
                            attentionIcon.setText(getResources().getString(R.string.attention_no));
                            //attention.setText("关注");
                            mArticle.setIs_follow(0);
                            attention.setActivated(true);
                        }
                    }
                } catch (Exception ignore) {
                }
                break;
            case MSG_LIKE_CALLBACK://点赞
                try {
                    String result = (String) msg.obj;
                    boolean like = msg.arg1 == 1;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (like) {
                            //praiseIcon.setTextColor(CommonUtils.getThemeColor(this));
                            mArticle.setIs_likes(1);
                            mArticle.setLike(mArticle.getLike() + 1);
                            mHeadViewHolder.praiseSum.setText(String.valueOf(mArticle.getLike()));
                            mHeadViewHolder.praiseSum.setActivated(false);
                        } else {
                            //praiseIcon.setTextColor(getResources().getColor(R.color.textColorEleven));
                            mArticle.setIs_likes(0);
                            mArticle.setLike(mArticle.getLike() - 1);
                            mHeadViewHolder.praiseSum.setText(String.valueOf(mArticle.getLike()));
                            mHeadViewHolder.praiseSum.setActivated(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_COLLECT_CALLBACK://收藏
                try {
                    String result = (String) msg.obj;
                    boolean collect = msg.arg1 == 1;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show(jsonObject.getString("msg"));
                        if (collect) {
                            mArticle.setIs_collection(1);
                            mHeadViewHolder.collect.setActivated(false);
                            mHeadViewHolder.collect.setText("已收藏");
                        } else {
                            mArticle.setIs_collection(0);
                            mHeadViewHolder.collect.setActivated(true);
                            mHeadViewHolder.collect.setText("收藏");
                        }
                    }
                } catch (Exception ignore) {
                }
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            getComments(0);
        } else if (requestCode == REQUEST_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (FloatWindowUtil.getInstance().checkFloatWindowPermission()) {
                    //FloatWindowUtil.getInstance().showFloatWindow();
                } else {
                    //不显示悬浮窗 并提示
                }
            }
        } else if (requestCode == REQUEST_REPORT && resultCode == Activity.RESULT_OK) {
            if (mArticle != null) {
                mArticle.setIs_user_report(1);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPosterShare() {//分享海报
        SharePosterDialog dialog = SharePosterDialog.getInstance(mArticle);
        dialog.setShareHandler(new SharePosterDialog.ShareHandler() {
            @Override
            public void onShare(String platform, Bitmap bitmap) {
                showShareImage(platform, bitmap);
            }

            @Override
            public void download(Bitmap bitmap) {
                final Bitmap saveBitmap = bitmap;
//获取写文件权限
                RxPermissions rxPermissions = new RxPermissions(ArticleDetailActivity02.this);
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) throws Exception {
                                if (granted) {
                                    Random rand = new Random();
                                    int randNum = rand.nextInt(1000);
                                    File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), CommonUtils.getCurrentTimeString() + randNum + ".png");
                                    CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                    ToastUtils.show("图片保存成功");
//发送广播更新相册
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(tempFile);
                                    intent.setData(uri);
                                    ArticleDetailActivity02.this.sendBroadcast(intent);
                                } else {
                                    ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                                }
                            }
                        });
            }
        });
        dialog.show(getSupportFragmentManager(), "ShareDialog");
    }

    private void showShareImage(String platform, final Bitmap bitmap) {
        final Bitmap saveBitmap = bitmap;
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
        final String childPath = CommonUtils.getCurrentTimeString() + randNum + ".png";
        if (QQ.NAME.equals(platform)) {
//获取写文件权限
            RxPermissions rxPermissions = new RxPermissions(ArticleDetailActivity02.this);
            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                final File tempFile = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_PICTURES), childPath);
                                CommonUtils.saveBitmapToPNG(tempFile.getAbsolutePath(), saveBitmap);
                                //ToastUtils.show("图片保存成功");
//发送广播更新相册
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri uri = Uri.fromFile(tempFile);
                                intent.setData(uri);
                                ArticleDetailActivity02.this.sendBroadcast(intent);
// oks.setImagePath(filePath);
                                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                    //自定义分享的回调想要函数
                                    @Override
                                    public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
                                        paramsToShare.setShareType(Platform.SHARE_IMAGE);
                                        // paramsToShare.setImageData(bitmap);
                                        paramsToShare.setImagePath(tempFile.getAbsolutePath());
                                    }
                                });
                            } else {
                                ToastUtils.show("未能获取相关权限，功能可能不能正常使用");
                            }
                        }
                    });
        } else {
            oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                //自定义分享的回调想要函数
                @Override
                public void onShare(Platform platform, final Platform.ShareParams paramsToShare) {
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
        oks.show(this);
    }

    private void showShare(String platform, final String title, final String desc, final String logo, final String url) {//分享至微信/QQ等
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
        oks.show(this);
    }

    public void shareStatistics(String channel, String postId, String shardUrl) {//未使用
        String url = ServerInfo.serviceIP + ServerInfo.shareStatistics;
        Map<String, String> params = new HashMap<>();
        if (User.getInstance() != null) {
            params.put("user_id", String.valueOf(User.getInstance().getId()));
        }
        params.put("channel", channel);
        params.put("post_id", postId);
        params.put("source_type", "3");
        params.put("type", "1");
        params.put("shard_url", shardUrl);
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Log.i("test", response);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                Log.i("test", exception.toString());
            }
        });
    }

    private void getArticleDetail() {//文章详情
        String url = ServerInfo.serviceIP + ServerInfo.getArticleDetail + mArticleId;
//        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_DETAIL;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        mViewSkeletonScreen.hide();//隐藏骨骼图
                        networkError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void articleVoices() {//朗读文章
        String url = ServerInfo.serviceIP + ServerInfo.articleVoices + mArticleId;
//        Map<String, String> params = new HashMap<>();
        OkHttpUtils.get(url, null, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_VOICES;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void collect(final boolean collect) {//收藏
        String url = ServerInfo.serviceIP + ServerInfo.collect01;
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(mArticleId));
        if (collect) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_COLLECT_CALLBACK;
                msg.arg1 = collect ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void like(final boolean like) {//点赞
        String url = ServerInfo.serviceIP + ServerInfo.like;
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(mArticleId));
        if (like) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        OkHttpUtils.post(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_LIKE_CALLBACK;
                msg.arg1 = like ? 1 : 0;
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, Exception exception) {
            }
        });
    }

    private void showFloatWindowPermission() {//悬浮窗权限
        FloatWindowUtil.getInstance().addOnPermissionListener(new FloatWindowUtil.OnPermissionListener() {
            @Override
            public void showPermissionDialog() {
                FRDialog dialog = new FRDialog.MDBuilder(ArticleDetailActivity02.this)
                        .setTitle("是否显示悬浮播放器")
                        .setMessage("要显示悬浮播放器，需要开启悬浮窗权限")
                        .setPositiveContentAndListener("现在去开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                }
                                return true;
                            }
                        }).setNegativeContentAndListener("暂不开启", new FRDialogClickListener() {
                            @Override
                            public boolean onDialogClick(View view) {
                                return true;
                            }
                        }).create();
                dialog.show();
            }
        });
        FloatWindowUtil.getInstance().setPermission();
    }

    private class InsideWebChromeClient extends WebChromeClient {//WebChromeClient
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mCustomView.setBackgroundColor(Color.parseColor("#000000"));
            root.addView(mCustomView);
            mCustomViewCallback = callback;
            mHeadViewHolder.webView.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onHideCustomView() {
            mHeadViewHolder.webView.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            root.removeView(mCustomView);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onHideCustomView();
        }
    }

    private class InsideWebViewClient extends WebViewClient {//WebViewClient

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) {
                String suffix = url.substring(url.lastIndexOf(".")/*, url.length() - 1*/);
                if (suffix.equalsIgnoreCase(".pdf") || suffix.equalsIgnoreCase(".doc") || suffix.equalsIgnoreCase(".docx") || suffix.equalsIgnoreCase(".wps")/*|| url.endsWith(".ppt") || url.endsWith(".pptx") || url.endsWith(".xls") || url.endsWith(".xlsx")*/) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException ignore) {
                        ToastUtils.show("未找到安装的浏览器");
                    }
                    return false;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (view.getProgress() == 100) {
                getArticleDetail();//文章详情

                getRelatedPosts();//相关推荐
                articleVoices();//朗读文章
                getComments(0);//获取评论列表
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHeadViewHolder.webView != null) mHeadViewHolder.webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHeadViewHolder.webView != null) mHeadViewHolder.webView.onPause();
    }

//    @Override
//    public void onBackPressed() {
//        if (mHeadViewHolder.webView != null && mHeadViewHolder.webView.canGoBack()) {
//            mHeadViewHolder.webView.goBack();
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    protected void doOnBackPressed() {
        if (mHeadViewHolder.webView != null && mHeadViewHolder.webView.canGoBack()) {
            mHeadViewHolder.webView.goBack();
            return;
        }
        super.doOnBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mHeadViewHolder.webView != null) {
            ViewParent parent = mHeadViewHolder.webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mHeadViewHolder.webView);
            }
            mHeadViewHolder.webView.removeAllViews();
            mHeadViewHolder.webView.destroy();
            mHeadViewHolder.webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).fitsSystemWindows(false).init();
                break;
            case Configuration.ORIENTATION_PORTRAIT: {//竖屏
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).fitsSystemWindows(true).init();
                break;
            }
        }
    }
}