package com.example.cbc.the_hack.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;
import com.example.cbc.the_hack.adapter.PublicLicenseAdapter;
import com.example.cbc.the_hack.module.webview.WebActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.cbc.R;

import com.example.cbc.the_hack.entity.PublicLicense;

public class PublicLicenseActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<PublicLicense> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_license_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_public_license)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        getData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        PublicLicenseAdapter mAdapter = new PublicLicenseAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new PublicLicenseAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, PublicLicense license) {
                gotoWeb(license.getName(), license.getUrl());
            }
        });
    }

    /**
     * 初始化数据
     */
    private void getData() {
        mData.add(new PublicLicense("okhttp", "square",
                "An HTTP+HTTP/2 client for Android and Java applications.",
                "https://github.com/square/okhttp"));
        mData.add(new PublicLicense("gson", "google",
                "A Java serialization/deserialization library to convert Java Objects into JSON and back",
                "https://github.com/google/gson"));
        mData.add(new PublicLicense("Tujian", "open.dpic.dev",
                "无人为孤岛，一图一世界。",
                "https://open.dpic.dev"));
        mData.add(new PublicLicense("butterknife", "JakeWharton",
                "Bind Android views and callbacks to fields and methods",
                "https://github.com/JakeWharton/butterknife"));
        mData.add(new PublicLicense("glide", "bumptech",
                "An image loading and caching library for Android focused on smooth scrolling",
                "https://github.com/bumptech/glide"));
        mData.add(new PublicLicense("glide-transformations", "wasabeef",
                "An Android transformation library providing a variety of image transformations for Glide.",
                "https://github.com/wasabeef/glide-transformations"));
        mData.add(new PublicLicense("Compressor", "zetbaitsu",
                "An android image compression library.",
                "https://github.com/zetbaitsu/Compressor"));
        mData.add(new PublicLicense("PhotoPicker", "donglua",
                "Image Picker like Wechat",
                "https://github.com/donglua/PhotoPicker"));
    }

    // 前往web页
    private void gotoWeb(String tittle, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
