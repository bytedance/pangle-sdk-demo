package com.google.ads.mediation.sample.customevent;

import android.os.Bundle;

/**
 * created by wuzejian on 2019-12-03
 */
public final class BundleBuilder {

    /**
     * ad size
     */
    public final static String AD_WIDTH = "ad_width";
    public final static String AD_HEIGHT = "ad_height";
    public final static String AD_GDPR = "gdpr";
    public final static String KEY_AD_PLACEMENT_ID = "ad_placement_id";


    private int gdpr = 1;
    private int width;
    private int height;
    private String codeId;

    public BundleBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public BundleBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public BundleBuilder setCodeId(String codeId) {
        this.codeId = codeId;
        return this;
    }

    public BundleBuilder setGdpr(int gdpr) {
        this.gdpr = gdpr;
        return this;
    }

    public Bundle build() {
        Bundle bundle = new Bundle();
        bundle.putInt(AD_GDPR, gdpr);
        bundle.putInt(AD_HEIGHT, height);
        bundle.putInt(AD_WIDTH, width);
        bundle.putString(KEY_AD_PLACEMENT_ID, codeId);
        return bundle;
    }
}