package com.union_test.toutiao.activity.expressad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.dialog.DislikeDialog;
import com.union_test.toutiao.utils.TToast;

import java.util.List;

@SuppressWarnings("unused")
public class NativeExpressActivity extends Activity {

    private TTAdNative mTTAdNative;
    private FrameLayout mExpressContainer;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonLoadAd;
    private Button mButtonLoadAdVideo;
    private EditText mEtWidth;
    private EditText mEtHeight;
    private TTNativeExpressAd mTTAd;

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_native_express);
        mContext = this.getApplicationContext();
        mExpressContainer = (FrameLayout) findViewById(R.id.express_container);
        mButtonLoadAd = (Button) findViewById(R.id.btn_express_load);
        mButtonLoadAdVideo = (Button) findViewById(R.id.btn_express_load_video);
        mEtHeight = (EditText) findViewById(R.id.express_height);
        mEtWidth = (EditText) findViewById(R.id.express_width);
        mButtonLoadAd.setOnClickListener(mClickListener);
        mButtonLoadAdVideo.setOnClickListener(mClickListener);
        //create TTAdNative object which use to request ad，createAdNative(Context context)
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_express_load) {
                loadExpressAd("945113157");
            }else if (v.getId() == R.id.btn_express_load_video){
                loadExpressAd("945113157");
            }
        }
    };

    private void loadExpressAd(String codeId) {
        mExpressContainer.removeAllViews();
        float expressViewWidth = 350;
        float expressViewHeight = 350;
        try{
            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
            expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
        }catch (Exception e){
            expressViewHeight = 0; //Set the height to 0, the height will be adaptive
        }
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //rit
                .setSupportDeepLink(true)
                .setAdCount(1) //ad count from 1 to 3
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //expected ad view size in dp
                .build();
        //step5:request ad，then call render method of TTNativeExpressAd
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(NativeExpressActivity.this, "load error : " + code + ", " + message);
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }
    private long startTime = 0;

    private boolean mHasShowDownloadActive = false;

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                TToast.show(mContext, "Ad clicked");
            }

            @Override
            public void onAdShow(View view, int type) {
                TToast.show(mContext, "Ad showed");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                TToast.show(mContext, msg+" code:"+code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //the width and height of view in dp
                TToast.show(mContext, "Render success");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
    }

    /**
     * Set the dislike of the advertisement.
     * Note: It is strongly recommended to set this logic.
     * If dislike processing logic is not set, the dislike area in the template advertisement will not respond to the dislike event.
     *
     * @param ad
     * @param customStyle Whether to customize the style，true:customize the style
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //remove ad
                    TToast.show(mContext, "Click " + filterWord.getName());
                    mExpressContainer.removeAllViews();
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //Use the default dislike popup style
        ad.setDislikeCallback(NativeExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                TToast.show(mContext, "Click " + value);
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onCancel() {
                TToast.show(mContext, "Cancel Click ");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }
}
