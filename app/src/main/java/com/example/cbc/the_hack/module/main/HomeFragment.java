package com.example.cbc.the_hack.module.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.cbc.library.base.BaseFragment;
import com.example.cbc.the_hack.common.util.SPUtil;
import com.example.cbc.the_hack.module.callback.PhotoCallBack;
import com.example.cbc.the_hack.module.util.FileUtils;
import com.example.cbc.the_hack.module.util.NaviDebug;
import com.example.cbc.the_hack.module.util.OkHttp3Util;
import com.example.cbc.the_hack.module.view.AlertView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.text_view)
    ViewFlipper textView;
    @BindView(R.id.text_view_under)
    ViewFlipper textViewUnder;
    @BindView(R.id.progress_view)
    CircularProgressView progressView;
    @BindView(R.id.btn_change)
    ImageButton photoButton;
    @BindView(R.id.iv_avater)
    ImageView ivAvater;
    @BindView(R.id.home_linear)
    LinearLayout linearLayout;


    public PhotoCallBack callBack;
    public String path = "";
    private String upload_api = "http://47.103.21.70";
    public Uri photoUri;
    private File file;


    private static final int TAKE_PICTURE = 0;
    private static OkHttpClient okHttpClient = null;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int CUT_PHOTO_REQUEST_CODE = 2;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    private static final String TYPE = "type";

    private String mType;
    private String mImageUrl;
    private boolean openTuPics;

    private Context context;

    public HomeFragment(Context context) {
        this.context = context;
    }

    public static HomeFragment newInstance(String newsType, Context context) {
        HomeFragment fragment = new HomeFragment(context);
        Bundle args = new Bundle();
        args.putString(TYPE, newsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        openTuPics = SPUtil.build().getBoolean("open_tu_pics");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        textView.setInAnimation(AnimationUtils.loadAnimation(context,R.anim.push_in));
        textViewUnder.setInAnimation(AnimationUtils.loadAnimation(context,R.anim.push_inl));
        textView.showPrevious();
        textViewUnder.showPrevious();

        photoButton.setOnClickListener(v -> changeAvater());
        linearLayout.setBackgroundResource(R.drawable.bg_home);

    }


    private void uploadPicture() throws IOException {
        if (path == "") {
            Toast.makeText(this.getContext(), "未传入图片", Toast.LENGTH_LONG).show();
            //Intent intent0=new Intent(MainActivity2.this,SecondActivity.class);
            //Bundle bundle=new Bundle();
            //bundle.putString("response","sdadsadsadasd");
            //intent0.putExtras(bundle);
            //startActivity(intent0);
            return;
        }
        File file = new File(path);
        HashMap<String, String> map = new HashMap<>();
        map.put("token", "1234");
        Log.d("---", "uploadPicture: 22222");

        progressView.setVisibility(View.VISIBLE);
        photoButton.setClickable(false);


        OkHttp3Util.uploadFile(upload_api, file, "hhhhh.jpg", map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("+++", "onFailure: " + e.getMessage());
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    progressView.setVisibility(View.GONE);
                    photoButton.setClickable(true);
                    Toast.makeText(context, "服务器无响应!请稍后再试!", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
//                    Intent intent0 = new Intent(context, MainActivity2.class);
//                    Bundle bundle = new Bundle();
//                    String s = response.body().string();
//                    bundle.putString("response", s);
//                    intent0.putExtras(bundle);
//                    getActivity().runOnUiThread(() -> {
//                        progressView.setVisibility(View.GONE);
//                        photoButton.setClickable(true);
//                    });
//                    startActivity(intent0);
                } else {
                    Looper.prepare();
                    Toast.makeText(context, "服务器无响应!请稍后再试!", Toast.LENGTH_LONG).show();
                    String log = response.body().toString();
                    NaviDebug naviDebug = NaviDebug.getInstance();
                    naviDebug.saveLog(log);
                    getActivity().runOnUiThread(() -> {
                        progressView.setVisibility(View.GONE);
                        photoButton.setClickable(true);
                    });
                    Looper.loop();
                }
            }
        });
    }

    private void changeAvater() {
        callBack = new PhotoCallBack() {
            @Override
            public void doSuccess(String path) {
                //将图片传给服务器
                //File file = new File(path);
                //RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                /*MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)//表单类型
                        .addFormDataPart("id", SaveUserInfo.getUid());
                builder.addFormDataPart("head_portrait", file.getName(), imageBody);
                List<MultipartBody.Part> partList=builder.build().parts();
                mPresenter.startUpdateHeadRequest(partList,SaveUserInfo.getUid());*/
            }

            @Override
            public void doError() {

            }
        };
        comfireImgSelection(context, ivAvater);
    }

    // 拍照
    public void comfireImgSelection(Context context, ImageView my_info) {
        ivAvater = my_info;
        new AlertView(null, null, "取消", null, new String[]{"从手机相册选择", "拍照", "上传"}, getActivity(), AlertView.Style.ActionSheet,
                (o, position) -> {
                    if (position == 0) {
                        // 从相册中选择
                        if (checkPermission(READ_EXTERNAL_STORAGE)) {
                            Intent i = new Intent(
                                    // 相册
                                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                        } else {//申请拍照权限和读取权限
                            startRequestrReadPermision();
                        }
                    } else if (position == 1) {
                        // 拍照
                        Log.d("hei", "Here: *************0");
                        if (checkPermission(CAMERA_PERMISSION)) {
                            Log.d("hei", "Here: *************1");
                            photo();
                        } else {//申请拍照权限和读取权限
                            Log.d("hei", "Here: *************2");
                            startRequestPhotoPermision();
                        }
                    } else if (position == 2) {
                        Log.d("hei", "Here: *************3");
                        uploadPicture();
                    }
                }).show();
    }

    private void startRequestrReadPermision() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)//多个权限用","隔开
                .subscribe(aBoolean -> {
                            if (aBoolean) {
                                //当所有权限都允许之后，返回true
                                Intent i = new Intent(
                                        // 相册
                                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            } else {
                                //只要有一个权限禁止，返回false，
                                //下一次申请只申请没通过申请的权限
                                return;
                            }
                        }
                );
    }

    private void startRequestPhotoPermision() {
        //请求多个权限
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)//多个权限用","隔开
                .subscribe(aBoolean -> {
                            if (aBoolean) {
                                //当所有权限都允许之后，返回true
                                photo();
                            } else {
                                //只要有一个权限禁止，返回false，
                                //下一次申请只申请没通过申请的权限
                                return;
                            }
                        }
                );
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.e("checkPermission", "PERMISSION_GRANTED" + ContextCompat.checkSelfPermission(context, permission));
                return true;
            } else {
                Log.e("checkPermission", "PERMISSION_DENIED" + ContextCompat.checkSelfPermission(context, permission));
                return false;
            }
        } else {
            Log.e("checkPermission", "M以下" + ContextCompat.checkSelfPermission(context, permission));
            return true;
        }
    }

    public void photo() {
        Log.d("testp", "photo: 1");

        try {
            Log.d("testp", "photo: 1");
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String sdcardState = Environment.getExternalStorageState();
            String sdcardPathDir = Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
            file = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                // 有sd卡，是否有myImage文件夹
                File fileDir = new File(sdcardPathDir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                // 是否有headImg文件
                long l = System.currentTimeMillis();
                file = new File(sdcardPathDir + l + ".JPEG");
            }
            if (file != null) {
                path = file.getPath();
                photoUri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24) {
                    photoUri = FileProvider.getUriForFile(context, "com.example.cbc.the_hack.fileProvider", file);
                } else {
                    photoUri = Uri.fromFile(file);
                }
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (file != null && file.exists())
                    startPhotoZoom(photoUri);
                break;
            case RESULT_LOAD_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        startPhotoZoom(uri);
                    }
                }
                break;
            case CUT_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK && null != data) {// 裁剪返回
                    if (path != null && path.length() != 0) {
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        //给头像设置图片源
                        ivAvater.setImageBitmap(bitmap);
                        textViewUnder.setVisibility(View.GONE);
                        //if (callBack != null)
                        //callBack.doSuccess(path);
                    }
                }
                break;
        }
    }

    private void startPhotoZoom(Uri uri) {
        try {
            // 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String address = sDateFormat.format(new Date());
            if (!FileUtils.isFileExist("")) {
                FileUtils.createSDDir("");
            }

            Uri imageUri = Uri.parse("file:///sdcard/formats/" + address + ".JPEG");
            final Intent intent = new Intent("com.android.camera.action.CROP");

            // 照片URL地址
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);// 去黑边
            intent.putExtra("scaleUpIfNeeded", true);// 去黑边
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 输出格式
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", false);
            intent.putExtra("return-data", false);
            intent.putExtra("fileurl", FileUtils.SDPATH + address + ".JPEG");
            path = FileUtils.SDPATH + address + ".JPEG";
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
