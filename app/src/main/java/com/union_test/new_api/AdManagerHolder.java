package com.union_test.new_api;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.api.init.PAGConfig;
import com.bytedance.sdk.openadsdk.api.init.PAGSdk;
import com.union_test.internationad.R;

/**
 * You can use a singleton to save the TTFAdManager instance, and call it when you need to initialize sdk
 */
public class AdManagerHolder {

    public static boolean sInit;

    private static final String TAG = "AdManagerHolder";

    private static PangleSdkHasInitSuccess pangleSdkHasInitSuccess;

    //step1: Initialize sdk
    public static void doInitNewApi(Context context) {
        if (!sInit) {
            PAGConfig pAGInitConfig = buildNewConfig();
            PAGSdk.init(context, pAGInitConfig, new PAGSdk.PAGInitCallback() {
                @Override
                public void success() {
                    sInit = true;
                    Log.i(TAG, "PAGInitCallback new api init success: ");
                    if (pangleSdkHasInitSuccess != null){
                        pangleSdkHasInitSuccess.initSuccess();
                        pangleSdkHasInitSuccess = null;
                    }
                }

                @Override
                public void fail(int code, String msg) {
                    Log.i(TAG, "PAGInitCallback new api init fail: " + code);
                }
            });
        }
    }

    private static PAGConfig buildNewConfig() {
        return new PAGConfig.Builder()
                .appId(RitConstants.AD_APP_ID)
                .appIcon(R.mipmap.app_icon)
                .debugLog(true)
                .supportMultiProcess(false)
                .build();
    }

    public interface PangleSdkHasInitSuccess {
        void initSuccess();
    }

    public static void setPangleSdkHasInitSuccess(PangleSdkHasInitSuccess sdkHasInitSuccess) {
        pangleSdkHasInitSuccess = sdkHasInitSuccess;
    }
}
