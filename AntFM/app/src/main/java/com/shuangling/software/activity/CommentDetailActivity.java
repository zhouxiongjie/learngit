package com.shuangling.software.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.adapter.CommentListAdapter;
import com.shuangling.software.adapter.LevelTwoCommentAdapter;
import com.shuangling.software.customview.TopTitleBar;
import com.shuangling.software.entity.Comment;
import com.shuangling.software.entity.User;
import com.shuangling.software.network.OkHttpCallback;
import com.shuangling.software.network.OkHttpUtils;
import com.shuangling.software.utils.ServerInfo;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

@EnableDragToClose()
public class CommentDetailActivity extends AppCompatActivity implements Handler.Callback {

    public static final int MSG_GET_COMMENTS = 0x0;

    public static final int MSG_WRITE_COMMENTS = 0x1;

    public static final int MSG_PRAISE = 0x2;

    public static final int MSG_DELETE_COMMENT=0x3;

    @BindView(R.id.activity_title)
    TopTitleBar activityTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.writeComment)
    TextView writeComment;
    @BindView(R.id.commentNumLayout)
    FrameLayout commentNumLayout;
    @BindView(R.id.bottom)
    LinearLayout bottom;

    private int mCommentId;
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
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        mHandler = new Handler(this);
        mCommentId = getIntent().getIntExtra("commentId", 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new MyItemDecoration());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        getComments();
    }


    private void getComments() {


        String url = ServerInfo.serviceIP + ServerInfo.getLevelTwoComentList;
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
        Map<String,String> params=new HashMap<>();
        params.put("id",""+comment.getId());

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


                    if (mCommentListAdapter == null) {
                        mCommentListAdapter = new LevelTwoCommentAdapter(this, mReplyComment);
                        mCommentListAdapter.setTopComment(mComment);
                        mCommentListAdapter.setOnItemReply(new LevelTwoCommentAdapter.OnItemReply() {
                            @Override
                            public void replyItem(final Comment comment) {
                                if (User.getInstance() == null) {
                                    Intent it = new Intent(CommentDetailActivity.this, LoginActivity.class);
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
                                            .setPositiveInput("发表", new OnInputClickListener() {
                                                @Override
                                                public void onClick(String text, View v) {
                                                    if (TextUtils.isEmpty(text)) {
                                                        ToastUtils.show("请输入内容");
                                                    } else {
                                                        mCommentDialog.dismiss();
                                                        //发送评论
                                                        writeComments(text, "" + comment.getId(), "" + mComment.getId());

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
                                    Intent it = new Intent(CommentDetailActivity.this, LoginActivity.class);
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
                                        .show(getSupportFragmentManager());
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

                    String result = (String)msg.obj;
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
            Intent it = new Intent(this, LoginActivity.class);
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
                    .setPositiveInput("发表", new OnInputClickListener() {
                        @Override
                        public void onClick(String text, View v) {
                            if (TextUtils.isEmpty(text)) {
                                ToastUtils.show("请输入内容");
                            } else {
                                mCommentDialog.dismiss();
                                //发送评论
                                writeComments(text, "" + mComment.getId(), "" + mComment.getId());

                            }
                        }
                    })
                    .show(getSupportFragmentManager());
        }
    }
}
