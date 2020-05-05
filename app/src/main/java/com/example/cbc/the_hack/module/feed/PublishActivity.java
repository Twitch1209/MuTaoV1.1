package com.example.cbc.the_hack.module.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.the_hack.adapter.PhotoSelAdapter;
import com.example.cbc.the_hack.common.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.example.cbc.the_hack.entity.Feed;
import com.example.cbc.the_hack.module.main.MainActivity;

import org.json.JSONObject;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.feed_info)
    AppCompatEditText mMoodInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private Integer mUid;
    private String mInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle("发布新动态")
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        mUid = SPUtil.build().getInt(Constants.SP_USER_ID);
        setLoading("发布中...");
        initRecycleView();
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos);
        mRecyclerView.setAdapter(mPhotoSelAdapter);
        mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                if (mPhotos.get(position).equals(PhotoSelAdapter.mPhotoAdd)) {
                    mPhotos.remove(position);
                    PhotoPicker.builder()
                            .setPhotoCount(6)
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setSelected((ArrayList<String>) mPhotos)
                            .setPreviewEnabled(false)
                            .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
                } else {
                    mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
                    PhotoPreview.builder()
                            .setPhotos((ArrayList<String>) mPhotos)
                            .setCurrentItem(position)
                            .setShowDeleteButton(true)
                            .start(PublishActivity.this);
                }
            }

            @Override
            public void onDelete(int position) {
                mPhotos.remove(position);
                mPhotoSelAdapter.setPhotos(mPhotos);
            }
        });
    }

    @OnClick(R.id.iv_submit)
    public void onClick() {
        mInfo = mMoodInfo.getText().toString().trim();
        if (TextUtils.isEmpty(mInfo)) {
            showToast("好歹写点什么吧！");
            return;
        }
        if (mPhotos.size() <= 1) {
            postSaveFeed(mPhotos);
        } else {
            postUpload(mPhotos);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PhotoPicker.REQUEST_CODE:
                    case PhotoPreview.REQUEST_CODE:
                        mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        break;
                }
            }
        }
        mPhotoSelAdapter.setPhotos(mPhotos);
    }

    // 上传图片
    private void postUpload(List<String> photos) {
        removePhotoAdd(photos);

        // 压缩图片
        photos = ImageUtil.compressorImage(this, photos);
        List<String> photoUrlList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS).build();
        boolean oneFail = false;

        for (File photoFile: ImageUtil.pathToImageFile(photos)) {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("smfile", "image",
                            RequestBody.create(MediaType.parse("image/*"), photoFile))
                    .build();
            Request request = new Request.Builder()
                    .url("https://sm.ms/api/v2/upload")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("Authorization", "ZNtF4YN9va6lBsSKpg31PKlkOCZIcEsC")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    photoUrlList.add(data.getString("url"));
                } else {
                    oneFail = true;
                    break;
                }
            } catch (Exception e) {
                oneFail = true;
                break;
            }
        }
        if (oneFail) {
            showToast("图片上传失败");
            addPhotoAdd(mPhotos);
        } else {
            postSaveFeed(photoUrlList);
        }
    }

    // 发布动态
    private void postSaveFeed(List<String> uploadImg) {
        removePhotoAdd(uploadImg);
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("uid", mUid)
                .addParam("content", mInfo)
                .addUrlParams("imageList", uploadImg)
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        dismissLoading();
                        String code = response.getCode();
                        if (!"200".equals(code)) {
                            showToast("发布失败");
                            addPhotoAdd(mPhotos);
                            return;
                        }
                        mMoodInfo.setText(null);
                        showToast("发布成功");
                        onBackPressed();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        dismissLoading();
                        showToast("发布失败");
                        addPhotoAdd(mPhotos);
                    }
                });
    }

    // 添加添加图片按钮
    private void addPhotoAdd(List<String> photList) {
        if (!photList.contains(PhotoSelAdapter.mPhotoAdd)) {
            photList.add(PhotoSelAdapter.mPhotoAdd);
        }
    }

    // 去除添加图片按钮
    private void removePhotoAdd(List<String> photList) {
        photList.remove(PhotoSelAdapter.mPhotoAdd);
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_camera);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
    }
}
