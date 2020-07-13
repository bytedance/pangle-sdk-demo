package com.union_test.toutiao.activity.testtools;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.union_test.toutiao.R;
import com.union_test.toutiao.utils.TempFileUtils;

public class GpsToolActivity extends Activity {

    private Button mLoadBtn;
    private Button mClearBtn;
    private EditText mGpsxEditText;
    private EditText mGpsyEditText;
    private TempFileUtils mTempFileUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_tool);
        mLoadBtn = findViewById(R.id.btn_load);
        mClearBtn = findViewById(R.id.btn_clear_gps);
        mGpsxEditText = findViewById(R.id.gpsx_edit);
        mGpsyEditText = findViewById(R.id.gpsy_edit);
        mLoadBtn.setOnClickListener(onClickListener);
        mClearBtn.setOnClickListener(onClickListener);
        mTempFileUtils = new TempFileUtils();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_clear_gps){
                //删除现有的本地文件
                mTempFileUtils.deleteFile("testGps.txt");
                Toast.makeText(GpsToolActivity.this, "Clear Success！", Toast.LENGTH_LONG).show();
            }
            if(v.getId()==R.id.btn_load){
                String Gpsx = String.valueOf(mGpsxEditText.getText());
                String Gpsy = String.valueOf(mGpsyEditText.getText());
                if (Gpsx == null) {
                    Toast.makeText(GpsToolActivity.this, "x can't be null ！", Toast.LENGTH_LONG).show();
                }else if(Gpsy == null) {
                    Toast.makeText(GpsToolActivity.this, "y can't be null ！", Toast.LENGTH_LONG).show();
                }else{
                    //保存到本地
                    String content = Gpsx+","+Gpsy;
                    mTempFileUtils.writeToFile("testGps.txt",content);
                    Toast.makeText(GpsToolActivity.this, "Preserve Success！", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
}
