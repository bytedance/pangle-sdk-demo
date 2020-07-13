package com.mopub.nativeads;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bytedance.sdk.openadsdk.adapter.MediaView;
import com.mopub.common.logging.MoPubLog;

import static com.mopub.common.logging.MoPubLog.SdkLogEvent.CUSTOM;

public class PangleAdNativeViewHolder {

    @Nullable
    public TextView mTitleView;
    @Nullable
    public TextView mDescription;
    @Nullable
    public ImageView mIcon;
    @Nullable
    public TextView mAdvertiserNameView;
    @Nullable
    public TextView mCallToActionView;
    @Nullable
    public ImageView mLogoView;

    /**
     * video View
     */
    @Nullable
    public MediaView mMediaView;


    private static PangleAdNativeViewHolder EMPTY_VIEW_HOLDER = new PangleAdNativeViewHolder();

    private PangleAdNativeViewHolder() {
    }

    static PangleAdNativeViewHolder fromViewBinder(@NonNull final View view,
                                                   @NonNull final PangleAdViewBinder pangleAdViewBinder) {
        final PangleAdNativeViewHolder adViewHolder = new PangleAdNativeViewHolder();
        try {
            adViewHolder.mTitleView = view.findViewById(pangleAdViewBinder.mTitleId);
            adViewHolder.mDescription = view.findViewById(pangleAdViewBinder.mDescriptionTextId);
            adViewHolder.mCallToActionView = view.findViewById(pangleAdViewBinder.mCallToActionId);
            adViewHolder.mAdvertiserNameView = view.findViewById(pangleAdViewBinder.mSourceId);
            adViewHolder.mIcon = view.findViewById(pangleAdViewBinder.mIconImageId);
            adViewHolder.mLogoView = view.findViewById(pangleAdViewBinder.mLogoViewId);
            adViewHolder.mMediaView = view.findViewById(pangleAdViewBinder.mMediaViewId);
            return adViewHolder;
        } catch (ClassCastException exception) {
            MoPubLog.log(CUSTOM, "Could not cast from id in PangleAdViewBinder to expected View type",
                    exception);
            return EMPTY_VIEW_HOLDER;
        }
    }


}
