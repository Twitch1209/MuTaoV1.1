package com.example.cbc.the_hack.module;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import com.example.cbc.the_hack.common.config.Api;
import com.example.cbc.the_hack.common.okhttp.OkUtil;
import com.example.cbc.the_hack.common.util.SPUtil;

public class LxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 一定要注册
        SPUtil.newInstance().init(this);
        // 根据需求使用
        initOkUtil();
    }

    /**
     * 初始化OkUtil
     */
    private void initOkUtil() {
        // 公共请求头
        Map<String, String> headers = new HashMap<>(1);
        String token = SPUtil.build().getString(Api.X_APP_TOKEN);
        headers.put(Api.X_APP_TOKEN, token);
        // 注册添加公共请求头
        OkUtil.newInstance().init(this)
                .addCommonHeaders(headers);
    }

}
