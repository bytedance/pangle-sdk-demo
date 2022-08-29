package com.union_test.new_api.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.union_test.new_api.DemoApplication;

/**
 * @author liuruifeng
 * created on 2020/12/8
 */
public class PangleSpUtils {


    private static final String TAG = "PangleSpUtils";


    private static final String KEY_COPPA = "pangle_coppa";
    private static final String KEY_GDPR = "pangle_gdpr";
    private static final String KEY_CCPA = "pangle_ccpa";

    public static final String PANGLE_DEMO_SP = "pangle_demo_sp";


    private static volatile PangleSpUtils mUserManager;
    private SharedPreferences sp;

    private PangleSpUtils() {
        sp = DemoApplication.CONTEXT.getSharedPreferences(PANGLE_DEMO_SP, Context.MODE_PRIVATE);
    }



    public static PangleSpUtils getInstance() {
        if (mUserManager == null) {
            synchronized (PangleSpUtils.class) {
                if (mUserManager == null) {
                    mUserManager = new PangleSpUtils();
                }
            }
        }
        return mUserManager;
    }


    public void setCoppa(int coppa) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_COPPA, coppa);
        editor.apply();
    }

    public int getCoppa() {
        return sp.getInt(KEY_COPPA, -1);
    }


    public void setGdpr(int gdpr) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_GDPR, gdpr);
        editor.apply();
    }

    public int getGdpr() {
        return sp.getInt(KEY_GDPR, 1);
    }

    public void setCcpa(int ccpa) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_CCPA, ccpa);
        editor.apply();
    }

    public int getCcpa() {
        return sp.getInt(KEY_CCPA, -1);
    }



}
