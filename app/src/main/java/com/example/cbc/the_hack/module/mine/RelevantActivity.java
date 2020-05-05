package com.example.cbc.the_hack.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.library.view.LoadingDialog;
import com.example.cbc.library.view.MoeToast;
import com.example.cbc.the_hack.adapter.RelevantAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;

import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.entity.Feed;
import com.example.cbc.the_hack.entity.PageInfo;
import com.example.cbc.the_hack.entity.Relevant;
import com.example.cbc.the_hack.module.feed.FeedActivity;
import okhttp3.Call;

/**
 * 与我相关 && 我的回复
 */
public class RelevantActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private Integer saveId;
    private List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relevant_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_can_you_find);
        }

        Intent intent = getIntent();
        String replyType = intent.getStringExtra(Constants.REPLY_TYPE);
        if (TextUtils.isEmpty(replyType)) {
            onBackPressed();
            return;
        }

        boolean isMine = false;
        String title = "";
        switch (replyType) {
            case Constants.REPLY_MY:
                title = getString(R.string.title_bar_my_reply);
                isMine = true;
                break;
            case Constants.REPLY_RELEVANT:
                title = getString(R.string.title_bar_relevant);
                break;
            default:
                onBackPressed();
                break;
        }

        ToolbarUtil.init(mToolbar, this)
                .setTitle(title)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        saveId = SPUtil.build().getInt(Constants.SP_USER_ID);
        loadingProgress = new LoadingDialog(this, R.string.dialog_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(this, mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new RelevantAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Relevant relevant) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.feed_body:
                        gotoFeed(relevant.getFeed());
                        break;
                }
            }
        });

        if (isMine) {
            getRelevantList(Api.mineReply);
        } else {
            getRelevantList(Api.relevant);
            updateUnread();
        }
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        Integer userId = SPUtil.build().getInt(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.updateUnread)
                .addParam("uid", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    // 请求与我相关
    public void getRelevantList(String url) {
        Integer pageNum = 1;
        Integer pageSize = 20;
        OkUtil.get()
                .url(url)
                .addUrlParams("uid", saveId.toString())
                .addUrlParams("pageNum", pageNum.toString())
                .addUrlParams("pageSize", pageSize.toString())
                .setLoadDelay()
                .setProgressDialog(loadingProgress)
                .execute(new ResultCallback<Result<PageInfo<Relevant>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Relevant>> response) {
                        String code = response.getCode();
                        if (!"200".equals(code)) {
                            showToast("加载失败，下拉重新加载");
                            return;
                        }
                        updateData(response.getData().getList());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast("加载失败，下拉重新加载");
                    }
                });
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    //前往详情页
    private void gotoFeed(Feed feed) {
        Intent intent = new Intent(RelevantActivity.this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        OkUtil.newInstance().cancelAll();
        super.onDestroy();
    }
}
