package com.julihe.order.smile;

import android.content.Context;
import android.util.Log;

import com.julihe.order.smile.presenter.ApproachSingleScanFacePresenter;
import com.julihe.order.smile.presenter.IScanFacePresenter;
import com.julihe.order.smile.presenter.NormalScanFacePresenter;
import com.julihe.order.smile.presenter.ApproachContinuityScanFacePresenter;
import com.alipay.zoloz.smile2pay.InstallCallback;
import com.alipay.zoloz.smile2pay.Zoloz;
import com.alipay.zoloz.smile2pay.ZolozConstants;
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse;

import java.util.HashMap;

/**
 * 1. 实例化SmileManager: SmileManager(MetaInfo mMetaInfo, Context context)
 * 2. 调用: SmileManager.beginSmile(String authToken)
 * <p>
 * 等待自动初始化完成，初始化成功后会唤起刷脸
 */
public class SmileManager {
    private final String TAG = "dxp.SmileManager";
    private final MetaInfo mMetaInfo;
    private final Zoloz mZoloz;
    private Boolean isInitSuccess = false;
    private Context mContext = null;

    private final HashMap<Integer, IScanFacePresenter> mSmilePresenters = new HashMap();

    // 正常刷脸
    public static int SCAN_TYPE_NORMAL = 1;
    // 进场检测-单次刷脸
    public static int SCAN_TYPE_APPROACH_SINGLE = 2;
    // 进场检测-连续刷脸
    public static int SCAN_TYPE_APPROACH_CONTINUITY = 3;

    public interface OnInstallResultListener {
        void onInstallResult(boolean isSuccess,String errMsg);
    }

    public interface OnScanFaceResultListener {
        void onVerifyResult(Boolean success, String fToken, String uid);
    }

    public SmileManager(MetaInfo mMetaInfo, Context context) {
        this.mMetaInfo = mMetaInfo;
        this.mContext = context;
        mZoloz = Zoloz.getInstance(context);

        mSmilePresenters.put(SCAN_TYPE_NORMAL, new NormalScanFacePresenter(mZoloz));
        mSmilePresenters.put(SCAN_TYPE_APPROACH_SINGLE, new ApproachSingleScanFacePresenter(mZoloz));
        mSmilePresenters.put(SCAN_TYPE_APPROACH_CONTINUITY, new ApproachContinuityScanFacePresenter(mZoloz));

    }

    public void startSmile(int scanType, OnInstallResultListener listener, OnScanFaceResultListener verifyListener) {
        Log.d(TAG, "开始人脸识别：startSmile");
        if (isInitSuccess) {
            listener.onInstallResult(true,"");
            scanFace(scanType, verifyListener);
            return;
        }
        Log.d(TAG, "未初始化，重新初始化");
        initScan(scanType, listener, verifyListener);
    }

    public void initScan(OnInstallResultListener listener) {
        initScan(0,listener, null);
    }

    private void initScan(int scanType, OnInstallResultListener listener, OnScanFaceResultListener verifyListener) {
        mZoloz.install(packageInstallData());
        // 监听初始化结果
        mZoloz.register(null, new InstallCallback() {
            @Override
            public void onResponse(Smile2PayResponse smile2PayResponse) {
                int code = smile2PayResponse.getCode();
                if (code == Smile2PayResponse.CODE_SUCCESS) {
                    Log.i(TAG, "smile初始化成功，唤起刷脸...");
                    // 初始化成功,唤起刷脸
                    isInitSuccess = true;
                    if (scanType != 0) {
                        scanFace(scanType,verifyListener);
                    }
                } else {
                    // 初始化或刷脸异常
                    isInitSuccess = false;
                }
                listener.onInstallResult(isInitSuccess,"subCode="+smile2PayResponse.getSubCode() + " \n subMsg="+smile2PayResponse.getSubMsg());
            }
        });
        // 监听smile服务连接状态
        mZoloz.setConnectCallback((b, componentName) -> {
            Log.i(TAG, b ? "smile服务已连接" : "smile断开连接");
            isInitSuccess = b;
        });
    }


    private void scanFace(int scanType, OnScanFaceResultListener listener) {
        if (!isInitSuccess) {
            mZoloz.install(packageInstallData());
            return;
        }

        mSmilePresenters.get(scanType).scanFace(listener);
    }

    private HashMap<String, Object> packageInstallData() {
        HashMap<String, Object> installData = new HashMap<>();
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_DEVICE_NUM, mMetaInfo.deviceNum);
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_ISV_NAME, mMetaInfo.isvName);
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_ISV_PID, mMetaInfo.isvPid);
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_ID, mMetaInfo.merchantId);
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_NAME, mMetaInfo.merchantName);
        installData.put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_PAY_PID, mMetaInfo.merchantPayPid);
        installData.put(ZolozConstants.KEY_GROUP_ID, mMetaInfo.groupID);
        return installData;
    }


    public void exitScan(int scanType){
        mSmilePresenters.get(scanType).exitScan();
    }

    public void onDestroy(int scanType) {
        mSmilePresenters.get(scanType).destroy();
    }


    public static class MetaInfo {
        public String merchantId;
        public String merchantName;
        public String isvPid;
        public String isvName;
        public String groupID;
        public String merchantPayPid;
        public String deviceNum;

        public MetaInfo(String merchantId, String merchantName, String isvPid, String isvName, String groupId, String deviceNum, String merchantPayPid) {
            this.merchantId = merchantId;
            this.merchantName = merchantName;
            this.isvPid = isvPid;
            this.isvName = isvName;
            this.deviceNum = deviceNum;
            this.groupID = groupId;
            this.merchantPayPid = merchantPayPid;
        }
    }


}
