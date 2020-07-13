package com.mopub.mobileads;

import android.content.Context;

import java.util.Map;

public class PangleSharedUtil {

    public static final int CONTENT_TYPE = 40000;//# http conent_type
    public static final int REQUEST_PB_ERROR = 40001;//# http request pb
    public static final int NO_AD = 20001;//# no ad
    public static final int ADSLOT_EMPTY = 40004;// ad code id can't been null
    public static final int ADSLOT_ID_ERROR = 40006;// code id error

    public static MoPubErrorCode mapErrorCode(int error) {
        switch (error) {
            case PangleSharedUtil.CONTENT_TYPE:
            case PangleSharedUtil.REQUEST_PB_ERROR:
                return MoPubErrorCode.NO_CONNECTION;
            case PangleSharedUtil.NO_AD:
                return MoPubErrorCode.NETWORK_NO_FILL;
            case PangleSharedUtil.ADSLOT_EMPTY:
            case PangleSharedUtil.ADSLOT_ID_ERROR:
                return MoPubErrorCode.MISSING_AD_UNIT_ID;
            default:
                return MoPubErrorCode.UNSPECIFIED;
        }
    }

    public static float[] getAdSizeSafely(Map<String, Object> params, String widthName, String heightName) {
        float[] adSize = new float[]{0, 0};
        if (params == null || widthName == null || heightName == null) {
            return adSize;
        }

        Object oWidth = params.get(widthName);
        if (oWidth != null) {
            String w = String.valueOf(oWidth);
            adSize[0] = Float.valueOf(w);
        }

        Object oHeight = params.get(heightName);

        if (oHeight != null) {
            String h = String.valueOf(oHeight);
            adSize[1] = Float.valueOf(h);
        }

        return adSize;
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float getScreenWidth(Context context) {
        if (context == null) return -1;
        return (float) context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenHeight(Context context) {
        if (context == null) return -1;
        return (float) context.getResources().getDisplayMetrics().heightPixels;
    }


    public static int pxtosp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sptopx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
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
    public static float[] getBannerAdSizeAdapterSafely(Map<String, Object> params, String widthName, String heightName) {
        float[] adSize = new float[]{0, 0};
        if (params == null || widthName == null || heightName == null) {
            return adSize;
        }

        Object oHeight = params.get(heightName);

        if (oHeight != null) {
            if (oHeight instanceof Integer) {
                adSize[1] = (float) ((Integer) oHeight);
            } else if (oHeight instanceof Float) {
                adSize[1] = (float) oHeight;
            } else {
                adSize[1] = Float.valueOf(String.valueOf(oHeight));
            }
        }


        Object oWidth = params.get(widthName);
        if (oWidth != null) {
            if (oWidth instanceof Integer) {
                adSize[0] = (float) ((Integer) oWidth);
            } else if (oWidth instanceof Float) {
                adSize[0] = (float) oWidth;
            } else {
                adSize[0] = Float.valueOf(String.valueOf(oWidth));
            }

            float s = adSize[0] / adSize[1];

            if (s == 600f / 500f || s == 600f / 400f ||
                    s == 690f / 388f || s == 600f / 300f ||
                    s == 600f / 260f || s == 600f / 150f ||
                    s == 640f / 100f || s == 600f / 90f
            ) {
                return adSize;
            }

            float fac = 0.25f;
            float ratdioWidth = adSize[0] / 600f;
            //assume width = 600f
            if (ratdioWidth <= 0.5f + fac) {
                ratdioWidth = 0.5f;
            } else if (ratdioWidth <= 1f + fac) {
                ratdioWidth = 1f;
            } else if (ratdioWidth <= 1.5f + fac) {
                ratdioWidth = 1.5f;
            } else {
                ratdioWidth = 2f;
            }


            if (s < 600f / 500f) { //1.2f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 500f * ratdioWidth;
            } else if (s < 600f / 400f) {//1.5f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 400f * ratdioWidth;
            } else if (s < 690f / 388f) { //1.77f
                ratdioWidth = adSize[0] / 690f;
                //assume width = 690f
                if (ratdioWidth < 0.5f + fac) {
                    ratdioWidth = 0.5f;
                } else if (ratdioWidth < 1f + fac) {
                    ratdioWidth = 1f;
                } else if (ratdioWidth < 1.5f + fac) {
                    ratdioWidth = 1.5f;
                } else {
                    ratdioWidth = 2f;
                }
                adSize[0] = 690f * ratdioWidth;
                adSize[1] = 388f * ratdioWidth;
            } else if (s < 600f / 300f) { // 2f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 300f * ratdioWidth;
            } else if (s < 600f / 260f) {//2.3f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 260f * ratdioWidth;
            } else if (s < 600f / 150f) {// 4.0f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 150f * ratdioWidth;
            } else if (s < 640f / 100f) { //6.4f
                ratdioWidth = adSize[0] / 640f;
                //assume width = 690f
                if (ratdioWidth < 0.5f + fac) {
                    ratdioWidth = 0.5f;
                } else if (ratdioWidth < 1f + fac) {
                    ratdioWidth = 1f;
                } else if (ratdioWidth < 1.5f + fac) {
                    ratdioWidth = 1.5f;
                } else {
                    ratdioWidth = 2f;
                }
                adSize[0] = 640f * ratdioWidth;
                adSize[1] = 100f * ratdioWidth;

            } else if (s < 600f / 90f || s > 600f / 90f) {//6.67f
                adSize[0] = 600f * ratdioWidth;
                adSize[1] = 90f * ratdioWidth;
            }
        }

        return adSize;
    }


}
