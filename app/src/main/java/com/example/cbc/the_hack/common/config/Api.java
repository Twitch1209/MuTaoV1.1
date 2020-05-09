package com.example.cbc.the_hack.common.config;

import com.example.cbc.library.BuildConfig;

/**
 * api manage
 * Created by bafsj on 17/3/1.
 */
public class Api {

    /**
     * token
     */
    public static final String X_APP_TOKEN = "Authorization";
    public static final String X_REFRESH_TOKEN = "refresh_token";

    /**
     * 收束gradle的flavor控制，将url变量在此接管
     */
    private static String baseUrl = "http://10.0.2.2:10087";
    private static String imageBase = "https://sm.ms/api/v2/upload";
    public static String rssUrl = "http://47.100.245.128/rss/lingxi";

    static {
        String flavor = BuildConfig.FLAVOR;
        switch (flavor) {
            case "alpha":
                baseUrl = "http://47.100.245.128/lingxi-test";
                rssUrl = "http://47.100.245.128/rss/lingxi-test";
                break;
            case "local":
                baseUrl = "http://192.168.21.103:8090/lingxi";
                rssUrl = "http://192.168.21.103/rss/lingxi-test";
                break;
            case "online":
                baseUrl = "http://47.100.245.128/lingxi";
                rssUrl = "http://47.100.245.128/rss/lingxi";
                break;
        }
    }

    /**
     * 画中有诗
     */
    public static String imageToPoem = baseUrl + "/search-service/search/poem";
    /**
     * 用户注册
     */
    public static String userRegister = baseUrl + "/user-service/register";
    /**
     * 用户登录
     */
    public static String userLogin = baseUrl + "/oauth-service/oauth/token";
    /**
     * 重置密码
     */
    public static String resetPassword = baseUrl + "/user-service/reset-password";
    /**
     * 用户注册
     */
    public static String refreshToken = baseUrl + "/oauth-service/oauth/token";
    /**
     * 更新用户信息
     */
    public static String updateUser = baseUrl + "/user-service/update-user";
    /**
     * 获取用户信息username
     */
    public static String userInfo = baseUrl + "/user-service/get-user-username";
    /**
     * 获取用户信息uid,不加密手机号
     */
    public static String userInfoUidMidSecurity = baseUrl + "/user-service/get-user-uid-m";
    /**
     * 获取用户信息uid
     */
    public static String userInfoUid = baseUrl + "/user-service/get-user-uid";
    /**
     * 查询用户信息
     */
    public static String searchUser = baseUrl + "/user-service/get-user-username";
    /**
     * 融云用户列表
     */
    public static String listRcUser = baseUrl + "/user/rc/list";
    /**
     * 动态列表
     */
    public static String pageFeed = baseUrl + "/society-service/post/page";
    /**
     * 用户个人动态列表
     */
    public static String pageUserFeed = baseUrl + "/society-service/post/user";
    /**
     * 发布动态
     */
    public static String saveFeed = baseUrl + "/society-service/post/publish";
    /**
     * 查看动态
     */
    public static String viewFeed = baseUrl + "/society-service/post/view";
    /**
     * 与我相关
     */
    public static String relevant = baseUrl + "/society-service/post/related";
    /**
     * 我的回复
     */
    public static String mineReply = baseUrl + "/society-service/post/my-reply";
    /**
     * 新增动态操作,如点赞
     */
    public static String saveAction = baseUrl + "/society-service/post-action/add";
    /**
     * 移除动态操作,如取消赞
     */
    public static String removeAction = baseUrl + "/society-service/post-action/delete";
    /**
     * 动态评论列表
     */
    public static String pageComment = baseUrl + "/society-service/comment/page";
    /**
     * 新增动态评论
     */
    public static String saveComment = baseUrl + "/society-service/comment/publish";
    /**
     * 获取最新app版本
     */
    public static String latestVersion = baseUrl + "/app/version/latest";
    /**
     * 上传用户图片
     */
    public static String uploadUserImage = imageBase;
    /**
     * 上传动态图片
     */
    public static String uploadFeedImage = baseUrl + "/rss/upload/feed/image";
    /**
     * 未读条数
     */
    public static String unreadComment = baseUrl + "/society-service/comment/get-unread";
    /**
     * 更新未读为已读
     */
    public static String updateUnread = baseUrl + "/society-service/comment/unread-update";
    /**
     * 保存写给未来
     */
    public static String saveFuture = baseUrl + "/future/save";
    /**
     * 资源采集
     */
    public static String incApi = baseUrl + "/inc/parse/api";
}
