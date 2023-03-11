package com.julihe.orderPad.ui.smile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.julihe.orderPad.smile.IsvInfo;
import com.julihe.orderPad.smile.NFCManager;
import com.julihe.orderPad.smile.SmileManager;
import com.julihe.orderPad.R;

public class UseSmileActivity extends Activity implements
        SmileManager.OnInstallResultListener,
        SmileManager.OnScanFaceResultListener,
        NFCManager.OnReceiveNfcTagCallback {
    private final String TAG = "UseSmileActivity";

    private SmileManager mSmileManager;
    private TextView tvResult;

    private int mType = SmileManager.SCAN_TYPE_NORMAL;

    private Intent mNfcIntent = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_use_smile);

        // 初始化并部署NFC相关配置

        tvResult = findViewById(R.id.tvMain_result);
        TextView mTvInvokeSmile = findViewById(R.id.tvUseSmile_invokeSmile);

        mTvInvokeSmile.setOnClickListener(view -> {
            // 调用Smile刷脸流程
            Log.d(TAG,"click SCAN_TYPE_NORMAL");
            startSmile(SmileManager.SCAN_TYPE_NORMAL);
        });

        initSmileManger();

    }

    private void showLoading(boolean isClose){
        Log.v(TAG,"showLoading : "+isClose);
    }


    /**
     * todo 1. 传入isv相关的必填信息，初始化Smile环境
     */
    private void initSmileManger(){
        Log.d(TAG,"initSmileManger");
        mSmileManager = new SmileManager(
                IsvInfo.ISV_INFO, UseSmileActivity.this
        );

    }

    private void startSmile(int scanType){
        Log.d(TAG,"startSmileStep : getMetaInfo");
        showLoading(false);

        mType = scanType;
        mSmileManager.startSmile(mType,UseSmileActivity.this,UseSmileActivity.this);

    }


    @Override
    public void onInstallResult(boolean isSuccess,String errMsg) {
        Log.d(TAG,"startSmileStep : getMetaInfo : onMetaInfo callback : getAuthToken : beginSmile : onInstallResult : " + isSuccess);
        runOnUiThread(()->{
            showLoading(true);
            Toast.makeText(UseSmileActivity.this, isSuccess ? "Smile初始化成功" : "Smile初始化失败", Toast.LENGTH_LONG).show();
            tvResult.setText(errMsg);
        });
    }

    @Override
    public void onVerifyResult(Boolean success,String fToken, String uid) {
        // todo 2. ISV在此处实现实际支付操作

        String resultStr;
        String tokenOrCodeStr;
        String uidOrMsgStr;
        if(success){
            resultStr = "扫脸成功!";
            tokenOrCodeStr = "fToken = " + fToken;
            uidOrMsgStr = "uid = " + uid;
        } else {
            resultStr = "扫脸失败!";
            tokenOrCodeStr = "subCode = " + fToken;
            uidOrMsgStr = "subMsg = " + uid;
        }
        Log.w(TAG,uidOrMsgStr + " | " + tokenOrCodeStr);

        runOnUiThread(() -> tvResult.setText(new StringBuilder(resultStr).append("\n \n").append(uidOrMsgStr).append("\n \n").append(tokenOrCodeStr)));

    }

    @Override
    public void onReceiveNfcTag(String tag) {
        Log.i(TAG,"onReceiveNfcTag : " + tag);
        if(mSmileManager != null){
            mSmileManager.exitScan(mType);
        }
        tvResult.setText(new StringBuilder("刷卡成功！ ").append("\n tag = ").append(tag));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent");
        mNfcIntent = intent;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mNfcIntent = null;
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG,"onDestroy");
        mSmileManager.onDestroy(mType);
    }

}
