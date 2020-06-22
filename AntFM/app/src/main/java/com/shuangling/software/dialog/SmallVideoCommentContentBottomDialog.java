package com.shuangling.software.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.shuangling.software.R;
import com.shuangling.software.activity.AlivcLittleVideoActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.CommentDetailActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.adapter.CommentRecyclerViewAdapter;
import com.shuangling.software.adapter.LevelTwoCommentAdapter;
import com.shuangling.software.adapter.LittleVideoCommentRecyclerAdapter;
import com.shuangling.software.customview.FontIconView;
import com.shuangling.software.customview.XCSlideView;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.CommentBean;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.Constant;
import com.shuangling.software.utils.KeyBordUtil;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by 你是我的 on 2019/1/7.
 */

public class SmallVideoCommentContentBottomDialog extends DialogFragment
        implements View.OnClickListener, Handler.Callback {


    public static final String TAG = "SmallVideoComment";

    public static final int MSG_GET_VIDEO_DETAIL = 0x00;

    public static final int MSG_COLLECT_CALLBACK = 0x01;

    public static final int MSG_UPDATE_STATUS = 0x02;

    public static final int MSG_GET_RELATED_POST = 0x03;

    public static final int MSG_GET_COMMENTS = 0x04;


    public static final int MSG_PRAISE = 0x05;

    public static final int MSG_WRITE_COMMENTS = 0x06;

    public static final int MSG_ATTENTION_CALLBACK = 0x07;

    public static final int MSG_LIKE_CALLBACK = 0x08;

    public static final int REQUEST_LOGIN = 0x09;

    private static final int SHARE_SUCCESS = 0xa;

    private static final int SHARE_FAILED = 0xb;

    private static final int MSG_DELETE_COMMENT = 0xc;

    private static final int MSG_GET_VIDEO_AUTH = 0xd;

    public static final int MSG_GET_COMMENTS_REPLY = 0x11;//获取评论详情

    @BindView(R.id.commentNumber)
    TextView commentNumber;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    private BottomSheetBehavior<FrameLayout> behavior;
    RecyclerView recyclerView;
    RelativeLayout layoutCommentInput;
    ImageView ivExitCommentContentDialog;
    List<CommentBean> commentList = new ArrayList<>();
    CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    LinearLayoutManager manager;
    CommentDialog commentDialog;


    View mContentView;
    // 顶部向下偏移量
    private int topOffest = 610;
    final Handler mHandler = new Handler(this);


    LittleVideoCommentRecyclerAdapter mAdapter;
    private int currentPage = 1;
    private Comment currentComment;
    private int mVideoId;
    private List<Comment> mComments = new ArrayList<>();


    //下面这些是评论详情相关的参数
    ViewGroup mContainer;
    XCSlideView mCommentDetailSlideView; //滑动View 加载详情页面
    RecyclerView mDetailRecyclerView;
    FontIconView mDetailIvExit;
    RelativeLayout  mDetailBottomLayout;
    Boolean  mIsDetailCommentViewShow = false;
    int mCommentId;  //当前点的评论id
    int mScrollToCommentId ;
    private List<Comment> mReplyComment;
    private LevelTwoCommentAdapter mCommentListAdapter;
    private Comment mComment;



    public void setVideoId(int videoId) {
        mVideoId = videoId;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        return new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.small_video_comment_content_bottom_dialog, container, false);
        unbinder = ButterKnife.bind(this, mContentView);
        initView(mContentView);
        int theme = getTheme();
        getComments(0);
        return mContentView;
    }

    private void initView(View view) {

        mAdapter = new LittleVideoCommentRecyclerAdapter(getContext());
        mContainer = view.findViewById(R.id.container);

        recyclerView = view.findViewById(R.id.recycler_view_small_video_comment_content);
        layoutCommentInput = view.findViewById(R.id.layout_small_video_comment_input);
        ivExitCommentContentDialog = view.findViewById(R.id.iv_exit_comment_content);


        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(commentList, getActivity());
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        commentDialog = new CommentDialog();

        if (getTag().equals("DialogAndInput")) {// 判断是否是要同时打开该dialog和软键盘
            commentDialog.show(getChildFragmentManager(), "smallVideosoftInput");
            commentDialog.setSendListener(new CommentDialog.SendListener() {
                @Override
                public void sendComment(String inputText) {
                    writeComments(inputText, "" + "0", "" + "0");
                    commentDialog.dismiss();
                }
            },null);
        }
        //关掉Dialog时关掉键盘
        commentDialog.setDismissListener(new CommentDialog.DismissListener() {
            @Override
            public void onDismiss() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyBordUtil.hideInput((getActivity()), mContentView);
                    }
                }, 50);
            }
        });


        ivExitCommentContentDialog.setOnClickListener(this);
        // ivExpressionEmojiInput.setOnClickListener(this);
        layoutCommentInput.setOnClickListener(this);


        //当内容很多向上滑动时，取消Dialog的下滑退出，当滑动到顶部，恢复下滑退出Dialog
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int currState = -1;
            Handler mHandler = new Handler();
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    currState = -1;
                    recyclerView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0f, 0f, 0));
                }
            };

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currState = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.i(TAG, "--------------------------------------");
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                Log.i(TAG, "firstCompletelyVisibleItemPosition: " + firstCompletelyVisibleItemPosition);
                if (firstCompletelyVisibleItemPosition == 0) {
                    Log.i(TAG, "滑动到顶部");
                    behavior.setHideable(true);
                } else {
                    behavior.setHideable(false);
                }


                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.i(TAG, "lastCompletelyVisibleItemPosition: " + lastCompletelyVisibleItemPosition);
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    Log.i(TAG, "滑动到底部");
                }


//如果在持续滚动中，则取消回调
                mHandler.removeCallbacks(mRunnable);
//当前RecyclerView在滚动设置到某个位置的动画状态，代码调用时或者惯性滚动时就是这个状态
                if (currState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mHandler.postDelayed(mRunnable, 20);
                }
            }
        });
//
//

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getComments(1);
            }
        });

        initDetailView();

    }


    private void initDetailView() {
        mCommentDetailSlideView = XCSlideView.create(getContext(), mContainer, XCSlideView.Positon.RIGHT);  // new XCSlideView(getContext());
        mCommentDetailSlideView.attachToParentView(XCSlideView.Positon.RIGHT);
        View detailView = LayoutInflater.from(getActivity()).inflate(R.layout.small_video_comment_detail_dialog, null);
        mCommentDetailSlideView.setMenuView(detailView);
        mCommentDetailSlideView.setMenuWidth(ScreenUtils.getWidth(getActivity()));

        mDetailBottomLayout = detailView.findViewById(R.id.layout_bottom);
        mDetailRecyclerView = detailView.findViewById(R.id.recycler_view_small_video_comment_content);
        mDetailIvExit =  detailView.findViewById(R.id.iv_exit_comment_content);
        mDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //退出详情
        mDetailIvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentDetailSlideView.dismiss();
                mIsDetailCommentViewShow = false;
            }
        });

        //输入
        mDetailBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.show(getChildFragmentManager(), "smallVideosoftInput");
                commentDialog.setSendListener(new CommentDialog.SendListener() {
                    @Override
                    public void sendComment(String inputText) {
                        writeComments(inputText, "" + mComment.getId(), "" + mComment.getId());
                        commentDialog.dismiss();
                    }
                },null);
            }
        });


    }

    private void addSubView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mContainer.addView(mCommentDetailSlideView, params);
    }


    @Override
    public void onStart() {
        super.onStart();
        // 设置软键盘不自动弹出
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getHeight();
            behavior = BottomSheetBehavior.from(bottomSheet);
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private int getHeight() {
        int height = 1920;
        if (getContext() != null) {
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (windowManager != null) {
                // 使用point已经减去了状态栏的距离
                windowManager.getDefaultDisplay().getSize(point);
                height = point.y - getTopOffest();
            }
        }
        return height;
    }

    private int getTopOffest() {
        return topOffest;
    }

    public void setTopOffest(int topOffest) {
        this.topOffest = topOffest;
    }

    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit_comment_content:
                dismiss();
//                getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case R.id.layout_small_video_comment_input:

                //判断是否登录
                if (User.getInstance() == null) {
                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                    it.putExtra("bindPhone", true);
                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                    startActivityForResult(it, REQUEST_LOGIN);
                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                    startActivity(it);
                } else {
                    commentDialog.show(getChildFragmentManager(), "smallVideosoftInput");
                    commentDialog.setSendListener(new CommentDialog.SendListener() {
                        @Override
                        public void sendComment(String inputText) {
                            writeComments(inputText, "" + "0", "" + "0");
                            commentDialog.dismiss();
                        }
                    },null);
                }

                break;
        }
    }


    //<editor-fold desc="请求相关">
    //type  0 正常   1 加载更多
    private void getComments(final int type) {
        if (type == 0) {
            currentPage = 1;
        }
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mVideoId);
        params.put("page", "" + currentPage);
        params.put("page_size", "" + Constant.PAGE_SIZE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

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


    private void deleteComment(Comment comment) {


        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + comment.getId());

        OkHttpUtils.delete(url, params, new OkHttpCallback(getContext()) {

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


    public void collect(final boolean collect) {

        String url = ServerInfo.serviceIP + ServerInfo.collect01;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mVideoId);
        if (collect) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

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


    public void like(final boolean like) {

        String url = ServerInfo.serviceIP + ServerInfo.like;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mVideoId);
        if (like) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

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




    private void writeComments(String content, String parentId, String topCommentId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mVideoId);
        params.put("type", "1");
        params.put("content", content);
        params.put("parent_id", parentId);
        params.put("base_comment_id", topCommentId);
        params.put("source_type", "3");
        OkHttpUtils.post(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_WRITE_COMMENTS;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {

                ToastUtils.show("发表失败，网络错误");
            }
        });
    }


    private void praise(Comment comment, final View view) {
        String url = ServerInfo.serviceIP + ServerInfo.praise;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + comment.getId());
        currentComment = comment;
        OkHttpUtils.put(url, params, new OkHttpCallback(getContext()) {

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


    private void praise(String commentId, final View view) {
        String url = ServerInfo.serviceIP + ServerInfo.praise;
        Map<String, String> params = new HashMap<>();
        params.put("id", commentId);

        OkHttpUtils.put(url, params, new OkHttpCallback(getContext()) {

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



    private void getCommentsReply() {
        if(mIsDetailCommentViewShow == false){
            return;
        }
        String url = ServerInfo.serviceIP + ServerInfo.commentReplay;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mCommentId);
        params.put("page", "" + 1);
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(getContext()) {

            @Override
            public void onResponse(Call call, String response) throws IOException {

                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS_REPLY;
                msg.obj = response;
                mHandler.sendMessage(msg);


            }

            @Override
            public void onFailure(Call call, Exception exception) {


            }
        });
    }

    //</editor-fold>




    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case MSG_GET_COMMENTS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);

                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                        currentPage++;

                        int totalNumber = jsonObject.getJSONObject("data").getInteger("total");
                        //commentNum.setText("(" + totalNumber+")");
                        mAdapter.setTotalComments(totalNumber);

                        if(totalNumber>0){
                            commentNumber.setText(totalNumber + "条评论");
                        }else{
                            commentNumber.setText("");
                        }


                        List<Comment> comments = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONArray("data").toJSONString(), Comment.class);

                        if (msg.arg1 == 0) {
                            mComments = comments;
                        } else if (msg.arg1 == 1) {

                            if (refreshLayout.getState() == RefreshState.Loading) {

                                if (comments == null || comments.size() == 0) {
                                    //refreshLayout.setEnableLoadMore(false);
                                    // refreshLayout.finishLoadMoreWithNoMoreData();
                                    refreshLayout.finishLoadMore();
                                } else {
                                    refreshLayout.finishLoadMore();
                                }
                            }

                            mComments.addAll(comments);
                        }

                        if (mComments == null || mComments.size() == 0) {
                            refreshLayout.setEnableLoadMore(false);
                        }
                        mAdapter.setComments(mComments);

                        mAdapter.setOnPraise(new LittleVideoCommentRecyclerAdapter.OnPraise() {
                            @Override
                            public void praiseItem(Comment comment, View view) {
                                if (User.getInstance() != null) {
                                    praise(comment, view);
                                } else {
                                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                    startActivityForResult(it, REQUEST_LOGIN);
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
                                        .show(getChildFragmentManager());
                            }
                        });

                        mAdapter.setOnItemReply(new LittleVideoCommentRecyclerAdapter.OnItemReply() {
                            @Override
                            public void replyItem(final Comment comment) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                                    it.putExtra("bindPhone", true);
                                    it.putExtra("jump_url", ServerInfo.h5IP + "/videos/" + mVideoId);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                                    startActivity(it);
                                } else {

                                    commentDialog.show(getChildFragmentManager(), "smallVideosoftInput");
                                    commentDialog.setSendListener(new CommentDialog.SendListener() {
                                        @Override
                                        public void sendComment(String inputText) {
                                            writeComments(inputText, "" + comment.getId(), "" + comment.getId());
                                            commentDialog.dismiss();
                                        }
                                    },comment.getUser().getNickname());
                                }
                            }
                        });


                        mAdapter.setOnItemDetail(new LittleVideoCommentRecyclerAdapter.OnItemDetail() {
                            @Override
                            public void itemDetail(int commentId, int scrollToCommentId) {
                                mCommentId = commentId;
                                mScrollToCommentId = scrollToCommentId;
                                mIsDetailCommentViewShow = true;
                                getCommentsReply();
                                mCommentDetailSlideView.show();
                                mDetailRecyclerView.setVisibility(View.INVISIBLE);

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case MSG_DELETE_COMMENT:
                try {

                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("删除成功");
                        getComments(0);
                        getCommentsReply();
                    }

                } catch (Exception e) {

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
                            int num = Integer.parseInt(textView.getText().toString()) - 1;
                            currentComment.setLike_count(num);
                            currentComment.setFabulous(0);
                            textView.setText("" + num);

                        } else {
                            //点赞成功
                            textView.setActivated(false);
                            int num = Integer.parseInt(textView.getText().toString()) + 1;
                            textView.setText("" + num);
                            currentComment.setLike_count(num);
                            currentComment.setFabulous(1);
                        }
                        //getComments();

                    }

                } catch (Exception e) {

                }
                break;
            case MSG_WRITE_COMMENTS:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("发表成功");
                        getComments(0);
                        getCommentsReply();

                    }else{
                        String message =  jsonObject.getString("msg");
                        if(message != null) {
                            ToastUtils.show("发表失败" + message);
                        }else{
                            ToastUtils.show("发表失败");
                        }
                    }
                } catch (Exception e) {

                }
                break;
            case MSG_GET_COMMENTS_REPLY:
                String result = (String) msg.obj;
                Log.d(TAG,result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {

                    mComment = JSONObject.parseObject(jsonObject.getJSONObject("data").getJSONObject("chief").toJSONString(), Comment.class);
                    mReplyComment = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONObject("reply").getJSONArray("data").toJSONString(), Comment.class);
                    recyclerView.setHasFixedSize(true);

                    if (mCommentListAdapter == null) {
                        mCommentListAdapter = new LevelTwoCommentAdapter(getContext(), mReplyComment);
                        mCommentListAdapter.setTopComment(mComment);
                        if(mScrollToCommentId!=-1){
                            mCommentListAdapter.setScrollToCommentId(mScrollToCommentId);
                        }
                        mCommentListAdapter.setOnItemReply(new LevelTwoCommentAdapter.OnItemReply() {
                            @Override
                            public void replyItem(final Comment comment) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                                    startActivity(it);
                                }else if (User.getInstance() !=null&&TextUtils.isEmpty(User.getInstance().getPhone())) {

                                    Intent it = new Intent(getContext(), BindPhoneActivity.class);
                                    //it.putExtra("hasLogined",true);
                                    startActivity(it);
                                } else {
                                    commentDialog.show(getChildFragmentManager(), "smallVideosoftInput");
                                    commentDialog.setSendListener(new CommentDialog.SendListener() {
                                        @Override
                                        public void sendComment(String inputText) {
                                            writeComments(inputText, "" + mComment.getId(), "" + mComment.getId());
                                            commentDialog.dismiss();
                                        }
                                    },comment.getUser().getNickname());
                                }
                            }
                        });

                        mCommentListAdapter.setOnPraise(new LevelTwoCommentAdapter.OnPraise() {
                            @Override
                            public void praiseItem(Comment comment, View view) {
                                if (User.getInstance() != null) {
                                    praise("" + comment.getId(), view);
                                } else {
                                    Intent it = new Intent(getContext(), NewLoginActivity.class);
                                    startActivity(it);
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
                                        .show(getChildFragmentManager());
                            }

                            @Override
                            public void scrollToPosition(int position) {

                                ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                            }
                        });
                        mDetailRecyclerView.setAdapter(mCommentListAdapter);

                    } else {
                        mCommentListAdapter.setTopComment(mComment);
                        mCommentListAdapter.updateView(mReplyComment);
                    }

                    mDetailRecyclerView.setVisibility(View.VISIBLE);

                }
                break;
        }
        return false;
    }


}
