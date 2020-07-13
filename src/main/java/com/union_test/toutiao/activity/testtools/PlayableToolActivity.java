package com.union_test.toutiao.activity.testtools;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.union_test.toutiao.R;
import com.union_test.toutiao.config.TTAdManagerHolder;
import com.union_test.toutiao.view.RadioButtonView;

import java.util.ArrayList;

public class PlayableToolActivity extends Activity {
    private Button mVerityBtn;
    private EditText mPlayableEditText;
    private EditText mDownLoadEditText;
    private EditText mPackageNameEditText;
    private EditText mDeeplinkEditText;
    private RadioButtonView rbv1;
    //传入激励视频的播放方向
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;
    private int mOrientation = VERTICAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playable);
        mVerityBtn = findViewById(R.id.btn_verity);
        mPlayableEditText = findViewById(R.id.playable_url_edit);
        mDownLoadEditText = findViewById(R.id.download_url_edit);
        mPackageNameEditText = findViewById(R.id.package_name_edit);
        mDeeplinkEditText = findViewById(R.id.deeplink_edit);
        rbv1 = findViewById(R.id.rbv_1);
        mVerityBtn.setOnClickListener(onClickListener);

        rbv1.setOptions(getShowTypes());
        rbv1.setOnRadioButtonChangedListener(new RadioButtonView.OnRadioButtonChangedListener() {
            @Override
            public void onRadioButtonChanged(String option, int index) {
                if ("VERTICAL".equals(option)) {
                    mOrientation = VERTICAL;
                } else if ("HORIZONTAL".equals(option)) {
                    mOrientation = HORIZONTAL;
                }
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String playableUrl = String.valueOf(mPlayableEditText.getText());
            String downloadUrl = String.valueOf(mDownLoadEditText.getText());
            String packageName = String.valueOf(mPackageNameEditText.getText());
            String deeplinkUrl = String.valueOf(mDeeplinkEditText.getText());
//            playableUrl = "https://sf3-ttcdn-tos.pstatp.com/obj/union-fe/playable/c1bc21ec4c2ea37e519d1a6c2e88910b/index.html";
//            downloadUrl = "https://api.pinduoduo.com/api/app/channel/pinduoduo_toutiao";
//            packageName = "com.xunmeng.pinduoduo";
//            deeplinkUrl = "pddopen://?appKey=13d70486b8f542ab8434cc777b24d2b0&packageId=com.ss.android.article.news&backUrl=__back_url__&h5Url=mlp_land_nd.html%3Fx_id%3D39095%26_p_ads_channel%3Dtoutiao%26_oc_ads_channel%3Dtoutiao%26_p_ads_type%3Dlaxin%26_p_launch_type%3Dadv&backurl=__back_url__";
            if (playableUrl == null) {
                Toast.makeText(PlayableToolActivity.this, " URL can't be null ！", Toast.LENGTH_LONG).show();
            } else if (!Patterns.WEB_URL.matcher(playableUrl).matches()) {
                Toast.makeText(PlayableToolActivity.this, " Please input valid URL ！", Toast.LENGTH_LONG).show();
            } else {
                //开始验证
                if (!TTAdManagerHolder.get().onlyVerityPlayable(playableUrl, mOrientation, downloadUrl, packageName, deeplinkUrl)) {
                    Log.d("PlayableToolActivity", "mOrientation=" + mOrientation);
                    Log.d("PlayableToolActivity", "url=" + playableUrl);
                    Toast.makeText(PlayableToolActivity.this, " invalid verity information , maybe invalid url !", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlayableToolActivity.this, " start verity !", Toast.LENGTH_LONG).show();
                }

            }
        }
    };


    private ArrayList<String> getShowTypes() {
        ArrayList<String> aar = new ArrayList<>();
        aar.add("VERTICAL");
        aar.add("HORIZONTAL");
        return aar;

    }
}