package com.union_test.new_api;


import static android.arch.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdInteractionListener;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdLoadListener;
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenRequest;

/**
 * AppOpenAdManger
 */
public class PAGAppOpenAdManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String TAG = "PAGAppOpenAdManager";

    private static boolean isShowingAd = false;

    private PAGAppOpenAd appOpenAd = null;

    private final Application myApplication;
    private Activity currentActivity;

    private final static int LOAD_TIMEOUT = 3000;

    private long mAdValidStartTime;

    /**
     * Need to get the request advertisement callback in real time
     * （Example：Cold start）
     */
    public interface RealTimeFetchListener {
        void loadedSuccess();

        void loadedFail();
    }

    /**
     * Need to get ad display and disappear callback
     * （Example：Cold start needs to perform operations such as jumping according to the ad has been displayed and disappeared）
     */
    public interface ManagerOpenAdInteractionListener {
        void onAdShow();

        void onAdClose();
    }

    /**
     * Constructor
     */
    public PAGAppOpenAdManager(Application myApplication) {
        this.myApplication = myApplication;

        //By keeping track of the current activity, you have a context to use to show the ad. You now need to register this interface using the registerActivityLifecycleCallbacks Application method in your PAGAppOpenAdLoadManager constructor.
        this.myApplication.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Request an ad
     */
    public void fetchAd(final RealTimeFetchListener realTimeFetchListener) {
        Log.d(TAG, "fetchAdNew");
        // Have unused ad, no need to fetch another.
        if (appOpenAd != null) {
            return;
        }
        PAGAppOpenRequest request = new PAGAppOpenRequest();
        request.setTimeout(LOAD_TIMEOUT);
        PAGAppOpenAd.loadAd(RitConstants.RIT_OPEN_VERTICAL, request, new PAGAppOpenAdLoadListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "errorCode: " + code + " errorMessage: " + message);

                if (realTimeFetchListener != null) {
                    realTimeFetchListener.loadedFail();
                }
            }

            @Override
            public void onAdLoaded(PAGAppOpenAd ad) {
                appOpenAd = ad;
                mAdValidStartTime = System.currentTimeMillis();
                if (realTimeFetchListener != null) {
                    realTimeFetchListener.loadedSuccess();
                }
            }
        });
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }


    /**
     * Show App Open Ad
     */
    public void showAdIfAvailable(final ManagerOpenAdInteractionListener managerOpenAdInteractionListener) {
        if (mAdValidStartTime > 0 && ((System.currentTimeMillis() - mAdValidStartTime) > 24 * 60 * 60 * 1000)) {
            Log.d(TAG, "Advertising material has expired");
            return;
        }
        if (!isShowingAd) {
            if (appOpenAd != null) {
                Log.d(TAG, "Will show ad.");
                appOpenAd.setAdInteractionListener(new PAGAppOpenAdInteractionListener() {
                    @Override
                    public void onAdShowed() {
                        Log.d(TAG, "onAdShow");
                        isShowingAd = true;

                        if (managerOpenAdInteractionListener != null) {
                            managerOpenAdInteractionListener.onAdShow();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "onAdClicked");
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.d(TAG, "onAdDismissed");
                        openAdClose(managerOpenAdInteractionListener);
                    }
                });
                appOpenAd.show(currentActivity);
                appOpenAd = null;
            } else {
                Log.d(TAG, "No ad to show. to fetchAd");
                fetchAd(null);
            }
        } else {
            Log.d(TAG, "There is currently an ad display or The current scene does not want to show the open screen");
        }
    }

    /**
     * Ad Dismiss control
     */
    private void openAdClose(ManagerOpenAdInteractionListener managerOpenAdInteractionListener) {
        isShowingAd = false;
        fetchAd(null);

        if (managerOpenAdInteractionListener != null) {
            managerOpenAdInteractionListener.onAdClose();
        }
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        Log.d(TAG, "onStart");
        showAdIfAvailable(null);
    }

    /**
     * There is currently an ad display, or The current scene does not want to show the open screen
     * @param isShowingNow not want to show the open screen
     */
    public static void setIsShowingAd(boolean isShowingNow) {
        isShowingAd = isShowingNow;
    }
}
