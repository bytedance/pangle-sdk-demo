package com.union_test.toutiao;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.union_test.toutiao.config.TTAdManagerHolder;

/**
 * Create by hanweiwei on 11/07/2018
 */
@SuppressWarnings("unused")
public class DemoApplication extends Application {

    public static RefWatcher sRefWatcher = null;
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    @Override
    public void onCreate() {
        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            sRefWatcher = LeakCanary.install(this);
        }

        // It is strongly recommended to call in Application #onCreate method,
        // to avoid content as null
        TTAdManagerHolder.init(this);
    }

}
