package com.shuangling.software.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.LevelTwoCommentAdapter;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

//@EnableDragToClose()
public class CommentDetailActivity extends QMUIActivity/*AppCompatActivity*/ implements Handler.Callback {
    public static final int MSG_GET_COMMENTS = 0x0;
    public static final int MSG_WRITE_COMMENTS = 0x1;
    public static final int MSG_PRAISE = 0x2;
    public static final int MSG_DELETE_COMMENT = 0x3;
    @BindView(R.id.activity_title)
    /*TopTitleBar*/ QMUITopBarLayout activityTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.writeComment)
    TextView writeComment;
    @BindView(R.id.commentNumLayout)
    FrameLayout commentNumLayout;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    private int mCommentId;
    private int mScrollToCommentId;
    private Handler mHandler;
    private Comment mComment;
    private List<Comment> mReplyComment;
    private LevelTwoCommentAdapter mCommentListAdapter;
    private DialogFragment mCommentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getInstance().getCurrentTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
//        CommonUtils.transparentStatusBar(this);
        ButterKnife.bind(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this); //
        activityTitle.addLeftImageButton(R.drawable.ic_left, com.qmuiteam.qmui.R.id.qmui_topbar_item_left_back).setOnClickListener(view -> { //
            finish();
        });
        activityTitle.setTitle("评论详情");
        init();
    }

    private void init() {
        mHandler = new Handler(this);
        mCommentId = getIntent().getIntExtra("commentId", 0);
        mScrollToCommentId = getIntent().getIntExtra("scrollToCommentId", -1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        getComments();
    }

    private void getComments() {
        String url = ServerInfo.serviceIP + ServerInfo.commentReplay;
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + mCommentId);
        params.put("page", "" + 1);
        params.put("page_size", "" + Integer.MAX_VALUE);
        OkHttpUtils.get(url, params, new OkHttpCallback(this) {
            @Override
            public void onResponse(Call call, String response) throws IOException {
                Message msg = Message.obtain();
                msg.what = MSG_GET_COMMENTS;
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

    private void writeComments(String content, String parentId, String topCommentId) {
        String url = ServerInfo.serviceIP + ServerInfo.getComentList;
        Map<String, String> params = new HashMap<>();
        params.put("post_id", "" + mComment.getPost_id());
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
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GET_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    mComment = JSONObject.parseObject(jsonObject.getJSONObject("data").getJSONObject("chief").toJSONString(), Comment.class);
                    mReplyComment = JSONObject.parseArray(jsonObject.getJSONObject("data").getJSONObject("reply").getJSONArray("data").toJSONString(), Comment.class);
                    recyclerView.setHasFixedSize(true);
                    if (mCommentListAdapter == null) {
                        mCommentListAdapter = new LevelTwoCommentAdapter(this, mReplyComment);
                        mCommentListAdapter.setTopComment(mComment);
                        if (mScrollToCommentId != -1) {
                            mCommentListAdapter.setScrollToCommentId(mScrollToCommentId);
                        }
                        mCommentListAdapter.setOnItemReply(new LevelTwoCommentAdapter.OnItemReply() {
                            @Override
                            public void replyItem(final Comment comment) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(CommentDetailActivity.this, NewLoginActivity.class);
                                    startActivity(it);
                                } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                                    Intent it = new Intent(CommentDetailActivity.this, BindPhoneActivity.class);
                                    //it.putExtra("hasLogined",true);
                                    startActivity(it);
                                } else {
                                    mCommentDialog = new CircleDialog.Builder()
                                            .setCanceledOnTouchOutside(false)
                                            .setCancelable(true)
                                            .setInputManualClose(true)
                                            .setTitle("发表评论")
//                        .setSubTitle("发表评论")
                                            .setInputHint("@" + comment.getUser().getNickname())
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
                                            .configPositive(new ConfigButton() {
                                                @Override
                                                public void onConfig(ButtonParams params) {
                                                    //按钮字体颜色
                                                    params.textColor = CommonUtils.getThemeColor(CommentDetailActivity.this);
                                                }
                                            })
                                            .setPositiveInput("发表", new OnInputClickListener() {
                                                @Override
                                                public void onClick(String text, View v) {
                                                    if (TextUtils.isEmpty(text)) {
                                                        ToastUtils.show("请输入内容");
                                                    } else {
                                                        CommonUtils.closeInputMethod(CommentDetailActivity.this);
                                                        //发送评论
                                                        writeComments(text, "" + comment.getId(), "" + mComment.getId());
                                                        mCommentDialog.dismiss();
                                                    }
                                                }
                                            })
                                            .show(getSupportFragmentManager());
                                }
                            }
                        });
                        mCommentListAdapter.setOnPraise(new LevelTwoCommentAdapter.OnPraise() {
                            @Override
                            public void praiseItem(Comment comment, View view) {
                                if (User.getInstance() != null) {
                                    praise("" + comment.getId(), view);
                                } else {
                                    Intent it = new Intent(CommentDetailActivity.this, NewLoginActivity.class);
                                    startActivity(it);
                                }
                            }

                            @Override
                            public void deleteItem(final Comment comment, View v) {
//                                new CircleDialog.Builder()
//                                        .setCanceledOnTouchOutside(false)
//                                        .setCancelable(false)
//                                        .setText("确认删除此评论?")
//                                        .setNegative("取消", null)
//                                        .setPositive("确定", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                deleteComment(comment);
//                                            }
//                                        })
//                                        .show(getSupportFragmentManager());

                                new QMUIDialog.MessageDialogBuilder(CommentDetailActivity.this)
                                        //.setTitle("提示")
                                        .setMessage("确认删除此评论?")
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setSkinManager(QMUISkinManager.defaultInstance(CommentDetailActivity.this))
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

                            @Override
                            public void scrollToPosition(int position) {
                                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                            }
                        });
                        recyclerView.setAdapter(mCommentListAdapter);
                    } else {
                        mCommentListAdapter.setTopComment(mComment);
                        mCommentListAdapter.updateView(mReplyComment);
                    }
                }
            }
            break;
            case MSG_DELETE_COMMENT:
                try {
                    String result = (String) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                        ToastUtils.show("删除成功");
                        getComments();
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_WRITE_COMMENTS: {
                String result = (String) msg.obj;
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
                    ToastUtils.show("发表成功");
                    getComments();
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

    @OnClick(R.id.writeComment)
    public void onViewClicked() {
        if (User.getInstance() == null) {
            Intent it = new Intent(this, NewLoginActivity.class);
            startActivity(it);
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
                    .setInputHint("@" + mComment.getUser().getNickname())
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
                    .configPositive(new ConfigButton() {
                        @Override
                        public void onConfig(ButtonParams params) {
                            //按钮字体颜色
                            params.textColor = CommonUtils.getThemeColor(CommentDetailActivity.this);
                        }
                    })
                    .setPositiveInput("发表", new OnInputClickListener() {
                        @Override
                        public void onClick(String text, View v) {
                            if (TextUtils.isEmpty(text)) {
                                ToastUtils.show("请输入内容");
                            } else {
                                CommonUtils.closeInputMethod(CommentDetailActivity.this);
                                //发送评论
                                writeComments(text, "" + mComment.getId(), "" + mComment.getId());
                                mCommentDialog.dismiss();
                            }
                        }
                    })
                    .show(getSupportFragmentManager());
        }
    }
}
