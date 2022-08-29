package com.union_test.new_api;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;
import com.bytedance.sdk.openadsdk.api.init.PAGSdk;
import com.union_test.new_api.PAGAppOpenAdManager;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("unused")
public class DemoApplication extends MultiDexApplication {

//    public static RefWatcher sRefWatcher = null;
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    private static final String TAG = "DemoApplication";
    public static Context CONTEXT = null;

    /**
     * app open ad
     */
    private PAGAppOpenAdManager mPAGAppOpenAdManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;

//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            sRefWatcher = LeakCanary.install(this);
//        }

        PAGSdk.addPAGInitCallback(new PAGSdk.PAGInitCallback() {
            @Override
            public void success() {
                Log.i(TAG, "PAGInitCallback success: addPAGInitCallback");
            }

            @Override
            public void fail(int code, String msg) {
                Log.i(TAG, "PAGInitCallback fail: addPAGInitCallback");
            }
        });
        initSdk();
        //app open ad
        mPAGAppOpenAdManager = new PAGAppOpenAdManager(this);
    }

    public void initSdk(){
        // It is strongly recommended to call in Application #onCreate method,
        // to avoid content as null
        AdManagerHolder.doInitNewApi(this);

    }



    /**
     * fetch an app open ad.
     */
    public void fetchAd(PAGAppOpenAdManager.RealTimeFetchListener realTimeFetchListener) {
        mPAGAppOpenAdManager.fetchAd(realTimeFetchListener);
    }

    /**
     * Shows an app open ad.
     */
    public void showAdIfAvailable(PAGAppOpenAdManager.ManagerOpenAdInteractionListener managerOpenAdInteractionListener) {
        mPAGAppOpenAdManager.showAdIfAvailable(managerOpenAdInteractionListener);
    }

}
