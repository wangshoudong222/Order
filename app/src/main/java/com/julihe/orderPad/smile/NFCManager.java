package com.julihe.orderPad.smile;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcBarcode;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;

public class NFCManager {
    private static final String TAG = "dxp.NFCManager";

    private WeakReference<Activity> mActivity;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilter = null;
    private String[][] mTechList = null;
    private boolean mIsReady = false;
    private boolean mCancelNfcSetting = false;

    private final OnReceiveNfcTagCallback mCallback;

    public interface OnReceiveNfcTagCallback {
        void onReceiveNfcTag(String tag);
    }

    // 初始化
    public NFCManager(Activity activity){
        mActivity = new WeakReference<>(activity);
        mCallback = (OnReceiveNfcTagCallback) activity;
    }

    private void init(){
        mAdapter = NfcAdapter.getDefaultAdapter(mActivity.get());
        if (mAdapter == null) {
            mIsReady = false;
            Toast.makeText(mActivity.get(), "设备不支持NFC功能!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!mAdapter.isEnabled()) {
                mIsReady = false;
                isToSet();
                return;
            } else {
                mIsReady = true;
                Toast.makeText(mActivity.get(), "NFC功能已打开!", Toast.LENGTH_SHORT).show();
            }
        }

        // 配置pendingIntent，NFC系统在读卡后会通过该配置跳转至目标Activity
        Intent intent = new Intent(mActivity.get(), mActivity.get().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(mActivity.get(), 0, intent, 0);

        // 需要确保xml/nfc_tech_filter.xml中已存在以下配置
        mTechList = new String[][]{
                {NfcA.class.getName(),IsoDep.class.getName()},
                {NfcA.class.getName(),MifareClassic.class.getName()},
                {NfcA.class.getName()},
                {IsoDep.class.getName()},
                {MifareClassic.class.getName()},
                {Ndef.class.getName()},
                {MifareUltralight.class.getName()},
                {NdefFormatable.class.getName()},
                {NfcBarcode.class.getName()},
                {NfcV.class.getName()},
                {NfcF.class.getName()},
                {NfcB.class.getName()},
        };

        // 做一个IntentFilter过滤你想要的action 这里过滤的是ndef
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        // 做一个IntentFilter过滤你想要的action 这里过滤的是tag
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilter = new IntentFilter[]{filter,filter2};

        dispatch();
    }

    // 注册前台分发
    public void dispatch(){
        Log.d(TAG,"dispatch");
        if(!mIsReady){
            Log.w(TAG,"NFC not ready !");
            init();
            return;
        }
        if(mAdapter == null){
            Log.w(TAG,"mAdapter is null");
            return;
        }
        Log.d(TAG,"mAdapter.isEnabled : " + mAdapter.isEnabled());
        mAdapter.enableForegroundDispatch(mActivity.get(),mPendingIntent,mIntentFilter,mTechList);
    }

    // 取消前台分发
    public void cancelDispatch(){
        Log.d(TAG,"cancelDispatch");
        if(!mIsReady){
            Log.w(TAG,"NFC not ready !");
            return;
        }
        if(mAdapter == null){
            Log.w(TAG,"mAdapter is null");
            return;
        }
        mAdapter.disableForegroundDispatch(mActivity.get());
    }

    // 处理Intent，解析NFC标签
    public void receiveTag(Intent intent) {
        if(intent == null){
            return;
        }
        Log.w(TAG,"receiveTag : " + intent.getAction());

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //获取 Tag 读取 ID 得到字节数组 转字符串 转码 得到卡号（默认16进制 这请自便）
        if (tag == null) {
            Log.w(TAG,"tag is null !!");
            return;
        }

        String tagId = toHexString(tag.getId());
        Log.i(TAG,"tagId = " + tagId);
        mCallback.onReceiveNfcTag(tagId);

        String[] techList = tag.getTechList();
        if(techList != null){
            for (String tech : techList) {
                Log.d(TAG,"tech = " + tech);
            }
        }

    }



    private void isToSet() {
        if(mCancelNfcSetting){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.get());
        builder.setMessage("是否跳转到设置页面打开NFC功能");
//        builder.setTitle("提示");
        builder.setPositiveButton("确认", (dialog, which) -> {
            goToSet(mActivity.get());
            dialog.dismiss();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            mCancelNfcSetting = true;
            dialog.dismiss();
        });
        builder.create().show();
    }

    private static void goToSet(Activity activity) {
        // 进入设置系统应用权限界面
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);
    }


    /**
     * 将字节数组转换为字符串
     */
    private static String toHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public void onDestroy(){
        Log.d(TAG,"onDestroy");
        mAdapter = null;
        mActivity.clear();
        mActivity = null;
        mIsReady = false;
    }

}
