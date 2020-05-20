package com.example.cbc.the_hack.module.feed;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.the_hack.common.util.FeedContentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.cbc.R;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.entity.Feed;
import com.example.cbc.the_hack.module.main.MainActivity;
import okhttp3.Call;

/**
 * 动态发布
 */
public class ShareFeedActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.feed_info)
    AppCompatEditText mFeedInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mUid;
    private StringBuffer mInfo = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.share_text)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        mUid = SPUtil.build().getString(Constants.SP_USER_ID);
        setLoading("发布中...");
        mRecyclerView.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent == null) {
            onBackPressed();
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            onBackPressed();
            return;
        }
        if (!"text/plain".equals(intent.getType())) {
            onBackPressed();
            return;
        }
        String title = bundle.getString(Intent.EXTRA_TITLE);
        String text = bundle.getString(Intent.EXTRA_TEXT);
        if (TextUtils.isEmpty(text)) {
            onBackPressed();
            return;
        }

        if (text.contains("bilibili")) {
            mInfo.append("#bilibili#");
        }

        if (text.contains("music.163")) {
            mInfo.append("#网易云音乐#");
        }

        mInfo.append(text);
        mFeedInfo.setEnabled(false);
        mFeedInfo.setText(FeedContentUtil.getFeedText(mInfo.toString(), mFeedInfo));

        // 预留
        int i = text.indexOf("http");
        String url = text.substring(i);
        Log.d(TAG, "init: title" + title);
        Log.d(TAG, "init: url" + url);
    }

    @OnClick(R.id.iv_submit)
    public void onClick() {
        postSaveFeed();
    }

    // 发布动态
    private void postSaveFeed() {
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("uid", mUid)
                .addParam("content", mInfo.toString())
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        dismissLoading();
                        String code = response.getCode();
                        if (!"200".equals(code)) {
                            showToast("发布失败");
                            return;
                        }
                        showSuccess();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        dismissLoading();
                        showToast("发布失败");
                    }
                });
    }

    // 发布成功
    private void showSuccess() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setMessage("发布成功，是否留在本APP");
        mDialog.setNegativeButton("离开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        mDialog.setPositiveButton("留下", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoHome();
            }
        });
        mDialog.setCancelable(false).create().show();
    }

    public void gotoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_camera);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
