package com.mopub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mopub.common.DataKeys;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.PangleAdapterConfiguration;

import com.union_test.toutiao.R;
import com.union_test.toutiao.utils.UIUtils;
import com.union_test.toutiao.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * created by wuzejian on 2019/11/29
 */
@SuppressLint("LongLogTag")
public class MopubBannerActivity extends Activity implements MoPubView.BannerAdListener {
    private static final String TAG = "MopubBannerActivity";

    private String mAdUnitId = "f46519805f6c49efa3c0a4ca20334c92";

    private MoPubView mMoPubView;
    private EditText mEtWidth;
    private EditText mEtHeight;

    private boolean mHasInit = false;

    private LoadMoreRecyclerView mListView;
    private List<AdSizeModel> mBannerAdSizeModelList;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mopub_activity_banner_express);
        Map<String, String> mediatedNetworkConfiguration = new HashMap<>();
//        mediatedNetworkConfiguration.put("appId", "8888888888888");

        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(mAdUnitId)
                .withAdditionalNetwork(PangleAdapterConfiguration.class.getName())
                .withMediatedNetworkConfiguration(PangleAdapterConfiguration.class.getName(), mediatedNetworkConfiguration)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .build();

        //init MoPub SDK
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

    }


    private void initRecycleView() {
        mListView = findViewById(R.id.my_list);
        initData();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mListView.setLayoutManager(layoutManager);
        AdapterForBannerType adapterForBannerType = new AdapterForBannerType(this, mBannerAdSizeModelList);
        mListView.setAdapter(adapterForBannerType);

    }

    private void initView() {
        mMoPubView = findViewById(R.id.adview);
        mEtHeight = findViewById(R.id.express_height);
        mEtWidth = findViewById(R.id.express_width);
        final float[] expressViewWidth = {800};
        final float[] expressViewHeight = {700};


        final Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(DataKeys.AD_WIDTH, expressViewWidth[0]);
        localExtras.put(DataKeys.AD_HEIGHT, expressViewHeight[0]);


//        findViewById(R.id.btn_native_load_600_300).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_load_600_300, 600, 300);
//            }
//        });
//
//        findViewById(R.id.btn_native_banner_640_100).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_banner_640_100, 640, 100);
//            }
//        });
//
//
//        //适配 600*500、600*260、600*90、600*150、690*388
//
//        findViewById(R.id.btn_native_banner_640_400).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_banner_640_400, 640, 400);
//
//            }
//        });
//
//
//        findViewById(R.id.btn_native_load_600_500).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getAd(localExtras, R.id.btn_native_load_600_500, 600, 500);
//
//            }
//        });
//
//
//        findViewById(R.id.btn_native_banner_600_260).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_banner_600_260, 600, 260);
//
//            }
//        });
//
//
//        findViewById(R.id.btn_native_banner_600_90).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_banner_600_90, 600, 90);
//
//            }
//        });
//
//        findViewById(R.id.btn_native_load_600_150).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_load_600_150, 600, 150);
//
//            }
//        });
//
//
//        findViewById(R.id.btn_native_banner_690_388).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAd(localExtras, R.id.btn_native_banner_690_388, 690, 388);
//
//            }
//        });

        findViewById(R.id.btn_native_banner_xxx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    expressViewWidth[0] = Float.parseFloat(mEtWidth.getText().toString());
                    expressViewHeight[0] = Float.parseFloat(mEtHeight.getText().toString());
                } catch (Exception e) {
                }

                if (expressViewWidth[0] == 0) expressViewWidth[0] = 600;
                if (expressViewHeight[0] == 0) expressViewHeight[0] = 400;

                localExtras.put(DataKeys.AD_WIDTH, expressViewWidth[0]);
                localExtras.put(DataKeys.AD_HEIGHT, expressViewHeight[0]);
                getAd(localExtras, R.id.btn_native_banner_xxx, expressViewWidth[0], expressViewHeight[0]);

            }
        });

        mMoPubView.setBannerAdListener(MopubBannerActivity.this);
    }

    private void getAd(final Map<String, Object> localExtras, int resourceId, final float width, final float height) {
//        if (!mHasInit) {
//            UIUtils.logToast(MopubBannerActivity.this, "init not finish, wait");
//            return;
//        }
//        mAdUnitId = "f9456948d99b4439bef97a93de51db8d";
//        localExtras.put(DataKeys.AD_WIDTH, width);
//        localExtras.put(DataKeys.AD_HEIGHT, height);
//        localExtras.put(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY, "945182502");
//        mMoPubView.setAdUnitId(mAdUnitId); // Enter your Ad Unit ID from www.mopub.com
//        mMoPubView.setLocalExtras(localExtras);
//        mMoPubView.loadAd();
    }

    @Override
    public void onBannerLoaded(MoPubView banner) {
        UIUtils.logToast(MopubBannerActivity.this, "MoPubView onBannerLoaded.");
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        UIUtils.logToast(MopubBannerActivity.this, "MoPubView onBannerFailed.-" + errorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        UIUtils.logToast(MopubBannerActivity.this, "MoPubView onBannerClicked.");

    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }


    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                mHasInit = true;
                Log.d("TiktokAdapter", "onInitializationFinished////");
                initView();
                initRecycleView();

           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMoPubView != null) {
            mMoPubView.destroy();
            mMoPubView = null;
        }
    }


    public static class AdapterForBannerType extends RecyclerView.Adapter<AdapterForBannerType.ViewHolder> {
        private List<AdSizeModel> mBannerSizeList;
        private MopubBannerActivity mActivity;

        public AdapterForBannerType(MopubBannerActivity activity, List<AdSizeModel> bannerSize) {
            this.mActivity = activity;
            this.mBannerSizeList = bannerSize;
        }

        @NonNull
        @Override
        public AdapterForBannerType.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.express_banner_list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterForBannerType.ViewHolder viewHolder, int i) {
            final AdSizeModel bannerSize = mBannerSizeList == null ? null : mBannerSizeList.get(i);
            if (bannerSize != null) {
                viewHolder.btnSize.setText(bannerSize.adSizeName);
                viewHolder.btnSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //请求banner广告
                        mActivity.loadExpressAd(bannerSize.codeId, bannerSize.width, bannerSize.height);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mBannerSizeList != null ? mBannerSizeList.size() : 0;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            private Button btnSize;

            public ViewHolder(View view) {
                super(view);
                btnSize = view.findViewById(R.id.btn_banner_size);
            }

        }
    }

    private void initData() {
        mBannerAdSizeModelList = new ArrayList<>();
        mBannerAdSizeModelList.add(new AdSizeModel("600*90", 300, 45, "901121246"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*150", 300, 75, "901121700"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*260", 300, 130, "901121148"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*300", 300, 150, "901121228"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*400", 300, 200, "901121686"));
        mBannerAdSizeModelList.add(new AdSizeModel("640*100", 320, 50, "901121223"));
        mBannerAdSizeModelList.add(new AdSizeModel("690*388", 345, 194, "901121158"));
        mBannerAdSizeModelList.add(new AdSizeModel("600*500", 300, 250, "901121834"));
    }

    private void loadExpressAd(String codeId, int expressViewWidth, int expressViewHeight) {
        mMoPubView.removeAllViews();
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        if (!mHasInit) {
            return;
        }
        //DataKeys.AD_WIDTH, DataKeys.AD_HEIGHT
        final Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(DataKeys.AD_WIDTH, expressViewWidth);
        localExtras.put(DataKeys.AD_HEIGHT, expressViewHeight);
        mAdUnitId = "f9456948d99b4439bef97a93de51db8d";
        localExtras.put(PangleAdapterConfiguration.AD_PLACEMENT_ID_EXTRA_KEY, codeId);
        mMoPubView.setAdUnitId(mAdUnitId); // Enter your Ad Unit ID from www.mopub.com
        mMoPubView.setLocalExtras(localExtras);
        mMoPubView.loadAd();
    }


    public static class AdSizeModel {
        public AdSizeModel(String adSizeName, int width, int height, String codeId) {
            this.adSizeName = adSizeName;
            this.width = width;
            this.height = height;
            this.codeId = codeId;
        }

        public String adSizeName;
        public int width;
        public int height;
        public String codeId;
    }
}
