package com.google.ads.mediation.sample.customevent;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.google.android.gms.ads.AdRequest;
import com.union_test.toutiao.config.TTAdManagerHolder;

import org.json.JSONException;
import org.json.JSONObject;


public class AdmobAdapterUtil {


    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static TTAdNative getPangleAdLoader(Context context) {
        if (context == null) return null;
        //It will only be initialized once inside the pangle SDK
        TTAdManagerHolder.init(context);
        TTAdManager mTTAdManager = TTAdManagerHolder.get();
        return mTTAdManager.createAdNative(context.getApplicationContext());
    }

    public static void setGdpr(int gdpr) {
        TTAdManager adManager = TTAdManagerHolder.get();
        if (adManager != null) {
            adManager.setGdpr(gdpr);
        }
    }

    public static boolean isExpressAd(String codeId, String adm) {
        TTAdManager adManager = TTAdManagerHolder.get();
        if (adManager != null) {
            return adManager.isExpressAd(codeId, adm);
        }
        return false;
    }

    public static String getAdPlacementId(String serverParameter) {
        if (!TextUtils.isEmpty(serverParameter)) {
            try {
                JSONObject serverParametersJson = new JSONObject(serverParameter);
                return serverParametersJson.getString(BundleBuilder.KEY_AD_PLACEMENT_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * pangle banner support size ：
     * 600*300、600*400、600*500、600*260、600*90、600*150、640*100、690*388
     *
     * @param params
     * @param widthName
     * @param heightName
     * @return
     */
    public static int[] getBannerAdSizeAdapterSafely(Bundle params, String widthName, String heightName) {
        //适配 600*300、600*400、600*500、600*260、600*90、600*150、640*100、690*388

        int[] adSize = new int[]{0, 0};
        if (params == null || widthName == null || heightName == null) {
            return adSize;
        }

        Object oHeight = params.get(heightName);

        if (oHeight != null) {
            adSize[1] = (int) oHeight;
        }

        Object oWidth = params.get(widthName);
        if (oWidth != null) {
            adSize[0] = (int) oWidth;

            if (adSize[0] > 0 && adSize[0] <= 600) {
                adSize[0] = 600;
                if (adSize[1] <= 100) {
                    adSize[1] = 90;
                } else if (adSize[1] <= 150) {
                    adSize[1] = 150;
                } else if (adSize[1] <= 260) {
                    adSize[1] = 260;
                } else if (adSize[1] <= 300) {
                    adSize[1] = 300;
                } else if (adSize[1] <= 450) {
                    adSize[1] = 400;
                } else {
                    adSize[1] = 500;
                }
            } else if (adSize[0] > 600 && adSize[0] <= 640) {
                adSize[0] = 640;
                adSize[1] = 100;
            } else {
                adSize[0] = 690;
                adSize[1] = 388;
            }
        }


        return adSize;
    }

    public static int[] getAdSizeSafely(Bundle params, String widthName, String heightName) {
        int[] adSize = new int[]{0, 0};
        if (params == null || widthName == null || heightName == null) {
            return adSize;
        }

        Object oWidth = params.get(widthName);
        if (oWidth != null) {
            String w = String.valueOf(oWidth);
            adSize[0] = Integer.valueOf(w);
        }

        Object oHeight = params.get(heightName);

        if (oHeight != null) {
            String h = String.valueOf(oHeight);
            adSize[1] = Integer.valueOf(h);
        }

        if (adSize[0] == 0) {
            adSize[0] = 450;
        }

        return adSize;
    }

    public static int getAdmobError(int errorCode) {
        switch (errorCode) {
            case 40016://slot id error
            case 40009: //codeId error
            case 40006: //slot id error
                return AdRequest.ERROR_CODE_INVALID_REQUEST;// id error
            case -2:
                return AdRequest.ERROR_CODE_NETWORK_ERROR;//network error
            case 20001:
                return AdRequest.ERROR_CODE_NO_FILL;

            case -3:
            case -1:
            case -4:
                return AdRequest.ERROR_CODE_INTERNAL_ERROR;
        }

        return errorCode;
    }

    public static double getValue(Object price) {
        if (price instanceof Integer) {
            int result = (int) price;
            return (double) result;
        }

        if (price instanceof String) {
            return Double.valueOf((String) price);
        }

        if (price instanceof Float) {
            return (double) price;
        }

        return 0;
    }


}
