package com.example.cbc.the_hack.module.member;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.library.view.LoadingDialog;
import com.example.cbc.library.view.MoeToast;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.cbc.R;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.common.result.ResultConstant;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.entity.UserInfo;
import com.example.cbc.the_hack.entity.UserToken;
import com.example.cbc.the_hack.module.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    AppCompatEditText mUsername;
    @BindView(R.id.password)
    AppCompatEditText mPassword;

    private long mExitTime = 0;
    private LoadingDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_login)
                .setTitleCenter()
                .build();

        loginProgress = new LoadingDialog(this, R.string.dialog_loading_login);

        int x = (int) (Math.random() * 6) + 1;
        if (x == 5) MoeToast.makeText(this, R.string.egg_from_where);

        String saveName = SPUtil.build().getString(Constants.SP_USER_NAME);
        mUsername.setText(saveName);
        mUsername.setSelection(saveName.length());
    }

    public void login(View view) {
        String username = Objects.requireNonNull(mUsername.getText()).toString().trim();
        String password = Objects.requireNonNull(mPassword.getText()).toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast(R.string.toast_login_null);
            return;
        }
        postLogin(username, password);
    }

    // 登录请求
    private void postLogin(final String userName, String userPwd) {
        OkUtil.post()
                .url(Api.userLogin)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("grant_type","password")
                .addHeader("Authorization","Basic YW5kcm9pZDpzZWNyZXQ=")
                .setProgressDialog(loginProgress)
                .execute(new ResultCallback<UserToken>() {
                    @Override
                    public void onSuccess(UserToken response) {
                        if(response.getError()==null){
                            SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, true);
                            SPUtil.build().putString(Api.X_APP_TOKEN, response.getAccess_token());
                            SPUtil.build().putString(Api.X_REFRESH_TOKEN, response.getRefresh_token());
                            OkUtil.newInstance().addCommonHeader(Api.X_APP_TOKEN, "bearer " + response.getAccess_token());
                            postLogin(userName);
                        }else {
                            showToast(R.string.toast_pwd_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_login_error);
                    }
                });
    }

    private void postLogin(final String userName){
        OkUtil.get()
                .url(Api.userInfo)
                .addUrlParams("username", userName)
                .setProgressDialog(loginProgress)
                .execute(new ResultCallback<Result<UserInfo>>() {
                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case ResultConstant.CODE_SUCCESS:
                                UserInfo user = response.getData();
                                SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, true);
                                SPUtil.build().putInt(Constants.SP_USER_ID, user.getUid());
                                SPUtil.build().putString(Constants.SP_USER_NAME, user.getUsername());
                                goHome();
                                break;
                            default:
                                showToast(R.string.toast_pwd_error);
                                break;
                        }
                    }
                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_login_error);
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, R.string.toast_again_exit);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void updatePwd(View view) {
        Intent intent = new Intent(this, ResetPwdActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.PASSED_UNREAD_NUM, 0);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (loginProgress.isShowing()) {
            loginProgress.dismiss();
        }
        super.onDestroy();
    }
}
