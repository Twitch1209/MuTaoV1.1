package com.example.cbc.the_hack.module.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.library.view.LoadingDialog;
import com.example.cbc.the_hack.adapter.PhotoSelAdapter;
import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.config.Constants;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.okhttp.ResultCallback;
import com.example.cbc.the_hack.common.result.Result;
import com.example.cbc.the_hack.common.util.ImageUtil;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.entity.Feed;
import com.example.cbc.the_hack.entity.NewPoem;
import com.example.cbc.the_hack.module.main.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Callback;
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

    private Integer photoNum = 0;

    private LoadingDialog publishProgress;

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
        publishProgress = new LoadingDialog(this, R.string.dialog_publishing_post);
        NewPoem poem=new NewPoem();
        Intent intent=getIntent();
        poem=(NewPoem)intent.getSerializableExtra("poem");
        if(poem!=null){
            String poems=poem.getPoetryName()+"\n" + poem.getPoetryDynasty()+"  "
                    +poem.getPoetryAuthor()+"\n" + poem.getPoetryBody();
            mMoodInfo.setText(poems);
        }
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
        CopyOnWriteArrayList<String> photoUrlList = new CopyOnWriteArrayList<>();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        photoNum = photos.size();
        publishProgress.setMessage(R.string.dialog_publishing_image);
        publishProgress.show();

        for (File photoFile : ImageUtil.pathToImageFile(photos)) {
            Log.e(TAG, "postUpload: start one upload");
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("smfile", photoFile.getName(),
                            RequestBody.create(MediaType.parse("image/*"), photoFile))
                    .build();
            Request request = new Request.Builder()
                    .url("https://sm.ms/api/v2/upload")
                    .addHeader("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405")
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("Authorization", "ZNtF4YN9va6lBsSKpg31PKlkOCZIcEsC")
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: " + e);
                    showToast("图片上传失败");
                    addPhotoAdd(mPhotos);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        Log.d(TAG, "onResponse: " + response.code());
                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                Log.d(TAG, "onResponse: url: " + data.getString("url"));
                                photoUrlList.add(data.getString("url"));
                                if (photoUrlList.size() == photoNum) {
                                    showToast("图片上传成功");
                                    postSaveFeed(photoUrlList);
                                }
                            } else if (!jsonObject.getBoolean("success") &&
                                    jsonObject.getString("code").equals("image_repeated")){
                                Log.d(TAG, "onResponse: url: " + jsonObject.getString("images"));
                                photoUrlList.add(jsonObject.getString("images"));
                                if (photoUrlList.size() == photoNum) {
                                    Log.d(TAG, "onResponse: start publish");
                                    showToast("图片上传成功");
                                    hideLoadingBar();
                                    postSaveFeed(photoUrlList);
                                }
                            }
                        } else {
                            showToast("图片上传失败");
                            addPhotoAdd(mPhotos);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("图片上传失败");
                        addPhotoAdd(mPhotos);
                    }
                }
            });
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
}

    // 发布动态
    private void postSaveFeed(List<String> uploadImg) {
        publishProgress.setMessage(R.string.dialog_publishing_post);
        publishProgress.show();
        removePhotoAdd(uploadImg);
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("uid", mUid)
                .addParam("content", mInfo)
                .addUrlParams("imageList", uploadImg)
                .setProgressDialog(publishProgress)
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
        publishProgress.dismiss();
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

    private void hideLoadingBar(){
        publishProgress.dismiss();
    }
}
