package com.example.cbc.the_hack.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.view.MoeToast;

import me.cl.lingxi.R;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.ResultConstant;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.module.main.MainActivity;
import com.example.cbc.the_hack.module.member.LoginActivity;

import okhttp3.Call;

/**
 * 闪屏
 */
public class SplashActivity extends BaseActivity {

    private boolean isLogin;
    private boolean tokenNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) {
            MoeToast.makeText(this, R.string.egg_end);
        }

        isLogin = SPUtil.build().getBoolean(Constants.SP_BEEN_LOGIN);
        String token = SPUtil.build().getString(Api.X_APP_TOKEN);
        // 此版本校验token
        tokenNull = TextUtils.isEmpty(token);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin && !tokenNull) {
                    getUnRead();
                } else {
                    goLogin();
                }
            }
        }, 1500);
    }

    /**
     * 获取未读条数
     */
    public void getUnRead() {
        String userId = SPUtil.build().getString(Constants.SP_USER_ID);
        OkUtil.post()
                .url(Api.unreadComment)
                .addParam("uid", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        goHome(ResultConstant.CODE_SUCCESS.equals(response.getCode()) ? response.getData() : 0);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        goHome(0);
                    }
                });
    }

    /**
     * 前往主页
     */
    private void goHome(int num) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(Constants.PASSED_UNREAD_NUM, num);
        startActivity(intent);
        finish();
    }

    /**
     * 前往登录
     */
    private void goLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
