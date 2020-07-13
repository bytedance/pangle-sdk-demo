package com.union_test.toutiao.activity.nativead;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTDrawFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.dingmouren.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.dingmouren.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.utils.NetworkUtils;
import com.union_test.toutiao.utils.TToast;
import com.union_test.toutiao.view.FullScreenVideoView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wuzejian on 2018/9/21.
 *
 */
public class DrawNativeVideoActivity extends AppCompatActivity {

    private static final String TAG = "DrawNativeVideoActivity";
    private static final int TYPE_COMMON_ITEM = 1;
    private static final int TYPE_AD_ITEM = 2;
    private RecyclerView mRecyclerView;
    private LinearLayout mBottomLayout;
    private RelativeLayout mTopLayout;
    private MyAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;
    private int[] imgs = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.img_video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private TTAdNative mTTAdNative;
    private Context mContext;
    private List<Item> datas = new ArrayList<>();
    private RequestManager mRequestManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_native_video);
        if (NetworkUtils.getNetworkType(this) == NetworkUtils.NetworkType.NONE) {
            return;
        }
        mRequestManager = Glide.with(this);
        initView();
        initListener();
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        mContext = this;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDrawNativeAd();
            }
        }, 500);
    }


    private void loadDrawNativeAd() {
        //Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("901121709")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setAdCount(2) //ad count from 1 to 3
                .build();
        //request ad
        mTTAdNative.loadDrawFeedAd(adSlot, new TTAdNative.DrawFeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, message);
                showToast(message);
            }

            @Override
            public void onDrawFeedAdLoad(List<TTDrawFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    TToast.show(DrawNativeVideoActivity.this, " ad is null!");
                    return;
                }
                for (int i = 0; i < 5; i++) {
                    int random = (int) (Math.random() * 100);
                    int index = random % videos.length;
                    datas.add(new Item(TYPE_COMMON_ITEM, null, videos[index], imgs[index]));
                }

                for (TTDrawFeedAd ad : ads) {
                    //set listener before call getAdView
                    ad.setDrawVideoListener(new TTDrawFeedAd.DrawVideoListener() {
                        @Override
                        public void onClickRetry() {
                            TToast.show(DrawNativeVideoActivity.this, " onClickRetry !");
                            Log.d("drawss", "onClickRetry!");
                        }

                        @Override
                        public void onClick() {
                            Log.d("drawss", "onClick download or view detail page ! !");
                            TToast.show(DrawNativeVideoActivity.this, " setDrawVideoListener click download or view detail page !");
                        }
                    });
                    ad.setCanInterruptVideoPlay(true);
                    ad.setPauseIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.dislike_icon), 60);
                    int random = (int) (Math.random() * 100);
                    int index = random % videos.length;
                    datas.add(index, new Item(TYPE_AD_ITEM, ad, -1, -1));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initView() {
        datas.add(new Item(TYPE_COMMON_ITEM, null, videos[0], imgs[0]));
        mRecyclerView = findViewById(R.id.recycler);
        mBottomLayout = findViewById(R.id.bottom);
        mTopLayout = findViewById(R.id.top);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new MyAdapter(datas);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private View getView() {
        FullScreenVideoView videoView = new FullScreenVideoView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        return videoView;
    }


    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                onLayoutComplete();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "onPageRelease:" + position + " isNext:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                if (datas.get(position).type == TYPE_COMMON_ITEM)
                    releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "onPageSelected:" + position + "  isBottom:" + isBottom);
                if (datas.get(position).type == TYPE_COMMON_ITEM) {
                    playVideo(0);
                    changeBottomTopLayoutVisibility(true);
                } else if (datas.get(position).type == TYPE_AD_ITEM) {
                    changeBottomTopLayoutVisibility(false);
                }
            }

            private void onLayoutComplete() {
                if (datas.get(0).type == TYPE_COMMON_ITEM) {
                    playVideo(0);
                    changeBottomTopLayoutVisibility(true);
                } else if (datas.get(0).type == TYPE_AD_ITEM) {
                    changeBottomTopLayoutVisibility(false);
                }
            }

        });
    }

    private void changeBottomTopLayoutVisibility(boolean visibility) {
        mBottomLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mTopLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void playVideo(int position) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(0);
        if (itemView == null) {
            return;
        }
        final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
        final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    mediaPlayer[0] = mp;
                    Log.e(TAG, "onInfo");
                    mp.setLooping(true);
                    imgThumb.animate().alpha(0).setDuration(200).start();
                    return false;
                }
            });
        }
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG, "onPrepared");

            }
        });


        imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                } else {
                    Log.e(TAG, "isPlaying:" + videoView.isPlaying());
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        if (isFinishing()) {
            return;
        }

        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            final FrameLayout videoLayout = itemView.findViewById(R.id.video_layout);
            if (videoLayout == null) return;
            View view = videoLayout.getChildAt(0);
            if (view instanceof VideoView) {
                final VideoView videoView = (VideoView) videoLayout.getChildAt(0);
                final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
                final ImageView imgPlay = itemView.findViewById(R.id.img_play);
                videoView.stopPlayback();
                imgThumb.animate().alpha(1).start();
                imgPlay.animate().alpha(0f).start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayoutManager != null) {
            mLayoutManager.setOnViewPagerListener(null);
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<Item> datas;

        public MyAdapter(List<Item> datas) {
            this.datas = datas;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View view = new View(mContext);
            Item item = null;
            if (datas != null) {
                item = datas.get(position);
                if (item.type == TYPE_COMMON_ITEM) {
                    holder.img_thumb.setImageResource(item.ImgId);
                    view = getView();
                    ((VideoView) view).setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + item.videoId));
                } else if (item.type == TYPE_AD_ITEM && item.ad != null) {
                    view = item.ad.getAdView();
                    if (item.ad.getIcon() != null && item.ad.getIcon().getImageUrl() != null) {
                        mRequestManager.load(item.ad.getIcon().getImageUrl()).into(holder.img_head_icon);
                    } else {
                        holder.img_head_icon.setImageBitmap(item.ad.getAdLogo());
                    }
                }
            }
            holder.videoLayout.removeAllViews();
            holder.videoLayout.addView(view);
            if (item != null && item.type == TYPE_AD_ITEM) {
                initAdViewAndAction(item.ad, holder.videoLayout);
            }
            if (item != null) {
                changeUIVisibility(holder, item.type);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType--" + position);

            return datas.get(position).type;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
//            changeUIVisibility(holder,);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            CircleImageView img_head_icon;
            ImageView img_play;
            RelativeLayout rootView;
            FrameLayout videoLayout;
            LinearLayout verticalIconLauout;

            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoLayout = itemView.findViewById(R.id.video_layout);
                img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
                verticalIconLauout = itemView.findViewById(R.id.vertical_icon);
                img_head_icon = itemView.findViewById(R.id.head_icon);

            }
        }
    }

    private void changeUIVisibility(MyAdapter.ViewHolder holder, int type) {
        boolean visibilable = true;
        if (type == TYPE_AD_ITEM) {
            visibilable = false;
        }
        Log.d(TAG, "visibilable=" + visibilable);
        holder.img_play.setVisibility(visibilable ? View.VISIBLE : View.GONE);
        holder.img_thumb.setVisibility(visibilable ? View.VISIBLE : View.GONE);

    }

    private void initAdViewAndAction(TTDrawFeedAd ad, FrameLayout container) {
        View actionView = this.getLayoutInflater().inflate(R.layout.native_draw_action_item,null,false);
        TextView title = actionView.findViewById(R.id.tv_title);
        title.setText(ad.getTitle());

        TextView desc = actionView.findViewById(R.id.tv_desc);
        desc.setText(ad.getDescription());

        Button action = actionView.findViewById(R.id.button_creative);
        action.setText(ad.getButtonText());

        int margin = (int) dip2Px(this, 10);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.gravity = Gravity.START | Gravity.BOTTOM;
        lp1.leftMargin = margin;
        lp1.bottomMargin = margin;
        container.addView(actionView, lp1);

        List<View> clickViews = new ArrayList<>();
        clickViews.add(title);
        clickViews.add(desc);
        List<View> creativeViews = new ArrayList<>();
        creativeViews.add(action);
        ad.registerViewForInteraction(container, clickViews, creativeViews, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                showToast("onAdClicked");
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                showToast("onAdCreativeClick");
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                showToast("onAdShow");
            }
        });


    }

    private float dip2Px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    private void showToast(String msg) {
        TToast.show(this, msg);
    }

    private static class Item {
        public int type = 0;
        public TTDrawFeedAd ad;
        public int videoId;
        public int ImgId;

        public Item(int type, TTDrawFeedAd ad, int videoId, int imgId) {
            this.type = type;
            this.ad = ad;
            this.videoId = videoId;
            ImgId = imgId;
        }
    }


}
