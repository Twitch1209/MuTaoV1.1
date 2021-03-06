package com.example.cbc.the_hack.module.member;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.library.view.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.cbc.R;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.entity.UserInfo;
import okhttp3.Call;

/**
 * 忘记密码重置
 */
public class ResetPwdActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.phone)
    EditText mPhone;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.do_password)
    EditText mDoPassword;

    private LoadingDialog updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_reset_pwd)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        updateProgress = new LoadingDialog(this, R.string.dialog_loading_reset_wd);
    }

    public void goUpdatePwd(View view) {
        String uName = mUsername.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
        }
        if (uPhone.length() != 11) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        postUpdatePwd(uName, uPwd, uPhone);
    }

    public void postUpdatePwd(String userName, String userPwd, String phone) {
        OkUtil.post()
                .url(Api.resetPassword)
                .addParam("username", userName)
                .addParam("password", userPwd)
                .addParam("phone", phone)
                .setProgressDialog(updateProgress)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        String code = response.getCode();
                        switch (code) {
                            case "200":
                                showToast(R.string.toast_reset_ped_success);
                                onBackPressed();
                                break;
                            default:
                                showToast(response.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showToast(R.string.toast_reset_pwd_error);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (updateProgress.isShowing()) {
            updateProgress.dismiss();
        }
        super.onDestroy();
    }
}
