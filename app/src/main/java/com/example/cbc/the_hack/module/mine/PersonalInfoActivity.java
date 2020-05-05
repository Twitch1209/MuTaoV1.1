package com.example.cbc.the_hack.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.library.view.LoadingDialog;
import com.example.cbc.library.view.MoeToast;
import com.example.cbc.the_hack.common.util.ContentUtil;
import com.example.cbc.the_hack.common.util.ImageUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.dialog.EditTextDialog;
import com.example.cbc.the_hack.entity.ImageResponseEntity;
import com.example.cbc.the_hack.entity.UserInfo;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

/**
 * 用户资料
 */
public class PersonalInfoActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_CUT = 456;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.person_img)
    ImageView mPersonImg;
    @BindView(R.id.person_name)
    TextView mPersonName;
    @BindView(R.id.person_signature)
    TextView mUserSignature;
    @BindView(R.id.person_sex)
    TextView mUserSex;

    private Integer mUserId;
    private String saveName;
    private String mImagePath;

    private LoadingDialog loadingProgress;

    // 用户更新的参数
    private String username;
    private String phone;
    private Integer sex;
    private String avatar;
    private String signature;

    // 是否为更新头像
    private boolean isUpdateAvatar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_personal_info)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        loadingProgress = new LoadingDialog(this, R.string.dialog_update_avatar);

        int x = (int) (Math.random() * 5) + 1;
        if (x == 1) {
            MoeToast.makeText(this, R.string.egg_who_is_there);
        }

        mUserId = SPUtil.build().getInt(Constants.SP_USER_ID);
        saveName = SPUtil.build().getString(Constants.SP_USER_NAME);
        mPersonName.setText(saveName);
        postUserInfo();
    }

    @OnClick({R.id.person_img, R.id.person_name,R.id.person_sex,R.id.person_signature})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_img:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(PersonalInfoActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.person_sex:
                String sexValue;
                if(sex==-1){
                    sexValue = this.getString(R.string.hint_confidential);
                }else if(sex==0){
                    sexValue = this.getString(R.string.user_value_female);
                }else {
                    sexValue = this.getString(R.string.user_value_male);
                }
                EditTextDialog sexDialog = EditTextDialog.newInstance("修改性别", sexValue, 20);
                sexDialog.show(getSupportFragmentManager(), "editSex");
                sexDialog.setPositiveListener(new EditTextDialog.PositiveListener() {
                    @Override
                    public void Positive(String value) {
                        if (value.equals(getResources().getString(R.string.user_value_female))
                                ||value.equals(getResources().getString(R.string.user_value_male))
                                ||value.equals(getResources().getString(R.string.hint_confidential))) {
                            setSex(value);
                            if(value.equals(getResources().getString(R.string.user_value_female))){
                                sex = 0;
                            }else if (value.equals(getResources().getString(R.string.user_value_male))){
                                sex = 1;
                            }else {
                                sex =-1;
                            }
                            postUpdateUserInfo();
                        }
                        else {
                            showToast("错误的格式");
                        }
                    }
                });
                break;
            case R.id.person_name:
                EditTextDialog usernameDialog = EditTextDialog.newInstance("修改用户名", saveName, 20);
                usernameDialog.show(getSupportFragmentManager(), "editUserName");
                usernameDialog.setPositiveListener(new EditTextDialog.PositiveListener() {
                    @Override
                    public void Positive(String value) {
                        if (!TextUtils.isEmpty(value) && !saveName.equals(value)) {
                            username = value;
                            setName(value);
                            postUpdateUserInfo();
                            SPUtil.build().putString(Constants.SP_USER_NAME,username);
                        }else{
                            showToast("错误的格式");
                        }
                    }
                });
                break;
            case R.id.person_signature:
                EditTextDialog signatureDialog = EditTextDialog.newInstance("修改签名", signature, 50);
                signatureDialog.show(getSupportFragmentManager(), "editSignature");
                signatureDialog.setPositiveListener(new EditTextDialog.PositiveListener() {
                    @Override
                    public void Positive(String value) {
                        setSignature(value);
                        signature = value==null?"":value;
                        postUpdateUserInfo();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        // 图片选择
        if (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                String photo = photos.get(0);
                Uri uri = ImageUtil.getFileUri(this, new File(photo));
                String imagePath = ImageUtil.getImagePath();
                mImagePath = imagePath;
                int size = 240;
                Intent intent = ImageUtil.callSystemCrop(uri, imagePath, size);
                startActivityForResult(intent, PHOTO_REQUEST_CUT);
            }
        }

        // 图片裁剪
        if (requestCode == PHOTO_REQUEST_CUT) {
            ContentUtil.loadAvatar(mPersonImg, mImagePath);
            postUserImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取用户信息
     */
    private void postUserInfo() {
        OkUtil.get()
                .url(Api.userInfoUidMidSecurity)
                .addUrlParams("uid", mUserId.toString())
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        if ("200".equals(response.getCode())) {
                            setUserInfo(response.getData());
                        } else {
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onBackPressed();
                    }
                });
    }

    /**
     * 上传用户头像
     */
    private void postUserImage() {
        File file = new File(mImagePath);
        if (!file.exists()) {
            showUserImageUpdateError();
            return;
        }
        OkUtil.post()
                .url(Api.uploadUserImage)
                .addFile("smfile", file)
                .addHeader("Authorization","YwuGXPC7h7E01RdwJRQW5q5LXFuDjjOX")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63")
                .setProgressDialog(loadingProgress)
                .execute(new ResultCallback<ImageResponseEntity>() {
                    @Override
                    public void onSuccess(ImageResponseEntity response) {
                        String code = response.getCode();
                        switch (code){
                            case "success":
                                Map<String,Object> photos = response.getData();
                                avatar = (String)photos.get("url");
                                isUpdateAvatar = true;
                                break;
                            case "image_repeated":
                                avatar = response.getImages();
                                isUpdateAvatar = true;
                                break;
                            default:
                                showUserImageUpdateError();
                                return;
                        }
                        postUpdateUserInfo();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showUserImageUpdateError();
                    }
                });
    }

    /**
     * 更新用户信息
     */
    private void postUpdateUserInfo() {
        OkUtil.post()
                .url(Api.updateUser)
                .addParam("uid", mUserId)
                .addParam("username", username)
                .addParam("avatar", avatar==null?"":avatar)
                .addParam("phone",phone)
                .addParam("sex",sex)
                .addParam("signature",signature==null?"":signature)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        if ("200".equals(response.getCode())) {
                            showUserUpdateSuccess();
                            setUserInfo(response.getData());
                        } else {
                            showUserUpdateError();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showUserUpdateError();
                    }
                });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo(UserInfo userInfo) {
        if (isUpdateAvatar) {
            isUpdateAvatar = false;
            notifyUpdateUserImage();
            File file = new File(mImagePath);
            if (file.exists()) {
                boolean delete = file.delete();
                Log.d(TAG, "setUserInfo: delete file " + delete);
            }
        }
        avatar = userInfo.getAvatar()==null?"":userInfo.getAvatar();
        ContentUtil.loadUserAvatar(mPersonImg, avatar);
        phone = userInfo.getPhone();
        sex = userInfo.getSex();
        signature = userInfo.getSignature()==null?"":userInfo.getSignature();
        username = userInfo.getUsername();
        mUserSignature.setText(signature);
        if(sex==0){
            mUserSex.setText(R.string.user_value_female);
        }
        if(sex==1){
            mUserSex.setText(R.string.user_value_male);
        }
        if(sex==-1){

        }
        if (!TextUtils.isEmpty(userInfo.getUsername())) {
            mPersonName.setText(userInfo.getUsername());
        }
    }

    /**
     * 通知更新用户头像
     */
    private void notifyUpdateUserImage() {
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction(Constants.UPDATE_USER_IMG);
        sendBroadcast(intent);
    }

    /**
     * 设置性别
     */
    private void setSex(String value){
        mUserSex.setText(value);
    }
    /**
     * 设置昵称
     */
    private void setName(String value){
        mPersonName.setText(value);
    }
    /**
     * 设置昵称
     */
    private void setSignature(String value){
        mUserSignature.setText(value);
    }
    /**
     * 提示头像修改失败
     */
    private void showUserImageUpdateError() {
        showToast("更新头像失败");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateSuccess() {
        showToast("更新用户信息成功");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateError() {
        showToast("更新用户信息失败");
    }
}
