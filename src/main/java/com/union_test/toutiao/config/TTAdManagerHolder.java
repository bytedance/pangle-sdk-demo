package com.union_test.toutiao.config;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

/**
 * You can use a singleton to save the TTFAdManager instance, and call it when you need to initialize sdk
 */
public class TTAdManagerHolder {

    private static boolean sInit;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context) {
        doInit(context);
    }

    //step1: Initialize sdk
    private static void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context));
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context) {
        return new TTAdConfig.Builder()
                .appId("5001121")
                .useTextureView(true)// Use TextureView to play the video. The default setting is SurfaceView, when the context is in conflict with SurfaceView, you can use TextureView
                .appName("APP Test Name")
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .setGDPR(1)
                .allowShowPageWhenScreenLock(true) // Allow or deny permission to display the landing page ad in the lock screen
                .debug(true)// Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
                .supportMultiProcess(false) // true for support multi-process environment,false for single-process
                //.httpStack(new MyOkStack3())//optional,you can customize network library for sdk, the demo is based on the okhttp3.
                .coppa(0) // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
                .build();
    }
}
