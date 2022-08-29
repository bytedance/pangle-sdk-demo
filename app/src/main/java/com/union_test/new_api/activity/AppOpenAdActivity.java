package com.union_test.new_api.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.union_test.internationad.R;
import com.union_test.new_api.AdManagerHolder;
import com.union_test.new_api.DemoApplication;
import com.union_test.new_api.PAGAppOpenAdManager;

public class AppOpenAdActivity extends Activity {
    private Handler mOpenAdLoadHandler;
    private static final long TIMEOUT = 3000;
    private static final int WHAT_TIMEOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mOpenAdLoadHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == WHAT_TIMEOUT) {
                    startMainActivity();
                }
            }
        };
        mOpenAdLoadHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT, TIMEOUT);

        final DemoApplication application = (DemoApplication) getApplication();
        AdManagerHolder.setPangleSdkHasInitSuccess(new AdManagerHolder.PangleSdkHasInitSuccess() {
            @Override
            public void initSuccess() {
                application.fetchAd(new PAGAppOpenAdManager.RealTimeFetchListener() {
                    @Override
                    public void loadedSuccess() {
                        application.showAdIfAvailable(new PAGAppOpenAdManager.ManagerOpenAdInteractionListener() {
                            @Override
                            public void onAdShow() {
                                mOpenAdLoadHandler.removeMessages(WHAT_TIMEOUT);
                            }

                            @Override
                            public void onAdClose() {
                                startMainActivity();
                            }
                        });
                    }

                    @Override
                    public void loadedFail() {

                    }
                });
            }
        });
    }

    /**
     * Start the MainActivity.
     */
    public void startMainActivity() {
        Intent intent = new Intent(this, PAGMainActivity.class);
        startActivity(intent);
        finish();
    }
}

