# Pangle Android SDK Integration Guideline

|Doc Version|Revision Date| Revision Statement|
| --------------- | ------------- | ------------------------------------------------------------ |
|  v2.0.0.0    | 2019-03-06   | Create file, added full-video ad and reward-video ad |
|  v2.0.2.0    | 2019-04-24   | Fix some issues |
|  v2.1.3.0    | 2019-12-10   | Enhanced security |
|  v2.1.5.0    | 2020-02-13   | Update GDPR android fix some issues |
|  v2.9.0.0    | 2020-03-23   | Support template ads and native/ banner/ intersitial ad |
|  v2.9.0.1    | 2020-03-27   | Enhanced security |
|  v2.9.0.2    | 2020-04-23   | Support privacy protection |
|  v3.0.0.0    | 2020-06-15   | Support privacy protection and gdpr protection |
|  v3.0.0.1    | 2020-07-01   | fixed some network issues |
|  v3.1.0.0    | 2020-07-03   | Support more detail privacy protection and gdpr |
|  v3.1.0.1    | 2020-07-10   | fixed report issues  |



## 1.SDK Integration

### 1.1 Import the SDK Package

#### 1.1.1 Apply for App CodeId
Please create the app id and ad slot id on Pangle platform.

#### 1.1.2 Import SDK Essential: aar and jar Package
Copy open\_ad\_sdk.aar within the SDK package to your Application Module/libs folder (Create one manually if there isn't one), and add the following code to your app's build.gradle:

```
repositories {
    flatDir {
        dirs 'libs'
    }
}
depedencies {
    compile(name: 'open_ad_sdk', ext: ‘aar')
}
```

### 1.2 Android Manifest Configuration

#### 1.2.1 Add Permissions

```
<!--Required  permissions-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- If there is a video ad and it is played with textureView, please be sure to add this, otherwise a black screen will appear -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
```



#### 1.2.2 provider Settings

**Note**: Both single-process and multi-process should be configured.

```xml
<provider
    android:name="com.bytedance.sdk.openadsdk.multipro.TTMultiProvider"
    android:authorities="${applicationId}.TTMultiProvider"
    android:exported="false" />
```

#### 1.2.3 Operating Environment Configuration
This SDK runs on Android 4.0 (API Level 14) and above.

```xml
<uses-sdk android:minSdkVersion="14" android:targetSdkVersion="29" />
```
If the developer declares targetSdkVersion to be API 23 or more, be sure to obtain all permissions required by the SDK before calling any SDK interface, otherwise the SDK may not work properly.

### 1.3 Obfuscation
If you need to use proguard to obfuscate your code, be sure not to obfuscate the SDK code. Please add the following configuration at the end of the proguard.cfg file (or other obfuscated files):

```
-keep class com.bytedance.sdk.openadsdk.** { *; }
```

**Note**: If the SDK code is obfuscated, it will cause the ad to fail to display or other anomalies.  
**Note**: The SO file used in the SDK supports five architectures.：x86,x86\_64,armeabi,armeabi-v7a,arm64-v8a. If your app's architecture is not one of them, please select a supported architecture supported by abiFilters in build.gradle. Set the architecture of supported SO libraries. Please set it based on your actual situation.
```
ndk {  
//abiFilters 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86\_64', 'armeabi'
}
```

### 1.4 Initialize the SDK

The developer needs to call the following code in the `Application#onCreate()` method to initialize the Pangle SDK. The current SDK supports multiple processes. If it is clear that a process will not use the ad SDK, you can initialize the ad SDK for specific processes only.

```java
public class DemoApplication extends Application {
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    @Override
    public void onCreate() {
        super.onCreate();

        // It is strongly recommended to call in Application #onCreate () method to avoid content as null
        TTAdSdk.init(context,
                            new TTAdConfig.Builder()
                                    .appId("5001121")
                                    .useTextureView(false) 
                                    // Use TextureView to play the video. The default setting is SurfaceView, when the context is in conflict with SurfaceView, you can use TextureView
                                    .appName("APP Name")
                                    .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                                    .allowShowPageWhenScreenLock(true) 
                                    // Allow or deny permission to display the landing page ad in the lock screen
                                    .debug(true) 
                                    // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
                                    .supportMultiProcess(false) 
                                    // Whether to support multi-process, true indicates support
                                    .coppa(0) 
                                    // Fields to indicate SDK whether the user is a child or an adult ，0:adult ，1:child
                                    .setGDPR(0)
                                    //Fields to indicate SDK whether the user grants consent for personalized ads, the value of GDPR : 0 User has granted the consent for personalized ads, SDK will return personalized ads; 1: User doesn't grant consent for personalized ads, SDK will only return non-personalized ads.
                                    .build());
    }
}
```

#### 1.4.1 Initialization Interface

```java
    /**
     * Entrance of Pangle SDK initialization
     *
     * @param context Must be application context
     * @param config Initialize configuration and required parameters
     * @return TTAdManager instance
     */
    public static TTAdManager init (Context context, TTAdConfig config);
```

#### 1.4.2 Initialize Configuration Parameters:

```java
     public Static Class TTAdConfig.Builder {
        private String mAppId; // Required parameter, set the app's AppId
        private String mAppName; // Required parameter, set the app name
        private boolean mIsPaid = false ; // Optional parameter, set whether it is a paid user: true indicates paid user, false indicates non-paid user. Default setting is false, non-paid user
        private int mTitleBarTheme = TTAdConstant.TITLE_BAR_THEME_LIGHT ; // Optional parameter, set the landing page theme, the default setting is TTAdConstant#TITLE_BAR_THEME_LIGHT
        private boolean mIsDebug = false ; // Optional parameter, whether to enable debug information output: true indicates enable, false indicates disable. The default setting is false, disable
        private boolean mIsUseTextureView = false ; // Optional parameter, set whether to use texture to play the video: true indicates use, false indicates do not use. The default setting is false, do not use (the default setting is to use surface)
        private boolean mIsSupportMultiProcess = false ; // Optional parameter, set whether to support multi-process: true indicates support, false indicates do not support. The default setting is false, do not support
        private IHttpStack mHttpStack;// Optional parameter, developers can customize external network request libraries ，the SDK default HttpUrlConnection
        private boolean mIsAsyncInit = false;// Whether initialize the SDK asynchronously or not
	}
```

## 2. Load Ad

### 2.1 Build TTAdManager Object:

TTAdManager object is the interface to Integrate the entire SDK and can be used for ad acquisition, permission request, and version number acquisition etc. Construction method:

```java
//Must be called after initialization, otherwise it will be null
TTAdManager ttAdManager = TTAdSdk.getAdManager();
```

TTAdManger interface:

```java
public interface TTAdManager {
    // Must use activity to create TTADNative object
    TTAdNative createAdNative (Context context);

    /**
     * Get the Pangle SDK version number
     *
     * @return Version number
     */
    String getSDKVersion ();
    
    /**
    * Fields to indicate SDK whether the user grants consent for personalized ads, 
    * the value of GDPR :  0 : User has granted consent for personalized ads,
    *                      1 : User doesn't grant consent for personalized ads
    * @return 
    */
    int getGdpr();
        
    /**
    * show GDPR Privacy Protection Dialog
    */
    void showPrivacyProtection();
    }
}
```

### 2.2 Build TTADNative Object

TTAdNative is the interface to load ads, such as reward-video ads, full-video ads etc. It also provides a load callback listener. It is recommended to be the member variable of the Activity.

Construction method:

```java
TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(baseContext);//baseContext is suggested to be activity
```

TTAdNative interface:

```java
public interface TTAdNative {
    /**
     * Asynchronously load rewarded video ad, the result will be callback via RewardVideoAdListener
     * @param adSlot
     * @param listener
     */
    void loadRewardVideoAd (AdSlot adSlot, @NonNull RewardVideoAdListener listener);

    /**
     * Asynchronously load full-screen video ad, the result will be callback through FullScreenVideoAdListener
     * @param adSlot
     * @param Listener
     */
    void loadFullScreenVideoAd (AdSlot adSlot, @NonNull FullScreenVideoAdListener Listener);
    
    /**
     * @param adSlot, Requests configuration information.
     * @param listener, Calls back loading results.
     * Feed ads are loaded asynchronously, and the results will be called back via FeedAdListener.
     */
     void loadNativeAd(AdSlot adSlot, @NonNull NativeAdListener listener);

    /**
     * @param adSlot, Requests configuration information.
     * @param listener, Calls back loading results.
     * Feed ads are loaded asynchronously, and the results will be called back via FeedAdListener.
     */
    void loadFeedAd(AdSlot adSlot, @NonNull NativeAdListener listener);

    /**
     * @param adSlot, Requests configuration information.
     * @param listener, Calls back loading results.
     * Feed ads are loaded asynchronously, and the results will be called back via BannerAdListener.
     */
    void loadBannerAd(AdSlot adSlot, @NonNull BannerAdListener listener);

    /**
     * @param adSlot, Requests configuration information.
     * @param listener, Calls back loading results.
     * Feed ads are loaded asynchronously, and the results will be called back via InteractionAdListener
     */
    void loadInteractionAd(AdSlot adSlot, @NonNull InteractionAdListener listener);

     /**
     * Rewarded video ad loading Listener
     */
     Interface RewardVideoAdListener {
        /**
         * Callback of failed loading
         *
         * @param code
         * @param message
         */
        @MainThread
        void onError ( int code, String message);

        /**
         * Callback of completed ad loading, the developers can render during callback
         *
         * @param ad Rewarded video ad interface
         */
        @MainThread
        void onRewardVideoAdLoad (TTRewardVideoAd ad);

        /**
         * Callback of completed local ad loading, the developers can render during callback
         */
        void onRewardVideoCached ();
    }

     /**
     * Full-screen video ad loading listener
     */
     Interface FullScreenVideoAdListener {
        /**
         * Callback of failed loading
         *
         * @param code
         * @param message
         */
        @MainThread
        void onError ( int code, String message);

        /**
         * Callback of completed ad loading, the developers can render during this callback
         *
         * @param ad full-screen video ad interface
         */
        @MainThread
        void onFullScreenVideoAdLoad (TTFullScreenVideoAd ad);

        /**
         * Callback of completed local ad loading, the developers can render during callback
         */
        void onFullScreenVideoCached ();
}

    /**
     * Loading listener for Native Express Ad
     */
     Interface NativeExpressAdListener {

         /**
          * Callback for failed loading.
          *
          * @param code
          * @param message
          */
         @MainThread
         void onError(int code, String message);

         /**
          * Callback for successful ad loading. Rendering is available in this callback.
          *
          * @param ads. Returned ad list.
          */
         @MainThread
         void onNativeExpressAdLoad(List<TTNativeExpressAd> ads);

    /**
     * @param adSlot. Requests configuration information.
     * @param listener. Calls back loading results.
     * Feed ads are loaded asynchronously, and the results will be called back via NativeExpressAdListener.     
     */
    void loadNativeExpressAd(AdSlot adSlot, @NonNull NativeExpressAdListener listener);
    
    /**
     * @param adSlot. Requests configuration information.
     * @param listener. Calls back loading results.
     * Banner ads are loaded asynchronously, and the results will be called back via NativeExpressAdListener
     */
     void loadBannerExpressAd(AdSlot adSlot, @NonNull NativeExpressAdListener listener);
    
    /**
     * @param adSlot. Requests configuration information.
     * @param listener. Calls back loading results.
     * Interstitial Express ads are loaded asynchronously, and the results will be called back via NativeExpressAdListener
     */
     void loadInteractionExpressAd(AdSlot adSlot, @NonNull NativeExpressAdListener listener);

    /**
     * Loading listener for feed ads.
     */
    interface FeedAdListener {

        /**
         * Callback for failed loading.
         *
         * @param code
         * @param message
         */
        @MainThread
        void onError(int code, String message);

        /**
         * Callback for successful ad loading. Rendering is available in this callback.         *
         * @param ads. Returned ad list.
         */
        @MainThread
        void onFeedAdLoad(List<TTFeedAd> ads);

    }

    /**
     * Loading listener for native ads.
     */
    interface NativeAdListener {

        /**
         * Callback for failed loading.
         *
         * @param code
         * @param message
         */
        @MainThread
        void onError(int code, String message);

        /**
         * * Callback for successful ad loading. Rendering is available in this callback.
         * @param ads. Returned ad list.
         */
        @MainThread
        void onNativeAdLoad(List<TTNativeAd> ads);

    }

    /**
     * Loading listener for Banner.
     */
    interface BannerAdListener {

        /**
         * Callback for failed loading.
         * @param code
         * @param message
         */
        @MainThread
        void onError(int code, String message);

        /**
         *Callback for successful ad loading. Rendering is available in this callback.
         * @param ad
         */
        @MainThread
        void onBannerAdLoad(TTBannerAd ad);

    }

    /**
     * Loading listener for Interaction.
     */
    interface InteractionAdListener {
        /**
         * Callback for failed loading.
         * @param code
         * @param message
         */
        @MainThread
        void onError(int code,String message);

        /**
         * Callback for successful ad loading. Rendering is available in this callback.
         */
        @MainThread
        void onInteractionAdLoad(TTInteractionAd ad);
    }

}
```

### 2.3 Build AdSlot Object

The AdSlot object is the ad information that needs to be set when TTADNative loads the ad. 
Construction method:

```java
AdSlot adSlot = new AdSlot.Builder ()
    // Required parameter, set your CodeId
    .setCodeId (" 900486272 ")
    // Required parameter, set the maximum size of the ad image and the desired aspect ratio of the image, in units of Px
    // Note: If you select a native ad on the Pangle, the returned image size may differ significantly from the size you expect
    .setImageAcceptedSize (640, 320)
    // For template ads, please set the size of the customized template ads (in dp). If your code bit belongs to a customized template ad, please check it on the media platform.    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
    // Optional parameter, allow or deny permission to support deeplink
    .setSupportDeepLink (true)
    // Optional parameter, set the number of ads returned per request for in-feed ads, up to 3
    .setAdCount ( 1 )
    // Required parameter for native ad requests, choose TYPE_BANNER or TYPE_INTERACTION_AD
    .setNativeAdType (AdSlot.TYPE_BANNER )
    // Parameter for rewarded video ad requests, name of the reward
    .setRewardName ( "gold coin" )
    // The number of rewards in rewarded video ad
    .setRewardAmount ( 3 )
    // User ID, a required parameter for rewarded video ads
    // It is developer's unique identifier for users; sdk pass-through is not necessary if the server is not in callback mode, it can be set to an empty string
    .setUserID ( "user123" )
    // Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
    .setOrientation (orientation)
     // Pass-through parameters and strings of rewards in rewarded video ad, if you use json object, you must use serialization as String type, it can be empty
    .setMediaExtra ( "media_extra")
    .build ();
```

### 2.4 Integrating Native Feed Ads (TTFeedAd)

Pangle SDK provides the developers with feed ads whose layout is customizable, and includes 4 basic types: large image, small image, group image and video. Also, it provides 4 interaction types: phone call, app download, redirecting to landing page, and redirecting to browser.

#### 2.4.1 TTFeedAd Interface Description

```java
 public interface TTFeedAd {

    /**
     * Get Pangle logo. Image size: 80*80.
     *
     * @return bitmap object.
     */
    Bitmap getAdLogo();

    /**
     * Ads title.
     *
     * @return
     */
    String getTitle();

    /**
     * Ads description.
     *
     * @return
     */
    String getDescription();

    /**
     * Ads source.
     *
     * @return
     */
    String getSource();

    /**
     * Ad logo Image
     *
     * @return
     */
    TTImage getIcon();

    /**
     * Ad Image list
     *
     * @return
     */
    List<TTImage> getImageList();

    /**
     * Return Feed Ad Interaction Type.
     *
     * @return 2:open the ad page in browser，3: open the page in app webview，4:download the app，5:phone call，others: Unidentified.
     */
    int getInteractionType();


    /**
     * Return Feed Ad Image Type.
     *
     * @return 3:large image; 2:small image; 4:group image; 5:video; others: unidentified.
     */
    int getImageMode();

    /**
     * Return dislike dialog.
     *
     * @param activity. It is recommended to use the current activity, otherwise the dislike dialog may not pop up.
     * @return
     */
    TTAdDislike getDislikeDialog(Activity activity);

    /**
     * Customized dislike dialog
     *
     * @param dialog Customized dialog, obtained from an external source.
     * @return
     */
    TTAdDislike getDislikeDialog(TTDislikeDialogAbstract dialog);


    /**
     * After you register a clickable View, click/show will be completed internally.
     *
     * @param container     Renders the outermost ViewGroup of the ad.
     * @param clickView     Clickable View
     */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull View clickView, AdInteractionListener listener);

    /**
     * After you register a clickable View, click/show will be completed internally.     *
     * @param container     Renders the outermost ViewGroup of the ad.
     * @param clickViews    List of clickable views.
     * @param creativeViews     Views for downloading or making phone calls.
     */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, AdInteractionListener listener);

    /**
    * After you register a clickable View, click/show will be completed internally.Register dislike icon.
     *
     * @param container     Renders the outermost ViewGroup of the ad.
     * @param clickViews    List of clickable views.
     * @param creativeViews   Views for downloading or making phone calls.
     * @param dislikeView   dislike icon
     * @param listener      click callback
     */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, @Nullable View dislikeView, AdInteractionListener listener);

    /**
     * Get the view of an ad (such as the view of a video ad)
     *
     * @param autoPlay  Is the video automatically played
     * @param quiet     Is the video muted
     * @return
     */
    @Deprecated
    View getAdView(boolean autoPlay, boolean quiet);

    /**
     * Get the view of an ad (such as the view of a video ad). You can set whether videos are automatically played or muted on the platform.
     * @return
     */
    View getAdView();

    /**
     * Callback interface for feed video ads.
     */
    interface VideoAdListener {

        /**
         * Video ad loading successful
         *
         * @param ad
         */
        void onVideoLoad(TTFeedAd ad);

        /**
         * Video ad loading failed (error type provided by the native media player).
         * @param errorCode ：
         *                  MEDIA_ERROR_UNKNOWN
         *                  MEDIA_ERROR_SERVER_DIED
         *
         * @param extraCode 
         *                  MEDIA_ERROR_IO
         *                  MEDIA_ERROR_MALFORMED
         *                  MEDIA_ERROR_UNSUPPORTED
         *                  MEDIA_ERROR_TIMED_OUT
         *                  MEDIA_ERROR_SYSTEM
         *
         */
        void onVideoError(int errorCode, int extraCode);

        /**
         * Callback for playing video ads.
         *
         * @param ad
         */
        void onVideoAdStartPlay(TTFeedAd ad);

        /**
         * Callback for pausing video ads.
         *
         * @param ad
         */
        void onVideoAdPaused(TTFeedAd ad);

        /**
         * Resume video ads.
         *
         * @param ad
         */
        void onVideoAdContinuePlay(TTFeedAd ad);
    }


    void setVideoAdListener(VideoAdListener videoAdListener);

}
```

#### 2.4.2  Loading Native feed Ads

You can call the TTAdNative.loadFeedAd(AdSlotadSlot,FeedAdListenerlistener) method to asynchronously load feed ads. AdSlot is the information requested by the user, and FeedAdListener is a callback listener that indicates if the loading succeeds or fails. For example:

```java
mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
    @Override
    public void onError(int code, String message) {
        //For failed loading callbacks, please refer to "Error Code Description"
        Toast.makeText(FeedActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFeedAdLoad(List<TTFeedAd> ads) {
        //For successful loading callbacks, please make sure your code is robust enough to overcome exceptions.
       if (ads == null || ads.isEmpty()) {
            Toast.makeText(FeedActivity.this, "on FeedAdLoaded: ad is null!", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < ads.size(); i++) {
            mData.set((AD_POSITION + i) % mData.size(), ads.get(i));
        }
        myAdapter.notifyDataSetChanged();
    }
});
```

For more examples regarding feed ads, please refer to the FeedActivity class in the demo and the listitem\_ad\_pic.xml file under reslayout directory.

####  2.4.3 Callback For Native Feed Video Ads

If you need additional video operations callback such as acquiring video loading status, pausing and resuming video, and other operations, please use the VideoAdListener interface.

```java
ttFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
        @Override
        public void onVideoLoad(TTFeedAd ad) {
            if (ad != null) {
                Log.d("ad","Video successfully loaded");
            }
        }

        @Override
        public void onVideoError(int errorCode, int extraCode) {
                Log.d("ad","Video playback error：errorCode="+errorCode+",extraCode="+extraCode);
        }

        @Override
        public void onVideoAdStartPlay(TTFeedAd ad) {
            if (ad != null) {
                Log.d("ad","Start Video");

            }
        }

        @Override
        public void onVideoAdPaused(TTFeedAd ad) {
            if (ad != null) {
                Log.d("ad","Pause Video");

            }
        }

        @Override
        public void onVideoAdContinuePlay(TTFeedAd ad) {
            if (ad != null) {
                Log.d("ad","Resume Video");

            }
        }
    });   
```

#### 2.4.4 Registering Clickable Views

After loading the feed ad, you'll need to register clickable view in the feed ad, that is, the TTFeedAd.registerViewForInteraction () method, to realize the billing and interaction of your ads. It contains the registration of both image clicking areas and additional creative clicking areas. For landing page ads, when the user clicks the ad image area, they will be redirected to the configured landing page, and when they click the creative area, they will make calls or download apps accordingly.
**Note**: If you want users to make calls or download an app when they click the image area, please pass the view of the image area to the creativeViewList. The detailed codes are as follows:

The sample codes for registering and clicking view are as follows. This example is called in the getView() method in BaseAdapter.

```java
private void bindData(View convertView, final AdViewHolder adViewHolder, TTFeedAd ad) {
    // Clickable view. You can also put the convertView here, and the entire item will become clickable. After user clicks it, they will be redirected to the landing page.
    List<View> clickViewList = new ArrayList<View>();
    clickViewList.add(convertView);
    // After user clicks the creative area, they will download an app or make calls based accordingly.
    //If you want users to make calls or download an app when they click the image area, please pass the view of the image area to the creativeViewList.
    List<View> creativeViewList = new ArrayList<View>();
    creativeViewList.add(adViewHolder.mCreativeButton);
    // Register ordinary and creative clicking areas. Important! This must be called correctly because it is related to ad billing and interaction. ConvertView must use ViewGroup.
    ad.registerViewForInteraction((ViewGroup) convertView, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
        @Override
        public void onAdClicked(View view, TTNativeAd ad) {
            // Callback when user clicks the ordinary area.
            Toast.makeText(mContext, "Ad is clicked.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ad) {
              // Callback when user clicks the creative area.
            Toast.makeText(mContext, "Ad creative button is clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdShow(TTNativeAd ad) {
           // Callback when the ad is displayed
            TToast.show(mContext, "ad" + ad.getTitle() + "show");
        }
    });
    ...
```

For more information, please refer to the listitem\_ad\_pic.xml file under FeedActivity or res/layout/.

### 2.5 Integrating Banner Ads (TTBannerAd)

SDK provides the developers with banner ads whose layout is customizable. The width and height of the view is match_parent as default, user can set the customized size or set the parent layout's size to determine the banner ad's width and height. (Notice: the width and height of the banner shoule be close to the size of the adslot, to avoid to much non-uniform stretching.)
There are multiple size scale that banner ad support：
600\*300，600\*400，600\*500，600\*260，600\*90，600\*150，640\*100，690\*388

#### 2.5.1 TTBannerAd Interface Description
```java
 public interface TTBannerAd {

    /**
     * get Banner ad
     *
     * @return
     */
    View getBannerView();

    /**
     * register Banner ad interaction callback
     *
     * @param listener listener
     */
    void setBannerInteractionListener(AdInteractionListener listener);

    /**
     * Return banner Ad Interaction Type.
     *
     * @return 2:open the ad page in browser，3: open the page in app webview，4:download the app，5:phone call，others: Unidentified.
     */
    int getInteractionType();

    /**
     * set the logic of using tt_dislike_icon
     *
     * @param dislikeInteractionCallback    Dislike selection result
     */
     void setShowDislikeIcon(TTAdDislike.DislikeInteractionCallback dislikeInteractionCallback);

    /**
     * Return dislike dialog.
     *
     * @param dislikeInteractionCallback 
     */
     TTAdDislike getDislikeDialog(TTAdDislike.DislikeInteractionCallback dislikeInteractionCallback);
     

    /**
     * Custom dislike dialog
     *
     * @param dialog    Customized dialog, obtained from an external source.
     * @return
     */
    TTAdDislike getDislikeDialog(TTDislikeDialogAbstract dialog);

     /**
     * Set Banner ads carousel time interval，if null, the carousel will not be played
     *
     * @param intervalTime  Carousel time interval，the value is between 30 * 1000 ~ 120 * 1000
     */
    void setSlideIntervalTime(int intervalTime);

    /**
     * Banner interaction listener
     */
    interface AdInteractionListener {

        /**
         * Banner click callback
         * @param view  Banner ad
         * @param type  Banner interaction type
         */
        void onAdClicked(View view, int type);

        /**
         * Banner display callback, only once for each ad 
         * @param view Banner
         * @param type Banner: interaction type
         */
        void onAdShow(View view, int type);

    }
}
```

#### 2.5.2  Load  Banner Ads

You can call the TTAdNative.loadBannerAd(AdSlotadSlot,BannerAdListenerlistener) method to asynchronously load Banner ads. AdSlot is the information requested by the user, TTAdNative.BannerAdListener is a callback listener that indicates if the loading succeeds or fails. For example:

```java
mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

    @Override
    public void onError(int code, String message){        
    //For failed loading callbacks, please refer to "Error Code Description"
       Toast.makeText(BannerActivity.this, "load error : " + code + ", " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerAdLoad(TTBannerAd ad) {        
    //For successful loading callbacks, please make sure your code is robust enough to overcome exceptions.
      View bannerView = ad.getBannerView();
        if (bannerView == null) {
            return;
        }
        mBannerContainer.removeAllViews();
        mBannerContainer.addView(bannerView);
    }
});
```
For more examples , please refer to the BannerActivity class in the demo.

### 2.6 Integrating Interstitial Ads（TTInteractionAd）
SDK provides the developers with interaction ads，which will pop-up a dialog above the activity. The usage scenarios include but not limited to:
1. Expand when entering: when the user enters the app homepage or enters the details page, the interaction ad will pop up;
2. Expand when slide to the bottom: when the user browses the details page and down to the bottom, the interaction ad will pop-up;
3. Expand when slide back: when the user slides back to the previous page, the interaction ad will pop-up;

The scale supported: 1:1, 3:2, 2:3. When pass in the request params, it is necessary to request the same scale size , such as the scale of 1:1, you can request for 600x600 size ad.

#### 2.6.1 TTInteractionAd Interface Description：

```java
public interface TTInteractionAd {
    /**
     *  register interaction ad interaction callback
     *
     * @param listener 
     */
    void setAdInteractionListener(AdInteractionListener listener);


    /**
     *  Return Interaction Ad Interaction Type.
     *
     * @return  2:open the ad page in browser，3:open the page in app webview，4:download the app，5:phone call，others: Unidentified.
     */
    int getInteractionType();

    /**
     * @param activity 
     */
    @MainThread
    void showInteractionAd(Activity activity);

    /**
     * interaction listener
     */
    interface AdInteractionListener {

        /**
         * click callback
         */
        void onAdClicked();

        /**
         * display callback, only once for each ad.
         */
        void onAdShow();

        /**
         * ad close callback
         */
        void onAdDismiss();
    }
}    
```

#### 2.6.2 Load  Interstital Ads

You can call the TTAdNative.loadInteractionAd(AdSlot adSlot, @NonNull InteractionAdListener listener) method to asynchronously load interaction ads. AdSlot is the params requested by the user, InteractionAdListener is the callback listener that indicates if the loading succeeds or fails. For example:：

```java
mTTAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
    @Override
    public void onError(int code, String message) {
        Toast.makeText(geclicklicationContext(), "code: " + code + "  message: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
        Toast.makeText(geclicklicationContext(), "type:  " + ttInteractionAd.getInteractionType(), Toast.LENGTH_SHORT).show();
        ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
            @Override
            public void onAdClicked() {
                Log.d(TAG, "click");
                Toast.makeText(mContext, "ad is clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "show");
                Toast.makeText(mContext, "ad displayed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdDismiss() {
                Log.d(TAG, "ad closed");
                Toast.makeText(mContext, "ad closed", Toast.LENGTH_SHORT).show();
            }
        });
 
        //pop up interstitial ads
        ttInteractionAd.showInteractionAd(InteractionActivity.this);
        }
});
```

#### 2.6.3  Display Interstitial Ads
When the interaction ad loads successfully, call ttInteractionAd.showInteractionAd() method to show the ad。
**Notice：call showInteractionAd() in the main thread**

```java
//pop-up interaction ads
ttInteractionAd.showInteractionAd(InteractionActivity.this);
```

For more examples, please refer to the demo.InteractionActivity.

### 2.7  Integrating Native ads（TTNativeAd）
SDK provides the developers with native ads，which supports native banner and native interstitial，the integrating method is same with the TTFeedAd.

#### 2.7.1 TTNativeAd interface Description：
```java
public interface TTNativeAd {

    /**
     * Get Pangle logo. Image size: 80*80
     *
     * @return bitmap
     */
    Bitmap getAdLogo();

    /**
     * Ads title.
     *
     * @return
     */
    String getTitle();

    /**
     * Ads description.
     *
     * @return
     */
    String getDescription();

    /**
     * Ads source.
     *
     * @return
     */
    String getSource();

    /**
     * Ad icon Image
     *
     * @return
     */
    TTImage getIcon();

    /**
     * Ad Image list
     *
     * @return
     */
    List<TTImage> getImageList();

    /**
    * Return Native Ad Interaction Type.
    *
    * @return 2:open the ad page in browser，3:open the page in app webview，4:download the app，5:phone call，others: Unidentified.     
    */
    int getInteractionType();

    /**
     * Return Native Ad Image Type.
     *
     * @return 3 large image;2 small image;4 group image;5 video; others Unidentified.
     */
    int getImageMode();

    /**
     * Return dislike dialog
     *
     * @param activity  It is recommended to pass in the current activity, otherwise the dislike dialog may not pop up.
     * @return
     */
    TTAdDislike getDislikeDialog(Activity activity);

    /**
     * custom dislike dialog
     *
     * @param dialog customized dialog, obtained from an external source.
     * @return
     */
    TTAdDislike getDislikeDialog(TTDislikeDialogAbstract dialog);

   
    /**
     * After you register a clickable View, click/show will be completed internally.
     *
     * @param container: Renders the outermost ViewGroup of the ad.
     * @param clickView : Clickable View
     */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull View clickView, AdInteractionListener listener);

    /**
     * After you register a clickable View, click/show will be completed internally. 
     *
     * @param container     Renders the outermost ViewGroup of the ad.
     * @param clickViews    List of clickable views.
     * @param creativeViews  Views for downloading or making phone calls.
     */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, AdInteractionListener listener);

    /**
     * After you register a clickable View, click/show will be completed internally.     
    *
    * @param container     Renders the outermost ViewGroup of the ad. 
    * @param clickViews    List of clickable views.
    * @param creativeViews   Views for downloading or making phone calls.
    * @param dislikeView   dislike icon
    * @param listener      Click callback     
    */
    void registerViewForInteraction(@NonNull ViewGroup container, @NonNull List<View> clickViews, @Nullable List<View> creativeViews, @Nullable View dislikeView, AdInteractionListener listener);
    
    View getAdView();

    /**
     * Callback Interface of the feed ad interaction.
     */
    interface AdInteractionListener {

        /**
         * The callback of the ad.
         *
         * @param ad
         */
        void onAdClicked(View view, TTNativeAd ad);

        /**
         * Callback when user clicks the creative area.
         *
         * @param view
         * @param ad
         */
        void onAdCreativeClick(View view, TTNativeAd ad);

        /**
        * Callback when the ad is displayed, only once for each ad.
         *
         * @param ad
         */
        void onAdShow(TTNativeAd ad);
    }
}
```

#### 2.7.2 Load Native Ads

You can call the `loadNativeAd(AdSlot adSlot, @NonNull NativeAdListener listener)` method to asynchronously load native ads. AdSlot is the information requested by the user, and NativeAdListener is a callback listener that indicates if the loading succeeds or fails. For example:

```java
//create the ad request params as AdSlot and pay attention to the setNativeAdtype method. For specific meaning of params, refer to the document.
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .setNativeAdType(AdSlot.TYPE_BANNER) //You must call this method when request the native ad, and set the params as TYPE_BANNER or TYPE_INTERACTION_AD
                .setAdCount(1)
                .build();

//request ads and render the returned ad.       
    mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(NativeBannerActivity.this, "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (ads.get(0) == null) {
                    return;
                }
                View bannerView = LayoutInflater.from(mContext).inflate(R.layout.native_ad, mBannerContainer, false);
                if (bannerView == null) {
                    return;
                }
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                // Demo NativeBannerActivity
                setAdData(bannerView, ads.get(0));
            }
        });
```

#### 2.7.3 Display Native Ads 

```java
mBannerContainer.removeAllViews();
mBannerContainer.addView(bannerView);       
```
For more information, please refer to demo directoryNativeBannerActivity。

### 2.8 Integrating Rewarded Video Ad ( TTRewardVideoAd )

The SDK provides rewarded video ad for the developers, and the user will be issued a reward upon completion of the video ad, usage scenarios include but are not limited to:
•    Watch video ad in games and get in-game gold coins, etc.: Users must watch the full video to get rewards;
•    Points apps;
•    Supported ad sizes: support full-screen display with horizontal screen and vertical screen, the default setting is horizontal screen

#### 2.8.1 TTRewardVideoAd Interface:

```java
public interface TTRewardVideoAd {
    /**
     * Set video interaction callback
     */
    void setRewardAdInteractionListener(RewardAdInteractionListener listener);


    /**
     * Returns the interaction type of the video ad, the current interaction type is downloading
     */
    int getInteractionType ();

    /**
     * Show video ad
     */
    @MainThread
    void showRewardVideoAd(Activity activity);

    /**
     * Video interactive callback interface
     */
    public interface RewardAdInteractionListener {
        //Video ad display callback
        void onAdhow ();

        //Ad download bar click callback
        void onAdVideoBarClick ();

        //Video ad close callback
        void onAdClose ();

        //Video ad play callback
        void onVideoComplete ();

        //video ad play error
        void onVideoError();

        //Call back of video ad completion and reward validation. The parameters are valid, the number of rewards, and the name of the reward.
        void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName);
    }
}
```
#### 2.8.2  Load Rewarded Video Ad

The developers can call `TTADNative.loadRewardVideoAdA ( dSlot var1, @NonNull TTAdNative.RewardVideoAdListener var2)` to asynchronously load the rewarded video ad, adSlot is the information requested by the user, and RewardVideoAdListener is the callback listener of a successful or failed loading.

```java
AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("901121430")
                .setSupportDeepLink(true)
                .setAdCount(2)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("gold coin") //name of the reward
                .setRewardAmount(3) // number of rewards
                // It is developer's unique identifier for users; sdk pass-through is not necessary if the server is not in callback mode               
                // can be set to an empty string
                .setUserID("user123")
                .setOrientation(orientation) // Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
                .setMediaExtra("media_extra") // pass-through user information, not mandatory
                .build();
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Toast.makeText(RewardVideoActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            //The loaded video file is cached to the local callback
            @Override
            public void onRewardVideoCached() {
                Toast.makeText(RewardVideoActivity.this, "rewardVideoAd video cached", Toast.LENGTH_SHORT).show();
            }

            //Video creatives are loaded into, such as title, video url, etc., excluding video files
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Toast.makeText(RewardVideoActivity.this, "rewardVideoAd loaded", Toast.LENGTH_SHORT).show();
                mttRewardVideoAd = ad;
                //mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd show", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd bar click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClose() {
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd close", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVideoComplete() {
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVideoError() {
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd onVideoError", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Toast.makeText(RewardVideoActivity.this, "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
```

#### 2.8.3 Display Reward-video Ads
```java
mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
```
Please refer to the demo example for specific usage.

### 2.9 Integrating Full-screen Video Ad ( TTFullScreenVideoAd )
The SDK provides full-screen video ad for the developers, the ad plays in full screen and can be skipped after a certain time, watching the whole video is not required. Supported ad sizes: support full-screen display horizontally or vertically, the default setting is to play horizontally.

#### 2.9.1 TTFullScreenVideoAd Interface:
```java
public interface TTFullScreenVideoAd {
    /**
     * Register interstitial ad interactive callback
     *
     * @param listener
     */
    void setFullScreenVideoAdInteractionListener(FullScreenVideoAdInteractionListener listener);

    /**
     * Get the interaction type for the ad
     *
     * @return 2 open in the browser (normal type) 3 landing page (normal type), 4: app download, 5: telephone dialing - 1 unknown type
     */
    int getInteractionType();

    /**
     * @param activity
     * display ad
     */
    @MainThread
    void showFullScreenVideoAd(Activity activity);

    void setShowDownLoadBar(boolean showDownLoadBar);

    /**
     * Full-screen video ad interactive listener
     */
    interface FullScreenVideoAdInteractionListener {
        /**
         * Ad display callback once per ad
         */
        void onAdShow();

        /**
         * Ad download bar click callback
         */
        void onAdVideoBarClick();

        /**
         * Ad closed callback
         */
        void onAdClose();

        /**
         * Callback after the video has finished playing
         */
        void onVideoComplete();

        /**
         * Skip video
         */
        void onSkippedVideo();

    }
}
```

#### 2.9.2 Load Full-screen Video Ad
The developers can call `loadFullScreenVideoAd(AdSlot adSlot, @NonNull FullScreenVideoAdListener Listener)` to asynchronously load a full-screen video ad, adSlot is the information requested by the user, and FullScreenVideoAdListener is the callback listener of a successful or failed loading.
```java
 // Set the ad parameters
 AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(orientation)
                .build();
        // Load full-screen video ad
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(FullScreenVideoActivity.this, message);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                TToast.show(FullScreenVideoActivity.this, "FullVideoAd loaded");
                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd close");
                    }

                    @Override
                    public void onVideoComplete() {
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
                        TToast.show(FullScreenVideoActivity.this, "FullVideoAd skipped");

                    }

                });
            }

            @Override
            public void onFullScreenVideoCached() {
                TToast.show(FullScreenVideoActivity.this, "FullVideoAd video cached");
            }
        });
```

#### 2.9.3 Display Video Ad
```java
mttFullVideoAd.showFullScreenVideoAd(FullScreenVideoActivity.this);
```

For detailed code, please refer to FullScreenVideoActivity in demo.

### 2.10  Integrating Customized Template Feed Ads （TTNativeExpressAd）

SDK provides users with customized template feed ads, supporting graphic and video styles. Developers do not need to edit and render ads by themselves. They can directly call relevant interfaces to obtain ads views, ad templates, and support fine-tune for later editor.

#### 2.10.1 TTNativeExpressAd Interface Description：
```java
/**
* Operation interface for entity class of customized template feed ads.
*/
 public interface TTNativeExpressAd {
    /**
     * Get the view of customized template feed ads.
     *
     * @return
     */
    View getExpressAdView();

    /**
     * Get native ad image type.
     *
     * @return 3 large image ; 2 small image ; 4 group image ; 5 video ; others Unidentified.
     */
    int getImageMode();

    /**
     * Return dislike dialog.
     *
     * @return
     */
    List<FilterWord> getFilterWords();

    /**
     * Register interactive callback for customized template feed ads.
     *
     * @param listener 
     */
    void setExpressInteractionListener(ExpressAdInteractionListener listener);
    
     /**
      * Register interactive callback for customized template interstitial ads.
      *
      * @param listener
      */
     void setExpressInteractionListener(AdInteractionListener listener);

    /**
     *  Return Ad Interaction Type.
     *
     * @return 2:open the ad page in browser，3:open the page in app webview，4:download the app，5:phone call，others: Unidentified.
     */
    int getInteractionType();

    /**
     * Render customized template ads.
     */
    void render();

    /**
     * Release resources.
     */
    void destroy();

    /**
     * Set to use dislike in customized template feed ads.
     *
     * @param activity Create dislike context
     * @param dislikeInteractionCallback Callback of dislike selection
     */
    void setDislikeCallback(Activity activity, TTAdDislike.DislikeInteractionCallback dislikeInteractionCallback);

    /**
     * Customize dislike dialog
     *
     * @param dialog. Customized dialog, obtained from an external source.
     * @return
     */
    void setDislikeDialog(TTDislikeDialogAbstract dialog);

    /**
     * Display interstitial ads.
     *
     * @param activity Host activity. The user determines if the host is finished.
     */
    @MainThread
    void showInteractionExpressAd(Activity activity);

    /**
     *Set Banner ads carousel time interval，if null, the carousel will not be played
     *
     * @param intervalTime  Carousel time interval，the value is between 30 * 1000 ~ 120 * 1000
     */
    void setSlideIntervalTime(int intervalTime);
    
     /**
     * Set the listener for video ad callbacks.
     *
     * @param videoAdListener  
     */
    void setVideoAdListener(ExpressVideoAdListener videoAdListener);

    /**
     * If video in customized template ads can be paused or resumed.
     *
     * @param canInterruptVideoPlay
     */
    void setCanInterruptVideoPlay(boolean canInterruptVideoPlay);
    
    /**
     * Ad interaction listener.
     */
    interface ExpressAdInteractionListener {

        /**
         *Callback for ad clicks.
         * @param view 
         * @param type 
         */
        void onAdClicked(View view, int type);

        /**
         * Callback for ad display. Each ad is called back only once.
         * @param view 
         * @param type 
         */
        void onAdShow(View view, int type);

        /**
         * Customized template ad rendering failed.
         * @param view
         */
        void onRenderFail(View view, String msg, int code);

        /**
         * Customized template rendering succeed.
         * @param view
         * @param width     Width of the returned view (in dp).
         * @param height    Height of the returned view (in dp).
         */
        void onRenderSuccess(View view, float width, float height);
    }
    
    /**
     * Interactive listener for customized template interstitial ads.
     */
    interface AdInteractionListener extends ExpressAdInteractionListener {
    
        /**
         * Callback for ad dismiss
         */
        void onAdDismiss();
    }
    
    /**
     * Callback for customized template video feed ads.
     */
    interface ExpressVideoAdListener {

        /**
         * Video ad loading successful.
         */
        void onVideoLoad();

        /**
         * Video ad loading failed (error type will be provided by the native media player).
         * @param errorCode 
         *                  MEDIA_ERROR_UNKNOWN
         *                  MEDIA_ERROR_SERVER_DIED
         *
         * @param extraCode 
         *                  MEDIA_ERROR_IO
         *                  MEDIA_ERROR_MALFORMED
         *                  MEDIA_ERROR_UNSUPPORTED
         *                  MEDIA_ERROR_TIMED_OUT
         *                  MEDIA_ERROR_SYSTEM
         *
         */
        void onVideoError(int errorCode, int extraCode);

        /**
         * Callback for playing video ads.
         */
        void onVideoAdStartPlay();

        /**
         * Callback for pausing video ads.
         */
        void onVideoAdPaused();

        /**
         * Resume video ads.
         */
        void onVideoAdContinuePlay();

        /**
         * Video payback progress.
         *
         * @param current
         * @param duration
         */
        void onProgressUpdate(long current, long duration);

        /**
         * Complete callback for playing video ads
         */
        void onVideoAdComplete();

        /**
         * Click to retry.
         */
        void onClickRetry();
    }
}
```

#### 2.10.2  Load Customized Template Feed Ads, Link Interface Callbacks and Dislike Logic
You can call the `loadNativeExpressAd (AdSlotadSlot, NonNullNativeExpressAdListenerlistener)` to asynchronously load ads. AdSlot is the information requested by the user, and FeedAdListener is a callback listener that indicates if the loading succeeds or fails.

**Note**: If you set the height of the customized template to 0, it will be automatically adjusted. Dislike logic will help improve users' advertising experience and matching suitability. Please follow the instructions to set it. **If you do not set the dislike callback, the dislike logic in the layout will not take effect.**


```java
 //Set ad params
   AdSlot adSlot = new AdSlot.Builder()
                  .setCodeId(codeId) //ad slot id
                  .setSupportDeepLink(true)
                  .setAdCount(1) //The number of requested ads must be between 1 to 3.
                  .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //The desired size of customized template ad (in dp).
                  .setImageAcceptedSize(640,320) //This parameter does not affect the size of customized template ads.
                  .build();
       //load ads
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
                         mTTAd.render();//Call render to start rendering ads.
                     }
                 });
    //Link ad behavior.
    private void bindAdListener(TTNativeExpressAd ad) {
            ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    TToast.show(mContext, "ads clicked");
                }
    
                @Override
                public void onAdShow(View view, int type) {
                    TToast.show(mContext, "ads displayed");
                }
    
                @Override
                public void onRenderFail(View view, String msg, int code) {
                    Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                    TToast.show(mContext, msg+" code:"+code);
                }
    
                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    //return the width and height of view(in dp)
                    TToast.show(mContext, "render success");
                    //display ad when it render success
                    mExpressContainer.removeAllViews();
                    mExpressContainer.addView(view);
                }
            });
            //dislike settings
            bindDislike(ad, false);
            if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
                return;
            }
        }
        /**
         * Set ad dislike, developers can customize its style.
         * @param ad
         * @param customStyle   Whether the style is customized. True: yes.
         */
        private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
            if (customStyle) {
                //Customized style 
                List<FilterWord> words = ad.getFilterWords();
                if (words == null || words.isEmpty()) {
                    return;
                }
        
                final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
                dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick(FilterWord filterWord) {
                        //Block ads.
                        TToast.show(mContext, "click " + filterWord.getName());
                        //After user selects the reason why they dislike the ad, the ad will be removed.
                        mExpressContainer.removeAllViews();
                    }
                });
                ad.setDislikeDialog(dislikeDialog);
                return;
            }
            //Use the default dislike pop-up style in the default template.
            ad.setDislikeCallback(NativeExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    TToast.show(mContext, "click " + value);
                    //After user selects the reason why they dislike the ad, the ad will be removed.
                    mExpressContainer.removeAllViews();
                }
        
                @Override
                public void onCancel() {
                    TToast.show(mContext, "click cancel ");
                }
            });
        }
        
        //Release the resources occupied by ads at the right time.
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (mTTAd != null) {
                //callback destroy() method to release.
                mTTAd.destroy();
            }
        }
```

#### 2.10.3 Display Customized Template Feed Ads

```java
 public void onRenderSuccess(View view, float width, float height) {
                    //Width and height of the returned view (in dp).
                    TToast.show(mContext, "Rendering successful");
                    //Display ads after rendering is successfully called back so as to improve user's experience.
                    mExpressContainer.removeAllViews();
                    mExpressContainer.addView(view);
                }
```
After loading the ad, call the TTNativeExpressAd.render() method to render the ad. After that, get the rendered ad from the onRenderSuccess(Viewview,floatwidth,floatheight) method and add it to the container for display.
For more details, please refer to the NativeExpressActivity in the demo. Please use the parameter NativeExpressListActivity in feed scenarios.

### 2.11  Integrating Customized Template Banner Ads (Same Interface as TTNativeExpressAd)
SDK provides users with customized template Banner ads, supporting graphic and video styles. Developers do not need
to edit and render ads by themselves. They can directly call relevant interfaces to obtain ads views, ad templates, and support fine-tune for later edit.

#### 2.11.1 Load Customized Template Banner Ads, Linking Interface Callbacks, and Dislike Logic
You can call the `loadBannerExpressAd(AdSlot adSlot, @NonNull NativeExpressAdListener listener)`to asynchronously load ads. AdSlot is the information requested by the user, and NativeExpressAdListeneris a callback listener that indicates if the loading succeeds or fails.

**Note**:
If you set the height of the customized template to 0, it will be automatically adjusted. Please keep the size consistent with the platform setting. Dislike logic will help improve users' advertising experience and matching suitability. Please follow the instructions to set it. **If you do not set the dislike callback, the dislike logic in the layout will not take effect.**

```java
 //Set ad params
   AdSlot adSlot = new AdSlot.Builder()
                  .setCodeId(codeId) //slot id
                  .setSupportDeepLink(true)
                  .setAdCount(1) //The number of requested ads must be between 1 to 3.
                  .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //The desired size of customized template ad (in dp).
                  .setImageAcceptedSize(640,320 )//This parameter does not affect the size of customized template ads.
                  .build();
       //load ads
         mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
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
                         mTTAd.setSlideIntervalTime(30*1000);//Set Banner ads carousel time interval(in ms)，if null, the carousel will not be
played.
                         bindAdListener(mTTAd);
                         mTTAd.render();//
                     }
                 });
    //link ads behavior.
    private void bindAdListener(TTNativeExpressAd ad) {
            ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int type) {
                    TToast.show(mContext, "ads clicked");
                }
    
                @Override
                public void onAdShow(View view, int type) {
                    TToast.show(mContext, "ads displayed");
                }
    
                @Override
                public void onRenderFail(View view, String msg, int code) {
                    Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                    TToast.show(mContext, msg+" code:"+code);
                }
    
                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    //return the width and height of view(in dp)
                     TToast.show(mContext,"render success");
                    //display ad when it render success                    mExpressContainer.removeAllViews();
                    mExpressContainer.addView(view);
                }
            });
            //dislike settings
            bindDislike(ad, false);
            if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
                return;
            }
        }
        /**
         * Set ad dislike, developers can customize its style.
         * @param ad
         * @param customStyle Whether the style is customized. True: yes.
         */
        private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
            if (customStyle) {
                //customized style.
                List<FilterWord> words = ad.getFilterWords();
                if (words == null || words.isEmpty()) {
                    return;
                }
        
                final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
                dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick(FilterWord filterWord) {
                        //Block ads
                        TToast.show(mContext, "click" + filterWord.getName());
                        //After user selects the reason why they dislike the ad, the ad will be removed.
                        mExpressContainer.removeAllViews();
                    }
                });
                ad.setDislikeDialog(dislikeDialog);
                return;
            }
            //Use the default dislike pop-up style in the default template.
            ad.setDislikeCallback(BannerExpressActivity.this, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    TToast.show(mContext, "click" + value);
                    //After user selects the reason why they dislike the ad, the ad will be removed.
                    mExpressContainer.removeAllViews();
                }
        
                @Override
                public void onCancel() {
                    TToast.show(mContext, "click cancel");
                }
            });
        }
        
        //Release the resources occupied by ads at the right time.
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (mTTAd != null) {
                //callback destroy() method to release.
                mTTAd.destroy();
            }
        }
```

#### 2.11.2 Display Customized Template Banner Ads

```java
 public void onRenderSuccess(View view, float width, float height) {
                     //Width and height of the returned view (in dp).
                    TToast.show(mContext,"Rendering successful");
                    //Display ads after rendering is successfully called back so asto improve user's experience.                    mExpressContainer.removeAllViews();
                    mExpressContainer.addView(view);
                }
```
After loading the ad, call the TTBannerExpressAd.render() method to render the ad. After that, get the rendered ad from the onRenderSuccess(Viewview,floatwidth,floatheight) method and add it to the container for display.
For more details, please refer to the BannerExpressActivity in the demo. 

### 2.12 Integrating Customized Template Interstitial Ads (Same Interface as TTNativeExpressAd)
SDK provides users with customized template interstitial ads. Developers do not need to edit and render ads by themselves. They can directly call relevant interfaces to obtain ads views, ad templates, and support fine-tune for later edit.

#### 2.12.1 Loading Customized Template Interstital Ads, Linking Interface Callbacks

You can call the `loadInteractionExpressAd(AdSlot adSlot, @NonNull NativeExpressAdListener listener)`to asynchronously load ads. AdSlot is the information requested by the user, and NativeExpressAdListener is a callback listener that indicates if the loading succeeds or fails.


**Note**: If you set the height of the customized template to 0, it will be automatically adjusted. Please keep the size consistent with the platform setting. Please follow the instructions to set it. **Interstitial ad doesn't support dislike settings**

```java
 //Set ad params
   AdSlot adSlot = new AdSlot.Builder()
                  .setCodeId(codeId) //slot id
                  .setSupportDeepLink(true)
                  .setAdCount(1) //The number of requested ads must be between 1 to 3.
                  .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //The desired size of customized template ad (in dp).
                  .setImageAcceptedSize(640,320 )//This parameter does not affect the size of customized template ads.
                  .build();
       //load ads
         mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
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
                         mTTAd.render();//
                     }
                 });
    //link ads behavior
    private void bindAdListener(TTNativeExpressAd ad) {
            ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                
                @Override
                public void onAdDismiss() {
                    TToast.show(mContext, "ad closed");
                }
                
                @Override
                public void onAdClicked(View view, int type) {
                    TToast.show(mContext, "ad clicked");
                }
    
                @Override
                public void onAdShow(View view, int type) {
                    TToast.show(mContext, "ad displayed");
                }
    
                @Override
                public void onRenderFail(View view, String msg, int code) {
                    Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                    TToast.show(mContext, msg+" code:"+code);
                }
    
                @Override
                public void onRenderSuccess(View view, float width, float height) {
                   //return the width and height of view(in dp)
                    TToast.show(mContext,"render success");
                    //display ad when it rendersuccess                    mExpressContainer.removeAllViews();
                    mExpressContainer.addView(view);
                }
            });
           
            if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
                return;
            }
        }
        
        //Release the resources occupied by ads at the right time.
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (mTTAd != null) {
                //callback destroy() method to release.
                mTTAd.destroy();
            }
        }
```

#### 2.12.2 Display Customized Template Interstital Ads


```java
 public void onRenderSuccess(View view, float width, float height) {
                    //Width and height of the returned view (in dp).
                    TToast.show(mContext, "Rendering successful");
                    //Display ads after rendering is successfully called back so as to improve user's experience.
                       mTTAd.showInteractionExpressAd(InteractionExpressActivity.this);
                }
```
After loading the ad, call the TTNativeExpressAd.render() method to render the ad. After that, get the rendered ad from the onRenderSuccess(View view,float width,float height) method and add it to the
container for display. For more details, please refer to the InteractionExpressActivity in the demo. 


### 3.0 Dislike Related Settings（TTAdDislike）
SDK provides the developer with the dislike method of feed ads and banner ads. When the user chooses to turn off the ad, there will be a pop-up dialog to ask the user why they turn off. The reasons includes ‘not interested’ and ‘have seen the same ad’ and so on. This feedback is mainly used for the advertising strategy  to optimize the click rate and improve the performance.

**Note: if you use the dislike logic, the param must be passed in activity when initialize TTAdNative mTTAdNative = ttAdManager.createAdNative(activity)**

#### 3.1 TTAdDislike interface descriptions 

After getting TTFeedAd or TTBannerAd, call TTFeedAd.getDislikeDialog() or TTBannerAd.getDislikeDialog() to get TTAdDislike object. The DislikeInteractionCallback is the callback interface for the user’s selection.  

```java
public interface TTAdDislike {
    /**
     * Pop up Dislike Dialog
     */
    void showDislikeDialog();

    /**
     * Set Dislike Interaction Callback
     */
    void setDislikeInteractionCallback(DislikeInteractionCallback dislikeInteractionCallback);

    /**
     * Expose to the user and handle the result of dislike selection
     */
    interface DislikeInteractionCallback {

        /**
         * @param position  The chosen position
         * @param value The chosen content
         */
        void onSelected(int position, String value);

        /**
         * Click cancel
         */
        void onCancel();
    }
}
```

#### 3.2 Feed ads Integrate Dislike Dialog
After getting TTFeedAd, call TTFeedAd.getDislikeDialog() to get TTAdDislike object, then set the object for user selection result callback. Specific code examples are as follows:  
```
//After getting TTFeedAd, integrate the dislike logic
final TTAdDislike ttAdDislike = ttFeedAd.getDislikeDialog();
if (ttAdDislike != null) {
      ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
          @Override
          public void onSelected(int position, String value) {
                //the user choose the ‘position’-th reason，the reason’s content is ‘value’
          }
    
          @Override
          public void onCancel() {
                //the user click the cancel 
          }
      });
      dislikeIcon.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ttAdDislike.showDislikeDialog();
          }
      });
}
```

#### 3.3 Banner ads Integrate dislike dialog
Method 1: use the SDK default dislike icon

```java
mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

    @Override
    public void onError(int code, String message) {
        Toast.makeText(BannerActivity.this, "load error : " + code + ", " + message, Toast.LENGTH_SHORT).show();
        mBannerContainer.setVisibility(View.GONE);
        mTvDownloadText.setVisibility(View.GONE);
    }

    @Override
    public void onBannerAdLoad(final TTBannerAd ad) {
        //Omit other codes for easy reading

        //Show the dislike icon in banner ads
        ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                Toast.makeText(mContext, "click" + value, Toast.LENGTH_SHORT).show();
                mBannerContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "click cancel ", Toast.LENGTH_SHORT).show();
            }
        });
    }
});
}
```

Method 2: Use the custom dislike icon

```java
 mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
    @Override
    public void onError(int code, String message) {
        Toast.makeText(BannerActivity.this, "load error : " + code + ", " + message, Toast.LENGTH_SHORT).show();
        mBannerContainer.setVisibility(View.GONE);
        mTvDownloadText.setVisibility(View.GONE);
    }
    
    @Override
    public void onBannerAdLoad(final TTBannerAd ad) {
      //Omit other codes for easy reading
    
        //Integrate dislike dialog, publisher can set mTTAdDislike.showDislikeDialog() in custom dislike icon;
    
        mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    Toast.makeText(mContext, "click" + value, Toast.LENGTH_SHORT).show();
                }
    
                @Override
                public void onCancel() {
                    Toast.makeText(mContext, "click cancel", Toast.LENGTH_SHORT).show();
                }
            });
        if (mTTAdDislike != null) {
            XXX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTTAdDislike.showDislikeDialog();
                }
            });
        }
    }
});
```

For detailed usage information, please refer to demo：FeedListActivity and BannerActivity.
Notice: the current usage of the dislike in FeedListActivity is changed to custom dislike.

#### 3.4 Instructions for the custom dislike dialog box

There are two ways to use the dislike dialog box: 1. the default dialog box of sdk；2.the custom dislike style of developers.
The details of the interface:

1.    The default dialog: TTFeedAd.getDislikeDialog(Activity activity)
2.    The custome dialog: TTFeedAd.getDislikeDialog(TTDislikeDialogAbstract dialog)

Notice：

1.    The dialog must use : TTDislikeDialogAbstract
2.    The list view must use: TTDislikeListView

For detailed usage information, please refer to demo：FeedListActivity#MyAdapter#bindDislikeCustom()
TTDislikeDialogAbstract：

```java
public abstract class TTDislikeDialogAbstract extends Dialog {

    /**
     * set view id
     *
     * @return id
     */
    public abstract int getLayoutId();

    /**
     * Get all the listview ids，these listview must from TTDislikeListView
     *
     * @return listview id
     */
    public abstract int[] getTTDislikeListViewIds();

    /**
     * Set params，null is acceptable.
     *
     * @return params
     */
    public abstract ViewGroup.LayoutParams getLayoutParams();

}
```

## 4. Reference

### 4.1 SDK Error Code

|Error Code  |  Description
|---------|-------------------------------------|
|20000 |   Success
|40000  |  http content type error
|40001  |  http request pb error
|40002  |  source_type='app', request app can't be empty
|40003  |  source_type='wap', request wap cannot be empty
|40004  |  Ad slot cannot be empty
|40005  |  Ad slot size cannot be empty
|40006   | Illegal ad ID
|40007   | Incorrect number of ads
|40008   | Image size error
|40009   | Media ID is illegal
|40010   | Media type is illegal
|40011   | Ad type is illegal
|40012   | Media access type is illegal and has been deprecated
|40013   | Code bit id is less than 900 million, but adType is not splash ad
|40014  |  The redirect parameter is incorrect
|40015   | Media rectification exceeds deadline, request illegal
|40016  |  The relationship between slot_id and app_id is invalid.
|40017 |   Media access type is not legal API/SDK
|40018  |  Media package name is inconsistent with entry
|40019  |  Media configuration ad type is inconsistent with request
|40020  |  The ad space registered by developers exceeds daily request limit
|40021  |  Apk signature sha1 value is inconsistent with media platform entry
|40022  |  Whether the media request material is inconsistent with the media platform entry
|40023  |  OS field is incorrect
|40024  |  SDK version is too low to return ads
|40025   | SDK package is incompletely installed. It is recommended that you verify the SDK package integrity or contact our technical support
|40029  |  Wrong methods are used for template ads. Please see the FAQ(Q5) description below
|50001 |   Server Error
|60001  |  Show event processing error
|60002  |  Click event processing error
|60007  |  Server abnormity or failure of rewarded video ad rewards verification
|-1   | Data parsing failed
|-2   | Network Error
|-3   | Parsing data without ad
|-4   |Return data is missing the necessary fields
|-5    |bannerAd image failed to load
|-6    |Interstitial ad image failed to load
|-7   | Splash ad image failed to load
|-8   | Frequent request
|-9   | Request entity is empty
|-10  |  Cache parsing failed
|-11  |  Cache expired
|-12  |  No splash ad in the cache
|101  |  Failed to parse the rendering result data
|102  |  Invalid main template
|103  |  Invalid template difference
|104  |  Abnormal material data
|105  |  Template data parsing error
|106  |  Rendering error
|107  |  Rendering has timed out and not called back

### 4.2 SDK Multi-process Support
If your app needs multi-processes support, be sure to set `TTAdConfig.supportMultiProcess(true)`.  

Three Ways to confirm if the app supports multi-process: 1 Pangle sdk initialization, 2 Pangle ad acquisition, 3 Pangle ad display, these three key points are called in different processes, otherwise it is a single process.  

**Notice:** Avoid multi-process as much as possible, multi-process is not as efficient as single-process.

### 4.3 Theme Configuration
This SDK allows user to configurate themes for landing page's titleBar.  

#####Landing Page TitleBar Configuration
The theme of the landing page can be set by TTAdConfig, currently supports the following three types:   
1.Without titleBar:(TITLE\_BAR\_THEME\_NO\_TITLE\_BAR);  
2.Bright color theme:(TTAdConstant.TITLE\_BAR\_THEME\_LIGHT);    
3.Dark color theme:（TTAdConstant.TITLE\_BAR\_THEME\_DARK).



```
TTAdConfig.titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK);
```

### 4.4 Frequently Asked Questions
**1.Global information:**  
When calling TTADManager.createAdNative(Context context) to set the global information, please ensure that the incoming context is activity, otherwise it will affect the function of the sdk  

**2.How to distinguish between landing page ad and app install ad?**  
In the ad interaction type, open in the browsers (normal type), landing page (normal type), and app install ad. You can distinguish whether it is a landing page ad or an app install ad.  

**3.Resource Obfuscation:**  
If your app also obfuscates resources (such as andResGuard), please don't obfuscate any resources of Pangle to prevent a crash from being able to locate resources. Please consult technical support for the list of resources of the Pangle.

**4. For feed ads:**  
1. Make sure that you have called the registerViewForInteraction (ViewGroupcontainer,ListclickViews,ListcreativeViews,AdInteractionListenerlistener) when creating creative feed ads, otherwise the ad billing and interactions will not work properly.  
2. When calling the registerViewForInteraction (ViewGroupcontainer,ListclickViews,ListcreativeViews,AdInteractionListenerlistener) method, if the clickViews and creativeViews contain the same view, the view will respond to the callback from the creative one.  
3. Please follow the installation steps for Android 7.0 in this document. Make sure your support-v4 package is at version 24.2.0 or above.  

**5. Error code 40029 may be caused by two reasons.**  
1. SDK is outdated. The SDK version must be no lower than version 2.5.0.0. Should this happen, please update the SDK to the latest version.  
2. Interface usage error. The type of created code bit is template rendering (or non-template rendering), but the request method is for the opposite template. If the code bit is template rendering on the platform, please refer to the customized template ad section in this document. You can also find express related codes in the demo. If the code position is not template rendering, please do not call interfaces that contain express related contents.  

**6.For other questions, please contact the Pangle staff.**


